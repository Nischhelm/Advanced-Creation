package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class OverviewPage extends AbstractPage
{


    public OverviewPage(String title) {
        super(title);

        String[][] general = {{ "Welcome to the Advanced Creation Mod! This mod will help you build quick and easy from an isometric perspective in Minecraft."},
                { ""},
                {TextFormatting.GREEN + "As of Alpha2.0, the SURVIVAL gamemode no longer has access to any Advanced Creation " +
                        "features they are only available in CREATIVE gamemode in the Isometric View perspective!"},
                { ""},
                {"This help screen contains all information you need to start understanding all that this mod has to offer. So no more Alt-Tabbing out of the game to look up tutorials " +
                        "and instructions for how to use features in a mod! "},
                {""},
                {"Each instruction will be accompanied with a quick GIF to show you how things are done properly. The keys you need to press will be shown on the right of each GIF."},
                {""},
                {"Click the Next button or click on a topic you want to know about in the overview on the left to start learning."},
                {""},
                {""},
                {"Can't find the information you are looking for? Let me know by making a report and sending it to me. Learn more about it on the 'Make Bug Reports' page under 'Wanna Contribute?' section."},
        };


        KeyBinding[] keybindings1 = {Keybindings.ALTER_TOOL_MODE.getKeybind(),Keybindings.OFFSET_DOWN.getKeybind()};
        Paragraph.KeyInformation[] keys= {new Paragraph.KeyInformation(keybindings1,"example")};


//        Paragraph para1 = new Paragraph(startX,50,paraWidth + (offsetX-startX),"trailer_ingame", "mp4", keys,"Overview",general, mc.getResourceManager());
//        para1.setFreezeEndGif(true);

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (offsetX-startX),"advanced_logo", "jpg", keys,"Overview",general, mc.getResourceManager());
//        para1.setFreezeEndGif(true);

        paragraphs.add(para1);
    }

}
