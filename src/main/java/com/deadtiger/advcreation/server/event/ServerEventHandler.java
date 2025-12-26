package com.deadtiger.advcreation.server.event;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.block_blacklist.BlockBlackListManager;
import com.deadtiger.advcreation.logging.LoggingServer;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.NetworkManager;
import com.deadtiger.advcreation.network.message.MessageNotifyClientOfBlockAction;
import com.deadtiger.advcreation.network.message.MessageSendServerBlockBlackListToClient;
import com.deadtiger.advcreation.network.message.MessageTriggerClickEvent;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.server.player.MPPlayerProperties;
import com.deadtiger.advcreation.server.server_commands.CommandPrintActions;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.deadtiger.advcreation.network.NetworkManager.isPlayerInFirstPerson;
import static com.deadtiger.advcreation.network.NetworkManager.isPlayerToolsDisabled;

/**
 * Don't annotate this class with the value=Side.Server otherwise it will not be loaded in a singleplayer game
 * I know this is stupid but the singleplayer game runs a local server when playing singleplayer and it needs these methods.
 */
@Mod.EventBusSubscriber(modid = Reference.MODID)
public class ServerEventHandler
{
    /**
     * This event is triggered when the player right clicks with nothing in hand AND not hitting any block!
     * This doesn't happen on the server I think, but I'll keep this to be sure anyway
     */
    @SubscribeEvent
    public static void interceptServerRightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
    {
        if(!event.getEntityPlayer().world.isRemote)
        {
            //server event
            if (!isPlayerInFirstPerson(event.getEntityPlayer().getName()) && !isPlayerToolsDisabled(event.getEntityPlayer().getName()))
            {
                event.setCanceled(true);
                AdvCreation.rightClickDownServer = true;
            }
        }

    }

    @SubscribeEvent
    public static void interceptServerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if(!event.getEntityPlayer().world.isRemote)
        {
            if (!isPlayerInFirstPerson(event.getEntityPlayer().getName()) && !isPlayerToolsDisabled(event.getEntityPlayer().getName()))
            {
                if (event.getEntityPlayer() instanceof EntityPlayerMP){
                    if(((EntityPlayerMP)event.getEntityPlayer()).interactionManager.isCreative()){
                        //if the player is not in firstperson mode cancel this event unless he clicks on a door or trapdoor
                        if (event.getHand() == EnumHand.MAIN_HAND)
                        {
                            Item item = event.getEntityPlayer().getHeldItemMainhand().getItem();
                            if ((PlacementHelper.isInteractableBlock(event.getEntityPlayer().world.getBlockState(event.getPos()).getBlock(),event.getPos())) ||  !(item instanceof ItemBlock || PlacementHelper.isAllowedNonItemBlocks(item)))
                                event.setCanceled(false);
                            else
                                event.setCanceled(true);
                        }
                        else
                            event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void interceptLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        if (!event.getEntityPlayer().world.isRemote)
        {
            NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW_LOCK.blockingAttemptAtLocking();
            if (NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW.get(event.getEntityPlayer().getName()) != null && !NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW.get(event.getEntityPlayer().getName())  && !isPlayerToolsDisabled(event.getEntityPlayer().getName()))
            {
                if (event.getEntityPlayer() instanceof EntityPlayerMP){
                    if(((EntityPlayerMP)event.getEntityPlayer()).interactionManager.isCreative()){
                        NetworkHandler.sendToClient(new MessageTriggerClickEvent(MessageTriggerClickEvent.mouseButton.leftMouse, event.getPos(), event.getFace()), (EntityPlayerMP) event.getEntityPlayer());
                        event.setCanceled(true);
                    }
                }
            }
            NetworkManager.PLAYER_IN_FIRST_PERSON_VIEW_LOCK.releaseLock();
        }
    }


    @SubscribeEvent
    public static void intercepServerTick(TickEvent.ServerTickEvent event)
    {
        NetworkManager.processReceivedMessages();
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public static void interceptStandAloneServerTick(TickEvent.ServerTickEvent event)
    {
        LoggingServer.tickLogging();

    }

    @SubscribeEvent
    public static void interceptServerPlayerUpdate(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (!event.player.world.isRemote)
            {
                //server player update
                MPPlayerProperties.updatePlayerProperties(event);

//                if (!Mousebindings.isRightButtonDown())
//                        AdvCreation.rightClickDownServer = false;
//                if (!Mousebindings.isLeftButtonDown())
//                        AdvCreation.leftClickDownServer = false;

            }
        }
    }

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void interceptServerStarting(FMLServerStartingEvent event)
    {
        System.out.println("starting the server event");
        event.registerServerCommand(new CommandPrintActions());


    }

    @SubscribeEvent
    public static void interceptPlayerLoggingIn(EntityJoinWorldEvent event)
    {
        World world =  event.getWorld();
        if(!world.isRemote && event.getEntity() instanceof EntityPlayerMP)
        {
            //tell player client to add the server's blacklist to it's blacklist.
            NetworkHandler.sendBlacklistActionToClient(new MessageSendServerBlockBlackListToClient(BlockBlackListManager.BLACKLISTED_BLOCKS,event.getEntity().getName()),(EntityPlayerMP) event.getEntity());
        }

    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public static void interceptServerChatEvent(ServerChatEvent event)
    {
        String message = event.getMessage();
        System.out.println("intercept message " + event.getMessage());
        if(message.contains(" left the game"))
        {
            String playerName = message.split(" left the game")[0];
            System.out.println("Player left the game: " + playerName);
            if(LoggingServer.prevLogTime.containsKey(playerName))
            {
                LoggingServer.prevLogTime.remove(playerName);
                System.out.println("Player" + playerName +"removed from logging");
            }
        }

    }


    //break events and place events to implement the undo/redo when not in isometric mode.
    @SubscribeEvent
    public static void interceptBreakBlockEvent(BlockEvent.BreakEvent event)
    {
        if(!(event.getPlayer() instanceof EntityPlayer))
            return;
        World world = event.getWorld();
        if(world.isRemote)
            return;

        System.out.println("BlockEvent.BreakEvent Player " + event.getPlayer().getName() + " pos" + event.getPos());
        BlockPos pos =  event.getPos();
        TileEntity tileEntity = world.getTileEntity(pos);
        TemplateBlock oldBlock = new TemplateBlock(EnumFacing.NORTH, pos, event.getState(),tileEntity);


        ArrayList<TemplateBlock> oldBlocks = new ArrayList<>();
        oldBlocks.add(oldBlock);
        MessageNotifyClientOfBlockAction.sendBlocksListToClient(oldBlocks,(EntityPlayerMP) event.getPlayer());
    }

    @SubscribeEvent
    public static void interceptPlaceBlockEvent(BlockEvent.EntityPlaceEvent event)
    {
        if(!(event.getEntity() instanceof EntityPlayer))
            return;
        World world = event.getWorld();
        if(world.isRemote)
            return;

        System.out.println("BlockEvent.EntityPlaceEvent Player " + event.getEntity().getName() + " placedBlock " + event.getPlacedBlock() + " placedAgainst " + event.getPlacedAgainst());

        BlockPos pos =  event.getPos();
        BlockSnapshot snap = event.getBlockSnapshot();
        IBlockState blockState = snap.getReplacedBlock();
        TileEntity tileEntity = snap.getTileEntity();
        TemplateBlock oldBlock = new TemplateBlock(EnumFacing.NORTH, pos, blockState,tileEntity);


        ArrayList<TemplateBlock> oldBlocks = new ArrayList<>();
        oldBlocks.add(oldBlock);
        MessageNotifyClientOfBlockAction.sendBlocksListToClient(oldBlocks,(EntityPlayerMP) event.getEntity());
    }

    @SubscribeEvent
    public static void interceptMultiPlaceBlockEvent(BlockEvent.EntityMultiPlaceEvent event)
    {
        if(!(event.getEntity() instanceof EntityPlayer))
            return;
        World world = event.getWorld();
        if(world.isRemote)
            return;
        System.out.println("BlockEvent.EntityMultiPlaceEven Player " + event.getEntity().getName() + " placedBlock " + event.getPlacedBlock() + " placedAgainst " + event.getPlacedAgainst());
        ArrayList<TemplateBlock> oldBlocks = new ArrayList<>();
        for (BlockSnapshot snap: event.getReplacedBlockSnapshots())
        {
            BlockPos pos =  snap.getPos();
            IBlockState blockState = snap.getReplacedBlock();
            TileEntity tileEntity = snap.getTileEntity();
            TemplateBlock oldBlock = new TemplateBlock(EnumFacing.NORTH, pos, blockState,tileEntity);
            oldBlocks.add(oldBlock);
        }

        MessageNotifyClientOfBlockAction.sendBlocksListToClient(oldBlocks,(EntityPlayerMP) event.getEntity());
    }


}
