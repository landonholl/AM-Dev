package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;

public class MobDigUp {

	float DigDist = 16.0F;
	public boolean isDoing;

	public MobDigUp(Mob M) {

		Entity M_Entity = (Entity) M;

		if (M.getPersistentData().getInt("action_lock") > 0) {
			return;
		}

		if (!M.swinging) {
			BlockPos Zombie_Pos = M.blockPosition();
			Entity Tgt = M.getTarget();

			if (Tgt instanceof ServerPlayer) {
				BlockPos Tgt_Pos = Tgt.blockPosition();

				if (isCloseEnough(Zombie_Pos, Tgt_Pos) && isTargetAbove(Zombie_Pos, Tgt_Pos)) {

					BlockPos HeadPos = Zombie_Pos.above();
					Block Dmg_Block_Head = M.level().getBlockState(HeadPos).getBlock();
					new MobDamageBlock(M_Entity, Dmg_Block_Head, HeadPos);

					BlockPos HeadPos_2 = Zombie_Pos.above(2);
					if (!HeadPos_2.equals(HeadPos)) {
						Block Dmg_Block = M.level().getBlockState(HeadPos_2).getBlock();
						new MobDamageBlock(M_Entity, Dmg_Block, HeadPos_2);
					}

					M.swing(M.getUsedItemHand());
					M.getPersistentData().putInt("action_lock", 20);
					isDoing = true;
				}
			}
		}
	}

	boolean isCloseEnough(BlockPos M_Pos, BlockPos T_Pos) {
		return (Math.abs(M_Pos.getX() - T_Pos.getX()) < DigDist) &&
		       (Math.abs(M_Pos.getZ() - T_Pos.getZ()) < DigDist);
	}

	boolean isTargetAbove(BlockPos M_Pos, BlockPos T_Pos) {
		return T_Pos.getY() > M_Pos.getY();
	}
}
