package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class FillModesPage extends AbstractPage
{


    public FillModesPage(String title) {
        super(title);

        //        ###############################################################################################
        String[][] intro = {{ "Fill modes allow you to place a filled or non-filled rectangle and circle."},
                {""},
                {"As seen in the Build Mode intro page you can select a fill mode in the QUICK-SELECT menu or click the HUD overlay button on the right."},
                {"You can also toggle the fill mode with the hotkey above."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings1 = {Keybindings.CHANGE_FILL_TERRAIN_MODE.getKeybind()};
        Paragraph.KeyInformation[] keys1= {
                new Paragraph.KeyInformation(keybindings1,"Toggle Fill Mode")};

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (200-startX),"53_fill_mode", "mp4", keys1,"To Fill or Not To Fill",intro, mc.getResourceManager());

        paragraphs.add(para1);
    }

}
