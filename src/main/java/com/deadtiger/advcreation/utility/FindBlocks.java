package com.deadtiger.advcreation.utility;

import net.minecraft.init.Blocks;

import java.lang.reflect.Field;

public class FindBlocks
{
    public Field[] blocks  = Blocks.class.getFields();

    public FindBlocks()
    {
        for (Field block: blocks)
        {
            System.out.println(block.getName());
        }
    }
}
