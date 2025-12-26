package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class MainModesPage extends AbstractPage
{


    public MainModesPage(String title) {
        super(title);

        String[][] general = {{ "There are 4 different MAIN modes each one serves a specific purpose."},
                { ""},
                {"Which mode you are in, is indicated by the highlighted button on the top-left corner of the screen."},
                {"You can switch between modes by clicking one of these buttons on the HUD overlay or by pressing the hotkey shown above."},
                {""},
                {"Here is a quick overview of the function of each mode, but far more information can be found in their seperate help pages:"},
                {"  BUILD mode:","Has a set of tools to place blocks easily and build structures with multiple clicks."},
                {"  EDIT mode:","Has a set of tools to change blocks and edit the landscape with brush like tools."},
                {"  PLACE mode:","Has a custom inventory to select and place templates made by you or others."},
                {"  CREATE mode:","Allows you to select an area of the world and save it as a new template."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"},
        };

        KeyBinding[] keybindings1 = {Keybindings.CHANGE_MODE.getKeybind()};
        Paragraph.KeyInformation[] keys= {new Paragraph.KeyInformation(keybindings1,"Toggle Main Mode")};

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (200-startX),"mainmodes", "png", keys,"Overview",general, mc.getResourceManager());

        paragraphs.add(para1);
    }

}
