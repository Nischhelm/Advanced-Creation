package com.deadtiger.advcreation.edit_mode.adjust_modes;

import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.edit_mode.EditMode;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

public class PaintBucketAdjustMode extends BaseAdjustMode
{
    public static HashMap<BlockPos,TemplateBlock> relToAbsBlockPos = new HashMap<>();
    
    public PaintBucketAdjustMode()
    {
        this.finalRightClick = 0;
        this.toolModeName = "PaintBucket Mode";
        this.buttonText = "PAINTBUCKET";
        this.tooltipText = "Replace all adjacent blocks of same type with current block";
        this.identificationIndex = 1;

        addLegendEntry(LevelAdjustMode.existingLowerLevelColor,"Will be deleted");
        addLegendEntry(LevelAdjustMode.changedColor,"Will be painted");
        addLegendEntry(LevelAdjustMode.unchangedColor,"Do not change");
    }
    
    @Override
    public int addToRightClickNumber() {
        return 0;
    }
    
    @Override
    public void addNewBlockRightClick0(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        EditMode.setSTARTVEC(hitVec);


    
        Minecraft mc = Minecraft.getMinecraft();
        IBlockState blockState = mc.world.getBlockState(hitBlockPos);
        EditMode.CURR_HITBLOCK_POS = hitBlockPos;
        EditMode.setStartBlock(block);

        relToAbsBlockPos.clear();

        normalHitPos = EditMode.CURR_HITBLOCK_POS;
        calcAdjacentBlocksOfSameType3D(blockState, hitBlockPos, face, mc);
        EditMode.updateCurrToolBlocks(block,hitVec);
    }
    
    @Override
    public void updateBlocksRightClick0(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
        GuiOverlayManager.setStartPos(normalHitPos);

        EditMode.CURR_TOOL_BLOCKS.addAll(relToAbsBlockPos.values());
    }
    
    @Override
    public int drawPreview(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {

        int hashcode = 0;
        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.enableColorLogic();
        GlStateManager.disableLighting();
        if(EditMode.SHOW_PREVIEW_BLOCKS)
        {
            if(EditMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
            {
                EditMode.DRAW_PREVIEW_OUTLINE_ONLY = true;
                for (TemplateBlock block : EditMode.CURR_PREVIEW_BLOCKS)
                {
                    RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                        face, block.getBlockPos());

                    if(!EditMode.DELETE_MODE)
                        RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,0.0F,1.0F,0.0F);
                    else
                        RenderTemplate.drawSelectionBox( entityplayer, new_position, 0, partialTicks,1.0F,0.0F,0.0F);

                    if(block.getBlockState() == null || EditMode.DELETE_MODE )
                        block.setBlockState(Blocks.AIR.getDefaultState());

                    int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();

                    hashcode += ~~currHashcode;
                }
                EditMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
            }
        }

        if(normalHitPos != null)
            RenderSelectionHighlight.drawWhiteBlockHighlight(entityplayer,normalHitPos,partialTicks);

        GlStateManager.enableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogic();
        return hashcode;
    }
    
    private void calcAdjacentBlocksOfSameType3D(IBlockState blockState, BlockPos hitBlockPos, EnumFacing face, Minecraft mc)
    {
        int limX = 2;
        int limY = 2;
        int limZ = 2;
        int[] selection = {0, 1, -1};
        
        int foundBlockCount = 0;

        ArrayList<BlockPos> prevRelBlockPositions = new ArrayList<>();
        prevRelBlockPositions.add(new BlockPos(0, 0, 0));
        
        while (!prevRelBlockPositions.isEmpty() && (foundBlockCount <= ConfigurationHandler.toolConfig.MAX_BLOCK_COUNT)) {
            ArrayList<BlockPos> currRelBlockPositions = new ArrayList<>();
            for (BlockPos currRelBlockPos : prevRelBlockPositions) {
                for (int x = 0; x <= limX; x++) {
                    for (int y = 0; y <= limY; y++) {
                        for (int z = 0; z <= limZ; z++) {
                            BlockPos relBlockPos = currRelBlockPos.add(selection[x], selection[y], selection[z]);
                            BlockPos newBlockPos = hitBlockPos.add(relBlockPos);
                            //check if the new position contain the same blockType as the initial block
                            
                            IBlockState newBlockState = sameBlockStateExceptColorOrVariant(mc.world.getBlockState(newBlockPos),blockState, EditMode.START_BLOCK.getBlockState());
                            
                            if (newBlockState == null ||  !newBlockState.equals(Blocks.AIR.getDefaultState()))
                            {
                                if (!relToAbsBlockPos.containsKey(relBlockPos))
                                {
                                    TemplateBlock newTemplateBlock = new TemplateBlock(EditMode.START_BLOCK.getFace(),newBlockPos,newBlockState,null);
                                    relToAbsBlockPos.put(relBlockPos, newTemplateBlock);
                                    currRelBlockPositions.add(relBlockPos);
                                    foundBlockCount++;
                                }
                            }

                        }
                    }
                }
            }
            prevRelBlockPositions = currRelBlockPositions;
        }
    }
    
    /**
     * This function checks if the currBlockState is the same as prevBlockstate in everything except shape and direction
     * if it is not is will return null. But if it is and newBlockState is the same type as currBlockState  it will change newBlockstate into currBlockstate
     * in everything except color and variant.
     * @param currBlockState  The world blockstate you are checking
     * @param prevBlockState  The world blockstate that is being replaced
     * @param newBlockState   The Blockstate you want to replace it with
     * @return
     */
    public IBlockState sameBlockStateExceptColorOrVariant(IBlockState currBlockState, IBlockState prevBlockState,IBlockState newBlockState)
    {
        if(prevBlockState.getBlock().getTranslationKey().equals(currBlockState.getBlock().getTranslationKey()))
        {
            //the class of the current block is the same as
            for (IProperty key :prevBlockState.getPropertyKeys())
            {
                //if they have the same color and/or variant continue
                if((key.getName().equals("color"))||(key.getName().equals("variant")))
                {
                    if(!prevBlockState.getValue(key).equals(currBlockState.getValue(key)))
                        return Blocks.AIR.getDefaultState();
                }
            }
        }
        else
            return Blocks.AIR.getDefaultState();

        //if currBlockState is also the same as newBlockState
        if(newBlockState != null)
        {
            if(currBlockState.getBlock().getClass().equals(newBlockState.getBlock().getClass()))
            {
                //the class of the current block is the same as the new one so we will take over all properties except Color and/or Variant
                for (IProperty key :prevBlockState.getPropertyKeys())
                {
                    if(!(key.getName().equals("color")) && !(key.getName().equals("variant")))
                        newBlockState = newBlockState.withProperty(key,currBlockState.getValue(key));
                }
            }
        }

        return newBlockState;
    }

    @Override
    public String getGuiOverlayMessage(boolean deleteMode)
    {

        if(deleteMode)
            return TextFormatting.RED + "EraserBucket" + TextFormatting.WHITE + " Mode";
        else
            return "PaintBucket Mode";

    }
    
}
