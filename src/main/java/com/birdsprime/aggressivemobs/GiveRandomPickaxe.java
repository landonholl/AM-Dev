package com.birdsprime.aggressivemobs;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
public class GiveRandomPickaxe {

	//Weights
	Item[] Pickaxes = {Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.WOODEN_PICKAXE, Items.IRON_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD, Items.IRON_AXE, Items.WOODEN_AXE,
			Items.STONE_AXE, Items.DIAMOND_AXE, Items.DIAMOND_PICKAXE};

	//Give this zombie a random pickaxe
	public GiveRandomPickaxe(Monster M)
	{
		//Get current item in hand
		ItemStack Pickaxe_Item_Stack = M.getItemBySlot(EquipmentSlot.MAINHAND);
		
		//Add pickaxe if there is nothing in this hand
		if(Pickaxe_Item_Stack.isEmpty())
		{
			Item Pickaxe_Item = RandomFromList();
			Pickaxe_Item_Stack = new ItemStack(Pickaxe_Item);
			M.setItemSlot(EquipmentSlot.MAINHAND, Pickaxe_Item_Stack);
		}
	}
	//Get random pickaxe from the list
	Item RandomFromList()
	{
		int PickaxeIndex = new RNG().GetInt(0, Pickaxes.length);
		return Pickaxes[PickaxeIndex];
	}
	
}
