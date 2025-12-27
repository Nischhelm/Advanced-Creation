package com.deadtiger.advcreation.edit_mode.adjust_modes;


import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.ExtremaXYZ;
import com.deadtiger.advcreation.build_mode.utility.FillVector;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.render.RenderPlaneSurface;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.utility.MultiThreadLock;
import com.deadtiger.advcreation.utility.PlacementHelper;
import com.deadtiger.advcreation.utility.shape_creator.CircleRectangleCreator;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class BaseAdjustMode
{
    public String toolModeName = "Base";
    public String buttonText = "BASE";
    public String tooltipText = "No Tooltip";
    public int identificationIndex;
    protected int finalRightClick = 0;

    public double radius = 1.75;
    public int radiusIndex = -1;

    public static ArrayList<FillVector> FILL_VECTOR_LIST = new ArrayList<>();
    public static MultiThreadLock FILL_VECTOR_LIST_LOCK = new MultiThreadLock();

    public BlockPos normalHitPos = null;

    public HashMap<Color,String> highlightLegendText = new HashMap<>();
    public ArrayList<Color> highlightColorOrder = new ArrayList<>();

    public boolean cancelRightClick()
    {
        return true;
    }

    public boolean rightClick(Action currAction, EntityPlayer player, int rightClickNumber)
    {
        if (rightClickNumber >= this.finalRightClick && EditMode.START_BLOCK.getBlockState() != null)
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
        return 0;
    }

    public boolean startCancelBuilding(int rightClickNumber)
    {
        if (EditMode.START_BLOCK.getBlockState() != null || EditMode.DELETE_MODE)
            return (this.finalRightClick == rightClickNumber);
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
        if (EditMode.RIGHT_CLICK_NUMBER == 0)
        {
            addNewBlockRightClick0(block, hitVec, hitBlockPos, face);
        }
        else if (EditMode.RIGHT_CLICK_NUMBER == 1)
        {
            addNewBlockRightClick1(block, hitVec, hitBlockPos, face);
        }
        else if (EditMode.RIGHT_CLICK_NUMBER == 2)
        {
            addNewBlockRightClick2(block, hitVec, hitBlockPos, face);
        }
    }

    public void addNewBlockRightClick0(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        EditMode.setSTARTVEC(hitVec);
        EditMode.setStartBlock(block);
        EditMode.setEndVec(hitVec);
        EditMode.updateCurrToolBlocks(block, hitVec);
    }

    public void addNewBlockRightClick1(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        EditMode.setEndVec(hitVec);
        EditMode.updateCurrToolBlocks(block, hitVec);

    }

    public void addNewBlockRightClick2(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        EditMode.setEndVec(PlacementHelper.parseVec(hitVec));
        EditMode.updateCurrToolBlocks(block, hitVec);
    }

    public void updateBlocksRightClick0(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
    }

    public void updateBlocksRightClick1(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
    }

    public void updateBlocksRightClick2(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
    }

    public void activateDeleteMode()
    {
    }

    public void deactivateDeleteMode()
    {
    }


    public void updateCurrToolBlocks(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        FILL_VECTOR_LIST_LOCK.blockingAttemptAtLocking();

        if(this.radiusIndex < 0)
            radiusIndex = CircleRectangleCreator.INSTANCE.getClosestHardcodedCircleIndex(this.radius);

        FILL_VECTOR_LIST.clear();
        if (EditMode.RIGHT_CLICK_NUMBER == 0)
        {
            updateBlocksRightClick0(endGlobalblock, hitVec, newStartVec);
        }
        else if (EditMode.RIGHT_CLICK_NUMBER == 1)
            updateBlocksRightClick1(endGlobalblock, hitVec, newStartVec);
        else if (EditMode.RIGHT_CLICK_NUMBER == 2)
            updateBlocksRightClick2(endGlobalblock, hitVec, newStartVec);
        FILL_VECTOR_LIST_LOCK.releaseLock();
        GuiOverlayManager.setDeletePointedCoordinate(null);
    }

    public int drawPreview(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        int hashcode = 0;
        if (EditMode.SHOW_PREVIEW_BLOCKS)
            hashcode = drawPreviewBlocks(entityplayer, hitVec, face, partialTicks);

        if (EditMode.END_VEC != null)
            renderPlaneSurfaces(partialTicks);

        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.enableColorLogic();
        GlStateManager.disableLighting();

        drawHelpLines(entityplayer, partialTicks);
        if(normalHitPos != null)
            RenderSelectionHighlight.drawWhiteBlockHighlight(entityplayer,normalHitPos,partialTicks);

        GlStateManager.enableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogic();

        return hashcode;
    }

    protected void renderPlaneSurfaces(Float partialTicks)
    {
        RenderPlaneSurface.render(EditMode.NEW_START_VEC, 3, EditMode.WORK_DIRECTION_MODE, partialTicks, 1.0);
    }

    protected void drawHelpLines(EntityPlayer entityplayer, Float partialTicks)
    {
        if (EditMode.LINEVEC != null && EditMode.RIGHT_CLICK_NUMBER > 0)
        {
            RenderTemplate.drawLine(EditMode.STARTVEC.x, EditMode.STARTVEC.y, EditMode.STARTVEC.z, EditMode.STARTVEC.x + EditMode.LINEVEC.x, EditMode.STARTVEC.y + EditMode.LINEVEC.y, EditMode.STARTVEC.z + EditMode.LINEVEC.z, entityplayer, 0, partialTicks, 0.0f, 1.0f, 0.0f);
        }
    }

    protected int drawPreviewBlocks(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        int hashcode = 0;
        if (EditMode.START_BLOCK != null && EditMode.RIGHT_CLICK_NUMBER == 0)
        {
            if (EditMode.DELETE_MODE || EditMode.START_BLOCK.getBlockState() == null)
            {
                RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                        face, EditMode.START_BLOCK.getBlockPos());
                RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks, 1.0F, 0.0F, 0.0F);
            }
            else
                RenderPreview.drawPreviewBlock(EditMode.START_BLOCK.getBlockPos(), new TemplateBlock(EditMode.START_BLOCK.getFace(), 0, 0, 0, EditMode.START_BLOCK.getBlockState(), BuildMode.START_BLOCK.getTileEntity())
                        , entityplayer, partialTicks,face );


        }

        if (EditMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
        {

            for (TemplateBlock block : EditMode.CURR_PREVIEW_BLOCKS)
            {
                if (EditMode.DELETE_MODE || EditMode.START_BLOCK.getBlockState() == null)
                {
                    RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                            face, block.getBlockPos());
                    RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks, 1.0F, 0.0F, 0.0F);
                }
                else
                    RenderPreview.drawPreviewBlock(block.getBlockPos(), new TemplateBlock(block.getFace(), 0, 0, 0, block.getBlockState(), block.getTileEntity())
                            , entityplayer, partialTicks,face );

                int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();

                hashcode += ~~currHashcode;
            }
            EditMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
        }
        return hashcode;
    }

    public void cancelBuilding()
    {
        FILL_VECTOR_LIST_LOCK.blockingAttemptAtLocking();
        FILL_VECTOR_LIST.clear();
        FILL_VECTOR_LIST_LOCK.releaseLock();
    }

    public Vec3d getNewStartVec(Vec3d startVec, Vec3d secondaryLineVec)
    {
        return startVec;
    }

    public double getRadius()
    {
        return radius;
    }

    public void setRadius(double radius)
    {
        this.radius = radius;
    }

    public void addRadius(double radius,int repeat)
    {
        double totalAddRadius = radius*(0.10*repeat);

        if(this.radiusIndex < CircleRectangleCreator.INSTANCE.hardcodedRadiusLengthList.size())
        {
            if (totalAddRadius < 0)
                this.radiusIndex+=  (int) MathHelper.floor (radius*repeat);
            else
                this.radiusIndex+= (int) MathHelper.ceil (radius*repeat);

            if (this.radiusIndex < 0)
                this.radiusIndex = 0;



            if( this.radiusIndex >= CircleRectangleCreator.INSTANCE.hardcodedRadiusLengthList.size())
            {
                this.radiusIndex = CircleRectangleCreator.INSTANCE.hardcodedRadiusLengthList.size();
                double maxHardcode = CircleRectangleCreator.INSTANCE.hardcodedRadiusLengthList.get(CircleRectangleCreator.INSTANCE.hardcodedRadiusLengthList.size()-1);
                this.radius = Math.floor(maxHardcode*10)/10.0;
            }

        }
        else
        {
            double maxCircleSize = ConfigurationHandler.toolConfig.CIRCLE_MAX_RADIUS;
            this.radius += totalAddRadius;
            if (this.radius < 0.0)
                this.radius = 0.0;
            else if (this.radius > maxCircleSize)
                this.radius = maxCircleSize;

            if(CircleRectangleCreator.INSTANCE.isInsideOfHardcodedRadiusRange(this.radius))
                this.radiusIndex = CircleRectangleCreator.INSTANCE.hardcodedRadiusLengthList.size()-1;
        }



    }

    public boolean allowRightClickException(Item item)
    {
        return false;
    }

    public boolean managesPreviewBlocksItself()
    {
        return false;
    }

    public boolean managesDeleteModeItself()
    {
        return false;
    }

    public boolean usesOnlyTerrainOption()
    {
        return false;
    }

    public boolean usesTerrainShapeOption()
    {
        return false;
    }

    protected void addBlocksToCURR_TOOL_BLOCKS_ForPreviewPurposes(ArrayList<TemplateBlock> toGround, int[][] newerHeights, int[][] blockIndices, float[][] data, BlockPos size2DArray, ExtremaXYZ extremaXYZ)
    {
        int dataSize = data.length;
        int dataHalf = MathHelper.floor(dataSize / 2.0);
        int adjacentMinHeight = 1000;
        int adjacentHeight = 0;
        int height;
        for (int x = dataHalf; x < size2DArray.getX() - dataHalf; x++)
        {
            for (int z = dataHalf; z < size2DArray.getZ() - dataHalf; z++)
            {
                height = newerHeights[x][z];
                adjacentMinHeight = 1000;

                if (height == 0 || height <= toGround.get(blockIndices[x][z]).getY_offset())
                {
                    continue;
                }

                for (int xOffset = -1; xOffset <= 1; xOffset++)
                {
                    for (int zOffset = -1; zOffset <= 1; zOffset++)
                    {


                        adjacentHeight = newerHeights[x + xOffset][z + zOffset];
                        if (adjacentHeight == 0)
                            adjacentHeight = extremaXYZ.minY;

                        if(adjacentHeight < adjacentMinHeight)
                            adjacentMinHeight = adjacentHeight;
                    }
                }
                //this is only done for when you want to raise the terrain since anything drawn beneath the terrain can barely be seen
                //add the same block as toGround to CURR_TOOL_BLOCKS
                TemplateBlock groundBlock = toGround.get(blockIndices[x][z]);
                EditMode.CURR_TOOL_BLOCKS.add(new TemplateBlock(EnumFacing.NORTH,groundBlock.getX_offset(),height,groundBlock.getZ_offset(),groundBlock.getBlockState(), TemplateBlock.EnumBlockType.WALL,false));

                if(adjacentMinHeight < height)
                {
                    //if the top block is grass fill the bottom up with dirt since that should be below it
                    IBlockState newState = groundBlock.getBlockState();
                    if(newState.getBlock() instanceof BlockGrass)
                        newState = Blocks.DIRT.getDefaultState();
                    //add as many block as necessary to the CURR_TOOL_BLOCKS to show a filled up terrain
                    for (int y = adjacentMinHeight; y < height; y++)
                    {
                        EditMode.CURR_TOOL_BLOCKS.add(new TemplateBlock(EnumFacing.NORTH,groundBlock.getX_offset(),y,groundBlock.getZ_offset(),newState, TemplateBlock.EnumBlockType.WALL,false));
                    }

                }

            }
        }
    }

    public String getGuiOverlayMessage(boolean deleteMode)
    {
        return this.toolModeName;
    }

    public void addLegendEntry(Color color, String text)
    {
        this.highlightLegendText.put(color,text);
        this.highlightColorOrder.add(color);
    }
}
