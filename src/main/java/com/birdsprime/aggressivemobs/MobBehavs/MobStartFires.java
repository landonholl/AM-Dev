package com.birdsprime.aggressivemobs.MobBehavs;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.RNG;
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

public final class MobStartFires {

    private static final String[] EXCLUDED_TAGS = {
        "_sign", "_bed", "pressure_plate", "_button", "sugar_cane", "juke", "trapdoor",
        "enchanting", "brewing", "potted_", "skull", "_head", "chest", "dropper", "prismarine",
        "book", "grass", "snow", "flower", "sapling", "bush", "stem", "dragon", "carrots",
        "potatoes", "carpet", "banner", "structure", "compost", "piston", "log"
    };

    private MobStartFires() {
        // Prevent instantiation
    }

    public static void attemptStartFire(Mob mob) {
        double rng = RNG.GetDouble(0, 100.0);
        double chance = AggressiveMobsConfig.ChanceOfZombieLightFire.get();

        if (rng < chance) {
            startFire(mob);
        }
    }

    private static void startFire(Mob mob) {
        BlockPos mobPos = mob.blockPosition();
        LivingEntity target = mob.getTarget();

        if (target instanceof ServerPlayer player) {
            Level level = player.level();
            boolean fireStarted = false;

            for (int x = -1; x <= 1 && !fireStarted; x++) {
                for (int y = -1; y <= 1 && !fireStarted; y++) {
                    for (int z = -1; z <= 1 && !fireStarted; z++) {
                        BlockPos checkPos = mobPos.offset(x, y, z);
                        BlockState state = level.getBlockState(checkPos);
                        Block block = state.getBlock();

                        if (state.isFlammable(level, checkPos, Direction.NORTH) && isNotExcluded(block)) {
                            lightFire(level, checkPos);
                            fireStarted = true;
                        }
                    }
                }
            }
        }
    }

    private static boolean isNotExcluded(Block block) {
        String blockName = block.getName().toString();
        for (String tag : EXCLUDED_TAGS) {
            if (blockName.contains(tag)) {
                return false;
            }
        }
        return true;
    }

    private static void lightFire(Level level, BlockPos pos) {
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        BlockState fireState = BaseFireBlock.getState(level, pos);
        level.setBlock(pos, fireState, 11);
    }
}
