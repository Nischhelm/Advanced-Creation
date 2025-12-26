package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class DirectionModesPage extends AbstractPage
{


    public DirectionModesPage(String title) {
        super(title);

        //        ###############################################################################################
        String[][] intro = {{ "Direction modes allow you to build structures in horizontal and vertical planes easily."},
                {""},
                {"As seen in the Build Mode intro page you can select a direction in the QUICK-SELECT menu or click the HUD overlay button on the right."},
                {"You can also toggle the direction mode with the hotkey above."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings1 = {Keybindings.CHANGE_DIR_SHAPE_MODE.getKeybind()};
        Paragraph.KeyInformation[] keys1= {
                new Paragraph.KeyInformation(keybindings1,"Toggle Direction Mode")};

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (200-startX),null, "mp4", keys1,"Intro Direction Modes",intro, mc.getResourceManager());

        //        ###############################################################################################
        String[][] free = {{ "The FREE direction is the normal mode where the selected position is exactly where the mouse is pointing."}
        };

        Paragraph para2= new Paragraph(startX,50,paraWidth + (200-startX),"21_free", "mp4", null,"FREE Direction",free, mc.getResourceManager());

        //        ###############################################################################################
        String[][] ZYplane = {{ "The XZ/XY/ZY plane directions restrict the selection to a horizontal XZ plane(blue) or vertical XY plane(red) or ZY plane(green)."},
                { "It usually only becomes active after the first click since the position of the plane is based on that first click."},
                { "This means this mode only works for multi-click tool modes such as: Line, Rectangle, Curve, Circle. The FillGap tool is the exception. "}
        };

        Paragraph para5= new Paragraph(startX,50,paraWidth + (200-startX),"27_planes", "mp4", null,"XZ/XY/ZY Plane Direction",ZYplane, mc.getResourceManager());

        //        ###############################################################################################
        String[][] auto = {{ "The AUTO direction mode will choose automatically between XZ,XY,ZY plane based on the orientation of the camera."},
        };

        Paragraph para6= new Paragraph(startX,50,paraWidth + (200-startX),"25_auto", "mp4", null,"AUTO Direction",auto, mc.getResourceManager());

        //        ###############################################################################################
        String[][] pull = {{ "The GROUND direction mode is special as it acts like the FREE mode but all blocks will trace the ground over which they would hang."},
                { "It usually only becomes active after the first click, allowing you to make shapes over an uneven landscape."},
                { "This means this mode only works for multi-click tool modes such as: Line, Rectangle, Curve, Circle. The FillGap tool is an exception as" +
                        " it will fill everything under the mouse selection. The Pull tool is also an exception as it will pull until the next block in the pull direction."}
        };

        Paragraph para7= new Paragraph(startX,50,paraWidth + (200-startX),"26_ground", "mp4", null,"GROUND Direction",pull, mc.getResourceManager());


        paragraphs.add(para1);
        paragraphs.add(para2);
        paragraphs.add(para5);
        paragraphs.add(para6);
        paragraphs.add(para7);

    }

}
