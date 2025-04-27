package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.SnapToBlockCenter;
import com.birdsprime.aggressivemobs.MobBehavs.MobBuildUp;
import com.birdsprime.aggressivemobs.MobBehavs.MobBuildBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;


import java.util.EnumSet;
import java.util.Random;

public class MobPursueDigOrBuildGoal extends Goal {
    private final Mob mob;
    private final double speed;
    private final Random rand = new Random();
    private static final double STEP = 0.5;
    private static final int MAX_DIG_DIST = 2;

    public MobPursueDigOrBuildGoal(Mob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        mob.getNavigation().moveTo(target, speed);

        Vec3 start = mob.getEyePosition(1.0F);

        Vec3 rawDir = target.getEyePosition(1.0F)
            .subtract(start)
            .normalize()
            .add(new Vec3(
                (rand.nextDouble() - 0.5) * 0.1,
                0,
                (rand.nextDouble() - 0.5) * 0.1
            ));
        Vec3 dir = new Vec3(rawDir.x, 0, rawDir.z).normalize();

        mob.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), speed);
        mob.setDeltaMovement(dir.scale(speed * 0.15));
        mob.hasImpulse = true;

        // ⭐ Passive jump if target slightly above and reachable
        if (target.getY() > mob.getY() && mob.distanceToSqr(target) < 9.0D) {
            mob.setJumping(true);
        }

        int cd = mob.getPersistentData().getInt("digCooldown");
        if (cd > 0) {
            mob.getPersistentData().putInt("digCooldown", cd - 1);
            return;
        }

        Level level = mob.level();
        BlockPos mobFeet = mob.blockPosition();
        BlockPos playerFeet = target.blockPosition();
        int dy = playerFeet.getY() - mobFeet.getY();

        boolean didDig = false;

        if (dy > 1) {
            // Player is ABOVE - break block above head
            BlockPos above1 = mobFeet.above(2);
            BlockPos above2 = above1.above();
            if (tryBreak(level, above1)) didDig = true;
            if (tryBreak(level, above2)) didDig = true;
            boolean brokeSomething = false;
    if (tryBreak(level, above1)) brokeSomething = true;
    if (tryBreak(level, above2)) brokeSomething = true;

        if (brokeSomething) {
            // ⭐ FIRST, place cobble under current position
            BlockState feetState = level.getBlockState(mobFeet);
            if (feetState.isAir() || feetState.getDestroySpeed(level, mobFeet) >= 0) {
                level.setBlockAndUpdate(mobFeet, Blocks.COBBLESTONE.defaultBlockState());

                // ⭐ THEN, teleport zombie upward +1 block
                mob.setPos(
                    mobFeet.getX() + 0.5,
                    mobFeet.getY() + 1.05,
                    mobFeet.getZ() + 0.5
                );
                SnapToBlockCenter.snap(mob);

                // ⭐ Force re-path and jump
                mob.getNavigation().moveTo(target, 1.1D);
                mob.setJumping(true);
                mob.hasImpulse = true;
            }
            return; // End tick after climbing
    }
        } else if (dy < -1) {
            // Player is BELOW - break 2 blocks below feet
            BlockPos below1 = mobFeet.below();
            BlockPos below2 = below1.below();
            if (tryBreak(level, below1)) didDig = true;
            if (tryBreak(level, below2)) didDig = true;
        } else {
            // Player is at SAME LEVEL - dig forward horizontally
            for (double d = 0; d < MAX_DIG_DIST; d += STEP) {
                Vec3 posV = start.add(dir.scale(d));
                BlockPos pos = new BlockPos(
                    (int) Math.floor(posV.x),
                    (int) Math.floor(posV.y),
                    (int) Math.floor(posV.z)
                );
                if (pos.distSqr(mob.blockPosition()) > MAX_DIG_DIST * MAX_DIG_DIST) continue;

                if (tryBreak(level, pos)) {
                    didDig = true;
                    break;
                }
            }
        }

        if (!didDig) {
            if (dy > 1) {
                // If player above and nothing broken, try to build up
                MobBuildUp.tryBuildUp(mob);
            } else {
                // Otherwise try to bridge forward
                MobBuildBridge.tryBuildBridge(mob);
            }
        }
    }

    private boolean tryBreak(Level level, BlockPos pos) {
        BlockState st = level.getBlockState(pos);
        if (!st.isAir() && st.getDestroySpeed(level, pos) >= 0) {
            level.destroyBlock(pos, true, mob);
            mob.swing(mob.getUsedItemHand());
            mob.getPersistentData().putInt("digCooldown", AggressiveMobsConfig.EntityDigDelay.get());
            mob.setJumping(true); // ⭐ Jump immediately after breaking block
            return true;
        }
        return false;
    }
}
