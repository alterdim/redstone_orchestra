package com.alternis.redstone_orchestra.block;

import com.alternis.redstone_orchestra.util.Emotion;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.*;

import static com.alternis.redstone_orchestra.RedstoneOrchestra.JAR_BLOCK_ENTITY;

public class JarBlockEntity extends BlockEntity {

    private static final int MAX_TOTAL = 64;   // cap, optional
    private final EnumMap<Emotion, Integer> counts = new EnumMap<>(Emotion.class);
    static final Logger LOGGER = LogUtils.getLogger();

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(JAR_BLOCK_ENTITY.get(), pos, state);
        LOGGER.info("new blockEntity");
    }

    public void onInstrumentPlayed(InstrumentPlayedEvent event) {
        add(Emotion.ANGER, 1);

    }

    /* ---------- querying ---------- */

    public int get(Emotion e)                   { return counts.getOrDefault(e, 0); }
    public int getTotal()                      { return counts.values().stream().mapToInt(Integer::intValue).sum(); }

    public boolean has(Emotion e, int qty)      { return get(e) >= qty; }
    public boolean hasAll(Map<Emotion,Integer> req) {
        return req.entrySet().stream().allMatch(e -> get(e.getKey()) >= e.getValue());
    }

    public Map<Emotion,Integer> getCounts() {
        return Collections.unmodifiableMap(counts);
    }

    /* ---------- mutation ---------- */

    /** @return true if the jar could accept the quantity */
    public boolean add(Emotion e, int qty) {
        if (qty <= 0) return false;
        if (getTotal() + qty > MAX_TOTAL) return false;

        counts.merge(e, qty, Integer::sum);
        onChanged();
        return true;
    }

    /** remove at most <qty> and return how many were removed */
    public int remove(Emotion e, int qty) {
        int current = get(e);
        int removed = Math.min(current, Math.max(qty, 0));
        if (removed > 0) {
            if (removed == current) counts.remove(e);
            else                    counts.put(e, current - removed);
            onChanged();
        }
        return removed;
    }

    /** convenience: remove a whole “recipe” if possible */
    public boolean consume(Map<Emotion,Integer> req) {
        if (!hasAll(req)) return false;
        req.forEach(this::remove);
        return true;
    }

    private void onChanged() {
        setChanged();
        if (level != null && !level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag emotionsTag = new CompoundTag();
        for (Map.Entry<Emotion, Integer> entry : counts.entrySet()) {
            emotionsTag.putInt(entry.getKey().name(), entry.getValue());
        }
        tag.put("Emotions", emotionsTag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        counts.clear(); // just in case
        if (tag.contains("Emotions", Tag.TAG_COMPOUND)) {
            CompoundTag emotionsTag = tag.getCompound("Emotions");
            for (String key : emotionsTag.getAllKeys()) {
                try {
                    Emotion emotion = Emotion.valueOf(key);
                    int count = emotionsTag.getInt(key);
                    counts.put(emotion, count);
                } catch (IllegalArgumentException e) {
                    // Invalid enum constant, ignore or log
                }
            }
        }
    }

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
    public boolean tryPay(EnumMap<Emotion,Integer> price) {

        // ---------- 1) check affordability ---------- //
        for (Map.Entry<Emotion,Integer> need : price.entrySet()) {
            int have = counts.getOrDefault(need.getKey(), 0);
            if (have < need.getValue()) return false;      // cannot afford
        }

        // ---------- 2) subtract / persist ---------- //
        for (Map.Entry<Emotion,Integer> need : price.entrySet()) {
            counts.merge(need.getKey(), -need.getValue(), Integer::sum);
        }
        setChanged();   // mark BE dirty so it saves to NBT

        return true;
    }
}
