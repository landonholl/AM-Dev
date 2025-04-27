package com.birdsprime.aggressivemobs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class DropChecker {

    public boolean isAllAir = true;

    public void checkColumnIsAir(Entity entity, int blockCount) {
        if (entity == null || blockCount <= 0) {
            isAllAir = false;
            return;
        }

        ServerLevel level = (ServerLevel) entity.level();
        BlockPos basePos = entity.blockPosition();

        for (int i = 0; i < blockCount && isAllAir; i++) {
            BlockPos checkPos = basePos.offset(0, i, 0);
            if (!level.getBlockState(checkPos).isAir()) {
                isAllAir = false;
            }
        }
    }
}
