package com.birdsprime.aggressivemobs;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DuplicateMob {

    private static final Random RANDOM = new Random();

    private final int SWARM_RADIUS = 2;
    private final ArrayList<BlockPos> usedPositions = new ArrayList<>();

    private final EntityType<?>[] DUPLICATION_WHITELIST = {
        EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.DROWNED, EntityType.ENDERMAN, EntityType.EVOKER,
        EntityType.GHAST, EntityType.HOGLIN, EntityType.HUSK, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE,
        EntityType.PILLAGER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SPIDER, EntityType.RAVAGER,
        EntityType.VEX, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.ZOGLIN,
        EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.STRAY
    };

    public DuplicateMob(Mob mob, int count) {
        if (!isWhitelisted(mob)) return;

        for (int i = 0; i < count; i++) {
            cloneMob(mob);
        }
    }

    private void cloneMob(Mob mob) {
        if (!(mob.level() instanceof ServerLevel level)) return;

        BlockPos basePos = mob.blockPosition();
        BlockPos offsetPos = getOffsetPosition(basePos);

        if (isPositionGood(level, offsetPos)) {
            mob.getType().spawn(
                level,
                new ItemStack(Items.BIRCH_WOOD),
                (Player) null,
                offsetPos,
                MobSpawnType.MOB_SUMMONED,
                true,
                false
            );
            usedPositions.add(offsetPos);
        }
    }

    private boolean isWhitelisted(Mob mob) {
        EntityType<?> type = mob.getType();
        for (EntityType<?> allowed : DUPLICATION_WHITELIST) {
            if (allowed == type) return true;
        }
        return false;
    }

    private BlockPos getOffsetPosition(BlockPos base) {
        int dx = RANDOM.nextInt(SWARM_RADIUS * 2 + 1) - SWARM_RADIUS;
        int dz = RANDOM.nextInt(SWARM_RADIUS * 2 + 1) - SWARM_RADIUS;
        return base.offset(dx, 0, dz);
    }

    private boolean isPositionGood(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir();
    }
}
