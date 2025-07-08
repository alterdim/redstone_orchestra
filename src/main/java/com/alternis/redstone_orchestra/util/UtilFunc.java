package com.alternis.redstone_orchestra.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

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

    public static void setBlockWithEffect(ServerLevel lvl, BlockPos pos, Block block) {
        SoundType sound = block.getSoundType(block.defaultBlockState(), lvl, pos, null);
        lvl.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS,
                (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        lvl.levelEvent(2001, pos, Block.getId(block.defaultBlockState()));
        lvl.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
    }

}
