package com.deadtiger.advcreation.build_mode.tool_mode;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumPosOrder;
import com.deadtiger.advcreation.build_mode.utility.FillVector;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.render.RenderPlaneSurface;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.utility.MultiThreadLock;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class BaseToolMode
{
    public String toolModeName = "Base";
    public String buttonText = "BASE";
    public String tooltipText = "No Tooltip";
    public int identificationIndex;
    protected int finalRightClick = 0;

    public static ArrayList<FillVector> FILL_VECTOR_LIST = new ArrayList<>();
    public static MultiThreadLock FILL_VECTOR_LIST_LOCK = new MultiThreadLock();

    public BlockPos currPreviewPos = null;
    public Template currCopyTemplate = null;
    protected MultiThreadLock currCopyTemplateLock = new MultiThreadLock();

    public BlockPos previousTemplateBlockPos = null;
    public int rotationCount = 0;
    public int secondaryRotationCount = 0; //another rotation that is used for the BlockStandingSign which has 16 rotation modes

    public BlockPos normalHitPos = null;
    public BlockPos deletePos = null;
    public EnumFacing faceBeforeAltPress = EnumFacing.NORTH;
    protected EnumPosOrder[] defualtPosOrder = {EnumPosOrder.START_POS,EnumPosOrder.MIDDLE_POS,EnumPosOrder.END_POS};

    public void activateDeleteMode(double deleteOffset)
    {
        if(!BuildMode.isUsingAbsCoord())
        {
            if(BuildMode.BOTH_HIT_AND_HAND_ARE_SLAB)
                BuildMode.setStartBlock(new TemplateBlock(BuildMode.getStartBlock().getFace(), BuildMode.getStartBlock().getBlockPos(), Blocks.AIR.getDefaultState()));
            else
            {
                Vec3d invFaceDirVec = HelpFunctions.Vec3iToVec3d(BuildMode.getStartBlock().getFace().getDirectionVec()).scale(-1);
                BuildMode.setStartBlock(new TemplateBlock(BuildMode.getStartBlock().getFace(), BuildMode.getStartBlock().getBlockPos().add(invFaceDirVec.x, invFaceDirVec.y, invFaceDirVec.z), Blocks.AIR.getDefaultState()));
                BuildMode.setStartVec(BuildMode.getStartVec().add(invFaceDirVec));
                GuiOverlayManager.setStartPos(BuildMode.START_BLOCK.getBlockPos());
            }
        }
    }

    public boolean cancelRightClick()
    {
        return true;
    }

    public boolean rightClick(Action currAction, EntityPlayer player, int rightClickNumber)
    {

        if (rightClickNumber >= this.finalRightClick && BuildMode.START_BLOCK.getBlockState() != null)
        {
            return true;
        }
        return false;
    }

    public boolean leftClick(Action currAction, EntityPlayer player, int rightClickNumber)
    {
        if (rightClickNumber >= this.finalRightClick)
        {
            return true;
        }
        return false;
    }

    public int addToRightClickNumber()
    {
        if (BuildMode.START_BLOCK.getBlockState() != null || BuildMode.DELETE_MODE)
            return 1;
        else
            return 0;
    }

    public boolean startFinishBuilding(int rightClickNumber)
    {
        if (BuildMode.START_BLOCK.getBlockState() != null || BuildMode.DELETE_MODE)
            return (this.finalRightClick <= rightClickNumber);
        return false;
    }

    public boolean allowRightClickCancel()
    {
        return true;
    }

    public boolean specialRightClickPermision()
    {
        return false;
    }

    public boolean cancelLeftClick()
    {
        return true;
    }

    public void addNewBlock(TemplateBlock block)
    {
        addNewBlock(block, null, null, null);
    }

    public void addNewBlock(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        if(!BuildMode.isHeldAlt())
        {
            faceBeforeAltPress = face;
        }

        if (BuildMode.RIGHT_CLICK_NUMBER == 0 && BuildMode.RIGHT_CLICK_NUMBER <= this.getFinalRightClick() )
        {
            addNewBlockRightClick0(block, hitVec, hitBlockPos, faceBeforeAltPress);
            if(BuildMode.DELETE_MODE || BuildMode.BOTH_HIT_AND_HAND_ARE_SLAB)
                deletePos = BuildMode.START_BLOCK.getBlockPos();
            else
                deletePos = block.getBlockPos().subtract(faceBeforeAltPress.getDirectionVec());
        }
        else if (BuildMode.RIGHT_CLICK_NUMBER == 1 && BuildMode.RIGHT_CLICK_NUMBER <= this.getFinalRightClick() )
        {
            addNewBlockRightClick1(block, hitVec, hitBlockPos, faceBeforeAltPress);
            if(BuildMode.DELETE_MODE)
                deletePos = BuildMode.LAST_BLOCK;
            else
                deletePos = null;
        }
        else if (BuildMode.RIGHT_CLICK_NUMBER == 2 && BuildMode.RIGHT_CLICK_NUMBER <= this.getFinalRightClick() )
        {
            addNewBlockRightClick2(block, hitVec, hitBlockPos, faceBeforeAltPress);
            if(BuildMode.DELETE_MODE)
                deletePos = BuildMode.LAST_BLOCK;
            else
                deletePos = null;
        }
        else
        {
            normalHitPos = block.getBlockPos();
//            GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
            GuiOverlayManager.setPlacePointedCoordinate(normalHitPos.add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET));

            deletePos = normalHitPos.subtract(faceBeforeAltPress.getDirectionVec()).add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);
        }


        if(!(this instanceof CopyPasteToolMode || this instanceof MoveToolMode || this instanceof FillGapToolMode || (this instanceof PullToolMode && BuildMode.RIGHT_CLICK_NUMBER == 0)))
        {
            if(deletePos != null)
//                GuiOverlayManager.setDeletePointedCoordinate(deletePos.subtract(new BlockPos(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET)));
                GuiOverlayManager.setDeletePointedCoordinate(deletePos);
            else
                GuiOverlayManager.setDeletePointedCoordinate(null);

            if(BuildMode.DELETE_MODE)
                GuiOverlayManager.setPlacePointedCoordinate(null);
        }
        else
            GuiOverlayManager.setDeletePointedCoordinate(null);


        if (BuildMode.CURR_TOOL_BLOCKS.isEmpty() || BuildMode.RIGHT_CLICK_NUMBER == 0)
        {
            if (previousTemplateBlockPos == null || previousTemplateBlockPos.equals(block.getBlockPos()))
            {
                //make sure the rotation is in the way the player has indicated
                applyRotation();
            }
            else
            {
                //if the player is still choosing the startblock then clear the rotation when changing position
                clearRotation();
            }
        }

        if((this instanceof CopyPasteToolMode || this instanceof MoveToolMode) && BuildMode.RIGHT_CLICK_NUMBER > 1)
            GuiOverlayManager.setOffsetPos(new BlockPos(PlaceTemplateMode.MOUSE_X_OFFSET,PlaceTemplateMode.MOUSE_Y_OFFSET,PlaceTemplateMode.MOUSE_Z_OFFSET));
        else
            GuiOverlayManager.setOffsetPos(new BlockPos(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET));
        previousTemplateBlockPos = block.getBlockPos();
    }

    public void addNewBlockRightClick0(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        addNewStartBlock(block, hitVec, face, BuildMode.CURR_HITBLOCK_WIDTH);
//        BuildMode.updateCurrToolBlocks(block,hitVec);
        updateCurrToolBlocks(block,hitVec,hitVec);
    }

    protected void addNewStartBlock(TemplateBlock block, Vec3d hitVec, EnumFacing face, double deleteOffset)
    {


        if (BuildMode.DELETE_MODE && !BuildMode.USING_ABS_COORD)
        {
            Vec3d faceDirVec = new Vec3d(face.getDirectionVec().getX(),face.getDirectionVec().getY(), face.getDirectionVec().getZ());
            BuildMode.setStartVec(hitVec.add(faceDirVec.scale(-1)));
            BuildMode.setStartBlock(new TemplateBlock(face, block.getBlockPos().subtract(face.getDirectionVec()), Blocks.AIR.getDefaultState()));

//            Vec3d faceDirVec = new Vec3d(face.getDirectionVec().getX(), face.getDirectionVec().getY(), face.getDirectionVec().getZ());
//            BuildMode.setStartVec(hitVec.add(faceDirVec.scale(-1.0)).add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET));
//            newBlock = new TemplateBlock(face, block.getBlockPos().subtract(face.getDirectionVec()), Blocks.AIR.getDefaultState());
//            BuildMode.setStartBlock(new TemplateBlock(newBlock.getFace(),newBlock.getBlockPos().add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET),newBlock.getBlockState()));
        }
        else
        {
            BuildMode.setStartVec(hitVec);
            BuildMode.setStartBlock(block);

//            BuildMode.setStartVec(hitVec.add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET));
//            BuildMode.setStartBlock((new TemplateBlock(newBlock.getFace(),newBlock.getBlockPos().add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET),newBlock.getBlockState())));
        }
//        normalHitPos = newBlock.getBlockPos();

        GuiOverlayManager.setPosVisibility(true);
    }


    public void addNewBlockRightClick1(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {


        if (BuildMode.DELETE_MODE && !BuildMode.isUsingAbsCoord())
        {
            Vec3d faceDirVec = HelpFunctions.Vec3iToVec3d(face.getDirectionVec()).scale(-1);
            BuildMode.setEndVec(hitVec.add(faceDirVec));
            TemplateBlock newBlock = new TemplateBlock(face, block.getBlockPos().add(faceDirVec.x,faceDirVec.y,faceDirVec.z), Blocks.AIR.getDefaultState());
            BuildMode.updateCurrToolBlocks(newBlock, BuildMode.END_VEC);
        }
        else
        {
            BuildMode.setEndVec(hitVec);
            BuildMode.updateCurrToolBlocks(block, hitVec);
        }

    }

    public void addNewBlockRightClick2(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        if (BuildMode.DELETE_MODE && !BuildMode.isUsingAbsCoord())
        {
            Vec3d faceDirVec = HelpFunctions.Vec3iToVec3d(face.getDirectionVec()).scale(-1);
            BuildMode.setEndVec(HelpFunctions.parseVec(hitVec).add(faceDirVec));
            TemplateBlock newBlock = new TemplateBlock(face, block.getBlockPos().add(faceDirVec.x,faceDirVec.y,faceDirVec.z), Blocks.AIR.getDefaultState());
            BuildMode.updateCurrToolBlocks(newBlock, hitVec.add(faceDirVec));
        }
        else
        {
            BuildMode.setEndVec(HelpFunctions.parseVec(hitVec));
            BuildMode.updateCurrToolBlocks(block, hitVec);
        }

    }

    public void updateBlocksRightClick0(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        GuiOverlayManager.setBlockCount(0,0, 0, 0);
    }

    public void updateBlocksRightClick1(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
    }

    public void updateBlocksRightClick2(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
    }


    public void updateCurrToolBlocks(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        FILL_VECTOR_LIST_LOCK.blockingAttemptAtLocking();
        FILL_VECTOR_LIST.clear();
        FILL_VECTOR_LIST_LOCK.releaseLock();

        if(BuildMode.LAST_BLOCK != null)
            BuildMode.LAST_BLOCK = BuildMode.LAST_BLOCK.add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);

        if (BuildMode.RIGHT_CLICK_NUMBER == 0)
        {
            if(BuildMode.START_BLOCK != null)
            {
                //update the startvec with the mouse offsets added
                normalHitPos = BuildMode.START_BLOCK.getBlockPos();

                BlockPos offsetBlockPos = BuildMode.START_BLOCK.getBlockPos().add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);
                BuildMode.START_BLOCK.setBlockPos(offsetBlockPos);
                BuildMode.setStartBlock(BuildMode.getStartBlock());
                BuildMode.setStartVec(BuildMode.getStartVec().add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET));
                this.addPositionToGuiOverlay(offsetBlockPos);
//            GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
                GuiOverlayManager.setPlacePointedCoordinate(offsetBlockPos);

                updateBlocksRightClick0(endGlobalblock, hitVec, newStartVec);
            }

        }
        else if (BuildMode.RIGHT_CLICK_NUMBER == 1)
        {
            updateBlocksRightClick1(endGlobalblock, hitVec, newStartVec);
        }
        else if (BuildMode.RIGHT_CLICK_NUMBER == 2)
            updateBlocksRightClick2(endGlobalblock, hitVec, newStartVec);
    }

    public int drawPreview(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        int hashcode = 0;

        if (BuildMode.START_BLOCK != null && (BuildMode.START_BLOCK.getBlockState() != null || BuildMode.DELETE_MODE))
        {
            if (BuildMode.SHOW_PREVIEW_BLOCKS)
                hashcode = drawPreviewBlocks(entityplayer, hitVec, face, partialTicks);

            if (BuildMode.END_VEC != null && BuildMode.RIGHT_CLICK_NUMBER != 0)
                renderPlaneSurfaces(partialTicks);

            GlStateManager.enableColorMaterial();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableColorLogic();
            GlStateManager.disableLighting();

            if((BuildMode.RIGHT_CLICK_NUMBER != 0 && BuildMode.CURR_PREVIEW_BLOCKS.size() > 1) || this instanceof FillGapToolMode)
            {
                drawHelpLines( entityplayer, hitVec, face, partialTicks);
                RenderSelectionHighlight.drawSelectionBlockOutline( entityplayer, BuildMode.START_BLOCK.getBlockPos(), partialTicks, 0,1f,0);
            }
            drawOffsetIndication( entityplayer,partialTicks);



            if (BuildMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
            {
                if(BuildMode.RIGHT_CLICK_NUMBER >  this.getFinalRightClick())
                {
                    if(!(BuildMode.DELETE_MODE || this instanceof FillGapToolMode))
                    {
                        if(this instanceof SingleToolMode)
                            //special condition for highlighting the single block fixed preview
                        {
                            RenderSelectionHighlight.drawBlueBlockHighlight(entityplayer, BuildMode.START_BLOCK.getBlockPos(), partialTicks);
                        }
                        else
                        {
                            //draw an outline to indicate the added blocks
                            for (TemplateBlock block : BuildMode.CURR_PREVIEW_BLOCKS)
                            {
                                if(!(BuildMode.LAST_BLOCK != null && block.getBlockPos().equals(BuildMode.LAST_BLOCK)) &&
                                        !(BuildMode.START_BLOCK != null && block.getBlockPos().equals(BuildMode.START_BLOCK.getBlockPos())))
                                {
                                    RenderSelectionHighlight.drawFixedBlockHighlight(entityplayer, block.getBlockPos(), partialTicks);
                                }
                            }
                        }
                    }

                    //draw the blue outline for the last block
                    if(BuildMode.CURR_PREVIEW_BLOCKS.size() > 1 && BuildMode.LAST_BLOCK != null)
                        RenderSelectionHighlight.drawBlueBlockHighlight(entityplayer, BuildMode.LAST_BLOCK, partialTicks);


                }
                // draw the green outline for the first block
                if(BuildMode.RIGHT_CLICK_NUMBER != 0 && BuildMode.CURR_PREVIEW_BLOCKS.size() > 1)
                {
                    BlockPos drawPos = BuildMode.START_BLOCK.getBlockPos();
                    RenderSelectionHighlight.drawGreenBlockHighlight(entityplayer, partialTicks, drawPos);
                }
                BuildMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
            }
            if(BuildMode.RIGHT_CLICK_NUMBER > 1)
            {
                drawMiddleBlockHighlight(entityplayer,partialTicks);
            }





            GlStateManager.enableLighting();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableColorLogic();

            //testing
//            GlStateManager.enableColorMaterial();
//            GlStateManager.enableAlpha();
//            GlStateManager.enableBlend();
//            GlStateManager.enableColorLogic();
//            GlStateManager.disableLighting();

//            GlStateManager.enableLighting();
//            GlStateManager.disableAlpha();
//            GlStateManager.disableBlend();
//            GlStateManager.disableColorMaterial();
//            GlStateManager.disableColorLogic();
//
//            if (BuildMode.SHOW_PREVIEW_BLOCKS)
//                hashcode = drawPreviewBlocks(entityplayer, hitVec, face, partialTicks);
        }
        return hashcode;

    }

    protected void drawMiddleBlockHighlight(EntityPlayer entityplayer,float partialTicks)
    {
    }

    protected void renderPlaneSurfaces(Float partialTicks)
    {
        RenderPlaneSurface.render(BuildMode.NEW_START_VEC, 3, BuildMode.WORK_DIRECTION_MODE, partialTicks, 1.0);
    }

    protected void drawHelpLines(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        if (BuildMode.LINE_VEC != null && BuildMode.RIGHT_CLICK_NUMBER > 0)
        {
            RenderTemplate.drawLine(BuildMode.START_VEC.x, BuildMode.START_VEC.y, BuildMode.START_VEC.z, BuildMode.START_VEC.x + BuildMode.LINE_VEC.x, BuildMode.START_VEC.y + BuildMode.LINE_VEC.y, BuildMode.START_VEC.z + BuildMode.LINE_VEC.z, entityplayer, 0, partialTicks, 0.0f, 1.0f, 0.0f);
        }
    }

    protected void drawOffsetIndication(EntityPlayer entityplayer, Float partialTicks)
    {
        if(normalHitPos != null)
        {
            RenderSelectionHighlight.drawFadingBlocksToPos(entityplayer, partialTicks,normalHitPos,BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);
            RenderSelectionHighlight.drawWhiteBlockHighlight(entityplayer, normalHitPos, partialTicks);
        }
        drawDeleteBlockHighlight(entityplayer, partialTicks);
    }

    protected  void drawDeleteBlockHighlight(EntityPlayer entityplayer, Float partialTicks)
    {
        if(deletePos != null)
        {
            RenderSelectionHighlight.drawRedBlockHighlight(entityplayer, deletePos, partialTicks);
        }
    }

    protected int drawPreviewBlocks(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {

        int hashcode = 0;
        //render the list of blocks going to be placed
        if (BuildMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
        {
            for (TemplateBlock block : BuildMode.CURR_PREVIEW_BLOCKS)
            {
                if (BuildMode.DELETE_MODE || (BuildMode.START_BLOCK.getBlockState() != null && BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockAir))
                {
                    RenderSelectionHighlight.drawRedBlockHighlight(entityplayer,block.getBlockPos(),partialTicks);
                }
                else if (BuildMode.START_BLOCK.getBlockState() != null)
                {
                    RenderPreview.drawPreviewBlock(block.getBlockPos(), new TemplateBlock(block.getFace(), 0, 0, 0, block.getBlockState(),block.getTileEntity()), entityplayer, partialTicks,face );
//                    RenderPreview.drawTransparentPreviewBlock(block.getBlockPos(), new TemplateBlock(block.getFace(), 0, 0, 0, block.getBlockState(),block.getTileEntity()), entityplayer, partialTicks,face );

                }

                int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();
                hashcode += ~~currHashcode;
            }
            BuildMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
        }
        return hashcode;
    }

    public void cancelBuilding()
    {
        FILL_VECTOR_LIST_LOCK.blockingAttemptAtLocking();
        FILL_VECTOR_LIST.clear();
        FILL_VECTOR_LIST_LOCK.releaseLock();
        currCopyTemplateLock.blockingAttemptAtLocking();
        currCopyTemplate = null;
        currCopyTemplateLock.releaseLock();
    }

    public Vec3d getNewStartVec(Vec3d startVec, Vec3d secondaryLineVec)
    {
        return startVec;
    }

    public static void drawBlockLine(Vec3d startVec, Vec3d toAddVec)
    {
        HashMap<Vec3d,TemplateBlock> blockDict = new HashMap<>();
        PlacementHelper.drawBlockLine(startVec, toAddVec, BuildMode.START_BLOCK, false, false,blockDict);
        BuildMode.CURR_TOOL_BLOCKS.addAll(blockDict.values());
//        PlacementHelper.drawBlockLine(startVec, toAddVec, false, false, BuildMode.START_BLOCK, BuildMode.CURR_TOOL_BLOCKS);
    }


    protected static void addRectVector(Vec3d newLineStartVec, Vec3d toAddVec2, HashMap<Vec3d, TemplateBlock> dict)
    {
        addRectVector(newLineStartVec, toAddVec2, dict, false, false, 0.0f, 1.0F, 0.0f);
    }

    protected static void addRectVector(Vec3d newLineStartVec, Vec3d toAddVec2, HashMap<Vec3d, TemplateBlock> dict, float red, float green, float blue)
    {
        addRectVector(newLineStartVec, toAddVec2, dict, false, false, red, green, blue);
    }

    protected static void addRectVector(Vec3d newLineStartVec, Vec3d toAddVec2, HashMap<Vec3d, TemplateBlock> dict, boolean dontDrawFirst, boolean dontDrawLast, float red, float green, float blue)
    {
        FillVector lineFillVec;
        PlacementHelper.drawBlockLine(newLineStartVec, toAddVec2,BuildMode.START_BLOCK, dontDrawFirst, dontDrawLast, dict);
        lineFillVec = new FillVector(newLineStartVec);
        lineFillVec.endVec = newLineStartVec.add(toAddVec2);
        lineFillVec.setColor(red, green, blue);
        FILL_VECTOR_LIST.add(lineFillVec);
    }

    protected static void addRectVector(TemplateBlock block, Vec3d newLineStartVec, Vec3d toAddVec2, HashMap<Vec3d, TemplateBlock> dict, boolean dontDrawFirst, boolean dontDrawLast, float red, float green, float blue)
    {
        FillVector lineFillVec;
        PlacementHelper.drawBlockLine(newLineStartVec, toAddVec2,block, dontDrawFirst, dontDrawLast, dict);
        lineFillVec = new FillVector(newLineStartVec);
        lineFillVec.endVec = newLineStartVec.add(toAddVec2);
        lineFillVec.setColor(red, green, blue);
        FILL_VECTOR_LIST.add(lineFillVec);
    }

    public void changeY(double wheel)
    {
        if (currCopyTemplate != null)
        {
            currCopyTemplateLock.blockingAttemptAtLocking();
            if (wheel > 0)
                currCopyTemplate.addY_offset_template();
            else
                currCopyTemplate.decrY_offset_template();
            currCopyTemplateLock.releaseLock();
        }

    }

    public void rotate()
    {
        rotationCount++;
        if (rotationCount > 3)
        {
            rotationCount = 0;
            //secondary rotation count for the 16 rotation modes of BlockStandingSign
            secondaryRotationCount++;
            if (secondaryRotationCount > 3)
                secondaryRotationCount = 0;
        }


        if (BuildMode.START_BLOCK != null)
            BuildMode.START_BLOCK.rotateOrientation();
    }

    public void applyRotation()
    {
        if (BuildMode.START_BLOCK != null)
        {
            if(BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockStandingSign || BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockSkull || BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockBanner)
            {
                for (int j = 0; j < secondaryRotationCount*4; j++)
                {
                    BuildMode.START_BLOCK.rotateOrientation();
                }
                for (int j = 0; j < rotationCount; j++)
                {
                    BuildMode.START_BLOCK.rotateOrientation();
                }
            }
            else
            {
                for (int i = 0; i < rotationCount; i++)
                {
                    BuildMode.START_BLOCK.rotateOrientation();
                }
            }

        }


    }

    public void clearRotation()
    {
        rotationCount = 0;
        secondaryRotationCount = 0;
    }


    public int getFinalRightClick()
    {
        return finalRightClick;
    }

    protected void updateCopyTemplatePreview()
    {
        BuildMode.CURR_PREVIEW_BLOCKS_LOCK.blockingAttemptAtLocking();
        BuildMode.CURR_PREVIEW_BLOCKS.clear();
        BlockPos position2 = currPreviewPos.add(PlaceTemplateMode.MOUSE_X_OFFSET, PlaceTemplateMode.MOUSE_Y_OFFSET, PlaceTemplateMode.MOUSE_Z_OFFSET);

        if(currCopyTemplate != null)
        {
            BlockPos size = GuiOverlayManager.getSizeCount();
            BlockPos technicalStart = position2.add(-size.getX() / 2, 0, -size.getZ() / 2);
            GuiOverlayManager.setStartPos(technicalStart);
            GuiOverlayManager.setMiddlePos(position2);
            GuiOverlayManager.setEndPos(technicalStart.add(size.add(-1, -1, -1)));

            if (!currCopyTemplate.tooBig)
            {
                for (int j = 0; j < currCopyTemplate.getBlockListSize(); j++)
                {

                    TemplateBlock tempBlock = currCopyTemplate.getTempBlockOffset(j);
                    if (tempBlock.getBlockState().getBlock().getTranslationKey().equals("tile.air") || tempBlock.isEnclosed())
                        continue;
                    BuildMode.CURR_PREVIEW_BLOCKS.add(new TemplateBlock(tempBlock.getFace(), position2.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset()), tempBlock.getBlockState(), tempBlock.getTileEntity()));

                }
            }
            else
            {
                ArrayList<TemplateBlock> offsetRotatedBlockList = currCopyTemplate.getOffsetRotatedBlockList();

                int hashcode = 0;
                for (int j = 0; j < offsetRotatedBlockList.size(); j++)
                {
                    TemplateBlock tempBlock = offsetRotatedBlockList.get(j);

                    if (tempBlock.getBlockState().getBlock().getTranslationKey().equals("tile.air") || tempBlock.isEnclosed())
                        continue;


                    BlockPos blkOffset = tempBlock.getBlockPos();
                    BlockPos realWorldPos = position2.add(blkOffset.getX(), blkOffset.getY(), blkOffset.getZ());

                    boolean onEdge = PlaceTemplateMode.isOnEdge(currCopyTemplate, position2, realWorldPos,(int) ConfigurationHandler.toolConfig.PREVIEW_BLOCK_LIMIT);

                    if (onEdge)
                        BuildMode.CURR_PREVIEW_BLOCKS.add(new TemplateBlock(tempBlock.getFace(), position2.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset()), tempBlock.getBlockState(), tempBlock.getTileEntity()));

                }
            }
        }
        BuildMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
    }

    public void mirrorZY()
    {
    }

    public boolean hasNoAlterPositionMode()
    {
        return false;
    }

    public boolean hasMiddlePosition()
    {
        return false;
    }

    public boolean hasEndPosition()
    {
        return true;
    }

    public EnumPosOrder[] getPosOrder()
    {
        return defualtPosOrder;
    }

    protected void addPositionToGuiOverlay(BlockPos pos)
    {
        EnumPosOrder currPos = getPosOrder()[BuildMode.RIGHT_CLICK_NUMBER];
//        System.out.println("posOrder " + getPosOrder());
        if(currPos == EnumPosOrder.START_POS)
        {
//            System.out.println("set start pos " + pos);
            GuiOverlayManager.setStartPos(pos);
        }
        else if(currPos == EnumPosOrder.MIDDLE_POS)
        {
//            System.out.println("set middle pos " + pos);
            GuiOverlayManager.setMiddlePos(pos);
        }
        else if (currPos == EnumPosOrder.END_POS)
        {
//            System.out.println("set end pos " + pos);
            GuiOverlayManager.setEndPos(pos);
        }



    }

    public boolean canUseAbsoluteCoord()
    {
        return true;
    }

    public boolean allowRightClickException(Item item)
    {
        return false;
    }

    public void clearMouseOffset()
    {
        BuildMode.MOUSE_X_OFFSET = 0;
        BuildMode.MOUSE_Y_OFFSET = 0;
        BuildMode.MOUSE_Z_OFFSET = 0;
    }

}

    

