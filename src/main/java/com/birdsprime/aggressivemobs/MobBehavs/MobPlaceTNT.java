package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.GetNearestTarget;
import com.birdsprime.aggressivemobs.RNG;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;

public class MobPlaceTNT {

	// Causes this entity to place TNT
	// Entity - the entity that places TNT
	public MobPlaceTNT(Entity Entity_Class) {

		// Get player nearest to entity
		Entity Nearest_Entity = new GetNearestTarget().Get(Entity_Class);

		// If valid target, then
		if (Nearest_Entity != null) {

			if (CanPlaceThisTick() && isCloseEnough(Nearest_Entity, Entity_Class)) {
				// Get entity block position
				BlockPos Entity_Block_Pos = Entity_Class.blockPosition();

				// Current Minecraft server
				ServerLevel Lvl = (ServerLevel) Entity_Class.level();

				// Place TNT
				EntityType.TNT.spawn(Lvl, null, (Player) null, Entity_Block_Pos, MobSpawnType.MOB_SUMMONED, true,
						false);
			}
		}

	}

	// Check if targeted entity is close enough to entity to drop TNT
	boolean isCloseEnough(Entity Target_Entity, Entity Entity_Class) {
		return Target_Entity.distanceTo(Entity_Class) < AggressiveMobsConfig.ZombieTNTDistance.get();
	}

	// Can entity place TNT this tick?
	boolean CanPlaceThisTick() {
		double Rand_Num = new RNG().GetDouble(0.0, 100.0);
		return (Rand_Num < AggressiveMobsConfig.ZombieTNTChance.get());
	}

}
