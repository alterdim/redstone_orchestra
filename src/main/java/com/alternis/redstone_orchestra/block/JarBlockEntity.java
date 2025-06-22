package com.alternis.redstone_orchestra.block;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import static com.alternis.redstone_orchestra.RedstoneOrchestra.JAR_BLOCK_ENTITY;

public class JarBlockEntity extends BlockEntity {

    private static final Logger LOGGER = LogUtils.getLogger();

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(JAR_BLOCK_ENTITY.get(), pos, state);
        LOGGER.info("new blockEntity");
    }

    public void onInstrumentPlayed(InstrumentPlayedEvent event) {
        LOGGER.info("note !");
    }
}
