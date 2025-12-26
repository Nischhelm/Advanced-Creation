package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class AdjustModesPage extends AbstractPage
{


    public AdjustModesPage(String title)
    {
        super(title);

        //        ###############################################################################################
        String[][] intro = {{"Adjust modes are your primary tools for editing the world and structures."},
                {""},
                {"As seen in the Edit Mode intro page you can select a toolmode in the QUICK-SELECT menu or click the HUD overlay button on the right."},
                {"You can also toggle the adjust mode with the hotkey above."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings1 = {Keybindings.CHANGE_TOOL_MODE.getKeybind()};
        Paragraph.KeyInformation[] keys1 = {
                new Paragraph.KeyInformation(keybindings1, "Toggle Adjust Mode")};

        Paragraph para1 = new Paragraph(startX, 50, paraWidth + (200 - startX), null, "mp4", keys1, "Intro Adjust Modes", intro, mc.getResourceManager());

        //        ###############################################################################################
        String[][] perspective1 = {{"The PAINTBUCKET adjust mode allows you to REPLACE a connected section of a block type to the block in your hand."},
                {"It can also be used to DELETE the connected section."}};
        KeyBinding[] keybindings2 = {mc.gameSettings.keyBindAttack};
        KeyBinding[] keybindings3 = {mc.gameSettings.keyBindUseItem};
        KeyBinding[] keybindings8 = {Keybindings.ROTATE_RIGHT.getKeybind()};
        Paragraph.KeyInformation[] keys2 = {
                new Paragraph.KeyInformation(keybindings2, "DELETE selection"),
                new Paragraph.KeyInformation(keybindings3, "REPLACE selection")};

        Paragraph para2 = new Paragraph(startX, 50, paraWidth + (200 - startX), "31_paintbucket", "mp4", keys2, "PAINTBUCKET Adjust Mode", perspective1, mc.getResourceManager());
        //        ###############################################################################################
        String[][] perspective2 = {{"The PAINT adjust mode provides a paintbrush that allows you to REPLACE the current selection with the block in your hand."},
                {"You can hold the REPLACE button and move to paint the world or structures."},
                {"It can also be used to DELETE the current selection."},
                {""},
                {"The size and shape of your paintbrush can be edited with the keycombination shown above."}
        };
        KeyBinding[] keybindings4 = {Keybindings.ALTER_TOOL_MODE.getKeybind(), Keybindings.ZOOM_IN.getKeybind()};
        KeyBinding[] keybindings5 = {Keybindings.ALTER_TOOL_MODE.getKeybind(), Keybindings.ZOOM_OUT.getKeybind()};
        Paragraph.KeyInformation[] keys3 = {
                new Paragraph.KeyInformation(keybindings2, "REPLACE Selection"),
                new Paragraph.KeyInformation(keybindings3, "DELETE Selection"),
                new Paragraph.KeyInformation(keybindings4, "Increase Brush Size"),
                new Paragraph.KeyInformation(keybindings5, "Decrease Brush Size")
        };

        Paragraph para3 = new Paragraph(startX, 50, paraWidth + (200 - startX), "32_paint", "mp4", keys3, "PAINT Adjust Mode", perspective2, mc.getResourceManager());


        //        ###############################################################################################
        String[][] firstPerson = {{"The PLANT adjust mode provides a brush for PLACING and DELETING plant life."},
                {"This will help clear plants from the area where you want to build structures."},
                {"When a log or leaf block is in your hand you can PLACE a tree of that type."},
                {"Increasing or Decreasing the size of the brush will change the height of the tree."},
                {"When DELETING only plant life will be removed."},
                {""},
                {"Pressing the regenerate button shown above will generate a new vanilla tree that can be PLACED."}
        };
        Paragraph.KeyInformation[] keys4 = {
                new Paragraph.KeyInformation(keybindings2, "DELETE Plant Life"),
                new Paragraph.KeyInformation(keybindings3, "PLACE Plant Life"),
                new Paragraph.KeyInformation(keybindings8, "Regenerate Tree/Plants"),
                new Paragraph.KeyInformation(keybindings4, "Increase Brush Size"),
                new Paragraph.KeyInformation(keybindings5, "Decrease Brush Size")};

        Paragraph para4 = new Paragraph(startX, 50, paraWidth + (200 - startX), "33_plant", "mp4", keys4, "PLANT Adjust Mode", firstPerson, mc.getResourceManager());

        //        ###############################################################################################
        String[][] digRaise = {{"The DIG/RAISE adjust mode provides a brush for DIGGING and RAISING terrain or other surfaces."},
                {"How deep/high the hole/pile you are digging/raising is decided by the TERRAIN SHAPE options (see next chapter). These options also " +
                        " decide how steep the sides of the hole/pile are, going from nearly flat to straight down."},
                {""},
                {"The ONLY TERRAIN option can help you manipulate terrain arround your structures without affecting the structures.(see 'Only Terrain Modes' chapter) "},
                {""},
                {"The size and shape of your brush can be edited with the keycombination shown above."}
        };
        Paragraph.KeyInformation[] keys5 = {
                new Paragraph.KeyInformation(keybindings2, "Dig Down"),
                new Paragraph.KeyInformation(keybindings3, "Raise Up"),
                new Paragraph.KeyInformation(keybindings4, "Increase Brush Size",true,false),
                new Paragraph.KeyInformation(keybindings5, "Decrease Brush Size", false, true)};

        Paragraph para5 = new Paragraph(startX, 50, paraWidth + (200 - startX), "62_dig_raise_adjust_mode", "mp4", keys5, "DIG/RAISE Adjust Mode", digRaise, mc.getResourceManager());

        //        ###############################################################################################
        String[][] smooth = {{"The SMOOTH/SHARPEN adjust mode provides a brush for manipulating slopes."},
                {"This tool can be used to change steep sloops or shallow slopes but can also be used to smoothen " +
                        "transistions between newly placed templates and existing terrain."},
                {"The power of the smoothing and sharpening operation is decided by the TERRAIN SHAPE options (see next chapter)."},
                {""},
                {"The ONLY TERRAIN option can help you manipulate terrain arround your structures without affecting the structures.(see 'Only Terrain Modes' chapter) "},
                {""},
                {"The size and shape of your brush can be edited with the keycombination shown above."}
        };
        Paragraph.KeyInformation[] keys6 = {
                new Paragraph.KeyInformation(keybindings2, "Smoothen the slope"),
                new Paragraph.KeyInformation(keybindings3, "Sharpen the slope"),
                new Paragraph.KeyInformation(keybindings4, "Increase Brush Size",true,false),
                new Paragraph.KeyInformation(keybindings5, "Decrease Brush Size", false, true)};

        Paragraph para6 = new Paragraph(startX, 50, paraWidth + (200 - startX), "63_smooth_sharpen_adjust_mode", "mp4", keys5, "SMOOTH/SHARPEN Adjust Mode", smooth, mc.getResourceManager());

//        ###############################################################################################
        String[][] level = {{"The LEVEL adjust mode provides a brush for lowering/raising the terrain/surface to the lowest/highest block " +
                "in the selected area."},
                {"The steepness of the edges of the level operation is decided by the TERRAIN SHAPE options (see next chapter)."},
                {""},
                {"The ONLY TERRAIN option can help you manipulate terrain arround your structures without affecting the structures.(see 'Only Terrain Modes' chapter) "},
                {""},
                {"The size and shape of your brush can be edited with the keycombination shown above."}
        };
        Paragraph.KeyInformation[] keys7 = {
                new Paragraph.KeyInformation(keybindings2, "Lower Terrain To Lowest Block"),
                new Paragraph.KeyInformation(keybindings3, "Raise Terrain To Highest Block"),
                new Paragraph.KeyInformation(keybindings4, "Increase Brush Size",true,false),
                new Paragraph.KeyInformation(keybindings5, "Decrease Brush Size", false, true)};

        Paragraph para7 = new Paragraph(startX, 50, paraWidth + (200 - startX), "64_level_adjust_mode", "mp4", keys7, "LEVEL Adjust Mode", level, mc.getResourceManager());



        paragraphs.add(para1);
        paragraphs.add(para2);
        paragraphs.add(para3);
        paragraphs.add(para4);
        paragraphs.add(para5);
        paragraphs.add(para6);
        paragraphs.add(para7);

    }

}
