package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class ToolModesPage extends AbstractPage
{


    public ToolModesPage(String title) {
        super(title);

        //        ###############################################################################################
        String[][] intro = {{ "Tool modes are your primary tools for creating new structures they allow you to quickly create " +
                "complex structures with only a few clicks."},
                {""},
                {"As seen in the Build Mode intro page you can select a toolmode in the QUICK-SELECT menu or click the HUD overlay button on the right."},
                {"You can also toggle the tool mode with the hotkey above."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings1 = {Keybindings.CHANGE_TOOL_MODE.getKeybind()};

        Paragraph.KeyInformation[] keys1= {
                new Paragraph.KeyInformation(keybindings1,"Toggle Tool Mode")};

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (200-startX),null, "mp4", keys1,"Intro Tool Modes",intro, mc.getResourceManager());

        //        ###############################################################################################
        String[][] perspective1 = {{ "The SINGLE tool mode is your normal single block place/delete method."}
        };
        KeyBinding[] keybindings2 = {mc.gameSettings.keyBindAttack};
        KeyBinding[] keybindings3 = {mc.gameSettings.keyBindUseItem};
        KeyBinding[] keybindings8 = {Keybindings.ROTATE_RIGHT.getKeybind()};
        Paragraph.KeyInformation[] keys2= {
                new Paragraph.KeyInformation(keybindings2,"DELETE block"),
                new Paragraph.KeyInformation(keybindings3,"PLACE block"),
                new Paragraph.KeyInformation(keybindings8,"Rotate block")};

        Paragraph para2= new Paragraph(startX,50,paraWidth + (200-startX),"11_single", "mp4", keys2,"SINGLE Tool",perspective1, mc.getResourceManager());

        //        ###############################################################################################
        String[][] perspective2 = {{ "The Line tool mode allows you to place/delete a nice line with 2 clicks."}
        };
        Paragraph.KeyInformation[] keys3= {
                new Paragraph.KeyInformation(keybindings2,"2 CLICKS to DELETE line"),
                new Paragraph.KeyInformation(keybindings3,"2 CLICKS to PLACE line"),
                new Paragraph.KeyInformation(keybindings8,"Rotate block")};

        Paragraph para3= new Paragraph(startX,50,paraWidth + (200-startX),"12_line", "mp4", keys3,"LINE Tool",perspective2, mc.getResourceManager());

        //        ###############################################################################################
        String[][] firstPerson = {{ "The RECTANGLE tool allows you to place/delete a rectangle of any shape with 3 clicks."},
                { "With 3 clicks you define the bottom side and right side of the rectangle, the top and left side are copies of these."}
        };
        Paragraph.KeyInformation[] keys4= {
                new Paragraph.KeyInformation(keybindings2,"3 CLICKS to DELETE rectangle"),
                new Paragraph.KeyInformation(keybindings3,"3 CLICKS to PLACE rectangle"),
                new Paragraph.KeyInformation(keybindings8,"Rotate block")};

        Paragraph para4= new Paragraph(startX,50,paraWidth + (200-startX),"13_rectangle", "mp4", keys4,"RECTANGLE Tool",firstPerson, mc.getResourceManager());

        //        ###############################################################################################
        String[][] curve = {{ "The CURVE tool allows you to place/delete a curve with 3 clicks."},
                { "With 3 clicks you define the start, end and curve point of the curve line."},
                {"The curve is created with the qaudratic bezier algorithm. If you are having trouble creating the curve you want you should look up qaudratic bezier curve. Wikipedia has a nice animation on how it operates."}
        };
        Paragraph.KeyInformation[] keys5= {
                new Paragraph.KeyInformation(keybindings2,"3 CLICKS to DELETE curve"),
                new Paragraph.KeyInformation(keybindings3,"3 CLICKS to PLACE curve"),
                new Paragraph.KeyInformation(keybindings8,"Rotate block")};

        Paragraph para5= new Paragraph(startX,50,paraWidth + (200-startX),"14_curve", "mp4", keys5,"CURVE Tool",curve, mc.getResourceManager());

        //        ###############################################################################################
        String[][] circle = {{ "The CIRCLE tool allows you to place/delete a circle with 2 clicks."},
                { "With 2 clicks you define the center and radius of a circle."},
                { "For now, circles can only be made in an orthogonal plane, see Direction modes pages."},
                { "The circle can be filled or empty, see Fill modes pages."}
        };
        Paragraph.KeyInformation[] keys6= {
                new Paragraph.KeyInformation(keybindings2,"2 CLICKS to DELETE circle"),
                new Paragraph.KeyInformation(keybindings3,"2 CLICKS to PLACE circle"),
                new Paragraph.KeyInformation(keybindings8,"Rotate block")};

        Paragraph para6= new Paragraph(startX,50,paraWidth + (200-startX),"15_circle", "mp4", keys6,"CIRCLE Tool",circle, mc.getResourceManager());

        //        ###############################################################################################
        String[][] pull = {{ "The PULL tool allows you to pull an existing shape of the same material in or out with 2 clicks"},
                { "Click 1 selects the area you want to pull, click 2 stretched the original surface and places/deletes new blocks of the original surface material."},
                { "The pull mode will always pull in the direction of the original surface."}
        };
        Paragraph.KeyInformation[] keys7= {
                new Paragraph.KeyInformation(keybindings2,"2 CLICKS to DELETE a pulled volume"),
                new Paragraph.KeyInformation(keybindings3,"2 CLICKS to PLACE a pulled volume")};

        Paragraph para7= new Paragraph(startX,50,paraWidth + (200-startX),"16_pull", "mp4", keys7,"PULL Tool",pull, mc.getResourceManager());

        //        ###############################################################################################
        String[][] copy = {{ "The COPY tool allows you to select an area and replicate is elsewhere with 3 clicks."},
                { "Click 1 selects the start of the copied area, click 2 selects the end of the copied area, click 3 places a new copy. "}
        };
        Paragraph.KeyInformation[] keys8= {
                new Paragraph.KeyInformation(keybindings2,"1st 2 CLICKS to SELECT area"),
                new Paragraph.KeyInformation(keybindings3,"as 3rd CLICK to PLACE copy")};

        Paragraph para8= new Paragraph(startX,50,paraWidth + (200-startX),"17_copy", "mp4", keys8,"COPY Tool",copy, mc.getResourceManager());

        //        ###############################################################################################
        String[][] move = {{ "The MOVE/DEL tool allows you to select an area and move it elsewhere or delete it with 3 clicks."},
                { "Click 1 selects the start of the moved area, click 2 selects the end of the moved area, click 3 moves/deletes the original area. "}
        };
        Paragraph.KeyInformation[] keys9= {
                new Paragraph.KeyInformation(keybindings2,"1st 2 CLICKS to SELECT area"),
                new Paragraph.KeyInformation(keybindings3,"as 3rd CLICK to MOVE area"),
                new Paragraph.KeyInformation(keybindings2,"as 3rd CLICK to DELETE area")};

        Paragraph para9= new Paragraph(startX,50,paraWidth + (200-startX),"18_move", "mp4", keys9,"MOVE/DEL Tool",move, mc.getResourceManager());

        //        ###############################################################################################
        String[][] adjust = {{ "After selecting an area with the COPY or MOVE/DEL tool you can adjust its position relative to the cursor"},
                {"You can move it sideways, up or down. This will help you place it in the right spot."},
                {""},
                {"When using this adjusting a white crosshair appears on the original position of the cursor. After adjusting, the cursor will jump back to this position and attach there to the area."}
        };
        KeyBinding[] keybindings4 = {Keybindings.ALTER_TOOL_MODE.getKeybind()};
        KeyBinding[] keybindings5 = {Keybindings.ALTER_TOOL_MODE.getKeybind(),Keybindings.ZOOM_IN.getKeybind()};
        KeyBinding[] keybindings6 = {Keybindings.ALTER_TOOL_MODE.getKeybind(),Keybindings.ZOOM_OUT.getKeybind()};
        KeyBinding[] keybindings7 = {Keybindings.MIRROR_ZY.getKeybind()};
        Paragraph.KeyInformation[] keys10= {
                new Paragraph.KeyInformation(keybindings4,"+ MOVE Adjust horizontally"),
                new Paragraph.KeyInformation(keybindings5,"Adjust up"),
                new Paragraph.KeyInformation(keybindings6,"Adjust down"),
                new Paragraph.KeyInformation(keybindings8,"Rotate area"),
                new Paragraph.KeyInformation(keybindings7,"Mirror area")};

        Paragraph para10= new Paragraph(startX,50,paraWidth + (200-startX),"19_adjust", "mp4", keys10,"Adjust COPY/MOVE area",adjust, mc.getResourceManager());

        //        ###############################################################################################
        String[][] fillGap = {{ "The FILLGAP tool allows you to fill up an enclosed area with one click"},
                {"Which area is filled depends on the current Direction mode, see the Direction modes pages."}
        };
        Paragraph.KeyInformation[] keys11= {
                new Paragraph.KeyInformation(keybindings3,"Fill up area")};

        Paragraph para11= new Paragraph(startX,50,paraWidth + (200-startX),"20_fillgap", "mp4", keys11,"FILLGAP Tool",fillGap, mc.getResourceManager());

        paragraphs.add(para1);
        paragraphs.add(para2);
        paragraphs.add(para3);
        paragraphs.add(para4);
        paragraphs.add(para5);
        paragraphs.add(para6);
        paragraphs.add(para7);
        paragraphs.add(para8);
        paragraphs.add(para9);
        paragraphs.add(para10);
        paragraphs.add(para11);
    }

}
