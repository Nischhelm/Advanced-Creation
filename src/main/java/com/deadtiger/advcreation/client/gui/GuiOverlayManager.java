package com.deadtiger.advcreation.client.gui;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.client.gui.gui_overlay.*;
import com.deadtiger.advcreation.client.gui.gui_screen.absoluteCoordScreen.AbsoluteCoordScreen;
import com.deadtiger.advcreation.client.gui.vanilla_creative_gui_overlay.VanillaCreativeGuiOverlay;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Random;

public class GuiOverlayManager
{


    //### LIST OF GUI OVERLAYS ###
    public static ArrayList<AbstractGuiOverlay> GUI_OVERLAYS = new ArrayList<>();
    //GuiOverlay objects
    // register the build mode gui overlay to the forgeregister
    public static AbstractGuiOverlay MAIN_MODE;
    public static AbstractGuiOverlay CAMERA_LEVEL;
    public static AbstractGuiOverlay UNDO_REDO;
    public static AbstractGuiOverlay PLACE_TOOL;
    public static AbstractGuiOverlay BUILD_TOOL;
    public static AbstractGuiOverlay INVENTORY_SELECTION;
    public static AbstractGuiOverlay EDIT_TOOL;
    public static AbstractGuiOverlay CUT_THROUGH;
    public static AbstractGuiOverlay IGNORE_PLANTS;
    public static AbstractGuiOverlay TEMPLATE_SELECTION;
    public static AbstractGuiOverlay DISABLE_TOOLS;
    //gives access to the state of the mouse buttons helps with GuiOverlayButtons
    public static boolean GUI_OVERLAY_CLICKED = false;
    public static boolean PLAYER_GUI_INITIALIZED = false;



    private static boolean GUI_OVERLAY_VISIBLE = false;

    public static VanillaCreativeGuiOverlay UNDO_REDO_NOTIFICATION;


    public static void initGuiOverlays()
    {
        GUI_OVERLAY_VISIBLE = ConfigurationHandler.general.GUI_OVERLAY_VISIBLE;
        MAIN_MODE = new MainModeGuiOverlay();
        ((MainModeGuiOverlay) MAIN_MODE).refreshCoordInfoDisplay = true;
        CAMERA_LEVEL = new CameraLevelGuiOverlay();
        UNDO_REDO = new UndoGuiOverlay();
        PLACE_TOOL = new PlaceToolsGuiOverlay();
        BUILD_TOOL = new BuildToolsGuiOverlay();
        EDIT_TOOL = new EditToolsGuiOverlay();
        INVENTORY_SELECTION = new SelectInventoryItemGuiOverlay();
        CUT_THROUGH = new CutThroughGuiOverlay();
        TEMPLATE_SELECTION = new SelectTemplateGuiOverlay();
        UNDO_REDO_NOTIFICATION = new VanillaCreativeGuiOverlay();
        DISABLE_TOOLS = new ToolDisableGuiOverlay();

        GUI_OVERLAYS.add(MAIN_MODE);
        GUI_OVERLAYS.add(CAMERA_LEVEL);
        GUI_OVERLAYS.add(UNDO_REDO);
        GUI_OVERLAYS.add(PLACE_TOOL);
        GUI_OVERLAYS.add(BUILD_TOOL);
        GUI_OVERLAYS.add(INVENTORY_SELECTION);
        GUI_OVERLAYS.add(EDIT_TOOL);
        GUI_OVERLAYS.add(CUT_THROUGH);
        GUI_OVERLAYS.add(TEMPLATE_SELECTION);
        GUI_OVERLAYS.add(DISABLE_TOOLS);
//        GUI_OVERLAYS.add(IGNORE_PLANTS);

        for (AbstractGuiOverlay overlays: GUI_OVERLAYS)
        {
            overlays.setVisibility(false);
            MinecraftForge.EVENT_BUS.register(overlays);
        }
        MinecraftForge.EVENT_BUS.register(UNDO_REDO_NOTIFICATION);
    }

    public static boolean isGuiOverlayLeftClicked(boolean isClient, int X_mouse, int Y_mouse)
    {
        boolean guiOverlayClicked;
        //check if the clicked screen position is on a button in the GUI overlay
        GuiOverlayManager.GUI_OVERLAY_CLICKED = false;

        if (GuiOverlayManager.GUI_OVERLAY_VISIBLE)
        {
            for (AbstractGuiOverlay overlay : GUI_OVERLAYS)
            {
                if (overlay.leftClick(X_mouse, Y_mouse, isClient))
                {
                    GuiOverlayManager.GUI_OVERLAY_CLICKED = true;
                }
            }
        }

        return GuiOverlayManager.GUI_OVERLAY_CLICKED;
    }

    public static boolean isGuiOverlayRightClicked(boolean isClient, int X_mouse, int Y_mouse)
    {
        boolean guiOverlayClicked;
        //check if the clicked screen position is on a button in the GUI overlay
        GuiOverlayManager.GUI_OVERLAY_CLICKED = false;

        if (GuiOverlayManager.GUI_OVERLAY_VISIBLE)
        {
            for (AbstractGuiOverlay overlay : GUI_OVERLAYS)
            {
                if (overlay.rightClick(X_mouse, Y_mouse, isClient))
                {
                    GuiOverlayManager.GUI_OVERLAY_CLICKED = true;
                }
            }
        }
        return GuiOverlayManager.GUI_OVERLAY_CLICKED;
    }

    public static void tryInitialisingPlayerGuiOverlay()
    {
        if (!PLAYER_GUI_INITIALIZED)
        {
            PLAYER_GUI_INITIALIZED = true;
            Minecraft minecraft = Minecraft.getMinecraft();


            AdvCreation.rand = new Random(minecraft.world.getSeed());

            if (GuiOverlayManager.GUI_OVERLAY_VISIBLE)
            {
                //reset the initialisation of all GuiOverlays
                for (AbstractGuiOverlay overlay : GUI_OVERLAYS)
                {
                    overlay.setInitialised(false);
                }
            }
        }
    }

    public static void setCameraOverlayCurrYSlideTab(double targetY)
    {
        ((CameraLevelGuiOverlay)GuiOverlayManager.CAMERA_LEVEL).setSlideTabCurrY(targetY);
    }

    public static double getCameraOverlayCurrYSlideTab()
    {
        return ((CameraLevelGuiOverlay)GuiOverlayManager.CAMERA_LEVEL).getSlideTabCurrY();
    }

    public static void setHelpButtonSelected(boolean selected)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setHelpSelected(selected);
    }

    public static void setBlockCount(int X, int Y, int Z, int blocks)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setCount(X,Y,Z,blocks);
    }

    public static void setBlockCount(int X, int Y, int Z, int blocks, BlockPos start, BlockPos mid,BlockPos end)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setCount(X,Y,Z,blocks);
        ((MainModeGuiOverlay) MAIN_MODE).setStartBlockPos(start);
        ((MainModeGuiOverlay) MAIN_MODE).setMiddleBlockPos(mid);
        ((MainModeGuiOverlay) MAIN_MODE).setEndBlockPos(end);

    }

    public static void setInfoDisplayVisibility(boolean visible)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setCountVisibility(visible);
        ((MainModeGuiOverlay) MAIN_MODE).setPosVisibility(visible);
    }

    public static void setBlockCountVisibility(boolean visible)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setCountVisibility(visible);
        ((MainModeGuiOverlay) MAIN_MODE).setPosVisibility(visible);
    }
    public static void setPosVisibility(boolean visible)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setPosVisibility(visible);
    }

    public static void setCrossHair(int x,int y)
    {
        ((SelectInventoryItemGuiOverlay) INVENTORY_SELECTION).setCrossHair(x,y);
    }

    public static void setCrosshairVisibility(boolean vis)
    {
        ((SelectInventoryItemGuiOverlay) INVENTORY_SELECTION).setCrosshairVisibility(vis);
    }

    public static void setZoomIcon(int x,int y)
    {
        ((SelectInventoryItemGuiOverlay) INVENTORY_SELECTION).setZoomIcon(x,y);
    }

    public static void setZoomIconVisibility(boolean vis)
    {
        ((SelectInventoryItemGuiOverlay) INVENTORY_SELECTION).setZoomIconVisibility(vis);
    }

    public static int getTemplateIndexOfTemplateButton(int buttonIndex)
    {
        return ((SelectTemplateGuiOverlay) TEMPLATE_SELECTION).getTemplateIndexFromTemplateButton(buttonIndex);
    }

    public static void setTemplateOfTemplateButton(int buttonIndex, Template template,int templateIndex)
    {
        ((SelectTemplateGuiOverlay) TEMPLATE_SELECTION).setTemplateOfTemplateButton(buttonIndex,template,templateIndex);
    }


    public static void calcAndDisplayCurrentToolBlocksSize(ArrayList<TemplateBlock> blockList)
    {
        int maxX = -10000;
        int maxY = -10000;
        int maxZ = -10000;
        int minX = 10000;
        int minY = 10000;
        int minZ = 10000;

        int sizeX = 1;
        int sizeY = 1;
        int sizeZ = 1;

        if (blockList.size() != 0)
        {
            for (int j = 0; j < blockList.size(); j++)
            {
                TemplateBlock newTempBlock = blockList.get(j);
                if (maxX < newTempBlock.getX_offset())
                    maxX = newTempBlock.getX_offset();
                if (maxY < newTempBlock.getY_offset())
                    maxY = newTempBlock.getY_offset();
                if (maxZ < newTempBlock.getZ_offset())
                    maxZ = newTempBlock.getZ_offset();
                if (minX > newTempBlock.getX_offset())
                    minX = newTempBlock.getX_offset();
                if (minY > newTempBlock.getY_offset())
                    minY = newTempBlock.getY_offset();
                if (minZ > newTempBlock.getZ_offset())
                    minZ = newTempBlock.getZ_offset();
            }

            sizeX = maxX - minX + 1;
            sizeY = maxY - minY + 1;
            sizeZ = maxZ - minZ + 1;
        }

        setBlockCount(sizeX, sizeY, sizeZ, blockList.size());

    }

    public static boolean isGuiOverlayVisible()
    {
        return GUI_OVERLAY_VISIBLE;
    }

    public static void setGuiOverlayVisible(boolean guiOverlayVisible)
    {
        if(ConfigurationHandler.general.GUI_OVERLAY_VISIBLE)
            GUI_OVERLAY_VISIBLE = guiOverlayVisible;
        else
            GUI_OVERLAY_VISIBLE = false;
    }

    public static void updateGuiVisibility()
    {
        if(!ConfigurationHandler.general.GUI_OVERLAY_VISIBLE)
            GUI_OVERLAY_VISIBLE = false;
    }

    public static void setStartPos(BlockPos selectionPos)
    {
        if(allowSetPos())
        {
            ((MainModeGuiOverlay) MAIN_MODE).setStartBlockPos(selectionPos);
        }

    }

    private static boolean allowSetPos()
    {
        return Minecraft.getMinecraft().currentScreen == null || Minecraft.getMinecraft().currentScreen instanceof AbsoluteCoordScreen;
    }

    public static void setEndPos(BlockPos selectionEndPos)
    {
        if(allowSetPos())
            ((MainModeGuiOverlay) MAIN_MODE).setEndBlockPos(selectionEndPos);
    }

    public static void setMiddlePos(BlockPos selectionPos)
    {
        if(allowSetPos())
            ((MainModeGuiOverlay) MAIN_MODE).setMiddleBlockPos(selectionPos);
    }

    public static void setOffsetPos(BlockPos offsetPos)
    {
        if(allowSetPos())
         ((MainModeGuiOverlay) MAIN_MODE).setOffsetBlockPos(offsetPos);
    }

    public static void setShowMiddlePos(boolean showMiddlePos)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setMiddleEnabled(showMiddlePos);
    }

    public static void clearDisplay()
    {
       setBlockCount(0,0,0,0,null,null,null);
       ((MainModeGuiOverlay) MAIN_MODE).setOffsetBlockPos(BlockPos.ORIGIN);
    }

    public static void setShowEndPos(boolean hasEndPosition)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setEndEnabled(hasEndPosition);
    }

    public static BlockPos getSizeCount()
    {
        return new BlockPos(((MainModeGuiOverlay) MAIN_MODE).sizeX,((MainModeGuiOverlay) MAIN_MODE).sizeY,((MainModeGuiOverlay) MAIN_MODE).sizeZ);
    }

    public static void setPlacePointedCoordinate(BlockPos pointed)
    {
        if(ConfigurationHandler.absCoordConfig.SHOW_ABS_MOUSE_COORD)
            ((MainModeGuiOverlay) MAIN_MODE).setPlaceCoordBlockPos(pointed);
    }

    public static void setDeletePointedCoordinate(BlockPos pointed)
    {
        if(ConfigurationHandler.absCoordConfig.SHOW_ABS_MOUSE_COORD)
            ((MainModeGuiOverlay) MAIN_MODE).setDeleteCoordBlockPos(pointed);
    }

    public static boolean isCrosshairVisible()
    {
        if(((SelectInventoryItemGuiOverlay) INVENTORY_SELECTION).crosshair != null)
            return ((SelectInventoryItemGuiOverlay) INVENTORY_SELECTION).crosshair.isVisible();
        else
            return false;
    }

    public static int getCrosshairX()
    {
        return ((SelectInventoryItemGuiOverlay) INVENTORY_SELECTION).crosshair.getX();
    }

    public static int getCrosshairY()
    {
        return ((SelectInventoryItemGuiOverlay) INVENTORY_SELECTION).crosshair.getY();
    }

    public static BlockPos getStartPosCoord()
    {
        return ((MainModeGuiOverlay) MAIN_MODE).getStartBlockPos();
    }

    public static BlockPos getMiddlePosCoord()
    {
        return ((MainModeGuiOverlay) MAIN_MODE).getMiddleBlockPos();
    }

    public static BlockPos getEndPosCoord()
    {
        return ((MainModeGuiOverlay) MAIN_MODE).getEndBlockPos();
    }

    public static boolean isShowMiddleEnabled()
    {
        return ((MainModeGuiOverlay) MAIN_MODE).isMiddleEnabled();
    }

    public static boolean isShowEndEnabled()
    {
        return ((MainModeGuiOverlay) MAIN_MODE).isEndEnabled();
    }

    public static void setCameraFocusPointPos(BlockPos cameraFocusPointPos)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setCameraFocusPointCoordPos(cameraFocusPointPos);
    }

    public static  void setSetAbsoluteCoordButtonEnable(boolean enable)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setSetAbsCoordButtonEnabled(enable);
    }

    public static void setDisplayPosCoord(boolean display)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setDisplayPosCoord(display);
    }

    public static void announceBlacklistMatch()
    {
        ((SelectInventoryItemGuiOverlay)  GuiOverlayManager.INVENTORY_SELECTION).updateToolModeIndication(AdvCreation.getMode(),"This Item is BlackListed",true,true);
    }


    public static boolean isAllowShowPlacedCoord()
    {
        return ((MainModeGuiOverlay) MAIN_MODE).isAllowShowPlacedCoord();
    }

    public static void setAllowShowPlacedCoord(boolean allowShowPlacedCoord)
    {
        ((MainModeGuiOverlay) MAIN_MODE).setAllowShowPlacedCoord(allowShowPlacedCoord);
    }

    public static void showFpsOptimiseButton()
    {
        ((MainModeGuiOverlay) MAIN_MODE).showFpsOptimiseButton();
    }

    public static void hideFpsOptimiseButton()
    {
        ((MainModeGuiOverlay) MAIN_MODE).hideFpsOptimiseButton();
    }




}
