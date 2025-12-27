package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;

public class AbsoluteCoordPage extends AbstractPage
{


    public AbsoluteCoordPage(String title) {
        super(title);

        String[][] general = {{"As of Alpha2.0, there is the ability to enter absolute coordinates into a window to perform " +
                "nearly all available functions in the Advanced Creation tool set."},
                { "The only functions that do not support this due to their nature are: "},
                { "1.", "BUILD MODE: PULL tool"},
                { "2.", "All EDIT MODE tools"},
                };

//        Paragraph para1 = new Paragraph(startX,50,paraWidth + (offsetX-startX),"trailer_ingame", "mp4", keys,"Overview",general, mc.getResourceManager());
//        para1.setFreezeEndGif(true);

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (offsetX-startX),null, "mp4",null,"Intro Absolute Coordinates",general, mc.getResourceManager());

        //        ###############################################################################################
        String[][] absoluteCoordScreen = {{ "When using a tool that supports absolute coordinates, " +
                "you can press the 'SET ABS CRD' HUD button on the bottom right of the screen or press the hotkey shown above."},
                {"This opens up the 'set absolute coordinates' screen where you can input X,Y,Z coordinates of the start,middle and end positions that need to be specified to complete the action of the current tool."},
                {"If the current tool does not require a middle and/or end position it will be disabled."},
                {""},
                {"Initialy all fields will be filled in with the previous coordinate the cursor was pointing or the coordinates that you had already selected. " },
                {"Whenever you edit a coordinate, that position will be FIXED as well as any prerequisite position that is required for that tool. " +
                        "This is indicated by the lock icon next to each position. Clicking this icon will toggle that position and prerequisite positions between FIXED and UNFIXED."},
                {""},
                {"After entering the desired coordinates most tools will allow you to select one of the following buttons:"},
                {"PLACE: ","PLACE blocks and finish the current tool's action."},
                {"DELETE: ","DELETE blocks and finish the current tool's action. "},
                {"PLACE preview: ","Show a preview of the blocks that would be PLACED."},
                {"DELETE preview: ","Show a preview of the blocks that would be DELETED."},
                {"CANCEL: ", "Cancel any current FIXED positions."},
                {"RETURN: ","Close the menu without making any changes."},
                {""},
                {"If you choose to preview there are other actions you can do outside this screen as explained in the following paragraphs."}
        };

        KeyBinding[] keybindings2 = {Keybindings.OPEN_ABS_COORD_SCREEN.getKeybind()};

        Paragraph.KeyInformation[] keys2= {
                new Paragraph.KeyInformation(keybindings2,"Open Set Absolute Coordinate screen")};

        Paragraph para2= new Paragraph(startX,50,paraWidth + (200-startX),"57_abs_coord_screen", "mp4", keys2,"Set Absolute Coordinates Screen",absoluteCoordScreen, mc.getResourceManager());

        //        ###############################################################################################
        String[][] supportFeatures = {{ "To make working with absolute coordinates easier Advanced Creation supplies the following features:"},
                {"1. HUD positions display","In the bottom right corner of the screen the current selected absolute coordinates are displayed for the current tool."},
                {"2. HUD mouse coordinates","Shows the coordinates of were the cursor will place a block (white) and where it would delete a block (red).(OFF by default see Mod Options) "},
                {"3. HUD camera cross coordinates","Shows the coordinates of block that the camera focus point cross is currently in.(OFF by default see Mod Options) "}
        };

        Paragraph para3= new Paragraph(startX,50,paraWidth + (200-startX),"58_supporting_features_abs", "mp4", null,"Supporting Features",supportFeatures, mc.getResourceManager());

        //        ###############################################################################################
        String[][] combinedOperations = {{ "The Absolute coordinate system is made to be compatible with the standard point and click system of working."},
                {""},
                {"The GIF above shows one example of this. Here the RECTANGLE tool is used to select the first position with the mouse (start) " +
                        "and the mouse is pointing at the next position (middle). When opening the 'Set Absolute Screen' screen in this situation with the hotkey, " +
                        "the menu will show that the start is already fixed in the position that was selected and the middle position is also filled in with the coordinates of " +
                        "where the cursor was pointing but hasn't been fixed yet. You can just fix that position and press the PLACE/DELETE preview. " +
                        "You can then use the mouse to select the last position to complete the action or" +
                        " go back into the 'set absolute coordinate' screen to specify the last position." },
                {""},
                {"When the all positions are FIXED you can select PLACE/DELETE preview and get a fixed preview of the whole action."},
                {"You can then right or left click to complete the action or cancel, dependent on whether you are DELETING or PLACING, as seen in the above hotkeys."},
                {""},
                {"This shows the most interesting ways in which you can combine ingame selection with absolute coordinates."}
        };

        KeyBinding[] keybindings3 = {mc.gameSettings.keyBindAttack};
        KeyBinding[] keybindings4 = {mc.gameSettings.keyBindUseItem};

        Paragraph.KeyInformation[] keys3= {
                new Paragraph.KeyInformation(keybindings4,"PLACE in PLACE preview"),
                new Paragraph.KeyInformation(keybindings3,"CANCEL in PLACE preview"),
                new Paragraph.KeyInformation(keybindings3,"DELETE in DELETE preview"),
                new Paragraph.KeyInformation(keybindings4,"CANCEL in DELETE preview"),
        };

        Paragraph para4= new Paragraph(startX,50,paraWidth + (200-startX),"59_combined_mouse_abs", "mp4", keys3,"Combining Ingame Selection With Abs. Coord.",combinedOperations, mc.getResourceManager());


        paragraphs.add(para1);
        paragraphs.add(para2);
        paragraphs.add(para3);
        paragraphs.add(para4);

    }

}
