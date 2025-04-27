package com.birdsprime.aggressivemobs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class SetPlayerSpawn {

    private SetPlayerSpawn() {
        // Prevent instantiation
    }

    public static void setPlayerSpawn(Player player, BlockPos pos) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.setRespawnPosition(serverPlayer.getRespawnDimension(), pos, 0F, true, true);
        }
    }
}
