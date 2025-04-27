package com.birdsprime.aggressivemobs;

import com.birdsprime.aggressivemobs.MobBehavs.*;
import com.birdsprime.aggressivemobs.Base.AggressiveCreeper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction; // <-- ADD THIS LINE
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.level.block.Blocks;


public class LivingMobTickHandler {

    public static void tick(Entity entity) {
        if (entity.level().isClientSide) return;
        if (!(entity instanceof Mob mob)) return;

        // Always refresh target
        MobTargetPlayer.targetNearestPlayer(entity);

        // Creeper special logic
        if (entity instanceof Creeper creeper) {
            if (AggressiveMobsConfig.isCreeperBreachingAllowed.get()) {
                CreeperBreachWalls.handleBreaching(creeper);
            }
            AggressiveCreeper.CreeperTick(creeper);
            return;
        }

        // Zombie logic (dig + build)
        if (entity instanceof Zombie zombie) {
            handleZombie(zombie);
        }

        // Iron Golem logic (build up & bridge)
        if (entity instanceof IronGolem golem) {
            handleGolem(golem);
        }

        // Spiders shoot webs
        if (entity.getType() == net.minecraft.world.entity.EntityType.SPIDER
         || entity.getType() == net.minecraft.world.entity.EntityType.CAVE_SPIDER) {
            if (AggressiveMobsConfig.SpidersShootWebs.get()) {
                SpiderShootWeb.attemptShootWeb(mob);
            }
        }

        // Water‐walking augment
        if (AggressiveMobsConfig.isEntitiesBounceOnWater.get()) {
            new AugmentMobSpeed(mob);
        }
    }

    private static void handleZombie(Zombie zombie) {
        if (zombie.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
         || AggressiveMobsConfig.AllowZombieGriefing.get()) {
    
            LivingEntity target = zombie.getTarget();
            if (target == null) return;
    
            // New Priority: Clear above head if stuck
            if (!isAirAbove(zombie)) {
                digUpward(zombie);
                return; // Don't run building yet if underground
            }
    
            // Once above head is clear, build normally
            if (Clocker.IsAtTimeInterval(zombie, AggressiveMobsConfig.EntityBuildDelay.get())) {
                MobBuildUp.tryBuildUp(zombie);
                MobBuildBridge.tryBuildBridge(zombie);
            }
    
            if (AggressiveMobsConfig.ZombiesLayTNT.get()) {
                MobPlaceTNT.attemptPlaceTNT(zombie);
            }
            if (AggressiveMobsConfig.ZombiesLightFires.get()) {
                MobStartFires.attemptStartFire(zombie);
            }
        }
    }
    
    private static void handleGolem(IronGolem golem) {
        LivingEntity target = golem.getTarget();
        if (target instanceof Player) {
            if (Clocker.IsAtTimeInterval(golem, AggressiveMobsConfig.EntityBuildDelay.get())) {
                MobBuildUp.tryBuildUp(golem);
                MobBuildBridge.tryBuildBridge(golem);
            }
        }
    }

    private static boolean isAirAbove(Zombie zombie) {
        BlockPos above = zombie.blockPosition().above();
        return zombie.level().getBlockState(above).isAir();
    }
    
    private static void digUpward(Zombie zombie) {
        Level level = zombie.level();
        BlockPos feet = zombie.blockPosition();
        BlockPos head = feet.above();
        BlockPos head2 = head.above();
    
        zombie.swing(InteractionHand.MAIN_HAND);
    
        if (!level.getBlockState(head).isAir()) {
            level.destroyBlock(head, true, zombie);
        }
        if (!level.getBlockState(head2).isAir()) {
            level.destroyBlock(head2, true, zombie);
        }
    
        // Place cobble at current feet if empty or replaceable
        BlockState feetState = level.getBlockState(feet);
        if (feetState.isAir() || feetState.getDestroySpeed(level, feet) >= 0) {
            level.setBlockAndUpdate(feet, Blocks.COBBLESTONE.defaultBlockState());
        }
    
        // Teleport zombie up
        zombie.teleportTo(zombie.getX(), zombie.getY() + 1, zombie.getZ());
        zombie.getNavigation().stop();
    }
}
