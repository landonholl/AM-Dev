package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.GetNearestTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;

public final class MobTargetPlayer {

    private static final String[] WHITELIST = initializeWhitelist();

    private MobTargetPlayer() {
        // Prevent instantiation
    }

    public static void targetNearestPlayer(Entity attacker) {
        if (!isAllowedMobType(attacker)) {
            return;
        }

        Mob mob = (Mob) attacker;
        Entity currentTarget = mob.getTarget();
        boolean allowInfighting = AggressiveMobsConfig.AllowMonsterInfighting.get();

        if (allowInfighting && currentTarget != null) {
            return;
        }

        Entity nearestTarget = GetNearestTarget.Get(attacker);

        if (nearestTarget != null && withinRange(nearestTarget, attacker)) {
            mob.setTarget((LivingEntity) nearestTarget);
        }
    }

    private static boolean isAllowedMobType(Entity entity) {
        String entityName = entity.getName().getString().toLowerCase();
        for (String allowed : WHITELIST) {
            if (allowed.equals(entityName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean withinRange(Entity target, Entity attacker) {
        float aboveGroundDist = AggressiveMobsConfig.MonsterAttackDistanceAboveGround.get();
        float undergroundDist = AggressiveMobsConfig.MonsterAttackDistanceUnderground.get();

        double dx = Math.abs(target.getX() - attacker.getX());
        double dz = Math.abs(target.getZ() - attacker.getZ());
        float attackerY = attacker.blockPosition().getY();

        if (attackerY < 50) {
            return target.distanceTo(attacker) < undergroundDist;
        } else {
            return (dx < aboveGroundDist) && (dz < aboveGroundDist);
        }
    }

    private static String[] initializeWhitelist() {
        String[] list = AggressiveMobsConfig.SiegeEntityWhitelist.get().toLowerCase().split(",");
        for (int i = 0; i < list.length; i++) {
            list[i] = list[i].trim();
        }
        return list;
    }
}
