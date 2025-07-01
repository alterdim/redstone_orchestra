package com.alternis.redstone_orchestra.item;

import com.alternis.redstone_orchestra.util.Emotion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class HeartItem extends Item {
    private final int power;

    public HeartItem(Properties properties, int power) {
        super(properties.stacksTo(1));
        this.power = power;
    }

    public int getPower() {
        return power;
    }

    // Get all emotions
    public static EnumMap<Emotion, Integer> getAllEmotions(ItemStack stack) {
        EnumMap<Emotion, Integer> emotions = new EnumMap<>(Emotion.class);
        CompoundTag tag = stack.getTagElement("Emotions");
        if (tag != null) {
            for (String key : tag.getAllKeys()) {
                try {
                    Emotion e = Emotion.valueOf(key);
                    emotions.put(e, tag.getInt(key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return emotions;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltip) {
        EnumMap<Emotion, Integer> emotions = getAllEmotions(itemStack);

        if (emotions.isEmpty()) {
            components.add(Component.literal("§7Empty"));
        } else {
            for (Map.Entry<Emotion, Integer> entry : emotions.entrySet()) {
                components.add(Component.literal("§6" + entry.getKey().name() + ": " + entry.getValue()));
            }
        }

        components.add(Component.literal("§8Capacity: " + getPower()));
        super.appendHoverText(itemStack, level, components, tooltip);
    }

    public static int getEmotion(ItemStack stack, Emotion emotion) {
        CompoundTag tag = stack.getTagElement("Emotions");
        return (tag != null) ? tag.getInt(emotion.name()) : 0;
    }

    public static void setEmotion(ItemStack stack, Emotion emotion, int value) {
        CompoundTag tag = stack.getOrCreateTagElement("Emotions");
        tag.putInt(emotion.name(), value);
    }

    public static void addEmotion(ItemStack stack, Emotion emotion, int delta) {
        int current = getEmotion(stack, emotion);
        setEmotion(stack, emotion, current + delta);
    }

    public static void clearEmotions(ItemStack stack) {
        stack.removeTagKey("Emotions");
    }

    public static boolean hasAll(ItemStack stack, Map<Emotion, Integer> required) {
        for (var entry : required.entrySet()) {
            if (getEmotion(stack, entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public static boolean tryConsume(ItemStack stack, Map<Emotion, Integer> required) {
        if (!hasAll(stack, required)) return false;
        for (var entry : required.entrySet()) {
            addEmotion(stack, entry.getKey(), -entry.getValue());
        }
        return true;
    }

    public static int getTotal(ItemStack stack) {
        return getAllEmotions(stack).values().stream().mapToInt(Integer::intValue).sum();
    }
}
