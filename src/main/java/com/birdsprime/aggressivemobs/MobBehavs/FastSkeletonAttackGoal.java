package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Skeleton;

import java.lang.reflect.Field;

public class FastSkeletonAttackGoal extends RangedBowAttackGoal<Skeleton> {

    private static Field attackTimeField;

    static {
        try {
            attackTimeField = RangedBowAttackGoal.class.getDeclaredField("attackTime");
            attackTimeField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FastSkeletonAttackGoal(Skeleton mob, double moveSpeed, int attackInterval, float maxAttackDistance) {
        super(mob, moveSpeed, attackInterval, maxAttackDistance);
    }

    @Override
    public void tick() {
        super.tick();
        try {
            attackTimeField.setInt(this, 5); // force attack every 10 ticks
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
