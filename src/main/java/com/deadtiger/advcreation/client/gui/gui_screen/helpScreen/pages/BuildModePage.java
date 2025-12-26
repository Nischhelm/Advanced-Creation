package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;

public class BuildModePage extends AbstractPage
{

    public BuildModePage(String title)
    {
        super(title);

        //        ###############################################################################################
        String[][] intro = {{"BUILD mode gives you a set of tools to place blocks easily and build structures with multiple clicks."},
                {""},
                {"When building in BUILD mode there are 3 submodes you can specify:"},
                {"Tool mode:", "What kind of shape you want to place or what kind of action you want to perform."},
                {"Direction mode:", "Do you want to place blocks freely or in a specific 2D plane?"},
                {"Fill mode:", "Do you want to fill up the enclosed space or leave is open? (Only rectangles and circles)"},
                {""},
                {"The QUICK-SELECT menu can be opened to pick an option for each submode quickly."}
        };
        KeyBinding[] keybindings1 = {mc.gameSettings.keyBindAttack};
        KeyBinding[] keybindings2 = {mc.gameSettings.keyBindUseItem};
        KeyBinding[] keybindings3 = {Keybindings.CANCEL_TEMPLATE_CREATION.getKeybind()};
        KeyBinding[] keybindings4 = {Keybindings.OPEN_TOOL_MENU.getKeybind()};

        Paragraph.KeyInformation[] keys1 = {
                new Paragraph.KeyInformation(keybindings1, "DELETE/advance DELETE action"),
                new Paragraph.KeyInformation(keybindings2, "PLACE/advance PLACE action"),
                new Paragraph.KeyInformation(keybindings3, "Cancel current action"),
                new Paragraph.KeyInformation(keybindings4, "HOLD to open QUICK-SELECT menu")};

        Paragraph para1 = new Paragraph(startX, 50, paraWidth + (200 - startX), "toolquickselect_1", "png", keys1, "Intro Build Mode", intro, mc.getResourceManager());

        //        ###############################################################################################
        String[][] buildhud = {
                {"Your current submode settings are shown on the right HUD overlay buttons from top to bottom it shows: Tool mode, direction mode and fill mode."},
                {"Clicking on any of these buttons will toggle through the settings in sequence."},
                {""},
                {"Right above the standard minecraft inventory there is a progressbar that shows you the current tool mode you are using and it shows your " +
                        "progress in the amount of clicks necessary to complete the current action."},
                {"When an action is complete is turns green for a second, when you cancel the current action it turns red."}};

        Paragraph para2 = new Paragraph(startX, 50, paraWidth + (200 - startX), "buildhud", "png", null, "HUD overlay Build Mode", buildhud, mc.getResourceManager());


        paragraphs.add(para1);
        paragraphs.add(para2);

    }
}
