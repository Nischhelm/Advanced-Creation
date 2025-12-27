package com.deadtiger.advcreation.tree_creator;

import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CreateHugeTree extends CreateAbstrTree
{
    @Override
    public ArrayList<TemplateBlock> generate(World worldIn, Random rand, BlockPos position, int x_size, int y_size)
    {
        return null;
    }

    @Override
    public boolean initTreeGen()
    {
        return super.initTreeGen();
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        return false;
    }


    protected int getHeight(Random rand, int x_size)
    {
        int i = x_size;
        if (i < minHeight)
            i = minHeight;
        else if (i > maxHeight)
            i = maxHeight;

        return i;
    }

    /**
     * returns whether or not there is space for a tree to grow at a certain position
     */
    private boolean isSpaceAt(World worldIn, BlockPos leavesPos, int height)
    {
        boolean flag = true;

        if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= 256)
        {
            for (int i = 0; i <= 1 + height; ++i)
            {
                int j = 2;

                if (i == 0)
                    j = 1;
                else if (i >= 1 + height - 2)
                    j = 2;

                for (int k = -j; k <= j && flag; ++k)
                {
                    for (int l = -j; l <= j && flag; ++l)
                    {
                        if (leavesPos.getY() + i < 0 || leavesPos.getY() + i >= 256)
                            flag = false;
                    }
                }
            }

            return flag;
        }
        else
        {
            return false;
        }
    }

    /**
     * returns whether or not there is dirt underneath the block where the tree will be grown.
     * It also generates dirt around the block in a 2x2 square if there is dirt underneath the blockpos.
     */
    private boolean ensureDirtsUnderneath(BlockPos pos, World worldIn, ArrayList<TemplateBlock> blockList)
    {
        BlockPos blockpos = pos.down();

        if (pos.getY() >= 2)
        {
            this.onPlantGrow(worldIn, blockpos, pos, blockList);
            this.onPlantGrow(worldIn, blockpos.east(), pos, blockList);
            this.onPlantGrow(worldIn, blockpos.south(), pos, blockList);
            this.onPlantGrow(worldIn, blockpos.south().east(), pos, blockList);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * returns whether or not a tree can grow at a specific position.
     * If it can, it generates surrounding dirt underneath.
     */
    protected boolean ensureGrowable(World worldIn, Random rand, BlockPos treePos, int height, ArrayList<TemplateBlock> blockList)
    {
        return this.isSpaceAt(worldIn, treePos, height) && this.ensureDirtsUnderneath(treePos, worldIn, blockList);
    }

    /**
     * grow leaves in a circle with the outsides being within the circle
     */
    protected void growLeavesLayerStrict(World worldIn, BlockPos layerCenter, int width, ArrayList<TemplateBlock> blockList)
    {
        int i = width * width;

        for (int j = -width; j <= width + 1; ++j)
        {
            for (int k = -width; k <= width + 1; ++k)
            {
                int l = j - 1;
                int i1 = k - 1;

                if (j * j + k * k <= i || l * l + i1 * i1 <= i || j * j + i1 * i1 <= i || l * l + k * k <= i)
                {
                    BlockPos blockpos = layerCenter.add(j, 0, k);
                    blockList.add(new TemplateBlock(EnumFacing.NORTH, blockpos, DEFAULT_LEAF));
                }
            }
        }
    }

    /**
     * grow leaves in a circle
     */
    protected void growLeavesLayer(World worldIn, BlockPos layerCenter, int width, ArrayList<TemplateBlock> blockList)
    {
        int i = width * width;

        for (int j = -width; j <= width; ++j)
        {
            for (int k = -width; k <= width; ++k)
            {
                if (j * j + k * k <= i)
                {
                    BlockPos blockpos = layerCenter.add(j, 0, k);
                    blockList.add(new TemplateBlock(EnumFacing.NORTH, blockpos, DEFAULT_LEAF));
                }
            }
        }
    }

    //Just a helper macro
    private void onPlantGrow(World world, BlockPos pos, BlockPos source, ArrayList<TemplateBlock> blockList)
    {
        blockList.add(new TemplateBlock(EnumFacing.NORTH, pos, net.minecraft.init.Blocks.DIRT.getDefaultState()));
    }
}
