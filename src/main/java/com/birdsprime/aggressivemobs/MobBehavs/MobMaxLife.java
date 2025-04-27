package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class MobMaxLife {

    private MobMaxLife() {
        // Prevent instantiation
    }

    public static void check(Entity entity) {
        if (entity.hasCustomName()) {
            return;
        }

        if (entity.tickCount > AggressiveMobsConfig.MobMaxLife.get()) {
            killMobRiding(entity);
            entity.discard();
        }
    }

    private static void killMobRiding(Entity entity) {
        Entity vehicle = entity.getVehicle();
        if (vehicle instanceof LivingEntity) {
            vehicle.discard();
        }
    }
}

