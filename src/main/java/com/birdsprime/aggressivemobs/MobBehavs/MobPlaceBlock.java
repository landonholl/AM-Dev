package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;

public final class MobPlaceBlock {

    private MobPlaceBlock() {
        // Prevent instantiation
    }

    public static void placeBlock(Entity entity, Block blockType, BlockPos position) {
        entity.level().setBlockAndUpdate(position, blockType.defaultBlockState());
        entity.level().playSound(
            null,
            position,
            blockType.getSoundType(blockType.defaultBlockState()).getBreakSound(),
            SoundSource.BLOCKS,
            0.6F,
            0.5F
        );
    }
}
