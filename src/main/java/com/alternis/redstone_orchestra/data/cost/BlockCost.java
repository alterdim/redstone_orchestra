package com.alternis.redstone_orchestra.data.cost;

import com.alternis.redstone_orchestra.block.ModBlocks;
import com.alternis.redstone_orchestra.util.CodecHelper;
import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.alternis.redstone_orchestra.util.UtilFunc;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record BlockCost(Map<Block, Integer> blocks) implements Cost {

    public static final Codec<BlockCost> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(CodecHelper.BLOCK_CODEC, Codec.INT)
                    .fieldOf("blocks").forGetter(BlockCost::blocks)
    ).apply(i, BlockCost::new));

    @Override
    public String type() {
        return "blocks"; // ✅ FIXED
    }

    private HashMap<Block, Integer> getBlocksAbove(NoteSource source) {
        List<BlockPos> positions = source.jar().findBlocksAround(ModBlocks.RECEPTACLE_BLOCK.get());
        HashMap<Block, Integer> blocks_above = new HashMap<>();
        for (BlockPos pos : positions) {
            Block block = source.serverLevel().getBlockState(pos.above()).getBlock(); // ✅ FIXED
            blocks_above.put(block, blocks_above.getOrDefault(block, 0) + 1);
        }
        return blocks_above;
    }

    @Override
    public boolean canPay(NoteSource source) {
        HashMap<Block, Integer> blocks_above = getBlocksAbove(source);
        for (Map.Entry<Block, Integer> entry : blocks.entrySet()) {
            if (blocks_above.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void pay(NoteSource source) {
        List<BlockPos> positions = source.jar().findBlocksAround(ModBlocks.RECEPTACLE_BLOCK.get());
        Map<Block, Integer> needed = new HashMap<>(blocks); // copy so we can mutate
        for (BlockPos pos : positions) {
            BlockPos above = pos.above();
            Block block = source.serverLevel().getBlockState(above).getBlock();

            if (needed.containsKey(block)) {
                int remaining = needed.get(block);
                if (remaining > 0) {
                    UtilFunc.setBlockWithEffect(source.serverLevel(), above, Blocks.AIR);
                    if (remaining == 1) {
                        needed.remove(block);
                    } else {
                        needed.put(block, remaining - 1);
                    }
                }
            }

            // stop early if we paid everything
            if (needed.isEmpty()) break;
        }
    }
}
