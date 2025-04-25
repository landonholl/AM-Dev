package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.GetBlockingBlock;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

public class MobDig {

	// Is zombie currently digging?
	public boolean isDoing;

	float DigDist = 2F;

	// Behavior that allows zombies to dig towards player if they can't get to them.
	public MobDig(Entity Entity_Class) {

		// Zombie as monster
		Monster M = (Monster) Entity_Class;

		// If monster not swinging, then
		if (!M.swinging) {

			// Get current target. If is valid target, then continue
			LivingEntity Tgt = M.getTarget();
			if (Tgt != null) {

				// If zombie's target is a server player, then
				if (Tgt instanceof ServerPlayer) {

					// Cast as server player
					ServerPlayer Server_Player = (ServerPlayer) Tgt;

					// If this player is not in creative
					if (!Server_Player.isCreative()) {

						// Get blocking block info
						GetBlockingBlock Blocking_Block = new GetBlockingBlock(Entity_Class);

						// Swing arms if block blocking
						if (!Blocking_Block.Current_Block.defaultBlockState().isAir()
								|| !Blocking_Block.Current_Block.defaultBlockState().isAir()) {
							// Try to dig
							M.swing(M.getUsedItemHand());
							
							new MobDamageBlock(Entity_Class, Blocking_Block.Current_Block,
									Blocking_Block.Current_Position);

							new MobDamageBlock(Entity_Class, Blocking_Block.Feet_Block, Blocking_Block.Feet_Position);

							isDoing = true;
						}

					}
				}

			}
		}
	}

	// Get distance from target - is this monster close enough to start digging up?
	boolean isAtLevel(Monster M, Entity Tgt) {

		// Get x and z for monster and target
		double M_Y = M.blockPosition().getY();
		double Tgt_Y = Tgt.blockPosition().getY();

		double Dist = M_Y - Tgt_Y;
		Dist = Math.abs(Dist);

		// Is almost at this entity's level
		return (Dist < DigDist);

	}

}