package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class MobBuildBridge {

    private static final Block BLOCK_TYPE = Blocks.COBBLESTONE;
    private static final int LOCK_TICKS = 8;  // how long before next build

    private MobBuildBridge() {
        // no instances
    }

    /**
     * If there's a gap in front of the mob, place a block at foot level so it can walk across.
     */
    public static void tryBuildBridge(Mob mob) {
        // already building?
        if (mob.getPersistentData().getInt("action_lock") > 0) {
            return;
        }

        LivingEntity target = mob.getTarget();
        if (!(target instanceof ServerPlayer)) {
            return;
        }

        // Force zombie to look and move toward player
        mob.lookAt(target, 30.0F, 30.0F);
        mob.getNavigation().moveTo(target, 1.0D);


        // only when roughly level with the player
        BlockPos mobPos    = mob.blockPosition();
        BlockPos targetPos = target.blockPosition();
        if (Math.abs(mobPos.getY() - targetPos.getY()) > 1) {
            return;
        }

        Level level         = mob.level();
        BlockPos forwardPos = mobPos.relative(mob.getDirection());
        BlockState fwdState = level.getBlockState(forwardPos);

        // only build if nothing at feet
        if (!fwdState.isAir()) {
            return;
        }

        // where we fill
        BlockPos fillPos = forwardPos.below();
        if (!level.getBlockState(fillPos).isAir()) {
            // there’s already ground to walk on
            return;
        }

        // place the bridge block
        MobPlaceBlock.placeBlock(mob, BLOCK_TYPE, fillPos);

        // lock further builds for a few ticks
        mob.getPersistentData().putInt("action_lock", LOCK_TICKS);
        mob.swing(mob.getUsedItemHand());
    }
}
