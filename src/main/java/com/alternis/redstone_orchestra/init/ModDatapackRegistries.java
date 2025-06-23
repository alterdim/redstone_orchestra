package com.alternis.redstone_orchestra.init;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.data.Song;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod.EventBusSubscriber(modid = RedstoneOrchestra.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModDatapackRegistries {

    /** songs registry key */
    public static final ResourceKey<net.minecraft.core.Registry<Song>> SONGS =
            ResourceKey.createRegistryKey(new ResourceLocation(RedstoneOrchestra.MODID, "songs"));

    @SubscribeEvent
    public static void newRegistry(DataPackRegistryEvent.NewRegistry e) {
        e.dataPackRegistry(SONGS, Song.CODEC);
    }

}
