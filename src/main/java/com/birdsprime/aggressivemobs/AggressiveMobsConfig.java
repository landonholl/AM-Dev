package com.birdsprime.aggressivemobs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class AggressiveMobsConfig {
	
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	
	//Options
	public static final ForgeConfigSpec.ConfigValue<Boolean> isSiegeModeEnabled;
	public static final ForgeConfigSpec.ConfigValue<Boolean> Entity_Duplication;
	public static final ForgeConfigSpec.ConfigValue<Boolean> isSpecialJockeyMobsAllowed;
	public static final ForgeConfigSpec.ConfigValue<Boolean> isChargedCreeperAllowed;
	public static final ForgeConfigSpec.ConfigValue<Boolean> AngryEntities;
	public static final ForgeConfigSpec.ConfigValue<Boolean> GiveZombiesPickaxe;
	public static final ForgeConfigSpec.ConfigValue<Boolean> GiveZombiesArmor;
	public static final ForgeConfigSpec.ConfigValue<Boolean> GiveSkeletonsArmor;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ZombiesLayTNT;
	public static final ForgeConfigSpec.ConfigValue<Double> ZombieTNTChance;
	public static final ForgeConfigSpec.ConfigValue<Integer> ZombieTNTDistance;
	public static final ForgeConfigSpec.ConfigValue<Boolean> AllowZombieGriefing;
	public static final ForgeConfigSpec.ConfigValue<Boolean> AllowZombieBuilding;	
	public static final ForgeConfigSpec.ConfigValue<Boolean> SpidersShootWebs;
	public static final ForgeConfigSpec.ConfigValue<Boolean> isCreeperBreachingAllowed;
	public static final ForgeConfigSpec.ConfigValue<Boolean> isEntitiesBounceOnWater;
	public static final ForgeConfigSpec.ConfigValue<Boolean> AllowSleeping;
	public static final ForgeConfigSpec.ConfigValue<Boolean> AllowMonsterInfighting;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ZombiesLightFires;
	public static final ForgeConfigSpec.ConfigValue<Boolean> AllowSuperSkeletons;
	public static final ForgeConfigSpec.ConfigValue<Boolean> isMonstersTargetVillagers;
	public static final ForgeConfigSpec.ConfigValue<Boolean> EntitiesNeedPickaxesToBreakBlocks;
	
	public static final ForgeConfigSpec.ConfigValue<Integer> InvadeEveryXDays;	
	public static final ForgeConfigSpec.ConfigValue<Double> SpecialJockeyGenerationChance;
	public static final ForgeConfigSpec.ConfigValue<Integer> DuplicationChance;
	public static final ForgeConfigSpec.ConfigValue<Double> DungeonDuplicationChance;
	public static final ForgeConfigSpec.ConfigValue<Integer> ChargedCreeperChance;
	public static final ForgeConfigSpec.ConfigValue<Integer> AngryEntityChance;
	public static final ForgeConfigSpec.ConfigValue<Integer> CreeperBreachingDistance;
	public static final ForgeConfigSpec.ConfigValue<Integer> CreeperStrikingDistance;
	public static final ForgeConfigSpec.ConfigValue<Integer> CreeperAboveExplodeDistance;
	public static final ForgeConfigSpec.ConfigValue<Integer> CreeperObstructedExplodeTicks;
	public static final ForgeConfigSpec.ConfigValue<Integer> MobMaxLife;
	public static final ForgeConfigSpec.ConfigValue<Double> ChanceOfNuclearCreeper;
	public static final ForgeConfigSpec.ConfigValue<Integer> NuclearCreeperExplosionRadius;
	public static final ForgeConfigSpec.ConfigValue<Integer> NuclearCreeperFuse;
	public static final ForgeConfigSpec.ConfigValue<Integer> ChanceOfPickaxe;
	public static final ForgeConfigSpec.ConfigValue<Double> ChanceOfZombieLightFire;
	public static final ForgeConfigSpec.ConfigValue<Double> ChanceOfSuperSkeleton;
	public static final ForgeConfigSpec.ConfigValue<Integer> EntityDigDelay;
	public static final ForgeConfigSpec.ConfigValue<Integer> EntityBuildDelay;
	public static final ForgeConfigSpec.ConfigValue<Integer> EntityDigXZDistance;
	
	public static final ForgeConfigSpec.ConfigValue<Integer> MonsterAttackDistanceAboveGround;
	public static final ForgeConfigSpec.ConfigValue<Integer> MonsterAttackDistanceUnderground;
	
	public static final ForgeConfigSpec.ConfigValue<Double> SpiderShootWebChance;
	public static final ForgeConfigSpec.ConfigValue<Integer> SpiderShootWebDist;
	
	public static final ForgeConfigSpec.ConfigValue<Integer> MaxDuplicationClones;
	public static final ForgeConfigSpec.ConfigValue<Integer> MinDuplicationClones;
	
	public static final ForgeConfigSpec.ConfigValue<Integer> MaxDungeonDuplicationClones;
	public static final ForgeConfigSpec.ConfigValue<Integer> MinDungeonDuplicationClones;
	
	public static final ForgeConfigSpec.ConfigValue<String> SiegeEntityWhitelist;

//	public static final ForgeConfigSpec.ConfigValue<boolean> UseBlockWhitelist;

	public static final ForgeConfigSpec.ConfigValue<String> BlocksEntitiesCannotDigThrough;
//	public static final ForgeConfigSpec.ConfigValue<String> BlocksEntitiesCanDigThrough;

	public static final ForgeConfigSpec.ConfigValue<String> RideableMobs;
	//public static final ForgeConfigSpec.ConfigValue<List> RideableMobsTest;
	public static final ForgeConfigSpec.ConfigValue<String> JockeyMobs;
	
	
	static
	{
		//Title
		BUILDER.push("Aggressive Mobs: Config");
		BUILDER.pop();
		
		//Definitions
		isSiegeModeEnabled = BUILDER.comment("Enable Enemies Swarming You").define("enableSiege", true);
		Entity_Duplication = BUILDER.comment("Enable Increased Enemy Presence").define("incEnemies", true);
		isSpecialJockeyMobsAllowed = BUILDER.comment("Should special mobs spawn riding other mobs?").define("isJockies", true);
		isChargedCreeperAllowed = BUILDER.comment("Charged Creepers Spawn Naturally").define("isChargedCreepers", true);
		AngryEntities = BUILDER.comment("Allow Bloodlusting Enemies").define("isBloodlusting", true);
		GiveZombiesPickaxe = BUILDER.comment("Give Zombies Pickaxes and Weapons?").define("isZombiePickaxes", true);
		GiveZombiesArmor = BUILDER.comment("Give Zombies Armor?").define("isZombieArmor", true);
		GiveSkeletonsArmor = BUILDER.comment("Give Skeletons Armor?").define("isSkeletonArmor", true);
		ZombiesLayTNT = BUILDER.comment("Allow Zombies to Lay TNT?").define("isZombieTnt", true);
		ZombieTNTChance = BUILDER.comment("Chance of zombies placing TNT each tick if in distance.").define("zombieTNTChance", 0.25);
		ZombieTNTDistance = BUILDER.comment("How close zombies need to be to player to allow setting TNT.").define("zombieTNTDistance", 2);
		AllowZombieGriefing = BUILDER.comment("Allow Zombies To Destroy Blocks? SETTING IS DEPRECATED AND WILL BE REMOVED IN FUTURE RELEASE. Please set mobGriefing server game rule instead.").define("iszombiegriefing", true);
		AllowZombieBuilding = BUILDER.comment("Allow Zombies To Place Blocks? DEPRECATED - REPLACED BY mobGriefing game rule. Will be removed in future.").define("iszombiebuilding", true);
		SpidersShootWebs = BUILDER.comment("Allow Spiders to Shoot Webs?").define("isSpiderShootWebs", true);
		isCreeperBreachingAllowed = BUILDER.comment("Can Creepers Breach Walls?").define("isCreeperBreaching", true);
		isEntitiesBounceOnWater = BUILDER.comment("Do entities bounce when they touch water? Default = true.").define("isOceanSurfing", true);
		AllowSleeping = BUILDER.comment("Allow Sleeping?").define("allowSleep", false);
		AllowMonsterInfighting = BUILDER.comment("Allow Monster Infighting?").define("allowMonsterInfighting", false);
		ZombiesLightFires = BUILDER.comment("Allow Zombies and Piglins to light flammable things on fire?").define("zombiesLightFires", true);
		AllowSuperSkeletons = BUILDER.comment("Allow Super Skeletons to Spawn in the Game?").define("allowSuperSkeletons", true);
		isMonstersTargetVillagers = BUILDER.comment("Should monsters target villagers?").define("isMonstersTargetVillagers", true);
		EntitiesNeedPickaxesToBreakBlocks = BUILDER.comment("Should only entities holding pickaxes be able to dig?").define("zombiePickaxeDigOnly", false);
		
		InvadeEveryXDays = BUILDER.comment("What days should sieges occur? 1 = default (everyday, constantly). 2 = every other day, 7 every week, and so on.").define("siegeRecurrence", 1);
		SpecialJockeyGenerationChance = BUILDER.comment("Chance of special mob spawning and riding another entity out of 100.").define("jockeyChance", 5.0);
		DuplicationChance = BUILDER.comment("Chance Entity Duplicate on Spawn (Out of 100)").define("dupeChance", 10);
		DungeonDuplicationChance = BUILDER.comment("Chance of entity duplicating when in dungeon (Out of 100)").define("dungeonDupeChance", 50.0);
		ChargedCreeperChance = BUILDER.comment("Chance of Creepers Spawning Charged Out of 100").define("chargedChance", 1);
		AngryEntityChance = BUILDER.comment("Chance of Entities Spawning Bloodlusted Out of 100").define("angryEntityChance", 10);
		CreeperBreachingDistance = BUILDER.comment("Distance at Which Creepers Will Breach if Blocked (in Blocks)").define("breachingDist", 64);
		CreeperStrikingDistance = BUILDER.comment("Creepers Will Explode if This Close to Player (in Blocks)").define("creeperExplodeDist", 6);
		CreeperAboveExplodeDistance = BUILDER.comment("Distance At Which Creepers Breech the Ground (in Blocks)").define("creepersBreechGroundDist", 6);
		CreeperObstructedExplodeTicks = BUILDER.comment("Ticks Until Blocked Creeper Explodes (in Game Ticks)").define("obstructedCreeperExpTicks", 60);
		MobMaxLife = BUILDER.comment("Maximum ticks in an entity's life until they despawn (if their target is the player). 20 Ticks = 1 second.").define("mobMaxLife", 4800);
		NuclearCreeperExplosionRadius = BUILDER.comment("Nuclear Creeper Explosion Radius (Default vanilla is 3").define("nuclearCreeperExplosionRadius", 10);
		NuclearCreeperFuse = BUILDER.comment("How long (in ticks) Nuclear Creeper is lit before it blows up").define("nuclearCreeperFuse",120);
		ChanceOfNuclearCreeper = BUILDER.comment("Chance of Nuclear Creeper out of 100").define("chanceNuclearCreeper", 0.25);
		ChanceOfPickaxe = BUILDER.comment("Chance of Zombies Receiving Special Item or Pickaxe Out of 100").define("pickaxeChance", 20);
		ChanceOfZombieLightFire = BUILDER.comment("Chance of Zombies Lighting Flammable Items on Fire Per Tick Out of 100").define("zombieFireChance", 1.0);
		ChanceOfSuperSkeleton = BUILDER.comment("Chance of More Powerful Skeleton Spawning").define("superSkeletonChance", 1.0);
		EntityDigDelay = BUILDER.comment("Cooldown time for entities breaking blocks in ticks").define("entityDigDelay", 5);
		EntityBuildDelay = BUILDER.comment("Cooldown time for entities placing blocks in ticks").define("entityBuildDelay", 5);
		EntityDigXZDistance = BUILDER.comment("If entity is within this distance for X and Z, it'll start digging to player").define("entityDigDist", 32);
					
		MonsterAttackDistanceAboveGround = BUILDER.comment("Above Ground Monster Attack Distance (in Blocks)").define("groundDist", 90);
		MonsterAttackDistanceUnderground = BUILDER.comment("Below Ground Monster Attack Distance (in Blocks)").define("caveDist", 42);
		
		SpiderShootWebChance = BUILDER.comment("Chance out of 100 for spider shooting webs each tick. Default = 0.5").define("spiderWebChance", 0.5);
		SpiderShootWebDist = BUILDER.comment("Distance at which spider can shoot a web at player. Default = 2 blocks").define("spiderWebDist", 2);
		
		MaxDuplicationClones = BUILDER.comment("Maximum Duplicated Enemies. Recommend Min. 1, Max. 4. Must be greater than min duplication clones and greater than 0.").define("maxDupes", 3);
		MinDuplicationClones = BUILDER.comment("Minimum Duplicated Enemies. Recommend Min. 0, Max. 4. Must be less than max duplication clones, or, will use max dupes value.").define("minDupes", 0);
		
		MaxDungeonDuplicationClones = BUILDER.comment("Maximum Duplicated Enemies FROM A DUNGEON. Recommend Min. 1, Max. 6. Must be greater than min duplication clones and greater than 0.").define("maxDungeonDupes", 6);
		MinDungeonDuplicationClones = BUILDER.comment("Maximum Duplicated Enemies FROM A DUNGEON. Recommend Min. 0, Max. 3. Must be less than max duplication clones, or, will use max dupes value.").define("minDungeonDupes", 1);

		SiegeEntityWhitelist = BUILDER.comment("Entities that will besiege players.").define("siegeWhitelist", "creeper,skeleton,witch,"
				+ "ghast,giant,husk,hoglin,pillager,vindicator,illusioner,evoker,ravager,shulker,silverfish,stray,slime,witch,zoglin,"
				+ "zombie,zombie_villager,zombified_piglin,zoglin,drowned,zombie_villager,wither_skeleton,pillager");
		
		BlocksEntitiesCannotDigThrough = BUILDER.comment("Any part of a block name, which defines which blocks that siege monsters CANNOT break to try and get to your position.").define("siegeBreakableBlockBlacklist",
				"_ore,"
				+ "_sign,air,water,lava,_bed,rail,_block,obsidian,torch,fire,spawner,ladder,furnace,level,iron_door,pressure_plate,_button,sugar_cane,"
				+ "juke,soul_,portal,cake,repeater,trapdoor,iron_bars,chain,vine,lily_pad,enchanting,brewing,cauldron,end_,dragon,potted_,skull,_head,"
				+ "anvil,chest,dropper,comparator,hopper,detector,Pillar,terracotta,light,prismarine,book,grass,snow,flower,sapling,bush,stem,dragon,"
				+ "carrots,potatoes,detector,powered,concrete,barrier,carpet,banner,beet,void,shulker,structure,compost,piston,dispenser,fern,pickle,"
				+ "chorus,purpur,beacon,conduit,frame,lectern,smith,ancient,armor,wither,barrel,cutter,hive,lode,respawn,sculk,brick,copper");
		
		RideableMobs = BUILDER.comment("Defines all mobs that can spawn as rideable by spawning monsters. E.g. 'chicken' will cause enemies to spawn riding chickens.").define("rideableMobs",
				"creeper,skeleton,chicken,pig,skeleton_horse,zombie_horse");

		JockeyMobs = BUILDER.comment("Mobs that can spawn riding other mobs.").define("jockeyMobs",
				"creeper,skeleton,zombie,pillager,vindicator,witch,zombified_piglin");
		
		SPEC = BUILDER.build();	
		
	}
	
}
