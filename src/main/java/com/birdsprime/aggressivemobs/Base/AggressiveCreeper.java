package com.birdsprime.aggressivemobs.Base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.RNG;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;

//Generates creepers in the world. Creepers have a chance of being charged.
public class AggressiveCreeper {

	private static final Logger LOGGER = LogManager.getLogger();
	
	// Generates creepers in the world. Creepers have a chance of being charged.
	// Entity_Class - The creepr's entity class
	// ChargedChange - The percent chance this creeper starts as charged
	public AggressiveCreeper(Entity Entity_Class, int ChargedChance) {
		// Get random weight
		int Creeper_RNG = RNG.GetInt(0, 100);

		// If weight is below certain amount, make creeper charged
		if (Creeper_RNG < (float) ChargedChance) {
			MakeCharged(Entity_Class);
		}

		// Get random number for property weights
		double Nuclear_Val = RNG.GetDouble(0.0, 100.0);

		if (Nuclear_Val < AggressiveMobsConfig.ChanceOfNuclearCreeper.get()) {
			MakeNuclear(Entity_Class);
		}
	}

	// Make this creeper spawn as charged
	void MakeCharged(Entity Entity_Class) {
		// Make this creeper charged
		Creeper C = (Creeper) Entity_Class;
		CompoundTag nbt = C.serializeNBT();
		nbt.putBoolean("powered", true);
		C.deserializeNBT(nbt);
	}

	void MakeNuclear(Entity Entity_Class) {
		// Make this creeper charged
		Creeper C = (Creeper) Entity_Class;
		CompoundTag nbt = C.serializeNBT();
		nbt.putBoolean("powered", true);
		int IntNuclearCreeperBlastRadius = AggressiveMobsConfig.NuclearCreeperExplosionRadius.get();
		if (IntNuclearCreeperBlastRadius < 0)
			IntNuclearCreeperBlastRadius = 0;
		if (IntNuclearCreeperBlastRadius > 255)
			IntNuclearCreeperBlastRadius = 255;
		nbt.putByte("ExplosionRadius", (byte) IntNuclearCreeperBlastRadius);
		nbt.putInt("Fuse", AggressiveMobsConfig.NuclearCreeperFuse.get());

		C.deserializeNBT(nbt);

		LivingEntity Living_Entity = (LivingEntity) Entity_Class;

		// Reduce nuclear creeper health to a quarter
		Living_Entity.setHealth(Living_Entity.getHealth() / 4.0F);

		// Get current movement speed of movement
		Living_Entity.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(
				new AttributeModifier("Nuclear_Powered", 1.2F, AttributeModifier.Operation.MULTIPLY_BASE));

		// Add effect
		Living_Entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1000, 1));
	}

	// Continuous AI for creeper
	public static void CreeperTick(Creeper C) {
		
		if (C.hasCustomName() && C.getCustomName().getString().compareTo("ICBM") == 0) {

			LOGGER.info("NAME MATCH");
			// If creeper has not been ignited yet, then
			if (!C.isIgnited()){

				LOGGER.info("NOT IGNITED");
				ICBMCreeper(C);
				
			}
		}

	}	
	
	static void ICBMCreeper(Creeper C)
	{
		

		CompoundTag nbt = C.serializeNBT();
		nbt.putBoolean("powered", true);
		nbt.putByte("ExplosionRadius", (byte) 100);
		nbt.putByte("ExplosionPower", (byte) 10);
		nbt.putInt("Fuse", 200);

		C.deserializeNBT(nbt);
		
		LOGGER.info("IGNITE CREEPER - BOOM!");
		C.ignite();
	}

	void KillIfOverXTicks() {

	}
}
