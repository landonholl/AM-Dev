package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.SnapToBlockCenter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MobBuildUp {

	private static final int BuildUpDist = 4;
	private static final Block Block_Type = Blocks.COBBLESTONE;

	public boolean isDoing;

	public MobBuildUp(Mob M) {

		if (M.getPersistentData().getInt("action_lock") > 0) return;

		Entity Tgt = M.getTarget();
		if (!(Tgt instanceof ServerPlayer)) return;

		ServerPlayer Server_Player = (ServerPlayer) Tgt;
		if (Server_Player.isCreative()) return;

		BlockPos M_Pos = M.blockPosition();
		BlockPos T_Pos = Tgt.blockPosition();

		if (!isCloseEnough(M_Pos, T_Pos)) return;
		if (!(isBelowPlayer(M_Pos, T_Pos) && isPlayerHighEnough(M_Pos, T_Pos))) return;

		BlockPos Head = M_Pos.above();
		BlockPos AboveHead = Head.above();

		ServerLevel Server_Level = Server_Player.serverLevel();
		BlockState AboveBlock = Server_Level.getBlockState(AboveHead);
		if (!AboveBlock.isAir()) return;

		Vec3 BuildDir = GetBuildDir(M_Pos, T_Pos);
		Vec3i Dir_I = new Vec3i((int)BuildDir.x, (int)BuildDir.y, (int)BuildDir.z);
		BlockPos Build_Pos = M_Pos.offset(Dir_I);

		new MobPlaceBlock(M, Block_Type, Build_Pos);
		M.setPos(M_Pos.getX(), M_Pos.getY() + 1.05, M_Pos.getZ());
		new SnapToBlockCenter(M);
		M.swing(M.getUsedItemHand());
		M.getPersistentData().putInt("action_lock", 5);
		isDoing = true;
	}

	boolean isCloseEnough(BlockPos M_Pos, BlockPos T_Pos) {
		return (Math.abs(M_Pos.getX() - T_Pos.getX()) < BuildUpDist) &&
		       (Math.abs(M_Pos.getZ() - T_Pos.getZ()) < BuildUpDist);
	}

	boolean isPlayerHighEnough(BlockPos M_Pos, BlockPos T_Pos) {
		return Math.abs(M_Pos.getY() - T_Pos.getY()) > 1;
	}

	boolean isBelowPlayer(BlockPos M_Pos, BlockPos T_Pos) {
		return M_Pos.getY() < T_Pos.getY();
	}

	Vec3 GetBuildDir(BlockPos M_Pos, BlockPos T_Pos) {
		float dx = T_Pos.getX() - M_Pos.getX();
		float dy = T_Pos.getY() - M_Pos.getY();
		float dz = T_Pos.getZ() - M_Pos.getZ();
		return new Vec3(dx, dy, dz).normalize();
	}
}
