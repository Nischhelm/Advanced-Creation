package com.deadtiger.advcreation.tree_creator;

import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CreateSavannaTree extends CreateAbstrTree
{

    @Override
    public boolean initTreeGen()
    {
        if (super.initTreeGen())
        {
            this.DEFAULT_LEAF = Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(BlockLeaves.CHECK_DECAY, false);
            this.DEFAULT_TRUNK = Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA);

            this.minHeight = 5;
            this.maxHeight = 11;

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
            i = minHeight;
        else if (i > this.maxHeight)
            i = this.maxHeight;


        boolean flag = true;
        if (position.getY() >= 1 && position.getY() + i + 1 <= 256)
        {
            int k2;
            if (!flag)
                return null;
            else
            {
                BlockPos down = position.down();
                if (position.getY() < worldIn.getHeight() - i - 1)
                {
                    blockList.add(new TemplateBlock(EnumFacing.NORTH, down,
                            Blocks.DIRT.getDefaultState()));
                }

                EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(rand);
                k2 = i - rand.nextInt(4) - 1;
                int l2 = 3 - rand.nextInt(3);
                int i3 = position.getX();
                int j1 = position.getZ();
                int k1 = 0;

                int k3;
                for (int l1 = 0; l1 < i; ++l1)
                {
                    k3 = position.getY() + l1;
                    if (l1 >= k2 && l2 > 0)
                    {
                        i3 += enumfacing.getFrontOffsetX();
                        j1 += enumfacing.getFrontOffsetZ();
                        --l2;
                    }

                    BlockPos blockpos = new BlockPos(i3, k3, j1);
                    this.placeLogAt(worldIn, blockpos, blockList);
                    k1 = k3;
                }

                BlockPos blockpos2 = new BlockPos(i3, k1, j1);

                int l3;
                for (k3 = -3; k3 <= 3; ++k3)
                {
                    for (l3 = -3; l3 <= 3; ++l3)
                    {
                        if (Math.abs(k3) != 3 || Math.abs(l3) != 3)
                        {
                            this.placeLeafAt(worldIn, blockpos2.add(k3, 0, l3), blockList);
                        }
                    }
                }

                blockpos2 = blockpos2.up();

                for (k3 = -1; k3 <= 1; ++k3)
                {
                    for (l3 = -1; l3 <= 1; ++l3)
                    {
                        this.placeLeafAt(worldIn, blockpos2.add(k3, 0, l3), blockList);
                    }
                }

                this.placeLeafAt(worldIn, blockpos2.east(2), blockList);
                this.placeLeafAt(worldIn, blockpos2.west(2), blockList);
                this.placeLeafAt(worldIn, blockpos2.south(2), blockList);
                this.placeLeafAt(worldIn, blockpos2.north(2), blockList);
                i3 = position.getX();
                j1 = position.getZ();
                EnumFacing enumfacing1 = EnumFacing.Plane.HORIZONTAL.random(rand);
                if (enumfacing1 != enumfacing)
                {
                    l3 = k2 - rand.nextInt(2) - 1;
                    int k4 = 1 + rand.nextInt(3);
                    k1 = 0;

                    int j5;
                    for (int l4 = l3; l4 < i && k4 > 0; --k4)
                    {
                        if (l4 >= 1)
                        {
                            j5 = position.getY() + l4;
                            i3 += enumfacing1.getFrontOffsetX();
                            j1 += enumfacing1.getFrontOffsetZ();
                            BlockPos blockpos1 = new BlockPos(i3, j5, j1);
                            this.placeLogAt(worldIn, blockpos1, blockList);
                            k1 = j5;

                        }
                        ++l4;
                    }

                    if (k1 > 0)
                    {
                        BlockPos blockpos3 = new BlockPos(i3, k1, j1);

                        int l5;
                        for (j5 = -2; j5 <= 2; ++j5)
                        {
                            for (l5 = -2; l5 <= 2; ++l5)
                            {
                                if (Math.abs(j5) != 2 || Math.abs(l5) != 2)
                                    this.placeLeafAt(worldIn, blockpos3.add(j5, 0, l5), blockList);
                            }
                        }

                        blockpos3 = blockpos3.up();

                        for (j5 = -1; j5 <= 1; ++j5)
                        {
                            for (l5 = -1; l5 <= 1; ++l5)
                            {
                                this.placeLeafAt(worldIn, blockpos3.add(j5, 0, l5), blockList);
                            }
                        }
                    }
                }

                return blockList;
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
        IBlockState state = worldIn.getBlockState(pos);
        blockList.add(new TemplateBlock(EnumFacing.NORTH, pos, DEFAULT_LEAF));
    }
}
