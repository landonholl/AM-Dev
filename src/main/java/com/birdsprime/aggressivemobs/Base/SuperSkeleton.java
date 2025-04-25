package com.birdsprime.aggressivemobs.Base;

import com.birdsprime.aggressivemobs.MobBehavs.MobBloodlust;

import net.minecraft.world.entity.monster.Monster;

public class SuperSkeleton {

	//Turns a skeleton into a "Super Skeleton" with enhanced abilities
	public SuperSkeleton(Monster M)
	{
		//Give skeleton bloodlust
		 new MobBloodlust(M);
		
	}
	
	
	
}
