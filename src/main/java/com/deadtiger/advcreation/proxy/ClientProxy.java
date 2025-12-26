package com.deadtiger.advcreation.proxy;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.block.ModBlocks;
import com.deadtiger.advcreation.block_blacklist.BlockBlackListManager;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_utility.deleteGifResources;
import com.deadtiger.advcreation.client.input.KeyInputHandler;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.input.MouseInputHandler;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.logging.Logging;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.template.TemplateManager;
import com.deadtiger.advcreation.utility.FakeWorld;
import com.deadtiger.advcreation.utility.LogHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        System.out.println("Load CLIENT configuration handler");
//        ConfigurationHandler.delete_any_old_configfiles();
        ConfigurationHandler handler = new ConfigurationHandler();
        FMLCommonHandler.instance().bus().register(handler);
        registerKeybinds();
    }

    private void registerKeybinds()
    {
        FMLCommonHandler.instance().bus().register(new KeyInputHandler());
        FMLCommonHandler.instance().bus().register(new MouseInputHandler());
        for(Keybindings key: Keybindings.values())
        {
            ClientRegistry.registerKeyBinding(key.getKeybind());
        }
    }

    @Override
    public void init(){}

    @Override
    public void postInit()
    {
        //extract all the new zip files in the advcreation_templates_zips
        TemplateManager.extractTemplateZips();
        //can this be a good place to load files from the advcreation_templates folder?
        TemplateManager.loadTemplates();


        // register the build mode gui overlay to the forgeregister
        GuiOverlayManager.initGuiOverlays();

        AdvCreation.setMode(EnumMainMode.BUILD);

        //change keyBind for picking a block up(creative mode copy to current selected block into inventory used to be middle mouse)
//        Keybindings.rebindPickUpBlockKey();

        LogHelper.info("Post Initialisation Complete");
        Logging log = new Logging();
        PlaceTemplateMode.init();

        //to make sure the temp files for the gifs in the helpscreen are deleted
        Runtime.getRuntime().addShutdownHook(new Thread( new deleteGifResources() ));

        BlockBlackListManager.initialiseBlackList( new File(Minecraft.getMinecraft().mcDataDir, BlockBlackListManager.BLACKLIST_FILENAME));


    }
    @Override
    public TileEntity createTileEntityOf(IBlockState blockState)
    {
        TileEntity entity = blockState.getBlock().createTileEntity(FakeWorld.INSTANCE.setPreviewBlockState(blockState), blockState);
        if (entity != null)
            entity.setWorld(FakeWorld.INSTANCE);
        return entity;
    }

    @Override
    public void getSubItems(ItemBlock block, CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (tab.getTabLabel().contains("search") || tab.getTabLabel().contains("misc"))
        {

            if(block.getRegistryName().toString().equals(ModBlocks.LAVA_ITEM.getRegistryName().toString()))
                items.add(new ItemStack(ModBlocks.LAVA_ITEM,1,0));
            else if(block.getRegistryName().toString().equals(ModBlocks.WATER_ITEM.getRegistryName().toString()))
                items.add(new ItemStack(ModBlocks.WATER_ITEM,1,0));
        }
    }
}
