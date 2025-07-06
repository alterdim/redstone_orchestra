package com.alternis.redstone_orchestra.block.jarblock;

import com.alternis.redstone_orchestra.item.HeartItem;
import com.alternis.redstone_orchestra.util.Emotion;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;

import static com.alternis.redstone_orchestra.block.ModBlockEntities.JAR_BLOCK_ENTITY;

public class JarBlockEntity extends BlockEntity {

    private static final Logger LOGGER = LogUtils.getLogger();
    private ItemStack heartStack = ItemStack.EMPTY;
    private int size = 0;

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(JAR_BLOCK_ENTITY.get(), pos, state);
        LOGGER.info("JarBlockEntity initialized at {}", pos);
    }

    public void onInstrumentPlayed(InstrumentPlayedEvent event) {
        add(Emotion.ANGER, 1);
        add(Emotion.SADNESS, 1);
        add(Emotion.JOY, 1);
    }

    public boolean add(Emotion emotion, int qty) {
        if (heartStack.isEmpty() || !(heartStack.getItem() instanceof HeartItem heart)) return false;

        int currentTotal = HeartItem.getTotal(heartStack);
        if (currentTotal + qty > size) {
            //LOGGER.info("Too full! Cap: {}, Attempting to add {}, Current Total: {}", size, qty, currentTotal);
            return false;
        }

        HeartItem.addEmotion(heartStack, emotion, qty);
        onChanged();
        return true;
    }

    public int get(Emotion emotion) {
        return heartStack.isEmpty() ? 0 : HeartItem.getEmotion(heartStack, emotion);
    }

    public EnumMap<Emotion, Integer> getCounts() {
        return heartStack.isEmpty() ? new EnumMap<>(Emotion.class) : HeartItem.getAllEmotions(heartStack);
    }

    public int remove(Emotion emotion, int qty) {
        if (heartStack.isEmpty()) return 0;
        int current = HeartItem.getEmotion(heartStack, emotion);
        int removed = Math.min(current, Math.max(0, qty));
        if (removed > 0) {
            HeartItem.addEmotion(heartStack, emotion, -removed);
            onChanged();
        }
        return removed;
    }

    public boolean consume(Map<Emotion, Integer> required) {
        return !heartStack.isEmpty() && HeartItem.tryConsume(heartStack, required);
    }

    public boolean tryPay(EnumMap<Emotion, Integer> cost) {
        if (heartStack.isEmpty()) return false;
        if (!HeartItem.hasAll(heartStack, cost)) return false;
        HeartItem.tryConsume(heartStack, cost);
        onChanged();
        return true;
    }

    public void setCurrentHeart(ItemStack stack) {
        if (stack.isEmpty()) {
            this.heartStack = ItemStack.EMPTY;
            this.size = 0;
        } else if (stack.getItem() instanceof HeartItem heart) {
            this.heartStack = stack.copy();
            this.size = heart.getPower();
        }
        onChanged();
    }

    public ItemStack getHeartStack() {
        return heartStack;
    }

    public int getSize() {
        return size;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!heartStack.isEmpty()) {
            tag.put("HeartStack", heartStack.save(new CompoundTag()));
        }
        tag.putInt("size", size);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        heartStack = tag.contains("HeartStack") ? ItemStack.of(tag.getCompound("HeartStack")) : ItemStack.EMPTY;
        if (heartStack.getItem() instanceof HeartItem heart) {
            size = heart.getPower();
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public List<BlockPos> findBlocksAround(Block block, int maxCount, int range) {
        List<BlockPos> result = new ArrayList<>();
        if (level == null) return result;

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    BlockPos pos = worldPosition.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock().equals(block)) {
                        result.add(pos);
                        if (result.size() >= maxCount) return result;
                    }
                }
            }
        }

        return result;
    }

    public List<BlockPos> findBlocksAround(Block block) {
        return findBlocksAround(block, 8, 2);
    }

    /*
    @Deprecated
    public static JarBlockEntity findJarSameChunk(ServerPlayer player) {
        var chunk = player.level().getChunkAt(player.blockPosition());
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            if (be instanceof JarBlockEntity jar) return jar;
        }
        return null;
    }
    */

    private void onChanged() {
        setChanged(); // marks the BE as dirty for saving
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

}
