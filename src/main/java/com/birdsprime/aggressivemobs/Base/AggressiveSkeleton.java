package com.birdsprime.aggressivemobs.Base;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.GiveRandomArmor;
import com.birdsprime.aggressivemobs.RNG;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;

public class AggressiveSkeleton {

	//Creates a nightmare skeleton
	//Entity - The skeleton enttiy
	public AggressiveSkeleton(Entity Entity_Class)
	{
		//Give skeleton random armor
		if(AggressiveMobsConfig.GiveSkeletonsArmor.get())
		{
			GiveRandomArmor.giveRandomArmor(Entity_Class);
		}
		
	}
	
}
