package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner.BlockType;
import net.minecraft.world.level.block.state.BlockState;

public class ItemChecker {

	//Does the given entity / mob have a pickaxe?
	public static boolean EntityHasPickaxe(Entity Entity_Class)
	{
		Mob Mob_Class = (Mob)Entity_Class;
		ItemStack Item_In_Entity_Hand = Mob_Class.getMainHandItem();
		String Item_Name = Item_In_Entity_Hand.getDescriptionId();
		return Item_Name.toLowerCase().contains("pick");
		
	}
}
