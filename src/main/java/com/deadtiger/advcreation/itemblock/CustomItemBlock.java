package com.deadtiger.advcreation.itemblock;

import com.deadtiger.advcreation.AdvCreation;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CustomItemBlock extends ItemBlock
{
    public CustomItemBlock(Block block)
    {
        super(block);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        AdvCreation.proxy.getSubItems(this,tab, items);


    }
}
