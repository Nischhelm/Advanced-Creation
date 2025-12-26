package com.deadtiger.advcreation.tree_creator;

import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

//
public class CreateOakTree extends CreateAbstrTree
{

    private final int minTreeHeight;
    private final boolean vinesGrow;
    private final IBlockState metaWood;
    private final IBlockState metaLeaves;

    public static ArrayList<CreateOakTree>  TREE_GEN = new ArrayList<>();
    private static boolean treeGenInitialised = false;

    public CreateOakTree()
    {
        this(4, false, null,null);
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        return false;
    }

    public CreateOakTree(int minTreeHeight, boolean vinesGrow, IBlockState metaWood, IBlockState metaLeaves)
    {
        super();
        this.minTreeHeight = minTreeHeight;
        this.vinesGrow = vinesGrow;
        this.metaWood = metaWood;
        this.metaLeaves = metaLeaves;
    }

    @Override
    public boolean initTreeGen()
    {
        if(super.initTreeGen())
        {
            DEFAULT_TRUNK = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
            DEFAULT_LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockLeaves.CHECK_DECAY, false);
            maxHeight = 7;
            minHeight = 4;
        }

        return false;
    }

    public  ArrayList<TemplateBlock> generate(World worldIn, Random rand, BlockPos position, int x_size,int y_size)
    {

        int i = x_size;
        if(i < this.minTreeHeight)
            i = this.minTreeHeight;
        else if(i > this.minTreeHeight + 3)
            i = this.minTreeHeight + 3;

        boolean flag = true;
        if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
            int l3;
            int i4;

            if (!flag)
                return null;
            else
            {
                ArrayList<TemplateBlock> blockList = new ArrayList<>();
                IBlockState state = worldIn.getBlockState(position.down());
                if (state.getBlock().canSustainPlant(state, worldIn, position.down(), EnumFacing.UP, (BlockSapling)Blocks.SAPLING) && position.getY() < worldIn.getHeight() - i - 1)
                {
                    int k2 = 1;
                    int l2 = 0;

                    int k4;
                    int l4;
                    int i5;
                    BlockPos blockpos3;
                    for(l3 = position.getY() - 3 + i; l3 <= position.getY() + i; ++l3) {
                        i4 = l3 - (position.getY() + i);
                        k4 = 1 - i4 / 2;

                        for(int k1 = position.getX() - k4; k1 <= position.getX() + k4; ++k1) {
                            l4 = k1 - position.getX();

                            for(i5 = position.getZ() - k4; i5 <= position.getZ() + k4; ++i5) {
                                int j2 = i5 - position.getZ();
                                if (Math.abs(l4) != k4 || Math.abs(j2) != k4 || rand.nextInt(2) != 0 && i4 != 0)
                                {
                                    blockpos3 = new BlockPos(k1, l3, i5);
                                    blockList.add(new TemplateBlock(EnumFacing.NORTH,blockpos3,this.DEFAULT_LEAF));
                                }
                            }
                        }
                    }

                    for(l3 = 0; l3 < i; ++l3) {
                        BlockPos upN = position.up(l3);
                        state = worldIn.getBlockState(upN);
                        blockList.add(new TemplateBlock(EnumFacing.NORTH,position.up(l3),this.DEFAULT_TRUNK));
                        if (this.vinesGrow && l3 > 0)
                        {
                            if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(-1, l3, 0)))
                                this.addVine(worldIn, position.add(-1, l3, 0), BlockVine.EAST,blockList);

                            if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(1, l3, 0)))
                                this.addVine(worldIn, position.add(1, l3, 0), BlockVine.WEST,blockList);

                            if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, l3, -1)))
                                this.addVine(worldIn, position.add(0, l3, -1), BlockVine.SOUTH,blockList);

                            if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, l3, 1)))
                                this.addVine(worldIn, position.add(0, l3, 1), BlockVine.NORTH,blockList);

                        }
                    }

                    if (this.vinesGrow)
                    {
                        for(l3 = position.getY() - 3 + i; l3 <= position.getY() + i; ++l3)
                        {
                            i4 = l3 - (position.getY() + i);
                            k4 = 2 - i4 / 2;
                            BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

                            for(l4 = position.getX() - k4; l4 <= position.getX() + k4; ++l4)
                            {
                                for(i5 = position.getZ() - k4; i5 <= position.getZ() + k4; ++i5)
                                {
                                    blockpos$mutableblockpos1.setPos(l4, l3, i5);
                                    state = worldIn.getBlockState(blockpos$mutableblockpos1);
                                    if (state.getBlock().isLeaves(state, worldIn, blockpos$mutableblockpos1))
                                    {
                                        BlockPos blockpos2 = blockpos$mutableblockpos1.west();
                                        blockpos3 = blockpos$mutableblockpos1.east();
                                        BlockPos blockpos4 = blockpos$mutableblockpos1.north();
                                        BlockPos blockpos1 = blockpos$mutableblockpos1.south();
                                        if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos2))
                                            this.addHangingVine(worldIn, blockpos2, BlockVine.EAST,blockList);

                                        if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos3))
                                            this.addHangingVine(worldIn, blockpos3, BlockVine.WEST,blockList);

                                        if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos4))
                                            this.addHangingVine(worldIn, blockpos4, BlockVine.SOUTH,blockList);

                                        if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos1))
                                            this.addHangingVine(worldIn, blockpos1, BlockVine.NORTH,blockList);
                                    }
                                }
                            }
                        }

                        if (rand.nextInt(5) == 0 && i > 5)
                        {
                            for(l3 = 0; l3 < 2; ++l3) {
                                Iterator var23 = EnumFacing.Plane.HORIZONTAL.iterator();

                                while(var23.hasNext())
                                {
                                    EnumFacing enumfacing = (EnumFacing)var23.next();
                                    if (rand.nextInt(4 - l3) == 0)
                                    {
                                        EnumFacing enumfacing1 = enumfacing.getOpposite();
                                        this.placeCocoa(worldIn, rand.nextInt(3), position.add(enumfacing1.getFrontOffsetX(), i - 5 + l3, enumfacing1.getFrontOffsetZ()), enumfacing,blockList);
                                    }
                                }
                            }
                        }
                    }

                    return blockList;
                }
                else
                    return null;

            }
        } else
        {
            return null;
        }
    }

    private void placeCocoa(World worldIn, int p_181652_2_, BlockPos pos, EnumFacing side,ArrayList<TemplateBlock> blockList)
    {
        blockList.add(new TemplateBlock(EnumFacing.NORTH,pos, Blocks.COCOA.getDefaultState().withProperty(BlockCocoa.AGE, p_181652_2_).withProperty(BlockCocoa.FACING, side)));
    }

    private void addVine(World worldIn, BlockPos pos, PropertyBool prop,ArrayList<TemplateBlock> blockList)
    {
        blockList.add(new TemplateBlock(EnumFacing.NORTH,pos, Blocks.VINE.getDefaultState().withProperty(prop, true)));

    }

    private void addHangingVine(World worldIn, BlockPos pos, PropertyBool prop,ArrayList<TemplateBlock> blockList)
    {
        this.addVine(worldIn, pos, prop,blockList);
        int i = 4;

        for(BlockPos blockpos = pos.down(); worldIn.isAirBlock(blockpos) && i > 0; --i)
        {
            this.addVine(worldIn, blockpos, prop, blockList);
            blockpos = blockpos.down();
        }

    }

}
