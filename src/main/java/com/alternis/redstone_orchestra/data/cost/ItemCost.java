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

public record ItemCost(List<ItemStack> inputs, List<ItemStack> outputs) implements Cost {

    public static final Codec<ItemCost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("inputs").forGetter(ItemCost::inputs),
            ItemStack.CODEC.listOf().fieldOf("outputs").forGetter(ItemCost::outputs)
    ).apply(instance, ItemCost::new));

    @Override
    public String type() {
        return "recipe";
    }

    public boolean canPay(NoteSource source) {
        JarBlockEntity jar = source.jar();
        if (jar == null) return false;
        BlockPos catalyst = jar.findBlocksAround(CATALYST_BLOCK.get()).get(0);
        BlockPos amp = jar.findBlocksAround(AMP_BLOCK.get()).get(0);
        if (catalyst == null || amp == null) {
            Log.warn("No catalyst or amp block found for RecipeReward at {}", jar.getBlockPos());
            return false;
        }
        Container outputContainer = getContainerAt(source.serverLevel(), amp.above());
        ServerLevel level = source.serverLevel();
        Container container = getContainerAt(level, catalyst.above());
        if (container == null || outputContainer == null) {
            Log.warn("No container found at catalyst or amp block for RecipeReward at {}", jar.getBlockPos());
            return false;
        }
        return hasRequiredItems(container, inputs) && hasOutputSpace(outputContainer, outputs);
    }

    @Override
    public void pay(NoteSource source) {
        JarBlockEntity jar = source.jar();
        if (jar == null) return;
        BlockPos catalyst = jar.findBlocksAround(CATALYST_BLOCK.get()).get(0);
        BlockPos amp = jar.findBlocksAround(AMP_BLOCK.get()).get(0);
        if (catalyst == null || amp == null) {
            Log.warn("No catalyst or amp block found for RecipeReward at {}", jar.getBlockPos());
            return;
        }

        ServerLevel level = source.serverLevel();
        Container container = getContainerAt(level, catalyst.above());
        Container outputContainer = getContainerAt(level, amp.above());

        if (!hasRequiredItems(container, inputs)) {
            Log.warn("Not enough items in container for RecipeReward at {}", jar.getBlockPos());
            return;
        }

        consumeItems(container, inputs);
        for (ItemStack output : outputs) {
            if (!output.isEmpty()) {
                insertItem(outputContainer, output.copy());
            }
        }

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

    private void insertItem(Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack slot = container.getItem(i);
            if (slot.isEmpty()) {
                container.setItem(i, stack);
                return;
            } else if (ItemStack.isSameItemSameTags(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
                int space = slot.getMaxStackSize() - slot.getCount();
                int toMove = Math.min(space, stack.getCount());
                slot.grow(toMove);
                stack.shrink(toMove);
                if (stack.isEmpty()) return;
            }
        }
    }

    private boolean hasOutputSpace(Container container, List<ItemStack> outputs) {
        // Clone a working copy of the container to simulate insertion
        ItemStack[] simulated = new ItemStack[container.getContainerSize()];
        for (int i = 0; i < simulated.length; i++) {
            simulated[i] = container.getItem(i).copy();
        }

        for (ItemStack output : outputs) {
            if (output.isEmpty()) continue;

            int remaining = output.getCount();

            // Try to merge output into simulated container
            for (int i = 0; i < simulated.length; i++) {
                ItemStack slot = simulated[i];

                if (slot.isEmpty()) {
                    simulated[i] = output.copy();
                    remaining = 0;
                    break;
                } else if (ItemStack.isSameItemSameTags(slot, output)) {
                    int space = slot.getMaxStackSize() - slot.getCount();
                    int toInsert = Math.min(space, remaining);
                    simulated[i].grow(toInsert);
                    remaining -= toInsert;
                    if (remaining <= 0) break;
                }
            }

            if (remaining > 0) return false; // not enough space
        }

        return true;
    }
}
