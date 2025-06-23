package com.alternis.redstone_orchestra.util.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jline.utils.Log;

import java.util.List;

public record SpecialEffectReward(List<String> effects) implements Reward {

    public static final Codec<SpecialEffectReward> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.listOf().fieldOf("effects").forGetter(SpecialEffectReward::effects)
    ).apply(i, SpecialEffectReward::new));

    @Override public String type() { return "special_effect"; }

    @Override public void grant(ServerPlayer p, ServerLevel lvl) {
        for (String effect : effects())
        {
            Log.debug(effect);
        }
    }
}