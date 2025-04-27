package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CreeperBreachWalls {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LAST_DIST_KEY = "breach_last_distance";
    private static final String TICKS_KEY = "breach_ticks";

    private CreeperBreachWalls() {
        // Prevent instantiation
    }

    /** Call inside your tick handler for any Creeper instance. */
    public static void handleBreaching(Creeper creeper) {
        if (creeper.level().isClientSide) return;

        Entity eTarget = creeper.getTarget();
        if (!(eTarget instanceof ServerPlayer player)
          || !player.isAlive()
          || player.isCreative())
        {
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }

        float dist = creeper.distanceTo(player);
        int maxDist = AggressiveMobsConfig.CreeperBreachingDistance.get();
        int maxStuck = AggressiveMobsConfig.CreeperObstructedExplodeTicks.get();

        if (dist > maxDist) {
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }

        if (creeper.hasLineOfSight(player)) {
            creeper.getPersistentData().remove(LAST_DIST_KEY);
            creeper.getPersistentData().remove(TICKS_KEY);
            return;
        }

        var data = creeper.getPersistentData();
        if (!data.contains(LAST_DIST_KEY)) {
            data.putFloat(LAST_DIST_KEY, dist);
            data.putInt(TICKS_KEY, 0);
            LOGGER.debug("Breach tracking started at distance {}", dist);
            return;
        }

        float lastDist = data.getFloat(LAST_DIST_KEY);
        int ticks = data.getInt(TICKS_KEY);

        if (dist < lastDist) {
            data.putFloat(LAST_DIST_KEY, dist);
            data.putInt(TICKS_KEY, 0);
            LOGGER.debug("Creeper moved closer; breach timer reset.");
        } else {
            ticks++;
            data.putInt(TICKS_KEY, ticks);
            LOGGER.debug("Creeper stuck for {} ticks", ticks);

            if (ticks >= maxStuck) {
                LOGGER.info("Path blocked for {} ticks; igniting creeper!", ticks);
                if (!creeper.isIgnited()) {
                    creeper.ignite();
                    creeper.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.0F);
                }
                data.remove(LAST_DIST_KEY);
                data.remove(TICKS_KEY);
            }
        }
    }
}
