package com.deadtiger.advcreation.logging;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.tool_mode.BaseToolMode;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.build_template.TemplateBuildingMode;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.network.message.MessageLogToServer;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.BaseAdjustMode;
import com.deadtiger.advcreation.template.TemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@SideOnly(Side.CLIENT)
public class Logging
{
    public static String FILENAME = null;
    public static boolean LOGGING_ACTIVE = false;
    public static File MOD_LOGS_DIR;
    public static File MOD_LOGS_PLAYER_DIR;

    public static LocalDateTime TIME;
    public static boolean INITIALISED_LOGGING = false;

    public static long PREV_LOGTIME;
    public static int LINE_COUNT = 0;
    public static int FILE_COUNT = 1;

    public Logging()
    {
        TIME = LocalDateTime.now(ZoneId.systemDefault());
        PREV_LOGTIME = System.currentTimeMillis();

        File mcDataDir = Minecraft.getMinecraft().gameDir;
        MOD_LOGS_DIR = new File(mcDataDir, "mod_logs");

    }

    public static boolean createLegendFile(File playerLogsDir)
    {
        File legendFile = new File(playerLogsDir, "legend_logs.txt");

        if (!legendFile.exists())
        {
            OutputStreamWriter outputstream = null;
            boolean flag2;

            try
            {
                outputstream = new FileWriter(legendFile);
                PrintWriter p = new PrintWriter(outputstream);

                p.println("The logs refer to a mode, tool or state with their index here is the legend for it: ");
                p.println("\nMainModeId");
                for (EnumMainMode mode : EnumMainMode.values())
                {
                    p.println(mode.name() + "; " + mode.index);
                }

                p.println("\nToolId");
                p.println("BuildMode ToolModes");
                for (BaseToolMode mode : BuildMode.TOOL_MODES)
                {
                    p.println(mode.toolModeName + "; " + mode.identificationIndex);
                }
                p.println("EditMode AdjustModes");
                for (BaseAdjustMode mode : EditMode.ADJUST_MODES)
                {
                    p.println(mode.toolModeName + "; " + mode.identificationIndex);
                }
                p.println("PLACE Mode settings");
                for (PlaceTemplateMode.EnumFoundationMode found : PlaceTemplateMode.EnumFoundationMode.values())
                {
                    for (PlaceTemplateMode.EnumAirMode air : PlaceTemplateMode.EnumAirMode.values())
                    {
                        for (PlaceTemplateMode.EnumWallMode wall : PlaceTemplateMode.EnumWallMode.values())
                        {
                            p.println(found.buttonText + ", " + air.buttonText + ", " + wall.buttonText + "; " + PlaceTemplateMode.IDENTIFICATION_INDICES.get(found).get(air).get(wall));

                        }
                    }
                }

                p.println("\nButtonid");
                p.println("RightClick; 1");
                p.println("LeftClick; 2");
                p.println("MiddleClick; 3");
                for (Keybindings key : Keybindings.values())
                {
                    p.println(key.keybinding.getDisplayName() + "; " + key.index);
                }
                p.println("Help; 24");
                p.println("NoFoundation; 25");
                p.println("OneLayerFoundation; 26");
                p.println("ToGroundFoundation; 27");
                p.println("NoAir ; 28");
                p.println("OnlyInsideAir; 29");
                p.println("AllAirButton; 30");
                p.println("NoWalls ; 31");
                p.println("NewWalls; 32");
                p.println("AlWalls; 33");
                p.println("FoundationMaterial; 34");
                p.println("InventoryBlock; 35");
                p.println("InventoryTemplate ; 36");
                p.println("Open report screen ; 37");

                p.println("\nToolStageId");
                p.println("CREATE Mode");
                for (TemplateBuildingMode mode : TemplateBuildingMode.values())
                {
                    p.println(mode.name() + "; " + mode.index);
                }

                flag2 = true;
            }
            catch (Throwable var13)
            {
                System.out.println(var13);
                flag2 = false;
            }
            finally
            {
                IOUtils.closeQuietly(outputstream);

            }
            return flag2;
        }

        return true;
    }

    public static boolean createLogfile(LocalDateTime time)
    {

        MOD_LOGS_PLAYER_DIR = new File(MOD_LOGS_DIR, "logs_" + Minecraft.getMinecraft().player.getName());

        if (!MOD_LOGS_DIR.exists())
        {
            if (!MOD_LOGS_DIR.mkdirs())
                return false;
        }
        if (!MOD_LOGS_PLAYER_DIR.exists())
        {
            if (!MOD_LOGS_PLAYER_DIR.mkdirs())
                return false;
        }

        if (MOD_LOGS_DIR.isDirectory())
        {
            if (!MOD_LOGS_PLAYER_DIR.isDirectory())
                return false;
        }
        FILENAME = FILE_COUNT + "_" + time.toString().replace('.', '-').replace(':', '-') + ".txt";
        File file2 = new File(MOD_LOGS_PLAYER_DIR, FILENAME);

        OutputStreamWriter outputstream = null;
        boolean flag2;
        try
        {
            outputstream = new FileWriter(file2);
            PrintWriter p = new PrintWriter(outputstream);
            p.println("Started logging at  " + time);
            p.println("SystemTime(ms); MainModeId; ToolId; ButtonId; ToolStageId; GuiOverlayClick;Comment");
            flag2 = true;
        }
        catch (Throwable var13)
        {
            System.out.println(var13);
            flag2 = false;
        }
        finally
        {
            IOUtils.closeQuietly(outputstream);
        }
        if (!flag2)
            return false;

        return createLegendFile(MOD_LOGS_PLAYER_DIR);
    }

    public static void initialiseLogging()
    {
        if (!INITIALISED_LOGGING)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.player != null)
            {
                LOGGING_ACTIVE = createLogfile(TIME);
                if (LOGGING_ACTIVE && ConfigurationHandler.general.LOG_ACTIONS)
                    System.out.println("start logging at time time " + TIME);

                INITIALISED_LOGGING = true;
            }

        }
    }

    public static void tickLogging()
    {
        if (LOGGING_ACTIVE && ConfigurationHandler.general.LOG_ACTIONS)
        {
            long time = System.currentTimeMillis();
            if ((time - PREV_LOGTIME) > 1000)
            {
                EnumMainMode mode = AdvCreation.getMode();
                if (mode.equals(EnumMainMode.BUILD))
                    Logging.logMouseClick(mode.index, BuildMode.TOOLMODE.identificationIndex, -1, BuildMode.RIGHT_CLICK_NUMBER, "");
                else if (mode.equals(EnumMainMode.EDIT))
                    Logging.logMouseClick(mode.index, BuildMode.TOOLMODE.identificationIndex, -1, BuildMode.RIGHT_CLICK_NUMBER, "");
                else if (mode.equals(EnumMainMode.PLACE))
                    Logging.logMouseClick(AdvCreation.getMode().index, PlaceTemplateMode.getCurrToolId(), -1, 1, "");
                else
                {
                    Logging.logMouseClick(AdvCreation.getMode().index, 1, -1, BuildTemplateMode.MODE.index, "");
                }
                PREV_LOGTIME = time;
            }

        }
    }

    public static boolean logMouseClick(int mainMode, int toolname, int mouseButton, int toolStage, String comment)
    {
        return logMouseClick(mainMode, toolname, mouseButton, toolStage, false, comment);
    }

    public static boolean logMouseClick(int mainMode, int toolname, int mouseButton, int toolStage, boolean guiOverlayClick, String comment)
    {
        if (ConfigurationHandler.general.LOG_ACTIONS)
        {
            initialiseLogging();
            if (LOGGING_ACTIVE)
            {
                if (LINE_COUNT > 10000)
                {
                    TIME = LocalDateTime.now(ZoneId.systemDefault());
                    FILE_COUNT++;
                    LINE_COUNT = 0;
                    createLogfile(TIME);
                }

                File file2 = new File(MOD_LOGS_PLAYER_DIR, FILENAME);
                OutputStream outputstream = null;
                boolean flag2;

                int guiOverlayClickIndex = 0;
                if (guiOverlayClick)
                    guiOverlayClickIndex = 1;

                if (file2.exists())
                {

                    FileWriter fw = null;
                    try
                    {
                        String filename = file2.getCanonicalPath();
                        fw = new FileWriter(filename, true); //the true will append the new data
                        PREV_LOGTIME = System.currentTimeMillis();
                        fw.write(PREV_LOGTIME + "; " + mainMode + "; " + toolname + "; " + mouseButton + "; " + toolStage + "; " + guiOverlayClickIndex + "; " + comment + "\n");//appends the string to the file

                    }
                    catch (IOException ioe)
                    {
                        System.err.println("IOException: " + ioe.getMessage());
                    }
                    finally
                    {
                        try
                        {
                            fw.close();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    LINE_COUNT++;
                    if (ConfigurationHandler.general.SEND_LOGS_TO_SERVER && mouseButton >= 0)
                        NetworkHandler.sendLogToServer(new MessageLogToServer(Minecraft.getMinecraft().player.getName(), mainMode, toolname, mouseButton, toolStage, guiOverlayClick, comment));

                    return true;
                }
            }
        }

        return false;
    }

    public static void logClickPlaceMode(String s, int mouseButton)
    {
        String comment = s + AdvCreation.getMode().name() + " with template None";
        int select_index = GuiTemplaceInventoryScreenFunctionality.selected_index;
        if (TemplateManager.TEMPLATES_LIST.size() > select_index && 0 <= select_index)
        {
            String filename = TemplateManager.FILENAME_LIST.get(select_index);
            comment = s + AdvCreation.getMode().name() + " with " + filename;
        }
        logMouseClick(AdvCreation.getMode().index, PlaceTemplateMode.getCurrToolId(), mouseButton, 1, comment);
    }

    public static void logClick(String s, String toolModeName, int rightClickNumber, boolean deleteMode, int identificationIndex, int i)
    {
        String comment = s + AdvCreation.getMode().name() + " in " + toolModeName + " at " + rightClickNumber + " with deletemode " + deleteMode;
        logMouseClick(AdvCreation.getMode().index, identificationIndex, i, rightClickNumber, comment);
    }

    public static void logClickCreateMode(String s, int i)
    {
        String comment = s + AdvCreation.getMode().name() + " at " + BuildTemplateMode.MODE.name();
        logMouseClick(AdvCreation.getMode().index, 1, i, BuildTemplateMode.MODE.index, comment);
    }
}
