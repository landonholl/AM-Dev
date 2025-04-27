package com.birdsprime.aggressivemobs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public final class SnapToBlockCenter {

    private SnapToBlockCenter() {
        // Prevent instantiation
    }

    public static void snap(Entity entity) {
        BlockPos blockPos = entity.blockPosition();
        double x = blockPos.getX() + 0.5;
        double z = blockPos.getZ() + 0.5;
        entity.setPos(x, blockPos.getY(), z);
    }
}
