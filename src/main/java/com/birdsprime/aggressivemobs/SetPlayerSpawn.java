package com.birdsprime.aggressivemobs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class SetPlayerSpawn {

	//Sets a player's spawn
	public SetPlayerSpawn(Player Player_Entity, BlockPos Pos)
	{
		ServerPlayer Server_Player = (ServerPlayer)Player_Entity;
		Server_Player.setRespawnPosition(Server_Player.getRespawnDimension(), Pos, 0F, true, true);
	}
	
}
