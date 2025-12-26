package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class PlaceModePage extends AbstractPage
{

    public PlaceModePage(String title)
    {
        super(title);

        //        ###############################################################################################
        String[][] intro = {{"PLACE mode allows you to select a template and place it in the world."},
                {"The red highlighted area indicates the air inside the building, this is important for the air mode setting explained further in this page."}
        };
        KeyBinding[] keybindings1 = {mc.gameSettings.keyBindAttack};
        KeyBinding[] keybindings2 = {mc.gameSettings.keyBindUseItem};
        Paragraph.KeyInformation[] keys1 = {
                new Paragraph.KeyInformation(keybindings1, "DETACH/ATTACH mouse from template"),
                new Paragraph.KeyInformation(keybindings2, "PLACE Template")};

        Paragraph para1 = new Paragraph(startX, 50, paraWidth + (200 - startX), "36_place_mode", "mp4", keys1, "Intro PLACE Mode", intro, mc.getResourceManager());

        //        ###############################################################################################
        String[][] prop = {{"A custom inventory screen can be opened with the button shown above."},
                {"Like vanilla inventory you can drag items into your quickaccess hotbar on the bottom of the window, except here the items are templates."},
                {""},
                {"Here there are 4 properties that you can search for:"},
                {"Template Name", "The name given by the player to the template"},
                {"Function", "The original function of the template ex.: castle, house,blacksmith,etc."},
                {"Category", "Which part of a structure this template is, 'structure' is a standalone building but other ex. are: wall, tower, room, etc."},
                {"Style", "The architecture style of the building ex.: medieval, futuristic, gothic, etc."},
                {""},
                {"The Function, Category and Style textfields show suggestions but you can write what you want there."},
                {"None of these properties change the usage of the template, they are purely descriptive."}

        };
        KeyBinding[] keybindings9 = {mc.gameSettings.keyBindInventory};
        Paragraph.KeyInformation[] keys4 = {
                new Paragraph.KeyInformation(keybindings9, "Open Template Inventory")};

        Paragraph para2 = new Paragraph(startX, 50, paraWidth + (200 - startX), "templateinventory", "png", keys4, "Template Inventory", prop, mc.getResourceManager());

        //        ###############################################################################################
        String[][] adjust = {{"Both when attached or detached you can adjust the template's position relative to the cursor."},
                {"You can move it sideways, up or down. This will help you place it in the right spot."},
                {""},
                {"When using this adjusting, a white crosshair appears on the original position of the cursor. After adjusting, the cursor will jump back to this position."}
        };
        KeyBinding[] keybindings5 = {Keybindings.ALTER_TOOL_MODE.getKeybind()};
        KeyBinding[] keybindings6 = {Keybindings.ALTER_TOOL_MODE.getKeybind(), Keybindings.ZOOM_IN.getKeybind()};
        KeyBinding[] keybindings7 = {Keybindings.ALTER_TOOL_MODE.getKeybind(), Keybindings.ZOOM_OUT.getKeybind()};
        KeyBinding[] keybindings8 = {Keybindings.ROTATE_RIGHT.getKeybind()};
        KeyBinding[] keybindings10 = {Keybindings.MIRROR_ZY.getKeybind()};
        Paragraph.KeyInformation[] keys10 = {
                new Paragraph.KeyInformation(keybindings5, "+ MOVE Adjust horizontally"),
                new Paragraph.KeyInformation(keybindings6, "Adjust up"),
                new Paragraph.KeyInformation(keybindings7, "Adjust down"),
                new Paragraph.KeyInformation(keybindings8, "Rotate area"),
                new Paragraph.KeyInformation(keybindings10, "Mirror area")};

        Paragraph para3 = new Paragraph(startX, 50, paraWidth + (200 - startX), "19_adjust", "mp4", keys10, "Adjust Template Position", adjust, mc.getResourceManager());

        //        ###############################################################################################
        String[][] buildhud = {
                {"When using PLACE mode there are 3 submodes you can specify from:"},
                {"Foundation mode:", "What kind of foundation do you want to place the template on?"},
                {"Air mode:", "Which parts of the walls do you want to place?"},
                {"Wall mode:", "Which parts of the air blocks do you want to place?"},
                {""},
                {"Your current submode settings are shown on the right HUD overlay buttons from top to bottom it shows: Foundation mode,Air mode and Wall mode."},
                {"Clicking on any of these buttons will open a screen where you can select the setting you want."},
                {""},
                {"Right above the standard minecraft inventory there is a progressbar that shows you the current selected template and whether the template is attached to the mouse or not."},
                {"When the template is placed it turns green for a second."}};

        Paragraph para4 = new Paragraph(startX, 50, paraWidth + (200 - startX), "placehud", "png", null, "HUD overlay PLACE Mode", buildhud, mc.getResourceManager());

        //        ###############################################################################################
        String[][] foundations = {{"Foundation modes allow you to easily place blocks under the walls and inside air blocks of a template."},
                {""},
                {"There are 3 foundation modes you can choose from:"},
                {"No Foundation:", "No blocks are added to the bottom of template."},
                {"One Layer of Foundation:", "One Layer of blocks is added to the bottom of the template."},
                {"Foundation to Ground:", "Foundation blocks are added under the template untill the first ground block."},
                {""},
                {"As seen in the Place Mode intro page you can select a foundation mode by clicking on the HUD overlay button on the right."},
                {"When a foundation is selected you can select the type of block you want to use as foundation in the HUD overlay button on the right."},
                {"You can also toggle the foundation mode with the hotkey above."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings11 = {Keybindings.CHANGE_DIR_SHAPE_MODE.getKeybind()};

        Paragraph.KeyInformation[] keys11 = {
                new Paragraph.KeyInformation(keybindings11, "Toggle Foundation Mode")};

        Paragraph para5 = new Paragraph(startX, 50, paraWidth + (200 - startX), "38_place_foundation", "mp4", keys11, "Foundations Modes", foundations, mc.getResourceManager());

        //        ###############################################################################################
        String[][] air = {{"Air modes allow you to specify which air blocks in the template are placed."},
                {""},
                {"There are 3 air modes you can choose from:"},
                {"No Air:", "None of the air blocks are placed"},
                {"Inside Air:", "Only the air inside the walls of the structure is placed. Inside air is indicated by red highlighted air blocks."},
                {"All Air:", "All air blocks in the template are placed."},
                {""},
                {"As seen in the Place Mode intro page you can select a air mode by clicking on the HUD overlay button on the right."},
                {"You can also toggle the air mode with the hotkey above."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings12 = {Keybindings.CHANGE_TOOL_MODE.getKeybind()};
        Paragraph.KeyInformation[] keys12 = {
                new Paragraph.KeyInformation(keybindings12, "Toggle Air Mode")};

        Paragraph para6 = new Paragraph(startX, 50, paraWidth + (200 - startX), "39_place_air", "mp4", keys12, "Air Modes", air, mc.getResourceManager());

        //        ###############################################################################################
        String[][] walls = {{"Wall modes allow you to specify which solid blocks in the template are placed."},
                {""},
                {"There are 3 wall modes you can choose from:"},
                {"No Wall:", "No solid block is placed. This means stairs, slabs, fences, torchs and furniture like furnaces and crafting tables are placed. This allows you to carve out a structure from existing terrain."},
                {"New Walls:", "Only solid blocks that are not already occupied with a solid terrain block are placed."},
                {"All Walls:", "All solid blocks are placed."},
                {""},
                {"As seen in the Place Mode intro page you can select a wall mode by clicking on the HUD overlay button on the right."},
                {"You can also toggle the wall mode with the hotkey above."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings13 = {Keybindings.CHANGE_FILL_TERRAIN_MODE.getKeybind()};

        Paragraph.KeyInformation[] keys13 = {
                new Paragraph.KeyInformation(keybindings13, "Toggle Wall Mode")};

        Paragraph para7 = new Paragraph(startX, 50, paraWidth + (200 - startX), "40_place_walls", "mp4", keys13, "Wall Modes", walls, mc.getResourceManager());

        //        ###############################################################################################
        String[][] download = {{"You can get templates made by other players on my website 'www.advancedcreationmod.com'. You do this by following these steps:"},
                {"1. ", "Go to 'www.advancedcreationmod.com/templates', here you can browse and search for templates. "},
                {"2. ", "Press the download icon on the chosen template."},
                {"3. ", "Drag/Copy the downloaded zipfile into the 'advcreation_templates_zips' folder in the '.minecraf' folder."},
                {"4. ", "Go back to minecraft, press the refresh button in the custom template inventory in PLACE mode."}
        };

        Paragraph para8 = new Paragraph(startX, 50, paraWidth + (200 - startX), "55_download_template", "mp4", null, "Download New Templates", download, mc.getResourceManager());

        paragraphs.add(para1);
        paragraphs.add(para2);
        paragraphs.add(para3);
        paragraphs.add(para4);
        paragraphs.add(para5);
        paragraphs.add(para6);
        paragraphs.add(para7);
        paragraphs.add(para8);

    }
}
