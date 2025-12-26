package com.deadtiger.advcreation.network.message;

import com.deadtiger.advcreation.logging.LoggingServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.nio.charset.Charset;

/***
 * message send by the server to the client when the mouse is held informing the client to run the rightclickblock or leftclickblock again
 */
public class MessageLogToServer extends MessageBase<MessageLogToServer>
{

    public String playerName;
    public int mainMode;
    public int toolname;
    public int mouseButton;
    public int toolStage;
    public boolean guiOverlayClick;
    public String comment;

    public MessageLogToServer()
    {

    }

    public MessageLogToServer(String playerName, int mainMode, int toolname, int mouseButton, int toolStage, boolean guiOverlayClick, String comment)
    {
        this.playerName = playerName;
        this.mainMode = mainMode;
        this.toolname = toolname;
        this.mouseButton = mouseButton;
        this.toolStage = toolStage;
        this.guiOverlayClick = guiOverlayClick;
        this.comment = comment;
    }

    @Override
    public void handleClientSide(MessageLogToServer message, EntityPlayer player)
    {

    }

    @Override
    @SideOnly(Side.SERVER)
    public void handleServerSide(MessageLogToServer message, EntityPlayer player)
    {
        if(message != null)
        {
            LoggingServer.logMouseClick(message.mainMode,message.toolname,message.mouseButton,message.toolStage,message.guiOverlayClick,message.comment,message.playerName);
        }
        else
            System.out.println("why is the message null?");
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int playerNameLength = buf.readShort();
        this.playerName = (String) buf.readCharSequence(playerNameLength, Charset.defaultCharset());
        this.mainMode = buf.readShort();
        this.toolname = buf.readShort();
        this.mouseButton = buf.readShort();
        this.toolStage = buf.readShort();
        this.guiOverlayClick = buf.readBoolean();
        int commentLength = buf.readShort();
        this.comment = (String) buf.readCharSequence(commentLength, Charset.defaultCharset());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeShort(this.playerName.length());
        buf.writeCharSequence(this.playerName, Charset.defaultCharset());
        buf.writeShort(this.mainMode);
        buf.writeShort(this.toolname);
        buf.writeShort(this.mouseButton);
        buf.writeShort(this.toolStage);
        buf.writeBoolean(this.guiOverlayClick);
        buf.writeShort(this.comment.length());
        buf.writeCharSequence(this.comment, Charset.defaultCharset());
    }
}
