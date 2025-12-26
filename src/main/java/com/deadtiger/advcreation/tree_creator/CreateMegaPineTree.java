package com.deadtiger.advcreation.tree_creator;

import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CreateMegaPineTree extends CreateHugeTree
{
    private static final IBlockState PODZOL = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);


    @Override
    public boolean initTreeGen() {
        if(super.initTreeGen())
        {
            this.DEFAULT_LEAF=Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
            this.DEFAULT_TRUNK = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);

            this.minHeight=13;
            this.maxHeight=28;
        }
        return true;
    }
    @Override
    public ArrayList<TemplateBlock> generate(World worldIn, Random rand, BlockPos position, int x_size, int y_size) {

        ArrayList<TemplateBlock> blockList = new ArrayList<>();
        int i = this.getHeight(rand,x_size);
        boolean flag = true;
        if (!flag)
            return null;
        else
        {
            this.createCrown(worldIn, position.getX(), position.getZ(), position.getY() + i, 0, rand,blockList);

            for (int j = 0; j < i; ++j)
            {
                blockList.add(new TemplateBlock(EnumFacing.NORTH,position.up(j),DEFAULT_TRUNK));
                if (j < i - 1)
                {
                    blockList.add(new TemplateBlock(EnumFacing.NORTH, position.add(1, j, 0),DEFAULT_TRUNK));
                    blockList.add(new TemplateBlock(EnumFacing.NORTH, position.add(1, j, 1),DEFAULT_TRUNK));
                    blockList.add(new TemplateBlock(EnumFacing.NORTH, position.add(0, j, 1),DEFAULT_TRUNK));
                }
            }

            return blockList;
        }
    }

    private void createCrown(World worldIn, int x, int z, int y, int p_150541_5_, Random rand, ArrayList<TemplateBlock> blockList)
    {
        int i = rand.nextInt(5) + 3;
        int j = 0;

        for (int k = y - i; k <= y; ++k)
        {
            int l = y - k;
            int i1 = p_150541_5_ + MathHelper.floor((float)l / (float)i * 3.5F);
            this.growLeavesLayerStrict(worldIn, new BlockPos(x, k, z), i1 + (l > 0 && i1 == j && (k & 1) == 0 ? 1 : 0),blockList);
            j = i1;
        }
    }

    public void generateSaplings(World worldIn, Random random, BlockPos pos)
    {
        this.placePodzolCircle(worldIn, pos.west().north());
        this.placePodzolCircle(worldIn, pos.east(2).north());
        this.placePodzolCircle(worldIn, pos.west().south(2));
        this.placePodzolCircle(worldIn, pos.east(2).south(2));

        for (int i = 0; i < 5; ++i)
        {
            int j = random.nextInt(64);
            int k = j % 8;
            int l = j / 8;

            if (k == 0 || k == 7 || l == 0 || l == 7)
            {
                this.placePodzolCircle(worldIn, pos.add(-3 + k, 0, -3 + l));
            }
        }
    }

    private void placePodzolCircle(World worldIn, BlockPos center)
    {
        for (int i = -2; i <= 2; ++i)
        {
            for (int j = -2; j <= 2; ++j)
            {
                if (Math.abs(i) != 2 || Math.abs(j) != 2)
                {
                    this.placePodzolAt(worldIn, center.add(i, 0, j));
                }
            }
        }
    }

    private void placePodzolAt(World worldIn, BlockPos pos)
    {
        for (int i = 2; i >= -3; --i)
        {
            BlockPos blockpos = pos.up(i);
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            if (block.canSustainPlant(iblockstate, worldIn, blockpos, EnumFacing.UP, ((BlockSapling)Blocks.SAPLING)))
            {
                this.setBlockAndNotifyAdequately(worldIn, blockpos, PODZOL);
                break;
            }

            if (iblockstate.getMaterial() != Material.AIR && i < 0)
                break;
        }
    }

    //Helper macro
    private boolean isAirLeaves(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos);
    }

}
