package com.alternis.redstone_orchestra.block.instrument.triangle;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import com.cstav.genshinstrument.networking.packet.instrument.util.InstrumentPacketUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class TriangleBlock extends AbstractInstrumentBlock {
    public TriangleBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected void onInstrumentOpen(ServerPlayer serverPlayer) {
        InstrumentPacketUtil.sendOpenPacket(serverPlayer, new ResourceLocation(RedstoneOrchestra.MODID, "triangle"));
    }

    @Override
    public InstrumentBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TriangleBlockEntityEntity(blockPos, blockState);
    }

}
