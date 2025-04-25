package com.birdsprime.aggressivemobs.MobBehavs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.Clocker;
import com.birdsprime.aggressivemobs.RNG;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

//Handles creation of mobs that a given mob can jockey
public class SpawnRideableMob {

	private static final Logger LOGGER = LogManager.getLogger();
	
	static EntityType[] RideableList;

	static EntityType[] JockeyList;

	// Create an entity for this mob to ride
	public SpawnRideableMob(Entity Jockey) {
		try{
				
		RefreshEntityList();

		ServerLevel Lvl = (ServerLevel)Jockey.level();

		if (!Lvl.isNaturalSpawningAllowed(Jockey.chunkPosition())){
			//LOGGER.info("Chunk not loaded yet");
			return;
		}

		else {
			//LOGGER.info("Chunk is loaded: " + Lvl.toString());
			BlockPos Above = Jockey.blockPosition().above();
			boolean isAboveOK = Lvl.getBlockState(Above).isAir();

			BlockPos Above_2 = Above.above();
			boolean isAboveOK_2 = Lvl.getBlockState(Above_2).isAir();

			if (isAboveOK && isAboveOK_2) {

			LOGGER.info("Spawning Jockey at: " + Jockey.blockPosition().toString());

				if (isJockeyMob(Jockey)) {
					RideAMob(Jockey);
				}
			}
		}
	}
	catch (Exception e) {
        LOGGER.error("Error in SpawnRideableMob: " + e.getMessage());
        e.printStackTrace();
    }
	}

	// Refreshes list of rideable mobs and jockeying mobs on first initialization
	void RefreshEntityList() {
		
		LOGGER.info("ESM - Init Rideable EntityEntity_Type and Jockey Lists...");
		
		if (RideableList == null) {
			RideableList = com.birdsprime.aggressivemobs.AggressiveMobsMain.GetEntitiesFromSerialized(AggressiveMobsConfig.RideableMobs.get().toString());
			LOGGER.info("RideListLength = " + RideableList.length);
			LOGGER.info("RideList = " + com.birdsprime.aggressivemobs.AggressiveMobsMain.EntitiesToSerialList(RideableList));
		}

		if (JockeyList == null) {
			JockeyList = com.birdsprime.aggressivemobs.AggressiveMobsMain.GetEntitiesFromSerialized(AggressiveMobsConfig.JockeyMobs.get().toString());
			LOGGER.info("Jockey List Length = " + JockeyList.length);
			LOGGER.info("JockeyList = " + com.birdsprime.aggressivemobs.AggressiveMobsMain.EntitiesToSerialList(JockeyList));
		}

	}

	boolean isJockeyMob(Entity Jockey) {
		return AggressiveMobsConfig.JockeyMobs.get().contains(Jockey.getName().getString().toLowerCase());
	}
	
	void RideAMob(Entity Jockey) {
		
		Entity MobToRide = GetRandomRideableMobAndSpawn(Jockey);
		if(MobToRide != null)
		{
			Jockey.startRiding(MobToRide);
		}
		
	}
	
	Entity GetRandomRideableMobAndSpawn(Entity Jockey)
	{
		ServerLevel Lvl = (ServerLevel)Jockey.level();
		
		// Current Minecraft server
		int MobNum = Clocker.GetIndexFromTime(RideableList.length);
		EntityType Entity_Type_To_Spawn = RideableList[MobNum];
		if(Entity_Type_To_Spawn != null)
		{
		Entity Entity_Class = Entity_Type_To_Spawn.spawn(Lvl,new ItemStack(Items.DIRT), (Player) null, Jockey.blockPosition(), MobSpawnType.MOB_SUMMONED, true, false);
		return Entity_Class;
		}
		else {
			return null;
		}
	}
	

}
