package com.birdsprime.aggressivemobs;

import com.birdsprime.aggressivemobs.MobBehavs.*;
import com.birdsprime.aggressivemobs.Base.AggressiveCreeper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.LivingEntity;

public class LivingMobLogic {

    public static void tick(Mob mob) {
        if (mob.level().isClientSide) return;

        // 1) Always acquire or reacquire player target
        MobTargetPlayer.targetNearestPlayer(mob);

        // 2) Special logic for creepers
        if (mob instanceof Creeper creeper) {
            if (AggressiveMobsConfig.isCreeperBreachingAllowed.get()) {
                CreeperBreachWalls.handleBreaching(creeper);
            }
            AggressiveCreeper.CreeperTick(creeper);
            return;
        }

        // 3) Special zombie behavior (digging, tunneling)
        if (mob instanceof Zombie zombie) {
            handleZombieBehavior(zombie);
        }

        // 4) Iron Golems build bridges/pillars toward players
        if (mob instanceof IronGolem golem) {
            LivingEntity target = golem.getTarget();
            if (target instanceof Player) {
                if (Clocker.IsAtTimeInterval(golem, AggressiveMobsConfig.EntityBuildDelay.get())) {
                    MobBuildUp.tryBuildUp(golem);
                    MobBuildBridge.tryBuildBridge(golem);
                }
            }
        }

        // 5) Spiders shoot webs
        if (mob.getType() == EntityType.SPIDER || mob.getType() == EntityType.CAVE_SPIDER) {
            if (AggressiveMobsConfig.SpidersShootWebs.get()) {
                SpiderShootWeb.attemptShootWeb(mob);
            }
        }

        // 6) Ocean speed boost (water walking)
        if (AggressiveMobsConfig.isEntitiesBounceOnWater.get()) {
            new AugmentMobSpeed(mob);
        }
    }

    private static void handleZombieBehavior(Zombie zombie) {
        LivingEntity target = zombie.getTarget();
        if (target == null) return;
    
        // If the vanilla pathfinder has no valid path, re-issue a move request.
        var nav = zombie.getNavigation();
        if (nav.isDone() || !nav.getPath().canReach()) {
            nav.moveTo(target, AggressiveMobsConfig.GlobalSpeedBoost.get());
        }
    }
}
