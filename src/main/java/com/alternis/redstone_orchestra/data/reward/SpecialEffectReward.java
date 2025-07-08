package com.alternis.redstone_orchestra.data.reward;

import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jline.utils.Log;

import java.util.List;

import static com.alternis.redstone_orchestra.block.ModBlocks.RECEPTACLE_BLOCK;
import static com.alternis.redstone_orchestra.util.UtilFunc.setBlockWithEffect;
import static net.minecraft.world.level.block.Blocks.*;

public record SpecialEffectReward(List<String> effects) implements Reward {

    public static final Codec<SpecialEffectReward> CODEC =
            RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.listOf().fieldOf("effects").forGetter(SpecialEffectReward::effects)
    ).apply(i, SpecialEffectReward::new));

    @Override public String type() { return "special_effect"; }

    @Override
    public void grant(NoteSource source) {
        for (String effect : effects())
        {
            switch (effect) {
                case "ore_column" -> stoneColumn(source.jar(), source.serverLevel());
                default -> Log.warn("Unknown special effect: " + effect);
            }
        }
    }

    // TIER 4
    private void stoneColumn(JarBlockEntity jar, ServerLevel lvl) {

        if (jar != null) {
            List<BlockPos> receptacles = jar.findBlocksAround(RECEPTACLE_BLOCK.get(), 3, 2);
            if (receptacles.isEmpty()) {}
            else  {
                for (BlockPos receptacle : receptacles) {
                    int height = lvl.random.nextInt(3) + 3; // between 3 and 5 blocks high
                    List<Block> possibleRewards = List.of(
                            IRON_ORE,
                            REDSTONE_ORE,
                            COPPER_ORE,
                            COAL_ORE
                    );
                    for (int i = 1; i <= height; i++) { // 5 blocks high
                        BlockPos pos = receptacle.above(i);
                        if (lvl.getBlockState(pos).isAir()) {
                            setBlockWithEffect(lvl, pos, possibleRewards.get(lvl.random.nextInt(possibleRewards.size())));
                        }
                    }
                }
            }
        } else {
            Log.warn("No JarBlockEntity found in the same chunk as the player.");
        }
    }

}