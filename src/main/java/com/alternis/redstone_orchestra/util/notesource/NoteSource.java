package com.alternis.redstone_orchestra.util.notesource;

import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.cstav.genshinstrument.event.NoteSoundPlayedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.alternis.redstone_orchestra.RedstoneOrchestra.LOGGER;

public sealed interface NoteSource permits NoteSource.PlayerSource, NoteSource.BlockSource, NoteSource.ItemSource {

    @Nullable
    BlockPos pos();

    @Nullable
    ServerLevel serverLevel();

    @Nullable
    JarBlockEntity jar();

    record PlayerSource(UUID uuid) implements NoteSource {
        @Override public @Nullable BlockPos pos() {
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
            return (player != null) ? player.blockPosition() : null;
        }

        @Override public @Nullable ServerLevel serverLevel() {
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
            return (player != null) ? player.serverLevel(): null;
        }

        @Override public @Nullable JarBlockEntity jar() {
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
            return JarBlockEntity.findJarSameChunk(player);
        }
    }
    record BlockSource(BlockPos pos, ServerLevel level) implements NoteSource {
        @Override public BlockPos pos() {
            return pos;
        }

        @Override public @Nullable ServerLevel serverLevel() {
            return level;
        }

        @Override public @Nullable JarBlockEntity jar() {
            return level.getBlockEntity(pos) instanceof JarBlockEntity jar ? jar : null;
        }
    }
    record ItemSource(UUID uuid) implements NoteSource {
        @Override
        public @org.jetbrains.annotations.Nullable BlockPos pos() {
            LOGGER.error("[FATAL] THIS IS VERY BAD. THIS SHOULD NEVER HAPPEN.");
            return null;
        }

        @Override
        public @org.jetbrains.annotations.Nullable ServerLevel serverLevel() {
            LOGGER.error("[FATAL] THIS IS VERY BAD. THIS SHOULD NEVER HAPPEN.");
            return null;
        }

        @Override
        public @org.jetbrains.annotations.Nullable JarBlockEntity jar() {
            LOGGER.error("[FATAL] THIS IS VERY BAD. THIS SHOULD NEVER HAPPEN.");
            return null;
        }
    }



    static NoteSource from(NoteSoundPlayedEvent ev) {
        var info = ev.entityInfo().get();

        if (info.entity instanceof ServerPlayer player) {
            return new PlayerSource(player.getUUID());
        } else {
            return new BlockSource(info.entity.blockPosition(), (ServerLevel) ev.level());
        }
    }

    // optional: for Map keys
    static String toKey(NoteSource source) {
        if (source instanceof NoteSource.PlayerSource p) {
            return "player:" + p.uuid();
        } else if (source instanceof NoteSource.BlockSource b) {
            return "block:" + b.level().dimension().location() + "/" + b.pos();
        } else if (source instanceof NoteSource.ItemSource i) {
            return "item:" + i.uuid();
        } else {
            return "unknown";
        }
    }
}
