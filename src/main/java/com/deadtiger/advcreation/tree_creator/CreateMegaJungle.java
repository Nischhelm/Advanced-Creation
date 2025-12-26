package com.deadtiger.advcreation.tree_creator;

import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CreateMegaJungle extends CreateHugeTree
{
    @Override
    public boolean initTreeGen() {
        if(super.initTreeGen())
        {
            DEFAULT_LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
            DEFAULT_TRUNK = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);;
            minHeight = 10;
            maxHeight = 20;
        }
        return true;
    }

    @Override
    public ArrayList<TemplateBlock> generate(World worldIn, Random rand, BlockPos position, int x_size, int y_size) {

        int i = this.getHeight(rand,x_size);
        ArrayList<TemplateBlock> blockList = new ArrayList<>();
        if (!this.ensureGrowable(worldIn, rand, position, i, blockList))
            return null;
        else
        {
            this.createCrown(worldIn, position.up(i), 2,blockList);

            for (int j = position.getY() + i - 2 - rand.nextInt(4); j > position.getY() + i / 2; j -= 2 + rand.nextInt(4))
            {
                float f = rand.nextFloat() * ((float)Math.PI * 2F);
                int k = position.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
                int l = position.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

                for (int i1 = 0; i1 < 5; ++i1)
                {
                    k = position.getX() + (int)(1.5F + MathHelper.cos(f) * (float)i1);
                    l = position.getZ() + (int)(1.5F + MathHelper.sin(f) * (float)i1);
//                    this.setBlockAndNotifyAdequately(worldIn, new BlockPos(k, j - 3 + i1 / 2, l), DEFAULT_TRUNK);
                    blockList.add(new TemplateBlock(EnumFacing.NORTH, new BlockPos(k, j - 3 + i1 / 2, l),DEFAULT_TRUNK));
                }

                int j2 = 1 + rand.nextInt(2);
                int j1 = j;

                for (int k1 = j - j2; k1 <= j1; ++k1)
                {
                    int l1 = k1 - j1;
                    this.growLeavesLayer(worldIn, new BlockPos(k, k1, l), 1 - l1,blockList);
                }
            }

            for (int i2 = 0; i2 < i; ++i2)
            {
                BlockPos blockpos = position.up(i2);

                if (this.isAirLeaves(worldIn,blockpos))
                {
                   blockList.add(new TemplateBlock(EnumFacing.NORTH,blockpos,DEFAULT_TRUNK));

                    if (i2 > 0)
                    {
                        this.placeVine(worldIn, rand, blockpos.west(), BlockVine.EAST,blockList);
                        this.placeVine(worldIn, rand, blockpos.north(), BlockVine.SOUTH,blockList);
                    }
                }

                if (i2 < i - 1)
                {
                    BlockPos blockpos1 = blockpos.east();

                    if (this.isAirLeaves(worldIn,blockpos1))
                    {
                       blockList.add(new TemplateBlock(EnumFacing.NORTH,blockpos1,DEFAULT_TRUNK));

                        if (i2 > 0)
                        {
                            this.placeVine(worldIn, rand, blockpos1.east(), BlockVine.WEST,blockList);
                            this.placeVine(worldIn, rand, blockpos1.north(), BlockVine.SOUTH,blockList);
                        }
                    }

                    BlockPos blockpos2 = blockpos.south().east();

                    if (this.isAirLeaves(worldIn,blockpos2))
                    {
                       blockList.add(new TemplateBlock(EnumFacing.NORTH,blockpos2,DEFAULT_TRUNK));

                        if (i2 > 0)
                        {
                            this.placeVine(worldIn, rand, blockpos2.east(), BlockVine.WEST,blockList);
                            this.placeVine(worldIn, rand, blockpos2.south(), BlockVine.NORTH,blockList);
                        }
                    }

                    BlockPos blockpos3 = blockpos.south();

                    if (this.isAirLeaves(worldIn,blockpos3))
                    {
                       blockList.add(new TemplateBlock(EnumFacing.NORTH,blockpos3,DEFAULT_TRUNK));

                        if (i2 > 0)
                        {
                            this.placeVine(worldIn, rand, blockpos3.west(), BlockVine.EAST,blockList);
                            this.placeVine(worldIn, rand, blockpos3.south(), BlockVine.NORTH,blockList);
                        }
                    }
                }
            }

            return blockList;
        }
    }

    private void placeVine(World p_181632_1_, Random p_181632_2_, BlockPos p_181632_3_, PropertyBool p_181632_4_,ArrayList<TemplateBlock> blockList)
    {
        if (p_181632_2_.nextInt(3) > 0 && p_181632_1_.isAirBlock(p_181632_3_))
        {
            blockList.add(new TemplateBlock(EnumFacing.NORTH,p_181632_3_, Blocks.VINE.getDefaultState().withProperty(p_181632_4_, Boolean.valueOf(true))));
        }
    }

    private void createCrown(World worldIn, BlockPos p_175930_2_, int p_175930_3_, ArrayList<TemplateBlock> blockList)
    {
        int i = 2;

        for (int j = -2; j <= 0; ++j)
        {
            this.growLeavesLayerStrict(worldIn, p_175930_2_.up(j), p_175930_3_ + 1 - j,blockList);
        }
    }

    //Helper macro
    private boolean isAirLeaves(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos);
    }
}
