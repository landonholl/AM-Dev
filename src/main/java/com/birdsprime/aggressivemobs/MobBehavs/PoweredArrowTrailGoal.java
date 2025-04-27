package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import java.util.EnumSet;
import java.util.List;

/**
 * Makes boosted (powered) arrows shot by skeletons emit crit trails.
 */
public class PoweredArrowTrailGoal extends Goal {

    private final Skeleton skeleton;
    private int cooldown;

    public PoweredArrowTrailGoal(Skeleton skeleton) {
        this.skeleton = skeleton;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE)); // allows moving still
        this.cooldown = 0;
    }

    @Override
    public boolean canUse() {
        // Only run if skeleton has recently boosted arrows
        return true; // Always allowed; actual filtering is in tick
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        cooldown = 5; // check every 5 ticks

        Level level = skeleton.level();
        if (level.isClientSide) return;

        List<AbstractArrow> arrows = level.getEntitiesOfClass(
            AbstractArrow.class,
            skeleton.getBoundingBox().inflate(32.0D),
            a -> a.getOwner() == skeleton && a.getPersistentData().getBoolean("isPoweredTrail")
        );

        for (AbstractArrow arrow : arrows) {
            level.addParticle(
                ParticleTypes.CRIT, 
                arrow.getX(), 
                arrow.getY(), 
                arrow.getZ(), 
                0.0, 0.0, 0.0
            );
        }
    }
}
