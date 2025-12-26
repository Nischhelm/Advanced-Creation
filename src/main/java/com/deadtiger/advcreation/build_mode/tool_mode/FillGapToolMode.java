package com.deadtiger.advcreation.build_mode.tool_mode;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;

public class FillGapToolMode extends BaseToolMode
{
    public static HashMap<BlockPos,TemplateBlock> relToAbsBlockPosMap = new HashMap<>();
    public boolean alreadyRotated=false;
    
    public FillGapToolMode()
    {
        this.finalRightClick = 0;
        this.toolModeName = "FillGap Mode";
        this.buttonText = "FILLGAP";
        this.tooltipText = "Fill in a gap in the chosen direction";
        this.identificationIndex = 9;
    }
    
    @Override
    public void addNewBlockRightClick0(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
//        normalHitPos = block.getBlockPos();
//        Vec3d newHitvec = hitVec.add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);
//        TemplateBlock newBlock = new TemplateBlock(block.getFace(),block.getBlockPos().add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET),block.getBlockState());

        Vec3d newHitvec = hitVec;
        TemplateBlock newBlock = block;

        BuildMode.setStartVec(newHitvec);
        BuildMode.setStartBlock(newBlock);
        BuildMode.updateCurrToolBlocks(newBlock,newHitvec);
    }

    @Override
    public void addNewBlockRightClick1(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {

    }

    @Override
    public void updateBlocksRightClick0(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        relToAbsBlockPosMap.clear();
        alreadyRotated = false;
        this.applyRotation();
        calcAdjacentAirBlocksInDir(BuildMode.WORK_DIRECTION_MODE,BuildMode.START_BLOCK.getBlockPos(),Minecraft.getMinecraft());

        BuildMode.CURR_TOOL_BLOCKS.addAll(relToAbsBlockPosMap.values());
        BuildMode.CURR_PREVIEW_BLOCKS_LOCK.blockingAttemptAtLocking();
        updatePreviewBlocks();
        BuildMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();

    }

    @Override
    public void applyRotation()
    {
        //make sure that rotations arent applied 2x
        //but rotation still need to applied before filling up the currtoolblocks list
        if(!alreadyRotated)
        {
            super.applyRotation();
            alreadyRotated = true;
        }
    }

    @Override
    protected void drawOffsetIndication(EntityPlayer entityplayer, Float partialTicks)
    {
        if(normalHitPos != null)
        {
            RenderSelectionHighlight.drawFadingBlocksToPos(entityplayer, partialTicks,normalHitPos,BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);
            RenderSelectionHighlight.drawWhiteBlockHighlight(entityplayer,normalHitPos,partialTicks);

        }
    }

    private void updatePreviewBlocks()
    {
        BuildMode.CURR_PREVIEW_BLOCKS.clear();
        BuildMode.CURR_PREVIEW_BLOCKS.add(BuildMode.START_BLOCK);
        for(BlockPos pos: relToAbsBlockPosMap.keySet())
        {
            if(pos.equals(BlockPos.ORIGIN))
                continue;
            BuildMode.CURR_PREVIEW_BLOCKS.add(new TemplateBlock(BuildMode.START_BLOCK.getFace(),pos.add(BuildMode.START_BLOCK.getBlockPos()),Blocks.AIR.getDefaultState()));
        }
    }

    @Override
    protected int drawPreviewBlocks(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {
        if(BuildMode.START_BLOCK != null && !(BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockAir) && BuildMode.RIGHT_CLICK_NUMBER == 0)
        {
             return RenderPreview.drawPreviewBlock(BuildMode.START_BLOCK.getBlockPos(), new TemplateBlock(BuildMode.START_BLOCK.getFace(),0,0,0,BuildMode.START_BLOCK.getBlockState(),BuildMode.START_BLOCK.getTileEntity())
                , entityplayer, partialTicks,face );
        }
        return 0;

    }
    
    @Override
    protected void drawHelpLines(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {
        if(BuildMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
        {
            for (TemplateBlock block : BuildMode.CURR_PREVIEW_BLOCKS)
            {
                if(block.getBlockPos().equals(BuildMode.START_BLOCK.getBlockPos()))
                    continue;

                RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                    face, block.getBlockPos());

                RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks,0.0F,1.0F,0.0F);
            }
            BuildMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
        }
    }
    
    private void calcAdjacentAirBlocksInDir(EnumDirectionMode dir, BlockPos startBlock, Minecraft mc)
    {
        int limX = 2;
        int limY = 2;
        int limZ = 2;

        if(dir == EnumDirectionMode.ZY)
            limX = 0;
        else if(dir == EnumDirectionMode.XY)
            limZ = 0;
        else if(dir == EnumDirectionMode.XZ)
            limY = 0;

        findAdjacentAirBlocks(startBlock, limX, limY, limZ, dir == EnumDirectionMode.GROUND, mc);
    }

    private void findAdjacentAirBlocks(BlockPos startBlock, int limX, int limY, int limZ, boolean inGroundDir, Minecraft mc)
    {
        ArrayList<BlockPos> prevRelBlockPositions = new ArrayList<>();
        prevRelBlockPositions.add(new BlockPos(0, 0, 0));
        int[] selection = {0, 1, -1};
        int foundBlockCount = 0;
        ArrayList<BlockPos> currRelBlockPositions = new ArrayList<>();
        while (!prevRelBlockPositions.isEmpty() && (foundBlockCount <= ConfigurationHandler.toolConfig.MAX_BLOCK_COUNT))
        {
            currRelBlockPositions.clear();
            for (BlockPos currRelBlockPos : prevRelBlockPositions)
            {
                for (int x = 0; x <= limX; x++)
                {
                    foundBlockCount += ifAirAddToList(startBlock, currRelBlockPos.add(selection[x],0,0), currRelBlockPositions,inGroundDir, mc);
                }
                for (int y = 0; y <= limY; y++) {
                    foundBlockCount += ifAirAddToList(startBlock, currRelBlockPos.add(0, selection[y],0), currRelBlockPositions,inGroundDir, mc);
                }
                for (int z = 0; z <= limZ; z++) {
                    foundBlockCount += ifAirAddToList(startBlock, currRelBlockPos.add(0,0, selection[z]), currRelBlockPositions,inGroundDir, mc);
                }
            }
            //each time the new found adjacent blocks are checked again if they have adjacent airblocks
            //Note that they are not added to the prevRelBlockPositions. They replace that list entirely preventing double checking
            prevRelBlockPositions.clear();
            prevRelBlockPositions.addAll(currRelBlockPositions);
        }
    }

    private int ifAirAddToList(BlockPos startBlock, BlockPos relBlockPos, ArrayList<BlockPos> currRelBlockPositions,boolean inGroundDir, Minecraft mc) {
        BlockPos newBlockPos = startBlock.add(relBlockPos);

        //in Ground direction mode only select below the start block
        if(inGroundDir && newBlockPos.getY()>startBlock.getY())
            return 0;

        IBlockState checkedState = mc.world.getBlockState(newBlockPos);
        boolean addBlock = checkedState.getMaterial() == Material.AIR;
        if(IsometricCamera.IGNORE_PLANTS)
            addBlock = addBlock || PlacementHelper.isPlant(checkedState);

        if(IsometricCamera.IGNORE_FLUIDS)
            addBlock = addBlock || checkedState.getBlock() instanceof BlockLiquid;

        //check if the new position contains an airblock
        if (addBlock)
        {
            //if the blockposition is already in the relToAbsBlockPos it is not added again to the currRelBlockPositions list
            //This prevents double checking
            if (!relToAbsBlockPosMap.containsKey(relBlockPos))
            {
                relToAbsBlockPosMap.put(relBlockPos, new TemplateBlock(BuildMode.START_BLOCK.getFace(),newBlockPos,BuildMode.START_BLOCK.getBlockState(),BuildMode.START_BLOCK.getTileEntity()));
                currRelBlockPositions.add(relBlockPos);
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean hasMiddlePosition()
    {
        return false;
    }

    @Override
    public boolean hasEndPosition()
    {
        return false;
    }


}
