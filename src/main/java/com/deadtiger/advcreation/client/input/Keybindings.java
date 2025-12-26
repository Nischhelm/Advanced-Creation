package com.deadtiger.advcreation.client.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
public enum Keybindings
{
    //Change advcreation mode BUILD/PLACE/CREATE_TEMPLATE keys
    CHANGE_MODE("Toggle between Build,Edit,Place,Create", Keyboard.KEY_G,4),

    //change perspective keys
    ROTATE_RIGHT("Rotate right", Keyboard.KEY_R,5),//also rotates templates
    MIRROR_ZY("Mirror arround ZY-plane",Keyboard.KEY_M,38),
    
    //Change template offset positon relative to the middle
    OFFSET_UP("Increase selection Z offset", Keyboard.KEY_UP,6),
    OFFSET_DOWN("Decrease selection Z offset", Keyboard.KEY_DOWN,7),
    OFFSET_RIGHT("Increase selection X offset", Keyboard.KEY_RIGHT,8),
    OFFSET_LEFT("Decrease selection X offset", Keyboard.KEY_LEFT,9),
    OFFSET_PG_UP("Increase selection Y offset", Keyboard.KEY_PRIOR,10),
    OFFSET_PG_DOWN("Decrease selection Y offset", Keyboard.KEY_NEXT,11),
    CONFIRM_TEMPLATE_CREATION("Finish template creation", Keyboard.KEY_RETURN,12),
    CANCEL_TEMPLATE_CREATION("Cancel template creation", Keyboard.KEY_ESCAPE,13),
    
    //undo functionality
    UNDO_ACTION("Undo action",Keyboard.KEY_BACK,14),
    REDO_ACTION("Redo action",Keyboard.KEY_M,15),
    
    //debug
    //ACTIVATE_TO_POINT("Activate to point in circle", Keyboard.KEY_Z),
    ALTER_TOOL_MODE("Alter tool properties (see help)", Keyboard.KEY_LMENU,16),
    OPEN_TOOL_MENU("Open tool selection wheel", Keyboard.KEY_LCONTROL,17),
    CHANGE_DIR_SHAPE_MODE("Change direction/terrain shape mode",Keyboard.KEY_V,18),
    CHANGE_TOOL_MODE("Change tool to build",Keyboard.KEY_X,19),
    CHANGE_FILL_TERRAIN_MODE("Change fill/only terrain mode",Keyboard.KEY_C,20),
//    TOGGLE_PREVIEW("Toggle preview of blocks",Keyboard.KEY_K,21),
    
    //cut-through functionality
    TOGGLE_CUTTHROUGH("Toggle cut-through of world",Keyboard.KEY_Z,22),
    REFRESH_TERRAIN("Refresh the terrain",Keyboard.KEY_Q,23),

    OPEN_REPORT_SCREEN("Make Report to Developer",Keyboard.KEY_P,37),

    ROT_CAMERA("Rotate camera arround focus point",-98,38),
    ZOOM_IN("Zoom in",-101,39),
    ZOOM_OUT("Zoom out",-102,40),

    CLEAR_OFFSET("Clear offset from cursor",0,41),
    OPEN_ABS_COORD_SCREEN("Open absolute coordinates screen",Keyboard.KEY_N,42),
    TOGGLE_IGNORE_PLANTS("Toggle whether cursor ignores plants",0,43),
    TOGGLE_IGNORE_LIQUIDS("Toggle whether cursor ignores plants",Keyboard.KEY_NONE,47),

    TOGGLE_SNAP_TO_CENTER("Toggle cursor snap to block center",Keyboard.KEY_NONE,48),

    ROT_CAM_LEFT("rotate camera left",Keyboard.KEY_NONE,52),
    ROT_CAM_RIGHT("rotate camera right",Keyboard.KEY_NONE,53),
    ROT_CAM_UP("rotate camera up",Keyboard.KEY_NONE,54),
    ROT_CAM_DOWN("rotate camera down",Keyboard.KEY_NONE,55),

    HOTKEY_TOOL_1("Tool 1 SINGLE/PAINT",Keyboard.KEY_NONE,56),
    HOTKEY_TOOL_2("Tool 2 LINE/PLANT",Keyboard.KEY_NONE,57),
    HOTKEY_TOOL_3("Tool 3 RECT/DIG/RAISE",Keyboard.KEY_NONE,58),
    HOTKEY_TOOL_4("Tool 4 CURVE/LEVEL",Keyboard.KEY_NONE,59),
    HOTKEY_TOOL_5("Tool 5 CIRCLE/SMOOTH",Keyboard.KEY_NONE,60),
    HOTKEY_TOOL_6("Tool 6 PULL/PAINTBUCKET",Keyboard.KEY_NONE,61),
    HOTKEY_TOOL_7("Tool 7 COPY",Keyboard.KEY_NONE,62),
    HOTKEY_TOOL_8("Tool 8 MOVE/DEL",Keyboard.KEY_NONE,63),
    HOTKEY_TOOL_9("Tool 9 FILLGAP",Keyboard.KEY_NONE,64),
    TO_ISOMETRIC_VIEW("Toggle isometric view",Keyboard.KEY_F5,65),
    TOGGLE_TOOLS_ENABLED("Toggle Adv. Creation tools", Keyboard.KEY_L,66);

//    NUMPAD1("debug setting 1",Keyboard.KEY_NUMPAD1,65),
//    NUMPAD2("debug setting 2",Keyboard.KEY_NUMPAD2,66),
//    NUMPAD3("debug setting 3",Keyboard.KEY_NUMPAD3,67),
//    NUMPAD4("debug setting 4",Keyboard.KEY_NUMPAD4,68),
//    NUMPAD5("debug setting 5",Keyboard.KEY_NUMPAD5,69),
//    NUMPAD6("debug setting 6",Keyboard.KEY_NUMPAD6,70),
//    NUMPAD7("debug setting 7",Keyboard.KEY_NUMPAD7,71),
//    NUMPAD8("debug setting 8",Keyboard.KEY_NUMPAD8,72),
//    NUMPAD9("debug setting 9",Keyboard.KEY_NUMPAD9,73);


    public final KeyBinding keybinding;
    public final int index;
    
    Keybindings(String keyName, int defaultKeyCode, int index)
    {
        keybinding = new KeyBinding(keyName,defaultKeyCode,"Mod.AdvCreation");
        this.index = index;

    }

    public static void rebindPickUpBlockKey()
    {
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        if (gameSettings.keyBindPickBlock.getKeyCode() == -98)// -98 == middle mouseButton
            gameSettings.keyBindPickBlock.setKeyCode(Keyboard.KEY_Q);
    }
    /*
    //Change advcreation mode BUILD/PLACE/CREATE_TEMPLATE keys
    CHANGE_MODE("key.advcreation.decrDivider", Keyboard.KEY_G),

    //Debug key
    INCREASE_DIVIDER("key.advcreation.incrDivider", Keyboard.KEY_H),
    
    //change perspective keys
    OPEN_TOOL_MENU("key.advcreation.activateRotateScreenMode", Keyboard.KEY_LCONTROL),
    ROTATE_RIGHT("key.advcreation.rotateRight", Keyboard.KEY_R),//also rotates templates
    ROTATE_LEFT("key.advcreation.rotateLeft", Keyboard.KEY_Q),
    
    //GuiScreen for selecting a template to build
    OPEN_CLOSE_GUI_TEMPLATE_SELECT_SCREEN("key.advcreation.openGuiTemplateSelectScreen", Keyboard.KEY_N),
    
    //Change template offset positon relative to the middle
    OFFSET_UP("key.advcreation.templateOffsetUp", Keyboard.KEY_UP),
    OFFSET_DOWN("key.advcreation.templateOffsetDown", Keyboard.KEY_DOWN),
    OFFSET_LEFT("key.advcreation.templateOffsetLeft", Keyboard.KEY_LEFT),
    OFFSET_RIGHT("key.advcreation.templateOffsetRight", Keyboard.KEY_RIGHT),
    OFFSET_PG_UP("key.advcreation.templateOffsetPgUp", Keyboard.KEY_PRIOR),
    OFFSET_PG_DOWN("key.advcreation.templateOffsetPgDown", Keyboard.KEY_NEXT),
    CONFIRM_TEMPLATE_CREATION("key.advcreation.templateConfirmCreation", Keyboard.KEY_RETURN),
    CANCEL_TEMPLATE_CREATION("key.advcreation.templateCancelCreation", Keyboard.KEY_ESCAPE);
    
    private final KeyBinding keybinding;

    Keybindings(String keyName, int defaultKeyCode)
    {
        keybinding = new KeyBinding(keyName,defaultKeyCode,"key.categories.advcreation");
    }*/

    public KeyBinding getKeybind()
    {
        return keybinding;
    }

    public boolean isPressed()
    {
        return keybinding.isPressed();
    }
    
    public boolean isDown(){return keybinding.isKeyDown();}


}
