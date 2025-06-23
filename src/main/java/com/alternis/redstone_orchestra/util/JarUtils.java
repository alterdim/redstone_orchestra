package com.alternis.redstone_orchestra.util;

import com.alternis.redstone_orchestra.block.JarBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.EnumMap;
import java.util.Map;

/**
 * Convenience helpers for interacting with emotion jars.
 */
public final class JarUtils {

    private JarUtils() {}  // static‐only

    /* ------------------------------------------------------------------
     *  Find the FIRST JarBlockEntity in the same chunk as the player.
     *  Returns null if none are present / loaded.
     * ------------------------------------------------------------------ */
    public static JarBlockEntity findJarSameChunk(ServerPlayer player) {

        var chunk = player.level().getChunkAt(player.blockPosition());   // loaded chunk
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            if (be instanceof JarBlockEntity jar) return jar;
        }
        return null;
    }

    /* ------------------------------------------------------------------
     * Attempt to pay the requested emotion cost from the given jar.
     * ► Returns TRUE and subtracts the counts atomically if affordable.
     * ► Returns FALSE and leaves the jar unchanged if any emotion short.
     * ------------------------------------------------------------------ */
    public static boolean tryPay(JarBlockEntity jar,
                                 EnumMap<Emotion,Integer> price) {

        // ---------- 1) check affordability ---------- //
        for (Map.Entry<Emotion,Integer> need : price.entrySet()) {
            int have = jar.getCounts().getOrDefault(need.getKey(), 0);
            if (have < need.getValue()) return false;      // cannot afford
        }

        // ---------- 2) subtract / persist ---------- //
        for (Map.Entry<Emotion,Integer> need : price.entrySet()) {
            jar.getCounts().merge(need.getKey(), -need.getValue(), Integer::sum);
        }
        jar.setChanged();   // mark BE dirty so it saves to NBT

        return true;
    }
}
