package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.SnapToBlockCenter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class MobBuildUp {

    private static final int BUILD_UP_DIST = 4;
    private static final Block BLOCK_TYPE = Blocks.COBBLESTONE;

    private MobBuildUp() {
        // Prevent instantiation
    }

    public static void tryBuildUp(Mob mob) {
        if (mob.getPersistentData().getInt("action_lock") > 0) {
            return;
        }

        var target = mob.getTarget();
        if (!(target instanceof ServerPlayer player)) {
            return;
        }
        if (player.isCreative()) {
            return;
        }

        BlockPos mobPos    = mob.blockPosition();
        BlockPos targetPos = target.blockPosition();
        if (!isCloseEnough(mobPos, targetPos) ||
            !(isBelowPlayer(mobPos, targetPos) && isPlayerHighEnough(mobPos, targetPos))) {
            return;
        }

        // Only build if space above is clear
        ServerLevel level      = player.serverLevel();
        BlockPos   aboveHead   = mobPos.above().above();
        BlockState aboveState  = level.getBlockState(aboveHead);
        if (!aboveState.isAir()) {
            return;
        }

        // Decide where to place the new block (one step toward the player)
        Vec3   dir        = getBuildDir(mobPos, targetPos);
        Vec3i  offset     = new Vec3i((int)dir.x, (int)dir.y, (int)dir.z);
        BlockPos buildPos = mobPos.offset(offset);

        // Place cobblestone pillar block
        MobPlaceBlock.placeBlock(mob, BLOCK_TYPE, buildPos);

        mob.getJumpControl().jump();     // for recent mappings, tells the JumpControl to start a jump

        // Center horizontally on the block
        SnapToBlockCenter.snap(mob);

        // Give a little upward push instead of teleport
        Vec3 vel = mob.getDeltaMovement();
        mob.setDeltaMovement(vel.x, 0.3, vel.z);

        // Swing arm for animation & lock further builds briefly
        mob.swing(mob.getUsedItemHand());
        mob.getPersistentData().putInt("action_lock", 6);
    }

    private static boolean isCloseEnough(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) < BUILD_UP_DIST &&
               Math.abs(a.getZ() - b.getZ()) < BUILD_UP_DIST;
    }

    private static boolean isPlayerHighEnough(BlockPos a, BlockPos b) {
        return Math.abs(a.getY() - b.getY()) > 1;
    }

    private static boolean isBelowPlayer(BlockPos a, BlockPos b) {
        return a.getY() < b.getY();
    }

    private static Vec3 getBuildDir(BlockPos a, BlockPos b) {
        float dx = b.getX() - a.getX();
        float dy = b.getY() - a.getY();
        float dz = b.getZ() - a.getZ();
        return new Vec3(dx, dy, dz).normalize();
    }
}
