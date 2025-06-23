package com.alternis.redstone_orchestra.event;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.block.JarBlockEntity;
import com.alternis.redstone_orchestra.init.ModDatapackRegistries;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.event.NoteSoundPlayedEvent;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.logging.Level;
import java.util.logging.Logger;

@Mod.EventBusSubscriber(modid = "redstone_orchestra")
public class ModEvents {

    @SubscribeEvent
    public static void onInstrumentPlayed(final NoteSoundPlayedEvent event) {
        if (event.level().isClientSide()) return;
        if (event.isByEntity())
        {
            InstrumentPlayedEvent.EntityInfo info = (InstrumentPlayedEvent.EntityInfo) event.entityInfo().get();
            NoteSound note = event.sound();
            Logger.getAnonymousLogger().log(Level.INFO, "InstrumentPlayedEvent: InstrumentPlayedEvent.NoteIdentifier: {0}", note.index);

            var chunk = event.level().getChunkAt(info.entity.blockPosition());
            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof JarBlockEntity jar) {
                    jar.onInstrumentPlayed(event);
                    break;
                }
            }
        }


        // Iterate over blocks in the chunk

    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent e) {
        var reg = e.getServer().registryAccess().registryOrThrow(ModDatapackRegistries.SONGS);
        RedstoneOrchestra.LOGGER.info(">> Songs registered: {}", reg.size());
    }
}
