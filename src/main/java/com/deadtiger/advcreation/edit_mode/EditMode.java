package com.deadtiger.advcreation.edit_mode;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.build_mode.utility.FillVector;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.edit_mode.adjust_modes.*;
import com.deadtiger.advcreation.edit_mode.utility.EnumTerrainMode;
import com.deadtiger.advcreation.edit_mode.utility.EnumTerrainShapeMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.utility.MultiThreadLock;
import com.deadtiger.advcreation.utility.PlacementHelper;
import com.deadtiger.advcreation.utility.VecTransformer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.*;

public class EditMode
{
    public static BaseAdjustMode[] ADJUST_MODES = {new PaintAdjustMode(), new PlantAdjustMode(), new PaintBucketAdjustMode(), new DigRaiseAdjustMode(), new SmoothAdjustMode(),new LevelAdjustMode()};
    public static BaseAdjustMode ADJUST_MODE = new PaintAdjustMode();
    public static int ADJUST_MODE_INDEX = 0;
    public static boolean ADJUST_MODE_HAS_CHANGED = false;
    public static EnumDirectionMode WORK_DIRECTION_MODE = EnumDirectionMode.AUTO;
    public static EnumTerrainShapeMode TERRAIN_SHAPE_MODE = EnumTerrainShapeMode.HILL;
    public static EnumTerrainMode ONLY_TERRAIN_MODE = EnumTerrainMode.ONLY_TERRAIN;

    // start block has global blockpos coordinates
    public static TemplateBlock START_BLOCK = null;
    public static BlockPos LAST_BLOCK = null;
    public static double CURR_HITBLOCK_WIDTH = 1.0;

    //relative vector
    public static Vec3d LINEVEC = null;
    public static Vec3d HITVEC = null;
    public static Vec3d SECONDARY_LINEVEC = null;

    //absolute vector
    public static Vec3d STARTVEC = null;
    public static Vec3d NEW_START_VEC = null;// some toolmodes change the startVec
    public static Vec3d END_VEC = null;

    //list with blocks that are preview and eventually placed
    public static ArrayList<TemplateBlock> CURR_TOOL_BLOCKS = new ArrayList<>();
    public static MultiThreadLock CURR_TOOL_BLOCKS_LOCK = new MultiThreadLock();
    public static boolean CLEAR_CURR_TOOL_BLOCKS = false;

    public static ArrayList<TemplateBlock> CURR_PREVIEW_BLOCKS = new ArrayList<>();
    public static MultiThreadLock CURR_PREVIEW_BLOCKS_LOCK = new MultiThreadLock();
    public static boolean DRAW_PREVIEW_OUTLINE_ONLY = false;

    public static ArrayList<TemplateBlock> CURR_PREVIEW_OUTLINE_BLOCKS = new ArrayList<>();
    public static MultiThreadLock CURR_PREVIEW_OUTLINE_BLOCKS_LOCK = new MultiThreadLock();

    public static int RIGHT_CLICK_NUMBER = 0;

    //too fill
    public static HashMap<Double, FillVector> FILL_VECTOR_MAP = new HashMap<>();

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

    public static int loops = 50;

    public static boolean showLegend = false;

    public static int addRadiusRepetitionCount = 1;   //how many times has addRadius been called in short succession (under resetDelay time between consecutive calls)
    public static int  scrollssUntilAddRadiusIncrease = 8; // after how many scrolls does the value added to radius increase per scroll
    public static double lastAddRadiusValue = 0;          //what did you add to radius last time
    public static long lastAddRadiusTime = 0;           //when was addRadius called last
    public static long resetDelay = 350;                // time in ms before reseting addRadiusMultiplier



    public static void rotateAdjustMode()
    {
        ADJUST_MODE_INDEX++;
        if (ADJUST_MODE_INDEX >= ADJUST_MODES.length)
            ADJUST_MODE_INDEX = 0;
        ADJUST_MODE_HAS_CHANGED = true;

    }

    public static void rotateOpositeAdjustMode()
    {
        ADJUST_MODE_INDEX--;
        if (ADJUST_MODE_INDEX < 0)
            ADJUST_MODE_INDEX = ADJUST_MODES.length - 1;
        ADJUST_MODE_HAS_CHANGED = true;

    }

    public static void rotateOnlyTerrainMode()
    {
        ONLY_TERRAIN_MODE = ONLY_TERRAIN_MODE.rotateMode();
    }

    public static void rotateTerrainShapeMode()
    {
        TERRAIN_SHAPE_MODE = TERRAIN_SHAPE_MODE.rotateMode();
    }

    public static void rotateOpositeTerrainShapeMode()
    {
        TERRAIN_SHAPE_MODE = TERRAIN_SHAPE_MODE.rotateOpositeMode();
    }

    public static void applyAdjustModeChange()
    {
        updateCoordInfoDisplay();

        ADJUST_MODE = ADJUST_MODES[ADJUST_MODE_INDEX];
        if (ADJUST_MODE instanceof PlantAdjustMode)
            DELETE_MODE = true;
        else
            DELETE_MODE = false;
        ADJUST_MODE_HAS_CHANGED = false;
    }

    public static void changeAdjustModeTo(int index)
    {
        if(index >= ADJUST_MODES.length)
            index =  ADJUST_MODES.length-1;
        else if(index < 0)
            index = 0;

        ADJUST_MODE_INDEX = index;
        ADJUST_MODE_HAS_CHANGED = true;

    }

    public static boolean cancelLeftClick()
    {
        return ADJUST_MODE.cancelLeftClick();
    }

    public static boolean cancelRightClick()
    {
        return ADJUST_MODE.cancelRightClick();
    }

    /**
     * do the action associated to leftclick and return true if the SculptMode action has changed something in the world
     * (this is important for adding the action to the actionHistory(undo functionality))
     *
     * @param currAction
     * @param player
     * @return
     */
    public static Boolean leftClick(Action currAction, EntityPlayer player)
    {
        //only let left clicks count if you didn't rightclick before(in process of building)
        if (RIGHT_CLICK_NUMBER == 0 || DELETE_MODE)
        {
            if (!DELETE_MODE)
            {
                DELETE_MODE = true;
//                ADJUST_MODE.activateDeleteMode();
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),"Switched to " + ADJUST_MODE.getGuiOverlayMessage(EditMode.DELETE_MODE) ,true,false,0xFF0000AA,false);
                return false;

            }

            boolean leftClick = ADJUST_MODE.leftClick(currAction, player, RIGHT_CLICK_NUMBER);
            if (ADJUST_MODE.startCancelBuilding(RIGHT_CLICK_NUMBER))
            {
                placeCurrToolBlocks(currAction, player);
                cancelBuilding();
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Active", true, false);
            }
            else
            {
                EditMode.RIGHT_CLICK_NUMBER += ADJUST_MODE.addToRightClickNumber();
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());
            }
            return leftClick;
        }
        else
        {
            cancelBuilding();
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Canceled", true, true);

        }
        return true;
    }

    /**
     * do the action associated to rightclick and return true if the SculptMode action has changed something in the world
     * (this is important for adding the action to the actionHistory(undo functionality))
     *
     * @param currAction
     * @param player
     * @return
     */
    public static boolean rightClick(Action currAction, EntityPlayer player)
    {
        //only let right clicks count if you didn't leftclick before(in process of deleting)
        if (RIGHT_CLICK_NUMBER == 0 || !DELETE_MODE)
        {
            if (DELETE_MODE)
            {
                DELETE_MODE = false;
//                ADJUST_MODE.activateDeleteMode();
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),"Switched to " + ADJUST_MODE.getGuiOverlayMessage(EditMode.DELETE_MODE),true,false,0xFF0000AA,false);
                return false;
            }

            boolean rightClicked = ADJUST_MODE.rightClick(currAction, player, RIGHT_CLICK_NUMBER);
            if (ADJUST_MODE.startCancelBuilding(RIGHT_CLICK_NUMBER))
            {
                placeCurrToolBlocks(currAction, player);
                cancelBuilding();
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Active", true, false);
            }
            else
            {
                EditMode.RIGHT_CLICK_NUMBER += ADJUST_MODE.addToRightClickNumber();
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());
            }

            return rightClicked;
        }
        else
        {
            cancelBuilding();
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Canceled", true, true);
        }
        return false;
    }

    public static void addNewBlock(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face, EntityPlayer player, double currHitblockWidth)
    {
        if(currHitblockWidth != 1)
            CURR_HITBLOCK_WIDTH = currHitblockWidth*1.1; // quick fix to allow of the deletion of blocks that are not 1 in width with anything other then single tool
        else
            CURR_HITBLOCK_WIDTH = currHitblockWidth;

        if (ADJUST_MODE_HAS_CHANGED)
            applyAdjustModeChange();

        if (CLEAR_CURR_TOOL_BLOCKS)
        {
            updateCurrToolBlocks(null, null);
            CLEAR_CURR_TOOL_BLOCKS = false;
        }

        ADJUST_MODE.addNewBlock(block, hitVec, hitBlockPos, face);
    }

    public static void updateCurrToolBlocks(TemplateBlock endGlobalblock, Vec3d hitVec)
    {

        //this one needs to be thread safe because it is called during the preview and so not in sync with the placeblock method
        //Only edit the CurrToolBlocks in this method and don't call this method anywhere else but addNewBlock method
        //never call a return in this method !!!!
        if (CURR_TOOL_BLOCKS_LOCK.attemptLocking())
        {
            CURR_TOOL_BLOCKS.clear();
            FILL_VECTOR_MAP.clear();

            if (endGlobalblock != null)
                LAST_BLOCK = endGlobalblock.getBlockPos();

            // Alters startVec to create newStartVec
            // Changes lineVec and lastBlock to the current configuration
            NEW_START_VEC = transformStartVecToDirectionMode(hitVec, EnumDirectionMode.FREE);

            if (EditMode.START_BLOCK != null && NEW_START_VEC != null)
            {
                ADJUST_MODE.updateCurrToolBlocks(endGlobalblock, hitVec, NEW_START_VEC);
            }
            if(!(ADJUST_MODE instanceof PlantAdjustMode) )
            {
                //Copy currToolBlocks to currPreviewBlocks to avoid
                if (CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
                {
                    CURR_PREVIEW_BLOCKS = new ArrayList<>(CURR_TOOL_BLOCKS);
                    CURR_PREVIEW_BLOCKS_LOCK.releaseLock();
                }
            }




            //calculate the size and blocks in the selection and set the count display
            if(!ADJUST_MODE.managesPreviewBlocksItself())
                GuiOverlayManager.calcAndDisplayCurrentToolBlocksSize(CURR_TOOL_BLOCKS);

            CURR_TOOL_BLOCKS_LOCK.releaseLock();
        }
    }

    public static void placeCurrToolBlocks(Action currAction, EntityPlayer player)
    {
        //blocking the whole application untill a different thread is done editing the currToolBlocks list but it should work
        CURR_TOOL_BLOCKS_LOCK.blockingAttemptAtLocking();

        ArrayList<TemplateBlock> blocks = new ArrayList<>();
        for (TemplateBlock block : CURR_TOOL_BLOCKS)
        {
            if (DELETE_MODE&& !(ADJUST_MODE.managesDeleteModeItself()))
            {
                TemplateBlock airblock = new TemplateBlock(block.getFace(), block.getBlockPos(), Blocks.AIR.getDefaultState());
                PlaceTemplateMode.placeBlockClient(BlockPos.ORIGIN, player, currAction, airblock);
                blocks.add(airblock);
            }
            else
            {
                PlaceTemplateMode.placeBlockClient(BlockPos.ORIGIN, player, currAction, block);
                blocks.add(block);
            }
        }
        NetworkPlaceBlockListFormatter.sendBlocksListsRelToPosToServer(BlockPos.ORIGIN, blocks, new ArrayList<>(), null, null,currAction);
        CURR_TOOL_BLOCKS_LOCK.releaseLock();
    }


    private static Vec3d transformStartVecToDirectionMode(Vec3d hitVec, EnumDirectionMode dirMode)
    {
        NEW_START_VEC = null;
        if (STARTVEC != null && hitVec != null)
        {
            NEW_START_VEC = ADJUST_MODE.getNewStartVec(STARTVEC, SECONDARY_LINEVEC);

            //allow different coordinates to be non-zero dependend on the workDirectionMode
            if(dirMode == EnumDirectionMode.FREE || dirMode == EnumDirectionMode.GROUND )
                END_VEC = hitVec;
            else if(dirMode == EnumDirectionMode.XY)
                END_VEC = VecTransformer.transformHitVecToXYPlane(hitVec, NEW_START_VEC,ModEntity.currCursorVec);
            else if(dirMode == EnumDirectionMode.ZY)
                END_VEC = VecTransformer.transformHitVecToZYPlane(hitVec, NEW_START_VEC,ModEntity.currCursorVec);
            else if(dirMode == EnumDirectionMode.XZ)
                END_VEC = VecTransformer.transformHitVecToXZPlane(hitVec, NEW_START_VEC,ModEntity.currCursorVec);

            LINEVEC = END_VEC.subtract(NEW_START_VEC);
            LAST_BLOCK = new BlockPos(Math.floor(LINEVEC.add(NEW_START_VEC).x), Math.floor(LINEVEC.add(NEW_START_VEC).y), Math.floor(LINEVEC.add(NEW_START_VEC).z));
        }
        return NEW_START_VEC;
    }

    public static void drawBlockLine(Vec3d startVec, Vec3d toAddVec)
    {
        PlacementHelper.drawBlockLine(startVec, toAddVec, false, false,getStartBlock(),CURR_TOOL_BLOCKS);
    }

    public static int drawPreview(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        DRAW_PREVIEW_OUTLINE_ONLY = false;
        return ADJUST_MODE.drawPreview(entityplayer, hitVec, face, partialTicks);
    }

    public static TemplateBlock getStartBlock()
    {
        return START_BLOCK;
    }

    public static void setStartBlock(TemplateBlock startBlock)
    {
        EditMode.START_BLOCK = startBlock;
    }

    public static Vec3d getSTARTVEC()
    {
        return STARTVEC;
    }

    public static void setSTARTVEC(Vec3d STARTVEC)
    {
        EditMode.STARTVEC = PlacementHelper.parseVec(STARTVEC);
    }

    public static Vec3d getEndVec()
    {
        return END_VEC;
    }

    public static void setEndVec(Vec3d endVec)
    {
        EditMode.END_VEC = endVec;
    }

    public static void cancelBuilding()
    {
        RIGHT_CLICK_NUMBER = 0;
        END_VEC = Vec3d.ZERO;
        LINEVEC = Vec3d.ZERO;
        SECONDARY_LINEVEC = Vec3d.ZERO;
        CLEAR_CURR_TOOL_BLOCKS = true;
        ADJUST_MODE.cancelBuilding();
    }

    public static void setColor(float red, float green, float blue)
    {
        EditMode.RED = red;
        EditMode.GREEN = green;
        EditMode.BLUE = blue;
    }

    public static void setColor(Color color)
    {
        float[] comp = color.getComponents(null);
        RED = comp[0];
        GREEN = comp[1];
        BLUE =comp[2];
    }

    public static void addRadius(double radius)
    {
        int multiplier = 1;

        if(ConfigurationHandler.toolConfig.ADAPTIVE_BRUSH_SIZE_CHANGES)
        {
            long currTime = System.currentTimeMillis();
            long timeFromLastCall = currTime - lastAddRadiusTime;
            boolean sameSign = (lastAddRadiusValue/Math.abs(lastAddRadiusValue)) == (radius/Math.abs(-radius));
            if(sameSign && timeFromLastCall  < resetDelay)
            {
                addRadiusRepetitionCount += (int) Math.ceil(Math.abs(radius));
                multiplier = (int) Math.pow(2, addRadiusRepetitionCount /scrollssUntilAddRadiusIncrease);
                if(multiplier > 16)
                    multiplier  = 16;
            }
            else
                addRadiusRepetitionCount = 0;
//            System.out.println("timeFromLastCall AddRadius: " + timeFromLastCall +" < " + resetDelay + " multiplier " + multiplier + " lastAddRadiusValue " + lastAddRadiusValue);
            lastAddRadiusTime = currTime;
        }

        lastAddRadiusValue = radius;
        ADJUST_MODE.addRadius(lastAddRadiusValue,multiplier);
    }

    public static void updateCoordInfoDisplay()
    {
        GuiOverlayManager.setShowMiddlePos(false);
        GuiOverlayManager.setShowEndPos(false);
    }

    public static boolean allowRightClickException(Item item)
    {
        return ADJUST_MODE.allowRightClickException(item);
    }

    public static int getMaxLegendEntries()
    {
        int max = 0;
        for (BaseAdjustMode mode : ADJUST_MODES )
        {
            max = (int) Math.max(mode.highlightColorOrder.size(),max);
        }

        return max;
    }

    public static int getCurrLegendEntryCount()
    {
        return ADJUST_MODES[ADJUST_MODE_INDEX].highlightColorOrder.size();
    }

    public static String getLegendText(int i)
    {
        if(i < ADJUST_MODES[ADJUST_MODE_INDEX].highlightColorOrder.size())
            return ADJUST_MODES[ADJUST_MODE_INDEX].highlightLegendText.get(ADJUST_MODE.highlightColorOrder.get(i));
        else
            return null;

    }

    public static Color getLegendColor(int i)
    {
        if(i < ADJUST_MODES[ADJUST_MODE_INDEX].highlightColorOrder.size())
            return  (Color)ADJUST_MODES[ADJUST_MODE_INDEX].highlightColorOrder.get(i);
        return null;
    }
    public static String getLegendText(Color color)
    {
        if(ADJUST_MODES[ADJUST_MODE_INDEX].highlightLegendText.containsKey(color))
            return ADJUST_MODES[ADJUST_MODE_INDEX].highlightLegendText.get(color);
        return null;

    }
}
