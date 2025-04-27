package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.GetNearestTarget;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import java.util.EnumSet;

/**
 * Always keeps the mob targeting the nearest valid player, ignoring LOS and range timeouts.
 */
public class PersistentTargetGoal extends Goal {
    private final Mob mob;

    public PersistentTargetGoal(Mob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.TARGET));
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
        LivingEntity nearest = GetNearestTarget.Get(mob);
        if (nearest != null && mob.getTarget() != nearest) {
            mob.setTarget(nearest);
        }
    }
}
