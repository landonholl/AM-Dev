// src/main/java/com/birdsprime/aggresivemobs/MobBehavs/CreeperBreachWalls.java
package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreeperBreachWalls {
    private static final Logger LOGGER = LogManager.getLogger();

    // NBT keys
    private static final String LAST_DIST_KEY = "breach_last_distance";
    private static final String TICKS_KEY     = "breach_ticks";

    /** Call inside your tick handler for any Creeper instance. */
    public CreeperBreachWalls(Creeper creeper) {
        if (creeper.level().isClientSide) return;

        // Reset if no valid player target
        Entity eTarget = creeper.getTarget();
        if (!(eTarget instanceof ServerPlayer player)
          || !player.isAlive()
          || player.isCreative())
        {
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }

        float dist       = creeper.distanceTo(player);
        int   maxDist    = AggressiveMobsConfig.CreeperBreachingDistance.get();       // default 64
        int   maxStuck   = AggressiveMobsConfig.CreeperObstructedExplodeTicks.get(); // default 60

        // If player is out of breach range, forget about breaching
        if (dist > maxDist) {
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }

        // If the creeper _can_ see the player again, reset the breach timer
        if (creeper.hasLineOfSight(player)) {
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }

        // Initialize tracking
        var data = creeper.getPersistentData();
        if (!data.contains(LAST_DIST_KEY)) {
            data.putFloat(LAST_DIST_KEY, dist);
            data.putInt(TICKS_KEY, 0);
            LOGGER.debug("Breach tracking started at distance {}", dist);
            return;
        }

        float lastDist = data.getFloat(LAST_DIST_KEY);
        int   ticks    = data.getInt(TICKS_KEY);

        if (dist < lastDist) {
            // made progress → reset timer
            data.putFloat(LAST_DIST_KEY, dist);
            data.putInt(TICKS_KEY, 0);
            LOGGER.debug("Creeper moved closer; breach timer reset.");
        } else {
            // still stuck
            ticks++;
            data.putInt(TICKS_KEY, ticks);
            LOGGER.debug("Creeper stuck for {} ticks", ticks);

            if (ticks >= maxStuck) {
                // prime the creeper
                LOGGER.info("Path blocked for {} ticks; igniting creeper!", ticks);
                if (!creeper.isIgnited()) {
                    creeper.ignite();
                    creeper.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.0F);
                }
                // clear data so it won't re-trigger
                data.remove(LAST_DIST_KEY);
                data.remove(TICKS_KEY);
            }
        }
    }
}
