package com.deadtiger.advcreation.tree_creator;

import com.deadtiger.advcreation.template.TemplateBlock;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CreateBigTree extends CreateAbstrTree
{
    private Random rand;
    private World world;
    private BlockPos basePos;
    int heightLimit;
    int height;
    double heightAttenuation;
    double branchSlope;
    double scaleWidth;
    double leafDensity;
    int trunkSize;
    int heightLimitLimit;
    int leafDistanceLimit;
    List<FoliageCoordinates> foliageCoords;

    static boolean bigTreeGenInitialised = false;

    public static CreateBigTree instance;

    public CreateBigTree()
    {
        super();
        this.basePos = BlockPos.ORIGIN;
        this.heightAttenuation = 0.618D;
        this.branchSlope = 0.381D;
        this.scaleWidth = 1.0D;
        this.leafDensity = 1.0D;
        this.trunkSize = 1;
        this.heightLimitLimit = 12;
        this.leafDistanceLimit = 4;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        return false;
    }

    public static void initBigTree()
    {
        if (!bigTreeGenInitialised)
        {
            instance = new CreateBigTree();
            bigTreeGenInitialised = true;
        }

    }


    @Override
    public boolean initTreeGen()
    {
        if (super.initTreeGen())
        {
            DEFAULT_LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false);
            DEFAULT_TRUNK = Blocks.LOG.getDefaultState();
            this.minHeight = 8;
            this.maxHeight = 17;
        }
        return false;
    }

    public ArrayList<TemplateBlock> generate(World worldIn, Random rand, BlockPos position, int x_size, int y_size)
    {
        ArrayList<TemplateBlock> blockList = new ArrayList<>();
        this.world = worldIn;
        this.basePos = position;
        this.rand = new Random(rand.nextLong());
        this.heightLimit = x_size;
        if (this.heightLimit > this.maxHeight)
            this.heightLimit = this.maxHeight;

        this.generateLeafNodeList();
        this.generateLeaves(blockList);
        this.generateTrunk(blockList);
        this.generateLeafNodeBases(blockList);
        this.world = null;
        return blockList;
    }


    void generateLeafNodeList()
    {
        this.height = (int) ((double) this.heightLimit * this.heightAttenuation);
        if (this.height >= this.heightLimit)
        {
            this.height = this.heightLimit - 1;
        }

        int i = (int) (1.382D + Math.pow(this.leafDensity * (double) this.heightLimit / 13.0D, 2.0D));
        if (i < 1)
        {
            i = 1;
        }

        int j = this.basePos.getY() + this.height;
        int k = this.heightLimit - this.leafDistanceLimit;
        this.foliageCoords = Lists.newArrayList();
        this.foliageCoords.add(new FoliageCoordinates(this.basePos.up(k), j));

        for (; k >= 0; --k)
        {
            float f = this.layerSize(k);
            if (f >= 0.0F)
            {
                for (int l = 0; l < i; ++l)
                {
                    double d0 = this.scaleWidth * (double) f * ((double) this.rand.nextFloat() + 0.328D);
                    double d1 = (double) (this.rand.nextFloat() * 2.0F) * 3.141592653589793D;
                    double d2 = d0 * Math.sin(d1) + 0.5D;
                    double d3 = d0 * Math.cos(d1) + 0.5D;
                    BlockPos blockpos = this.basePos.add(d2, (double) (k - 1), d3);
                    BlockPos blockpos1 = blockpos.up(this.leafDistanceLimit);
                    if (this.checkBlockLine(blockpos, blockpos1) == -1)
                    {
                        int i1 = this.basePos.getX() - blockpos.getX();
                        int j1 = this.basePos.getZ() - blockpos.getZ();
                        double d4 = (double) blockpos.getY() - Math.sqrt((double) (i1 * i1 + j1 * j1)) * this.branchSlope;
                        int k1 = d4 > (double) j ? j : (int) d4;
                        BlockPos blockpos2 = new BlockPos(this.basePos.getX(), k1, this.basePos.getZ());
                        if (this.checkBlockLine(blockpos2, blockpos) == -1)
                        {
                            this.foliageCoords.add(new FoliageCoordinates(blockpos, blockpos2.getY()));
                        }
                    }
                }
            }
        }

    }

    void crosSection(BlockPos pos, float p_181631_2_, IBlockState p_181631_3_, ArrayList<TemplateBlock> blockList)
    {
        int i = (int) ((double) p_181631_2_ + 0.618D);

        for (int j = -i; j <= i; ++j)
        {
            for (int k = -i; k <= i; ++k)
            {
                if (Math.pow((double) Math.abs(j) + 0.5D, 2.0D) + Math.pow((double) Math.abs(k) + 0.5D, 2.0D) <= (double) (p_181631_2_ * p_181631_2_))
                {
                    BlockPos blockpos = pos.add(j, 0, k);
                    blockList.add(new TemplateBlock(EnumFacing.NORTH, blockpos, p_181631_3_));
                }
            }
        }

    }

    float layerSize(int y)
    {
        if ((float) y < (float) this.heightLimit * 0.3F)
        {
            return -1.0F;
        }
        else
        {
            float f = (float) this.heightLimit / 2.0F;
            float f1 = f - (float) y;
            float f2 = MathHelper.sqrt(f * f - f1 * f1);
            if (f1 == 0.0F)
            {
                f2 = f;
            }
            else if (Math.abs(f1) >= f)
            {
                return 0.0F;
            }

            return f2 * 0.5F;
        }
    }

    float leafSize(int y)
    {
        if (y >= 0 && y < this.leafDistanceLimit)
        {
            return y != 0 && y != this.leafDistanceLimit - 1 ? 3.0F : 2.0F;
        }
        else
        {
            return -1.0F;
        }
    }

    void generateLeafNode(BlockPos pos, ArrayList<TemplateBlock> blockList)
    {
        for (int i = 0; i < this.leafDistanceLimit; ++i)
        {
            this.crosSection(pos.up(i), this.leafSize(i), DEFAULT_LEAF, blockList);
        }
    }

    void limb(BlockPos p_175937_1_, BlockPos p_175937_2_, ArrayList<TemplateBlock> blockList)
    {
        BlockPos blockpos = p_175937_2_.add(-p_175937_1_.getX(), -p_175937_1_.getY(), -p_175937_1_.getZ());
        int i = this.getGreatestDistance(blockpos);
        float f = (float) blockpos.getX() / (float) i;
        float f1 = (float) blockpos.getY() / (float) i;
        float f2 = (float) blockpos.getZ() / (float) i;

        for (int j = 0; j <= i; ++j)
        {
            BlockPos blockpos1 = p_175937_1_.add((double) (0.5F + (float) j * f), (double) (0.5F + (float) j * f1), (double) (0.5F + (float) j * f2));
            BlockLog.EnumAxis blocklog$enumaxis = this.getLogAxis(p_175937_1_, blockpos1);
            blockList.add(new TemplateBlock(EnumFacing.NORTH, blockpos1, DEFAULT_TRUNK.withProperty(BlockLog.LOG_AXIS, blocklog$enumaxis)));
        }

    }

    private int getGreatestDistance(BlockPos posIn)
    {
        int i = MathHelper.abs(posIn.getX());
        int j = MathHelper.abs(posIn.getY());
        int k = MathHelper.abs(posIn.getZ());
        if (k > i && k > j)
        {
            return k;
        }
        else
        {
            return j > i ? j : i;
        }
    }

    private BlockLog.EnumAxis getLogAxis(BlockPos p_175938_1_, BlockPos p_175938_2_)
    {
        BlockLog.EnumAxis blocklog$enumaxis = BlockLog.EnumAxis.Y;
        int i = Math.abs(p_175938_2_.getX() - p_175938_1_.getX());
        int j = Math.abs(p_175938_2_.getZ() - p_175938_1_.getZ());
        int k = Math.max(i, j);
        if (k > 0)
        {
            if (i == k)
                blocklog$enumaxis = BlockLog.EnumAxis.X;
            else if (j == k)
                blocklog$enumaxis = BlockLog.EnumAxis.Z;

        }

        return blocklog$enumaxis;
    }

    void generateLeaves(ArrayList<TemplateBlock> blockList)
    {
        Iterator var1 = this.foliageCoords.iterator();

        while (var1.hasNext())
        {
            FoliageCoordinates worldgenbigtree$foliagecoordinates = (FoliageCoordinates) var1.next();
            this.generateLeafNode(worldgenbigtree$foliagecoordinates, blockList);
        }

    }

    boolean leafNodeNeedsBase(int p_76493_1_)
    {
        return (double) p_76493_1_ >= (double) this.heightLimit * 0.2D;
    }

    void generateTrunk(ArrayList<TemplateBlock> blockList)
    {
        BlockPos blockpos = this.basePos;
        BlockPos blockpos1 = this.basePos.up(this.height);
        Block block = Blocks.LOG;
        this.limb(blockpos, blockpos1, blockList);
        if (this.trunkSize == 2)
        {
            this.limb(blockpos.east(), blockpos1.east(), blockList);
            this.limb(blockpos.east().south(), blockpos1.east().south(), blockList);
            this.limb(blockpos.south(), blockpos1.south(), blockList);
        }

    }

    void generateLeafNodeBases(ArrayList<TemplateBlock> blockList)
    {
        Iterator var1 = this.foliageCoords.iterator();

        while (var1.hasNext())
        {
            FoliageCoordinates foliagecoordinates = (FoliageCoordinates) var1.next();
            int i = foliagecoordinates.getBranchBase();
            BlockPos blockpos = new BlockPos(this.basePos.getX(), i, this.basePos.getZ());
            if (!blockpos.equals(foliagecoordinates) && this.leafNodeNeedsBase(i - this.basePos.getY()))
            {
                this.limb(blockpos, foliagecoordinates, blockList);
            }
        }

    }

    int checkBlockLine(BlockPos posOne, BlockPos posTwo)
    {
        BlockPos blockpos = posTwo.add(-posOne.getX(), -posOne.getY(), -posOne.getZ());
        int i = this.getGreatestDistance(blockpos);
        float f = (float) blockpos.getX() / (float) i;
        float f1 = (float) blockpos.getY() / (float) i;
        float f2 = (float) blockpos.getZ() / (float) i;
        if (i == 0)
        {
            return -1;
        }
        else
        {
            for (int j = 0; j <= i; ++j)
            {
                BlockPos blockpos1 = posOne.add((double) (0.5F + (float) j * f), (double) (0.5F + (float) j * f1), (double) (0.5F + (float) j * f2));
                if (!this.isReplaceable(this.world, blockpos1))
                {
                    return j;
                }
            }

            return -1;
        }
    }

    public void setDecorationDefaults()
    {
        this.leafDistanceLimit = 5;
    }


    private boolean validTreeLocation()
    {

        int i = this.checkBlockLine(this.basePos, this.basePos.up(this.heightLimit - 1));
        if (i == -1)
            return true;
        else if (i < 6)
            return false;
        else
        {
            this.heightLimit = i;
            return true;
        }
    }

    static class FoliageCoordinates extends BlockPos
    {
        private final int branchBase;

        public FoliageCoordinates(BlockPos pos, int p_i45635_2_)
        {
            super(pos.getX(), pos.getY(), pos.getZ());
            this.branchBase = p_i45635_2_;
        }

        public int getBranchBase()
        {
            return this.branchBase;
        }
    }

}
