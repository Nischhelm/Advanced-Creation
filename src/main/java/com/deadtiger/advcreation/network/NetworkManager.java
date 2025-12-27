package com.deadtiger.advcreation.network;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.client.render.RenderCameraFocusPoint;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.network.message.MessagePlaceListsTemplateBlock;
import com.deadtiger.advcreation.network.message.MessagePreviewListsTemplateBlock;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntityRenderer;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.utility.MultiThreadLock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class NetworkManager
{
    public static boolean PLAYER_INITIALISED = false;

    //dictionary that holds if the players are in first personview view
    public static HashMap<String, Boolean> PLAYER_IN_FIRST_PERSON_VIEW = new HashMap<>();
    public static int PREVIOUS_HASHCODE = 0;
    public static MultiThreadLock PLAYER_IN_FIRST_PERSON_VIEW_LOCK = new MultiThreadLock();

    public static ArrayList<EntityPlayer> RENDER_PLAYERS = new ArrayList<>();

    public static int MESSAGE_DELAY_TICK_COUNT = 0;
    public static boolean CLIENT_IS_SENDING_BLOCKS = false;
    public static ArrayDeque<MessagePlaceListsTemplateBlock> PLACE_MESSAGE_SEND_QUEUE = new ArrayDeque<>();
    public static boolean RESEND_LAST_PLACE_MESSAGE_SENT = false;
    public static MessagePlaceListsTemplateBlock LAST_PLACE_MESSAGE_SENT_TO_SERVER = null;
    public static int RESEND_COUNT = 0;
    public static final int RESEND_COUNT_LIMIT = 20; //limit the amount of resend to 20 incase of errors preventing the mod from locking up partially
    public static HashMap<EntityPlayerMP, ArrayDeque<MessagePlaceListsTemplateBlock>> PLACE_MESSAGE_RECEIVE_QUEUE = new HashMap<>();
    public static HashMap<EntityPlayerMP, Boolean> PLACE_RECEIVED_TEMPLATE = new HashMap<>();

    public static HashMap<String, ArrayList<MessagePreviewListsTemplateBlock>> PREVIEW_MESSAGE_RECEIVED_LIST = new HashMap<>();
    public static MultiThreadLock PREVIEW_MESSAGE_RECEIVED_LOCK = new MultiThreadLock();

    public static HashMap<String, Boolean> PLAYER_TOOLS_DISABLED = new HashMap<>();
    public static MultiThreadLock PLAYER_TOOLS_DISABLED_LOCK = new MultiThreadLock();


    public static boolean isPlayerInFirstPerson(String playerName)
    {
        PLAYER_IN_FIRST_PERSON_VIEW_LOCK.blockingAttemptAtLocking();
        boolean playerInFirstPerson = false;
        if (PLAYER_IN_FIRST_PERSON_VIEW.get(playerName) != null)
            playerInFirstPerson = PLAYER_IN_FIRST_PERSON_VIEW.get(playerName);
        PLAYER_IN_FIRST_PERSON_VIEW_LOCK.releaseLock();

        return playerInFirstPerson;
    }

    public static boolean isPlayerToolsDisabled(String playerName)
    {
        PLAYER_TOOLS_DISABLED_LOCK.blockingAttemptAtLocking();
        boolean playerInFirstPerson = false;
        if (PLAYER_TOOLS_DISABLED.get(playerName) != null)
            playerInFirstPerson = PLAYER_TOOLS_DISABLED.get(playerName);
        PLAYER_TOOLS_DISABLED_LOCK.releaseLock();

        return playerInFirstPerson;
    }

    public static void trySendPlaceMessageToServer()
    {

        if(Minecraft.getMinecraft().isGamePaused())
            return;

        //only send the next message if the current message has already translated into changes in the client.
        if(NetworkPlaceBlockListFormatter.CURR_MESSAGE_CHECK_COMPLETE)
        {
            //i want a few ticks inbetween message so the server is not overwhelmed
            if (MESSAGE_DELAY_TICK_COUNT <= 0)
            {
                //send messages to build new template per tick so that the client has time to process the new blocks being set.
                MessagePlaceListsTemplateBlock msg = PLACE_MESSAGE_SEND_QUEUE.pollLast();
                if (msg != null)
                {
                    NetworkHandler.sendListToServer(msg);
                    MESSAGE_DELAY_TICK_COUNT = 2;
                    LAST_PLACE_MESSAGE_SENT_TO_SERVER = msg;
                    NetworkPlaceBlockListFormatter.setCurrMessageBlocksToCheckForPlacement(NetworkPlaceBlockListFormatter.BLOCKS_TO_CHECK_FOR_PLACEMENT.pollLast());

                }
                else
                {
                    CLIENT_IS_SENDING_BLOCKS = false;
                }
                if (PLACE_MESSAGE_SEND_QUEUE.isEmpty())
                    MESSAGE_DELAY_TICK_COUNT = 0;

            }
            else
                MESSAGE_DELAY_TICK_COUNT--;
        }

        if(RESEND_LAST_PLACE_MESSAGE_SENT && LAST_PLACE_MESSAGE_SENT_TO_SERVER != null)
        {
            RESEND_COUNT++;

            if(RESEND_COUNT >= RESEND_COUNT_LIMIT)
            {
                NetworkManager.LAST_PLACE_MESSAGE_SENT_TO_SERVER = null;
                NetworkPlaceBlockListFormatter.CURR_MESSAGE_BLOCKS_TO_CHECK_FOR_PLACEMENT = null;
                NetworkPlaceBlockListFormatter.CURR_MESSAGE_CHECK_COMPLETE = true;
                RESEND_COUNT = 0;
            }
            else
            {
                System.out.println("CLIENT RESEND try " + RESEND_COUNT + " place message nbr " + LAST_PLACE_MESSAGE_SENT_TO_SERVER.messageNbr + " out of " + LAST_PLACE_MESSAGE_SENT_TO_SERVER.totMessages);
                NetworkHandler.sendListToServer(LAST_PLACE_MESSAGE_SENT_TO_SERVER);
                MESSAGE_DELAY_TICK_COUNT = 2;
                RESEND_LAST_PLACE_MESSAGE_SENT = false;
            }



//            try
//            {
//                Thread.sleep(NetworkPlaceBlockListFormatter.sleeptimeAfterCheckOrSend);
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
        }
    }

    public static void processReceivedMessages()
    {
        for (EntityPlayerMP player : PLACE_MESSAGE_RECEIVE_QUEUE.keySet())
        {
            if (PLACE_RECEIVED_TEMPLATE.get(player) != null && PLACE_RECEIVED_TEMPLATE.get(player))
            {

                MessagePlaceListsTemplateBlock message = PLACE_MESSAGE_RECEIVE_QUEUE.get(player).pollFirst();
                while (message != null)
                {
                    //there is a needsUpdateList here in MC 1.16.5 for fences, walls and panes that were deleted in the client before placing them they need
                    //to be synced again with the server.
                    //ServerPlacementHelper.serverSendsUpdateCommandsToClient(message.startPos,updateNeededPosList,player);
                    // I don't think MC1.12.2 needs this.
                    for (MessagePlaceListsTemplateBlock.CompressedTemplateBlock block : message.firstPlacedList)
                    {
                        IBlockState blockState = message.stateLegendList.get(block.blockStateIndex);
//                        TileEntity tileEntity = HelpFunctions.createTileEntityOf(blockState);
                        TileEntity tileEntity = AdvCreation.proxy.createTileEntityOf(blockState);
                        if(tileEntity != null && block.getTag() != null)
                        {
                            tileEntity.readFromNBT(block.getTag());
                            tileEntity.setWorld(player.world);
                        }

                        PlaceTemplateMode.placeBlockServer(message.startPos, new TemplateBlock(EnumFacing.NORTH, block.getPos(), blockState, tileEntity), (EntityPlayerMP) player);
                    }

                    for (MessagePlaceListsTemplateBlock.CompressedTemplateBlock block : message.secondPlacedList)
                    {
                        IBlockState blockState = message.stateLegendList.get(block.blockStateIndex);
                        TileEntity tileEntity = AdvCreation.proxy.createTileEntityOf(blockState);
                        if(tileEntity != null && block.getTag() != null)
                        {
                            tileEntity.readFromNBT(block.getTag());
                            tileEntity.setWorld(player.world);
                        }
                        PlaceTemplateMode.placeBlockServer(message.startPos, new TemplateBlock(EnumFacing.NORTH, block.getPos(), blockState,tileEntity), (EntityPlayerMP) player);
                    }

                    message = PLACE_MESSAGE_RECEIVE_QUEUE.get(player).pollFirst();
                }

            }


        }
    }

    public static void processPreviewMessages(RayTraceResult objectMouseOver, EntityPlayer entityplayer, Float partialTicks)
    {
        if (PREVIEW_MESSAGE_RECEIVED_LOCK.attemptLocking())
        {
            for (String playerName : PREVIEW_MESSAGE_RECEIVED_LIST.keySet())
            {
                if (playerName.equals(entityplayer.getName()))
                    continue;

                ArrayList<MessagePreviewListsTemplateBlock> messages = PREVIEW_MESSAGE_RECEIVED_LIST.get(playerName);
                for (MessagePreviewListsTemplateBlock message : messages)
                {
                    if (message.drawSelectionBox)
                    {
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableAlpha();
                        GlStateManager.enableBlend();

                        RenderPreview.drawSelectionBoundingBox(message.startPos, message.endPos, objectMouseOver.hitVec, Minecraft.getMinecraft(), entityplayer, false, partialTicks, 0.5F, 0.5F, 0.5F, 1.0F);

                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                        GlStateManager.disableColorMaterial();
                    }


                    for (MessagePlaceListsTemplateBlock.CompressedTemplateBlock block : message.firstPlacedList)
                    {
                        IBlockState state = message.stateLegendList.get(block.getBlockStateIndex());
                        if (message.drawOutlineOnly)
                            RenderTemplate.drawBlockOutline(block, objectMouseOver, entityplayer, partialTicks, 0.0F, 0.0F, 1.0F);
                        else if (state.getMaterial() != Blocks.AIR.getDefaultState().getMaterial())
                        {
                            TileEntity tileEntity = AdvCreation.proxy.createTileEntityOf(state);
                            if(tileEntity != null && block.getTag() != null)
                                tileEntity.readFromNBT(block.getTag());
                            RenderPreview.drawPreviewBlock(block.pos, new TemplateBlock(EnumFacing.NORTH, 0, 0, 0, state,tileEntity)
                                    , entityplayer, partialTicks,EnumFacing.NORTH );
                        }
                            //TODO: Lazy fix: tis needs a information about te side that object is on instead of EnumFacing.NORTH as last argument
                        else
                            RenderTemplate.drawBlockOutline(block, objectMouseOver, entityplayer, partialTicks, 1.0F, 0.0F, 0.0F);
                    }

                    for (MessagePlaceListsTemplateBlock.CompressedTemplateBlock block : message.secondPlacedList)
                    {
                        IBlockState state = message.stateLegendList.get(block.getBlockStateIndex());
                        if (state.getMaterial() != Blocks.AIR.getDefaultState().getMaterial())
                            RenderTemplate.drawBlockOutline(block, objectMouseOver, entityplayer, partialTicks, 0.0F, 0.0F, 1.0F);
                        else
                            RenderTemplate.drawBlockOutline(block, objectMouseOver, entityplayer, partialTicks, 1.0F, 0.0F, 0.0F);
                    }

                }
            }
            PREVIEW_MESSAGE_RECEIVED_LOCK.releaseLock();
        }
    }

    public static void processOtherPlayerRenderingMessages(EntityPlayer entityplayer, Float partialTicks)
    {
        //scale the size of the indicator based on the cameraDistance
        double radius = ModEntityRenderer.customCameraDistance / 12.0;
        if (radius > 0.5)
            radius = 0.5;

        for (EntityPlayer player : RENDER_PLAYERS)
        {
            if((entityplayer != player)|| IsometricCamera.isPlayerInIsometricPerspective() )
                RenderCameraFocusPoint.renderPlayerCameraFocusPoint(entityplayer, player, radius, partialTicks);
        }
    }

    public static void sendPreviewBlocksToServer(EntityPlayer entityplayer, ArrayList<TemplateBlock> currPreviewBlocks, boolean b)
    {
        if (AdvCreation.drawSelectionBox)
        {
            MessagePreviewListsTemplateBlock.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, currPreviewBlocks, new ArrayList<>(), entityplayer.getName(), AdvCreation.previewStartPos, AdvCreation.previewEndPos);
            AdvCreation.drawSelectionBox = false;
        }
        else
            MessagePreviewListsTemplateBlock.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, currPreviewBlocks, new ArrayList<>(), entityplayer.getName(), b);
    }


    public static void sendPreviewBlocksToServer(EntityPlayer entityplayer, ArrayList<TemplateBlock> currPreviewBlocks,ArrayList<TemplateBlock> currOutlinePreviewBlocks, boolean b)
    {
        if (AdvCreation.drawSelectionBox)
        {
            MessagePreviewListsTemplateBlock.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, currPreviewBlocks, currOutlinePreviewBlocks, entityplayer.getName(), AdvCreation.previewStartPos, AdvCreation.previewEndPos);
            AdvCreation.drawSelectionBox = false;
        }
        else
            MessagePreviewListsTemplateBlock.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, currPreviewBlocks, currOutlinePreviewBlocks, entityplayer.getName(), b);
    }

}
