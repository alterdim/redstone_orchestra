package com.alternis.redstone_orchestra.util.notesource;

import com.alternis.redstone_orchestra.block.instrument.LinkableInstrumentBlockEntity;
import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.item.NecklaceItem;
import com.cstav.genshinstrument.event.NoteSoundPlayedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

import static com.alternis.redstone_orchestra.RedstoneOrchestra.LOGGER;

public sealed interface NoteSource permits NoteSource.PlayerSource, NoteSource.BlockSource, NoteSource.ItemSource {

    @Nullable
    BlockPos pos();

    @Nullable
    default String type() {
        return this.getClass().getSimpleName();
    }

    @Nullable
    ServerLevel serverLevel();

    @Nullable
    JarBlockEntity jar();

    @Nullable
    void sendMessage(String message);

    record PlayerSource(UUID uuid) implements NoteSource {
        @Override
        public @Nullable BlockPos pos() {
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
            return (player != null) ? player.blockPosition() : null;
        }

        @Override
        public @Nullable ServerLevel serverLevel() {
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
            return (player != null) ? player.serverLevel() : null;
        }

        @Override
        public @Nullable JarBlockEntity jar() {

            // 1. Resolve the player from UUID
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer()
                    .getPlayerList()
                    .getPlayer(uuid);
            if (player == null) return null;

            // 2. Get the player’s Curios inventory
            Optional<ICuriosItemHandler> optHandler = CuriosApi.getCuriosInventory(player).resolve();
            if (optHandler.isEmpty()) return null;
            ICuriosItemHandler handler = optHandler.get();   // stable in 1.20.1 :contentReference[oaicite:0]{index=0}

            // 3. Locate the first necklace curio
            ItemStack necklaceStack = handler.findFirstCurio(
                    stack -> stack.getItem() instanceof NecklaceItem       // your class
            ).map(SlotResult::stack).orElse(ItemStack.EMPTY);

            if (necklaceStack.isEmpty()) return null;        // player isn’t wearing a necklace

            // 4. Read bound-jar data from the necklace
            BlockPos jarPos = NecklaceItem.getBoundJarPos(necklaceStack);
            ResourceKey<Level> jarDimKey = NecklaceItem.getBoundDimension(necklaceStack);
            if (jarPos == null || jarDimKey == null) return null;   // necklace not bound

            // 5. Fetch the jar’s ServerLevel and block entity
            ServerLevel jarLevel = player.server.getLevel(jarDimKey);
            if (jarLevel == null) return null;               // dimension not loaded / invalid

            return jarLevel.getBlockEntity(jarPos) instanceof JarBlockEntity jar ? jar : null;
        }

        @Nullable @Override  public void sendMessage(String message) {
            LOGGER.warn(message); //TODO
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
            LinkableInstrumentBlockEntity linkable = level.getBlockEntity(pos) instanceof LinkableInstrumentBlockEntity ?
                    (LinkableInstrumentBlockEntity) level.getBlockEntity(pos) : null;
            if (linkable == null) {
                LOGGER.error("No instrument bloc found, this SHOULD not happen.");
                return null;
            }
            return linkable.getLinkedJar();
        }


        @Override public  @Nullable void sendMessage(String message) {
            LOGGER.error(message); //TODO
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

        @org.jetbrains.annotations.Nullable
        @Override
        public void sendMessage(String message) {
            LOGGER.error(message);
        }
    }



    static NoteSource from(NoteSoundPlayedEvent ev) {
        var info = ev.entityInfo().get();

        if (info.entity instanceof ServerPlayer player && info.isItemInstrument()) {
            return new PlayerSource(player.getUUID());
        } else if (info.isBlockInstrument()) {
            return new BlockSource(ev.soundMeta().pos(), (ServerLevel) ev.level());
        } else throw new RuntimeException("Problem catching a note event."); // fallback to item source if not player or block
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
