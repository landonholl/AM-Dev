package com.birdsprime.aggressivemobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.pathfinder.Path;

public class CannotReachPlayer {


	// Can this zombie reach the player?
	boolean CannotReach(Entity Entity_Class) {

		// Path navigation
		PathNavigation Path_Nav = null;

		Monster M = (Monster) Entity_Class;
		Path_Nav = M.getNavigation();

		if (Path_Nav != null) {

			// Get this entity's path. If it's null, then cannot reach player.
			Path Entity_Path = Path_Nav.getPath();

			if(Entity_Path != null)
			{
				return !Entity_Path.canReach();
			}else{
				return false;
			}
			
		} else {
			return false;
		}

	}

}
