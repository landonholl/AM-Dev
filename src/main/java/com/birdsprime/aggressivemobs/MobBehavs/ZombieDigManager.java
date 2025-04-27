package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
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

        int pillarLock = zombie.getPersistentData().getInt("pillar_lock");
        if (pillarLock > 0) {
            zombie.getPersistentData().putInt("pillar_lock", pillarLock - 1);
            zombie.setDeltaMovement(0.0, zombie.getDeltaMovement().y, 0.0);
            zombie.getNavigation().stop();
            return;
        }

        if (zombie.getPersistentData().getBoolean("shouldPlaceCobble")) {
            boolean controlled = zombie.getPersistentData().getBoolean("controlledJump");
            if (controlled) {
                BlockPos feet = zombie.blockPosition();
                double centerX = feet.getX() + 0.5;
                double centerZ = feet.getZ() + 0.5;
                double dx = centerX - zombie.getX();
                double dz = centerZ - zombie.getZ();
                double clamp = 0.2;
                zombie.setDeltaMovement(
                    Mth.clamp(dx, -clamp, clamp),
                    zombie.getDeltaMovement().y,
                    Mth.clamp(dz, -clamp, clamp)
                );
            }
            if (controlled && zombie.getDeltaMovement().y > 0) {
                BlockPos under = zombie.blockPosition().below();
                if (!zombie.level().getBlockState(under).isAir() && zombie.level().getBlockState(under).canOcclude()) {
                    zombie.level().setBlockAndUpdate(zombie.blockPosition(), Blocks.COBBLESTONE.defaultBlockState());
                    zombie.getPersistentData().putBoolean("shouldPlaceCobble", false);
                    zombie.getPersistentData().putBoolean("controlledJump", false);
                    zombie.getPersistentData().putInt("pillar_lock", 5);
                }
            }
        }

        int lock = zombie.getPersistentData().getInt("action_lock");
        if (lock > 0) {
            zombie.getPersistentData().putInt("action_lock", lock - 1);
            return;
        }

        LivingEntity target = zombie.getTarget();
        if (target == null) return;
        var nav = zombie.getNavigation();

        int stuck = zombie.getPersistentData().getInt("stuckTicks");
        if (nav.isDone() || nav.getPath() == null || !nav.getPath().canReach()) stuck++;
        else stuck = 0;
        zombie.getPersistentData().putInt("stuckTicks", stuck);

        if (stuck < REALIZATION_TICKS) {
            if (!nav.isDone() && nav.getPath() != null) {
                nav.moveTo(target, 1.2D);
            }
            return;
        }

        if (zombie.getDeltaMovement().y < FALLING_THRESHOLD) return;

        double dy = target.getY() - zombie.getY();
        double dx = target.getX() - zombie.getX();
        double dz = target.getZ() - zombie.getZ();
        double horizontalDistSq = dx * dx + dz * dz;

        if (horizontalDistSq > 4.0) {
            if (!nav.isDone() && nav.getPath() != null) {
                nav.moveTo(target, 1.2D);
                return;
            }
        }

        if (dy > 1.5) {
            if (tryBurrowUp(zombie)) nav.moveTo(target, 1.2D);
        } else if (dy < -1.5) {
            if (tryDigDown(zombie)) nav.moveTo(target, 1.2D);
        } else {
            if (tryTunnelForward(zombie)) nav.moveTo(target, 1.2D);
        }
    }

    private static boolean maybeWaterClutch(Zombie zombie) {
        if (zombie.getDeltaMovement().y < -0.6 && zombie.fallDistance > 4.0F) {
            if (RANDOM.nextFloat() < 0.13F) {
                Level level = zombie.level();
                BlockPos below = zombie.blockPosition().below();
                if (!level.getBlockState(below).isAir()) {
                    BlockPos p = zombie.blockPosition();
                    level.setBlockAndUpdate(p, Blocks.WATER.defaultBlockState());
                    zombie.getPersistentData().putInt("waterX", p.getX());
                    zombie.getPersistentData().putInt("waterY", p.getY());
                    zombie.getPersistentData().putInt("waterZ", p.getZ());
                    zombie.getPersistentData().putInt("water_clutch_timer", 15);
                }
            }
        }
        int timer = zombie.getPersistentData().getInt("water_clutch_timer");
        if (timer > 0) {
            timer--;
            zombie.getPersistentData().putInt("water_clutch_timer", timer);
            if (timer == 0) {
                Level lvl = zombie.level();
                int x = zombie.getPersistentData().getInt("waterX");
                int y = zombie.getPersistentData().getInt("waterY");
                int z = zombie.getPersistentData().getInt("waterZ");
                BlockPos pos = new BlockPos(x, y, z);
                if (lvl.getBlockState(pos).getBlock() == Blocks.WATER) {
                    lvl.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
            return true;
        }
        return false;
    }

    private static boolean tryBurrowUp(Zombie zombie) {
        Level lvl = zombie.level();
        BlockPos feet = zombie.blockPosition();
        BlockPos below = feet.below();
        BlockPos a1 = feet.above();
        BlockPos a2 = a1.above();
        if (!lvl.getBlockState(a1).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            lvl.destroyBlock(a1, true, zombie);
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }
        if (!lvl.getBlockState(a2).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            lvl.destroyBlock(a2, true, zombie);
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }
        if (zombie.onGround()) {
            if (lvl.getBlockState(feet).isAir() && !lvl.getFluidState(below).isSource()) {
                lvl.setBlockAndUpdate(feet, Blocks.COBBLESTONE.defaultBlockState());
            }
            zombie.getJumpControl().jump();
            zombie.getPersistentData().putBoolean("shouldPlaceCobble", true);
            zombie.getPersistentData().putBoolean("controlledJump", true);
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }
        return false;
    }

    private static boolean tryDigDown(Zombie zombie) {
        Level lvl = zombie.level();
        BlockPos b1 = zombie.blockPosition().below();
        BlockPos b2 = b1.below();
        if (!lvl.getBlockState(b1).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            lvl.destroyBlock(b1, true, zombie);
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }
        if (!lvl.getBlockState(b2).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            lvl.destroyBlock(b2, true, zombie);
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }
        if (lvl.getBlockState(b1).isAir()) {
            zombie.teleportTo(zombie.getX(), zombie.getY() - 1, zombie.getZ());
            zombie.getPersistentData().putInt("action_lock", 10);
            return true;
        }
        return false;
    }

    private static boolean tryTunnelForward(Zombie zombie) {
        Level lvl = zombie.level();
        BlockPos f1 = zombie.blockPosition().relative(zombie.getDirection());
        BlockPos f2 = f1.above();
        boolean did = false;
        if (!lvl.getBlockState(f1).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            lvl.destroyBlock(f1, true, zombie);
            did = true;
        }
        if (!lvl.getBlockState(f2).isAir()) {
            zombie.swing(zombie.getUsedItemHand());
            lvl.destroyBlock(f2, true, zombie);
            did = true;
        }
        if (did) {
            zombie.getPersistentData().putInt("action_lock", 10);
        }
        return did;
    }
}
