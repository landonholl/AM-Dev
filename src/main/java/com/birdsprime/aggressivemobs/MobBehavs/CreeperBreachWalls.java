package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreeperBreachWalls {
    private static final Logger LOGGER = LogManager.getLogger();

    // NBT keys for tracking breach state
    private static final String LAST_DIST_KEY = "breach_last_distance";
    private static final String TICKS_KEY     = "breach_ticks";

    /** Call this each tick to handle creeper wall-breaching logic. */
    public CreeperBreachWalls(Creeper creeper) {
        // Only run on the server side
        if (creeper.level().isClientSide) {
            return;
        }
        // If the creeper is dead or removed, clear any stored state
        if (!creeper.isAlive() || creeper.isRemoved()) {
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }
        // Must have a valid player target
        Entity target = creeper.getTarget();
        if (!(target instanceof ServerPlayer player)) {
            // No valid target (or not a player) -> reset and exit
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }
        if (!player.isAlive() || player.isCreative()) {
            // Target is invalid (dead or creative mode) -> reset and exit
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }

        // Calculate current distance to target
        float distance = creeper.distanceTo(player);
        int maxBreachDistance = AggressiveMobsConfig.CreeperBreachingDistance.get();
        int maxStuckTicks = AggressiveMobsConfig.CreeperObstructedExplodeTicks.get();  // e.g. 60 ticks

        // If target is beyond the breaching range, do not engage breach logic
        if (distance > maxBreachDistance) {
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }

        // Check line-of-sight to determine if there's an obstruction
        boolean canSeeTarget = creeper.hasLineOfSight(player);
        if (canSeeTarget) {
            // Creeper has direct line of sight – reset any breach timer and allow normal behavior
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }

        // At this point, the creeper has a target within range but line-of-sight is obstructed.
        // Initialize tracking data if not already present
        if (!creeper.getPersistentData().contains(LAST_DIST_KEY)) {
            creeper.getPersistentData().putFloat(LAST_DIST_KEY, distance);
            creeper.getPersistentData().putInt(TICKS_KEY, 0);
            LOGGER.debug("Breach tracking initialized at distance {}", distance);
            return;  // start tracking from next tick
        }

        // Retrieve last recorded closest distance and stuck tick count
        float lastClosestDist = creeper.getPersistentData().getFloat(LAST_DIST_KEY);
        int stuckTicks = creeper.getPersistentData().getInt(TICKS_KEY);

        if (distance < lastClosestDist) {
            // The creeper managed to get closer to the target than before – reset the timer
            creeper.getPersistentData().putFloat(LAST_DIST_KEY, distance);
            creeper.getPersistentData().putInt(TICKS_KEY, 0);
            LOGGER.debug("Creeper moved closer to target. Breach timer reset.");
        } else {
            // Creeper did not get any closer this tick (path is still obstructed)
            stuckTicks += 1;
            creeper.getPersistentData().putInt(TICKS_KEY, stuckTicks);
            LOGGER.debug("Creeper stuck for {} ticks (no progress toward target).", stuckTicks);

            if (stuckTicks >= maxStuckTicks) {
                // Creeper has been unable to close the distance for too long – initiate breach
                LOGGER.info("Creeper path obstructed for {} ticks. Igniting creeper to breach!", stuckTicks);
                if (!creeper.isIgnited()) {
                    // Ignite the creeper (start fuse) and play the priming sound
                    creeper.ignite();
                    creeper.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.0F);
                }
                // Once ignited, clear the breach tracking data (the creeper will explode shortly)
                creeper.getPersistentData().remove(LAST_DIST_KEY);
                creeper.getPersistentData().remove(TICKS_KEY);
            }
        }
    }
}
