package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.RNG;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SpiderShootWeb {

	// Adds behavior for a spider shooting web if close enough to player
	// M - The monster class for the attacking entity
	public SpiderShootWeb(Mob M) {

		// If spider has target, then
		LivingEntity Tgt = M.getTarget();

		// If spider has target, then
		if (Tgt != null) {

			// If target is server player, then
			if (Tgt instanceof ServerPlayer) {

				ServerPlayer Targeted_Player = (ServerPlayer) Tgt;

				// Is spider in shooting distance?
				if (SpiderIsInShootDistance(M, Targeted_Player)) {

					double New_RNG = new RNG().GetDouble(0.0, 100.0);
					if (New_RNG < AggressiveMobsConfig.SpiderShootWebChance.get()) {
						ShootWebAtPlayer(M, Targeted_Player);
					}

				}
			}
		}
	}

	// Is spider in shooting distance
	boolean SpiderIsInShootDistance(Mob Spider_Entity, ServerPlayer Targeted_Player) {
		float Dist = Spider_Entity.distanceTo(Targeted_Player);
		return Dist < AggressiveMobsConfig.SpiderShootWebDist.get();
	}

	// Entity spider shoots web at targeted player
	void ShootWebAtPlayer(Mob Spider_Entity, ServerPlayer Targeted_Player) {
		// Get current level
		Level Lvl = Spider_Entity.level();

		// Get block at player position
		BlockState Player_Block_State = Lvl.getBlockState(Targeted_Player.blockPosition());

		// If player in air, then
		if (Player_Block_State.isAir()) {
			// Set as web
			Lvl.setBlock(Targeted_Player.blockPosition(), Blocks.COBWEB.defaultBlockState(), 1);
		}

	}

}
