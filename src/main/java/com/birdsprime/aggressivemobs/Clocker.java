package com.birdsprime.aggressivemobs;
import net.minecraft.world.entity.Entity;

public class Clocker {

	//If using time to get index each from current ms timestamp
	public static int GetIndexFromTime(int IndexCount)
	{
		long Index_Long_Val = System.currentTimeMillis() % IndexCount;
		return (int)Index_Long_Val;
	}
	
	//Check if time is at given time interval. For example, allows ticking every 300 milliseconds if entering 300.
	// TimeIntervalMS = Time Interval to check in Milliseconds.
	public static boolean IsAtTimeInterval(int TimeIntervalMS)
	{
		return (System.currentTimeMillis() % TimeIntervalMS) == 0;
	}
	
	//Check if time is at given time interval. For example, allows ticking every 300 milliseconds if entering 300.
	// Entity_Class = Grab this entity's ticks to see if they are at a given interval.
	// TimeIntervalMS = Time Interval to check in Milliseconds.
	public static boolean IsAtTimeInterval(Entity Entity_Class, int TimeIntervalMS)
	{
		return (Entity_Class.tickCount % TimeIntervalMS) == 0;
	}
	
}
