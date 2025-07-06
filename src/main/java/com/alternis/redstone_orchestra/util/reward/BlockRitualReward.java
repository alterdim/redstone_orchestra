package com.alternis.redstone_orchestra.util.reward;

import com.alternis.redstone_orchestra.util.notesource.NoteSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.alternis.redstone_orchestra.block.ModBlocks.RECEPTACLE_BLOCK;

public record BlockRitualReward(List<BlockState> inputs, BlockState output) implements Reward {

    public static final Codec<BlockRitualReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.listOf().fieldOf("inputs").forGetter(BlockRitualReward::inputs),
            BlockState.CODEC.fieldOf("output").forGetter(BlockRitualReward::output)
    ).apply(instance, BlockRitualReward::new));
    @Override
    public String type() {
        return "";
    }

    @Override
    public void grant(NoteSource source) {
        List<BlockPos> receptacles = source.jar().findBlocksAround(RECEPTACLE_BLOCK.get());

    }

    @Override
    public boolean canGrant(NoteSource source) {
        return Reward.super.canGrant(source);
    }
}
