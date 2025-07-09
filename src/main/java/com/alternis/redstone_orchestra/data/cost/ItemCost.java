package com.alternis.redstone_orchestra.data.cost;

import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jline.utils.Log;

import java.util.List;

import static com.alternis.redstone_orchestra.block.ModBlocks.AMP_BLOCK;
import static com.alternis.redstone_orchestra.block.ModBlocks.CATALYST_BLOCK;
import static net.minecraft.world.level.block.entity.HopperBlockEntity.getContainerAt;

public record ItemCost(List<ItemStack> items) implements Cost {

    public static final Codec<ItemCost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(ItemCost::items)
    ).apply(instance, ItemCost::new));

    @Override
    public String type() {
        return "items";
    }

    public boolean canPay(NoteSource source) {
        JarBlockEntity jar = source.jar();
        List<BlockPos> catalysts = jar.findBlocksAround(CATALYST_BLOCK.get());
        if (catalysts.isEmpty()) {
            Log.warn("No catalyst block found for ItemCost at {}", jar.getBlockPos());
            return false;
        }
        BlockPos catalyst = catalysts.get(0);
        ServerLevel level = source.serverLevel();
        Container container = getContainerAt(level, catalyst.above());
        if (container == null) {
            Log.warn("No container found at catalyst or amp block for RecipeReward at {}", jar.getBlockPos());
            return false;
        }
        return hasRequiredItems(container, items);
    }

    @Override
    public void pay(NoteSource source) {
        JarBlockEntity jar = source.jar();
        BlockPos catalyst = jar.findBlocksAround(CATALYST_BLOCK.get()).get(0);
        if (catalyst == null) {
            Log.warn("No catalyst block found for RecipeReward at {}", jar.getBlockPos());
            return;
        }

        ServerLevel level = source.serverLevel();
        Container container = getContainerAt(level, catalyst.above());
        if (!hasRequiredItems(container, items)) {
            Log.warn("Not enough items in container for RecipeReward at {}", jar.getBlockPos());
            return;
        }

        consumeItems(container, items);
    }

    private boolean hasRequiredItems(Container container, List<ItemStack> required) {
        for (ItemStack need : required) {
            int needed = need.getCount();
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack slot = container.getItem(i);
                if (ItemStack.isSameItemSameTags(slot, need)) {
                    needed -= slot.getCount();
                    if (needed <= 0) break;
                }
            }
            if (needed > 0) return false;
        }
        return true;
    }

    private void consumeItems(Container container, List<ItemStack> toConsume) {
        for (ItemStack need : toConsume) {
            int remaining = need.getCount();
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack slot = container.getItem(i);
                if (ItemStack.isSameItemSameTags(slot, need)) {
                    int take = Math.min(remaining, slot.getCount());
                    slot.shrink(take);
                    remaining -= take;
                    if (slot.isEmpty()) container.setItem(i, ItemStack.EMPTY);
                    if (remaining <= 0) break;
                }
            }
        }
    }


}
