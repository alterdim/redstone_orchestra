package com.alternis.redstone_orchestra.data.reward;

import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.data.cost.BlockCost;
import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.alternis.redstone_orchestra.util.CodecHelper;
import com.alternis.redstone_orchestra.util.UtilFunc;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.alternis.redstone_orchestra.block.ModBlocks.AMP_BLOCK;

public record BlockReward(Map<Block, Integer> blocks) implements Reward {

    public static final Codec<BlockReward> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(CodecHelper.BLOCK_CODEC, Codec.INT)
                    .fieldOf("blocks").forGetter(BlockReward::blocks)
    ).apply(i, BlockReward::new));

    @Override
    public String type() {
        return "blocks";
    }

    @Override
    public void grant(NoteSource source) {
        List<BlockPos> amps = source.jar().findBlocksAround(AMP_BLOCK.get());
        List<BlockPos> aboveAmps = amps.stream()
                .map(BlockPos::above)
                .toList();
        if (blocks.values().stream().mapToInt(i -> i).sum() > aboveAmps.size()) {
            return;
        }

        Iterator<BlockPos> it = aboveAmps.iterator();
        for (var entry : blocks.entrySet()) {
            Block block = entry.getKey();
            int count    = entry.getValue();
            for (int i = 0; i < count; i++) {
                if (!it.hasNext()) break;
                BlockPos pos = it.next();
                UtilFunc.setBlockWithEffect(source.serverLevel(), pos, block);
            }
        }
    }

    @Override
    public boolean canGrant(NoteSource source) {
        JarBlockEntity jar = source.jar();
        int amp_count = jar.findBlocksAround(AMP_BLOCK.get()).size();
        int total_blocks = blocks.values().stream().mapToInt(Integer::intValue).sum();
        return amp_count >= total_blocks;
    }

}
