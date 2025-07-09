package com.alternis.redstone_orchestra.data.reward;

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

public record ItemReward(List<ItemStack> items) implements Reward {

    public static final Codec<ItemReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(ItemReward::items)
    ).apply(instance, ItemReward::new));

    @Override
    public String type() {
        return "items";
    }

    @Override
    public boolean canGrant(NoteSource source) {
        JarBlockEntity jar = source.jar();
        BlockPos amp = jar.findBlocksAround(AMP_BLOCK.get()).get(0);
        if (amp == null) {
            Log.warn("No catalyst or amp block found for RecipeReward at {}", jar.getBlockPos());
            return false;
        }
        Container outputContainer = getContainerAt(source.serverLevel(), amp.above());
        if (outputContainer == null) {
            Log.warn("No output container found at amp block for RecipeReward at {}", jar.getBlockPos());
            return false;
        }
        return hasOutputSpace(outputContainer, items);
    }

    @Override
    public void grant(NoteSource source) {
        JarBlockEntity jar = source.jar();
        BlockPos amp = jar.findBlocksAround(AMP_BLOCK.get()).get(0);
        ServerLevel level = source.serverLevel();
        Container outputContainer = getContainerAt(level, amp.above());
        for (ItemStack output : items) {
            if (!output.isEmpty()) {
                insertItem(outputContainer, output.copy());
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
