package com.birdsprime.aggressivemobs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Utility to find the nearest valid target (player or optionally villager) for a mob.
 */
public class GetNearestTarget {
    private static final TargetingConditions VILLAGER_CONDITIONS =
        TargetingConditions.forCombat()
                         .ignoreLineOfSight()
                         .range(64.0D);

    /**
     * Returns the nearest LivingEntity target for the given attacker, or null if none found.
     * This method replaces the old instance Get() call.
     */
    public static LivingEntity Get(Entity attacker) {
        if (!(attacker instanceof Mob) || attacker.level().isClientSide()) {
            return null;
        }
        // Get server-level
        ServerLevel level = ServerLifecycleHooks.getCurrentServer()
            .getLevel(attacker.level().dimension());
        if (level == null) return null;

        List<LivingEntity> candidates = new ArrayList<>();
        // Collect non-creative, alive players
        for (ServerPlayer player : level.getPlayers(p -> !p.isCreative() && p.isAlive())) {
            candidates.add(player);
        }

        // Optionally include villagers
        if (AggressiveMobsConfig.isMonstersTargetVillagers.get()) {
            candidates.addAll(
                level.getNearbyEntities(
                    Villager.class,
                    VILLAGER_CONDITIONS,
                    (LivingEntity) attacker,
                    attacker.getBoundingBox().inflate(16.0D, 4.0D, 16.0D)
                )
            );
        }

        // Select nearest by distance
        Optional<LivingEntity> nearest = candidates.stream()
            .min(Comparator.comparingDouble(attacker::distanceTo));

        return nearest.orElse(null);
    }
}
