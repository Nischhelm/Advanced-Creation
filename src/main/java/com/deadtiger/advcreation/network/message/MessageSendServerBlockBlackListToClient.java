package com.deadtiger.advcreation.network.message;

import com.deadtiger.advcreation.block_blacklist.BlockBlackListManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MessageSendServerBlockBlackListToClient extends MessageBase<MessageSendServerBlockBlackListToClient>
{

    public String playerName;
    public ArrayList<String> serverBlackListBlocks;
    public boolean refreshlist;

    public MessageSendServerBlockBlackListToClient()
    {

    }

    public MessageSendServerBlockBlackListToClient(ArrayList<String> serverBlackListBlocks, String playerName)
    {
        this.refreshlist = false;
        this.playerName = playerName;
        this.serverBlackListBlocks = serverBlackListBlocks;
    }

    public MessageSendServerBlockBlackListToClient(boolean refreshlist, String playerName)
    {
        this.refreshlist = refreshlist;
        this.playerName = playerName;
        this.serverBlackListBlocks = new ArrayList<>();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientSide(MessageSendServerBlockBlackListToClient message, EntityPlayer player)
    {
        System.out.println("CLIENT player " + message.playerName + " which hasSinglePlayerserver = " + Minecraft.getMinecraft().isSingleplayer() +
                " received blacklist message with refresh = " + message.refreshlist + ", list size = " + message.serverBlackListBlocks.size());

        if(Minecraft.getMinecraft().isSingleplayer())
            return;

        if(message.refreshlist)
            BlockBlackListManager.refreshBlackList(new File(Minecraft.getMinecraft().gameDir, BlockBlackListManager.BLACKLIST_FILENAME));

        if(!message.serverBlackListBlocks.isEmpty())
            BlockBlackListManager.addBlacklistedEntries(message.serverBlackListBlocks);

    }

    @Override
    public void handleServerSide(MessageSendServerBlockBlackListToClient message, EntityPlayer player)
    {

    }


    @Override
    public void fromBytes(ByteBuf buf)
    {
        int playerNameLength = buf.readShort();
        this.playerName = (String) buf.readCharSequence(playerNameLength, Charset.defaultCharset());

        int sizeServerBlackListBlocks = buf.readInt();
        this.serverBlackListBlocks = new ArrayList<>();
        int stringLength = 0;
        for (int i = 0; i < sizeServerBlackListBlocks; i++)
        {
            stringLength = buf.readShort();
            this.serverBlackListBlocks.add( (String) buf.readCharSequence(stringLength, Charset.defaultCharset()));
        }

    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeShort(this.playerName.length());
        buf.writeCharSequence(this.playerName, Charset.defaultCharset());

        //write the list of blocks/items blacklisted on the server
        buf.writeInt(this.serverBlackListBlocks.size());
        String blacklistEntry;
        for (int i = 0; i < this.serverBlackListBlocks.size(); i++)
        {
            blacklistEntry = this.serverBlackListBlocks.get(i);
            buf.writeShort(blacklistEntry.length());
            buf.writeCharSequence(blacklistEntry, Charset.defaultCharset());
        }
    }


}
