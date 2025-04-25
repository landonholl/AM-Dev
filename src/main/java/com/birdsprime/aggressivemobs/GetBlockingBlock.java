// File: GetBlockingBlock.java
package com.birdsprime.aggressivemobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

//Get block that is blocking entity from moving
public class GetBlockingBlock {

	//The blocking block's current position
	public BlockPos Current_Position;
	
	//Block that is blocking this entity
	public Block Current_Block;
	
	//Can this block block this entity
	public boolean isBlocking;
	
	//The block position at this entity's feet
	public BlockPos Feet_Position;
	
	//The block in front of this entity's feet
	public Block Feet_Block;
	
	//Is the block at this entity's foot blocking it from moving?
	public boolean isFootBlocked;
	
	//Get the block that is blocking this entity
	public GetBlockingBlock(Entity Entity_Class)
	{
		
		//Get entity eye position (x,y,z)
		//Vec3 Entity_Eye_Pos = Entity_Class.getEyePosition();		1.19
		Vec3 Entity_Eye_Pos = Entity_Class.getEyePosition();
		
		//Get normalized forward for entity
		//Vec3 Fwd = Entity_Class.getForward().normalize();		//1.19
		Vec3 Fwd = Entity_Class.getForward().normalize();	
		
		//Get block at forward position. No need for cell size, as cubes are 1x1x1
		//Vec3 Fwd_Block_Pos = Entity_Eye_Pos.add(Fwd);
		Vec3 Fwd_Block_Pos = Entity_Eye_Pos.add(Fwd);
		
		//As block position
		//BlockPos Fwd_Block = new BlockPos(Fwd_Block_Pos); //1.19
		Vec3i Fwd_Block_Pos_I = new Vec3i((int)Fwd_Block_Pos.x, (int)Fwd_Block_Pos.y, (int)Fwd_Block_Pos.z);
		BlockPos Fwd_Block = new BlockPos(Fwd_Block_Pos_I);
		Current_Position = Fwd_Block;
		
		//Get block at this position
		//Level Curr_Level = Entity_Class.getLevel();	//1.19.3
		Level Curr_Level = Entity_Class.level();		//1.20.1
		
		//Get block
		Current_Block = Curr_Level.getBlockState(Fwd_Block).getBlock();
		
		//Would this block prevent mob or player from going through it?
		//isBlocking = !Current_Block.defaultBlockState().isAir() && !Current_Block.defaultBlockState().getMaterial().isLiquid();
		isBlocking = !Current_Block.defaultBlockState().isAir() && !Current_Block.defaultBlockState().liquid();
				
		//Get blocks below
		Feet_Position = Fwd_Block.below();
		Feet_Block = Current_Block = Curr_Level.getBlockState(Feet_Position).getBlock();
		isFootBlocked = !Current_Block.defaultBlockState().isAir() && !Current_Block.defaultBlockState().liquid();
	}
	
}
