package com.birdsprime.aggressivemobs;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

import com.birdsprime.aggressivemobs.Base.*;
import com.birdsprime.aggressivemobs.MobBehavs.*;

import java.util.Random;
import java.util.UUID;

public class SpawnHandler {

    private static final UUID GLOBAL_SPEED_BOOST_ID = UUID.fromString("a7c9f6c4-42dd-4b65-b3de-5615df5183ff");
    private static final Random RNG = new Random();

    public static void handleSpawn(Entity e, MobSpawnType spawnType) {
        if (!(e instanceof Mob mob) || mob.level().isClientSide) return;

        final int invadeEvery = AggressiveMobsConfig.InvadeEveryXDays.get();
        final boolean siegeDay = (e.level().getDayTime() / 24000) % invadeEvery == 0;
        if (!siegeDay) return;

        final boolean dupEnabled = AggressiveMobsConfig.Entity_Duplication.get();
        final boolean jockeyOK = AggressiveMobsConfig.isSpecialJockeyMobsAllowed.get();
        final boolean bloodlust = AggressiveMobsConfig.AngryEntities.get();
        final boolean chargeCreeper = AggressiveMobsConfig.isChargedCreeperAllowed.get();

        MobTargetPlayer.targetNearestPlayer(e);

        AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null && speedAttr.getModifier(GLOBAL_SPEED_BOOST_ID) == null) {
            speedAttr.addPermanentModifier(new AttributeModifier(
                GLOBAL_SPEED_BOOST_ID, "Global Speed Boost", AggressiveMobsConfig.GlobalSpeedBoost.get(), AttributeModifier.Operation.ADDITION)
            );
        }

        if (e instanceof Zombie zomb) {
            new AggressiveZombie(zomb);
            zomb.goalSelector.getAvailableGoals().removeIf(entry ->
                entry.getGoal() instanceof RandomStrollGoal
                || entry.getGoal() instanceof WaterAvoidingRandomStrollGoal
            );
            //zomb.goalSelector.addGoal(2, new MobPursueDigOrBuildGoal(zomb, 1.1D));
        }

        if (e instanceof Skeleton skel) {
            new AggressiveSkeleton(skel);
            skel.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof net.minecraft.world.entity.ai.goal.RangedBowAttackGoal);
            skel.goalSelector.addGoal(2, new FastSkeletonAttackGoal(skel, 1.0D, 5, 15.0F));
            skel.goalSelector.addGoal(3, new PoweredArrowTrailGoal(skel));
        }

        if (e instanceof Creeper cr && chargeCreeper) {
            new AggressiveCreeper(cr, AggressiveMobsConfig.ChargedCreeperChance.get());
        }

        if (dupEnabled) {
            if (spawnType != MobSpawnType.CHUNK_GENERATION && spawnType != MobSpawnType.STRUCTURE) {
                if (RNG.nextInt(100) < AggressiveMobsConfig.DuplicationChance.get()) {
                    int min = AggressiveMobsConfig.MinDuplicationClones.get();
                    int max = AggressiveMobsConfig.MaxDuplicationClones.get();
                    new DuplicateMob(mob, RNG.nextInt(Math.max(1, max - min + 1)) + min);
                }
            }
            if (spawnType == MobSpawnType.SPAWNER) {
                int min = AggressiveMobsConfig.MinDungeonDuplicationClones.get();
                int max = AggressiveMobsConfig.MaxDungeonDuplicationClones.get();
                new DuplicateMob(mob, RNG.nextInt(Math.max(1, max - min + 1)) + min);
            }
        }

        if (jockeyOK && RNG.nextDouble() * 100 < AggressiveMobsConfig.SpecialJockeyGenerationChance.get()) {
            new SpawnRideableMob(e);
        }

        if (bloodlust && RNG.nextInt(100) < AggressiveMobsConfig.AngryEntityChance.get()) {
            new MobBloodlust(e);
        }
    }

    public static void handleArrowImpact(ProjectileImpactEvent evt, String missTag) {
        if (!(evt.getEntity() instanceof AbstractArrow arrow)) return;
        if (!(arrow.getOwner() instanceof Skeleton skel)) return;

        if (evt.getRayTraceResult() instanceof EntityHitResult) {
            skel.getPersistentData().putInt(missTag, 0);
        } else {
            skel.getPersistentData().putInt(missTag, skel.getPersistentData().getInt(missTag) + 1);
        }
    }

    public static void handleArrowSpawn(EntityJoinLevelEvent evt, String missTag) {
        if (!(evt.getEntity() instanceof AbstractArrow arrow)) return;
        if (!(arrow.getOwner() instanceof Skeleton skel)) return;

        if (skel.getPersistentData().getInt(missTag) > 2) {
            arrow.getPersistentData().putBoolean("shouldBoost", true);
            arrow.getPersistentData().putBoolean("isPoweredTrail", true); // <-- IMPORTANT
            arrow.setDeltaMovement(arrow.getDeltaMovement().scale(2.0D));
            arrow.setBaseDamage(arrow.getBaseDamage() * 2.0D);
            arrow.setCritArrow(true);
            skel.getPersistentData().putInt(missTag, 0);
        }
    }

    public static void handleServerTick(TickEvent.ServerTickEvent evt) {
        for (ServerLevel level : evt.getServer().getAllLevels()) {
            level.getEntitiesOfClass(AbstractArrow.class, level.getWorldBorder().getCollisionShape().bounds())
                 .stream()
                 .filter(arrow -> arrow.getPersistentData().getBoolean("shouldBoost"))
                 .forEach(arrow -> {
                     if (arrow.tickCount > 1) {
                         arrow.setDeltaMovement(arrow.getDeltaMovement().scale(2.0D));
                         arrow.setBaseDamage(arrow.getBaseDamage() * 2.0D);
                         arrow.setCritArrow(true);
                         arrow.getPersistentData().putBoolean("shouldBoost", false);
                         // leave isPoweredTrail = true
                     }
                 });
        }
    }
}
