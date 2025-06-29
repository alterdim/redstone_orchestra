package com.alternis.redstone_orchestra.event;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.event.NoteSoundPlayedEvent;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.NoteBlockEvent;

public class OrchestraNoteEvent extends InstrumentPlayedEvent<NoteSound> {
    public OrchestraNoteEvent(Level level, NoteSound sound, NoteSoundMetadata soundMeta) {
        super(level, sound, soundMeta);
    }
}
