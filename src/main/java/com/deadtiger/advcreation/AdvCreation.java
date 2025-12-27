package com.deadtiger.advcreation;


import com.deadtiger.advcreation.block.ModBlocks;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_overlay.AbstractGuiOverlay;
import com.deadtiger.advcreation.item.ModItems;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.NetworkManager;
import com.deadtiger.advcreation.proxy.IProxy;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Random;

//@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class AdvCreation
{
    //boolean that needs to be true when you compile as a jar
    public static boolean isJar = false;
//    public static boolean isJar = true;

    @Mod.Instance(Reference.MODID)
    public static AdvCreation instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    public static EnumMainMode mode = EnumMainMode.BUILD;

    private static int frame_count = 0;
    private static int X_prevMouse = -500;
    private static int Y_prevMouse = -500;

    public static boolean rightClickDownClient = false;
    public static boolean leftClickDownClient = false;
    public static int leftClickCountClient = 0;
    public static boolean rightClickDownServer = false;
    public static boolean leftClickDownServer = false;
    public static HashMap<EntityPlayerMP, Integer> leftClickCountServer = new HashMap<>(); //keep a leftclick count per player

    public static boolean clientRightClickGuiOverlay = false;
    public static boolean clientLeftClickGuiOverlay = false;

    public static boolean initialResize = false;

    public static BlockPos previewStartPos = BlockPos.ORIGIN;
    public static BlockPos previewEndPos = BlockPos.ORIGIN;
    public static boolean drawSelectionBox = false;


    public static Random rand;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //initialise all the elements you want to add to minecraft
        //registering all the mod options that are available to the player
//        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
//        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
        //registering the custom key and mouse inputhandlers
        proxy.preInit();
        ModBlocks.init();
        ModItems.init();
        //registering all custom networking channels
        NetworkHandler.init();

        LogHelper.info("Pre Initialisation Complete");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code

        LogHelper.info("Initialisation Complete");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }


    public static EnumMainMode getMode()
    {
        return mode;
    }

    public static void setMode(EnumMainMode mode)
    {
        if (mode == EnumMainMode.PLACE)
            GuiIngameForge.renderHotbar = false;
        else
            GuiIngameForge.renderHotbar = true;

        AdvCreation.mode = mode;

        if (GuiOverlayManager.isGuiOverlayVisible())
        {
            for (AbstractGuiOverlay overlay : GuiOverlayManager.GUI_OVERLAYS)
            {
                overlay.updateChangedMainMode(AdvCreation.mode);
            }
        }

        NetworkManager.RENDER_PLAYERS.clear();
    }


    @Mod.EventBusSubscriber
    public static class RegsitrationHandler
    {

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
//            ModItems.register(event.getRegistry());
//            ModBlocks.registerItemBlocks(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
//            ModBlocks.register(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
//            ModItems.registerModels();
//            ModBlocks.registerModels();
        }


    }
}


