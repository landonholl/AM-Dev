package com.birdsprime.aggressivemobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.pathfinder.Path;

public final class CannotReachPlayer {

    private CannotReachPlayer() {
        // Prevent instantiation
    }

    /**
     * Returns true if the given monster cannot reach its current target via pathfinding.
     * @param entity any Entity (only Monsters are checked)
     */
    public static boolean cannotReach(Entity e) {
        if (!(e instanceof Monster m))                return false;
        var nav = m.getNavigation();
        if (nav == null)                              return true;   // no nav ⇒ can’t reach
        var p = nav.getPath();
        if (p   == null)                              return true;   // no path ⇒ can’t reach
        return !p.canReach();
    }
    
}
