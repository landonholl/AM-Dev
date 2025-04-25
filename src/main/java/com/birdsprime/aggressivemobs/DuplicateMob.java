package com.birdsprime.aggressivemobs;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

//Causes an entity to be duplicated
public class DuplicateMob {


	// Within how many blocks should swarming entity be placed?
	int Swarm_Placement_Proximity = 2;

	// Points where an entity was already placed so that entities are not placed on
	// top of each other when spawned.
	ArrayList<BlockPos> Used_Points = new ArrayList<BlockPos>();

	// These monsters are allowed to be duplicated.
	EntityType[] Duplication_Whitelist = { EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.DROWNED,
			EntityType.ENDERMAN, EntityType.EVOKER, EntityType.GHAST, EntityType.HOGLIN, EntityType.HUSK,
			EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER, EntityType.SILVERFISH, EntityType.SKELETON,
			EntityType.SPIDER, EntityType.RAVAGER, EntityType.VEX, EntityType.VINDICATOR, EntityType.WITCH,
			EntityType.WITHER_SKELETON, EntityType.ZOGLIN, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER,
			EntityType.ZOMBIFIED_PIGLIN, EntityType.STRAY};
	
	// Duplicate the given monster
	// M - the monster to duplicate
	// Count - Number of clones to create
	public DuplicateMob(Mob M, int Count) {
		// If this monster is on the duplication whitelist, then
		if (isWhitelisted(M)) {

			for (int i = 0; i < Count; i++) {

				// For number of cloning iterations, clone
				Clone(M);
			}
		}
	}

	// Clone the given monster
	// M - Monster to clone.
	void Clone(Mob M) {

		// Current Minecraft server
		//ServerLevel Lvl = (ServerLevel)M.level;	//1.19
		ServerLevel Lvl = (ServerLevel)M.level();	//1.20
		
		// Get entity block position
		BlockPos pos = M.blockPosition();

		// Get offset block position
		BlockPos Offset_Pos = GetOffsetPosition(pos);

		if (isPositionGood(Lvl, Offset_Pos)) {
			M.getType().spawn(Lvl, new ItemStack(Items.BIRCH_WOOD), (Player) null, Offset_Pos,
					MobSpawnType.MOB_SUMMONED, true, false);
		}
	}

	// Is the given monster allowed to be duplicated?
	boolean isWhitelisted(Mob M) {
		for (int i = 0; i < Duplication_Whitelist.length; i++) {
			if (Duplication_Whitelist[i] == M.getType()) {
				// Monster is on the white list
				return true;
			}
		}

		// This monster is not on the white list
		return false;
	}

	// Get original block offset position
	BlockPos GetOffsetPosition(BlockPos Original_Pos) {
		// Get randomly offset X and Z
		//double Rand_X_Off = new RNG().GetInt(-Swarm_Placement_Proximity, Swarm_Placement_Proximity);	//1.19
		//double Rand_Z_Off = new RNG().GetInt(-Swarm_Placement_Proximity, Swarm_Placement_Proximity);	//1.19

		int Rand_X_Off = new RNG().GetInt(-Swarm_Placement_Proximity, Swarm_Placement_Proximity);		//1.20
		int Rand_Z_Off = new RNG().GetInt(-Swarm_Placement_Proximity, Swarm_Placement_Proximity);		//1.20
		
		// Offset old block position
		//BlockPos New_Pos = Original_Pos.offset(Rand_X_Off, 0.0D, Rand_Z_Off);	//1.19
		BlockPos New_Pos = Original_Pos.offset(Rand_X_Off, 0, Rand_Z_Off);	//1.20

		return New_Pos;
	}

	// Should a duplicate monster not be spawned here? True if placement is OK.
	// Lvl - The current level mob being spawned in
	// Position - The potential position to spawn this mob in.
	boolean isPositionGood(ServerLevel Lvl, BlockPos Position) {
		boolean isBottomOK = Lvl.getBlockState(Position).isAir();
		//boolean isTopOK = Lvl.getBlockState(Position.offset(0D, 1D, 0D)).isAir();	//1.19
		boolean isTopOK = Lvl.getBlockState(Position.offset(0, 1, 0)).isAir();	//1.20
		return isBottomOK && isTopOK;
	}

}
