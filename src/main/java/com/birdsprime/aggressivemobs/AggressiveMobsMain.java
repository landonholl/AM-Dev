package com.birdsprime.aggressivemobs;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import  net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.birdsprime.aggressivemobs.Base.AggressiveCreeper;
import com.birdsprime.aggressivemobs.Base.AggressiveSkeleton;
import com.birdsprime.aggressivemobs.Base.AggressiveZombie;
import com.birdsprime.aggressivemobs.MobBehavs.CreeperBreachWalls;
import com.birdsprime.aggressivemobs.MobBehavs.ItemChecker;
import com.birdsprime.aggressivemobs.MobBehavs.MobBloodlust;
import com.birdsprime.aggressivemobs.MobBehavs.MobBuildBridge;
import com.birdsprime.aggressivemobs.MobBehavs.MobBuildUp;
// import com.birdsprime.aggressivemobs.MobBehavs.MobDig;
// import com.birdsprime.aggressivemobs.MobBehavs.MobDigDown;
// import com.birdsprime.aggressivemobs.MobBehavs.MobDigUp;
import com.birdsprime.aggressivemobs.MobBehavs.MobPlaceTNT;
import com.birdsprime.aggressivemobs.MobBehavs.MobStartFires;
import com.birdsprime.aggressivemobs.MobBehavs.MobTargetPlayer;
import com.birdsprime.aggressivemobs.MobBehavs.SpawnRideableMob;
import com.birdsprime.aggressivemobs.MobBehavs.SpiderShootWeb;

import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.fml.config.ModConfig;

import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import java.util.List;


import java.util.Optional;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("aggressive_mobs")
public class AggressiveMobsMain {

	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String ModID = "aggressivemobs";

	public AggressiveMobsMain() {

		// net.minecraft.world.entity.animal
		// net.minecraft.world.entity.monster
		// net.minecraft.world.entity.GlowSquid

		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the enqueueIMC method for modloading
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// Register the processIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AggressiveMobsConfig.SPEC, "aggressive-mobs-config.toml");

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {

	}

	@SubscribeEvent
	public void onCreeperHurt(LivingHurtEvent event) {
		if (!(event.getEntity() instanceof Creeper creeper)) return;

		// 1.20.1 fire‐damage check
		boolean isFireDamage   = event.getSource().is(DamageTypeTags.IS_FIRE);
		// burning arrow check
		Entity direct = event.getSource().getDirectEntity();
		boolean isBurningArrow = direct instanceof AbstractArrow && direct.isOnFire();

		if (isFireDamage || isBurningArrow) {
			// only prime once
			if (!creeper.isIgnited()) {
				// play the hiss sound
				creeper.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.0F);
				// vanilla ignite → starts normal fuse countdown
				creeper.ignite();
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	//public void onEntitySpawn(SpecialSpawn e) {	//1.19
	public void onEntitySpawn(MobSpawnEvent.FinalizeSpawn e) {	//1.20
		// Get expanded entity properties
		Entity Entity_Class = e.getEntity();

		//Sieges can recur every X days. If on Siege day, then
		if (isSiegeDay(Entity_Class)) {

			new MobTargetPlayer(Entity_Class);

			// If is monster, then, target player
			if (Entity_Class instanceof Mob) {

				// If is not client object, then
				if (!Entity_Class.level().isClientSide) {

					// Cast as monster
					Mob M = (Mob) Entity_Class;

					// If this is a zombie, then
					if (Entity_Class.getType() == EntityType.ZOMBIE
							|| Entity_Class.getType() == EntityType.ZOMBIE_VILLAGER
							|| Entity_Class.getType() == EntityType.HUSK) {

						// Create new nightmare zombie
						new AggressiveZombie(Entity_Class);
					}

					// If this is a creeper, then
					if (Entity_Class instanceof Creeper C) {

						if (AggressiveMobsConfig.isChargedCreeperAllowed.get()) {
							// If entity is a creeper, then
							new AggressiveCreeper(Entity_Class, AggressiveMobsConfig.ChargedCreeperChance.get());
						}
					}

					// If this is a skeleton then
					if (Entity_Class.getType() == EntityType.SKELETON) {
						new AggressiveSkeleton(Entity_Class);
					}

					if (AggressiveMobsConfig.Entity_Duplication.get()) {

						int MaxDupes = AggressiveMobsConfig.MaxDuplicationClones.get();
						int MinDupes = AggressiveMobsConfig.MinDuplicationClones.get();
						if (MinDupes > MaxDupes) {
							MinDupes = MaxDupes;
						}

						// If mob was not summoned after it was spawned and is not loading from save,
						// then...
						if (e.getSpawnType() != MobSpawnType.MOB_SUMMONED
								&& e.getSpawnType() != MobSpawnType.CHUNK_GENERATION &&
								e.getSpawnType() != MobSpawnType.STRUCTURE) {
							int TotalDupes = new RNG().GetInt(MinDupes, MaxDupes);
							if (ShouldDuplicate()) {
								new DuplicateMob(M, TotalDupes);
							}
						}
					

						// If entity came from a spawner, run duplication again to create even more
						// monsters
						if (e.getSpawnType() == MobSpawnType.SPAWNER) {

							int MaxDungeonDupes = AggressiveMobsConfig.MaxDungeonDuplicationClones.get();
							int MinDungeonDupes = AggressiveMobsConfig.MinDungeonDuplicationClones.get();
							if (MinDungeonDupes > MaxDungeonDupes) {
								MinDungeonDupes = MaxDungeonDupes;
							}

							int TotalDupes = new RNG().GetInt(MinDungeonDupes, MaxDungeonDupes);

							new DuplicateMob(M, TotalDupes);
						}
					}
						// if jockeys are allowed
					if (AggressiveMobsConfig.isSpecialJockeyMobsAllowed.get()) {
						// Check if this entity should ride a mob (with RNG)
						double RNG_Val = new RNG().GetDouble(0.0, 100.0);
						double Jockey_Chance = AggressiveMobsConfig.SpecialJockeyGenerationChance.get();
						if (RNG_Val < Jockey_Chance) {
							// This entity will spawn riding a special jockey animal

							new SpawnRideableMob(Entity_Class);
						}

					}

					// Get RNG for blood-lusting entities
					if (AggressiveMobsConfig.AngryEntities.get()) {
						float Bloodlust_RNG = new RNG().GetInt(0, 100);
						if (Bloodlust_RNG < AggressiveMobsConfig.AngryEntityChance.get()) {

							new MobBloodlust(Entity_Class);
						}
					}

				}
			}
		}
	
	}

	// Based on RNG, should this entity duplicate?
	boolean ShouldDuplicate() {
		int RNG_Val = new RNG().GetInt(0, 100);
		return RNG_Val < AggressiveMobsConfig.DuplicationChance.get();
	}

	// Is the current day a day that is having a siege, based on siege recurrence?
	boolean isSiegeDay(Entity Entity_Class) {
		//long Uptime = Entity_Class.getLevel().getDayTime();	//1.19.3
		long Uptime = Entity_Class.level().getDayTime();	//1.20.1
		long DayCount = (Uptime / 24000);
		return ((DayCount % AggressiveMobsConfig.InvadeEveryXDays.get()) == 0);
	}

	@SubscribeEvent
	public void EntityTick(LivingTickEvent e) {
		// Get entity being ticked
		Entity Entity_Class = e.getEntity(); // 1.20.1

		// Handle all creeper fuse countdowns (including modded/vanilla)
		if (Entity_Class instanceof Creeper) {
			Creeper creeper = (Creeper) Entity_Class;

			if (creeper.getPersistentData().contains("forced_fuse_ticks")) {
				int fuse = creeper.getPersistentData().getInt("forced_fuse_ticks");
				fuse--;

				if (fuse <= 0) {
					creeper.level().explode(
						creeper,
						creeper.getX(),
						creeper.getY(),
						creeper.getZ(),
						creeper.isPowered() ? 6.0F : 3.0F,
						net.minecraft.world.level.Level.ExplosionInteraction.MOB
					);
					creeper.discard();
				} else {
					creeper.getPersistentData().putInt("forced_fuse_ticks", fuse);
				}
			}
		}

		// Now handle general modded mob behavior
		if (Entity_Class instanceof Mob) {
			Mob Mob_Class = (Mob) Entity_Class;

			// Reduce any action lock counter
			int lock = Mob_Class.getPersistentData().getInt("action_lock");
			if (lock > 0) {
				Mob_Class.getPersistentData().putInt("action_lock", lock - 1);
			}

			// If unnamed, run full mod logic
			if (!Mob_Class.hasCustomName()) {
				LivingMobTick(e, Entity_Class);
			} else if (Mob_Class instanceof Creeper) {
				// Named creepers still get custom ticking (e.g., enhanced AI)
				AggressiveCreeper.CreeperTick((Creeper) Mob_Class);
			}
		}
	}


	// Do tick only for a living mob
	void LivingMobTick(LivingTickEvent e, Entity Entity_Class) {

		// Current time
		long TimeStamp = System.currentTimeMillis();

		// If is not client object, then
		if (!Entity_Class.level().isClientSide) {

			new MobTargetPlayer(Entity_Class);

			// If this is a creeper, then, perform breaching behavior
			if (Entity_Class instanceof Creeper C) {
				if (AggressiveMobsConfig.isCreeperBreachingAllowed.get()) {
					new CreeperBreachWalls(C);
				}
				AggressiveCreeper.CreeperTick(C);
			}


			// Monster instance
			Mob Entity_Mob = (Mob) Entity_Class;

			// If this is a zombie, then
			if (Entity_Class instanceof Zombie) {

				// Is game rule "Mob Griefing" Allowed?
				boolean isMobGriefingAllowed = Entity_Class.getServer().getGameRules()
						.getBoolean(GameRules.RULE_MOBGRIEFING);

				
				// Are zombies allowed to destroy blocks?
				if (isMobGriefingAllowed || AggressiveMobsConfig.AllowZombieGriefing.get()) {

					Zombie zombie = (Zombie) Entity_Class;

					// only when holding pickaxe if configured
					boolean needPick = AggressiveMobsConfig.EntitiesNeedPickaxesToBreakBlocks.get();
					if (!needPick || ItemChecker.EntityHasPickaxe(zombie)) {
						LivingEntity target = zombie.getTarget();
						// only if target exists and within config range
						if (target != null && isWithinDigRange(zombie, target)) {
							Path path = zombie.getNavigation().getPath();
							int cd = zombie.getPersistentData().getInt("digCooldown");
							if ((path == null || !path.canReach()) && cd <= 0) {
								attemptDigTowards(zombie, target);
							} else if (cd > 0) {
								// decrement cooldown each tick
								zombie.getPersistentData().putInt("digCooldown", cd - 1);
							}
						}
						
					}				

					if (Clocker.IsAtTimeInterval(Entity_Class, AggressiveMobsConfig.EntityBuildDelay.get())) {
						MobBuildUp Zombies_Build_Up = new MobBuildUp(Entity_Mob);
						MobBuildBridge Zombie_Build_Bridge = new MobBuildBridge(Entity_Mob);
					}

					if (AggressiveMobsConfig.ZombiesLayTNT.get()) {
						// Place TNT
						new MobPlaceTNT(Entity_Class);
					}

					// Is fire-lighting allowed?
					if (AggressiveMobsConfig.ZombiesLightFires.get()) {
						// If so, check if should light wood on fire?
						new MobStartFires(Entity_Mob);
					}
				}

			}

			// Behaviors for spiders
			if (Entity_Class.getType() == EntityType.SPIDER || Entity_Class.getType() == EntityType.CAVE_SPIDER) {

				if (AggressiveMobsConfig.SpidersShootWebs.get()) {
					// Shoot webs
					new SpiderShootWeb(Entity_Mob);
				}
			}

			// Can entities hop on the ocean?
			if (AggressiveMobsConfig.isEntitiesBounceOnWater.get()) {

				// LOGGER.info("1");
				// Augment entity speeds on ocean
				new AugmentMobSpeed(Entity_Mob);
			}

		}
	}

	    /**
     * Returns true if the horizontal distance to the target is
     * within the configured dig range.
     */
    private boolean isWithinDigRange(Zombie zombie, LivingEntity target) {
        double dx = zombie.getX() - target.getX();
        double dz = zombie.getZ() - target.getZ();
        double maxDist = AggressiveMobsConfig.EntityDigXZDistance.get(); // make sure this matches your config field
        return dx*dx + dz*dz <= maxDist*maxDist;
    }

    /**
     * Figures out which two blocks (2-high tunnel) to break
     * based on whether the player is above/level/below.
     */
    private void attemptDigTowards(Zombie zombie, LivingEntity target) {
        Level level = zombie.level();
        BlockPos base = zombie.blockPosition();
        Direction face = zombie.getDirection();
        double dy = target.getY() - zombie.getY();

        BlockPos pos1, pos2;
        if (dy > 1.0) {
            // player is above → break blocks at head level and above
            pos1 = base.relative(face, 1).above(1);
            pos2 = pos1.above();
        } else if (dy < -1.0) {
            // player is below → break front-floor and block below it
            pos1 = base.relative(face, 1);
            pos2 = pos1.below();
        } else {
            // roughly level → break 2-high door forward
            pos1 = base.relative(face, 1);
            pos2 = pos1.above();
        }

        // swing animation
        zombie.swing(InteractionHand.MAIN_HAND);
        // actually break them
        breakBlocks(zombie, pos1, pos2);

        // set a cooldown so we only dig every EntityDigDelay ticks
        zombie.getPersistentData().putInt("digCooldown", AggressiveMobsConfig.EntityDigDelay.get());
    }

    /**
     * Breaks each non-air, non-liquid block in positions
     * unless it matches the blacklist.
     */
    private void breakBlocks(Zombie zombie, BlockPos... positions) {
        Level level = zombie.level();
        for (BlockPos p : positions) {
            if (p == null) continue;
            BlockState st = level.getBlockState(p);
            if (st.isAir() || !st.getFluidState().isEmpty()) continue;
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(st.getBlock());
            if (isBlockBlacklisted(id)) continue;
            level.destroyBlock(p, /*drop=*/true, /*byEntity=*/zombie);
        }
    }

    /**
     * Returns true if the block’s registry name contains
     * any of the substrings in your blacklist.
     */
    private boolean isBlockBlacklisted(ResourceLocation blockID) {
		// fetch raw CSV from config
		String raw = AggressiveMobsConfig.BlocksEntitiesCannotDigThrough.get();
		// split on commas
		for (String pattern : raw.split(",")) {
			if (blockID.toString().contains(pattern)) {
				return true;
			}
		}
		return false;
	}
	


	// Fires when player tries to sleep
	// e - Event
	@SubscribeEvent
	public void cancelSleep(PlayerSleepInBedEvent e) {

		if (!AggressiveMobsConfig.AllowSleeping.get()) {
			// Set player spawn pos
			Player Player_Entity = e.getEntity();
			new SetPlayerSpawn(Player_Entity, e.getPos());

			// Deny sleeping in bed
			e.setResult(BedSleepingProblem.NOT_SAFE);

		}
	}

	// Convert comma-separated list to entities
	public static EntityType[] GetEntitiesFromSerialized(String SerialList) {
		String[] Entity_String_List = SerialList.split(",");
		EntityType[] RetVal = new EntityType[Entity_String_List.length];
		for (int i = 0; i < Entity_String_List.length; i++) {
			String Curr_Entity = Entity_String_List[i];
			EntityType Entity_Type_Class = AggressiveMobsMain.ToEntity(Curr_Entity);
			RetVal[i] = Entity_Type_Class;
		}

		return RetVal;
	}

	// Finds an entity class by name
	public static EntityType ToEntity(String Name) {
		Optional<EntityType<?>> Entity_Type = EntityType.byString(Name);
		if (Entity_Type.isPresent()) {
			return Entity_Type.get();
		} else {
			return null;
		}

	}

	public static String EntitiesToSerialList(EntityType[] Entity_List) {

		String Entity_List_Str = "";
		for (int i = 0; i < Entity_List.length; i++) {
			EntityType Curr_Entity = Entity_List[i];
			if (Curr_Entity == null) {
				Entity_List_Str += "NULL,";
			} else {
				Entity_List_Str += Curr_Entity.getDescription().getString().toLowerCase() + ",";
			}

		}

		return Entity_List_Str;

	}

	private void processIMC(final InterModProcessEvent event) {
		// some example code to receive and process InterModComms from other mods
		LOGGER.info("Got IMC {}",
				event.getIMCStream().map(m -> m.messageSupplier().get()).collect(Collectors.toList()));
	}

//	// You can use SubscribeEvent and let the Event Bus discover methods to call
//	@SubscribeEvent
//	public void onServerStarting(ServerStartingEvent event) {
//
//	}

	// You can use EventBusSubscriber to automatically subscribe events on the
	// contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {

	}
}
