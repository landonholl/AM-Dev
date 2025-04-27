package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

public final class ZombieDigManager {

    private static final int REALIZATION_TICKS = 10;
    private static final double FALLING_THRESHOLD = -0.1;
    private static final Random RANDOM = new Random();

    private ZombieDigManager() {}

    public static void tick(Zombie zombie) {
        if (zombie.level().isClientSide) return;

        if (zombie.getDeltaMovement().y < -0.6 && zombie.getPersistentData().getInt("fall_lock") == 0) {
            zombie.getPersistentData().putInt("fall_lock", 20);
        }

        if (maybeWaterClutch(zombie)) return;

        int fallLock = zombie.getPersistentData().getInt("fall_lock");
        if (fallLock > 0) {
            zombie.getPersistentData().putInt("fall_lock", fallLock - 1);
            return;
        }

        int lock = zombie.getPersistentData().getInt("action_lock");
        if (lock > 0) {
            zombie.getPersistentData().putInt("action_lock", lock - 1);
            return;
        }

        LivingEntity target = zombie.getTarget();
        if (target == null) return;

        var nav = zombie.getNavigation();

        int stuckTicks = zombie.getPersistentData().getInt("stuckTicks");
        if (nav.isDone() || nav.getPath() == null || !nav.getPath().canReach()) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }
        zombie.getPersistentData().putInt("stuckTicks", stuckTicks);

        if (stuckTicks < REALIZATION_TICKS) {
            return;
        }

        if (zombie.getDeltaMovement().y < FALLING_THRESHOLD) {
            return;
        }

        double dy = target.getY() - zombie.getY();

        if (dy > 1.5) {
            if (tryBurrowUp(zombie)) {
                zombie.getNavigation().moveTo(target, 1.2D);
            }
        } else if (dy < -1.5) {
            if (tryDigDown(zombie)) {
                zombie.getNavigation().moveTo(target, 1.2D);
            }
        } else {
            if (tryTunnelForward(zombie)) {
                zombie.getNavigation().moveTo(target, 1.2D);
            }
        }
    }

    private static boolean maybeWaterClutch(Zombie zombie) {
        if (zombie.getDeltaMovement().y < -0.6 && zombie.fallDistance > 8.0F) { //8 block fall
            if (RANDOM.nextFloat() < 0.5F) {
                Level level = zombie.level();
                BlockPos landing = zombie.blockPosition();
                if (level.getBlockState(landing).isAir() || !level.getFluidState(landing).isEmpty()) {
                    level.setBlockAndUpdate(landing, Blocks.WATER.defaultBlockState());
                    zombie.getPersistentData().putInt("water_clutch_timer", 20);
                }
            }
        }

        int timer = zombie.getPersistentData().getInt("water_clutch_timer");
        if (timer > 0) {
            timer--;
            zombie.getPersistentData().putInt("water_clutch_timer", timer);
            if (timer == 0) {
                Level level = zombie.level();
                BlockPos pos = zombie.blockPosition();
                if (level.getBlockState(pos).getBlock() == Blocks.WATER) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
            return true;
        }
        return false;
    }

    private static boolean tryBurrowUp(Zombie zombie) {
        Level level = zombie.level();
        BlockPos feet = zombie.blockPosition();
        BlockPos below = feet.below();
        BlockPos above1 = feet.above();
        BlockPos above2 = above1.above();

        if (!level.getBlockState(above1).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            level.destroyBlock(above1, true, zombie);
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }

        if (!level.getBlockState(above2).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            level.destroyBlock(above2, true, zombie);
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }

        if (zombie.onGround()) {
            if (level.getBlockState(feet).isAir() && !level.getFluidState(below).isSource()) {
                level.setBlockAndUpdate(feet, Blocks.COBBLESTONE.defaultBlockState());
            }
            zombie.getJumpControl().jump();
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }

        return false;
    }

    private static boolean tryDigDown(Zombie zombie) {
        Level level = zombie.level();
        BlockPos below1 = zombie.blockPosition().below();
        BlockPos below2 = below1.below();

        if (!level.getBlockState(below1).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            level.destroyBlock(below1, true, zombie);
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }

        if (!level.getBlockState(below2).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            level.destroyBlock(below2, true, zombie);
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }

        if (level.getBlockState(below1).isAir()) {
            zombie.teleportTo(zombie.getX(), zombie.getY() - 1, zombie.getZ());
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }

        return false;
    }

    private static boolean tryTunnelForward(Zombie zombie) {
        Level level = zombie.level();
        BlockPos forward = zombie.blockPosition().relative(zombie.getDirection());
        BlockPos forward2 = forward.above();

        boolean didDig = false;

        if (!level.getBlockState(forward).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            level.destroyBlock(forward, true, zombie);
            didDig = true;
        }

        if (!level.getBlockState(forward2).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            level.destroyBlock(forward2, true, zombie);
            didDig = true;
        }

        if (didDig) {
            zombie.getPersistentData().putInt("action_lock", 10);
        }

        return didDig;
    }
}