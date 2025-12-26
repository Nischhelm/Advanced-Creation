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

public class CreateCanopyTree extends CreateAbstrTree {

    @Override
    public boolean initTreeGen() {
        if( super.initTreeGen())
        {
            DEFAULT_LEAF =  Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK).withProperty(BlockLeaves.CHECK_DECAY, false);
            DEFAULT_TRUNK =Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK);
            minHeight =6;
            maxHeight = 11;
        }
        return true;
    }

    @Override
    public ArrayList<TemplateBlock> generate(World worldIn, Random rand, BlockPos position, int x_size, int y_size) {
        ArrayList<TemplateBlock> blockList = new ArrayList<>();

        int i = x_size;
        if(i < minHeight)
            i = minHeight;
        else if(i > maxHeight)
            i = maxHeight;


        int j = position.getX();
        int k = position.getY();
        int l = position.getZ();
        if (k >= 1 && k + i + 1 < 256) {
            BlockPos blockpos = position.down();
            if ( position.getY() < worldIn.getHeight() - i - 1)
            {
                boolean flag = true;
                if (!flag)
                    return null;
                else
                {
                    this.onPlantGrow(worldIn, blockpos, position,blockList);
                    this.onPlantGrow(worldIn, blockpos.east(), position,blockList);
                    this.onPlantGrow(worldIn, blockpos.south(), position,blockList);
                    this.onPlantGrow(worldIn, blockpos.south().east(), position,blockList);
                    EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(rand);
                    int i1 = i - rand.nextInt(4);
                    int j1 = 2 - rand.nextInt(3);
                    int k1 = j;
                    int l1 = l;
                    int i2 = k + i - 1;

                    int k3;
                    int j4;
                    for (k3 = 0; k3 < i; ++k3)
                    {
                        if (k3 >= i1 && j1 > 0)
                        {
                            k1 += enumfacing.getXOffset();
                            l1 += enumfacing.getZOffset();
                            --j1;
                        }

                        j4 = k + k3;
                        BlockPos blockpos1 = new BlockPos(k1, j4, l1);
                        this.placeLogAt(worldIn, blockpos1,blockList);
                        this.placeLogAt(worldIn, blockpos1.east(),blockList);
                        this.placeLogAt(worldIn, blockpos1.south(),blockList);
                        this.placeLogAt(worldIn, blockpos1.east().south(),blockList);
                    }

                    for (k3 = -2; k3 <= 0; ++k3) {
                        for (j4 = -2; j4 <= 0; ++j4) {
                            int k4 = -1;
                            this.placeLeafAt(worldIn, k1 + k3, i2 + k4, l1 + j4,blockList);
                            this.placeLeafAt(worldIn, 1 + k1 - k3, i2 + k4, l1 + j4,blockList);
                            this.placeLeafAt(worldIn, k1 + k3, i2 + k4, 1 + l1 - j4,blockList);
                            this.placeLeafAt(worldIn, 1 + k1 - k3, i2 + k4, 1 + l1 - j4,blockList);
                            if ((k3 > -2 || j4 > -1) && (k3 != -1 || j4 != -2)) {
                                 k4 = 1;
                                this.placeLeafAt(worldIn, k1 + k3, i2 + k4, l1 + j4,blockList);
                                this.placeLeafAt(worldIn, 1 + k1 - k3, i2 + k4, l1 + j4,blockList);
                                this.placeLeafAt(worldIn, k1 + k3, i2 + k4, 1 + l1 - j4,blockList);
                                this.placeLeafAt(worldIn, 1 + k1 - k3, i2 + k4, 1 + l1 - j4,blockList);
                            }
                        }
                    }

                    if (rand.nextBoolean()) {
                        this.placeLeafAt(worldIn, k1, i2 + 2, l1,blockList);
                        this.placeLeafAt(worldIn, k1 + 1, i2 + 2, l1,blockList);
                        this.placeLeafAt(worldIn, k1 + 1, i2 + 2, l1 + 1,blockList);
                        this.placeLeafAt(worldIn, k1, i2 + 2, l1 + 1,blockList);
                    }

                    for (k3 = -3; k3 <= 4; ++k3)
                    {
                        for (j4 = -3; j4 <= 4; ++j4)
                        {
                            if ((k3 != -3 || j4 != -3) && (k3 != -3 || j4 != 4) && (k3 != 4 || j4 != -3) && (k3 != 4 || j4 != 4) && (Math.abs(k3) < 3 || Math.abs(j4) < 3))
                                this.placeLeafAt(worldIn, k1 + k3, i2, l1 + j4,blockList);
                        }
                    }

                    for (k3 = -1; k3 <= 2; ++k3)
                    {
                        for (j4 = -1; j4 <= 2; ++j4)
                        {
                            if ((k3 < 0 || k3 > 1 || j4 < 0 || j4 > 1) && rand.nextInt(3) <= 0)
                            {
                                int l4 = rand.nextInt(3) + 2;

                                int k5;
                                for (k5 = 0; k5 < l4; ++k5)
                                {
                                    this.placeLogAt(worldIn, new BlockPos(j + k3, i2 - k5 - 1, l + j4),blockList);
                                }

                                int l5;
                                for (k5 = -1; k5 <= 1; ++k5)
                                {
                                    for (l5 = -1; l5 <= 1; ++l5)
                                    {
                                        this.placeLeafAt(worldIn, k1 + k3 + k5, i2, l1 + j4 + l5,blockList);
                                    }
                                }

                                for (k5 = -2; k5 <= 2; ++k5)
                                {
                                    for (l5 = -2; l5 <= 2; ++l5)
                                    {
                                        if (Math.abs(k5) != 2 || Math.abs(l5) != 2)
                                            this.placeLeafAt(worldIn, k1 + k3 + k5, i2 - 1, l1 + j4 + l5,blockList);
                                    }
                                }
                            }
                        }
                    }

                    return blockList;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    public boolean generate(World worldIn, Random rand, BlockPos position) {
        return false;
    }

    private boolean placeTreeOfHeight(World worldIn, BlockPos pos, int height) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int l = 0; l <= height + 1; ++l)
        {
            int i1 = 1;
            if (l == 0)
            {
                i1 = 0;
            }

            if (l >= height - 1)
            {
                i1 = 2;
            }

            for (int j1 = -i1; j1 <= i1; ++j1)
            {
                for (int k1 = -i1; k1 <= i1; ++k1)
                {
                    if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.setPos(i + j1, j + l, k + k1)))
                        return false;
                }
            }
        }

        return true;
    }

    private void placeLogAt(World worldIn, BlockPos pos,ArrayList<TemplateBlock> blockList)
    {
            blockList.add(new TemplateBlock(EnumFacing.NORTH,pos,DEFAULT_TRUNK));
    }

    private void placeLeafAt(World worldIn, int x, int y, int z,ArrayList<TemplateBlock> blockList)
    {
        BlockPos blockpos = new BlockPos(x, y, z);
        IBlockState state = worldIn.getBlockState(blockpos);
        if (state.getBlock().isAir(state, worldIn, blockpos))
        {
            blockList.add(new TemplateBlock(EnumFacing.NORTH,blockpos,DEFAULT_LEAF));
        }
    }

    private void onPlantGrow(World world, BlockPos pos, BlockPos source,ArrayList<TemplateBlock> blockList)
    {
        blockList.add(new TemplateBlock(EnumFacing.NORTH,pos, Blocks.DIRT.getDefaultState()));
    }
}


