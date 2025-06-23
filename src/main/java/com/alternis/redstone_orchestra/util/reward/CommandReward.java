package com.alternis.redstone_orchestra.util.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public record CommandReward(List<String> commands) implements Reward {

    public static final Codec<CommandReward> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.STRING.listOf().fieldOf("commands").forGetter(CommandReward::commands)
            ).apply(inst, CommandReward::new));

    @Override public String type() { return "command"; }

    @Override public void grant(ServerPlayer p, ServerLevel lvl) {
        var src = lvl.getServer().createCommandSourceStack();   // silent/null source
        commands.forEach(c -> lvl.getServer().getCommands().performPrefixedCommand(src, c));
    }
}
