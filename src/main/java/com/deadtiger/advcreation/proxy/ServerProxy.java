package com.deadtiger.advcreation.proxy;

import com.deadtiger.advcreation.block_blacklist.BlockBlackListManager;
import com.deadtiger.advcreation.handler.ServerConfigurationHandler;
import com.deadtiger.advcreation.logging.LoggingServer;
import com.deadtiger.advcreation.utility.FakeWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        System.out.println("Load SERVER configuration handler");
//        ServerConfigurationHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ServerConfigurationHandler());
    }

    @Override
    public void postInit()
    {
        LoggingServer logger = new LoggingServer();
        Path path = Paths.get(BlockBlackListManager.BLACKLIST_FILENAME);
        BlockBlackListManager.initialiseBlackList(path.toFile());
    }
    @Override
    public TileEntity createTileEntityOf(IBlockState blockState)
    {
        TileEntity entity = blockState.getBlock().createTileEntity(null, blockState);
        return entity;
    }

    @Override
    public void getSubItems(ItemBlock block, CreativeTabs tab, NonNullList<ItemStack> items)
    {

    }
}
