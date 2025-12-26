package com.deadtiger.advcreation.build_mode.tool_mode;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumPosOrder;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class CurveToolMode extends BaseToolMode
{
    protected BlockPos firstEndBlock = null;
    private final ArrayList<Vec3d> previewCurveHelpLineVecPoints = new ArrayList<>();
    public static double multiplier = 20.0;

    public CurveToolMode()
    {
        this.finalRightClick = 2;
        this.toolModeName = "Curve Mode";
        this.buttonText = "CURVE";
        this.tooltipText = "Draw/Delete Curve";
        this.identificationIndex = 4;
    }
    
    
    @Override
    public void addNewBlockRightClick1(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        if(BuildMode.DELETE_MODE)
        {
            Vec3d faceDirVec = HelpFunctions.Vec3iToVec3d(face.getDirectionVec()).scale(-1);
            BuildMode.setEndVec(HelpFunctions.parseVec(hitVec).add(faceDirVec));
            BuildMode.updateCurrToolBlocks(block,hitVec.add(faceDirVec));
        }
        else
        {
            BuildMode.setEndVec(HelpFunctions.parseVec(hitVec));
            BuildMode.updateCurrToolBlocks(block,hitVec);
        }
    }


    @Override
    public void updateBlocksRightClick1(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        normalHitPos = new BlockPos(BuildMode.START_VEC.add(BuildMode.LINE_VEC));
        BuildMode.LINE_VEC = BuildMode.LINE_VEC.addVector(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);
        BuildMode.LAST_BLOCK = new BlockPos(BuildMode.START_VEC.add(BuildMode.LINE_VEC));
        this.addPositionToGuiOverlay(BuildMode.LAST_BLOCK);
//        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
        GuiOverlayManager.setPlacePointedCoordinate(BuildMode.LAST_BLOCK);

        BuildMode.SECONDARY_LINE_VEC = BuildMode.LINE_VEC;
        firstEndBlock = BuildMode.LAST_BLOCK;
        
        drawCurve(BuildMode.LINE_VEC,BuildMode.SECONDARY_LINE_VEC);
    }
    
    @Override
    public void updateBlocksRightClick2(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        normalHitPos = new BlockPos(BuildMode.END_VEC);
        BuildMode.LINE_VEC = BuildMode.LINE_VEC.addVector(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET);
        BuildMode.LAST_BLOCK = new BlockPos(BuildMode.START_VEC.add(BuildMode.LINE_VEC));

        this.addPositionToGuiOverlay(BuildMode.LAST_BLOCK);
//        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
        GuiOverlayManager.setPlacePointedCoordinate(BuildMode.LAST_BLOCK);


        drawCurve(BuildMode.LINE_VEC,BuildMode.SECONDARY_LINE_VEC);
    }
    
    @Override
    protected void drawHelpLines(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {
        if (BuildMode.RIGHT_CLICK_NUMBER != 0 && BuildMode.LINE_VEC != null)
        {
            drawCurveHelplines(entityplayer,partialTicks);
//            // the second line you draw will be the point that decides the curve (p1)
//            Vec3d newLineVec = new Vec3d(0,0,0);
//            if(BuildMode.RIGHT_CLICK_NUMBER >= 2)
//            {
//                newLineVec = BuildMode.LINE_VEC;
//            }
//
//            Vec3d diffVec = BuildMode.LINE_VEC;
//            double diffLength = diffVec.lengthVector();
//            if(diffLength > 1.0)
//            {
//                int pointsToAdd =(int) Math.floor(diffLength/0.25);
//                double step = 1.0/pointsToAdd;
//
//                //calculating a bezier curve
//                Vec3d p0 = new Vec3d(0,0,0);
//                Vec3d p1 = newLineVec;
//                Vec3d p2 = BuildMode.SECONDARY_LINE_VEC;
//                Vec3d previousPoint = BuildMode.START_VEC;
//                for(double t = 0; t <= 1.0; t = t + step)
//                {
//                    double addX = p1.x + Math.pow((1.0 - t),2)*(p0.x-p1.x) + Math.pow( t,2)*(p2.x-p1.x);
//                    double addY = p1.y + Math.pow((1.0 - t),2)*(p0.y-p1.y) + Math.pow( t,2)*(p2.y-p1.y);
//                    double addZ = p1.z + Math.pow((1.0 - t),2)*(p0.z-p1.z) + Math.pow( t,2)*(p2.z-p1.z);
//
//                    Vec3d newPoint = BuildMode.START_VEC.addVector(addX, addY, addZ);
//                    RenderTemplate.drawLine(previousPoint.x,previousPoint.y,previousPoint.z,newPoint.x,newPoint.y,newPoint.z,entityplayer,0,
//                        partialTicks,BuildMode.RED,BuildMode.GREEN,BuildMode.BLUE);
//                    previousPoint = newPoint;
//                }
//            }
        }
    }

    @Override
    public int drawPreview(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        int hashcode = super.drawPreview(entityplayer, hitVec, face, partialTicks);

        if(BuildMode.RIGHT_CLICK_NUMBER > 1)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableColorLogic();
            GlStateManager.disableLighting();

            RenderSelectionHighlight.drawBlueBlockHighlight(entityplayer,normalHitPos.add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET),partialTicks);

            GlStateManager.enableLighting();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableColorLogic();
        }

        return hashcode;
    }

    private void drawCurve(Vec3d vecToP1, Vec3d vecToP2)
    {
        if(vecToP1.lengthVector() > 1.0)
        {
            BlockPos[] prevPositions = {null,null,null};//{previousPosition,previousPreviousPosition,lastCheckedPosition}

            int[] votes = new int[2];
            ArrayList<BlockPos> positionsList = generateBezierCurvePositions(vecToP1,vecToP2, prevPositions, votes);

            for (BlockPos pos: positionsList)
            {
                PlacementHelper.placePositionDirect(pos,BuildMode.START_BLOCK,BuildMode.CURR_TOOL_BLOCKS);
            }

            //make sure the end vector is also drawn
            if (PlacementHelper.isWireRailOrNeedsConnection(BuildMode.START_BLOCK))
            {
                if (firstEndBlock != null && (prevPositions[0] == null || !(prevPositions[0].getX() == firstEndBlock.getX() && prevPositions[0].getZ() == firstEndBlock.getZ())|| prevPositions[0].equals(firstEndBlock) && !firstEndBlock.equals(BuildMode.START_BLOCK.getBlockPos()) && (prevPositions[1]==null || !(prevPositions[1].getX() == firstEndBlock.getX() && prevPositions[1].getZ() == firstEndBlock.getZ())) || positionsList.isEmpty() || !positionsList.get(positionsList.size()-1).equals(firstEndBlock)))
                {
                    PlacementHelper.placePositionDirect(firstEndBlock,BuildMode.START_BLOCK,BuildMode.CURR_TOOL_BLOCKS);
                    if(prevPositions[0] != null && PlacementHelper.isDiagonal(prevPositions[0],firstEndBlock))
                    {
                        BlockPos intermediatePosition = PlacementHelper.calculateIntermediatePosition(firstEndBlock,BuildMode.START_VEC.add(vecToP1), prevPositions[0]);
                        if(!(prevPositions[0].getX() ==  intermediatePosition.getX() && prevPositions[0].getZ() ==  intermediatePosition.getZ()))
                            PlacementHelper.placePositionDirect(firstEndBlock,BuildMode.START_BLOCK,BuildMode.CURR_TOOL_BLOCKS);
                    }
                }



            }
            else if ( firstEndBlock != null && (prevPositions[0] == null || positionsList.isEmpty() || !positionsList.get(positionsList.size()-1).equals(firstEndBlock)))
                PlacementHelper.placePositionDirect(firstEndBlock,BuildMode.START_BLOCK,BuildMode.CURR_TOOL_BLOCKS);
        }
        else
            PlacementHelper.placePositionDirect(new BlockPos(BuildMode.START_VEC),BuildMode.START_BLOCK,BuildMode.CURR_TOOL_BLOCKS);
    }

    private ArrayList<BlockPos> generateBezierCurvePositions(Vec3d p1, Vec3d p2, BlockPos[] prevPositions, int[] votes)
    {
        previewCurveHelpLineVecPoints.clear();
        ArrayList<BlockPos> positionsList = new ArrayList<>();

        if (p1 == null || p2 == null)
            return positionsList;

//        int pointsToAdd =(int) Math.floor(p1.lengthVector() /0.10);

        int pointsToAdd =(int) Math.floor((p1.lengthVector()*Math.max(p2.lengthVector(),2))*multiplier);
        double step = 1.0/pointsToAdd;

        //calculating a bezier curve
        //      p0 is the start of the curve
        //      p1 is the curve point
        //      p2 is the end of the curve
        Vec3d p0 = new Vec3d(0,0,0);

        for(double t = 0; t <= 1.0; t = t + step)
        {
            double addX = p1.x + Math.pow((1.0 - t), 2) * (p0.x - p1.x) + Math.pow(t, 2) * (p2.x - p1.x);
            double addY = p1.y + Math.pow((1.0 - t), 2) * (p0.y - p1.y) + Math.pow(t, 2) * (p2.y - p1.y);
            double addZ = p1.z + Math.pow((1.0 - t), 2) * (p0.z - p1.z) + Math.pow(t, 2) * (p2.z - p1.z);

            Vec3d newPoint = BuildMode.START_VEC.addVector(addX, addY, addZ);
            previewCurveHelpLineVecPoints.add(newPoint);
            if (BuildMode.START_BLOCK.getBlockState() != null && (BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockRedstoneWire || BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockRailBase) )
            {
                if (PlacementHelper.processNewWirePosition(new BlockPos(newPoint),newPoint, positionsList, prevPositions, null,false, null, false))
                    break;
            }
            else if (PlacementHelper.processNewPosition(new BlockPos(newPoint), positionsList, prevPositions, null,false, null, false,votes, null, null))
                break;
        }
        return positionsList;
    }



    private void drawCurveHelplines(EntityPlayer entityplayer, Float partialTicks)
    {
        Vec3d previousPoint = null;
        for (Vec3d circleVecPoint : previewCurveHelpLineVecPoints)
        {
            if (previousPoint == null)
            {
                previousPoint = circleVecPoint;
                continue;
            }

            RenderTemplate.drawLine(previousPoint.x, previousPoint.y, previousPoint.z, circleVecPoint.x, circleVecPoint.y, circleVecPoint.z, entityplayer, 0,
                    partialTicks, BuildMode.RED, BuildMode.GREEN, BuildMode.BLUE);

            previousPoint = circleVecPoint;
        }

    }

    @Override
    protected void drawMiddleBlockHighlight(EntityPlayer entityplayer,float partialTicks)
    {
        RenderSelectionHighlight.drawBlueBlockHighlight(entityplayer, new BlockPos(BuildMode.getStartVec().add(BuildMode.SECONDARY_LINE_VEC)), partialTicks);
    }

    @Override
    public boolean hasMiddlePosition()
    {
        return true;
    }

    @Override
    public EnumPosOrder[] getPosOrder()
    {
        return new EnumPosOrder[]{EnumPosOrder.START_POS,EnumPosOrder.END_POS,EnumPosOrder.MIDDLE_POS};
    }
}
