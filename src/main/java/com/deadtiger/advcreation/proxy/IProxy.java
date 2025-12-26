package com.deadtiger.advcreation.proxy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

public interface IProxy
{
    public void registerItemRenderer(Item item, int meta, String id);

    public void registerRenderers() ;
    public String localize(String unlocalized, Object... args);

    public void preInit();

    public void init();

    public void postInit();

    public TileEntity createTileEntityOf(IBlockState blockState);

    public void getSubItems(ItemBlock block, CreativeTabs tab, NonNullList<ItemStack> items);


}
