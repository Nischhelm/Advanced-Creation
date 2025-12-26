package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class OnlyTerrainModesPage extends AbstractPage
{


    public OnlyTerrainModesPage(String title) {
        super(title);

        //        ###############################################################################################
        String[][] intro = {{ "Only-Terrain mode allow you to exclude blocks that do not belong to the original terrain in the DIG/RAISE, SMOOTH/SHARPEN, LEVEL " +
                "adjust tool modes. It does not affect any other adjust tool modes."},
                {""},
                {"There are 2 options here:"},
                {"ONLY_TERRAIN","Only manipulate blocks that can occur naturally in a generated world without villages (excluding plants/leaves)." +
                        " Among these are: grass blocks, dirt, all types of naturally occuring stone and sand blocks and some others. This is useful for manipulating terrain around structures without affecting the structure."},
                {"ALL","All blocks within the range of the tool are manipulated."},
                {""},
                {"You can select/deselect only-terrain mode in the QUICK-SELECT menu or click the HUD overlay button on the right."},
                {"You can also toggle the only-terrain mode with the hotkey above."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings1 = {Keybindings.CHANGE_FILL_TERRAIN_MODE.getKeybind()};
        Paragraph.KeyInformation[] keys1= {
                new Paragraph.KeyInformation(keybindings1,"Toggle Only Terrain Mode")};

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (200-startX),"68_terrain_only_setting", "mp4", keys1,"Only-Terrain mode",intro, mc.getResourceManager());

        paragraphs.add(para1);
    }

}
