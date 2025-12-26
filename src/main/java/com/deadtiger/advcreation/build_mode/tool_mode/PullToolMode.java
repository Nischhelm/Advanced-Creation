package com.deadtiger.advcreation.build_mode.tool_mode;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.build_mode.utility.EnumPosOrder;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.color.ICC_ColorSpace;
import java.util.*;

public class PullToolMode extends BaseToolMode
{
    public static HashMap<BlockPos,BlockPos> relToAbsBlockPos = new HashMap<>();
    public EnumDirectionMode currDirectionMode= null;

    public int pullSurfaceBlockLimit = 1 + 8 * ((int) (Math.pow(2, 6)));
    
    public PullToolMode()
    {
        this.finalRightClick = 1;
        this.toolModeName = "Pull Mode";
        this.buttonText = "PULL";
        this.tooltipText = "Draw/Delete Pull";
        this.identificationIndex = 6;
    }
    
    @Override
    public boolean rightClick(Action currAction, EntityPlayer player, int rightClickNumber) {
        if (rightClickNumber >= this.finalRightClick )
        {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean startFinishBuilding(int rightClickNumber) {
        return(this.finalRightClick  <= rightClickNumber);
    }
    
    @Override
    public void addNewBlockRightClick0(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        BuildMode.setStartVec(hitVec);
    
        Minecraft mc = Minecraft.getMinecraft();
        IBlockState blockState = mc.world.getBlockState(hitBlockPos);
        TileEntity tileEntity = mc.world.getTileEntity(hitBlockPos);
        BuildMode.CURR_HITBLOCK_POS = hitBlockPos;

        normalHitPos = hitBlockPos;
        deletePos = null;
        this.addPositionToGuiOverlay(hitBlockPos);
        GuiOverlayManager.setPlacePointedCoordinate(hitBlockPos);
        GuiOverlayManager.setDeletePointedCoordinate(null);

        if(BuildMode.DELETE_MODE)
            BuildMode.setStartBlock(new TemplateBlock(face,block.getBlockPos(), Blocks.AIR.getDefaultState()));
        else
            BuildMode.setStartBlock(new TemplateBlock(face, hitBlockPos, blockState,tileEntity));

        relToAbsBlockPos.clear();
    
        calcAdjacentBlocksOfSameType2D(blockState, hitBlockPos, face, mc);
    }
    
    @Override
    public void addNewBlockRightClick1(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face) {
        BuildMode.setEndVec(PlacementHelper.parseVec(hitVec));
        
        if(BuildMode.DIRECTION_MODE != EnumDirectionMode.GROUND)
        {
            //update the work_direction based on the camera look and pull surface face so that no matter how you look
            // The pulled volume follows the cursor nicely
            BuildMode.WORK_DIRECTION_MODE =  getDirectionFromLookAndPullSurfaceFace(IsometricCamera.CAMERA_LOOK_VECTOR,BuildMode.START_BLOCK.getFace());
            this.currDirectionMode = BuildMode.WORK_DIRECTION_MODE;
        }

        BuildMode.updateCurrToolBlocks(block,hitVec);
    }
    
    @Override
    public void updateBlocksRightClick1(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {

        HashMap<Vec3d,TemplateBlock> blockDict = new HashMap<Vec3d,TemplateBlock>();
    
        Vec3i dir = BuildMode.START_BLOCK.getFace().getDirectionVec();
    
        Vec3d pullVec = BuildMode.END_VEC.subtract(new Vec3d(BuildMode.START_BLOCK.getX_offset()+0.5,BuildMode.START_BLOCK.getY_offset()+0.5,BuildMode.START_BLOCK.getZ_offset()+0.5));
        pullVec = new Vec3d(pullVec.x*Math.abs(dir.getX()),pullVec.y*Math.abs(dir.getY()),pullVec.z*Math.abs(dir.getZ()));
    
        //limit how deep or high you can go based on the size of surface you are pulling
        int surfaceBlocks = relToAbsBlockPos.values().size();
        
        double maxPullVecLenght = (ConfigurationHandler.toolConfig.MAX_BLOCK_COUNT*10);
        if(surfaceBlocks != 0)
            maxPullVecLenght = (ConfigurationHandler.toolConfig.MAX_BLOCK_COUNT*10)/surfaceBlocks;
        
        if(pullVec.length() > maxPullVecLenght)
        {
            pullVec = pullVec.scale(maxPullVecLenght/pullVec.length());
        }

        if(BuildMode.WORK_DIRECTION_MODE == EnumDirectionMode.GROUND)
        {
            pullToNextSurface(blockDict, maxPullVecLenght);

            GuiOverlayManager.setEndPos(null);
            GuiOverlayManager.setPlacePointedCoordinate(null);
            GuiOverlayManager.setDeletePointedCoordinate(null);
        }
        else
        {
            pullToPullVec(blockDict, pullVec);
            normalHitPos = new BlockPos(BuildMode.END_VEC);
            GuiOverlayManager.setEndPos(normalHitPos);
            if(BuildMode.DELETE_MODE)
            {
                GuiOverlayManager.setPlacePointedCoordinate(null);
                GuiOverlayManager.setDeletePointedCoordinate(normalHitPos);
            }
            else
            {
                GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
                GuiOverlayManager.setDeletePointedCoordinate(null);
            }

        }
       
        BuildMode.CURR_TOOL_BLOCKS.addAll(blockDict.values());
    }



    @Override
    public int drawPreview(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {
        int hashcode = 0;
        if(BuildMode.SHOW_PREVIEW_BLOCKS)
        {
            if(BuildMode.START_BLOCK != null && BuildMode.RIGHT_CLICK_NUMBER == 0)
            {
                RenderPreview.drawPreviewBlock(BuildMode.START_BLOCK.getBlockPos(), new TemplateBlock(EnumFacing.NORTH,0,0,0,BuildMode.START_BLOCK.getBlockState(),BuildMode.START_BLOCK.getTileEntity())
                    , entityplayer, partialTicks,face );
            }
            if(BuildMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
            {
                for (TemplateBlock block : BuildMode.CURR_PREVIEW_BLOCKS)
                {
                    if(BuildMode.DELETE_MODE)
                    {
                        RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                            face, block.getBlockPos());
    
                        RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks,1.0F,0.0F,0.0F);
                    }
                    else
                    {
                        RenderPreview.drawPreviewBlock(block.getBlockPos(), new TemplateBlock(block.getFace(), 0, 0, 0, block.getBlockState(),block.getTileEntity())
                            , entityplayer, partialTicks,face );
                    }

                    int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();
                    hashcode += ~~currHashcode;
                }
                BuildMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
            }
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
    
        if(BuildMode.END_VEC !=null)
            renderPlaneSurfaces(partialTicks);
        
        for (BlockPos blockPos : relToAbsBlockPos.values())
        {
            RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                face, blockPos);
            RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks, 0.0F, 0.0F, 1.0F);
            
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();

        return hashcode;
    }
    
    private EnumDirectionMode getDirectionFromLookAndPullSurfaceFace(Vec3d lookVector, EnumFacing selectedFace) {
        EnumDirectionMode newDir = null;
        double[] camCoords =
                {Math.abs(lookVector.x),
                 Math.abs(lookVector.y),
                 Math.abs(lookVector.z)};
        List b = Arrays.asList(ArrayUtils.toObject(camCoords));
        int maxIndex = b.indexOf(Collections.max(b));
        int minIndex = b.indexOf(Collections.min(b));

        if(selectedFace.getDirectionVec().getY() !=0)
        {
            //pull in Y direction
            if(maxIndex == 0)//means looking in the X direction
                newDir = EnumDirectionMode.ZY;
            else if(maxIndex == 1)//means looking in the Y direction
            {
                if(minIndex == 0)// looking more to Z direction
                    newDir = EnumDirectionMode.XY;
                else if(minIndex == 2)// looking more to X direction
                    newDir = EnumDirectionMode.ZY;
            }
            else if(maxIndex == 2)//means looking in the Z direction
                newDir = EnumDirectionMode.XY;
        }
        else if(selectedFace.getDirectionVec().getX() !=0)
        {
            //pull in X direction
            if(maxIndex == 0)//means looking in the X direction
            {
                if(minIndex == 1)// looking more to Z direction
                    newDir = EnumDirectionMode.XY;
                else if(minIndex == 2)// looking more to Y direction
                    newDir = EnumDirectionMode.XZ;
            }
            else if(maxIndex == 1)//means looking in the Y direction
                newDir = EnumDirectionMode.XZ;
            else if(maxIndex == 2)//means looking in the Z direction
                newDir = EnumDirectionMode.XY;
        }
        else if(selectedFace.getDirectionVec().getZ() !=0)
        {
            //pull in Z direction
            if(maxIndex == 0)//means looking in the X direction
                newDir = EnumDirectionMode.ZY;
            else if(maxIndex == 1)//means looking in the Y direction
                newDir = EnumDirectionMode.XZ;
            else if(maxIndex == 2)//means looking in the Z direction
            {
                if(minIndex == 0)// looking more to X direction
                    newDir = EnumDirectionMode.ZY;
                else if(minIndex == 2)// looking more to Y direction
                    newDir = EnumDirectionMode.XZ;
            }
        }
        return newDir;
    }
    
    private void calcAdjacentBlocksOfSameType2D(IBlockState blockState, BlockPos hitBlockPos, EnumFacing face, Minecraft mc)
    {
        Vec3i dirVec = face.getDirectionVec();
        int limX = 2;
        int limY = 2;
        int limZ = 2;
        int[] selection = {0, 1, -1};
        if (dirVec.getX() != 0) {
            limX = 0;
        } else if (dirVec.getY() != 0) {
            limY = 0;
        } else if (dirVec.getZ() != 0) {
            limZ = 0;
        }

        findAdjacentBlocksOfBlockState(blockState, hitBlockPos, face, limX, limY, limZ, selection, mc);
    }

    private void findAdjacentBlocksOfBlockState(IBlockState blockState, BlockPos hitBlockPos, EnumFacing face, int limX, int limY, int limZ, int[] selection, Minecraft mc)
    {
        int foundBlockCount = 0;
        ArrayList<BlockPos> prevRelBlockPositions = new ArrayList<>();
        prevRelBlockPositions.add(new BlockPos(0, 0, 0));

        ArrayList<BlockPos> currRelBlockPositions = new ArrayList<>();
        while (!prevRelBlockPositions.isEmpty() && foundBlockCount <= ((((int) Math.round(ConfigurationHandler.toolConfig.MAX_BLOCK_COUNT)*10))/11)) {
            currRelBlockPositions.clear();
            for (BlockPos currRelBlockPos : prevRelBlockPositions)
            {
                for (int x = 0; x <= limX; x++) {
                    for (int y = 0; y <= limY; y++) {
                        for (int z = 0; z <= limZ; z++) {
                            foundBlockCount += ifSameBlockStateAndSurfaceAddToList(blockState, hitBlockPos,
                                    currRelBlockPos.add(selection[x], selection[y], selection[z]), face,
                                    currRelBlockPositions, mc);

                        }
                    }
                }
            }
            //each time the new found adjacent blocks are checked again if they have adjacent airblocks
            //Note that they are not added to the prevRelBlockPositions. They replace that list entirely preventing double checking
            prevRelBlockPositions.clear();
            prevRelBlockPositions.addAll(currRelBlockPositions);

        }
    }

    private int ifSameBlockStateAndSurfaceAddToList(IBlockState blockState, BlockPos hitBlockPos, BlockPos relBlockPos, EnumFacing face, ArrayList<BlockPos> currRelBlockPositions, Minecraft mc)
    {
        BlockPos newBlockPos = hitBlockPos.add(relBlockPos);
        //check if the new position contain the same blockType as the initial block
        if ((mc.world.getBlockState(newBlockPos).equals(blockState)) &&
                PlacementHelper.isNotGroundMaterial(mc.world.getBlockState(newBlockPos.offset(face)))) {
            if (!relToAbsBlockPos.containsKey(relBlockPos))
            {
                relToAbsBlockPos.put(relBlockPos, newBlockPos);
                currRelBlockPositions.add(relBlockPos);
                return 1;
            }
        }
        return 0;
    }

    private void pullToPullVec(HashMap<Vec3d, TemplateBlock> blockDict, Vec3d pullVec)
    {
        FILL_VECTOR_LIST_LOCK.blockingAttemptAtLocking();
        for (BlockPos pos: relToAbsBlockPos.values())
        {
            Vec3d start = new Vec3d(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
            if(BuildMode.DELETE_MODE)
                addRectVector(start, pullVec, blockDict,false,false, 0.0f, 1.0f, 0.0f);
            else
            {
                //take the block of the surface and pull that
                IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(pos);
                TileEntity tileEntity = Minecraft.getMinecraft().world.getTileEntity(pos);
                EnumFacing side = EnumFacing.NORTH;
                if(blockState.getBlock() instanceof BlockSkull)
                {
                    side = blockState.getValue(BlockSkull.FACING);
                }
                TemplateBlock newblock = new TemplateBlock(side,0,0,0,blockState,tileEntity);


                addRectVector(newblock,start, pullVec, blockDict,true,false, 0.0f, 1.0f, 0.0f);
            }
                        }
        FILL_VECTOR_LIST_LOCK.releaseLock();
    }

    private void pullToNextSurface(HashMap<Vec3d, TemplateBlock> blockDict, double maxPullVecLenght)
    {
        EnumFacing face = BuildMode.START_BLOCK.getFace();

        //transform blockpos list into a TemplateBlockList
        ArrayList<TemplateBlock> templateBlocks = new ArrayList<>();
        for (BlockPos pos: relToAbsBlockPos.values())
        {
            templateBlocks.add(new TemplateBlock( face,pos,BuildMode.START_BLOCK.getBlockState(),BuildMode.START_BLOCK.getTileEntity()));
        }

        //change block positions to the next SolidBlock
        HelpFunctions.updateListToFirstSolidBlockInDir(templateBlocks,face);

        if(relToAbsBlockPos.values().toArray().length == 0)
            return;

        BlockPos beginBlock = (BlockPos) relToAbsBlockPos.values().toArray()[0];

        // create a block from the starting surface to the blocks at the solidblocks
        for(TemplateBlock block : templateBlocks)
        {
            Vec3d newPullVec = null;
            Vec3d start = null;
            if ( face.equals(EnumFacing.SOUTH) || face.equals(EnumFacing.NORTH))
            {
                newPullVec = new Vec3d(0,0,block.getZ_offset()-beginBlock.getZ());
                start =new Vec3d(block.getX_offset()+0.5,block.getY_offset()+0.5,beginBlock.getZ()+0.5);
            }
            else if (face.equals(EnumFacing.EAST) || face.equals(EnumFacing.WEST))
            {
                newPullVec = new Vec3d(block.getX_offset()-beginBlock.getX(),0,0);
                start = new Vec3d(beginBlock.getX()+0.5,block.getY_offset()+0.5,block.getZ_offset()+0.5);
            }
            else if (face.equals(EnumFacing.UP) || face.equals(EnumFacing.DOWN) )
            {
                newPullVec = new Vec3d(0,block.getY_offset()-beginBlock.getY(),0);
                start = new Vec3d(block.getX_offset()+0.5,beginBlock.getY()+0.5,block.getZ_offset()+0.5);
            }

            //limit the size of this newPullVec
            if(newPullVec.length() > maxPullVecLenght)
            {
                newPullVec = newPullVec.scale(maxPullVecLenght /newPullVec.length());
            }

            if(BuildMode.DELETE_MODE)
                addRectVector(start,newPullVec, blockDict,false,false, 0.0f, 1.0f, 0.0f);
            else
                addRectVector(start,newPullVec, blockDict,true,false, 0.0f, 1.0f, 0.0f);

        }
    }

    @Override
    public boolean hasNoAlterPositionMode()
    {
        return true;
    }

    @Override
    public boolean hasMiddlePosition()
    {
        return false;
    }

    @Override
    public EnumPosOrder[] getPosOrder()
    {
        return new EnumPosOrder[]{EnumPosOrder.START_POS,EnumPosOrder.END_POS};
    }

    @Override
    public boolean canUseAbsoluteCoord()
    {
        return false;
    }

    @Override
    public boolean allowRightClickException(Item item)
    {
        return BuildMode.RIGHT_CLICK_NUMBER == this.finalRightClick;
    }
}
