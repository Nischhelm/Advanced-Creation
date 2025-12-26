package com.deadtiger.advcreation.block;

import com.deadtiger.advcreation.itemblock.CustomItemBlock;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid= Reference.MODID)
public class ModBlocks
{

    public static  Block WATER_PREVIEW;
    public static String WATER_PREVIEW_NAME = "water_preview_block";
    public static  Block LAVA_PREVIEW;
    public static String LAVA_PREVIEW_NAME = "lava_preview_block";

    public static ItemBlock WATER_ITEM;
    public static String WATER_ITEM_NAME = "water_block";
    public static ItemBlock LAVA_ITEM;
    public static String LAVA_ITEM_NAME = "lava_block";

    public static ArrayList<Block> MODBLOCKS = new ArrayList<>();

    public static void init() {

        WATER_PREVIEW = new Block(Material.WATER).setCreativeTab(CreativeTabs.MISC).setLightLevel(1.0f).setRegistryName(WATER_PREVIEW_NAME).setUnlocalizedName(WATER_PREVIEW_NAME);
        LAVA_PREVIEW = new Block(Material.LAVA).setCreativeTab(CreativeTabs.MISC).setLightLevel(1.0f).setRegistryName(LAVA_PREVIEW_NAME).setUnlocalizedName(LAVA_PREVIEW_NAME);


        MODBLOCKS.add(WATER_PREVIEW);
        MODBLOCKS.add(LAVA_PREVIEW);

        WATER_ITEM = (ItemBlock) new CustomItemBlock(Blocks.WATER).setRegistryName(WATER_ITEM_NAME).setUnlocalizedName(WATER_ITEM_NAME).setCreativeTab(CreativeTabs.MISC);
        LAVA_ITEM = (ItemBlock) new CustomItemBlock(Blocks.LAVA).setRegistryName(LAVA_ITEM_NAME).setUnlocalizedName(LAVA_ITEM_NAME).setCreativeTab(CreativeTabs.MISC);

    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(MODBLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(WATER_ITEM);
        event.getRegistry().registerAll(LAVA_ITEM);
    }

    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent event) {
        registerRender(WATER_ITEM);
        registerRender(LAVA_ITEM);
    }

    public static void registerRender(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
    }

//    static Block waterBlock;
//    static Block lavaBlock;
//
//    public static void init() {
//        waterBlock = new Block( Material.WATER).setRegistryName("water_preview_block").setCreativeTab(CreativeTabs.MATERIALS);
//        lavaBlock = new Block( Material.LAVA).setRegistryName("lava_preview_block").setCreativeTab(CreativeTabs.MATERIALS);
//    }
//
//    @SubscribeEvent
//    public static void registerBlocks(RegistryEvent.Register<Block> event) {
//        event.getRegistry().registerAll(waterBlock);
//        event.getRegistry().registerAll(lavaBlock);
//    }
//
//    @SubscribeEvent
//    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
//        event.getRegistry().registerAll(new ItemBlock(Blocks.WATER).setRegistryName("water_block"));
//        event.getRegistry().registerAll(new ItemBlock(Blocks.LAVA).setRegistryName("lava_block"));
//    }
//
//    @SubscribeEvent
//    public static void registerRenders(ModelRegistryEvent event) {
//        registerRender(Item.getItemFromBlock(waterBlock));
//        registerRender(Item.getItemFromBlock(lavaBlock));
//    }
//
//    public static void registerRender(Item item) {
//        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
//    }

}


