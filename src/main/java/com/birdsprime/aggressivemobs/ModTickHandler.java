// ModTickHandler.java
package com.birdsprime.aggressivemobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import com.birdsprime.aggressivemobs.Base.AggressiveCreeper;



public class ModTickHandler {
    public static void handleTick(Entity e) {
        if (!(e instanceof Mob mob)) return;

        // 1) reduce action lock
        int lock = mob.getPersistentData().getInt("action_lock");
        if (lock > 0) mob.getPersistentData().putInt("action_lock", lock - 1);

        // 2) named creepers only get creeperTick
        if (mob.hasCustomName() && mob instanceof net.minecraft.world.entity.monster.Creeper cr) {
            AggressiveCreeper.CreeperTick(cr);
            return;
        }

        // 3) only un-named mobs run full logic
        if (!mob.hasCustomName()) {
            LivingMobLogic.tick(mob);
        }
    }
}
