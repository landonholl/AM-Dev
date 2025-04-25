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
			new GiveRandomArmor(Entity_Class);
		}
		
		//Get random number between 0 and 100 for chance of super skeleton
		if(AggressiveMobsConfig.AllowSuperSkeletons.get())
		{
			int SkeleWeightValue = new RNG().GetInt(0, 100);
			double Chance_SuperSkeleton = AggressiveMobsConfig.ChanceOfSuperSkeleton.get();
			if(SkeleWeightValue < Chance_SuperSkeleton)
			{
				Monster M = (Monster)Entity_Class;
				
				new SuperSkeleton(M);
			}
		}
	}
	
}
