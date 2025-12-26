package com.deadtiger.advcreation.item;

import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Mod.EventBusSubscriber(modid= Reference.MODID)
public class ModItems
{

    static Item tutorialItem;
    static String tutorialItemName = "tutorial_block";

    public static void init() 
    {
//        tutorialItem = new Item().setRegistryName(tutorialItemName).setTranslationKey(tutorialItemName);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
//        event.getRegistry().registerAll(tutorialItem);
    }

    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent event)
    {
        registerRender(tutorialItem);
    }

    private static void registerRender(Item item)
    {
//        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
    }
}

