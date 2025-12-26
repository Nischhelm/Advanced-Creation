package com.deadtiger.advcreation.tree_creator;

import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.ArrayList;
import java.util.Random;

public abstract class CreateAbstrTree extends WorldGenAbstractTree
{

    public IBlockState DEFAULT_TRUNK;
    public IBlockState DEFAULT_LEAF;
    public static ArrayList<CreateAbstrTree> GEN_TREES = new ArrayList<>();
    public static boolean CREATED_TREE_CREATORS = false;

    public int minHeight = 0;
    public int maxHeight = 100;

    public boolean initialised = false;

    public CreateAbstrTree()
    {
        super(false);
    }

    public static void createTreeCreators()
    {
        if (!CREATED_TREE_CREATORS)
        {
            GEN_TREES.add(new CreateBigTree());
            GEN_TREES.add(new CreateOakTree());
            GEN_TREES.add(new CreateBirchTree());
            GEN_TREES.add(new CreateCanopyTree());
            GEN_TREES.add(new CreateMegaJungle());
            GEN_TREES.add(new CreateMegaPineTree());
            GEN_TREES.add(new CreateSavannaTree());
            GEN_TREES.add(new CreateTaiga2tree());
            CREATED_TREE_CREATORS = true;
        }
    }


    public boolean initTreeGen()
    {
        if (!initialised)
        {
            initialised = true;
            return true;
        }
        else
            return false;
    }

    public static CreateAbstrTree getRightTreeGenerator(IBlockState state, int height)
    {
        ArrayList<CreateAbstrTree> resList = new ArrayList<>();
        for (CreateAbstrTree abstrTree : GEN_TREES)
        {
            if (abstrTree.stateIsPartOfTree(state))
            {
                resList.add(abstrTree);
            }
        }
        if (resList.isEmpty())
            return null;
        else if (resList.size() == 1)
            return resList.get(0);
        else
        {
            return getAbstrTreeCreatorClosestToHeight(height, resList);
        }
    }


    private static CreateAbstrTree getAbstrTreeCreatorClosestToHeight(int height, ArrayList<CreateAbstrTree> resList)
    {
        int closestValue = 1000;
        CreateAbstrTree resTreeGen = null;
        for (CreateAbstrTree abstrTree : resList)
        {
            if (abstrTree.heightInRange(height))
                return abstrTree;
            int diff = Math.abs(abstrTree.maxHeight - height);
            if (diff < closestValue)
            {
                closestValue = diff;
                resTreeGen = abstrTree;
            }
        }
        return resTreeGen;
    }

    public abstract ArrayList<TemplateBlock> generate(World worldIn, Random rand, BlockPos position, int x_size, int y_size);


    public boolean stateIsPartOfTree(IBlockState state)
    {
        if (state.getBlock() instanceof BlockOldLog && this.DEFAULT_TRUNK.getBlock() instanceof BlockOldLog)
            return (state.getValue(BlockOldLog.VARIANT).equals(this.DEFAULT_TRUNK.getValue(BlockOldLog.VARIANT)));
        else if (state.getBlock() instanceof BlockOldLeaf && this.DEFAULT_LEAF.getBlock() instanceof BlockOldLeaf)
            return (state.getValue(BlockOldLeaf.VARIANT).equals(this.DEFAULT_LEAF.getValue(BlockOldLeaf.VARIANT)));
        else if (state.getBlock() instanceof BlockNewLog && this.DEFAULT_TRUNK.getBlock() instanceof BlockNewLog)
            return (state.getValue(BlockNewLog.VARIANT).equals(this.DEFAULT_TRUNK.getValue(BlockNewLog.VARIANT)));
        else if (state.getBlock() instanceof BlockNewLeaf && this.DEFAULT_LEAF.getBlock() instanceof BlockNewLog)
            return (state.getValue(BlockNewLeaf.VARIANT).equals(this.DEFAULT_LEAF.getValue(BlockNewLeaf.VARIANT)));

        return false;
    }

    public boolean heightInRange(int height)
    {
        return (height < maxHeight && height >= minHeight);
    }

    public static ArrayList<TemplateBlock> addPosToTempateBlockList(ArrayList<TemplateBlock> blockList, BlockPos pos)
    {
        ArrayList<TemplateBlock> res = new ArrayList<>();
        for (TemplateBlock block : blockList)
        {
            res.add(new TemplateBlock(block.getFace(), block.getBlockPos().add(pos), block.getBlockState()));
        }
        return res;
    }
}
