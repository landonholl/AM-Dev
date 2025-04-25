package com.birdsprime.aggressivemobs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class AugmentMobSpeed {

    private static final UUID OCEAN_WALK_SPEED_ID = UUID.fromString("a7c9f6c4-42dd-4b65-b3de-5615df5183ff");
    private static final UUID OCEAN_SWIM_SPEED_ID = UUID.fromString("ad4e3c1a-02f3-4ccf-8c34-23b1bc44de78");

    private static final double WALK_MULTIPLIER = 1.5; // +50% walk speed
    private static final double SWIM_SPEED = 1.5;       // Default swim speed is 1.0

    public AugmentMobSpeed(Mob mob) {
        if (mob.level().isClientSide) return;

        BlockPos pos = mob.blockPosition();
        Level level = mob.level();

        boolean inWater = level.getBlockState(pos).getBlock() == Blocks.WATER
                       || level.getBlockState(pos.below()).getBlock() == Blocks.WATER;

        if (inWater) {
            applyOceanBoost(mob);
        } else {
            removeOceanBoost(mob);
        }
    }

    private void applyOceanBoost(Mob mob) {
        // WALK SPEED
        var walkAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (walkAttr != null && walkAttr.getModifier(OCEAN_WALK_SPEED_ID) == null) {
            walkAttr.addTransientModifier(new AttributeModifier(
                OCEAN_WALK_SPEED_ID, "Ocean Movement Boost", WALK_MULTIPLIER, AttributeModifier.Operation.MULTIPLY_BASE
            ));
        }

        // SWIM SPEED
        var swimAttr = mob.getAttribute(ForgeMod.SWIM_SPEED.get());
        if (swimAttr != null) {
            if (swimAttr.getModifier(OCEAN_SWIM_SPEED_ID) == null) {
                swimAttr.addTransientModifier(new AttributeModifier(
                    OCEAN_SWIM_SPEED_ID, "Ocean Swim Speed", SWIM_SPEED, AttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    private void removeOceanBoost(Mob mob) {
        var walkAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (walkAttr != null && walkAttr.getModifier(OCEAN_WALK_SPEED_ID) != null) {
            walkAttr.removeModifier(OCEAN_WALK_SPEED_ID);
        }

        var swimAttr = mob.getAttribute(ForgeMod.SWIM_SPEED.get());
        if (swimAttr != null && swimAttr.getModifier(OCEAN_SWIM_SPEED_ID) != null) {
            swimAttr.removeModifier(OCEAN_SWIM_SPEED_ID);
        }
    }
}