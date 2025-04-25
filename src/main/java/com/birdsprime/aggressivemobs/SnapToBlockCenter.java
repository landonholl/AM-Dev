package com.birdsprime.aggressivemobs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

//Snap to center of block
public class SnapToBlockCenter {


	//Snap to center of block
	//Entity_Class - Entity to snap to center of block
	public SnapToBlockCenter(Entity Entity_Class)
	{
		//Get entity block position
		BlockPos Block_Pos = Entity_Class.blockPosition();
		
		//Get X and Z
		int X = Block_Pos.getX();
		int Y = Block_Pos.getY();
		int Z = Block_Pos.getZ();
		
		//Get mid of block
		double X_D = (double)X;
		double Z_D = (double)Z;
		
		//Set block pos
		Entity_Class.setPos(X_D, Y, Z_D);
	}
}
