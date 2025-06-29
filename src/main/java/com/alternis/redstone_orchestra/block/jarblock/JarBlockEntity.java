package com.alternis.redstone_orchestra.block.jarblock;
import static com.alternis.redstone_orchestra.block.ModBlockEntities.JAR_BLOCK_ENTITY;
import static com.alternis.redstone_orchestra.block.ModBlocks.RECEPTACLE_BLOCK;
import static com.alternis.redstone_orchestra.item.ModItems.ZOMBIE_HEART_ITEM;
import com.alternis.redstone_orchestra.item.HeartItem;
import com.alternis.redstone_orchestra.util.Emotion;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;



public class JarBlockEntity extends BlockEntity {

    private int size = 0;   // cap, optional
    private HeartItem currentHeart; // the heart that is currently being used to pay
    private final EnumMap<Emotion, Integer> counts = new EnumMap<>(Emotion.class);
    static final Logger LOGGER = LogUtils.getLogger();

    public JarBlockEntity(BlockPos pos, BlockState state)
    {
        super(JAR_BLOCK_ENTITY.get(), pos, state);
        LOGGER.info("new blockEntity");
    }

    public void onInstrumentPlayed(InstrumentPlayedEvent event) {
        LOGGER.info("trying to add emotion");
        add(Emotion.ANGER, 1);
        add(Emotion.SADNESS, 1);
        add(Emotion.JOY, 1);

    }

    /* ---------- querying ---------- */

    public int get(Emotion e)                   {
        System.out.println(counts);
        return counts.getOrDefault(e, 0);
    }
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
        if (get(e) >= size) {
            LOGGER.info("Too full ! Size: {}, Adding: {}, Total: {}", size, qty, getTotal() + qty);
            return false;
        }

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
        tag.putInt("size", size);
        tag.putInt("heart", currentHeart != null ? currentHeart.getPower() : 0);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag); // saves counts, size, etc.
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag); // reload counts from tag
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        counts.clear(); // just in case

        currentHeart = tag.getInt("heart") > 0
                ? (HeartItem) ZOMBIE_HEART_ITEM.get()
                : null;
        size = tag.getInt("size");

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

    public int getSize() {
        return size;
    }

    public static JarBlockEntity findJarSameChunk(ServerPlayer player) {

        var chunk = player.level().getChunkAt(player.blockPosition());   // loaded chunk
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            if (be instanceof JarBlockEntity jar) return jar;
        }
        return null;
    }

    public void setCurrentHeart(HeartItem heart) {
        this.currentHeart = heart;
        this.size = heart.getPower();
        LOGGER.info("Current heart set to: {}", heart.getDescriptionId());
        onChanged();
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
        onChanged();   // mark BE dirty so it saves to NBT

        return true;
    }

    public List<BlockPos> findReceptacles() {
        List<BlockPos> receptacles = new ArrayList<>();
        if (level == null) return receptacles;

        // search for up to 8 catalyst blocks in a 3x3x3 area around the jar
        int radius = 2;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = worldPosition.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock().equals(RECEPTACLE_BLOCK.get()) ) {
                        receptacles.add(pos);
                        if (receptacles.size() >= 8) {
                            return receptacles; // found enough receptacles
                        }
                    }
                }
            }
        }
        if (receptacles.isEmpty()) {
            LOGGER.warn("No receptacles found in the vicinity of JarBlockEntity at {}", worldPosition);
        } else {
            LOGGER.info("Found {} receptacles near JarBlockEntity at {}", receptacles.size(), worldPosition);
        }
        return receptacles;
    }
}
