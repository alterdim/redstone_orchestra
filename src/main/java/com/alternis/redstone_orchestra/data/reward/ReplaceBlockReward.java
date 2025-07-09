package com.alternis.redstone_orchestra.data.reward;

import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.alternis.redstone_orchestra.util.CodecHelper;
import com.alternis.redstone_orchestra.util.UtilFunc;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

/**
 * Replaces all block1 in block1:block2 pairs into block2
 * @param blockMap block to replace with another block
 * @param range the range around the source block to replace blocks
 */
public record ReplaceBlockReward(Map<Block, Block> blockMap, int range) implements Reward {

    public static final Codec<ReplaceBlockReward> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(CodecHelper.BLOCK_CODEC, CodecHelper.BLOCK_CODEC).fieldOf("blockMap").forGetter(ReplaceBlockReward::blockMap),
            Codec.INT.optionalFieldOf("range", 3).forGetter(ReplaceBlockReward::range)
    ).apply(i, ReplaceBlockReward::new));

    @Override
    public String type() {
        return "replace_block";
    }

    @Override
    public void grant(NoteSource source) {
        for (Block toReplace : blockMap.keySet()) {
            Block replacement = blockMap.get(toReplace);
            if (replacement == null) continue;

            List<BlockPos> blocks = source.jar().findBlocksAround(toReplace, 999, range);
            for (BlockPos blockPos : blocks) {
                if (source.serverLevel().getBlockState(blockPos).is(toReplace)) {
                    UtilFunc.setBlockWithEffect(source.serverLevel(), blockPos, replacement);
                }
            }
        }
    }
}
