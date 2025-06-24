package com.alternis.redstone_orchestra.event;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.data.Song;
import com.alternis.redstone_orchestra.block.JarBlockEntity;
import com.alternis.redstone_orchestra.init.ModDatapackRegistries;
import com.alternis.redstone_orchestra.util.Emotion;
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

    /** rolling buffers keyed by player UUID */
    private static final Map<UUID, ArrayDeque<Integer>> NOTE_CACHE = new HashMap<>();

    /** ---------------------------------------------------------------- */
    /** MAIN EVENT HANDLER                                               */
    /** ---------------------------------------------------------------- */
    @SubscribeEvent
    public static void onNote(NoteSoundPlayedEvent ev) {

        /* logical-server only */
        if (ev.level().isClientSide) return;

        /* only handle player-played notes */
        if (!(ev.entityInfo().get().entity instanceof ServerPlayer player)) return;

        /* ---------------- push note into that player's queue ---------- */
        ArrayDeque<Integer> buf = NOTE_CACHE.computeIfAbsent(player.getUUID(),
                id -> new ArrayDeque<>(WINDOW));

        buf.addLast(ev.sound().index);
        while (buf.size() > WINDOW) buf.removeFirst();

        /* ---------------- iterate over all songs ---------------------- */
        Registry<Song> songs = player.level()
                .registryAccess()
                .registryOrThrow(ModDatapackRegistries.SONGS);

        for (Song song : songs) {
            RedstoneOrchestra.LOGGER.error(song.toString());

            if (!endsWith(buf, song.pattern())) continue;              // no match

            JarBlockEntity jar = JarBlockEntity.findJarSameChunk(player);
            if (jar == null) {
                player.sendSystemMessage(RedstoneOrchestra.text(
                        "No emotion jar nearby!", ChatFormatting.GRAY));
                continue;
            }

            if (!jar.tryPay(toEnumMap(song.cost()))) {
                player.sendSystemMessage(RedstoneOrchestra.text(
                        "Not enough emotions!", ChatFormatting.DARK_RED));
                continue;
            }

            /* ---------------- pattern + payment succeeded ------------- */
            applyReward(song, player);
            spawnSuccessParticles(player);
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

    private static void applyReward(Song song, ServerPlayer player) {
        for (Reward reward : song.rewards()) {
            reward.grant(player, player.serverLevel());
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
