package com.birdsprime.aggressivemobs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class DropChecker {

	// Is entire given column in this checker air?
	public boolean isAllAir;

	// Checks if given position to -n blocks below that position is all air. Starts
	// at original position to check if that is air.
	// Final checked positon is BlockCount - 1
	// Entity_Class - The entity to check drop for. original position will be this
	// entity's position
	// BlockCount - Number of positions down to check if they're air or not
	public void isColumnAir(Entity Entity_Class, int BlockCount) {
		// Get entity's original position
		BlockPos Original_Pos = Entity_Class.blockPosition();

		// Get server level
		//ServerLevel Lvl = (ServerLevel) Entity_Class.level;		//1.19
		ServerLevel Lvl = (ServerLevel) Entity_Class.level();	//1.20

		// Counts down number of blocks and ensure they're all air
		for (int i = 0; i < BlockCount; i++) {
			
			//Only continue in loop if is all air
			if (isAllAir) {

				// Offset for Y position to check downward
				
				
				//BlockPos Curr_Pos = Original_Pos.offset(0.0, i, 0.0);		//1.19.3	
				BlockPos Curr_Pos = Original_Pos.offset(0, i, 0);	//1.20.1

				// Get block at this position
				if (!Lvl.getBlockState(Curr_Pos).isAir()) {
					isAllAir = false;
				}
			}

		}
	}

}
