package com.alternis.redstone_orchestra.event;

import com.alternis.redstone_orchestra.block.JarBlockEntity;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.logging.Level;
import java.util.logging.Logger;

@Mod.EventBusSubscriber(modid = "redstone_orchestra")
public class ModEvents {

    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent event) {
        if (event.level().isClientSide()) return;
        if (event.isByEntity())
        {
            InstrumentPlayedEvent.EntityInfo info = (InstrumentPlayedEvent.EntityInfo) event.entityInfo().get();
            var chunk = event.level().getChunkAt(info.entity.blockPosition());
            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "blockEntity discovered");
                if (blockEntity instanceof JarBlockEntity jar) {
                    jar.onInstrumentPlayed(event);
                }
            }
        }


        // Iterate over blocks in the chunk

    }
}
