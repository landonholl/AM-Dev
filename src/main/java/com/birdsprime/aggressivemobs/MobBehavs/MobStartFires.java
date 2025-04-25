package com.birdsprime.aggressivemobs.MobBehavs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.RNG;

import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;
//import net.minecraft.world.level.material.Material;	//1.19

public class MobStartFires {

	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	String[] Excluded_Tags = { "_sign", "_bed", "pressure_plate", "_button", "sugar_cane", "juke", "trapdoor",
			"enchanting", "brewing", "potted_", "skull", "_head", "chest", "dropper", "prismarine", "book", "grass",
			"snow", "flower", "sapling", "bush", "stem", "dragon", "carrots", "potatoes", "carpet", "banner",
			"structure", "compost", "piston", "log" };

	// If entity is adjacent to wood, will light it on fire.
	public MobStartFires(Mob M) {
		// Get RNG
		double New_RNG = new RNG().GetDouble(0, 100.0);

		// Get chance of burning wood per tick
		double Chance_Burn_Wood = AggressiveMobsConfig.ChanceOfZombieLightFire.get();

		// If RNG value falls below chance of burning wood, then
		if (New_RNG < Chance_Burn_Wood) {
			StartFire(M);
		}

	}

	// If zombie is near flammable material, set it alight!
	void StartFire(Mob M) {

		// Get block position that zombie currently occupies
		BlockPos Block_Pos = M.blockPosition();

		// Get current server and level
		LivingEntity Living_Entity = M.getTarget();
		if (Living_Entity != null) {
			if (Living_Entity instanceof ServerPlayer) {

				// Get current server level
				ServerPlayer Server_Player = (ServerPlayer) Living_Entity;
				Level Lvl = Server_Player.level();

				// Has the zombie successfully lit something on fire?
				boolean isDone = false;

				// Check through cubic area around zombie, try to light a block on fire
				for (int x = -1; x <= 1; x++) {
					for (int y = -1; y <= 1; y++) {
						for (int z = -1; z <= 1; z++) {

							if (!isDone) {
								// Get block currently occupying this position.
								BlockPos Curr_Block_Pos = Block_Pos.offset(x, y, z);
								BlockState CurrBlock_State = Lvl.getBlockState(Curr_Block_Pos);
								Block B = CurrBlock_State.getBlock();
								boolean canBurn = CurrBlock_State.isFlammable(Lvl, Curr_Block_Pos, Direction.NORTH);
								if (canBurn && isNotExcluded(B)) {
									LightFire(Server_Player, Lvl, Curr_Block_Pos, CurrBlock_State);
									isDone = true;
								}
							}

						}
					}
				}
			}
		}
	}

	// Is this block free to burn (not on the exclusion list)
	boolean isNotExcluded(Block B) {
		for (int i = 0; i < Excluded_Tags.length; i++) {
			String Curr_Excl = Excluded_Tags[i];
			if (B.getName().toString().contains(Curr_Excl)) {
				return false;
			}
		}

		return true;
	}

	// Light a fire at this position
	void LightFire(ServerPlayer Curr_Tgt, Level Lvl, BlockPos Fire_Pos, BlockState Curr_Blockstate) {
		Lvl.playLocalSound(Fire_Pos.getX(), Fire_Pos.getY(), Fire_Pos.getZ(), SoundEvents.FLINTANDSTEEL_USE,
				SoundSource.BLOCKS, 1.0F, 1.0F, false);
		BlockState Block_State_Fire = BaseFireBlock.getState(Lvl, Fire_Pos);
		Lvl.setBlock(Fire_Pos, Block_State_Fire, 11);
		// Lvl.setBlock(Fire_Pos, Curr_Blockstate.setValue(BlockStateProperties.LIT,
		// Boolean.valueOf(true)), 11);
		//LOGGER.info("Started fire");
	}

}
