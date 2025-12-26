package com.deadtiger.advcreation.build_mode.tool_mode;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumPosOrder;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.client.input.KeyInputHandler;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.utility.MultiThreadLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class CopyPasteToolMode extends BaseToolMode
{

    protected ArrayList<BlockPos> currSelectCoords = new ArrayList<>();
    protected MultiThreadLock currSelectCoordsLock = new MultiThreadLock();
    protected BlockPos firstPos = null;
    protected BlockPos lastPos = null;

    protected boolean allowAddRightClickNumber = false;
    
    public CopyPasteToolMode()
    {
        this.finalRightClick = 2;
        this.toolModeName = "Copy Mode";
        this.buttonText = "COPY";
        this.tooltipText = "Select and Copy blocks";
        this.identificationIndex = 7;
    }
    
    @Override
    public boolean rightClick(Action currAction, EntityPlayer player, int rightClickNumber)
    {
        allowAddRightClickNumber = false;
        if (rightClickNumber >= this.finalRightClick  )
        {
            if(currCopyTemplate != null && currPreviewPos != null && !BuildMode.USING_ABS_COORD)
            {
                currCopyTemplateLock.blockingAttemptAtLocking();
                PlaceTemplateMode.placeNewTemplate(currCopyTemplate, currPreviewPos,currAction, player);
                currCopyTemplateLock.releaseLock();
                BuildMode.RIGHT_CLICK_NUMBER = 2;
            }
            allowAddRightClickNumber = true;
            return true;
        }
        
        return false;
    }
    
    @Override
    public int addToRightClickNumber()
    {
        if(allowAddRightClickNumber)
            return 1;
        return 0;
    }
    
    @Override
    public boolean allowRightClickCancel()
    {
        return true;
    }
    
    @Override
    public boolean specialRightClickPermision()
    {
        return (BuildMode.RIGHT_CLICK_NUMBER >= this.finalRightClick);
    }
    
    @Override
    public boolean leftClick(Action currAction, EntityPlayer player, int rightClickNumber)
    {
        allowAddRightClickNumber = false;
        if(rightClickNumber == 0)
        {
            BuildTemplateMode.cancelTemplate();
            firstPos =  new BlockPos(BuildMode.START_BLOCK.getBlockPos());
            BuildTemplateMode.leftClick(firstPos);

            allowAddRightClickNumber = true;
        }
        else if (rightClickNumber == 1)
        {
            lastPos = new BlockPos(BuildMode.CURR_HITBLOCK_POS);
            BuildTemplateMode.leftClick(lastPos);
            currCopyTemplateLock.blockingAttemptAtLocking();
            currCopyTemplate = BuildTemplateMode.finishTemplate();
            currCopyTemplate.tryCalculateProperties();
            currCopyTemplateLock.releaseLock();
            allowAddRightClickNumber = true;
        }
        
        return false;
    }
    
    @Override
    public boolean startFinishBuilding(int rightClickNumber)
    {
        return (this.finalRightClick  <= rightClickNumber);
    }
    
    @Override
    public void addNewBlockRightClick0(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face) {
        if(!BuildMode.DELETE_MODE)
            BuildMode.DELETE_MODE = true;
        super.addNewBlockRightClick0(block, hitVec, hitBlockPos, face);
    }


    @Override
    public void cancelBuilding()
    {
        currSelectCoordsLock.blockingAttemptAtLocking();
        currSelectCoords.clear();
        currSelectCoordsLock.releaseLock();
        super.cancelBuilding();
    }


    @Override
    public void updateBlocksRightClick1(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        if(endGlobalblock != null)
        {
            normalHitPos = endGlobalblock.getBlockPos();
            BuildMode.CURR_HITBLOCK_POS = endGlobalblock.getBlockPos().add(BuildMode.MOUSE_X_OFFSET, BuildMode.MOUSE_Y_OFFSET, BuildMode.MOUSE_Z_OFFSET);
            BuildMode.LAST_BLOCK = BuildMode.CURR_HITBLOCK_POS;
            currSelectCoordsLock.blockingAttemptAtLocking();
            BuildTemplateMode.updateTechnicalStartPos(BuildMode.CURR_HITBLOCK_POS);
            currSelectCoords = BuildTemplateMode.getPreviewBlockPosList(BuildMode.CURR_HITBLOCK_POS);
            BuildTemplateMode.setEndPos(BuildMode.LAST_BLOCK);
            BuildTemplateMode.updateCountDisplay(currSelectCoords);
//        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
            GuiOverlayManager.setPlacePointedCoordinate(BuildMode.CURR_HITBLOCK_POS);
            currSelectCoordsLock.releaseLock();
        }
    }
    
    @Override
    public void updateBlocksRightClick2(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        int blocksSize = BuildTemplateMode.NEW_SIZE.getX()*BuildTemplateMode.NEW_SIZE.getY()*BuildTemplateMode.NEW_SIZE.getZ();

        if(currCopyTemplate != null)
        {
            if(currCopyTemplateLock.attemptLocking() )
            {
                //if the currCopyTemplate is not the same size as the template in BuildTemplateMode finishTemplate to
                //generate a update template
                if(blocksSize != currCopyTemplate.getBlockListSize())
                {
                    currCopyTemplate = BuildTemplateMode.finishTemplate();
                    if(currCopyTemplate != null)
                        currCopyTemplate.tryCalculateProperties();
                }


                if(BuildMode.DELETE_MODE)
                    BuildMode.DELETE_MODE = false;

                if(!KeyInputHandler.alterToolMode)
                {
                    currPreviewPos = endGlobalblock.getBlockPos();
                    PlaceTemplateMode.updateParametersInNonAlterMode(endGlobalblock, hitVec,ModEntity.currCursorVec);
                }
                else
                    currPreviewPos = PlaceTemplateMode.getAlterModePosition(endGlobalblock.getBlockPos(), hitVec);

                normalHitPos = currPreviewPos;
//                GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
                GuiOverlayManager.setPlacePointedCoordinate(currPreviewPos.add(PlaceTemplateMode.MOUSE_X_OFFSET, PlaceTemplateMode.MOUSE_Y_OFFSET, PlaceTemplateMode.MOUSE_Z_OFFSET));

//                updateCopyTemplatePreview();

                currCopyTemplateLock.releaseLock();
            }

        }


    }


    @Override
    public int drawPreview(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        int hashcode = 0;
        if(BuildMode.RIGHT_CLICK_NUMBER == 0)
        {
            BlockPos raytraceBlock = null;
            if(BuildMode.START_BLOCK != null)
                raytraceBlock = BuildMode.START_BLOCK.getBlockPos();
            else
                raytraceBlock = new BlockPos(hitVec);

            RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                face, raytraceBlock);
            RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks,1.0F,0.0F,0.0F);
            hashcode = raytraceBlock.hashCode();
        }
        else if(BuildMode.RIGHT_CLICK_NUMBER >= 1)
        {
            AdvCreation.drawSelectionBox = true;

            BuildTemplateMode.drawSelectionBox(Minecraft.getMinecraft(),entityplayer,BuildMode.CURR_HITBLOCK_POS,hitVec,false,partialTicks);
            BuildTemplateMode.drawHighlighting(Minecraft.getMinecraft(),BuildMode.CURR_HITBLOCK_POS,partialTicks);
            hashcode = BuildMode.CURR_HITBLOCK_POS.hashCode();

            GlStateManager.enableColorMaterial();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableColorLogic();

            if(firstPos != null)
                RenderSelectionHighlight.drawGreenBlockHighlight(entityplayer,partialTicks,firstPos);

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableColorLogic();


            if(BuildMode.RIGHT_CLICK_NUMBER >= 2 && currPreviewPos != null && currCopyTemplate != null)
            {
                GlStateManager.enableColorMaterial();
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.enableColorLogic();

                if(lastPos != null)
                    RenderSelectionHighlight.drawBlueBlockHighlight(entityplayer, lastPos, partialTicks);

                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.disableColorMaterial();
                GlStateManager.disableColorLogic();

                boolean prevDeleteMode = BuildMode.DELETE_MODE;
                BuildMode.DELETE_MODE = false;


                BlockPos position2 = currPreviewPos.add(PlaceTemplateMode.MOUSE_X_OFFSET, PlaceTemplateMode.MOUSE_Y_OFFSET, PlaceTemplateMode.MOUSE_Z_OFFSET);

                if(currCopyTemplate != null)
                {
                    BlockPos size = GuiOverlayManager.getSizeCount();
                    BlockPos technicalStart = position2.add(-size.getX() / 2, 0, -size.getZ() / 2);
                    GuiOverlayManager.setStartPos(technicalStart);
                    GuiOverlayManager.setMiddlePos(position2);
                    GuiOverlayManager.setEndPos(technicalStart.add(size.add(-1, -1, -1)));

                    if(!currCopyTemplate.tooBig)
                        hashcode = PlaceTemplateMode.drawPreviewBlocks( currCopyTemplate, position2, entityplayer, partialTicks);
                    else
                        hashcode = PlaceTemplateMode.drawFilteredEdgePreviewBlocks(currCopyTemplate, position2, entityplayer,face, partialTicks,null);
                }
                else
                hashcode = drawPreviewBlocks(entityplayer,hitVec,face,partialTicks);
                BuildMode.DELETE_MODE = prevDeleteMode;

            }
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.enableColorLogic();

        drawOffsetIndication(entityplayer,partialTicks);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogic();
        return hashcode;
    }

    @Override
    protected int drawPreviewBlocks(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        int hashcode = 0;
        //render the list of blocks going to be placed
        if (BuildMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
        {
            for (TemplateBlock block : BuildMode.CURR_PREVIEW_BLOCKS)
            {
                RenderPreview.drawPreviewBlock(block.getBlockPos(), new TemplateBlock(block.getFace(), 0, 0, 0, block.getBlockState(),block.getTileEntity()), entityplayer, partialTicks,face );

                int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();
                hashcode += ~~currHashcode;
            }
            BuildMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
        }
        return hashcode;
    }

    @Override
    protected void drawDeleteBlockHighlight(EntityPlayer entityplayer, Float partialTicks)
    {
        if(deletePos != null && BuildMode.RIGHT_CLICK_NUMBER < 2)
            RenderSelectionHighlight.drawBlueBlockHighlight(entityplayer, deletePos, partialTicks);

    }

    @Override
    protected void drawOffsetIndication(EntityPlayer entityplayer, Float partialTicks)
    {
        if(BuildMode.RIGHT_CLICK_NUMBER < 2 )
            super.drawOffsetIndication(entityplayer,partialTicks);
        else
        {
            if(normalHitPos != null)
            {
                RenderSelectionHighlight.drawFadingBlocksToPos(entityplayer, partialTicks,normalHitPos,PlaceTemplateMode.MOUSE_X_OFFSET,PlaceTemplateMode.MOUSE_Y_OFFSET,PlaceTemplateMode.MOUSE_Z_OFFSET);
                RenderSelectionHighlight.drawWhiteBlockHighlight(entityplayer, normalHitPos, partialTicks);
            }
            drawDeleteBlockHighlight(entityplayer, partialTicks);

        }

    }

    @Override
    public void rotate() {

//        PlaceTemplateMode.rotateMouseOffset();
        if(currCopyTemplate != null)
        {
            currCopyTemplateLock.blockingAttemptAtLocking();
            currCopyTemplate.rotateY();
            currCopyTemplateLock.releaseLock();
        }
    }
    
    @Override
    public void applyRotation()
    {
    
    }

    @Override
    public void mirrorZY() {
        if(currCopyTemplate != null)
        {
            currCopyTemplateLock.blockingAttemptAtLocking();
            currCopyTemplate.mirrorZY();
            currCopyTemplateLock.releaseLock();
        }
    }

    @Override
    public EnumPosOrder[] getPosOrder()
    {
        if(BuildMode.RIGHT_CLICK_NUMBER < 2)
            return new EnumPosOrder[]{EnumPosOrder.START_POS,EnumPosOrder.END_POS};
        else
            return defualtPosOrder;
    }

    @Override
    public boolean hasMiddlePosition()
    {
        return (BuildMode.RIGHT_CLICK_NUMBER > 1);
    }

    @Override
    public boolean allowRightClickException(Item item)
    {
        return BuildMode.RIGHT_CLICK_NUMBER == this.finalRightClick;
    }

    @Override
    public void clearMouseOffset()
    {
        if(BuildMode.RIGHT_CLICK_NUMBER >= 2)
            PlaceTemplateMode.clearMouseOffset();
        else
            super.clearMouseOffset();
    }

}
