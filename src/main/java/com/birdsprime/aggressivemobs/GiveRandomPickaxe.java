package com.birdsprime.aggressivemobs;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class GiveRandomPickaxe {

    private static final Item[] PICKAXES = {
        Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.WOODEN_PICKAXE,
        Items.IRON_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD,
        Items.IRON_AXE, Items.WOODEN_AXE, Items.STONE_AXE,
        Items.DIAMOND_AXE, Items.DIAMOND_PICKAXE
    };

    private GiveRandomPickaxe() {
        // Prevent instantiation
    }

    public static void giveRandomPickaxe(Monster mob) {
        ItemStack handItem = mob.getItemBySlot(EquipmentSlot.MAINHAND);
        if (handItem.isEmpty()) {
            Item pickaxeItem = randomFromList();
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(pickaxeItem));
        }
    }

    private static Item randomFromList() {
        int index = RNG.GetInt(0, PICKAXES.length - 1);
        return PICKAXES[index];
    }
}
