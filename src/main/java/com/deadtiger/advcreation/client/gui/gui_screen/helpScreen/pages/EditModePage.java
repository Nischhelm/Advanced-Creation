package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class EditModePage extends AbstractPage
{

    public EditModePage(String title) {
        super(title);

        //        ###############################################################################################
        String[][] intro = {{ "EDIT mode gives you a set of brush-like tools to edit the world and structures."},
                {""},
                {TextFormatting.GREEN + "As of Beta1.0, deleting with left-click will not happen immediately. The first left-click will switch you to the delete" +
                        " mode of that tool. The progressbar will show a message like: 'Switching to ... mode' and no operation will have happened. Left-clicking again will execute a delete mode operation. " +
                        "To switch back to edit mode you have to right-click, which will again will show you the previous message without doing any operation."},
                {""},
                {"When building in EDIT mode there are 3 submode to specify:"},
                {"Adjust mode:","How do you want to edit the world?" },
                {"Terrain Shape mode:","How steep do you want the new slopes to be"},
                {"Only Terrain mode:","Do you want to manipulate all blocks or only the terrain?"},
                {"The Terrain Shape & Only Terrain mode only affect the DIG/RAISE, SMOOTH/SHARPEN and LEVEL Adjust tool mode."},
                {""},
                {"The QUICK-SELECT menu can be opened to pick an option for each submode quickly."},
                {""},
                {"The top-right buttons can be used to switch to the BUILD main mode QUICK-SELECT menu."},
                {""},
                {"The highlight legend can be toggled to show information about what the colors of the block highlights mean."},
                {"This is especially usefull for the DIG/RAISE, SMOOTH/SHARPEN and LEVEL Adjust tool modes added in Beta1.0 as these" +
                        " required more complex indication to show you what the tool will do when you click."}

        };



        KeyBinding[] keybindings1 = {mc.gameSettings.keyBindAttack};
        KeyBinding[] keybindings2 = {mc.gameSettings.keyBindUseItem};
        KeyBinding[] keybindings3 = {Keybindings.CANCEL_TEMPLATE_CREATION.getKeybind()};
        KeyBinding[] keybindings4 = {Keybindings.OPEN_TOOL_MENU.getKeybind()};
        Paragraph.KeyInformation[] keys1= {
                new Paragraph.KeyInformation(keybindings1,"DELETE action"),
                new Paragraph.KeyInformation(keybindings2,"EDIT action"),
                new Paragraph.KeyInformation(keybindings3,"Cancel current action"),
                new Paragraph.KeyInformation(keybindings4,"HOLD to open QUICK-SELECT menu")};

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (200-startX),"editquickselect_1", "png", keys1,"Intro Edit Mode",intro, mc.getResourceManager());

        //        ###############################################################################################
        String[][] buildhud = {
                {"Your current Adjust mode is shown on the right HUD overlay button."},
                {"Clicking on this button will toggle through the settings in sequence."},
                {""},
                {"Right above the standard minecraft inventory there is a progressbar that shows you the current adjust mode you are using."},
                {"When an action is active it turns green."}};

        Paragraph para2 = new Paragraph(startX,50,paraWidth + (200-startX),"edithud", "png", null,"HUD overlay Edit Mode",buildhud, mc.getResourceManager());

        paragraphs.add(para1);
        paragraphs.add(para2);

    }
}
