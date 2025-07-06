package com.alternis.redstone_orchestra.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.List;

public class UtilFunc {

    public static boolean canFindBlocksAbove(List<BlockPos> receptacles, HashSet<Block> blocks, Level level)
    {
        HashSet<Block> foundBlocks = new HashSet<>();
        for (BlockPos pos : receptacles) {

            Block block = level.getBlockState(pos.above()).getBlock();
            foundBlocks.add(block);

        }
        return foundBlocks.containsAll(blocks);
    }
}
