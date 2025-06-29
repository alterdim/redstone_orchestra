package com.alternis.redstone_orchestra.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.alternis.redstone_orchestra.RedstoneOrchestra.MODID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> ZOMBIE_HEART_ITEM = ITEMS.register("zombie_heart", () -> new HeartItem(new Item.Properties(), 5));

    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }

}
