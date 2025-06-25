package com.alternis.redstone_orchestra.item;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class HeartItem extends Item {

    private int power = 0;
    public HeartItem(Properties p_41383_, int power) {
        super(p_41383_);
        this.power = power;

    }

    public int getPower() {
        return power;
    }
}
