package com.alternis.redstone_orchestra.client;
import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.client.gui.TriangleScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.InstrumentScreenRegistry;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.util.CommonUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = RedstoneOrchestra.MODID)
public class ClientInitializer {


    private static final Map<ResourceLocation, Supplier<? extends InstrumentScreen>> INSTRUMENTS = Map.of(
            TriangleScreen.INSTRUMENT_ID, TriangleScreen::new
    );

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        InstrumentScreenRegistry.register(INSTRUMENTS);
    }

}