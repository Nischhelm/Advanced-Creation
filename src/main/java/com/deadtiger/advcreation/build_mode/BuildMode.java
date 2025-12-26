package com.deadtiger.advcreation.build_mode;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_mode.tool_mode.*;
import com.deadtiger.advcreation.build_mode.utility.*;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.utility.CursorVector;
import com.deadtiger.advcreation.utility.MultiThreadLock;
import com.deadtiger.advcreation.utility.PlacementHelper;
import com.deadtiger.advcreation.utility.VecTransformer;
import net.minecraft.block.BlockSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.awt.*;
import java.util.*;

import static com.deadtiger.advcreation.utility.PlacementHelper.isNotGroundMaterial;
import static com.deadtiger.advcreation.utility.PlacementHelper.parseVec;

public class BuildMode {
    //public static EnumToolMode adjustMode = EnumToolMode.SINGLE_TOOL;
    public static BaseToolMode[] TOOL_MODES = {new SingleToolMode(), new LineToolMode(), new RectangleToolMode(),
        new CurveToolMode(), new CircleToolMode(), new PullToolMode(), new CopyPasteToolMode(), new MoveToolMode(), new FillGapToolMode()};
    public static int TOOLMODE_INDEX = 0;
    public static boolean TOOLMODE_HAS_CHANGED = false;
    public static BaseToolMode TOOLMODE = new SingleToolMode();
    public static EnumDirectionMode WORK_DIRECTION_MODE = EnumDirectionMode.FREE;
    public static EnumDirectionMode DIRECTION_MODE = EnumDirectionMode.FREE;
    public static final int DIR_SELECTION_PLANE_SIZE = 1000000;
    public static EnumFillMode FILL_MODE = EnumFillMode.FILL;
    
    // start block has global blockpos coordinates
    public static TemplateBlock START_BLOCK = null;
    public static BlockPos LAST_BLOCK = null;
    public static double CURR_HITBLOCK_WIDTH = 1.0;
    public static boolean BOTH_HIT_AND_HAND_ARE_SLAB = false;

    //relative vector
    public static Vec3d LINE_VEC = null;
    public static Vec3d HITVEC = null;
    public static Vec3d SECONDARY_LINE_VEC = null;
    
    //absolute vector
    public static Vec3d START_VEC = null;
    public static Vec3d NEW_START_VEC = null;// some toolmodes change the startVec
    public static Vec3d END_VEC = null;

    //prev pos and vectors for Alt-mode adjustments
    private static BlockPos PREV_POS = null;
    private static Vec3d PREV_HITVEC = null;
    private static CursorVector PREV_CURSOR_VEC = null;

    //to make the alt mode offset work
    private static int PREV_X_OFFSET = 0;
    private static int PREV_Z_OFFSET = 0;
    private static boolean HELD_ALT = false;

    public static int MOUSE_X_OFFSET = 0;
    public static int MOUSE_Y_OFFSET = 0;
    public static int MOUSE_Z_OFFSET = 0;
    
    //list with blocks that are preview and eventually placed
    public static ArrayList<TemplateBlock> CURR_TOOL_BLOCKS = new ArrayList<>();
    public static MultiThreadLock CURR_TOOL_BLOCKS_LOCK = new MultiThreadLock();
    public static boolean CLEAR_CURR_TOOL_BLOCKS = false;
    
    public static ArrayList<TemplateBlock> CURR_PREVIEW_BLOCKS = new ArrayList<>();
    public static MultiThreadLock CURR_PREVIEW_BLOCKS_LOCK = new MultiThreadLock();
    
    //good positions to create nice circles
    public static boolean JUMP_TO_POINT = true;//done
    
    public static int RIGHT_CLICK_NUMBER = 0;
    
    //too fill
    public static HashMap<Double, FillVector> FILL_VECTOR_DICT = new HashMap<>();
    
    //debug
    public static boolean SHOW_PREVIEW_BLOCKS = true;
    
    //pull mode mechanics
    public static BlockPos CURR_HITBLOCK_POS;
    
    //delete mechanics
    public static boolean DELETE_MODE = false;
    
    //color of the indicator
    public static float RED = 1.0f;
    public static float GREEN = 0.0f;
    public static float BLUE = 0.0f;

    public static boolean USING_ABS_COORD = false;
    
    public static void rotateToolMode() {
        TOOLMODE_INDEX++;
        if (TOOLMODE_INDEX >= TOOL_MODES.length)
            TOOLMODE_INDEX = 0;
        TOOLMODE_HAS_CHANGED = true;
    
    }
    
    public static void rotateOpositeToolMode() {
        TOOLMODE_INDEX--;
        if (TOOLMODE_INDEX < 0)
            TOOLMODE_INDEX = TOOL_MODES.length -1;
        TOOLMODE_HAS_CHANGED = true;
        
    }
    
    public static void changeToolModeTo(int index)
    {
        if(index >=  TOOL_MODES.length)
            index =  TOOL_MODES.length-1;
        else if(index < 0)
            index = 0;

        TOOLMODE_INDEX = index;
        TOOLMODE_HAS_CHANGED = true;
    }
    
    public static void applyToolModeChange()
    {
        TOOLMODE = TOOL_MODES[TOOLMODE_INDEX];
        DELETE_MODE = TOOLMODE instanceof CopyPasteToolMode || TOOLMODE instanceof MoveToolMode;
        clearBuildMode();
        TOOLMODE_HAS_CHANGED = false;
//        updateCoordInfoDisplay();
    }

    public static void updateCoordInfoDisplay()
    {
        GuiOverlayManager.setShowMiddlePos(TOOLMODE.hasMiddlePosition());
        GuiOverlayManager.setShowEndPos(TOOLMODE.hasEndPosition());
    }

    public static void rotateBlocks()
    {
       TOOLMODE.rotate();
    }

    public static void mirrorZY() {
        TOOLMODE.mirrorZY();}
    
    
    public static boolean cancelLeftClick()
    {
        return TOOLMODE.cancelLeftClick();
    }
    
    public static boolean cancelRightClick()
    {
        return TOOLMODE.cancelRightClick();
    }
    
    /**
     * do the action associated to leftclick and return true if the BuildMode action has changed something in the world
     *(this is important for adding the action to the actionHistory(undo functionality))
     * @param currAction
     * @param player
     * @return
     */
    public static Boolean leftClick(Action currAction, EntityPlayer player)
    {
        //only let left clicks count if you didn't rightclick before(in process of building)
        if(RIGHT_CLICK_NUMBER == 0 || DELETE_MODE)
        {

            if(!DELETE_MODE)
            {
                DELETE_MODE = true;
                TOOLMODE.activateDeleteMode(CURR_HITBLOCK_WIDTH);
            }
            
            boolean leftClick = TOOLMODE.leftClick(currAction, player, RIGHT_CLICK_NUMBER);

            if(!USING_ABS_COORD && TOOLMODE.startFinishBuilding(RIGHT_CLICK_NUMBER))
            {
                BuildMode.clearMouseOffset();
                //some toolmodes handle the placement of the blocks themselves
                if(!(TOOLMODE instanceof MoveToolMode || TOOLMODE instanceof CopyPasteToolMode || TOOLMODE instanceof SingleToolMode ))
                    placeCurrToolBlocks(currAction,player);

                BuildMode.RIGHT_CLICK_NUMBER += TOOLMODE.addToRightClickNumber();
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());

                if(!( TOOLMODE instanceof CopyPasteToolMode ))
                    clearBuildMode();
                else
                    //for copyPaste mode the rightclicknumber addition is reverted so you can continue pasting
                    BuildMode.RIGHT_CLICK_NUMBER -= TOOLMODE.addToRightClickNumber();

            }
            else
            {
                BuildMode.RIGHT_CLICK_NUMBER += TOOLMODE.addToRightClickNumber();
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());
            }
            updateCoordInfoDisplay();
            return leftClick;
            
        }
        else
        {
            clearBuildMode();
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),"Canceled",true,true);
//            updateCoordInfoDisplay();
            return false;
        }
    }

    /**
     * do the action associated to rightclick and return true if the BuildMode action has changed something in the world
     *(this is important for adding the action to the actionHistory(undo functionality))
     * @param currAction
     * @param player
     * @return
     */
    public static boolean rightClick(Action currAction, EntityPlayer player)
    {
        //only let right clicks count if you didn't leftclick before(in process of deleting)
        if((RIGHT_CLICK_NUMBER == 0 || !DELETE_MODE) || TOOLMODE.specialRightClickPermision())
        {
            DELETE_MODE = false;
            boolean rightClicked = TOOLMODE.rightClick(currAction, player, RIGHT_CLICK_NUMBER);

            if (!USING_ABS_COORD && TOOLMODE.startFinishBuilding(RIGHT_CLICK_NUMBER))
            {
                BuildMode.clearMouseOffset();
                //some toolmodes handle the placement of the blocks themselves
                if(!(TOOLMODE instanceof MoveToolMode || TOOLMODE instanceof CopyPasteToolMode))// || TOOLMODE instanceof SingleToolMode))
                {
                    placeCurrToolBlocks(currAction, player);
                }

                BuildMode.RIGHT_CLICK_NUMBER += TOOLMODE.addToRightClickNumber();
                if(START_BLOCK.getBlockState().getBlock() instanceof BlockSign)
                    GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),"RightClick Sign To Edit Text",true,false,0xFF0000AA,true);
                else
                    GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());

                if(!( TOOLMODE instanceof CopyPasteToolMode ))
                    clearBuildMode();
                else
                    //for copyPaste mode the rightclicknumber addition is reverted so you can continue pasting
                    BuildMode.RIGHT_CLICK_NUMBER -= TOOLMODE.addToRightClickNumber();
            }
            else
            {

                BuildMode.RIGHT_CLICK_NUMBER += TOOLMODE.addToRightClickNumber();
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());
            }
            updateCoordInfoDisplay();
            return rightClicked;
        }
        else
        {
            if(TOOLMODE.allowRightClickCancel())
            {
                clearBuildMode();
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),"Canceled",true,true);
            }

        }
        return false;
    }

    /**
     * Main method used by the drawBlockHighlightEvent to set a new block hit by the player
     *
     * @param block
     * @param hitVec
     * @param hitBlockPos
     * @param face
     * @param bothHitAndHandAreSlab
     */
    public static void addNewBlock(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face, double currHitblockWidth, boolean bothHitAndHandAreSlab)
    {

        BOTH_HIT_AND_HAND_ARE_SLAB = bothHitAndHandAreSlab;
        if(BOTH_HIT_AND_HAND_ARE_SLAB)
            CURR_HITBLOCK_WIDTH = currHitblockWidth/2; //you don't want to delete the block under the slab
        else if(currHitblockWidth != 1)
            CURR_HITBLOCK_WIDTH = currHitblockWidth*1.1; // quick fix to allow of the deletion of blocks that are not 1 in width with anything other then single tool
        else
            CURR_HITBLOCK_WIDTH = currHitblockWidth;

        if(CLEAR_CURR_TOOL_BLOCKS)
        {
            updateCurrToolBlocks(null,null);
            CLEAR_CURR_TOOL_BLOCKS = false;
        }
        
        //if in AUTO direction mode calculate with plane the player is in
        if(DIRECTION_MODE == EnumDirectionMode.AUTO)
            WORK_DIRECTION_MODE = IsometricCamera.calcAutoDirectionMode();
        else
            WORK_DIRECTION_MODE = DIRECTION_MODE;

//        if(TOOLMODE instanceof CopyPasteToolMode || TOOLMODE instanceof MoveToolMode)
            TOOLMODE.addNewBlock(block,hitVec, hitBlockPos,face);
//        else
//        {
//            block.setBlockPos(block.getBlockPos().add(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET));
//            TOOLMODE.addNewBlock(block,hitVec.addVector(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET), hitBlockPos.add(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET),face);
//        }

    }

    /**
     * Main method used by all toolmodes to update the blocks that the player will place
     *
     * this one needs to be thread safe because it is called during the preview and so not in sync with the placeblock method
     * Only edit the CurrToolBlocks in this method and don't call this method anywhere else but addNewBlock method
     * never call a return in this method !!!!
     * @param endGlobalblock
     * @param hitVec
     */
    public static void updateCurrToolBlocks(TemplateBlock endGlobalblock,Vec3d hitVec)
    {
        
        //this one needs to be thread safe because it is called during the preview and so not in sync with the placeblock method
        //Only edit the CurrToolBlocks in this method and don't call this method anywhere else but addNewBlock method
        //never call a return in this method !!!!
        if(CURR_TOOL_BLOCKS_LOCK.attemptLocking())
        {
            CURR_TOOL_BLOCKS.clear();
            FILL_VECTOR_DICT.clear();
            
            if(endGlobalblock != null)
                LAST_BLOCK = endGlobalblock.getBlockPos();
            
            // Alters startVec to create newStartVec
            // Changes lineVec and lastBlock to the current configuration
            Vec3d newStartVec = transformStartVecToDirectionMode(hitVec);

            
//            if(LINE_VEC != null && LAST_BLOCK != null)
//            {
                TOOLMODE.updateCurrToolBlocks(endGlobalblock,hitVec,newStartVec);
//            }
            
            //if direction mode is Ground reduce all the y of each block in currToolBlocks to 1 above ground level
            if(WORK_DIRECTION_MODE == EnumDirectionMode.GROUND && !(TOOLMODE instanceof FillGapToolMode) && !(TOOLMODE instanceof PullToolMode) )
            {
                updateCurrToolBlocksToGround(CURR_TOOL_BLOCKS);
            }
            
            //Copy currToolBlocks to currPreviewBlocks to avoid bugs
            if(!(TOOLMODE instanceof CopyPasteToolMode || TOOLMODE instanceof MoveToolMode || TOOLMODE instanceof FillGapToolMode))
            {
                if(CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
                {
                    CURR_PREVIEW_BLOCKS = new ArrayList<>(CURR_TOOL_BLOCKS);
                    CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
                }
            }

            
            //calculate the size and blocks in the selection and set the count display
            if(!(TOOLMODE instanceof CopyPasteToolMode) && !(TOOLMODE instanceof MoveToolMode)&& !(TOOLMODE instanceof SingleToolMode))
            {
                GuiOverlayManager.calcAndDisplayCurrentToolBlocksSize(CURR_TOOL_BLOCKS);
            }

            CURR_TOOL_BLOCKS_LOCK.releaseLock();
        }
    }

    /**
     * Method used by the ToGround mode to update all the CURR_TOOL_BlOCKS to position following the ground level
     */
    private static void updateCurrToolBlocksToGround(ArrayList<TemplateBlock> blockList)
    {
        //get the currtoolBlock is 3D matrix format
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> rows = HelpFunctions.getRowDirectionXZY(blockList);
    
        for (ArrayList<ArrayList<TemplateBlock>> rowX: rows)
        {
            for (ArrayList<TemplateBlock> rowZ: rowX)
            {
                int maxY = rowZ.size();
                //get the minimum Y for the current X,Z coordinates
                BlockPos currPos = null;
                for (TemplateBlock block: rowZ)
                {
                    if(block !=null && (currPos == null || currPos.getY() > block.getY_offset()))
                            currPos= block.getBlockPos();
                }

                //if the row was not empty
                if(currPos != null)
                {
                    //start from the lowest block and count how many block to the ground
                    BlockPos worldPos = new BlockPos(currPos).add(0,maxY,0);
                    World world = Minecraft.getMinecraft().world;
                    IBlockState iblockstate2 = world.getBlockState(worldPos);
    
                    int maxTravel = 100;
                    int depthCount = 0;
                    int heightCount= 0;
                    
                    //go up till the lowest block is right above the surface
                    while(!isNotGroundMaterial(iblockstate2) && heightCount < maxTravel)
                    {
                        worldPos = worldPos.up();
                        iblockstate2 = world.getBlockState(worldPos);
                        heightCount++;
                    }
                    
                    //keep going down while the block is made from an non-ground material
                    while(isNotGroundMaterial(iblockstate2) && depthCount < maxTravel)
                    {
                        worldPos = worldPos.down();
                        iblockstate2 = world.getBlockState(worldPos);
                        depthCount++;
                    }
                    depthCount--;
                    depthCount -= maxY;
                    
                    //substract the count of blocks from all valid blocks in this row
                    for (TemplateBlock block: rowZ)
                    {
                        if(block !=null)
                            block.setY_offset(block.getY_offset() - depthCount + heightCount);
                    }
                }
            }

        }
        
    }

    public static void placeCurrToolBlocks(Action currAction, EntityPlayer player)
    {
        //blocking the whole application untill a different thread is done editing the currToolBlocks list but it should work
        CURR_TOOL_BLOCKS_LOCK.blockingAttemptAtLocking();

        for (TemplateBlock block: CURR_TOOL_BLOCKS)
        {
            PlaceTemplateMode.placeBlockClient(block.getBlockPos(), player, currAction, new TemplateBlock(block.getFace(),0,0,0,block.getBlockState(),block.getTileEntity()));
        }

        NetworkPlaceBlockListFormatter.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, CURR_TOOL_BLOCKS,new ArrayList<>(),null,null,currAction);

        CURR_TOOL_BLOCKS_LOCK.releaseLock();
    }


    public static Vec3d transformStartVecToDirectionMode(Vec3d hitVec)
    {
        NEW_START_VEC = null;
        if(START_VEC != null && hitVec != null)
        {
            NEW_START_VEC = TOOLMODE.getNewStartVec(START_VEC, SECONDARY_LINE_VEC);

            CursorVector cursorVector = ModEntity.currCursorVec;
            if(BuildMode.HELD_ALT)
                cursorVector = BuildMode.getPrevCursorVec();

            //allow different coordinates to be non-zero dependend on the workDirectionMode
//            if(WORK_DIRECTION_MODE == EnumDirectionMode.FREE || WORK_DIRECTION_MODE == EnumDirectionMode.GROUND )
//                END_VEC = hitVec;
            else if(WORK_DIRECTION_MODE == EnumDirectionMode.XY)
            {
                if(BuildMode.isUsingAbsCoord())
                    END_VEC = new Vec3d(hitVec.x, hitVec.y, NEW_START_VEC.z);
                else
                    END_VEC = VecTransformer.transformHitVecToXYPlane(hitVec, NEW_START_VEC,cursorVector);
            }
            else if(WORK_DIRECTION_MODE == EnumDirectionMode.ZY)
            {
                if(BuildMode.isUsingAbsCoord())
                    END_VEC = new Vec3d(NEW_START_VEC.x, hitVec.y, hitVec.z);
                else
                    END_VEC = VecTransformer.transformHitVecToZYPlane(hitVec, NEW_START_VEC,cursorVector);
            }
            else if(WORK_DIRECTION_MODE == EnumDirectionMode.XZ || (WORK_DIRECTION_MODE == EnumDirectionMode.FREE && TOOLMODE instanceof CircleToolMode && RIGHT_CLICK_NUMBER >= 1))
            {
                if(BuildMode.isUsingAbsCoord())
                    END_VEC = new Vec3d(hitVec.x, NEW_START_VEC.y, hitVec.z);
                else
                    END_VEC = VecTransformer.transformHitVecToXZPlane(hitVec, NEW_START_VEC,cursorVector);
            }
            else
                END_VEC = hitVec;

//            TOOLMODE.normalHitPos = new BlockPos(END_VEC);

//            END_VEC = END_VEC.addVector(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET);
            if(ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER)
                END_VEC = parseVec(END_VEC);
            LINE_VEC = END_VEC.subtract(NEW_START_VEC);
            LAST_BLOCK = new BlockPos(LINE_VEC.add(NEW_START_VEC));

        }
        return NEW_START_VEC;
    }


    public static int drawPreview(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        return TOOLMODE.drawPreview(entityplayer,hitVec,face,partialTicks);
    }
    
    public static TemplateBlock getStartBlock()
    {
        if(START_BLOCK != null)
            return START_BLOCK;
        else
            return new TemplateBlock(EnumFacing.NORTH, BlockPos.ORIGIN, Blocks.AIR.getDefaultState());
    }
    
    public static void setStartBlock(TemplateBlock startBlock) {
        BuildMode.START_BLOCK = startBlock;
        if(CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
        {
            BuildMode.CURR_PREVIEW_BLOCKS.clear();
            if(BuildMode.START_BLOCK != null)
                BuildMode.CURR_PREVIEW_BLOCKS.add(BuildMode.START_BLOCK);
            CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
        }

    }

    public static Vec3d getStartVec()
    {
        if(BuildMode.START_VEC != null)
            return START_VEC;
        else
            return Vec3d.ZERO;
    }
    
    public static void setStartVec(Vec3d startVec)
    {
        BuildMode.START_VEC = PlacementHelper.parseVec(startVec);
    }

    public static Vec3d getEndVec()
    {
        return END_VEC;
    }
    
    public static void setEndVec(Vec3d endVec)
    {
        BuildMode.END_VEC = endVec;
    }

    public static void clearBuildMode()
    {
        RIGHT_CLICK_NUMBER = 0;
        END_VEC = Vec3d.ZERO;
        LINE_VEC = Vec3d.ZERO;
        SECONDARY_LINE_VEC = Vec3d.ZERO;
        CLEAR_CURR_TOOL_BLOCKS = true;
        TOOLMODE.cancelBuilding();
        DELETE_MODE = false;
        PlaceTemplateMode.clearMouseOffset();
        BuildMode.clearMouseOffset();
        GuiOverlayManager.clearDisplay();
        updateCoordInfoDisplay();
    }

    /**
     * Only relevant for the Circle mode
     */
    public static void toggleJumpToPoint()
    {
        JUMP_TO_POINT = !JUMP_TO_POINT;
    }

    
    public static void setColor(float red,float green,float blue)
    {
        BuildMode.RED = red;
        BuildMode.GREEN = green;
        BuildMode.BLUE = blue;
    }

    public static void setColor(Color color)
    {
        float[] comp = color.getComponents(null);
        RED = comp[0];
        GREEN = comp[1];
        BLUE =comp[2];
    }

    public static void updateAlterModeInactive(BlockPos position, Vec3d hitVec)
    {
        if(!(TOOLMODE instanceof CopyPasteToolMode || TOOLMODE instanceof  MoveToolMode) )
        {
            if (BuildMode.RIGHT_CLICK_NUMBER > 0)
            {
                if (START_VEC != null && hitVec != null)
                {
                    NEW_START_VEC = TOOLMODE.getNewStartVec(START_VEC, SECONDARY_LINE_VEC);
                    hitVec = VecTransformer.transformStartVecToDirectionMode(BuildMode.WORK_DIRECTION_MODE, hitVec, NEW_START_VEC, ModEntity.currCursorVec);
                    position = new BlockPos(hitVec);
                }
            }
        }

        setPrevPos(position);
        setPrevHitvec(hitVec);
        setPrevCursorVec(ModEntity.currCursorVec);
        setHeldAlt(false);
    }

    public static BlockPos getAlterModePosition(BlockPos position, Vec3d hitVec)
    {
        if(hasNoAlterPositionMode())
            return position;


        if (PREV_POS != null)
        {
            if (!HELD_ALT)
            {
                PREV_X_OFFSET = MOUSE_X_OFFSET;
                PREV_Z_OFFSET = MOUSE_Z_OFFSET;
                HELD_ALT = true;
            }

            Vec3d prevPosVec = new Vec3d(PREV_POS.getX() + 0.5, PREV_POS.getY() + 0.5, PREV_POS.getZ() + 0.5);

            Vec3d newPosVec2 = VecTransformer.transformHitVecToXZPlane(hitVec, prevPosVec, ModEntity.currCursorVec);
            BlockPos newPosition2 = new BlockPos(Math.floor(newPosVec2.x), Math.floor(newPosVec2.y), Math.floor(newPosVec2.z));

            Vec3d newPrevPosVec2 = VecTransformer.transformHitVecToXZPlane(PREV_HITVEC, prevPosVec, PREV_CURSOR_VEC);
            BlockPos newPrevPos2 = new BlockPos(Math.floor(newPrevPosVec2.x), Math.floor(newPrevPosVec2.y), Math.floor(newPrevPosVec2.z));

            MOUSE_X_OFFSET = (PREV_X_OFFSET + newPosition2.getX() - newPrevPos2.getX());
            MOUSE_Z_OFFSET = (PREV_Z_OFFSET + newPosition2.getZ() - newPrevPos2.getZ());

            return PREV_POS;
        }


        return position;

    }

    public static void setHeldAlt(boolean held)
    {
        HELD_ALT = held;
    }

    public static void setPrevPos(BlockPos pos)
    {
        PREV_POS = pos;
    }

    public static Vec3d getPrevHitvec()
    {
        return PREV_HITVEC;
    }

    public static void setPrevHitvec(Vec3d prevHitvec)
    {
        PREV_HITVEC = prevHitvec;
    }

    public static CursorVector getPrevCursorVec()
    {
        return PREV_CURSOR_VEC;
    }

    public static void setPrevCursorVec(CursorVector prevCursorVec)
    {
        PREV_CURSOR_VEC = prevCursorVec;
    }

    public static void clearMouseOffset()
    {
        TOOLMODE.clearMouseOffset();
    }

    public static void changeYBuildMode(double wheel)
    {
        if(hasNoAlterPositionMode())
            return;

        if (wheel > 0)
            MOUSE_Y_OFFSET++;
        else
            MOUSE_Y_OFFSET--;
    }

    public static boolean hasNoAlterPositionMode()
    {
        return TOOLMODE.hasNoAlterPositionMode();
    }

    public static boolean isHeldAlt()
    {
        return HELD_ALT;
    }

    public static boolean isUsingAbsCoord()
    {
        return USING_ABS_COORD;
    }

    public static void setUsingAbsCoord(boolean usingAbsCoord)
    {
        BuildMode.USING_ABS_COORD = usingAbsCoord;
    }

    public static EnumPosOrder[] getToolPosOrder()
    {
        return TOOLMODE.getPosOrder();
    }

    public static boolean canUseAbsoluteCoord()
    {
        return TOOLMODE.canUseAbsoluteCoord();
    }

    public static boolean allowRightClickException(Item item)
    {
        return TOOLMODE.allowRightClickException(item);
    }
}
