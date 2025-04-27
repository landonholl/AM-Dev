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
import net.minecraft.world.entity.animal.IronGolem;

import com.birdsprime.aggressivemobs.Base.AggressiveCreeper;
import com.birdsprime.aggressivemobs.Base.AggressiveSkeleton;
import com.birdsprime.aggressivemobs.Base.AggressiveZombie;
import com.birdsprime.aggressivemobs.MobBehavs.FastSkeletonAttackGoal;
import com.birdsprime.aggressivemobs.MobBehavs.CreeperBreachWalls;
import com.birdsprime.aggressivemobs.MobBehavs.MobBuildBridge;
import com.birdsprime.aggressivemobs.MobBehavs.MobBuildUp;
import com.birdsprime.aggressivemobs.MobBehavs.MobPlaceBlock;
// import com.birdsprime.aggressivemobs.MobBehavs.MobDig;
// import com.birdsprime.aggressivemobs.MobBehavs.MobDigDown;
// import com.birdsprime.aggressivemobs.MobBehavs.MobDigUp;
import com.birdsprime.aggressivemobs.MobBehavs.MobPlaceTNT;
import com.birdsprime.aggressivemobs.MobBehavs.MobStartFires;
import com.birdsprime.aggressivemobs.MobBehavs.MobTargetPlayer;
import com.birdsprime.aggressivemobs.MobBehavs.PoweredArrowTrailGoal;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;


import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;

import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("aggressive_mobs")
@Mod.EventBusSubscriber(
  modid = AggressiveMobsMain.ModID,
  bus   = Mod.EventBusSubscriber.Bus.FORGE
)
public class AggressiveMobsMain {

	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	private static final String MISS_TAG = "skeletonMissCount";

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

			MobTargetPlayer.targetNearestPlayer(Entity_Class);

			// If is monster, then, target player
			if (Entity_Class instanceof Mob) {

				

				// If is not client object, then
				if (!Entity_Class.level().isClientSide) {

					// Cast as monster
					Mob M = (Mob) Entity_Class;

					if (M.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) != null) {
						M.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
							.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
								"global_speed_boost", 0.04D, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
							));
					}

					// If this is a zombie, then
					if (Entity_Class.getType() == EntityType.ZOMBIE
							|| Entity_Class.getType() == EntityType.ZOMBIE_VILLAGER
							|| Entity_Class.getType() == EntityType.HUSK) {

						// Create new nightmare zombie
						new AggressiveZombie(Entity_Class);
					}

					// If this is a skeleton then
					if (Entity_Class instanceof Skeleton skel) {

						new AggressiveSkeleton(skel);
						// remove any existing bow‐attack goals
						skel.goalSelector.getAvailableGoals().removeIf(entry ->
							entry.getGoal() instanceof RangedBowAttackGoal
						);
						// now add your faster version at the same (or higher) priority
						skel.goalSelector.addGoal(2,
							new FastSkeletonAttackGoal(skel, 1.0D, 5, 15.0F)
						);

                        skel.goalSelector.addGoal(3, new PoweredArrowTrailGoal(skel));
					}

					// If this is a creeper, then
					if (Entity_Class instanceof Creeper C) {

						if (AggressiveMobsConfig.isChargedCreeperAllowed.get()) {
							// If entity is a creeper, then
							new AggressiveCreeper(Entity_Class, AggressiveMobsConfig.ChargedCreeperChance.get());
						}
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
							int TotalDupes = RNG.GetInt(MinDupes, MaxDupes);
							if (ShouldDuplicate()) {
								new DuplicateMob(M, MinDupes);
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

							int TotalDupes = RNG.GetInt(MinDungeonDupes, MaxDungeonDupes);

							new DuplicateMob(M, MinDupes);
						}
					}
						// if jockeys are allowed
					if (AggressiveMobsConfig.isSpecialJockeyMobsAllowed.get()) {
						// Check if this entity should ride a mob (with RNG)
						double RNG_Val = RNG.GetDouble(0.0, 100.0);
						double Jockey_Chance = AggressiveMobsConfig.SpecialJockeyGenerationChance.get();
						if (RNG_Val < Jockey_Chance) {
							// This entity will spawn riding a special jockey animal

							new SpawnRideableMob(Entity_Class);
						}

					}

				}
			}
		}
	
	}

	/**
	 * Track when a skeleton’s arrow “misses” (i.e., doesn’t hit a living entity).
	 */
	@SubscribeEvent
    public static void onArrowImpact(ProjectileImpactEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;
        if (!(arrow.getOwner() instanceof Skeleton skel)) return;

        HitResult result = event.getRayTraceResult();
        if (result instanceof EntityHitResult) {
            // hit a living entity → reset miss count
            skel.getPersistentData().putInt(MISS_TAG, 0);
        } else {
            // hit block or nothing → increment miss count
            int misses = skel.getPersistentData().getInt(MISS_TAG) + 1;
            skel.getPersistentData().putInt(MISS_TAG, misses);

            if (misses > 2) {
                skel.getPersistentData().putBoolean("readyToBoostNextArrow", true);
                skel.getPersistentData().putInt(MISS_TAG, 0);
            }
        }
    }


	/**
	 * When a Skeleton’s arrow entity joins the world, boost it if
	 * that skeleton has missed more than twice.
	 */
	@SubscribeEvent
    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;
        if (!(arrow.getOwner() instanceof Skeleton skel)) return;

        if (skel.getPersistentData().getBoolean("readyToBoostNextArrow")) {
            arrow.getPersistentData().putBoolean("shouldBoost", true);
            arrow.getPersistentData().putBoolean("isPoweredTrail", true);
            arrow.setDeltaMovement(arrow.getDeltaMovement().scale(2.0D));
            arrow.setBaseDamage(arrow.getBaseDamage() * 2.0D);
            arrow.setCritArrow(true);

            // Reset the flag so only next arrow is boosted
            skel.getPersistentData().putBoolean("readyToBoostNextArrow", false);
        }
    }


	@SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            for (AbstractArrow arrow : level.getEntitiesOfClass(AbstractArrow.class, level.getWorldBorder().getCollisionShape().bounds())) {
                if (arrow.getPersistentData().getBoolean("shouldBoost")) {
                    if (arrow.tickCount > 1) {
                        // re-boost velocity & damage once
                        arrow.setDeltaMovement(arrow.getDeltaMovement().scale(2.0D));
                        arrow.setBaseDamage(arrow.getBaseDamage() * 2.0D);
                        arrow.setCritArrow(true);

                        // spawn a burst of crit particles
                        arrow.level().addParticle(
                            ParticleTypes.CRIT,
                            arrow.getX(), arrow.getY(), arrow.getZ(),
                            0.0, 0.0, 0.0
                        );

                        // After boosting, clear shouldBoost but KEEP isPoweredTrail for trails
                        arrow.getPersistentData().putBoolean("shouldBoost", false);
                        arrow.getPersistentData().putBoolean("isPoweredTrail", true);
                    }
                }
            }
        }
    }

	
	// Based on RNG, should this entity duplicate?
	boolean ShouldDuplicate() {
		int RNG_Val = RNG.GetInt(0, 100);
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
        Entity Entity_Class = e.getEntity();

        if (!Entity_Class.level().isClientSide) {

            if (Entity_Class instanceof Creeper creeper) {
                // Handle creeper fuse countdown
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
                // Named creepers still get custom ticking
                AggressiveCreeper.CreeperTick(creeper);
                return;
            }

            if (Entity_Class instanceof Mob mob) {
                // Unified mob behavior (digging, building, fire, TNT, spiders, golems, etc.)
                LivingMobTickHandler.tick(mob);
            }
        }
    }

	// Fires when player tries to sleep
	// e - Event
	@SubscribeEvent
	public void cancelSleep(PlayerSleepInBedEvent e) {

		if (!AggressiveMobsConfig.AllowSleeping.get()) {
			// Set player spawn pos
			Player Player_Entity = e.getEntity();
			SetPlayerSpawn.setPlayerSpawn(Player_Entity, e.getPos());

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
	@Mod.EventBusSubscriber(modid = AggressiveMobsMain.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {

	}
}
