package com.deadtiger.advcreation.handler;

import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.message.MessageUpdatePlayerSetting;
import com.deadtiger.advcreation.plugin.modded_classes.ModPlayerControllerMP;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
@Config(modid=Reference.MODID,name=Reference.MODID)
public class ConfigurationHandler
{





    @Config.Comment("General Configuration that I didn't know where to put")
    @Config.Name("General")
    public static GeneralConfig general = new GeneralConfig();
    @Config.Comment("Options specifying how the isometric camera works")
    @Config.Name("Isometric Camera")
    public static CameraConfig cameraConfig = new CameraConfig();
    @Config.Comment("Options for Advanced Creation tools")
    @Config.Name("Advanced Creation Tools")
    public static ToolConfig toolConfig = new ToolConfig();
    @Config.Comment("Options for working with absolute coordinates")
    @Config.Name("Absolute Coordinates")
    public static AbsoluteCoordConfig absCoordConfig = new AbsoluteCoordConfig();
    @Config.Comment("Options for issues with other mods")
    @Config.Name("Mod Compatibility")
    public static ModCompatibility modComp = new ModCompatibility();


    public static class GeneralConfig
    {

        private  final boolean MOVE_RELATIVE_TO_CAMERA_default = true;
        @Config.Comment({"If true then move buttons will move the player relative to the view of the camera"})
        @Config.Name("Move relative to camera view")
        public boolean MOVE_RELATIVE_TO_CAMERA = MOVE_RELATIVE_TO_CAMERA_default;
        //turn the GUI overlay on and off
        @Config.Comment({"Is GUI overlay visible (reload world)"})
        @Config.Name("Gui overlay visible")
        public boolean GUI_OVERLAY_VISIBLE = true;
        //if youre computer can handle it the mod will refresh the world each second
        @Config.Comment({"(heavy load)constant refreshing of terrain works better with cut-through feature"})
        @Config.Name("Constant terrain refresh")
        public  boolean CONSTANT_TERRAIN_REFRESH = false;
        @Config.Comment({"Is preview of other players on the server shown in your screen"})
        @Config.Name("Show other players preview")
        public  boolean SHOW_OTHER_PLAYERS_PREVIEW = true;
        @Config.Comment({"Allow the mod to log keypresses and clicks when using the mods features"})
        @Config.Name("Log your action")
        public  boolean LOG_ACTIONS = false;
        @Config.Comment({"Allow the server to save the logs of your actions on the server"})
        @Config.Name("Send logs to server")
        public  boolean SEND_LOGS_TO_SERVER = false;
        @Config.Comment({"The size of the text on the help screen"})
        @Config.RangeDouble(min = 20.0, max = 200.0)
        @Config.SlidingOption
        @Config.Name("Help screen text size")
        public  double HELP_TEXT_SIZE = 100.0;
        @Config.Comment({"How bright is the color of the selection/deletion highlighting"})
        @Config.RangeDouble(min = 0.0, max = 100.0)
        @Config.SlidingOption
        @Config.Name("Selection Highlight Brightness")
        public  double SELECTION_HIGHLIGHT_BRIGHTNESS = 75.0;
        @Config.Comment({"Disable the pop-up message that says: 'Click here for instruction on how to do things for each mode' when you first hover on the MainMode buttons"})
        @Config.Name("Disable helpscreen pop-up message")
        public  boolean HIDE_HELPSCREEN_MESSAGE = false;
        @Config.Comment({"Disable the pop-up message that says: 'The current tool is sinking your FPS. Click here to reduce the preview/blockCount limit to get better FPS.' when a tool causes FPS below 5"})
        @Config.Name("Disable low FPS pop-up message")
        public  boolean HIDE_LOW_FPS_MESSAGE = false;
        @Config.Comment({"Disable the pop-up message that says: 'Mod option 'GUI overlay visible' is set to false. Change it to true to use the Advanced Creation tools' when going into creative"})
        @Config.Name("Disable gui visible false pop-up message")
        public  boolean HIDE_GUI_OVERLAY_VISIBLE_OFF_MESSAGE = false;

        @Config.Comment({"Give the player infinite reach when in first person in creative"})
        @Config.Name("First-person infinite reach in creative")
        public  boolean FIRST_PERSON_INFINITE_REACH_IN_CREATIVE = false;
        @Config.Comment({"Enter Isometric view whenever you switch to creative"})
        @Config.Name("Enter CREATIVE in isometric")
        public  boolean ENTER_CREATIVE_IN_ISOMETRIC = true;

        @Config.Comment({"When disabled, tools will not show previews or execute. Allowing use of other mod's tools in isometric view"})
        @Config.Name("Adv. creation tools enabled")
        public  boolean TOOLS_ENABLED = true;
    }

    public static class CameraConfig
    {

        private final double X_ANGLE_MIN = -90.0;
        private final double X_ANGLE_MAX = 90.0;
        private final double X_ANGLE_default = 45.0;
        private final double Y_ANGLE_default = 0;
        private final double PAN_SPEED_default = 30.0;
        private final double ZOOM_SPEED_default = 15.0;
        @Config.Comment({"How fast the camera zooms based on mouse scrolling"})
        @Config.RangeDouble(min = 1.0, max = 100.0)
        @Config.SlidingOption
        @Config.Name("Zoom speed")
        public double ZOOM_SPEED = ZOOM_SPEED_default;
        @Config.Comment({"How fast the camera pans based on mouse movement"})
        @Config.RangeDouble(min = 1.0, max = 100.0)
        @Config.SlidingOption
        @Config.Name("Camera rotation speed")
        public double CAMERA_ROTATION_SPEED = PAN_SPEED_default;
        @Config.Comment({"Rotation of the camera arround Y axis"})
        @Config.RangeDouble(min = -180.0, max = 180.0)
        @Config.Name("Camera angle Y-axis")
        public double Y_angle = Y_ANGLE_default;
        @Config.Comment({"Rotation of the camera arround X axis"})
        @Config.RangeDouble(min = -180, max = 180)
        @Config.Name("Camera angle X-axis")
        public double X_angle = X_ANGLE_default;
        @Config.Comment({"Whether zooming in will move the camera focuspoint towards the block the cursor is pointing at."})
        @Config.Name("Zoom toward cursor")
        public boolean ZOOM_TOWARDS_CURSOR = true;

        @Config.Comment({"How high your camera focus point can go"})
        @Config.Name("Max camera height")
        public double MAX_CAMERA_HEIGHT = 213.0;

        @Config.Comment({"Invert vertical camera rotation to mouse movement in isometric view"})
        @Config.Name("Invert vertical camera rotation")
        public boolean INVERT_VERT_ROT_CAM = false;
        @Config.Comment({"Invert horizontal camera rotation to mouse movement in isometric view"})
        @Config.Name("Invert horizontal camera rotation")
        public boolean INVERT_HOR_ROT_CAM = false;

        public void add_X_angle(double add_X_angle)
        {
            if(ConfigurationHandler.cameraConfig.INVERT_VERT_ROT_CAM)
                add_X_angle = -add_X_angle;

            double new_X_angle = X_angle + add_X_angle;

            if(new_X_angle > X_ANGLE_MAX)
            {
                X_angle = X_ANGLE_MAX;
            }
            else if(new_X_angle < X_ANGLE_MIN)
            {
                X_angle = X_ANGLE_MIN;
            }
            else
            {
                X_angle = new_X_angle;
            }
        }

        public void add_Y_angle(double add_Y_angle)
        {
            if(ConfigurationHandler.cameraConfig.INVERT_HOR_ROT_CAM)
                add_Y_angle = -add_Y_angle;

            double new_Y_angle = Y_angle + add_Y_angle;


            if(Math.abs(new_Y_angle) > 360)
            {
                if(new_Y_angle > 0 )
                    Y_angle= (new_Y_angle - 360);
                else
                    Y_angle = (new_Y_angle + 360);
            }
            else
            {
                Y_angle = new_Y_angle;
            }
        }
    }

    public static class ToolConfig
    {

        private final double CIRCLE_MAX_RADIUS_default = 50.0;
        private double MAX_BLOCK_COUNT_default = 1 + 8 * ((int) (Math.pow(2, 8)));
        private final double  MAX_BLOCK_COUNT_min = MAX_BLOCK_COUNT_default/10.0;
        private final double  MAX_BLOCK_COUNT_max = MAX_BLOCK_COUNT_default*1000.0;

        private static final double PREVIEW_BLOCK_LIMIT_default = 10;

        @Config.Comment({"Maximum circle radius for the BUILD mode CIRCLE tool and EDIT mode PAINT, PLANT, DIG/RAISE, SMOOTH/SHARPEN, LEVEL tools (increase at your own risk, might cause lag if your PC can't handle it)"})
        @Config.Name("Max Circle radius")
        @Config.RangeDouble(min = 20.0,max = 1000.0)
        public double CIRCLE_MAX_RADIUS = CIRCLE_MAX_RADIUS_default;

        @Config.Comment({"Maximum amount of blocks that can be placed at once for the BUILD mode PULL and FILLGAP tools and EDIT mode PAINTBUCKET tool (increase at your own risk, might cause lag if your PC can't handle it)"})
        @Config.Name("Max placed/deleted blocks")
        @Config.RangeDouble(min = 200.0,max = 2049000.0)
        public double MAX_BLOCK_COUNT = MAX_BLOCK_COUNT_default;


        @Config.Comment({"Showing previews of big templates can affect performance. By reducing this value you will see less of a big template when trying to place it but your FPS should be better."})
        @Config.Name("Big template preview block limit")
        @Config.RangeDouble(min = 1, max = 50)
        public double PREVIEW_BLOCK_LIMIT  = PREVIEW_BLOCK_LIMIT_default;

        @Config.Comment({"In EDIT mode the brush size is changed with alt+scroll (default). This option will make the brush size change faster as you scroll faster. This should help you get to the desired brush size faster. Disable this if it annoys you"})
        @Config.Name("Use adaptive brush size changes")
        public boolean ADAPTIVE_BRUSH_SIZE_CHANGES = true;

        @Config.Comment({"LINE & CURVE tool allow you to select a point within a block. Enable this to always snap to the center of each block."})
        @Config.Name("Snap to center of block")
        public  boolean ALWAYS_SNAP_TO_BLOCK_CENTER = false;

        @Config.Comment({"Whether to show the top and bottom layers of a big template (disable when lagging because of big template previews)"})
        @Config.Name("Show top and bottom of big templates")
        public  boolean PREVIEW_TOPBOTTOM_BIG_TEMPLATE = true;

    }

    public static class AbsoluteCoordConfig
    {

        @Config.Comment({"Show the absolute coordinates of where the mouse is pointing above the cursor"})
        @Config.Name("Show absolute mouse coordinates")
        public boolean SHOW_ABS_MOUSE_COORD = false;
        @Config.Comment({"Show the absolute coordinates of the position of the camera focus point cross"})
        @Config.Name("Show abs. camera focus point coord.")
        public boolean SHOW_ABS_CAMERA_FOCUS_COORD = false;

        @Config.Comment({"Show the absolute coordinates of the selected start/middle/end positions of the current tool in the lower-right side of the HUD"})
        @Config.Name("Show absolute positions on HUD")
        public boolean SHOW_ABS_POS_ON_GUI = true;
    }


    public static class ModCompatibility
    {

        @Config.Comment({"Llibrary also wants to edit the camera distance if a mod using Llibrary has camera issues, try setting this to TRUE"})
        @Config.Name("Use Llibrary camera distance")
        public boolean USE_LLIBRARY_CAMERA_DISTANCE = false;
    }


    @SubscribeEvent
    public static void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.getModID().equals(Reference.MODID))
        {
            System.out.println("update the configuration of advanced creation");
            ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
            GuiOverlayManager.setGuiOverlayVisible(general.GUI_OVERLAY_VISIBLE);
            GuiOverlayManager.setDisplayPosCoord(absCoordConfig.SHOW_ABS_POS_ON_GUI);
            GuiOverlayManager.setPosVisibility(absCoordConfig.SHOW_ABS_POS_ON_GUI);
            GuiOverlayManager.setSetAbsoluteCoordButtonEnable(absCoordConfig.SHOW_ABS_POS_ON_GUI);
            ModPlayerControllerMP.giveCustomReachDistance = ConfigurationHandler.general.FIRST_PERSON_INFINITE_REACH_IN_CREATIVE;
            NetworkHandler.sendPlayerSettingsUpdateToServer(new MessageUpdatePlayerSetting(!IsometricCamera.isPlayerInIsometricPerspective(),!ConfigurationHandler.general.TOOLS_ENABLED, Minecraft.getMinecraft().player.getName()));

        }
    }


//    public static void delete_any_old_configfiles()
//    {
//        System.out.println("CHECKING CONFIGFILE for old versions");
//        String[] alpha1_5_fields = {
//                "B:CONSTANT_TERRAIN_REFRESH",
//        "B:GUI_OVERLAY_VISIBLE",
//        "B:LOG_ACTIONS",
//        "B:MOVE_RELATIVE_TO_CAMERA",
//        "D:PAN_SPEED",
//        "B:SEND_LOGS_TO_SERVER",
//        "B:SHOW_OTHER_PLAYERS_PREVIEW",
//        "D:X_angle",
//        "D:Y_angle"} ;
//
//        HashMap<String,Boolean> fieldsArePresentMap = new HashMap<>();
//
//        //initialise the map
//        for (String field: alpha1_5_fields)
//        {
//            fieldsArePresentMap.put(field,false);
//        }
//        File mcDataFolder = Minecraft.getMinecraft().mcDataDir;
//        File configFolder = new File(mcDataFolder, "config");
//        File configFile = new File( configFolder , Reference.MODID + ".cfg");
//
//
//        boolean configCorrect = true;
//        if(configFile.exists())
//        {
//            System.out.println("config file exists");
//            Scanner reader = null;
//            try
//            {
//                reader = new Scanner(configFile);
//
//                //check if the current configfile has all the necessary fields
//                while (reader.hasNextLine())
//                {
//                    String data = reader.nextLine();
//                    //check if this line contains one of the fields
//                    for (String field: alpha1_5_fields)
//                    {
//                        if(data.contains(field))
//                        {
//                            System.out.println("field " + field + " present");
//                            fieldsArePresentMap.put(field,true);
//                        }
//                    }
//                }
//
//                // if only one of the fields is not found in the previous loop delete the file
//                for (boolean isFieldPresent: fieldsArePresentMap.values())
//                {
//                    if(!isFieldPresent)
//                    {
//                        configCorrect = false;
//                        break;
//                    }
//
//                }
//                reader.close();
//
//
//
//            }
//            catch (FileNotFoundException e)
//            {
//                e.printStackTrace();
//            }
//
//        }
//        if(!configCorrect)
//        {
//            configFile.setWritable(true);
//            if(configFile.delete())
//                System.out.println("succesfully deleted old config file");
//            else
//                System.out.println("unable to delete old config file");
//            System.out.println("configfile didn't have all the necessary fields so it has been remade");
//        }
//    }



}
