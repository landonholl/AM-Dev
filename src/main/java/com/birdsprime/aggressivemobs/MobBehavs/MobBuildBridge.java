package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class MobBuildBridge {

	// Block for zombie to place when building up
	Block Block_Type = Blocks.COBBLESTONE;

	public boolean isDoing;

	// If next block is air, then, build bridge
	// M - The monster
	// Tgt - The target of this monster
	public MobBuildBridge(Mob M) {

		// Get zombie target
		Entity Tgt = M.getTarget();
		if (M.getPersistentData().getInt("action_lock") > 0) return;

		// If valid target, then
		if (Tgt != null) {

			// Is this target is a server player, then
			if (Tgt instanceof ServerPlayer) {

				// is monster at target level?
				if (isMonsterAtTargetLvl(M, Tgt)) {

					// Get zombie current position
					BlockPos M_Pos = M.blockPosition();

					// Look at player
					M.lookAt(Tgt, 0, 0);

					// Get offset block position
					 BlockPos Fwd_Block_Pos = M_Pos.relative(M.getDirection());

					// Get block at this position
						//Level Curr_Level = Entity_Class.getLevel();	//1.19.3
					Entity Entity_Class = (Entity)M;
					 Level Curr_Level = Entity_Class.level();		//1.20.1

					// Get block
					Block Current_Block = Curr_Level.getBlockState(Fwd_Block_Pos).getBlock();

					// If block is air, then
					if (Current_Block.defaultBlockState().isAir()) {
						// Get block below this one
						BlockPos Block_Below_Pos = Fwd_Block_Pos.below();
						BlockPos Block_Below_Pos2 = Block_Below_Pos.below();

						// If block below that one is air, then
						Block Block_Below = Curr_Level.getBlockState(Block_Below_Pos).getBlock();
						Block Block_Below2 = Curr_Level.getBlockState(Block_Below_Pos2).getBlock();
						
						if (Block_Below.defaultBlockState().isAir() && Block_Below2.defaultBlockState().isAir()) {

							// Place a block under this monster
							new MobPlaceBlock(Entity_Class, Block_Type, Fwd_Block_Pos.below());
							M.getPersistentData().putInt("action_lock", 1);

							M.swing(M.getUsedItemHand());

							isDoing = true;

						}
					}
				}
			}
		}
	}

	// Is the zombie's target at the same level as them?
	boolean isMonsterAtTargetLvl(Mob M, Entity Tgt) {
		// Get x and z for monster and target
		double M_Y = M.blockPosition().getY();
		double TgtY = Tgt.blockPosition().getY();

		return Math.abs(TgtY - M_Y) < 2;
	}

}
