package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.RNG;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class SpiderShootWeb {

    private SpiderShootWeb() {
        // Prevent instantiation
    }

    public static void attemptShootWeb(Mob mob) {
        LivingEntity target = mob.getTarget();
        if (!(target instanceof ServerPlayer player)) {
            return;
        }

        if (spiderIsInShootDistance(mob, player)) {
            double chance = RNG.GetDouble(0.0, 100.0);
            if (chance < AggressiveMobsConfig.SpiderShootWebChance.get()) {
                shootWebAtPlayer(mob, player);
            }
        }
    }

    private static boolean spiderIsInShootDistance(Mob spider, ServerPlayer player) {
        float distance = spider.distanceTo(player);
        return distance < AggressiveMobsConfig.SpiderShootWebDist.get();
    }

    private static void shootWebAtPlayer(Mob spider, ServerPlayer player) {
        Level level = spider.level();
        BlockState playerBlockState = level.getBlockState(player.blockPosition());

        if (playerBlockState.isAir()) {
            level.setBlock(player.blockPosition(), Blocks.COBWEB.defaultBlockState(), 1);
        }
    }
}
