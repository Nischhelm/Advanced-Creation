package com.deadtiger.advcreation.network;

import com.deadtiger.advcreation.network.message.*;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler
{
    private static SimpleNetworkWrapper PLACE_TEMPLATE_BLOCK_CHANNEL;
    private static SimpleNetworkWrapper CLICK_EVENT_CHANNEL;
    private static SimpleNetworkWrapper PLACE_LIST_TEMPLATEBLOCK_CHANNEL;
    private static SimpleNetworkWrapper FIRS_PERSON_VIEW_UPDATE;
    private static SimpleNetworkWrapper PREVIEW_BlOCKS_UPDATE;
    private static SimpleNetworkWrapper LOG_CHANNEL;
    private static SimpleNetworkWrapper REQUEST_SIGN_EDIT_CHANNEL;
    private static SimpleNetworkWrapper SHARE_BLACKLIST_CHANNEL;
    private static SimpleNetworkWrapper BLOCK_ACTION_CHANNEL;

    public static void init()
    {
        //!!!!!! CHANNEL name cannot be longer then 20 characters or it will not work on a server !!!!!!!

        //allows the client to tell the server to place a single block
        PLACE_TEMPLATE_BLOCK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID + "" +
                "1");
        PLACE_TEMPLATE_BLOCK_CHANNEL.registerMessage(MessagePlaceTemplateBlock.class, MessagePlaceTemplateBlock.class, 0, Side.SERVER);
        PLACE_TEMPLATE_BLOCK_CHANNEL.registerMessage(MessagePlaceTemplateBlock.class, MessagePlaceTemplateBlock.class, 0, Side.CLIENT);

        //The server lets the client know when he registered him leftclicking
        //This is a bit of a work arround but I forgot why this was necessary
        CLICK_EVENT_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID + "2");
        CLICK_EVENT_CHANNEL.registerMessage(MessageTriggerClickEvent.class, MessageTriggerClickEvent.class, 0, Side.CLIENT);

        //Allows the client to tell the server to place a whole list of blocks
        // this is similar to the PLACE_TEMPLATE_BLOCK_CHANNEL except it can send multiple blocks at once preventing the
        // network channels from getting clogged with messages when placing multiple blocks at the same time
        PLACE_LIST_TEMPLATEBLOCK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID + "3");
        PLACE_LIST_TEMPLATEBLOCK_CHANNEL.registerMessage(MessagePlaceListsTemplateBlock.class, MessagePlaceListsTemplateBlock.class, 0, Side.SERVER);

        //First the client tells the server that its entering/exiting the firstperson view then the server notifies all other client of this.
        FIRS_PERSON_VIEW_UPDATE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID + "4");
        FIRS_PERSON_VIEW_UPDATE.registerMessage(MessageUpdatePlayerSetting.class, MessageUpdatePlayerSetting.class, 0, Side.SERVER);
        FIRS_PERSON_VIEW_UPDATE.registerMessage(MessageUpdatePlayerSetting.class, MessageUpdatePlayerSetting.class, 0, Side.CLIENT);

        //Client lets the server know where they are currently previewing a template or blocks. The server sends this to
        // all other clients so that they can also  display the preview of the client player.
        PREVIEW_BlOCKS_UPDATE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID + "5");
        PREVIEW_BlOCKS_UPDATE.registerMessage(MessagePreviewListsTemplateBlock.class, MessagePreviewListsTemplateBlock.class, 0, Side.SERVER);
        PREVIEW_BlOCKS_UPDATE.registerMessage(MessagePreviewListsTemplateBlock.class, MessagePreviewListsTemplateBlock.class, 0, Side.CLIENT);

        //The client can send logs of the client player to the server for debug/improvement purposes
        LOG_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID + "6");
        LOG_CHANNEL.registerMessage(MessageLogToServer.class, MessageLogToServer.class, 0, Side.SERVER);

        REQUEST_SIGN_EDIT_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID + "7");
        REQUEST_SIGN_EDIT_CHANNEL.registerMessage(MessageClientRequestToEditSign.class,MessageClientRequestToEditSign.class,0,Side.SERVER);

        SHARE_BLACKLIST_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID + "8");
        SHARE_BLACKLIST_CHANNEL.registerMessage(MessageSendServerBlockBlackListToClient.class, MessageSendServerBlockBlackListToClient.class, 0, Side.CLIENT);

        //The server lets the client know when he registered him placing a block while not in isometric view
        BLOCK_ACTION_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID + "9");
        BLOCK_ACTION_CHANNEL.registerMessage(MessageNotifyClientOfBlockAction.class, MessageNotifyClientOfBlockAction.class, 0, Side.CLIENT);


    }

    public static void sendToServer(IMessage message)
    {
        PLACE_TEMPLATE_BLOCK_CHANNEL.sendToServer(message);
    }

    public static void sendBlockPlacementToClient(IMessage message, EntityPlayerMP player)
    {
        PLACE_TEMPLATE_BLOCK_CHANNEL.sendTo(message, player);
    }

    public static void sendListToServer(IMessage message)
    {
        PLACE_LIST_TEMPLATEBLOCK_CHANNEL.sendToServer(message);
    }

    public static void sendToClient(IMessage message, EntityPlayerMP player)
    {
        CLICK_EVENT_CHANNEL.sendTo(message, player);
    }

    public static void sendPlayerSettingsUpdateToServer(IMessage message)
    {
        FIRS_PERSON_VIEW_UPDATE.sendToServer(message);
    }

    public static void sendFirstPersonUpdateToAllClients(IMessage message)
    {
        FIRS_PERSON_VIEW_UPDATE.sendToAll(message);
    }

    public static void sendPreviewBlocksToAllClients(IMessage message)
    {
        PREVIEW_BlOCKS_UPDATE.sendToAll(message);
    }

    public static void sendPreviewBlocksToServer(IMessage message)
    {
        PREVIEW_BlOCKS_UPDATE.sendToServer(message);
    }

    public static void sendLogToServer(IMessage message)
    {
        LOG_CHANNEL.sendToServer(message);
    }

    public static void sendSignEditRequestToServer(IMessage message)
    {
        REQUEST_SIGN_EDIT_CHANNEL.sendToServer(message);
    }

    public static void sendBlacklistActionToClient(MessageBase message, EntityPlayerMP player)
    {
        SHARE_BLACKLIST_CHANNEL.sendTo(message,player);
    }

    public static void sendBlockActionToClient(MessageBase message, EntityPlayerMP player)
    {
        BLOCK_ACTION_CHANNEL.sendTo(message,player);
    }

}
