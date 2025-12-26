package com.deadtiger.advcreation.network.message;

import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.nio.charset.Charset;

/***
 * message send by the server to the client when the mouse is held informing the client to run the rightclickblock or leftclickblock again
 */
public class MessageUpdatePlayerSetting extends MessageBase<MessageUpdatePlayerSetting>
{

    public boolean firstPersonViewActive;
    public boolean toolDisabled;
    public String playerName;

    public MessageUpdatePlayerSetting()
    {

    }

    public MessageUpdatePlayerSetting(boolean firstPersonViewActive, String playerName)
    {
        this(firstPersonViewActive,false,playerName);
    }

    public MessageUpdatePlayerSetting(boolean firstPersonViewActive, boolean toolDisabled, String playerName)
    {
        this.playerName = playerName;
        this.firstPersonViewActive = firstPersonViewActive;
        this.toolDisabled = toolDisabled;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientSide(MessageUpdatePlayerSetting message, EntityPlayer player)
    {
        if (message != null)
        {
            NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW_LOCK.blockingAttemptAtLocking();
             NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW.put(message.playerName, message.firstPersonViewActive);
            NetworkManager.PREVIEW_MESSAGE_RECEIVED_LOCK.blockingAttemptAtLocking();
            if(this.firstPersonViewActive && NetworkManager.PREVIEW_MESSAGE_RECEIVED_LIST.containsKey(this.playerName))
                NetworkManager.PREVIEW_MESSAGE_RECEIVED_LIST.remove(this.playerName);
            NetworkManager.PREVIEW_MESSAGE_RECEIVED_LOCK.releaseLock();
            NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW_LOCK.releaseLock();

            NetworkManager.PLAYER_TOOLS_DISABLED_LOCK.blockingAttemptAtLocking();
            NetworkManager.PLAYER_TOOLS_DISABLED.put(message.playerName, message.toolDisabled);
            NetworkManager.PLAYER_TOOLS_DISABLED_LOCK.releaseLock();

        }
        else
            System.out.println("why is the message null?");


    }

    @Override
    public void handleServerSide(MessageUpdatePlayerSetting message, EntityPlayer player)
    {
        if (message != null)
        {
            NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW_LOCK.blockingAttemptAtLocking();
            NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW.put(message.playerName, message.firstPersonViewActive);


            NetworkManager.PLAYER_TOOLS_DISABLED_LOCK.blockingAttemptAtLocking();
            NetworkManager.PLAYER_TOOLS_DISABLED.put(message.playerName, message.toolDisabled);
            System.out.println(message.hashCode() + " playername " + message.playerName + " real player name " + player.getName() + " firstperson " + message.firstPersonViewActive + " toolsDisabled " + message.toolDisabled);
            //update the clients with the status of all other players
            for (String name : NetworkManager.PLAYER_TOOLS_DISABLED.keySet())
            {
                NetworkHandler.sendFirstPersonUpdateToAllClients(new MessageUpdatePlayerSetting(NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW.get(name),NetworkManager.PLAYER_TOOLS_DISABLED.get(name), name));
            }
            NetworkManager.PLAYER_TOOLS_DISABLED_LOCK.releaseLock();
            NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW_LOCK.releaseLock();
        }
        else
            System.out.println("why is the message null?");

    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int playerNameLength = buf.readShort();
        this.playerName = (String) buf.readCharSequence(playerNameLength, Charset.defaultCharset());
        this.firstPersonViewActive = buf.readBoolean();
        boolean tooldisable = buf.readBoolean();
        this.toolDisabled = tooldisable;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeShort(this.playerName.length());
        buf.writeCharSequence(this.playerName, Charset.defaultCharset());
        buf.writeBoolean(this.firstPersonViewActive);
        buf.writeBoolean(this.toolDisabled);
    }
}
