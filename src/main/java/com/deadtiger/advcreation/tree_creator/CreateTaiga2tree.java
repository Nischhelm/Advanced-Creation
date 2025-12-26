package com.deadtiger.advcreation.tree_creator;

import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CreateTaiga2tree extends CreateAbstrTree
{
    @Override
    public boolean initTreeGen()
    {
        if (super.initTreeGen())
        {
            DEFAULT_LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, false);
            DEFAULT_TRUNK = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
            minHeight = 6;
            maxHeight = 10;
        }
        return true;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        return false;
    }

    @Override
    public ArrayList<TemplateBlock> generate(World worldIn, Random rand, BlockPos position, int x_size, int y_size)
    {

        ArrayList<TemplateBlock> blockList = new ArrayList<>();
        int i = x_size;
        if (i < this.minHeight)
            i = this.minHeight;
        else if (i > this.maxHeight)
            i = this.maxHeight;
        if (rand.nextInt(2) > 0)
        {
            int j = 1 + rand.nextInt(2);
            int k = i - j;
            int l = 2 + rand.nextInt(2);
            boolean flag = true;
            if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight())
            {
                int j3;

                if (!flag)
                {
                    return null;
                }
                else
                {
                    BlockPos down = position.down();
                    blockList.add(new TemplateBlock(EnumFacing.NORTH, down,
                            Blocks.DIRT.getDefaultState()));

                    int i3 = rand.nextInt(2);
                    j3 = 1;
                    int k3 = 0;

                    int j4;
                    int i4;
                    for (i4 = 0; i4 <= k; ++i4)
                    {
                        j4 = position.getY() + i - i4;

                        for (int i2 = position.getX() - i3; i2 <= position.getX() + i3; ++i2)
                        {
                            int j2 = i2 - position.getX();

                            for (int k2 = position.getZ() - i3; k2 <= position.getZ() + i3; ++k2)
                            {
                                int l2 = k2 - position.getZ();
                                if (Math.abs(j2) != i3 || Math.abs(l2) != i3 || i3 <= 0)
                                {
                                    BlockPos blockpos = new BlockPos(i2, j4, k2);
                                    placeLeafAt(worldIn, blockpos, blockList);
                                }
                            }
                        }

                        if (i3 >= j3)
                        {
                            i3 = k3;
                            k3 = 1;
                            ++j3;
                            if (j3 > l)
                                j3 = l;

                        }
                        else
                            ++i3;
                    }

                    i4 = rand.nextInt(3);

                    for (j4 = 0; j4 < i - i4; ++j4)
                    {
                        placeLogAt(worldIn, position.up(j4), blockList);
                    }

                    return blockList;
                }
            }

        }
        else
        {
            int j = i - rand.nextInt(2) - 3;
            int k = i - j;
            int l = 1 + rand.nextInt(k + 1);
            if (position.getY() >= 1 && position.getY() + i + 1 <= 256)
            {
                boolean flag = true;

                int k2;
                int l2;

                if (!flag)
                {
                    return null;
                }
                else
                {
                    BlockPos down = position.down();
                    if (position.getY() < 256 - i - 1)
                    {
                        blockList.add(new TemplateBlock(EnumFacing.NORTH, down,
                                Blocks.DIRT.getDefaultState()));
                        k2 = 0;

                        for (l2 = position.getY() + i; l2 >= position.getY() + j; --l2)
                        {
                            for (int j3 = position.getX() - k2; j3 <= position.getX() + k2; ++j3)
                            {
                                int k3 = j3 - position.getX();

                                for (int i2 = position.getZ() - k2; i2 <= position.getZ() + k2; ++i2)
                                {
                                    int j2 = i2 - position.getZ();
                                    if (Math.abs(k3) != k2 || Math.abs(j2) != k2 || k2 <= 0)
                                    {
                                        BlockPos blockpos = new BlockPos(j3, l2, i2);
                                        placeLeafAt(worldIn, blockpos, blockList);
                                    }
                                }
                            }

                            if (k2 >= 1 && l2 == position.getY() + j + 1)
                            {
                                --k2;
                            }
                            else if (k2 < l)
                            {
                                ++k2;
                            }
                        }

                        for (l2 = 0; l2 < i - 1; ++l2)
                        {
                            BlockPos upN = position.up(l2);
                            placeLogAt(worldIn, position.up(l2), blockList);
                        }

                        return blockList;
                    }
                }
            }
        }


        return null;
    }

    private void placeLogAt(World worldIn, BlockPos pos, ArrayList<TemplateBlock> blockList)
    {
        blockList.add(new TemplateBlock(EnumFacing.NORTH, pos, DEFAULT_TRUNK));

    }

    private void placeLeafAt(World worldIn, BlockPos pos, ArrayList<TemplateBlock> blockList)
    {
        blockList.add(new TemplateBlock(EnumFacing.NORTH, pos, DEFAULT_LEAF));
    }
}
