package com.alternis.redstone_orchestra.init;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.registrar.NoteSoundRegistrar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RedstoneOrchestra.MODID);
    public static void register(final IEventBus bus) {
        SOUNDS.register(bus);
    }

    public static final NoteSound[]
            TRIANGLE = nsr(loc("triangle")).registerGrid(),
            ELECTRIC_GUITAR = nsr(loc("electric_guitar")).registerGrid();

    private static NoteSoundRegistrar nsr(ResourceLocation instrumentId) {
        return new NoteSoundRegistrar(ModSounds.SOUNDS, instrumentId);
    }

    private static ResourceLocation loc(final String id) {
        return new ResourceLocation(RedstoneOrchestra.MODID, id);
    }

}