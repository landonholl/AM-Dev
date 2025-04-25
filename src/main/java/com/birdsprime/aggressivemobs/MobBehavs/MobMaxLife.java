package com.birdsprime.aggressivemobs.MobBehavs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MobMaxLife {

	private static final Logger LOGGER = LogManager.getLogger();
	
	public MobMaxLife(Entity Entity_Class)
	{		
		//If mob has custom name, do not kill. Otherwise, die.
		if(!Entity_Class.hasCustomName())
		{
			//If life expired, then, kill entity
			if(Entity_Class.tickCount > AggressiveMobsConfig.MobMaxLife.get())
			{
				KillMobRiding(Entity_Class);
				Entity_Class.discard();
			}		
			
		}
		
		
	}
	
	//Kill the mob that this mob is riding (if they are riding one)
	void KillMobRiding(Entity Entity_Class) {
		Entity Veh = Entity_Class.getVehicle();
		if(Veh instanceof LivingEntity)
		{
			Veh.discard();
		}
	}
	
}
