package com.birdsprime.aggressivemobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GiveRandomArmor {

	//Chance of an entity spawning with armor
	float Chance_Of_Armor = 20;
	
	//Items that each entity can have
	Item[] Armor_Head = {Items.CHAINMAIL_HELMET, Items.GOLDEN_HELMET, Items.IRON_HELMET, Items.LEATHER_HELMET};
	Item[] Armor_Chest = {Items.CHAINMAIL_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.IRON_CHESTPLATE, Items.LEATHER_CHESTPLATE};
	Item[] Armor_Leggings = {Items.CHAINMAIL_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.IRON_LEGGINGS, Items.LEATHER_LEGGINGS};
	Item[] Armor_Boots = {Items.CHAINMAIL_BOOTS, Items.GOLDEN_BOOTS, Items.IRON_BOOTS, Items.LEATHER_BOOTS};
	
	//Gives this entity random armor set
	public GiveRandomArmor(Entity Entity_Class)
	{
		int RNG_Val = new RNG().GetInt(0,100);
		if(RNG_Val < Chance_Of_Armor)
		{
			//Get armor to set
			int ArmorType = new RNG().GetInt(0,Armor_Head.length);
			Item Head_Armor = Armor_Head[ArmorType];
			Item Chest_Armor = Armor_Chest[ArmorType];
			Item Leggings_Armor = Armor_Leggings[ArmorType];
			Item Boots_Armor = Armor_Boots[ArmorType];
			
			//Set armor
			Entity_Class.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Head_Armor));
			Entity_Class.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Chest_Armor));
			Entity_Class.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Leggings_Armor));
			Entity_Class.setItemSlot(EquipmentSlot.FEET, new ItemStack(Boots_Armor));
		}
		
		

		
	}
	
	
}
