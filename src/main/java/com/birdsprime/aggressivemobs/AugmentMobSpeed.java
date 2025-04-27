package com.birdsprime.aggressivemobs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class AugmentMobSpeed {

    private static final UUID WALK_SPEED_ID = UUID.fromString("a7c9f6c4-42dd-4b65-b3de-5615df5183ff");
    private static final UUID SWIM_SPEED_ID = UUID.fromString("ad4e3c1a-02f3-4ccf-8c34-23b1bc44de78");

    private static final double WALK_MULTIPLIER = 1.33;
    private static final double SWIM_ADD = 1.33;

    public AugmentMobSpeed(Mob mob) {
        if (mob == null || mob.level().isClientSide) return;

        BlockPos pos = mob.blockPosition();
        Level level = mob.level();
        boolean inWater = isWaterBlock(level, pos) || isWaterBlock(level, pos.below());

        if (inWater) {
            applyBoosts(mob);
        } else {
            removeBoosts(mob);
        }
    }

    private boolean isWaterBlock(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() == Blocks.WATER;
    }

    private void applyBoosts(Mob mob) {
        var walkAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (walkAttr != null && walkAttr.getModifier(WALK_SPEED_ID) == null) {
            walkAttr.addTransientModifier(new AttributeModifier(
                WALK_SPEED_ID, "Ocean Walk Speed", WALK_MULTIPLIER, AttributeModifier.Operation.MULTIPLY_BASE));
        }

        var swimAttr = mob.getAttribute(ForgeMod.SWIM_SPEED.get());
        if (swimAttr != null && swimAttr.getModifier(SWIM_SPEED_ID) == null) {
            swimAttr.addTransientModifier(new AttributeModifier(
                SWIM_SPEED_ID, "Ocean Swim Speed", SWIM_ADD, AttributeModifier.Operation.ADDITION));
        }
    }

    private void removeBoosts(Mob mob) {
        var walkAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (walkAttr != null && walkAttr.getModifier(WALK_SPEED_ID) != null) {
            walkAttr.removeModifier(WALK_SPEED_ID);
        }

        var swimAttr = mob.getAttribute(ForgeMod.SWIM_SPEED.get());
        if (swimAttr != null && swimAttr.getModifier(SWIM_SPEED_ID) != null) {
            swimAttr.removeModifier(SWIM_SPEED_ID);
        }
    }
}
