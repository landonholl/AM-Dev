package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.GetNearestTarget;
import com.birdsprime.aggressivemobs.RNG;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;

public final class MobPlaceTNT {

    private MobPlaceTNT() {
        // Prevent instantiation
    }

    public static void attemptPlaceTNT(Entity entity) {
        Entity nearest = GetNearestTarget.Get(entity);

        if (nearest != null && canPlaceThisTick() && isCloseEnough(nearest, entity)) {
            BlockPos pos = entity.blockPosition();
            ServerLevel level = (ServerLevel) entity.level();
            EntityType.TNT.spawn(level, null, (Player) null, pos, MobSpawnType.MOB_SUMMONED, true, false);
        }
    }

    private static boolean isCloseEnough(Entity target, Entity entity) {
        return target.distanceTo(entity) < AggressiveMobsConfig.ZombieTNTDistance.get();
    }

    private static boolean canPlaceThisTick() {
        return RNG.GetDouble(0.0, 100.0) < AggressiveMobsConfig.ZombieTNTChance.get();
    }
}
