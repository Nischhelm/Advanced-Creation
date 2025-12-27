package com.deadtiger.advcreation.tree_creator;

import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/***
 * based on WorldGenBirchTree generate method
 */
public class CreateBirchTree extends CreateAbstrTree
{

    public ArrayList<TemplateBlock> generate(World worldIn, Random rand, BlockPos position, int x_size, int y_size)
    {
        int i = x_size;
        if (i < this.minHeight)
            i = this.minHeight;
        else if (i > this.maxHeight)
            i = this.maxHeight;

        boolean flag = true;

        if (!flag)
            return null;
        else
        {
            ArrayList<TemplateBlock> blockList = new ArrayList<>();
            boolean isSoil = true;

            if (isSoil && position.getY() < worldIn.getHeight() - i - 1)
            {
                for (int i2 = position.getY() - 3 + i; i2 <= position.getY() + i; ++i2)
                {
                    int k2 = i2 - (position.getY() + i);
                    int l2 = 1 - k2 / 2;

                    for (int i3 = position.getX() - l2; i3 <= position.getX() + l2; ++i3)
                    {
                        int j1 = i3 - position.getX();

                        for (int k1 = position.getZ() - l2; k1 <= position.getZ() + l2; ++k1)
                        {
                            int l1 = k1 - position.getZ();
                            if (Math.abs(j1) != l2 || Math.abs(l1) != l2 || rand.nextInt(2) != 0 && k2 != 0)
                            {
                                BlockPos blockpos = new BlockPos(i3, i2, k1);
                                blockList.add(new TemplateBlock(EnumFacing.NORTH, blockpos, DEFAULT_LEAF));
                            }
                        }
                    }
                }


                for (int i2 = 0; i2 < i; ++i2)
                {
                    blockList.add(new TemplateBlock(EnumFacing.NORTH, position.up(i2), DEFAULT_TRUNK));
                }

                return blockList;
            }
            else
                return null;
        }
    }

    @Override
    public boolean initTreeGen()
    {
        if (super.initTreeGen())
        {
            DEFAULT_TRUNK = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);

            DEFAULT_LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH).withProperty(BlockOldLeaf.CHECK_DECAY, false);
            this.minHeight = 5;
            this.maxHeight = 12;
        }
        return false;
    }


    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        return false;
    }
}
