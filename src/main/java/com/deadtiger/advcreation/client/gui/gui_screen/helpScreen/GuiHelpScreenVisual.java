package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.elements.GuiTextButton;
import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.pages.*;
import com.deadtiger.advcreation.client.gui.gui_screen.reportScreen.ReportScreen;
import com.deadtiger.advcreation.client.input.KeyInputHandler;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.report.Report;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GuiHelpScreenVisual extends GuiScreen {

    public static int keyScrollSpeed = 48*2;

    public boolean scrollUpMouseHeld = false;
    public boolean scrollDownMouseHeld = false;
    public long scrollRepeatDelay = 50;
    public long previousScrollTime = 0;

    public static class pageSelection
    {
        int pageIndex;
        int pageOffset;

        public pageSelection(int pageIndex)
        {
           this(pageIndex,0);
        }

        public pageSelection(int pageIndex, int pageOffset) {
            this.pageIndex = pageIndex;
            this.pageOffset = pageOffset;
        }
    }

    // size of the main gui window
    private int guiWidth = 212;
    private int guiHeight = 80;
    
    //size of the menu screen? is always a low resolution? because ... why not?
    private int guiScreenWidth = 480;
    private int guiScreenHeight = 270;

    private int sideBarStartX = 5;
    private int sideBarStartY = 50;
    
    //variables for scrolling througb the help info
    private int offsetY = 0; // only goes negative and added to the Y-coordinate of the text
    private boolean canScrollDown = false;
    private boolean canScrollUp = false;
    double totalHeight = 0;
    private boolean sideHovered = false;
    private int sideOffsetY = 0; // only goes negative and added to the Y-coordinate of the text
    private boolean sideCanScrollDown = false;
    private boolean sideCanScrollUp = false;
    double sideTotalHeight = 0;

    private GuiButton backButton;
    private static final int BACK_BUTTON_ID = 0;
    private GuiButton nextButton;
    private static final int NEXT_BUTTON_ID = 1;

    private HashMap<GuiTextButton,pageSelection> sideMenuButtons = new HashMap<>();
    private ArrayList<GuiTextButton> sideMenuButtonsList = new ArrayList<>();

    private net.minecraftforge.fml.client.config.GuiSlider textSizeSlider;
    private int oldSliderValue;

    private ArrayList<AbstractPage> pages = new ArrayList<>();
    private int currentPage = 0;
    private int newPage = 0;
    private int newOffset = 0;
    private boolean loadingPage = false;
    private boolean secondStageLoadingPage= false;
    private boolean offsetPage = false;

    private Color flashyColor;

    private GuiTextButton currentSideMenuItem;

    private HashMap<String,GuiTextButton> modes = new HashMap<>();

    private long lastClickTime = 0;
    private final long clickDelay = 100;

    private static boolean firstTimeOpeningHelp = true;

    public static double textSizeValue;

    public static Color firstRowColor = Color.WHITE;
    public static Color secondRowColor = new Color(150, 150, 150);
    public static Color thirdRowColor = new Color(100, 100, 100);
    public static Color fourthRowColor = new Color(73, 73, 73);
    public static Color[] rowColors = {firstRowColor,secondRowColor,thirdRowColor,fourthRowColor};


    @Override
    public void initGui() {
        buttonList.clear();
        sideMenuButtons.clear();
        sideMenuButtonsList.clear();
        pages.clear();


        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        int factor = scaledresolution.getScaleFactor();


        //back and next button
        backButton = new GuiButton(BACK_BUTTON_ID,5,0,135,20,"BACK");
        nextButton = new GuiButton(NEXT_BUTTON_ID,5,height-20,135,20,"NEXT");

        //sidebar elements
        sideBarStartX = 0;
        GuiTextButton overviewItem = new GuiTextButton(5,sideBarStartX,sideBarStartY,"Introduction",true,true);
        int newbuttonY = sideBarStartY + overviewItem.getHeight();

        GuiTextButton perspectivesItem = new GuiTextButton(6,sideBarStartX,newbuttonY,"Isometric View",true,true);
        newbuttonY += addNextMainItem(perspectivesItem, overviewItem);

        GuiTextButton generalControlsItem = new GuiTextButton(7,sideBarStartX,newbuttonY,"General Features",true,true);
        newbuttonY += addNextMainItem(generalControlsItem,perspectivesItem);

        GuiTextButton mainModeItem = new GuiTextButton(12,sideBarStartX,newbuttonY,"Main Modes",true,true);
        int newChildButtonY = newbuttonY + addNextMainItem(mainModeItem, generalControlsItem);

            GuiTextButton introMainModeItem = new GuiTextButton(13,sideBarStartX+5,newChildButtonY,"Intro Main Modes",false,true,secondRowColor,sideBarStartX);
            newChildButtonY += addFirstChildToParent(introMainModeItem, mainModeItem);

            GuiTextButton buildModeItem = new GuiTextButton(13,sideBarStartX+5,newChildButtonY,"Build Mode",false,true,secondRowColor,sideBarStartX);
            int newChildButtonY2 =  newChildButtonY + addChildWithChildren(buildModeItem, mainModeItem, introMainModeItem);

                GuiTextButton introBuildItem = new GuiTextButton(14,sideBarStartX+10,newChildButtonY2,"Intro Build Mode",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY2 += addFirstChildToParent(introBuildItem, buildModeItem);

                GuiTextButton toolModesItem = new GuiTextButton(15,sideBarStartX+10,newChildButtonY2,"Tool Modes",false,true,thirdRowColor,sideBarStartX);
                int newChildButtonY3 =  newChildButtonY2 + addChildWithChildren(toolModesItem, buildModeItem, introBuildItem);

                    GuiTextButton introToolModesItem = new GuiTextButton(16,sideBarStartX+15,newChildButtonY3,"Intro Tool Modes",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addFirstChildToParent(introToolModesItem, toolModesItem);

                    GuiTextButton SingleItem = new GuiTextButton(17,sideBarStartX+15,newChildButtonY3,"Single Tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(SingleItem, toolModesItem, introToolModesItem);

                    GuiTextButton lineItem = new GuiTextButton(18,sideBarStartX+15,newChildButtonY3,"Line Tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(lineItem, toolModesItem, SingleItem);

                    GuiTextButton rectangleItem = new GuiTextButton(19,sideBarStartX+15,newChildButtonY3,"Rectangle Tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(rectangleItem, toolModesItem, lineItem);

                    GuiTextButton curveItem = new GuiTextButton(20,sideBarStartX+15,newChildButtonY3,"Curve Tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(curveItem, toolModesItem, rectangleItem);

                    GuiTextButton circleItem = new GuiTextButton(21,sideBarStartX+15,newChildButtonY3,"Circle Tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(circleItem, toolModesItem, curveItem);

                    GuiTextButton pullItem = new GuiTextButton(22,sideBarStartX+15,newChildButtonY3,"Pull Tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(pullItem, toolModesItem, circleItem);

                    GuiTextButton copyItem = new GuiTextButton(23,sideBarStartX+15,newChildButtonY3,"Copy Tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(copyItem, toolModesItem, pullItem);

                    GuiTextButton moveDelItem = new GuiTextButton(24,sideBarStartX+15,newChildButtonY3,"Move/Del Tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(moveDelItem, toolModesItem, copyItem);

                    GuiTextButton adjustItem = new GuiTextButton(24,sideBarStartX+15,newChildButtonY3,"Adjust Copy/Move area",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(adjustItem, toolModesItem, moveDelItem);

                    GuiTextButton fillGapItem = new GuiTextButton(25,sideBarStartX+15,newChildButtonY3,"FillGap Tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(fillGapItem, toolModesItem, adjustItem);

                newChildButtonY2 += toolModesItem.getTotalHeight();
                GuiTextButton directionModesItem = new GuiTextButton(26,sideBarStartX+10,newChildButtonY2,"Direction Modes",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY3 =  newChildButtonY2 + addNextChildWithChildren(directionModesItem, buildModeItem, toolModesItem, fillGapItem);

                    GuiTextButton introDirModesItem = new GuiTextButton(16,sideBarStartX+15,newChildButtonY3,"Intro Direction Modes",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addFirstChildToParent(introDirModesItem, directionModesItem);

                    GuiTextButton freeItem = new GuiTextButton(21,sideBarStartX+15,newChildButtonY3,"Free selection",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(freeItem, directionModesItem, introDirModesItem);

                    GuiTextButton XZplaneItem = new GuiTextButton(17,sideBarStartX+15,newChildButtonY3,"XZ/XY/ZY planes",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(XZplaneItem, directionModesItem, freeItem);

                    GuiTextButton autoItem = new GuiTextButton(20,sideBarStartX+15,newChildButtonY3,"Auto plane",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(autoItem, directionModesItem, XZplaneItem);

                    GuiTextButton groundItem = new GuiTextButton(22,sideBarStartX+15,newChildButtonY3,"To Ground",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(groundItem, directionModesItem, autoItem);

                newChildButtonY2 += directionModesItem.getTotalHeight();
                GuiTextButton fillModesItem = new GuiTextButton(26,sideBarStartX+10,newChildButtonY2,"Fill Modes",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY2 += addNextChildWithChildren(fillModesItem, buildModeItem, directionModesItem, fillModesItem);

            //EDIT MODE
            newChildButtonY = newChildButtonY + buildModeItem.getTotalHeight();
            GuiTextButton editModeItem = new GuiTextButton(30,sideBarStartX+5,newChildButtonY,"Edit Mode",false,true,secondRowColor,sideBarStartX);
            newChildButtonY2 =  newChildButtonY + addNextChildWithChildren(editModeItem, mainModeItem, buildModeItem, fillModesItem);

                GuiTextButton introEditItem = new GuiTextButton(31,sideBarStartX+10,newChildButtonY2,"Intro Edit Mode",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY2 += addFirstChildToParent(introEditItem, editModeItem);

                GuiTextButton adjustModesItem = new GuiTextButton(32,sideBarStartX+10,newChildButtonY2,"Adjust Modes",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY3 =  newChildButtonY2 + addChildWithChildren(adjustModesItem, editModeItem, introEditItem);

                    GuiTextButton introAdjustModesItem = new GuiTextButton(33,sideBarStartX+15,newChildButtonY3,"Intro",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addFirstChildToParent(introAdjustModesItem, adjustModesItem);

                    GuiTextButton paintBucketItem = new GuiTextButton(36,sideBarStartX+15,newChildButtonY3,"PaintBucket adjust tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(paintBucketItem, adjustModesItem, introAdjustModesItem);

                    GuiTextButton paintItem = new GuiTextButton(34,sideBarStartX+15,newChildButtonY3,"Paint adjust tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(paintItem, adjustModesItem, paintBucketItem);

                    GuiTextButton plantItem = new GuiTextButton(35,sideBarStartX+15,newChildButtonY3,"Plant adjust tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(plantItem, adjustModesItem, paintItem);

                    GuiTextButton digRaiseItem = new GuiTextButton(36,sideBarStartX+15,newChildButtonY3,"Dig/Raise adjust tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(digRaiseItem, adjustModesItem, plantItem);

                    GuiTextButton smoothItem = new GuiTextButton(34,sideBarStartX+15,newChildButtonY3,"Smooth/Sharpen adjust tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(smoothItem, adjustModesItem, digRaiseItem);

                    GuiTextButton levelItem = new GuiTextButton(35,sideBarStartX+15,newChildButtonY3,"Level adjust tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(levelItem, adjustModesItem, smoothItem);

                newChildButtonY2 += adjustModesItem.getTotalHeight();
                GuiTextButton terrainShapeModesItem = new GuiTextButton(26,sideBarStartX+10,newChildButtonY2,"Terrain Shape Modes",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY3 =  newChildButtonY2 + addNextChildWithChildren(terrainShapeModesItem, editModeItem, adjustModesItem, levelItem);

                    GuiTextButton introTerrainShapeModesItem = new GuiTextButton(16,sideBarStartX+15,newChildButtonY3,"Intro Terrain Shape Modes",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addFirstChildToParent(introTerrainShapeModesItem, terrainShapeModesItem);

                    GuiTextButton effectDigRaiseItem = new GuiTextButton(21,sideBarStartX+15,newChildButtonY3,"Effect on Dig/Raise tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(effectDigRaiseItem, terrainShapeModesItem, introTerrainShapeModesItem);

                    GuiTextButton effectSmoothItem = new GuiTextButton(17,sideBarStartX+15,newChildButtonY3,"Effect on Smooth/Sharpen tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(effectSmoothItem, terrainShapeModesItem, effectDigRaiseItem);

                    GuiTextButton effectLevelItem = new GuiTextButton(20,sideBarStartX+15,newChildButtonY3,"Effect on Level tool",false,true,fourthRowColor,sideBarStartX);
                    newChildButtonY3 += addNextChildToParent(effectLevelItem, terrainShapeModesItem, effectSmoothItem);

                newChildButtonY2 += terrainShapeModesItem.getTotalHeight();
                GuiTextButton onlyTerrainModesItem = new GuiTextButton(26,sideBarStartX+10,newChildButtonY2,"Only Terrain Modes",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY2 += addNextChildWithChildren(onlyTerrainModesItem, editModeItem, terrainShapeModesItem, effectLevelItem);

            //PLACE MODE
            newChildButtonY += editModeItem.getTotalHeight();
            GuiTextButton placeModeItem = new GuiTextButton(37,sideBarStartX+5,newChildButtonY,"Place Mode",false,true,secondRowColor,sideBarStartX);
            newChildButtonY2 =  newChildButtonY + addNextChildWithChildren(placeModeItem, mainModeItem, editModeItem, onlyTerrainModesItem);

                GuiTextButton introPlaceItem = new GuiTextButton(38,sideBarStartX+10,newChildButtonY2,"Intro Place Mode",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY2 += addFirstChildToParent(introPlaceItem, placeModeItem);

                GuiTextButton foundationItem = new GuiTextButton(39,sideBarStartX+10,newChildButtonY2,"Foundation Modes",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY2 += addNextChildToParent(foundationItem,placeModeItem,introPlaceItem);

                GuiTextButton airModesItem = new GuiTextButton(44,sideBarStartX+10,newChildButtonY2,"Air Modes",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY2 += addNextChildToParent(airModesItem,placeModeItem,foundationItem);

                GuiTextButton wallModesItem = new GuiTextButton(48,sideBarStartX+10,newChildButtonY2,"Wall Modes",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY2 += addNextChildToParent(wallModesItem,placeModeItem,airModesItem);

                GuiTextButton downloadItem = new GuiTextButton(49,sideBarStartX+10,newChildButtonY2,"Download Templates",false,true,thirdRowColor,sideBarStartX);
                newChildButtonY2 += addNextChildToParent(downloadItem,placeModeItem,wallModesItem);

            //CREATE MODE
            newChildButtonY = newChildButtonY + placeModeItem.getTotalHeight();
            GuiTextButton createModeItem = new GuiTextButton(54,sideBarStartX+5,newChildButtonY,"Create Mode",false,true,secondRowColor,sideBarStartX);
            newChildButtonY =  newChildButtonY + addNextChildWithChildren(createModeItem, mainModeItem, placeModeItem, downloadItem);

        newbuttonY += mainModeItem.getTotalHeight();
        GuiTextButton AbsCoordItem = new GuiTextButton(54,sideBarStartX,newbuttonY,"Absolute Coordinates",true,true);
        newbuttonY += addMainItemAfterChild(AbsCoordItem, mainModeItem, createModeItem);

        GuiTextButton fpsOptimisationItem = new GuiTextButton(54,sideBarStartX,newbuttonY,"Fps Optimisation",true,true);
        newbuttonY += addNextMainItem(fpsOptimisationItem, AbsCoordItem);

        GuiTextButton proMovesItem = new GuiTextButton(54,sideBarStartX,newbuttonY,"Pro Moves",true,true);
        newbuttonY += addNextMainItem(proMovesItem, fpsOptimisationItem);

        GuiTextButton contriItem = new GuiTextButton(54,sideBarStartX,newbuttonY,"Wanna Contribute?",true,true);
        newChildButtonY += newbuttonY + addNextMainItem(contriItem, proMovesItem);

            GuiTextButton bugReportItem = new GuiTextButton(13,sideBarStartX+5,newChildButtonY,"Make Bug Reports",false,true,secondRowColor,sideBarStartX);
            addChildToParent(bugReportItem, contriItem);
            newChildButtonY += introMainModeItem.getHeight();

            GuiTextButton keyLogsItem = new GuiTextButton(13,sideBarStartX+5,newChildButtonY,"Send KeyLog Files",false,true,secondRowColor,sideBarStartX);
            newChildButtonY += addNextChildToParent(keyLogsItem, contriItem, bugReportItem);

            GuiTextButton donateItem = new GuiTextButton(13,sideBarStartX+5,newChildButtonY,"Donate",false,true,secondRowColor,sideBarStartX);
            newChildButtonY += addNextChildToParent(donateItem, contriItem, keyLogsItem);

        newbuttonY += contriItem.getTotalHeight();
        GuiTextButton modChangesItem = new GuiTextButton(54,sideBarStartX,newbuttonY,UpdateChangesText.update_title,true,true);
        newbuttonY += addMainItemAfterChild(modChangesItem, contriItem, donateItem);


        sideTotalHeight = newbuttonY + 150;

        //add the sidemenu buttons to the list of sidemenu buttons and others
        buttonList.add(backButton);
        buttonList.add(nextButton);
        //General info
        addSideMenuButton(overviewItem,0,0);

        // Perspectives
        addSideMenuButton(perspectivesItem,1,0);
        // General Features
        addSideMenuButton(generalControlsItem,2,0);
        // Main modes
        addSideMenuButton(mainModeItem);
            addSideMenuButton(introMainModeItem,3,0);
            // Build mode
            addSideMenuButton(buildModeItem);
                addSideMenuButton(introBuildItem,4,0);
                // Tool modes
                addSideMenuButton(toolModesItem);
                    addSideMenuButton(introToolModesItem,5,0);
                    addSideMenuButton(SingleItem,5,1,BuildMode.TOOL_MODES[0].toolModeName, modes);
                    addSideMenuButton(lineItem,5,2,BuildMode.TOOL_MODES[1].toolModeName, modes);
                    addSideMenuButton(rectangleItem,5,3,BuildMode.TOOL_MODES[2].toolModeName, modes);
                    addSideMenuButton(curveItem,5,4,BuildMode.TOOL_MODES[3].toolModeName, modes);
                    addSideMenuButton(circleItem,5,5,BuildMode.TOOL_MODES[4].toolModeName, modes);
                    addSideMenuButton(pullItem,5,6,BuildMode.TOOL_MODES[5].toolModeName, modes);
                    addSideMenuButton(copyItem,5,7,BuildMode.TOOL_MODES[6].toolModeName, modes);
                    addSideMenuButton(moveDelItem,5,8,BuildMode.TOOL_MODES[7].toolModeName, modes);
                    addSideMenuButton(adjustItem,5,9);
                    addSideMenuButton(fillGapItem,5,10,BuildMode.TOOL_MODES[8].toolModeName, modes);
                // Direction modes
                addSideMenuButton(directionModesItem);
                    addSideMenuButton(introDirModesItem,6,0);
                    addSideMenuButton(freeItem,6,1);
                    addSideMenuButton(XZplaneItem,6,2);
                    addSideMenuButton(autoItem,6,3);
                    addSideMenuButton(groundItem,6,4);
                // Fill modes
                addSideMenuButton(fillModesItem,7,0);
            // Edit mode
            addSideMenuButton(editModeItem);
                addSideMenuButton(introEditItem,8,0);
                // Adjust modes
                addSideMenuButton(adjustModesItem);
                    addSideMenuButton(introAdjustModesItem,9,0);
                    addSideMenuButton(paintBucketItem,9,1,EditMode.ADJUST_MODES[2].toolModeName,modes);
                    addSideMenuButton(paintItem,9,2,EditMode.ADJUST_MODES[0].toolModeName,modes);
                    addSideMenuButton(plantItem,9,3,EditMode.ADJUST_MODES[1].toolModeName,modes);
                    addSideMenuButton(digRaiseItem,9,4,EditMode.ADJUST_MODES[2].toolModeName,modes);
                    addSideMenuButton(smoothItem,9,5,EditMode.ADJUST_MODES[0].toolModeName,modes);
                    addSideMenuButton(levelItem,9,6,EditMode.ADJUST_MODES[1].toolModeName,modes);
                addSideMenuButton(terrainShapeModesItem);
                    addSideMenuButton(introTerrainShapeModesItem,10,0);
                    addSideMenuButton(effectDigRaiseItem,10,1);
                    addSideMenuButton(effectSmoothItem,10,2);
                    addSideMenuButton(effectLevelItem,10,3);
                addSideMenuButton(onlyTerrainModesItem,11,0);
        // Place mode
            addSideMenuButton(placeModeItem);
                addSideMenuButton(introPlaceItem,12,0,"place",modes);
                addSideMenuButton(foundationItem,12,4);
                addSideMenuButton(airModesItem,12,5);
                addSideMenuButton(wallModesItem,12,6);
                addSideMenuButton(downloadItem,12,7);
            // Create mode
            addSideMenuButton(createModeItem,13,0,"create",modes);
            // Absolute Coordinates
            addSideMenuButton(AbsCoordItem,14,0);
            // Fps Optimisation
            addSideMenuButton(fpsOptimisationItem,15,0);
            //extra topics
            addSideMenuButton(proMovesItem,16,0);
            addSideMenuButton(contriItem);
                addSideMenuButton(bugReportItem,17,0);
                addSideMenuButton(keyLogsItem,17,1);
                addSideMenuButton(donateItem,17,2);
        addSideMenuButton(modChangesItem,18,0);

        currentSideMenuItem = overviewItem;
        mainModeItem.setVisibleChildren(true);

        // ### make the pages and add them to the pages list###
        pages.add(new OverviewPage("Introduction"));                //0
        pages.add(new PerspectivesPage("Isometric View"));          //1
        pages.add(new GeneralControlsPage("General Features"));     //2
        pages.add(new MainModesPage("Main Modes"));                 //3
        pages.add(new BuildModePage("Build Mode"));                 //4
        pages.add(new ToolModesPage("Tool Modes"));                 //5
        pages.add(new DirectionModesPage("Direction Modes"));       //6
        pages.add(new FillModesPage("Fill Modes"));                 //7
        pages.add(new EditModePage("Edit Mode"));                   //8
        pages.add(new AdjustModesPage("Adjust Modes"));             //9
        pages.add(new TerrainShapeModesPage("Terrain Shape Modes"));//10
        pages.add(new OnlyTerrainModesPage("Only Terrain Modes"));  //11
        pages.add(new PlaceModePage("Place Mode"));                 //12
        pages.add(new CreateModePage("Create Mode"));               //13
        pages.add(new AbsoluteCoordPage("Absolute Coordinates"));   //14
        pages.add(new FpsOptimisationPage("Fps Optimisation"));     //15
        pages.add(new ProMovesPage("Pro Moves"));                   //16
        pages.add(new ContributePage("Wanna Contribute?"));         //17
        pages.add(new ModChangesPage(UpdateChangesText.update_title));   //18

        loadingPage = true;

        //first time opening the help screen shows you the welcoming screen
        if(!firstTimeOpeningHelp)
        {
            if(AdvCreation.mode == EnumMainMode.BUILD)
            {
                GuiTextButton button = modes.get(BuildMode.TOOLMODE.toolModeName);
                if(button!=null)
                    ActivateButton(button);
            }
            else if(AdvCreation.mode == EnumMainMode.EDIT)
            {
                GuiTextButton button = modes.get(EditMode.ADJUST_MODE.toolModeName);
                if(button!=null)
                    ActivateButton(button);
            }
            else if(AdvCreation.mode == EnumMainMode.PLACE)
            {
                GuiTextButton button = modes.get("place");
                if(button!=null)
                    ActivateButton(button);
            }
            else if(AdvCreation.mode == EnumMainMode.CREATE)
            {
                GuiTextButton button = modes.get("create");
                if(button!=null)
                    ActivateButton(button);

            }
        }
        firstTimeOpeningHelp=false;




        //slider to change the size of the text font
        textSizeSlider = new net.minecraftforge.fml.client.config.GuiSlider(2, guiScreenWidth/2 +10 , 3, 90, 16, "", "", 20, 200,(int) ConfigurationHandler.general.HELP_TEXT_SIZE,true,false);
        oldSliderValue = textSizeSlider.getValueInt();
        buttonList.add(textSizeSlider);

        super.initGui();

    }




    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();

        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        int factor = scaledresolution.getScaleFactor();



        
        double scale = 2.0/factor;
//        //### DRAW SCROLL DOWN ###
        if(loadingPage)
        {
            String loading = "LOADING PAGE";
            Random randomiser = new Random(System.currentTimeMillis());
            flashyColor = new Color(randomiser.nextInt(255),randomiser.nextInt(255),randomiser.nextInt(255));
            GuiScreenTextPrinter.drawText(loading,guiScreenWidth/2 -(mc.fontRenderer.getStringWidth(loading)*2),guiScreenHeight/2-20,flashyColor.getRGB() ,2.0);

            if(secondStageLoadingPage)
            {
                pages.get(currentPage).removeGif();
                currentPage = newPage;
                pages.get(currentPage).initPage(Minecraft.getMinecraft(), GuiScreenTextPrinter.charSize);
                this.loadingPage = false;
                this.secondStageLoadingPage = false;
                this.offsetPage = true;
            }
            else
                this.secondStageLoadingPage= true;


        }
        else
        {
            long currtime = System.currentTimeMillis();
            if(scrollUpMouseHeld && scrollRepeatDelay < (currtime - previousScrollTime))
            {
                previousScrollTime = currtime;
                mouseScroll(keyScrollSpeed*2);
            }
            if(scrollDownMouseHeld && scrollRepeatDelay < (currtime - previousScrollTime))
            {
                previousScrollTime = currtime;
                mouseScroll(-keyScrollSpeed*2);
            }


            drawSideMenuBorders(mouseX, mouseY);
            setRightSideMenuItemActive();
            manageScrolling((double) width, height, scale);

            pages.get(currentPage).drawPage(mc,mouseX,mouseY,partialTicks);
            drawButtonsAndLabels(mouseX, mouseY, partialTicks);

            int newTotalHeight = pages.get(currentPage).getUpdatedHeight();
            if(totalHeight < (newTotalHeight+150))
                totalHeight = newTotalHeight+150;

            if(offsetPage)
            {
                this.offsetY = -(pages.get(currentPage).getParagraphPosY(newOffset)-50);
                updateChildElementsOffsetY();
                offsetPage = false;
            }

        }
        //draw the slider to adjust the text size of the helpscreen
        GuiScreenTextPrinter.drawText("",155,3, 0xFFFFFFFF,1.8);
        mc.renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/gui/custom_windows.png"));
        drawTexturedModalRect(150, 0, 0,0 , 256, 22);
        drawTexturedModalRect(150, 5, 0, 256-17, 256, 17);
        Random randomiser = new Random(System.currentTimeMillis());
        GuiScreenTextPrinter.drawText("TEXT SIZE",155,3, flashyColor.getRGB(),1.8);
        GuiScreenTextPrinter.drawText(textSizeSlider.getValueInt() + "%",(155+190),3, flashyColor.getRGB(),1.8);


        textSizeSlider.drawButton(mc,mouseX,mouseY,partialTicks);

    }

    private void manageScrolling(double width, int height, double scale)
    {
        if ((sideTotalHeight + sideOffsetY) >= height) {
            String[][] scrollText = {{"SCROLL DOWN"}};
            sideCanScrollDown = true;
//            GuiScreenTextPrinter.drawAndFormatBody(scrollText, sideBarStartX+1, height - 12, 12, scale);
            GuiScreenTextPrinter.drawAndFormatBody(scrollText, pages.get(currentPage).startX-1 - (int)(Minecraft.getMinecraft().fontRenderer.getStringWidth(scrollText[0][0])*scale), height - 32, 12, scale);

        }
        else
            sideCanScrollDown = false;

        sideCanScrollUp = sideOffsetY != 0;

        if ((totalHeight + offsetY) >= height) {
            String[][] scrollText = {{"SCROLL DOWN"}};
            canScrollDown = true;
            GuiScreenTextPrinter.drawAndFormatBody(scrollText, width - 80, height - 12, 12, scale);
        }
        else
            canScrollDown = false;

        canScrollUp = offsetY != 0;
    }

    private void drawSideMenuBorders(int mouseX, int mouseY)
    {
        int color = Color.BLACK.getRGB();
        int startX = 1;
        int endX = pages.get(currentPage).startX-1;
        int startY = 1;
        int endY = (int)sideTotalHeight + sideOffsetY;

        sideHovered = mouseX >= startX && mouseY >= startY && mouseX < endX && mouseY < endY;
        if(sideHovered)
            color = -6250336;

        this.drawVerticalLine(startX,startY,endY,color);
        this.drawVerticalLine(endX,startY,endY,color);
        this.drawHorizontalLine(startX,endX,startY,color);
        this.drawHorizontalLine(startX,endX,endY,color);
        GlStateManager.color(1.0f, 1.0f,1.0f,1.0f);
    }

    private void setRightSideMenuItemActive()
    {
        for (GuiTextButton menuButton:sideMenuButtons.keySet())
        {
            menuButton.setWidth((pages.get(currentPage).startX-1)-menuButton.x);
            menuButton.setActive(false);
        }

        if(currentSideMenuItem != null)
            currentSideMenuItem.setActive(true);
    }

    public void drawButtonsAndLabels(int mouseX, int mouseY, float partialTicks)
    {
        for (GuiButton guiButton : this.buttonList)
        {

            if (guiButton == nextButton || guiButton == backButton)
                guiButton.drawButton(this.mc, mouseX, mouseY, partialTicks);

            else if (((guiButton.y + guiButton.height * 3 / 4 + sideOffsetY > nextButton.y)))
                continue;
            else if (((guiButton.y + guiButton.height / 4 + sideOffsetY < backButton.y + backButton.height)))
                continue;
            else
                guiButton.drawButton(this.mc, mouseX, mouseY, partialTicks);
        }


        for (GuiLabel guiLabel : this.labelList)
        {
            guiLabel.drawLabel(this.mc, mouseX, mouseY);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        int DWheel = Mouse.getEventDWheel();
        if(DWheel != 0)
        {
            if(DWheel < 0.0001F && Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102)
            {
                super.handleMouseInput();
                return;
            }
            else if(DWheel > 0.0001F && Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101)
            {
                super.handleMouseInput();
                return;
            }

            mouseScroll(DWheel);
        }
        super.handleMouseInput();
    }
    
    protected void mouseScroll(int DWheel)
    {
        int scaled = (int)Math.floor(DWheel/12.0);
        if(!sideHovered)
        {
            if( scaled < 0 && canScrollDown)
                offsetY += Math.max(scaled, -30);
            else if( scaled > 0 && canScrollUp)
            {
                offsetY += Math.min(scaled, 30);

                if(offsetY > 0)
                {
                    offsetY = 0;
                }
            }
        }
        else
        {
            if( scaled < 0 && sideCanScrollDown)
                sideOffsetY += Math.max(scaled, -30);
            else if( scaled > 0 && sideCanScrollUp)
            {
                sideOffsetY += Math.min(scaled, 30);
                if(sideOffsetY > 0)
                    sideOffsetY = 0;
            }
        }
        updateChildElementsOffsetY();
    }

    private void updateChildElementsOffsetY() {
        for (GuiButton button: buttonList)
        {
            if(button instanceof GuiTextButton)
                ((GuiTextButton) button).setOffsetY(sideOffsetY);
            if(button instanceof Paragraph)
                ((Paragraph) button).setOffsetY(offsetY);
        }

        for(AbstractPage page: pages)
        {
            page.setOffsetY(offsetY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        previousScrollTime = System.currentTimeMillis();

        if(Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101 && (-100+mouseButton) == Keybindings.ZOOM_IN.getKeybind().getKeyCode())
        {
            mouseScroll(keyScrollSpeed);
            previousScrollTime = System.currentTimeMillis();
            scrollUpMouseHeld = true;
            return ;
        }
        if(Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102 && (-100+mouseButton) == Keybindings.ZOOM_OUT.getKeybind().getKeyCode())
        {
            mouseScroll(-keyScrollSpeed);
            previousScrollTime = System.currentTimeMillis();
            scrollDownMouseHeld = true;
            return ;
        }



        if(selectedButton == null)
            //any mouse should close the help screen
            Minecraft.getMinecraft().displayGuiScreen(null);
        
    }



    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if(scrollUpMouseHeld)
            scrollUpMouseHeld = false;
        if(scrollDownMouseHeld)
            scrollDownMouseHeld = false;

        if(textSizeSlider.dragging)
        {
            int newSliderValue = textSizeSlider.getValueInt();
            if(oldSliderValue != newSliderValue)
            {
                ConfigurationHandler.general.HELP_TEXT_SIZE = (double) textSizeSlider.getValueInt() ;
                pageSelection pageSelected = new pageSelection(currentPage,0);
                activateLoadingNewPage(pageSelected);
                oldSliderValue = newSliderValue;
                ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
            }
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        long time = System.currentTimeMillis();
        if(time-lastClickTime < clickDelay)
            return;
        lastClickTime = time;

        if(button.id == BACK_BUTTON_ID)
        {
            if (performBackButtonAction()) return;
        }
        else if(button.id == NEXT_BUTTON_ID)
        {

            if (performNextButtonAction()) return;

        }
        else if(button instanceof GuiTextButton)
        {
            perfromSideMenuAction((GuiTextButton) button);
        }
        else if(button == textSizeSlider)
        {
//            ((GuiSlider) textSizeSlider).mousePressed(mc,mous)
        }
        super.actionPerformed(button);
    }



    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if(keyCode == Keybindings.OPEN_REPORT_SCREEN.getKeybind().getKeyCode())
        {
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.OPEN_REPORT_SCREEN,"open ReportScreen");
            Report.saveScreenshot();

            Minecraft.getMinecraft().displayGuiScreen(new ReportScreen(this));
        }
        else if(Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101 && keyCode == Keybindings.ZOOM_IN.getKeybind().getKeyCode())
        {
            mouseScroll(keyScrollSpeed);
            return ;
        }
        else if(Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102 && keyCode == Keybindings.ZOOM_OUT.getKeybind().getKeyCode())
        {
            mouseScroll(-keyScrollSpeed);
            return ;
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void onGuiClosed()
    {
        GuiOverlayManager.setHelpButtonSelected(false);
        pages.get(currentPage).removeGif();
        super.onGuiClosed();
    }

    //################################ extracted methods  #############################################################

    public int addNextMainItem(GuiTextButton mainItem, GuiTextButton previousItem)
    {
        int newHeight;
        previousItem.addNextButton(mainItem);
        newHeight = mainItem.getHeight();
        return newHeight;
    }

    public int addMainItemAfterChild(GuiTextButton mainItem, GuiTextButton previousItem, GuiTextButton previousChildItem)
    {
        int newHeight;
        previousChildItem.addNextButton(mainItem);
        previousItem.addNextButton(mainItem);
        newHeight = mainItem.getHeight();
        return newHeight;
    }

    public int addChildWithChildren(GuiTextButton childItem, GuiTextButton parentItem, GuiTextButton previousItem)
    {
        int newHeight;
        addChildToParent(childItem, parentItem);
        previousItem.addNextButton(childItem);
        newHeight = childItem.getHeight();
        return newHeight;
    }

    public int addNextChildWithChildren(GuiTextButton childItem, GuiTextButton parentItem, GuiTextButton previousItem, GuiTextButton previousChildItem)
    {
        previousItem.addNextButton(childItem);
        previousChildItem.addNextButton(childItem);
        addChildToParent(childItem, parentItem);
        return childItem.getHeight();
    }

    public int addNextChildToParent(GuiTextButton childItem, GuiTextButton parentItem, GuiTextButton previousItem)
    {
        addChildToParent(childItem, parentItem);
        previousItem.addNextButton(childItem);
        return childItem.getHeight();
    }

    public int addFirstChildToParent(GuiTextButton childItem, GuiTextButton parentItem)
    {
        addChildToParent(childItem, parentItem);
        return childItem.getHeight();
    }

    private void ActivateButton(GuiTextButton button) {
        ArrayList<GuiTextButton> parents= new ArrayList<>();
        GuiTextButton parent = button.parentButton;
        while(parent != null)
        {
            parents.add(parent);
            parent = parent.parentButton;
        }

        for(int i = parents.size()-1;i>=0;i--)
        {
            if(!parents.get(i).isVisibleChildren())
            {
                parents.get(i).setVisibleChildren(true);
            }
        }

        try {
            this.actionPerformed(button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addChildToParent(GuiTextButton childItem, GuiTextButton parent) {
        parent.addChildButton(childItem);
        childItem.setParentButton(parent);

    }

    public void addSideMenuButton(GuiTextButton button)
    {
        sideMenuButtons.put(button,null);
        buttonList.add(button);
    }

    public void addSideMenuButton(GuiTextButton button,boolean nonFunctional)
    {
        sideMenuButtons.put(button,null);
        button.setNonFunctional(nonFunctional);
    }

    public void addSideMenuButton(GuiTextButton button,int pageIndex,int pageOffset)
    {
        sideMenuButtons.put(button,new pageSelection(pageIndex,pageOffset));
        sideMenuButtonsList.add(button);
        buttonList.add(button);
    }

    public void addSideMenuButton(GuiTextButton button,int pageIndex,int pageOffset,String mode,HashMap<String,GuiTextButton> map)
    {
        sideMenuButtons.put(button,new pageSelection(pageIndex,pageOffset));
        sideMenuButtonsList.add(button);
        buttonList.add(button);
        map.put(mode,button);

    }

    private void perfromSideMenuAction(GuiTextButton button)
    {
        currentSideMenuItem = button;
        pageSelection pageSelected = sideMenuButtons.get(currentSideMenuItem);
        sideTotalHeight = (button.toggleVisibleChildren()+150);

        if(pageSelected!=null)
        {
            if(currentPage != pageSelected.pageIndex)
                activateLoadingNewPage(pageSelected);
            ActivateGotoParagraph(pageSelected);

            //get the name for the Next Button
            updateNextButton(sideMenuButtonsList.indexOf(currentSideMenuItem)+1);
            //get the name for the Back button
            updateBackButton(sideMenuButtonsList.indexOf(currentSideMenuItem)-1);
        }
    }

    private void updateBackButton(int currIndex)
    {
        currIndex += -1;
        if(isValidSideMenuIndex(currIndex))
        {
            currIndex = getNextSideMenuIndexToPage(currIndex, 1);
            GuiTextButton nextBackButton = getValidSideMenuItem(currIndex);
            pageSelection pageSelected2 = sideMenuButtons.get(nextBackButton);
            if(pageSelected2 == null)
                backButton.displayString = "BACK";
            else
                backButton.displayString = "BACK: " + nextBackButton.displayString;
        }
        else
            backButton.displayString = "BACK";
    }

    private boolean isValidSideMenuIndex(int sideMenuIndex)
    {
        return sideMenuIndex < sideMenuButtonsList.size() && sideMenuIndex >= 0;
    }

    private boolean performNextButtonAction()
    {
        //Find the next sideMenuItem that points to a valid page and paragraph
        GuiTextButton previousMenuButton = currentSideMenuItem;
        int currIndex = sideMenuButtonsList.indexOf(currentSideMenuItem)+1;

        if(!isValidSideMenuIndex(currIndex))
            return true;

        currIndex = getNextSideMenuIndexToPage(currIndex, 1);
        GuiTextButton newActiveSideMenuItem = getValidSideMenuItem(currIndex);
        pageSelection pageSelected = sideMenuButtons.get(newActiveSideMenuItem);

        if(pageSelected!=null)
        {
            if(newActiveSideMenuItem.parentButton != null)
                sideTotalHeight = newActiveSideMenuItem.parentButton.setVisibleChildren(true) +150;
            if(currentPage != pageSelected.pageIndex)
                activateLoadingNewPage(pageSelected);
            ActivateGotoParagraph(pageSelected);
            backButton.displayString = "BACK: " +previousMenuButton.displayString;
            currentSideMenuItem = newActiveSideMenuItem;
        }

        //update the text of the next button to show the name of the next page
        updateNextButton(currIndex);

        return false;
    }

    private boolean performBackButtonAction()
    {
        GuiTextButton previousMenuButton = currentSideMenuItem;
        int currIndex = sideMenuButtonsList.indexOf(currentSideMenuItem)-1;

        if(!isValidSideMenuIndex(currIndex))
            return true;

        currIndex = getNextSideMenuIndexToPage(currIndex, -1);
        GuiTextButton newActiveSideMenuItem = getValidSideMenuItem(currIndex);
        pageSelection pageSelected = sideMenuButtons.get(newActiveSideMenuItem);

        if(pageSelected!=null)
        {
            if(newActiveSideMenuItem.parentButton != null)
                sideTotalHeight = newActiveSideMenuItem.parentButton.setVisibleChildren(true) +150;
            if(currentPage != pageSelected.pageIndex)
                activateLoadingNewPage(pageSelected);
            ActivateGotoParagraph(pageSelected);
            nextButton.displayString = "NEXT: " +previousMenuButton.displayString;
            currentSideMenuItem = newActiveSideMenuItem;
        }

        //update the text of the back button to show the name of the next backwords page
        currIndex+= -1;
        if(isValidSideMenuIndex(currIndex))
        {
            currIndex = getNextSideMenuIndexToPage(currIndex, -1);
            GuiTextButton nextBackButton = sideMenuButtonsList.get(currIndex);
            pageSelection pageSelected2 = sideMenuButtons.get(nextBackButton);
            if(pageSelected2 == null)
                backButton.displayString = "BACK";
            else
                backButton.displayString = "BACK: " + nextBackButton.displayString;
        }
        else
            backButton.displayString = "BACK";

        return false;
    }

    private void updateNextButton(int currIndex)
    {
        currIndex+= 1;
        if(isValidSideMenuIndex(currIndex))
        {
            currIndex = getNextSideMenuIndexToPage(currIndex, 1);
            GuiTextButton nextNextButton = sideMenuButtonsList.get(currIndex);
            pageSelection pageSelected2 = sideMenuButtons.get(nextNextButton);

            if(pageSelected2 == null)
                nextButton.displayString = "NEXT";
            else
                nextButton.displayString = "NEXT: " + nextNextButton.displayString;
        }
        else
            nextButton.displayString = "NEXT";
    }

    private void ActivateGotoParagraph(pageSelection pageSelected)
    {
        offsetPage = true;
        newOffset = pageSelected.pageOffset;
    }

    private int getNextSideMenuIndexToPage(int currIndex, int addition)
    {
        GuiTextButton newActiveSideMenuItemIter = getValidSideMenuItem(currIndex);
        pageSelection pageSelectedIter = sideMenuButtons.get(newActiveSideMenuItemIter);
        while (pageSelectedIter == null)
        {
            currIndex += addition;
            newActiveSideMenuItemIter = getValidSideMenuItem(currIndex);

            if (newActiveSideMenuItemIter == null)
                break;
            pageSelectedIter = sideMenuButtons.get(newActiveSideMenuItemIter);
        }
        return currIndex;
    }

    private void activateLoadingNewPage(pageSelection pageSelected)
    {
        this.loadingPage = true;
        newPage = pageSelected.pageIndex;
    }

    private GuiTextButton getValidSideMenuItem(int currIndex)
    {
        GuiTextButton newActiveSideMenuItem;
        if (isValidSideMenuIndex(currIndex))
            newActiveSideMenuItem = sideMenuButtonsList.get(currIndex);
        else
            newActiveSideMenuItem = null;
        return newActiveSideMenuItem;
    }


}


