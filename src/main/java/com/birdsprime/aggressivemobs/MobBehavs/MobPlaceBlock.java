package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

//Place block
public class MobPlaceBlock {

	
	// Cause given entity to block a certain block type at the given block position
	// Entity_Class - Entity placing this block
	// Block_Type - The type of block to place
	// Position - Position of this block
	public MobPlaceBlock(Entity Entity_Class, Block Block_Type, BlockPos Position) {

		// play sound for breaking block or placing block
		SoundEvent Snd;
		
		
			// Set this block as the set block
			Entity_Class.level().setBlockAndUpdate(Position, Block_Type.defaultBlockState());
	
			Snd = Block_Type.getSoundType(Block_Type.defaultBlockState()).getBreakSound();
	
			Entity_Class.level().playSound(null, Position, Snd, SoundSource.BLOCKS, 0.6F, 0.5F);

	}
}
