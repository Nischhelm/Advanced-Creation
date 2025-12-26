package com.deadtiger.advcreation.network.message;

import com.deadtiger.advcreation.network.network_utility.ByteBufCustomUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.deadtiger.advcreation.client.event.ClientEventHandler.handleClientLeftClick;

/***
 * message send by the server to the client when the mouse is held informing the client to run the rightclickblock or leftclickblock again
 */
public class MessageTriggerClickEvent extends MessageBase<MessageTriggerClickEvent>
{

    public static enum mouseButton
    {rightMouse, leftMouse}

    public mouseButton button;
    public BlockPos pos;
    public EnumFacing face;

    public MessageTriggerClickEvent()
    {

    }

    public MessageTriggerClickEvent(mouseButton button, BlockPos pos, EnumFacing face)
    {
        this.button = button;
        this.pos = pos;
        this.face = face;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientSide(MessageTriggerClickEvent message, EntityPlayer player)
    {

        if (message != null)
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (message.button == mouseButton.leftMouse)
                handleClientLeftClick(mc.player, message.pos);

        }
        else
            System.out.println("why is the message null?");


    }

    @Override
    public void handleServerSide(MessageTriggerClickEvent message, EntityPlayer player)
    {

    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.button = ByteBufCustomUtils.readCustomEnumValue(buf, mouseButton.class);
        this.pos = ByteBufCustomUtils.readBlockPos(buf);
        this.face = ByteBufCustomUtils.readCustomEnumValue(buf, EnumFacing.class);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufCustomUtils.writeCustomEnumValue(buf, this.button);
        ByteBufCustomUtils.writeBlockPos(buf, this.pos);
        ByteBufCustomUtils.writeCustomEnumValue(buf, this.face);
    }
}
