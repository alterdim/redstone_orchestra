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
        ArrayList<BlockPos> above_amps = new ArrayList<>(amps.stream()
                .map(BlockPos::above)
                .toList());
        for (Map.Entry<Block, Integer> entry : blocks.entrySet()) {
            Block block = entry.getKey();
            UtilFunc.setBlockWithEffect(source.serverLevel(), above_amps.get(0), block);
            above_amps.remove(0);
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
