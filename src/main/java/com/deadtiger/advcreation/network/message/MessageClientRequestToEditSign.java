package com.deadtiger.advcreation.network.message;

import com.deadtiger.advcreation.network.network_utility.ByteBufCustomUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;

public class MessageClientRequestToEditSign extends MessageBase<MessageClientRequestToEditSign>
{
    private BlockPos signPosition;

    public MessageClientRequestToEditSign()
    {
    }

    public MessageClientRequestToEditSign(BlockPos posIn)
    {
        this.signPosition = posIn;
    }

    @Override
    public void handleClientSide(MessageClientRequestToEditSign message, EntityPlayer player)
    {

    }

    @Override
    public void handleServerSide(MessageClientRequestToEditSign message, EntityPlayer player)
    {

        if(player.world.getTileEntity(message.signPosition) != null)
            player.openEditSign((TileEntitySign) player.world.getTileEntity(message.signPosition));
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.signPosition = ByteBufCustomUtils.readBlockPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufCustomUtils.writeBlockPos(buf,this.signPosition);
    }
}
