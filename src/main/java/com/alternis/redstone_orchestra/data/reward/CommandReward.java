package com.alternis.redstone_orchestra.data.reward;

import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record CommandReward(List<String> commands) implements Reward {

    public static final Codec<CommandReward> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.STRING.listOf().fieldOf("commands").forGetter(CommandReward::commands)
            ).apply(inst, CommandReward::new));

    @Override public String type() { return "command"; }



    @Override public void grant(NoteSource source) {
        var pos = source.pos();
        var level = source.serverLevel();
        var server = level.getServer();
        var src = new CommandSourceStack(
                CommandSource.NULL,                     // no entity attached
                Vec3.atLowerCornerOf(source.pos() != null ? source.pos() : BlockPos.ZERO),
                Vec2.ZERO,                              // no rotation
                level,
                4,                                      // permission level 4 (OP-level)
                "@RedstoneOrchestra",                   // name
                Component.literal("@RedstoneOrchestra"),// display name
                server,                                 // server
                null                                    // entity
        );

        commands.forEach(c -> level.getServer().getCommands().performPrefixedCommand(src, c));
    }
}
