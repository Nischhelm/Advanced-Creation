package com.deadtiger.advcreation.client.input;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.build_template.TemplateBuildingMode;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_screen.absoluteCoordScreen.AbsoluteCoordScreen;
import com.deadtiger.advcreation.client.gui.gui_screen.reportScreen.ReportScreen;
import com.deadtiger.advcreation.client.gui.gui_screen.saveTemplateScreen.GuiSaveTemplateScreen;
import com.deadtiger.advcreation.client.gui.gui_screen.selection_wheel.GuiAdjustModeSelectionScreen;
import com.deadtiger.advcreation.client.gui.gui_screen.selection_wheel.GuiToolModeSelectionScreen;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.client.gui.gui_screen.warningScreen.GuiWarningScreenFactory;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.client.player.ToolEnabled;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.PlantAdjustMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.logging.Logging;
import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.plugin.modded_classes.ModBlockRendererDispatcher;
import com.deadtiger.advcreation.plugin.modded_classes.ModMouseHelper;
import com.deadtiger.advcreation.report.Report;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateManager;
import com.deadtiger.advcreation.undo_actions.UndoFunctionality;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class KeyInputHandler
{
    public static boolean alterToolMode = false;
    public static int originalMouseX = 0;
    public static int originalMouseY =0;

    public static Keybindings prevKey = null;
    public static long previousKeyPressTime = 0;
    public static long delay = 100;

    public static int ticksSinceLastPress = 0;

    public static int[] listOfTicksSinceLastPress = new int[5];
    public static int indexListOfTicksSinceLastPress = 0;

    public static Method methodProcessKeyBinds = ObfuscationReflectionHelper.findMethod(Minecraft.class,"func_184117_aA",Void.TYPE);
    public static Method methodMiddleMouseClick = ObfuscationReflectionHelper.findMethod(Minecraft.class,"func_147112_ai",Void.TYPE);

    static
    {
        methodProcessKeyBinds.setAccessible(true);
        methodMiddleMouseClick.setAccessible(true);
    }
    
    public static Keybindings getPressedKey()
    {
        for(Keybindings key: Keybindings.values())
        {
            if(key.isPressed())
                return key;
        }
        return null;
    }

    public static ArrayList<Keybindings> getDownKeys()
    {
        ArrayList<Keybindings> keyDown = new ArrayList<>();
        for(Keybindings key: Keybindings.values())
        {
            if(key.isDown())
                keyDown.add(key);
        }


        return keyDown;
    }
    
    public static Keybindings getAltKey()
    {
        return Keybindings.ALTER_TOOL_MODE;
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event) {
        Keybindings key = getPressedKey();
        if (IsometricCamera.isPlayerInIsometricPerspective())
        {
            IsometricCamera.checkIfNeedForCompensateForPressF5();
            //checks if the alter key is down when pressing the current key
            Keybindings altKey = getAltKey();
            if(ConfigurationHandler.general.TOOLS_ENABLED) {
                if ((AdvCreation.getMode().equals(EnumMainMode.PLACE)) ||
                        (AdvCreation.getMode().equals(EnumMainMode.BUILD)) ||
                        (AdvCreation.getMode().equals(EnumMainMode.EDIT)) ||
                        (AdvCreation.getMode().equals(EnumMainMode.CREATE) && !BuildTemplateMode.MODE.equals(TemplateBuildingMode.MAKE_ADJUSTMENTS))) {
                    if (altKey.isDown()) {

                        if ((AdvCreation.getMode().equals(EnumMainMode.BUILD)) && BuildMode.hasNoAlterPositionMode())
                            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Altering Position NOT AVAILABLE", true, true);
                        else
                            GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Altering Position", false, false);

                        if (!alterToolMode) {
                            String comment = "activate ctrl menu";
                            logKeyPress(AdvCreation.mode, altKey, comment);
                            alterToolMode = true;
                            if (!(AdvCreation.getMode().equals(EnumMainMode.EDIT))) {
                                originalMouseX = Mouse.getX();
                                originalMouseY = Mouse.getY();

                                int[] coord = CustomGuiUtils.scaleMouseCoord(originalMouseX, originalMouseY);
                                GuiOverlayManager.setCrossHair(coord[0], coord[1]);
                                GuiOverlayManager.setCrosshairVisibility(true);
                            }
                        }
                    } else {
                        if (alterToolMode) {
                            if (AdvCreation.mode == EnumMainMode.PLACE)
                                GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode());
                            else
                                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());


                            if (!(AdvCreation.getMode().equals(EnumMainMode.EDIT))) {
                                Mouse.setCursorPosition(originalMouseX, originalMouseY);
                                GuiOverlayManager.setCrosshairVisibility(false);
                            }
                        }
                        alterToolMode = false;
                    }
                } else {
                    if (altKey.isDown())
                        if (alterToolMode == false) {
                            BuildTemplateMode.ALT_PRESSES += 1;
                            alterToolMode = true;
                        } else
                            alterToolMode = false;

                }
            }
//            else {
//                alterToolMode = false;
//                GuiOverlayManager.setCrosshairVisibility(false);
//            }
            if(Keybindings.ROT_CAMERA.getKeybind().isKeyDown() )
            {
                Mousebindings.initialiseCameraRotation = true;
            }

            checkKeybindingPressed(key);
        }
        if(Minecraft.getMinecraft().playerController != null && Minecraft.getMinecraft().playerController.isInCreativeMode())
            checkUndoOrRedoKeybindingPressed(key);
        if(Keybindings.ROT_CAMERA == key)
        {
            if(!IsometricCamera.isPlayerInIsometricPerspective() && Keybindings.ROT_CAMERA.getKeybind().getKeyCode() == Minecraft.getMinecraft().gameSettings.keyBindPickBlock.getKeyCode())
            {

                try
                {
                    //Minecraft.getMinecraft.middleMouseClick()
                    methodMiddleMouseClick.invoke(Minecraft.getMinecraft(),null);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if(Keybindings.TO_ISOMETRIC_VIEW == key)
        {
            if(Keybindings.TO_ISOMETRIC_VIEW.getKeybind().getKeyCode() == Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.getKeyCode())
            {

                if(Minecraft.getMinecraft().playerController != null && Minecraft.getMinecraft().playerController.isInCreativeMode())
                    IsometricCamera.handlePerspectiveChange();
                else
                {
//

                    try
                    {
                        //do pressTime variable of the "toggle perspectives" key +1 so the processkeybinds is tricked into thinking the player pressed that key once

                        //Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.pressTime
                        ObfuscationReflectionHelper.setPrivateValue(KeyBinding.class,Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective,1,"field_151474_i");
                        //Minecraft.getMinecraft().processKeybinds()
                        methodProcessKeyBinds.invoke(Minecraft.getMinecraft(),null);
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            else if (Minecraft.getMinecraft().playerController != null && Minecraft.getMinecraft().playerController.isInCreativeMode())
            {
                if(IsometricCamera.getThirdPersonViewSetting() == 0)
                    IsometricCamera.setThirdPersonViewSetting(3);
                else
                    IsometricCamera.setThirdPersonViewSetting(0);

            }
        }

    }

    public static void checkUndoOrRedoKeybindingPressed(Keybindings key)
    {

        if (key != null) {
            switch (key)
            {
                case UNDO_ACTION:
                    logKeyPress(AdvCreation.getMode(),key,"pressed undo");
                    UndoFunctionality.activateUndo();

                    break;
                case REDO_ACTION:
                    logKeyPress(AdvCreation.getMode(),key,"pressed redo");
                    UndoFunctionality.activateRedo();

                    break;
            }

        }
    }

    public static void checkKeybindingPressed(Keybindings key)
    {
        previousKeyPressTime = System.currentTimeMillis();



        if (key != null)
        {
            ticksSinceLastPress = 0;

            Template template = null;
            int selected_index = GuiTemplaceInventoryScreenFunctionality.getSelected_index();
            if (TemplateManager.TEMPLATES_LIST.size() >  selected_index && 0 <= selected_index) {
                template = TemplateManager.TEMPLATES_LIST.get(GuiTemplaceInventoryScreenFunctionality.getSelected_index());
            }

            //if the sprint key is pressed I will unpress it
            if(Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown())
            {
                KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(),false);
            }


            //System.out.println(key.getKeybind().getDisplayName());
            switch (key) {
                case CHANGE_MODE:

                    EnumMainMode prevMode = AdvCreation.getMode();
                    AdvCreation.setMode(AdvCreation.mode.rotateMode());
                    //System.out.println(AdvCreation.mode);
                    logKeyPress(AdvCreation.getMode(),key,prevMode.name() + " to " + AdvCreation.getMode().name());

                    break;
                case ROTATE_RIGHT:
                    // rotation of the templates
                    if (AdvCreation.getMode().equals(EnumMainMode.PLACE)) {
                        EnumFacing prevFace = PlaceTemplateMode.ROTATION;
                        PlaceTemplateMode.rotate();
                        for (Template template1 : TemplateManager.TEMPLATES_LIST) {
                            template1.rotateY();
                        }
                        logKeyPress(AdvCreation.getMode(),key,prevFace.getName() + " to " + PlaceTemplateMode.ROTATION.getName());

                    } else if (AdvCreation.getMode().equals(EnumMainMode.BUILD))
                    {
                        logKeyPress(AdvCreation.getMode(),key,"");
                        BuildMode.rotateBlocks();
                    }
                    else if(AdvCreation.getMode().equals(EnumMainMode.EDIT) && EditMode.ADJUST_MODE instanceof PlantAdjustMode)
                    {
                        ((PlantAdjustMode) EditMode.ADJUST_MODE).regenerateKeyPressed = true;
                        logKeyPress(AdvCreation.getMode(),key,"Plant manually regenerated");
                    }
                    else
                        logKeyPress(AdvCreation.getMode(),key,"nothing happened");

                    break;
                case MIRROR_ZY:
                    if (AdvCreation.getMode().equals(EnumMainMode.PLACE)) {
                        PlaceTemplateMode.mirrorZY();
//                            EnumFacing prevFace = PlaceTemplateMode.rotation;
//                            PlaceTemplateMode.rotate();
//                            for (Template template1 : AdvCreation.templatesList) {
//                                template1.rotateY();
//                            }
                        logKeyPress(AdvCreation.getMode(),key,"Mirror arround ZY plane");

                    }
                    else if (AdvCreation.getMode().equals(EnumMainMode.BUILD))
                    {
                        logKeyPress(AdvCreation.getMode(),key,"");
                        BuildMode.mirrorZY();
                    }
                    break;
                case CHANGE_TOOL_MODE:
                    if (AdvCreation.getMode().equals(EnumMainMode.BUILD))
                    {
                        String prevtoolmode = BuildMode.TOOLMODE.toolModeName;
                        BuildMode.rotateToolMode();
                        logKeyPress(AdvCreation.getMode(),key,prevtoolmode + " to next");
                    }
                    else if (AdvCreation.getMode().equals(EnumMainMode.EDIT))
                    {
                        String prevAdjustMode = EditMode.ADJUST_MODE.toolModeName;
                        EditMode.rotateAdjustMode();
                        logKeyPress(AdvCreation.getMode(),key,prevAdjustMode + " to  next" );
                    }
                    else if (AdvCreation.mode == EnumMainMode.PLACE && template != null) {
                        String prevDir = PlaceTemplateMode.AIR.name();
                        PlaceTemplateMode.AIR = PlaceTemplateMode.AIR.rotateMode();
                        logKeyPress(AdvCreation.getMode(),key,prevDir + " to " + PlaceTemplateMode.AIR.name());
                    }

                    else
                        logKeyPress(AdvCreation.getMode(),key,"nothing happened");
                    break;
                case OFFSET_UP:
//                    if (AdvCreation.mode == EnumMainMode.PLACE && template != null) {
////                            template.addX_offset_template();
//                        PlaceTemplateMode.MOUSE_X_OFFSET++;
//                        logKeyPress(AdvCreation.getMode(),key,"add 1 to X offset to " +PlaceTemplateMode.MOUSE_X_OFFSET);
////                        System.out.println(template.getX_offset_template());
//                    } else if (AdvCreation.mode == EnumMainMode.BUILD && (BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode)) {
////                            BuildTemplateMode.decrX_offset_template();
//                        PlaceTemplateMode.MOUSE_X_OFFSET++;
//                        logKeyPress(AdvCreation.getMode(),key,"add 1 to X offset to " +PlaceTemplateMode.MOUSE_X_OFFSET);
//                    }
//                    else
//                        logKeyPress(AdvCreation.getMode(),key,"nothing happened");
                    GuiAdjustModeSelectionScreen.colorOn = !GuiAdjustModeSelectionScreen.colorOn;
                    System.out.println("colorOn: " + GuiAdjustModeSelectionScreen.colorOn);


                    break;
                case OFFSET_DOWN:
//                    if (AdvCreation.mode == EnumMainMode.PLACE && template != null) {
////                            template.decrX_offset_template();
//                        PlaceTemplateMode.MOUSE_X_OFFSET--;
//                        logKeyPress(AdvCreation.getMode(),key,"subtract 1 to X offset to " +PlaceTemplateMode.MOUSE_X_OFFSET);
////                        System.out.println(template.getX_offset_template());
//                    } else if (AdvCreation.mode == EnumMainMode.BUILD && (BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode)) {
////                            BuildTemplateMode.decrX_offset_template();
//                        PlaceTemplateMode.MOUSE_X_OFFSET--;
//                        logKeyPress(AdvCreation.getMode(),key,"subtract 1 to X offset to " +PlaceTemplateMode.MOUSE_X_OFFSET);
////
//                    }
//                    else
//                        logKeyPress(AdvCreation.getMode(),key,"nothing happened");

                    GuiAdjustModeSelectionScreen.blendOn = !GuiAdjustModeSelectionScreen.blendOn;
                    System.out.println("blendOn: " + GuiAdjustModeSelectionScreen.blendOn);
                                       break;
                case OFFSET_LEFT:
//                    if (AdvCreation.mode == EnumMainMode.PLACE && template != null) {
////                            template.decrZ_offset_template();
//                        PlaceTemplateMode.MOUSE_Z_OFFSET--;
//                        logKeyPress(AdvCreation.getMode(),key,"subtract 1 to Z offset to " +PlaceTemplateMode.MOUSE_Z_OFFSET);
////
////                        System.out.println(template.getZ_offset_template());
//                    } else if (AdvCreation.mode == EnumMainMode.BUILD && (BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode)) {
////                            BuildTemplateMode.decrX_offset_template();
//                        PlaceTemplateMode.MOUSE_Z_OFFSET--;
//                        logKeyPress(AdvCreation.getMode(),key,"subtract 1 to Z offset to " +PlaceTemplateMode.MOUSE_Z_OFFSET);
////
//                    }
//                    else
//                        logKeyPress(AdvCreation.getMode(),key,"nothing happened");
                    GuiAdjustModeSelectionScreen.lightOn = !GuiAdjustModeSelectionScreen.lightOn;
                    System.out.println("lightOn: " + GuiAdjustModeSelectionScreen.lightOn);
                    break;
                case OFFSET_RIGHT:
//                    if (AdvCreation.mode == EnumMainMode.PLACE && template != null) {
////                            template.addZ_offset_template();
//                        PlaceTemplateMode.MOUSE_Z_OFFSET++;
//                        logKeyPress(AdvCreation.getMode(),key,"add 1 to Z offset to " +PlaceTemplateMode.MOUSE_Z_OFFSET);
////
////                        System.out.println(template.getZ_offset_template());
//                    }else if (AdvCreation.mode == EnumMainMode.BUILD && (BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode)) {
////                            BuildTemplateMode.decrX_offset_template();
//                        PlaceTemplateMode.MOUSE_Z_OFFSET++;
//                        logKeyPress(AdvCreation.getMode(),key,"add 1 to Z offset to " +PlaceTemplateMode.MOUSE_Z_OFFSET);
////
//                    }
//                    else
//                        logKeyPress(AdvCreation.getMode(),key,"nothing happened");
                    GuiAdjustModeSelectionScreen.depthOn = !GuiAdjustModeSelectionScreen.depthOn;
                    System.out.println("depthOn: " + GuiAdjustModeSelectionScreen.depthOn);
                    break;
                case OFFSET_PG_UP:
//                    if (AdvCreation.mode == EnumMainMode.PLACE && template != null) {
////                            template.addY_offset_template();
//                        PlaceTemplateMode.MOUSE_Y_OFFSET++;
//                        logKeyPress(AdvCreation.getMode(),key,"add 1 to Y offset to " +PlaceTemplateMode.MOUSE_Y_OFFSET);
////                        System.out.println(template.getY_offset_template());
//                    } else if (AdvCreation.mode == EnumMainMode.BUILD && (BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode)) {
////                            BuildTemplateMode.decrX_offset_template();
//                        PlaceTemplateMode.MOUSE_Y_OFFSET++;
//                        logKeyPress(AdvCreation.getMode(),key,"add 1 to Y offset to " +PlaceTemplateMode.MOUSE_Y_OFFSET);
//                    }
//                    else
//                        logKeyPress(AdvCreation.getMode(),key,"nothing happened");
                    GuiAdjustModeSelectionScreen.alphaOn = !GuiAdjustModeSelectionScreen.alphaOn;
                    System.out.println("alphaOn: " + GuiAdjustModeSelectionScreen.alphaOn);


                    break;
                case OFFSET_PG_DOWN:
//                    if (AdvCreation.mode == EnumMainMode.PLACE && template != null) {
////                            template.decrY_offset_template();
//                        PlaceTemplateMode.MOUSE_Y_OFFSET--;
//                        logKeyPress(AdvCreation.getMode(),key,"substract 1 to Y offset to " +PlaceTemplateMode.MOUSE_Y_OFFSET);
////                        System.out.println(template.getY_offset_template());
//                    } else if (AdvCreation.mode == EnumMainMode.BUILD && (BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode)) {
////                            BuildTemplateMode.decrX_offset_template();
//                        PlaceTemplateMode.MOUSE_Y_OFFSET--;
//                        logKeyPress(AdvCreation.getMode(),key,"substract 1 to Y offset to " +PlaceTemplateMode.MOUSE_Y_OFFSET);
//                    }
//                    else
//                        logKeyPress(AdvCreation.getMode(),key,"nothing happened");
                    GuiAdjustModeSelectionScreen.colorMatOn = !GuiAdjustModeSelectionScreen.colorMatOn;
                    System.out.println("colorMatOn: " + GuiAdjustModeSelectionScreen.colorMatOn);

                    break;
                case CONFIRM_TEMPLATE_CREATION:
                    if (BuildTemplateMode.MODE.equals(TemplateBuildingMode.MAKE_ADJUSTMENTS)) {
                        logKeyPress(AdvCreation.getMode(),key,"finishing a new template");
                        Minecraft.getMinecraft().displayGuiScreen(new GuiSaveTemplateScreen());
                    }
                    else
                        logKeyPress(AdvCreation.getMode(),key,"nothing happened");
                    break;
                case CANCEL_TEMPLATE_CREATION:
                    logKeyPress(AdvCreation.getMode(),key,"cancel template");
                    BuildTemplateMode.cancelTemplate();
                    BuildMode.clearBuildMode();
                    PlaceTemplateMode.reset();
                    if(NetworkPlaceBlockListFormatter.isPlacementOperationInProgress())
                        Minecraft.getMinecraft().displayGuiScreen(GuiWarningScreenFactory.factory.createCancelPlacementWarningScreen());
                    GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),"Canceled",true,true);

                    break;

//                case ACTIVATE_TO_POINT:
//                    BuildMode.toggleJumpToPoint();
//                    break;
                case CHANGE_DIR_SHAPE_MODE:

                    if (AdvCreation.mode == EnumMainMode.PLACE && template != null) {
                        String prevDir = PlaceTemplateMode.FOUNDATION.name();
                        PlaceTemplateMode.FOUNDATION = PlaceTemplateMode.FOUNDATION.rotateMode();
                        logKeyPress(AdvCreation.getMode(),key,prevDir + " to " + PlaceTemplateMode.FOUNDATION.name());
                    }else if (AdvCreation.mode == EnumMainMode.BUILD ) {
                        String prevDir = BuildMode.DIRECTION_MODE.name();
                        BuildMode.DIRECTION_MODE = BuildMode.DIRECTION_MODE.rotateMode();
                        logKeyPress(AdvCreation.getMode(),key,prevDir + " to " + BuildMode.DIRECTION_MODE.name());
                    }
                    else if (AdvCreation.mode == EnumMainMode.EDIT)
                    {
                        String prevDir = EditMode.TERRAIN_SHAPE_MODE.name();
                        EditMode.TERRAIN_SHAPE_MODE = EditMode.TERRAIN_SHAPE_MODE.rotateMode();
                        logKeyPress(AdvCreation.getMode(),key,prevDir + " to " + EditMode.TERRAIN_SHAPE_MODE.name());
                    }


                    break;
                case OPEN_TOOL_MENU:
                    if( !ModMouseHelper.setMousePosNextTick)
                    {
                        if (AdvCreation.getMode() == EnumMainMode.BUILD) {
                            logKeyPress(AdvCreation.getMode(),key,"open toolModeSelectionScreen ");
                            Minecraft.getMinecraft().displayGuiScreen(new GuiToolModeSelectionScreen());
                        } else if (AdvCreation.getMode() == EnumMainMode.EDIT) {
                            logKeyPress(AdvCreation.getMode(),key,"open AdjustModeSelectionScreen");
                            Minecraft.getMinecraft().displayGuiScreen(new GuiAdjustModeSelectionScreen());
                        }
                    }

                    break;
                case CHANGE_FILL_TERRAIN_MODE:
                    if (AdvCreation.mode == EnumMainMode.PLACE && template != null) {
                        String prevDir = PlaceTemplateMode.WALL.name();
                        PlaceTemplateMode.WALL = PlaceTemplateMode.WALL.rotateMode();
                        logKeyPress(AdvCreation.getMode(),key,prevDir + " to " + PlaceTemplateMode.WALL.name());
                    }else if (AdvCreation.mode == EnumMainMode.BUILD ) {
                        String prevFill = BuildMode.FILL_MODE.buttonText;
                        BuildMode.FILL_MODE = BuildMode.FILL_MODE.rotateMode();
                        logKeyPress(AdvCreation.getMode(),key,prevFill + " to " + BuildMode.FILL_MODE.buttonText);
                    }
                    else if (AdvCreation.mode == EnumMainMode.EDIT ) {
                        String prevFill = EditMode.ONLY_TERRAIN_MODE.buttonText;
                        EditMode.ONLY_TERRAIN_MODE = EditMode.ONLY_TERRAIN_MODE.rotateMode();
                        logKeyPress(AdvCreation.getMode(),key,prevFill + " to " + EditMode.ONLY_TERRAIN_MODE.buttonText);
                    }
                    break;
                case ZOOM_IN:
                    MouseInputHandler.processScrollLikeInput(2.4f);
                    break;
                case ZOOM_OUT:
                    MouseInputHandler.processScrollLikeInput(-2.4f);
                    break;
//                case TOGGLE_PREVIEW:
//
////                    if(SelectInventoryItemGuiOverlay.crosshair != null)
////                    {
////                        if(!SelectInventoryItemGuiOverlay.crosshair.isVisible())
////                            SelectInventoryItemGuiOverlay.setCrosshairVisibility(true);
////                        else
////                            SelectInventoryItemGuiOverlay.setCrosshairVisibility(false);
////                    }
//
//                    BuildMode.SHOW_PREVIEW_BLOCKS = !BuildMode.SHOW_PREVIEW_BLOCKS;
//                    logKeyPress(AdvCreation.getMode(),key,"toggle show preview blocks to " + BuildMode.SHOW_PREVIEW_BLOCKS);
//
//                    break;
                case TOGGLE_CUTTHROUGH:
                    ModBlockRendererDispatcher.cuttThroughOn = !ModBlockRendererDispatcher.cuttThroughOn;
                    logKeyPress(AdvCreation.getMode(),key,"toggle cutthrough to " + ModBlockRendererDispatcher.cuttThroughOn);
                    break;
                case REFRESH_TERRAIN:
                    ModBlockRendererDispatcher.refreshTerrain = true;
                    logKeyPress(AdvCreation.getMode(),key,"refresh terrain ");

                    break;
                case OPEN_REPORT_SCREEN:
                    logKeyPress(AdvCreation.getMode(),key,"open ReportScreen");
                    Report.saveScreenshot();

                    Minecraft.getMinecraft().displayGuiScreen(new ReportScreen());
                    break;
                case OPEN_ABS_COORD_SCREEN:
                        AbsoluteCoordScreen.openAbsoluteCoordScreen();
                    break;
                case CLEAR_OFFSET:
                    if(AdvCreation.getMode().equals(EnumMainMode.BUILD))
                        BuildMode.clearMouseOffset();
                    else if(AdvCreation.getMode().equals(EnumMainMode.CREATE))
                        BuildTemplateMode.clearMouseOffset();
                    else if(AdvCreation.getMode().equals(EnumMainMode.PLACE))
                        PlaceTemplateMode.clearMouseOffset();
                    break;
                case TOGGLE_IGNORE_PLANTS:
                    IsometricCamera.IGNORE_PLANTS = !IsometricCamera.IGNORE_PLANTS;
                    break;
                case TOGGLE_IGNORE_LIQUIDS:
                    IsometricCamera.IGNORE_FLUIDS = !IsometricCamera.IGNORE_FLUIDS;
                    break;
                case ROT_CAM_DOWN:
                    MouseInputHandler.rotateCameraAngles(0, -(8.0D * (ConfigurationHandler.cameraConfig.CAMERA_ROTATION_SPEED/50.0f)));
                    break;
                case ROT_CAM_UP:
                    MouseInputHandler.rotateCameraAngles(0, (8.0D * (ConfigurationHandler.cameraConfig.CAMERA_ROTATION_SPEED/50.0f)));
                    break;
                case ROT_CAM_LEFT:
                    MouseInputHandler.rotateCameraAngles((8.0D * (ConfigurationHandler.cameraConfig.CAMERA_ROTATION_SPEED/50.0f)), 0);
                    break;
                case ROT_CAM_RIGHT:
                    MouseInputHandler.rotateCameraAngles(-(8.0D * (ConfigurationHandler.cameraConfig.CAMERA_ROTATION_SPEED/50.0f)), 0);
                    break;
                case HOTKEY_TOOL_1:
                    if(AdvCreation.getMode() == EnumMainMode.BUILD)
                        BuildMode.changeToolModeTo(0);
                    else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                        EditMode.changeAdjustModeTo(0);
                    break;
                case HOTKEY_TOOL_2:
                    if(AdvCreation.getMode() == EnumMainMode.BUILD)
                        BuildMode.changeToolModeTo(1);
                    else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                        EditMode.changeAdjustModeTo(1);
                    break;
                case HOTKEY_TOOL_3:
                    if(AdvCreation.getMode() == EnumMainMode.BUILD)
                        BuildMode.changeToolModeTo(2);
                    else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                        EditMode.changeAdjustModeTo(2);
                    break;
                case HOTKEY_TOOL_4:
                    if(AdvCreation.getMode() == EnumMainMode.BUILD)
                        BuildMode.changeToolModeTo(3);
                    else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                        EditMode.changeAdjustModeTo(3);
                    break;
                case HOTKEY_TOOL_5:
                    if(AdvCreation.getMode() == EnumMainMode.BUILD)
                        BuildMode.changeToolModeTo(4);
                    else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                        EditMode.changeAdjustModeTo(4);
                    break;
                case HOTKEY_TOOL_6:
                    if(AdvCreation.getMode() == EnumMainMode.BUILD)
                        BuildMode.changeToolModeTo(5);
                    else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                        EditMode.changeAdjustModeTo(5);
                    break;
                case HOTKEY_TOOL_7:
                    if(AdvCreation.getMode() == EnumMainMode.BUILD)
                        BuildMode.changeToolModeTo(6);
                    else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                        EditMode.changeAdjustModeTo(6);
                    break;
                case HOTKEY_TOOL_8:
                    if(AdvCreation.getMode() == EnumMainMode.BUILD)
                        BuildMode.changeToolModeTo(7);
                    else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                        EditMode.changeAdjustModeTo(7);
                    break;
                case HOTKEY_TOOL_9:
                    if(AdvCreation.getMode() == EnumMainMode.BUILD)
                        BuildMode.changeToolModeTo(8);
                    else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                        EditMode.changeAdjustModeTo(8);
                    break;
                case TOGGLE_TOOLS_ENABLED:
                    ToolEnabled.toggleToolsEnabled();
                    break;
//                case NUMPAD1:
//                    GuiAdjustModeSelectionScreen.otherSetting1On = !GuiAdjustModeSelectionScreen.otherSetting1On;
//                    System.out.println("otherSetting1On: " + GuiAdjustModeSelectionScreen.otherSetting1On);
//                   break;
//
//                case NUMPAD2:
//                    GuiAdjustModeSelectionScreen.otherSetting2On = !GuiAdjustModeSelectionScreen.otherSetting2On;
//                    System.out.println("otherSetting2On: " + GuiAdjustModeSelectionScreen.otherSetting2On);
//                    break;
//                case NUMPAD3:
//                    GuiAdjustModeSelectionScreen.otherSetting3On = !GuiAdjustModeSelectionScreen.otherSetting3On;
//                    System.out.println("otherSetting3On: " + GuiAdjustModeSelectionScreen.otherSetting3On);
//                    break;
//                case NUMPAD4:
//                    GuiAdjustModeSelectionScreen.otherSetting4On = !GuiAdjustModeSelectionScreen.otherSetting4On;
//                    System.out.println("otherSetting4On: " + GuiAdjustModeSelectionScreen.otherSetting4On);
//                    break;
//                case NUMPAD5:
//                    GuiAdjustModeSelectionScreen.otherSetting5On = !GuiAdjustModeSelectionScreen.otherSetting5On;
//                    System.out.println("otherSetting5On: " + GuiAdjustModeSelectionScreen.otherSetting5On);
//                    break;
//                case NUMPAD6:
//                    GuiAdjustModeSelectionScreen.otherSetting6On = !GuiAdjustModeSelectionScreen.otherSetting6On;
//                    System.out.println("otherSetting6On: " + GuiAdjustModeSelectionScreen.otherSetting6On);
//                    break;
//                case NUMPAD7:
//                    GuiAdjustModeSelectionScreen.otherSetting7On = !GuiAdjustModeSelectionScreen.otherSetting7On;
//                    System.out.println("otherSetting7On: " + GuiAdjustModeSelectionScreen.otherSetting7On);
//                    break;
//                case NUMPAD8:
//                    GuiAdjustModeSelectionScreen.otherSetting8On = !GuiAdjustModeSelectionScreen.otherSetting8On;
//                    System.out.println("otherSetting8On: " + GuiAdjustModeSelectionScreen.otherSetting8On);
//                    break;
//                case NUMPAD9:
//                    GuiAdjustModeSelectionScreen.otherSetting9On = !GuiAdjustModeSelectionScreen.otherSetting9On;
//                    System.out.println("otherSetting9On: " + GuiAdjustModeSelectionScreen.otherSetting9On);
//                    break;

            }
        }

    }

    public static void logKeyPress(EnumMainMode mode, Keybindings key, String comment)
    {
        if(mode.equals(EnumMainMode.BUILD))
        {
            String newComment = "pressed '" + key.keybinding.getKeyDescription() + "' on '" + AdvCreation.getMode().name() +"' in '" + BuildMode.TOOLMODE.toolModeName + "' at '" + BuildMode.RIGHT_CLICK_NUMBER + "' with deletemode '" + BuildMode.DELETE_MODE + "' :" + comment;
            Logging.logMouseClick(mode.index,BuildMode.TOOLMODE.identificationIndex,key.index,BuildMode.RIGHT_CLICK_NUMBER, newComment);
        }
        else if(mode.equals(EnumMainMode.EDIT))
        {
            String newComment = "pressed '" + key.keybinding.getKeyDescription() + "' on '" + AdvCreation.getMode().name() +"' in '" + EditMode.ADJUST_MODE.toolModeName + "' at '" + EditMode.RIGHT_CLICK_NUMBER + "' with deletemode '" + EditMode.DELETE_MODE + "' :" + comment;
            Logging.logMouseClick(mode.index,BuildMode.TOOLMODE.identificationIndex,key.index,BuildMode.RIGHT_CLICK_NUMBER, newComment);
        }
        else if(mode.equals(EnumMainMode.PLACE))
        {
            int select_index = GuiTemplaceInventoryScreenFunctionality.getSelected_index();
            String newComment = "pressed '"+ key.keybinding.getKeyDescription() + "' on '" + AdvCreation.getMode().name() +"' with template None :" + comment;
            if(TemplateManager.TEMPLATES_LIST.size() > select_index && 0 <= select_index )
            {
                String filename = TemplateManager.FILENAME_LIST.get(select_index);
//                        Logging.logMouseClick(AdvCreation.getMode().index,1,2, 1, true);
                newComment = "pressed '"+ key.keybinding.getKeyDescription() + "' on '" + AdvCreation.getMode().name() +"' with " + filename+ "' :" + comment;
            }
            Logging.logMouseClick(AdvCreation.getMode().index,PlaceTemplateMode.getCurrToolId(),key.index,1,newComment);

        }
        else
        {
            String newComment = "pressed '"+ key.keybinding.getKeyDescription() +"' on '" + AdvCreation.getMode().name() + "' at '" + BuildTemplateMode.MODE.name() + "' :" + comment;
            Logging.logMouseClick(AdvCreation.getMode().index,1,key.index,BuildTemplateMode.MODE.index,newComment);

        }
    }

    public static void periodicCheckPressedKeys()
    {
        if(prevKey == Keybindings.ZOOM_IN || prevKey == Keybindings.ZOOM_OUT ||
                Keybindings.ROT_CAM_DOWN.isDown()||
                Keybindings.ROT_CAM_UP.isDown()||
                Keybindings.ROT_CAM_LEFT.isDown()||
                Keybindings.ROT_CAM_RIGHT.isDown())
        {
            long time = System.currentTimeMillis();
            if(ticksSinceLastPress >= 2)
            {
                Keybindings key = getPressedKey();
                Keybindings keyThatIsDown = key;
                if(keyThatIsDown == null && (prevKey != null &&  prevKey.isDown()))
                    keyThatIsDown = prevKey ;
                else
                    prevKey = key;

                checkKeybindingPressed(keyThatIsDown);
            }

        }

    }

}
