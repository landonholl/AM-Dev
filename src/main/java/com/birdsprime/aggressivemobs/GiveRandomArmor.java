package com.birdsprime.aggressivemobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class GiveRandomArmor {

    // Expanded to include all vanilla helmets + pumpkins
    private static final Item[] ARMOR_HEAD = {
        Items.LEATHER_HELMET,
        Items.CHAINMAIL_HELMET,
        Items.IRON_HELMET,
        Items.GOLDEN_HELMET,
        Items.DIAMOND_HELMET,
        Items.NETHERITE_HELMET,
        Items.TURTLE_HELMET
    };

    private static final Item[] ARMOR_CHEST = {
        Items.LEATHER_CHESTPLATE,
        Items.CHAINMAIL_CHESTPLATE,
        Items.IRON_CHESTPLATE,
        Items.GOLDEN_CHESTPLATE,
        Items.DIAMOND_CHESTPLATE,
        Items.NETHERITE_CHESTPLATE
    };

    private static final Item[] ARMOR_LEGGINGS = {
        Items.LEATHER_LEGGINGS,
        Items.CHAINMAIL_LEGGINGS,
        Items.IRON_LEGGINGS,
        Items.GOLDEN_LEGGINGS,
        Items.DIAMOND_LEGGINGS,
        Items.NETHERITE_LEGGINGS
    };

    private static final Item[] ARMOR_BOOTS = {
        Items.LEATHER_BOOTS,
        Items.CHAINMAIL_BOOTS,
        Items.IRON_BOOTS,
        Items.GOLDEN_BOOTS,
        Items.DIAMOND_BOOTS,
        Items.NETHERITE_BOOTS
    };

    private GiveRandomArmor() {
        // Prevent instantiation
    }

    public static void giveRandomArmor(Entity entity) {
        // Roll once per entity spawn
        int roll = RNG.GetInt(0, 100);
        if (roll >= AggressiveMobsConfig.ChanceOfPickaxe.get()) {
            // no armor this time
            return;
        }

        // Choose a random armor set index
        int idx = RNG.GetInt(0, ARMOR_HEAD.length - 1);

        // Equip head
        entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ARMOR_HEAD[idx]));
        // Equip chest
        entity.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ARMOR_CHEST[idx % ARMOR_CHEST.length]));
        // Equip leggings
        entity.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ARMOR_LEGGINGS[idx % ARMOR_LEGGINGS.length]));
        // Equip boots
        entity.setItemSlot(EquipmentSlot.FEET, new ItemStack(ARMOR_BOOTS[idx % ARMOR_BOOTS.length]));
    }
}
