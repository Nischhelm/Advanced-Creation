package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class TerrainShapeModesPage extends AbstractPage
{


    public TerrainShapeModesPage(String title) {
        super(title);

        //        ###############################################################################################
        String[][] intro = {{ "Terrain Shape modes allow you to specify the height, strength and steepness  of the DIG/RAISE, SMOOTH/SHARPEN, LEVEL " +
                "adjust tool modes. It does not affect any other adjust tool modes."},
                {""},
                {"There are 8 Terrain Shape modes that you can choose from. These modes are numbered from 1 - 8 and it's name is derived from a geological feature " +
                        "going from nearly flat to very steep. These are your options: "},
                {"1.PLANE","For manipulating the terrain/surface into a flat slope"},
                {"2.RISE","For manipulating the terrain/surface into a gentle slope"},
                {"3.SLOPE","For manipulating the terrain/surface into a long medium slope"},
                {"4.HILL","For manipulating the terrain/surface into a medium slope"},
                {"5.VALLEY","For manipulating the terrain/surface into a short medium slope"},
                {"6.MOUNTAIN","For manipulating the terrain/surface into a steep slope"},
                {"7.CLIFF","For manipulating the terrain/surface into a intense slop"},
                {"8.STRAIGHT","For manipulating the terrain/surface into a straight edge"},
                {"Each of these modes has a different effect on each of the adjust tool modes. This effect is explored in the next paragraphs."},
                {""},
                {"You can select a Terrain Shape mode in the QUICK-SELECT menu or click the HUD overlay button on the right."},
                {"You can also toggle the Terrain Shape mode with the hotkey above."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings1 = {Keybindings.CHANGE_DIR_SHAPE_MODE.getKeybind()};
        Paragraph.KeyInformation[] keys1= {
                new Paragraph.KeyInformation(keybindings1,"Toggle Terrain Shape Mode")};

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (200-startX),null, "mp4", keys1,"Intro Terrain Shape Modes",intro, mc.getResourceManager());

        //        ###############################################################################################
        String[][] digRaise = {{ "In the DIG/RAISE adjust tool mode, the Terrain Shape mode both determines the depth/height and the steepness of the edges of the hole/pile that is created."},
                {""},
                {"The most important in this tool is the number of the Terrain Shape mode as it tells you the depth/height of the hole/pile, for example:"},
                {"1.PLANE creates a hole/pile of 1 block deep/high with a nearly flat edge."},
                {"4.HILL creates a hole/pile of 4 blocks deep/high with a medium slope as edge."},
                {"8.STRAIGHT create a hole/pile of 8 blocks deep/high with a straight edge"},
                {"The clip above shows the change in height of the hill when cycling through the Terrain Shape modes in RAISE mode of the DIG/RAISE tool."},
                {""},
                {"The purpose of these options is to give you more freedom in with what kind of hole/hill you want to creation."}
        };

        Paragraph para2= new Paragraph(startX,50,paraWidth + (200-startX),"65_terrain_shape_dig_raise", "mp4", null,"Effect on Dig/Raise tool",digRaise, mc.getResourceManager());

        //        ###############################################################################################
        String[][] smooth = {{ "In the SMOOTH/SHARPEN adjust tool mode, the Terrain Shape mode determines the strenght of the smoothing/sharpening action of the tool."},
                {""},
                {"The effect starts with a strong smoothening effect and a very weak sharpening effect in 1.PLANE since this is intended to turn a steep slope into a flat plane." +
                        "As the number of the Terrain Shape goes up the smoothening effect becomes weaker and the sharpening effect becomes stronger." +
                        "When you come to 8.STRAIGHT theres is no smoothening effect any more and the sharpening effect will attempt to turn the current slope into a straight edge."},
                {"The clip above shows an example of what the smoothening effect can do to a straight-edged mountain in the smooth mode of the SMOOTH/SHARPEN tool. The number next to each hill is number of the " +
                        "Terrain Shape mode used to smoothen that same hill. This should give you an initial idea of the strength of each option."},
                {""},
                {"The purpose of these options is to give you a tune-able strength when manipulating slopes and hills."}
        };

        Paragraph para5= new Paragraph(startX,50,paraWidth + (200-startX),"66_terrain_shape_smooth_sharpen", "mp4", null,"Effect on Smooth/Sharpen tool",smooth, mc.getResourceManager());

        //        ###############################################################################################
        String[][] level = {{ "In the LEVEL adjust tool mode, the Terrain Shape mode determines the steepness of the edges of the leveling action of the tool."},
                {""},
                {"The effect starts with only a small part of the selected area being lowered/raised to the desired level at 1.PLANE, the rest is a fairly flat slope towards the original terrain." +
                        "As the number of the Terrain Shape goes up the edge becomes steeper in accordance to its name. This also results in more of the area being brought to the desired level." +
                        "When you come to 8.STRAIGHT all of selected area is lowered/raised to the desired level. In this case, no attempt is made to make the new surface connect to original terrain height."},
                {"The clip above shows the change in steepness and raised surface when cycling through the Terrain Shape modes in the Highest Level Mode of the LEVEL tool."},
                {""},
                {"The purpose of these options is to allow you to level terrain while still keeping the desired slope that connects the new surface to the current landscape."}
        };
        Paragraph para6= new Paragraph(startX,50,paraWidth + (200-startX),"67_terrain_shape_level", "mp4", null,"Effect on Level tool",level, mc.getResourceManager());


        paragraphs.add(para1);
        paragraphs.add(para2);
        paragraphs.add(para5);
        paragraphs.add(para6);

    }

}
