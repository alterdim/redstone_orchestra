package com.alternis.redstone_orchestra.event;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.data.Song;
import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.init.ModDatapackRegistries;
import com.alternis.redstone_orchestra.util.Emotion;
import com.alternis.redstone_orchestra.util.notesource.NoteSource;
import com.alternis.redstone_orchestra.util.reward.Reward;
import com.cstav.genshinstrument.event.NoteSoundPlayedEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * Listens for Genshin-Instrument notes, matches them against datapack-defined
 * songs, checks / pays emotion cost, and triggers rewards.
 */
@Mod.EventBusSubscriber(modid = RedstoneOrchestra.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InstrumentPatternListener {

    /** How many recent notes we keep per player. */
    private static final int WINDOW = 16;
    private static final Map<String, ArrayDeque<Integer>> NOTE_CACHE = new HashMap<>();

    /** ---------------------------------------------------------------- */
    /** MAIN EVENT HANDLER                                               */
    /** ---------------------------------------------------------------- */
    @SubscribeEvent
    public static void onNote(NoteSoundPlayedEvent ev) {

        /* logical-server only */
        if (ev.level().isClientSide) return;

        NoteSource source = NoteSource.from(ev);
        //System.out.println(source.pos());
        String key = NoteSource.toKey(source);


        /* ---------------- push note into that player's queue ---------- */
        ArrayDeque<Integer> buf = NOTE_CACHE.computeIfAbsent(key,
                k -> new ArrayDeque<>(WINDOW));

        buf.addLast(ev.sound().index);
        while (buf.size() > WINDOW) buf.removeFirst();

        String instrument_name = ev.soundMeta().instrumentId().toString();

        /* ---------------- iterate over all songs ---------------------- */
        Registry<Song> songs = ev.entityInfo().get().entity.level().registryAccess().registryOrThrow(ModDatapackRegistries.SONGS);

        for (Song song : songs) {
            if (!endsWith(buf, song.pattern()) || !song.allowedInstruments().contains(instrument_name)) continue;              // no match
            System.out.println("Trying to grant song");
            JarBlockEntity jar = source.jar();
            if (jar == null) {
                source.sendMessage(ChatFormatting.RED + "No jar block entity found! Source is " + source.type());
                return;
            }



            /* ---------------- pattern + payment succeeded ------------- */
            for (Reward reward : song.rewards()) {
                if (!reward.canGrant(source)) {
                    source.sendMessage(ChatFormatting.RED + "You cannot grant this reward!");
                    return;
                }
            }

            if (!jar.tryPay(toEnumMap(song.cost()))) {
                source.sendMessage(ChatFormatting.RED + "You cannot pay this reward!");
                continue;
            }
            applyReward(song, source);
            //spawnSuccessParticles(player);
            buf.clear();                       // optional: reset after success
            break;                             // one song per keystroke
        }
    }

    /* ================================================================= */
    /* helpers                                                           */
    /* ================================================================= */

    /** true if deque ends with pattern (exact order) */
    private static boolean endsWith(ArrayDeque<Integer> q, List<Integer> pat) {
        if (pat.size() > q.size()) return false;
        Iterator<Integer> itQ  = q.descendingIterator();
        ListIterator<Integer> itP = pat.listIterator(pat.size());
        while (itP.hasPrevious()) {
            if (!Objects.equals(itQ.next(), itP.previous())) return false;
        }
        return true;
    }

    private static void applyReward(Song song, NoteSource source) {
        for (Reward reward : song.rewards()) {
            reward.grant(source);
        }
    }

    /** convert String→Integer map (from JSON) into EnumMap<Emotion,Integer> */
    private static EnumMap<Emotion, Integer> toEnumMap(Map<String,Integer> raw) {
        EnumMap<Emotion,Integer> out = new EnumMap<>(Emotion.class);
        raw.forEach((k,v) -> {
            try { out.put(Emotion.valueOf(k.toUpperCase(Locale.ROOT)), v); }
            catch (IllegalArgumentException ignore) {}
        });
        return out;
    }

    private static void spawnSuccessParticles(ServerPlayer p) {
        ((ServerLevel)p.level()).sendParticles(ParticleTypes.NOTE,
                p.getX(), p.getY() + 2, p.getZ(),
                16, 0.4, 0.4, 0.4, 0.0);
    }
}
