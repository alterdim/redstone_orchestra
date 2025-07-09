package com.alternis.redstone_orchestra;

import com.alternis.redstone_orchestra.block.ModBlockEntities;
import com.alternis.redstone_orchestra.block.ModBlocks;
import com.alternis.redstone_orchestra.init.ModSounds;
import com.alternis.redstone_orchestra.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import static com.alternis.redstone_orchestra.block.ModBlocks.*;
import static com.alternis.redstone_orchestra.item.ModItems.*;

@Mod(RedstoneOrchestra.MODID)
public class RedstoneOrchestra
{
    public static final String MODID = "redstone_orchestra";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("ro_tab", () -> CreativeModeTab.builder()
            .icon(() -> JAR_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                // ITEMS
                output.accept(ZOMBIE_HEART_ITEM.get());
                output.accept(NECKLACE_ITEM.get());
                output.accept(CONDUCTING_BATON.get());
                output.accept(MUSICAL_POWDER.get());

                // ORE
                output.accept(ACCIDENTAL_ORE_SHARP_BLOCK_ITEM.get());
                output.accept(ACCIDENTAL_ORE_FLAT_BLOCK_ITEM.get());

                // INSTRUMENTS
                output.accept(TRIANGLE_BLOCK_ITEM.get());
                output.accept(ELECTRIC_GUITAR_BLOCK_ITEM.get());

                // UTIL BLOCKS
                output.accept(JAR_BLOCK_ITEM.get());
                output.accept(RECEPTACLE_BLOCK_ITEM.get());
                output.accept(CATALYST_BLOCK_ITEM.get());
                output.accept(AMP_BLOCK_ITEM.get());

            }).build());



    public RedstoneOrchestra(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);


        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModSounds.register(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }


    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static Component text(String msg, ChatFormatting colour) {
        return Component.literal(msg).withStyle(colour);
    }


}
