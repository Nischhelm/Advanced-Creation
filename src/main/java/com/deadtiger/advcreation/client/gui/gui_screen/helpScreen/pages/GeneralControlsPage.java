package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class GeneralControlsPage extends AbstractPage
{


    public GeneralControlsPage(String title)
    {
        super(title);


        //        ###############################################################################################
        String[][] undo = {{"You can undo or redo any action."},
                {""},
                {"You can also do this by clicking the Redo and Undo buttons on the HUD overlay"},
                {""},
                {"Redoing undone actions becomes unavailable if you perform an another action"},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}

        };
        KeyBinding[] keybindings8 = {Keybindings.UNDO_ACTION.getKeybind()};
        KeyBinding[] keybindings9 = {Keybindings.REDO_ACTION.getKeybind()};
        Paragraph.KeyInformation[] keys3 = {
                new Paragraph.KeyInformation(keybindings8, "Undo Action"),
                new Paragraph.KeyInformation(keybindings9, "Redo Action")};

//        Paragraph para3 = new Paragraph(startX, 50, paraWidth + (200 - startX), "textures/gui/gifs/undoaction.gif", keys3, "Redo & Undo", undo, mc.getResourceManager());
        Paragraph para3 = new Paragraph(startX, 50, paraWidth + (200 - startX), "4_undo", "mp4", keys3, "Redo & Undo", undo, mc.getResourceManager());

        //        ###############################################################################################
        String[][] cutthrough = {{"You can look inside buildings with the cut-through view."},
                {""},
                {"You can also do this by clicking the cutt-through buttons on the HUD overlay"},
                {""},
                {"This feature can cause chunks not loading, pressing the refresh terrain button solves this."},
                {""},
                {"You can also do this by clicking the RE button on the HUD overlay"},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}

        };
        KeyBinding[] keybindings10 = {Keybindings.TOGGLE_CUTTHROUGH.getKeybind()};
        KeyBinding[] keybindings11 = {Keybindings.REFRESH_TERRAIN.getKeybind()};
        Paragraph.KeyInformation[] keys4 = {
                new Paragraph.KeyInformation(keybindings10, "Toggle Cut-through view"),
                new Paragraph.KeyInformation(keybindings11, "Refresh Terrain")};

        Paragraph para4 = new Paragraph(startX, 50, paraWidth + (200 - startX), "5_cutthrough", "mp4", keys4, "Cut-through View", cutthrough, mc.getResourceManager());

        //        ###############################################################################################
        String[][] offsetting = {{"As of Alpha2.0, you can offset the current selected position relative to the cursor by using the above shown key/movement combinations."},
                {"You can move it sideways, up or down. This will help you select the right spot as the block preview shows where you will place and" +
                        "the red highlighting shows where you will delete."},
                {""},
                {"When using this adjusting, a white crosshair appears on the original position of the cursor. After adjusting, the cursor will jump back to this position."},
                {"The current offset is shown with highlighted blocks that get progressively grayer. Additionally the current offset is shown in the HUD on the bottom right corner of the screen behind 'OFFS:'."},
                {""},
                {"The current offset can be cleared by pressing the 'CLR OFF SET' HUD button in the bottom right corner of the screen or with the hotkey above"},
                {""},
                {TextFormatting.RED + "This feature is not available for PULL tool in BUILD mode and all EDIT mode tools."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}

        };
        KeyBinding[] keybindings12 = {Keybindings.ALTER_TOOL_MODE.getKeybind()};
        KeyBinding[] keybindings13 = {Keybindings.ALTER_TOOL_MODE.getKeybind(), Keybindings.ZOOM_IN.getKeybind()};
        KeyBinding[] keybindings14 = {Keybindings.ALTER_TOOL_MODE.getKeybind(), Keybindings.ZOOM_OUT.getKeybind()};
        KeyBinding[] keybindings15 = {Keybindings.CLEAR_OFFSET.getKeybind()};
        Paragraph.KeyInformation[] keys5 = {
                new Paragraph.KeyInformation(keybindings12, "+ MOVE Adjust horizontally"),
                new Paragraph.KeyInformation(keybindings13, "Adjust up"),
                new Paragraph.KeyInformation(keybindings14, "Adjust down"),
                new Paragraph.KeyInformation(keybindings15, "Clear current Offset")
        };

        Paragraph para5 = new Paragraph(startX, 50, paraWidth + (200 - startX), "56_offsetting", "mp4", keys5, "Offsetting Selection", offsetting, mc.getResourceManager());


        paragraphs.add(para3);
        paragraphs.add(para4);
        paragraphs.add(para5);


    }

}
