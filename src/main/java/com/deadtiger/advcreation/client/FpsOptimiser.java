package com.deadtiger.advcreation.client;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.tool_mode.*;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.*;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.template.TemplateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiMessageDialog;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FpsOptimiser
{
    public static int currFps = 30;
    public static double avgFps = 30;

    public static boolean lowFps = false;
    public static int  fpsMaxHistoryPoints = 15;
    public static int fpsHistoryPointToUpdate = 0;
    public static double  fpsHistory[] = new double[fpsMaxHistoryPoints];
    public static double lowFpsThreshold = 5;
    public static double acceptableFpsThreshold = 10;
    public static boolean optimisationInProgress = false;

    static
    {
        //prefill the fpsHistory with 30 fps as to not immediately trigger the low fps system
        for (int i = 0; i < fpsMaxHistoryPoints; i++)
        {
            fpsHistory[i] = 30;
        }
    }

    public static void updateFpsMeasure(int fps)
    {
        currFps = fps;
        fpsHistory[fpsHistoryPointToUpdate] = (double) fps;


        fpsHistoryPointToUpdate++;

        if(fpsHistoryPointToUpdate >= fpsMaxHistoryPoints)
        {
            avgFps = getAvgFps();
            checkIfLowFps(avgFps);
            fpsHistoryPointToUpdate = 0;
        }

    }

    public static boolean checkIfLowFps(double avgFps)
    {


        boolean islowFps = ( avgFps <= lowFpsThreshold);
        boolean isAcceptableFps = ( avgFps >= acceptableFpsThreshold);


        if(islowFps && checkIfOptimisationIsAvailable())
            GuiOverlayManager.showFpsOptimiseButton();
//        else if(isAcceptableFps)
//            GuiOverlayManager.hideFpsOptimiseButton();


        lowFps = islowFps;

        if(optimisationInProgress)
        {
            if(!isAcceptableFps)
                optimise();
            else
                endOptimisation();
        }

        return islowFps;
    }

    private static double getAvgFps()
    {
        double total = 0;
        for (int i = 0; i < fpsMaxHistoryPoints; i++)
        {
            total += fpsHistory[i];
        }
        return (total/fpsMaxHistoryPoints);
    }

    public static boolean checkIfOptimisationIsAvailable()
    {
        EnumMainMode mode = AdvCreation.getMode();
        boolean optimisationNecessary = false;
        if(mode == EnumMainMode.BUILD)
        {
            if((BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode) && BuildMode.TOOLMODE.currCopyTemplate != null && BuildMode.TOOLMODE.currCopyTemplate.tooBig == true)
                optimisationNecessary = true;
            else if(BuildMode.TOOLMODE instanceof FillGapToolMode || BuildMode.TOOLMODE instanceof PullToolMode)
                optimisationNecessary = true;
            else if(BuildMode.TOOLMODE instanceof CircleToolMode)
                optimisationNecessary = true;
        }
        else  if(mode == EnumMainMode.EDIT)
        {
            if(EditMode.ADJUST_MODE instanceof PaintBucketAdjustMode)
                optimisationNecessary = true;
            else if(EditMode.ADJUST_MODE instanceof PaintAdjustMode || EditMode.ADJUST_MODE instanceof PlantAdjustMode|| EditMode.ADJUST_MODE instanceof LevelAdjustMode
                    || EditMode.ADJUST_MODE instanceof DigRaiseAdjustMode
                    || EditMode.ADJUST_MODE instanceof SmoothAdjustMode)
                optimisationNecessary = true;
            else if(EditMode.ADJUST_MODE instanceof PaintAdjustMode || EditMode.ADJUST_MODE instanceof PlantAdjustMode)
                optimisationNecessary = true;
        }
        else  if(mode == EnumMainMode.PLACE)
        {
            int select_index = GuiTemplaceInventoryScreenFunctionality.getSelected_index();
            if (TemplateManager.TEMPLATES_LIST.size() > select_index && 0 <= select_index)
            {
                if(TemplateManager.TEMPLATES_LIST.get(select_index).tooBig)
                    optimisationNecessary = true;
            }
        }
        else  if(mode == EnumMainMode.CREATE)
        {

        }
        return optimisationNecessary;

    }

    public static void startOptimisation()
    {
        optimisationInProgress = true;
    }
    public static void optimise()
    {


    }

    public static String increaseFpsTooltip()
    {
        EnumMainMode mode = AdvCreation.getMode();
        String tooltip = "";
        if(mode == EnumMainMode.BUILD)
        {
            if((BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode) && BuildMode.TOOLMODE.currCopyTemplate != null)
                tooltip = "Improve FPS by reducing preview model";
            else if(BuildMode.TOOLMODE instanceof FillGapToolMode || BuildMode.TOOLMODE instanceof PullToolMode)
                tooltip = "Improve FPS by decreasing selected block limit";
            else if(BuildMode.TOOLMODE instanceof CircleToolMode)
                tooltip = "Improve FPS by decreasing max circle radius";
        }
        else  if(mode == EnumMainMode.EDIT)
        {
            if(EditMode.ADJUST_MODE instanceof PaintAdjustMode || EditMode.ADJUST_MODE instanceof PlantAdjustMode || EditMode.ADJUST_MODE instanceof LevelAdjustMode
                    || EditMode.ADJUST_MODE instanceof DigRaiseAdjustMode
                    || EditMode.ADJUST_MODE instanceof SmoothAdjustMode)
                tooltip = "Improve FPS by decreasing max circle radius";
            else if(EditMode.ADJUST_MODE instanceof PaintBucketAdjustMode)
                tooltip = "Improve FPS by decreasing selected block limit";

            if(EditMode.ADJUST_MODE instanceof PaintAdjustMode || EditMode.ADJUST_MODE instanceof PlantAdjustMode)
                tooltip = "Improve FPS by decreasing max circle radius";
            else if(EditMode.ADJUST_MODE instanceof PaintBucketAdjustMode)
                tooltip = "Improve FPS by decreasing selected block limit";


        }
        else  if(mode == EnumMainMode.PLACE)
        {
            tooltip = "Improve FPS by reducing preview model";
        }
        else  if(mode == EnumMainMode.CREATE)
        {

        }
        return tooltip;
    }

    public static String decreaseFpsTooltip()
    {
        EnumMainMode mode = AdvCreation.getMode();
        String tooltip = "";
        if(mode == EnumMainMode.BUILD)
        {
            if((BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode) && BuildMode.TOOLMODE.currCopyTemplate != null)
                tooltip = "Decrease FPS but enlarge preview model";
            else if(BuildMode.TOOLMODE instanceof FillGapToolMode || BuildMode.TOOLMODE instanceof PullToolMode)
                tooltip = "Decrease FPS but increase selected block limit";
            else if(BuildMode.TOOLMODE instanceof CircleToolMode)
                tooltip = "Decrease FPS but increase max circle radius";
        }
        else  if(mode == EnumMainMode.EDIT)
        {
            if(EditMode.ADJUST_MODE instanceof PaintAdjustMode || EditMode.ADJUST_MODE instanceof PlantAdjustMode || EditMode.ADJUST_MODE instanceof LevelAdjustMode
                    || EditMode.ADJUST_MODE instanceof DigRaiseAdjustMode
                    || EditMode.ADJUST_MODE instanceof SmoothAdjustMode)
                tooltip = "Decrease FPS but increase max circle radius";
            else if(EditMode.ADJUST_MODE instanceof PaintBucketAdjustMode)
                tooltip = "Decrease FPS but increase selected block limit";

            if(EditMode.ADJUST_MODE instanceof PaintAdjustMode || EditMode.ADJUST_MODE instanceof PlantAdjustMode)
                tooltip = "Decrease FPS but increase max circle radius";
            else if(EditMode.ADJUST_MODE instanceof PaintBucketAdjustMode)
                tooltip = "Decrease FPS but increase selected block limit";
        }
        else  if(mode == EnumMainMode.PLACE)
        {
            tooltip = "Decrease FPS but enlarge preview model";
        }
        else  if(mode == EnumMainMode.CREATE)
        {

        }
        return tooltip;
    }


    public static ArrayList<String> increaseFps()
    {
        EnumMainMode mode = AdvCreation.getMode();
        ArrayList<String> feedback = new ArrayList<>();
        if(mode == EnumMainMode.BUILD)
        {
            if(BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode)
            {
                if(manageBigTemplateDecreaseConfig(feedback))
//                if(decreaseConfig("Big template preview block limit", 1, feedback))
                    PlaceTemplateMode.FORCE_REDRAW = true;
            }
            else if(BuildMode.TOOLMODE instanceof FillGapToolMode || BuildMode.TOOLMODE instanceof PullToolMode)
                decreaseConfig("Max placed/deleted blocks", 1000, feedback);
            else if(BuildMode.TOOLMODE instanceof CircleToolMode)
                decreaseConfig("Max Circle radius", 5, feedback);
        }
        else  if(mode == EnumMainMode.EDIT)
        {
            if(EditMode.ADJUST_MODE instanceof PaintAdjustMode || EditMode.ADJUST_MODE instanceof PlantAdjustMode|| EditMode.ADJUST_MODE instanceof LevelAdjustMode
                    || EditMode.ADJUST_MODE instanceof DigRaiseAdjustMode
                    || EditMode.ADJUST_MODE instanceof SmoothAdjustMode)
                decreaseConfig("Max Circle radius", 5, feedback);
            else if(EditMode.ADJUST_MODE instanceof PaintBucketAdjustMode)
                decreaseConfig("Max placed/deleted blocks", 1000, feedback);

            if(EditMode.ADJUST_MODE instanceof PaintAdjustMode || EditMode.ADJUST_MODE instanceof PlantAdjustMode)
                decreaseConfig("Max Circle radius", 5, feedback);
            else if(EditMode.ADJUST_MODE instanceof PaintBucketAdjustMode)
                decreaseConfig("Max placed/deleted blocks", 1000, feedback);

        }
        else  if(mode == EnumMainMode.PLACE)
        {
            if(manageBigTemplateDecreaseConfig(feedback))
//            if(decreaseConfig("Big template preview block limit", 1, feedback))
                PlaceTemplateMode.FORCE_REDRAW = true;
        }
        else  if(mode == EnumMainMode.CREATE)
        {

        }
        return feedback;
    }

    public static ArrayList<String> decreaseFps()
    {
        EnumMainMode mode = AdvCreation.getMode();
        ArrayList<String> feedback = new ArrayList<>();
        if(mode == EnumMainMode.BUILD)
        {
            if(BuildMode.TOOLMODE instanceof CopyPasteToolMode || BuildMode.TOOLMODE instanceof MoveToolMode)
            {
                if(manageBigTemplateIncreaseConfig(feedback))
//                if(increaseConfig("Big template preview block limit", 1, feedback))
                    PlaceTemplateMode.FORCE_REDRAW = true;

            }
            else if(BuildMode.TOOLMODE instanceof FillGapToolMode || BuildMode.TOOLMODE instanceof PullToolMode)
                increaseConfig("Max placed/deleted blocks", 1000, feedback);
            else if(BuildMode.TOOLMODE instanceof CircleToolMode)
                increaseConfig("Max Circle radius", 5, feedback);
        }
        else  if(mode == EnumMainMode.EDIT)
        {
            if(EditMode.ADJUST_MODE instanceof PaintAdjustMode || EditMode.ADJUST_MODE instanceof PlantAdjustMode|| EditMode.ADJUST_MODE instanceof LevelAdjustMode
                    || EditMode.ADJUST_MODE instanceof DigRaiseAdjustMode
                    || EditMode.ADJUST_MODE instanceof SmoothAdjustMode)
                increaseConfig("Max Circle radius", 5, feedback);
            else if(EditMode.ADJUST_MODE instanceof PaintBucketAdjustMode)
                increaseConfig("Max placed/deleted blocks", 1000, feedback);

            if(EditMode.ADJUST_MODE instanceof PaintAdjustMode || EditMode.ADJUST_MODE instanceof PlantAdjustMode)
                increaseConfig("Max Circle radius", 5, feedback);
            else if(EditMode.ADJUST_MODE instanceof PaintBucketAdjustMode)
                increaseConfig("Max placed/deleted blocks", 1000, feedback);


        }
        else  if(mode == EnumMainMode.PLACE)
        {
            if(manageBigTemplateIncreaseConfig(feedback))
//            if(increaseConfig("Big template preview block limit", 1, feedback))
                PlaceTemplateMode.FORCE_REDRAW = true;
        }
        else  if(mode == EnumMainMode.CREATE)
        {

        }
        return feedback;
    }
    
    private static boolean  manageBigTemplateIncreaseConfig( ArrayList<String> feedback)
    {
        String configKey1PreviewBlockLimit = "Big template preview block limit";
        String configKey2TopBottom = "Show top and bottom of big templates";

        List<IConfigElement> configElements = collectConfigElements(ConfigManager.getModConfigClasses(Reference.MODID));
        IConfigElement chosenConfig1 = lookForConfig(configKey1PreviewBlockLimit,configElements);
        IConfigElement chosenConfig2 = lookForConfig(configKey2TopBottom,configElements);

        boolean optionWasChanged = false;

        if(chosenConfig2 != null && (chosenConfig2.getType() == ConfigGuiType.BOOLEAN) && chosenConfig1 != null && (chosenConfig1.getType() == ConfigGuiType.DOUBLE || chosenConfig1.getType() == ConfigGuiType.INTEGER))
        {
            boolean topBottomShown = (boolean) Boolean.parseBoolean((String) chosenConfig2.get());
            double oldValue = (double) Double.parseDouble((String)chosenConfig1.get());;

            if(oldValue >= 15 && !topBottomShown)
            {
                double min = (double) Double.parseDouble((String)chosenConfig1.getMinValue());
                double newValue = min;
                optionWasChanged = true;
                setConfigTo(chosenConfig1,newValue);
                feedback.add("Mod Option '" + configKey1PreviewBlockLimit + "' = " + ((int) newValue));
                boolean newTopBottom = true;
                chosenConfig2.set((boolean) newTopBottom);
                feedback.add("Mod Option '" + configKey2TopBottom + "' = " + newTopBottom);
            }
            else
                optionWasChanged = increaseConfig("Big template preview block limit", 1, feedback);

            if (optionWasChanged)
            {
                if (Loader.isModLoaded(Reference.MODID))
                {
                    ConfigChangedEvent event = new ConfigChangedEvent.OnConfigChangedEvent(Reference.MODID, null, true, false);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (!event.getResult().equals(Event.Result.DENY))
                        MinecraftForge.EVENT_BUS.post(new ConfigChangedEvent.PostConfigChangedEvent(Reference.MODID, null, true, false));
                }
            }
        }
        else
        {
            feedback.add("Mod Option '" + configKey1PreviewBlockLimit + "' & '" + configKey2TopBottom + "' not found");
        }


        return optionWasChanged;
    }

    private static boolean  manageBigTemplateDecreaseConfig( ArrayList<String> feedback)
    {
        String configKey1PreviewBlockLimit = "Big template preview block limit";
        String configKey2TopBottom = "Show top and bottom of big templates";

        List<IConfigElement> configElements = collectConfigElements(ConfigManager.getModConfigClasses(Reference.MODID));
        IConfigElement chosenConfig1 = lookForConfig(configKey1PreviewBlockLimit,configElements);
        IConfigElement chosenConfig2 = lookForConfig(configKey2TopBottom,configElements);

        boolean optionWasChanged = false;

        if(chosenConfig2 != null && (chosenConfig2.getType() == ConfigGuiType.BOOLEAN) && chosenConfig1 != null && (chosenConfig1.getType() == ConfigGuiType.DOUBLE || chosenConfig1.getType() == ConfigGuiType.INTEGER))
        {
            boolean topBottomShown = (boolean) Boolean.parseBoolean((String) chosenConfig2.get());
            double oldValue = (double) Double.parseDouble((String) chosenConfig1.get());


            double min = (double) Double.parseDouble((String) chosenConfig1.getMinValue());
            if(oldValue <= min && topBottomShown)
            {
                double newValue = 15;
                optionWasChanged = true;
                setConfigTo(chosenConfig1,newValue);
                feedback.add("Mod Option '" + configKey1PreviewBlockLimit + "' = " + ((int) newValue));
                boolean newTopBottom = false;
                chosenConfig2.set((boolean) newTopBottom);
                feedback.add("Mod Option '" + configKey2TopBottom + "' = " + newTopBottom);
            }
            else
                optionWasChanged = decreaseConfig("Big template preview block limit", 1, feedback);

        }
        else
        {
            feedback.add("Mod Option '" + configKey1PreviewBlockLimit + "' & '" + configKey2TopBottom + "' not found");
        }

        return optionWasChanged;
    }

    private static boolean increaseConfig(String configKey, double change, ArrayList<String> feedback)
    {
        boolean optionWasChanged = false;

        List<IConfigElement> configElements = collectConfigElements(ConfigManager.getModConfigClasses(Reference.MODID));
        IConfigElement chosenConfig = lookForConfig(configKey,configElements);

        if(chosenConfig != null && (chosenConfig.getType() == ConfigGuiType.DOUBLE || chosenConfig.getType() == ConfigGuiType.INTEGER))
        {
            double previousValue = (double) Double.parseDouble((String)chosenConfig.get());
            double newValue = previousValue + change;
            double max = (double) Double.parseDouble((String)chosenConfig.getMaxValue());
            if(newValue > max)
                newValue = max;

            if(previousValue == max)
            {
                feedback.add("Mod Option '" + configKey + "' = " + ((int) newValue + " (max)"));
                optionWasChanged = false;
            }
            else
            {
                optionWasChanged = true;
                setConfigTo(chosenConfig, newValue);
                feedback.add("Mod Option '" + configKey + "' = " + ((int) newValue));
            }

            if (optionWasChanged)
            {
                if (Loader.isModLoaded(Reference.MODID))
                {
                    ConfigChangedEvent event = new ConfigChangedEvent.OnConfigChangedEvent(Reference.MODID, null, true, false);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (!event.getResult().equals(Event.Result.DENY))
                        MinecraftForge.EVENT_BUS.post(new ConfigChangedEvent.PostConfigChangedEvent(Reference.MODID, null, true, false));
                }
            }
        }
        else
        {
            feedback.add("Mod Option '" + configKey + "' not found");
        }

        return optionWasChanged;
    }

    private static void setConfigTo(IConfigElement chosenConfig, double newValue)
    {
        if(chosenConfig.getType() == ConfigGuiType.INTEGER)
            chosenConfig.set((int) newValue);
        else if(chosenConfig.getType() == ConfigGuiType.DOUBLE)
            chosenConfig.set((double) newValue);
    }

    public static IConfigElement lookForConfig(String configKey , List<IConfigElement> configElements)
    {
        IConfigElement chosen = null;
        for (IConfigElement configElement: configElements)
        {
            if(configElement.getType() == ConfigGuiType.CONFIG_CATEGORY)
            {
                chosen = lookForConfig(configKey , configElement.getChildElements());
            }
            else
            {
                if(configElement.getName().equals(configKey))
                    return configElement;
            }
            if(chosen != null)
                break;

        }

        return chosen;
    }

    public static boolean decreaseConfig(String configKey, double change, ArrayList<String> feedback)
    {
        boolean optionWasChanged = false;
        List<IConfigElement> configElements = collectConfigElements(ConfigManager.getModConfigClasses(Reference.MODID));
        IConfigElement chosenConfig = lookForConfig(configKey,configElements);

        if((chosenConfig != null) && (chosenConfig.getType() == ConfigGuiType.DOUBLE || chosenConfig.getType() == ConfigGuiType.INTEGER))
        {
            double previousValue = (double) Double.parseDouble((String) chosenConfig.get());
            double newValue = previousValue - change;
            double min = (double) Double.parseDouble((String) chosenConfig.getMinValue());
            if(newValue < min)
                newValue = min;

            if(previousValue == min)
            {
                feedback.add("Mod Option '" + configKey + "' = " + ((int) newValue + " (min)"));
                optionWasChanged = false;
            }
            else
            {
                optionWasChanged = true;
                setConfigTo(chosenConfig, newValue);
                feedback.add("Mod Option '" + configKey + "' = " + ((int) newValue));
            }

            if (optionWasChanged)
            {
                if (Loader.isModLoaded(Reference.MODID))
                {
                    ConfigChangedEvent event = new ConfigChangedEvent.OnConfigChangedEvent(Reference.MODID, null, true, false);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (!event.getResult().equals(Event.Result.DENY))
                        MinecraftForge.EVENT_BUS.post(new ConfigChangedEvent.PostConfigChangedEvent(Reference.MODID, null, true, false));
                }
            }
        }
        else
        {
            feedback.add("Mod Option '" + configKey + "' not found");
        }



        return optionWasChanged;
    }

    public static void endOptimisation()
    {
        optimisationInProgress = true;
//        GuiOverlayManager.hideFpsOptimiseButton();
    }

    private static List<IConfigElement> collectConfigElements(Class<?>[] configClasses)
    {
        List<IConfigElement> toReturn;
        if(configClasses.length == 1)
        {
            toReturn = ConfigElement.from(configClasses[0]).getChildElements();
        }
        else
        {
            toReturn = new ArrayList<IConfigElement>();
            for(Class<?> clazz : configClasses)
            {
                toReturn.add(ConfigElement.from(clazz));
            }
        }
        toReturn.sort(Comparator.comparing(e -> I18n.format(e.getLanguageKey())));
        return toReturn;
    }
}
