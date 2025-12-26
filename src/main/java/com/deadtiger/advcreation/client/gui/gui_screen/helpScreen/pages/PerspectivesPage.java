package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages;

import com.deadtiger.advcreation.client.input.Keybindings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class PerspectivesPage extends AbstractPage
{


    public PerspectivesPage(String title) {
        super(title);

        //        ###############################################################################################
        String[][] intro = {
                {"The Isometric View is a new perspective that is added as the 4th perspective when pressing F5 (or your own defined hotkey seen above)."},
                {"For now it is only available in CREATIVE as it does not work with the limited resources and knowledge you should have in SURVIVAL."},
                {"To make accessing the Isometric View easier, Advanced Creation also adds buttons to the Ingame menu to toggle both your perspective and your gamemode(see image above)."},
                {""},
                { "Since Alpha2.0 the organisation of perspectives has changed and the old Vanilla perspectives are available again."},
                {"In the SURVIVAL gamemode there is no longer a Isometric perspective, just the 3 Vanilla perspective."},
                {"Everytime you enter CREATIVE mode the perspective will jump to the isometric perspective."}};
        KeyBinding[] keybindings1 = {mc.gameSettings.keyBindTogglePerspective};

        Paragraph.KeyInformation[] keys1= {
                new Paragraph.KeyInformation(keybindings1,"Change Perspective")};

        Paragraph para1 = new Paragraph(startX,50,paraWidth + (200-startX),"new_ingame_menu_buttons", "png", keys1,"Intro Isometric View",intro, mc.getResourceManager());

        //        ###############################################################################################
        String[][] rotate = {{"You can freely orient the camera around the focus point."},
                {""},
                {"Zooming in will zoom towards the block you are pointing at by default."},
                {"But you can change this behavior by setting ZOOM_TOWARDS_CURSOR to false in the Mod Config of Advanced Creation."},
                {"When this config is false, zooming in will only zoom in towards your current camera focus point."},
                {""},
                {"Additionally you can adjust the speed of rotation and zooming with the CAMERA_ROTATION_SPEED and ZOOM_SPEED settings in the Mod Config."}};

        KeyBinding[] keybindings3 = {Keybindings.ROT_CAMERA.getKeybind()};
        KeyBinding[] keybindings4 = {Keybindings.ZOOM_IN.getKeybind()};
        KeyBinding[] keybindings5 = {Keybindings.ZOOM_OUT.getKeybind()};
        Paragraph.KeyInformation[] keys2 = {
                new Paragraph.KeyInformation(keybindings3, "+ MOVE Rotate"),
                new Paragraph.KeyInformation(keybindings4, "Zoom In"),
                new Paragraph.KeyInformation(keybindings5, "Zoom Out")};

//        Paragraph para1 = new Paragraph(startX, 50, paraWidth + (200 - startX), "textures/gui/gifs/orientcamera.gif", keys1, "Orient Camera", rotate, mc.getResourceManager());
        Paragraph para2 = new Paragraph(startX, 50, paraWidth + (200 - startX), "2_move_camera", "mp4", keys2, "Orient Camera", rotate, mc.getResourceManager());

        //        ###############################################################################################
        String[][] cameraHeightFocuspoint = {{"You can move the focus point of the camera up and down."},
                {""},
                {"You can also do this by dragging the gray tab on the vertical bar on the left."},
                {""},
                {"The blue tab with the grass block on the left-side vertical bar indicates the ground height below your camera focus point."},
                {"Clicking this tab will put the camera focus point in ground follow mode allowing you to move over the terrain with the camera focus point always above ground."},
                {"Changing the height of your camera focus point manually will automatically deactivate this mode."},
                {""},
                {TextFormatting.AQUA + "If hotkeys are not working it means other functions use that key. Resolve these conflicts in the standard CONTROLS menu"}
        };
        KeyBinding[] keybindings6 = {mc.gameSettings.keyBindJump};
        KeyBinding[] keybindings7 = {mc.gameSettings.keyBindSneak};
        Paragraph.KeyInformation[] keys3 = {
                new Paragraph.KeyInformation(keybindings6, "Move Up"),
                new Paragraph.KeyInformation(keybindings7, "Move Down")};

//        Paragraph para2 = new Paragraph(startX, 50, paraWidth + (200 - startX), "textures/gui/gifs/heightcamera.gif", keys2, "Changing Camera Focus Point Height", cameraHeightFocuspoint, mc.getResourceManager());
        Paragraph para3 = new Paragraph(startX, 50, paraWidth + (200 - startX), "3_height_camera", "mp4", keys3, "Changing Camera Focus Point Height", cameraHeightFocuspoint, mc.getResourceManager());



        //        ###############################################################################################
        String[][] perspective1 = {{ "In the isometric perspective the default selection mode is unable to select plant life."},
                {"This includes grass, leaves, flowers and other bushlike plants. This is made so it is easier to build on grass fields and under trees."},
                {""},
                {"If you want to do some editing of plant life you can toggle the selection of plants with the ingame menu button next to the perspective button or press the hotkey shown above."}
        };

        KeyBinding[] keybindings2 = {Keybindings.TOGGLE_IGNORE_PLANTS.getKeybind()};

        Paragraph.KeyInformation[] keys4= {
                new Paragraph.KeyInformation(keybindings2,"Toggle Ignore Plants Selection")};

        Paragraph para4= new Paragraph(startX,50,paraWidth + (200-startX),"60_ignore_plants", "mp4", keys4,"Selection With Ignoring Plants",perspective1, mc.getResourceManager());

        //        ###############################################################################################
        String[][] removeWater = {{ "In the isometric perspective the default selection mode is unable to select liquids."},
                {"This includes still and streaming water or lava. This is made so it is easier to build underwater."},
                {""},
                {"It can be useful to turn this on so you can build on top of water or remove water with the PAINTBUCKET tool."},
                {"You can toggle the selection of liquids with the ingame menu button next to the perspective button or press the hotkey shown above."}
        };
        KeyBinding[] keybindings8 = {Keybindings.TOGGLE_IGNORE_LIQUIDS.getKeybind()};

        Paragraph.KeyInformation[] keys5= {
                new Paragraph.KeyInformation(keybindings8,"Toggle Ignore Liquids Selection")};

        Paragraph para5 = new Paragraph(startX, 50, paraWidth + (200 - startX), "70_promove7_remove_water", "mp4", keys5, "Selection With Ignoring Liquids", removeWater, mc.getResourceManager());


        paragraphs.add(para1);
        paragraphs.add(para2);
        paragraphs.add(para3);
        paragraphs.add(para4);
        paragraphs.add(para5);
    }

}
