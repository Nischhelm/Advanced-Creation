package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;

public class ProMovesPage extends AbstractPage
{

    public ProMovesPage(String title)
    {
        super(title);

        //        ###############################################################################################
        String[][] intro = {{"Here are some pro moves to inspire you on ways of using the tools that are provided in this mod and convince you of their versatility."}};

        Paragraph para1 = new Paragraph(startX, 50, paraWidth + (200 - startX), null, "mp4", null, "Intro Pro Moves", intro, mc.getResourceManager());

        //        ###############################################################################################
        String[][] prop = {{"You can change the direction mode (FREE,AUTO,XY,...) after the first or second click to create interesting shapes"},
                {""},
                {"The above GIF and Keys show an example for PLACING a rectangle with the RECTANGLE tool in BUILD mode but you can also use it to DELETE. Same goes for any tool that requires multiple clicks."}};
        KeyBinding[] keybindings1 = {mc.gameSettings.keyBindUseItem};
        KeyBinding[] keybindings2 = {Keybindings.OPEN_TOOL_MENU.getKeybind(), mc.gameSettings.keyBindAttack};
        Paragraph.KeyInformation[] keys4 = {
                new Paragraph.KeyInformation(keybindings1, "1st CLICK to Start PLACING"),
                new Paragraph.KeyInformation(keybindings2, "Select a New Direction"),
                new Paragraph.KeyInformation(keybindings1, "2nd Click to Continue PLACING"),
                new Paragraph.KeyInformation(keybindings2, "Select a New Direction"),
                new Paragraph.KeyInformation(keybindings1, "3rd Click to Finish PLACING")};

        Paragraph para2 = new Paragraph(startX, 50, paraWidth + (200 - startX), "promove_1", "mp4", keys4, "MOVE #1: Change Direction Mid-Action", prop, mc.getResourceManager());

        //        ###############################################################################################
        String[][] adjust = {{"You can create a wall that follows the terrain by using the RECTANGLE tool"},
                {""},
                {"You do this by creating the first side straight up, place that and then change to GROUND direction mode"},
        };
        Paragraph.KeyInformation[] keys5 = {
                new Paragraph.KeyInformation(keybindings1, "1st CLICK to Start PLACING"),
                new Paragraph.KeyInformation(keybindings2, "Select XY or ZY direction"),
                new Paragraph.KeyInformation(keybindings1, "2nd Click Straight up"),
                new Paragraph.KeyInformation(keybindings2, "Select GROUND direction"),
                new Paragraph.KeyInformation(keybindings1, "3rd Click to Finish PLACING")};

        Paragraph para3 = new Paragraph(startX, 50, paraWidth + (200 - startX), "48_promove_2", "mp4", keys5, "MOVE #2: Create Walls Following Terrain", adjust, mc.getResourceManager());

        //        ###############################################################################################
        String[][] iconmaker = {{"Use the PULL tool with the GROUND direction mode to pull untill the next solid blocks " +
                "in that direction, this works not only in the vertical direction!"}
        };
        Paragraph.KeyInformation[] keys6 = {
                new Paragraph.KeyInformation(keybindings1, "1st CLICK to SELECT Surface"),
                new Paragraph.KeyInformation(keybindings2, "Select GROUND direction"),
                new Paragraph.KeyInformation(keybindings1, "2nd CLICK Anywhere To Finish PLACING")};

        Paragraph para4 = new Paragraph(startX, 50, paraWidth + (200 - startX), "49_promove_3", "mp4", keys6, "MOVE #3: Pulling to opposite surface", iconmaker, mc.getResourceManager());

        //        ###############################################################################################
        String[][] adjustcopy = {{"The COPY and MOVE/DEL toolmodes are made to be fast 2 click selection. If however you still want to adjust the selection it is possible!"},
                {""},
                {" After selecting the initial area to move or copy you can go to CREATE mode and change the dimensions of the selection box there, before going back to BUILD mode to execute the operation"}
        };
        KeyBinding[] keybindings3 = {mc.gameSettings.keyBindAttack};
        Paragraph.KeyInformation[] keys7 = {
                new Paragraph.KeyInformation(keybindings3, "1st 2 CLICKs to SELECT AREA"),
                new Paragraph.KeyInformation(keybindings3, "3rd CLICK the CREATE Mode Button"),
                new Paragraph.KeyInformation(keybindings3, "4th 2 CLICKS to Adjust Selection Box"),
                new Paragraph.KeyInformation(keybindings3, "6th CLICK the BUILD Mode Button"),
                new Paragraph.KeyInformation(keybindings1, "7th CLICK PLACE copied/moved area")
        };

        Paragraph para5 = new Paragraph(startX, 50, paraWidth + (200 - startX), "50_promove_4", "mp4", keys7, "MOVE #4: Adjust COPY/MOVE Selection area", adjustcopy, mc.getResourceManager());

        //        ###############################################################################################
        String[][] cancel = {{"You can also CANCEL any PLACE or DELETE action that needs multiple clicks by pressing the button for the opposite action. This means that when in a PLACE action, pressing the DELETE action button cancels it and vice versa."},
        };

        Paragraph.KeyInformation[] keys8 = {
                new Paragraph.KeyInformation(keybindings1, "1st CLICK to Start PLACE Action"),
                new Paragraph.KeyInformation(keybindings3, "2nd CLICK to Cancel PLACE Action"),
                new Paragraph.KeyInformation(keybindings3, "1st CLICK to Start DELETE Action"),
                new Paragraph.KeyInformation(keybindings1, "2nd CLICK to Cancel DELETE Action"),
        };
        Paragraph para6 = new Paragraph(startX, 50, paraWidth + (200 - startX), "52_quickcancel", "mp4", keys8, "MOVE #5: Quick Cancel Of Current Action", cancel, mc.getResourceManager());


        //        ###############################################################################################
        String[][] redstoneGround = {{"If you tried to place redstone wires or rails on a hill with the LINE tool and FREE direction mode, " +
                "you would have to make sure the line places all the rails/wires right above the ground and not in the air."},
                {"By using the GROUND direction mode, all parts of the line will follow the surface of the ground. This makes placing redstone wires and rails much easier."},
                {"There are limits though, both redstone wires and rails can only rise/descend 1 block per block. " +
                        "Additionally, rails need a straight rail to change height which the tool does not take into account, as seen in the GIF, manual correction is necessary. "}
        };
        KeyBinding[] keybindings4 = {Keybindings.OPEN_TOOL_MENU.getKeybind(),  mc.gameSettings.keyBindAttack};

        Paragraph.KeyInformation[] keys9 = {
                new Paragraph.KeyInformation(keybindings4, "Select GROUND direction"),
                new Paragraph.KeyInformation(keybindings1, "1st CLICK to Start PLACE Action"),
                new Paragraph.KeyInformation(keybindings1, "2nd CLICK to finish PLACE Action")
        };

        Paragraph para7 = new Paragraph(startX, 50, paraWidth + (200 - startX), "61_promove5_redstone_ground", "mp4", keys9, "MOVE #6: Place Rail/Redstone On A Hill", redstoneGround, mc.getResourceManager());

        paragraphs.add(para1);
        paragraphs.add(para2);
        paragraphs.add(para3);
        paragraphs.add(para4);
        paragraphs.add(para5);
        paragraphs.add(para6);
        paragraphs.add(para7);

    }
}
