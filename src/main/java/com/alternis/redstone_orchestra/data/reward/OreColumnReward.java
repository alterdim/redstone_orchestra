package com.alternis.redstone_orchestra.data.reward;
import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.List;

import static com.alternis.redstone_orchestra.block.ModBlocks.RECEPTACLE_BLOCK;
import static com.alternis.redstone_orchestra.util.UtilFunc.setBlockWithEffect;

public record OreColumnReward(List<ResourceLocation> blockIds, int minHeight, int maxHeight) implements Reward {

    public static final Codec<OreColumnReward> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.listOf().fieldOf("blocks").forGetter(OreColumnReward::blockIds),
            Codec.INT.optionalFieldOf("min_height", 3).forGetter(OreColumnReward::minHeight),
            Codec.INT.optionalFieldOf("max_height", 5).forGetter(OreColumnReward::maxHeight)
    ).apply(i, OreColumnReward::new));

    @Override public String type() { return "ore_column"; }

    @Override
    public void grant(NoteSource source) {
        JarBlockEntity jar = source.jar();
        ServerLevel lvl = source.serverLevel();

        if (jar == null) return;

        List<Block> blocks = blockIds.stream()
                .map(BuiltInRegistries.BLOCK::get)
                .filter(b -> b != null)
                .toList();

        if (blocks.isEmpty()) return;

        List<BlockPos> receptacles = jar.findBlocksAround(RECEPTACLE_BLOCK.get());
        for (BlockPos receptacle : receptacles) {
            int height = lvl.random.nextInt(maxHeight - minHeight + 1) + minHeight;

            for (int i = 1; i <= height; i++) {
                BlockPos pos = receptacle.above(i);
                if (lvl.getBlockState(pos).isAir()) {
                    Block chosen = blocks.get(lvl.random.nextInt(blocks.size()));
                    setBlockWithEffect(lvl, pos, chosen);
                }
            }
        }
    }


}
