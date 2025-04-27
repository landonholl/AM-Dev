package com.birdsprime.aggressivemobs.Base;


import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.GiveRandomArmor;
import com.birdsprime.aggressivemobs.GiveRandomPickaxe;
import com.birdsprime.aggressivemobs.RNG;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;

//net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration

//Spawn a nightmare zombie with chance of it having a creeper as a jockey. Also has a chance of being a lightning zombie e.g. extremely fast,
//extra health.
public class AggressiveZombie {
	  
    
	//Spawn a nightmare zombie with chance of it having a creeper as a jockey. Also has a chance of being a lightning zombie e.g. extremely fast,
	//extra health.
	public AggressiveZombie(Entity Entity_Class) 
	{    	
		//Get RNG value for this spawn
    	int ZombWeightValue = RNG.GetInt(0, 100);
    	    	
    	//Cast to zombie
    	Monster M = (Monster)Entity_Class;    		
    	
    	//Should zombies be allowed to hold special items?
    	if(AggressiveMobsConfig.GiveZombiesPickaxe.get())
    	{
    		int PickaxeWeightVal = RNG.GetInt(0,100);
	    	//Give this zombie a random pickaxe based on probability
	    	if(PickaxeWeightVal < AggressiveMobsConfig.ChanceOfPickaxe.get())
	    	{
	    		GiveRandomPickaxe.giveRandomPickaxe(M);
	    	}
    	}
    	
    	//Should zombies be given special armor?
    	if(AggressiveMobsConfig.GiveZombiesArmor.get())
    	{
    		//Give random armor
    		GiveRandomArmor.giveRandomArmor(Entity_Class);
    	}
	}
	

	// Set number of ticks for creeper
	void SetTicks(Monster M, int Amt) {
		M.getPersistentData().putInt("placecooloff", Amt);
		M.getPersistentData().putInt("digcooloff", Amt);
	}
	
}
