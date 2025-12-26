package com.deadtiger.advcreation.client.gui.gui_screen.selection_wheel;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.client.gui.gui_overlay.ToolDisableGuiOverlay;
import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.*;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.client.input.KeyInputHandler;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.gui.gui_screen.reportScreen.ReportScreen;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.client.player.ToolEnabled;
import com.deadtiger.advcreation.edit_mode.adjust_modes.LevelAdjustMode;
import com.deadtiger.advcreation.edit_mode.utility.EnumTerrainMode;
import com.deadtiger.advcreation.edit_mode.utility.EnumTerrainShapeMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.message.MessageUpdatePlayerSetting;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.report.Report;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static com.deadtiger.advcreation.client.gui.gui_overlay.ToolDisableGuiOverlay.calculateToolDisableButtonY;

public class GuiAdjustModeSelectionScreen extends GuiScreen
{
    public static int mouseXBeforeOpening = 100;
    public static int mouseYBeforeOpening = 100;

    ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/gui/selectionwheel1.png");
    private final ResourceLocation legendIndicationTexture = new ResourceLocation(Reference.MODID,"textures/gui/legend_highlight_sign.png");

    // size of the main gui window
    private int guiWidth = 200;
    private int guiHeight = 200;
    
    //size of the menu screen? is always a low resolution? because ... why not?
    private int guiScreenWidth = 480;
    private int guiScreenHeight = 270;
    
    //gui screen texts
    private String title = "Select a Tool";
    
    //button on the Wheel GUI
    private GuiCustomButton paintAdjustButton;
    private static final int PAINT_BUTTON_ID = 0;
    private GuiCustomButton plantAdjustButton;
    private static final int PLANT_BUTTON_ID = 1;
    private GuiCustomButton paintBucketAdjustButton;
    private static final int PAINTBUCKET_BUTTON_ID = 2;
    private GuiCustomButton levelAdjustButton;
    private static final int LEVEL_BUTTON_ID = 3;
    private GuiCustomButton digRaiseToolButton;
    private static final int SMOOTH_BUTTON_ID = 4;
    private GuiCustomButton smoothSharpenToolButton;
    private static final int CIRCLE_BUTTON_ID = 5;

    private GuiCustomButton onlyTerrainButton;
    private static final int FILL_BUTTON_ID = 15;
    private GuiCustomButton allBlockButton;
    private static final int NO_FILL_BUTTON_ID = 16;
    
    //buttons to change the direction
    protected ResourceLocation BASE_DIR_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/base_direction_selection.png");
    protected ResourceLocation ACTIVE_DIR_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/active_direction_selection.png");
    protected ResourceLocation HOVER_DIR_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/hover_direction_selection.png");
    
    public boolean debug = false;
    
    //keeps the current hovered button to support the timed tooltip
    private GuiButton currHoverButton = null;
    private long firstHoverTime = 0;
    private int tooltipTimeDelay = 500;

    // the window
    private GuiCustomWindow mainModeWindow;
    // the buttons in the window
    private GuiToggleButton buildButton, editButton;
    private static final int BUILD_BUTTON_ID = 25, EDIT_BUTTON_ID = 26;

    //the buttons to choose a terrain shape mode in Smooth/sharpen mode
    private GuiCustomButton straightButton;
    private final int TERRAIN_SHAPE_STRAIGHT_ID = 24;
    private GuiCustomButton cliffButton;
    private final int TERRAIN_SHAPE_CLIFF_ID = 23;
    private GuiCustomButton mountainButton;
    private final int TERRAIN_SHAPE_MOUNTAIN_ID = 22;
    private GuiCustomButton valleyButton;
    private final int TERRAIN_SHAPE_VALLEY_ID = 21;
    private GuiCustomButton hillButton;
    private final int TERRAIN_SHAPE_HILL_ID = 20;
    private GuiCustomButton slopeButton;
    private final int TERRAIN_SHAPE_SLOPE_ID = 19;
    private GuiCustomButton riseButton;
    private final int TERRAIN_SHAPE_RISE_ID = 18;
    private GuiCustomButton planeButton;
    private final int TERRAIN_SHAPE_PLANE_ID = 17;


    private String legendTitle = "";
    private GuiToggleLongTextButton toggleLegendButton;
    private static final int LEGEND_BUTTON_ID = 27 , TOOL_ENABLE_BUTTON_ID = 19;
    private GuiOnOffButton toolDisableButton;
    private ArrayList<GuiLegendEntry> legend = new ArrayList<>();
    private int scrollBarTop = 0;
    private int scrollBarBottom = 0;
    private int scroll = 0;
    private int maxScroll = 0;
    private boolean hoveredOnLegend = false;
    private int startYLegend = 0;


    public static boolean alphaOn = true;
    public static boolean blendOn = true;
    public static boolean colorOn = true;
    public static boolean colorMatOn = true;
    public static boolean depthOn = true;
    public static boolean lightOn = true;
    public static boolean otherSetting1On = false;
    public static boolean otherSetting2On = false;
    public static boolean otherSetting3On = false;
    public static boolean otherSetting4On = false;
    public static boolean otherSetting5On = false;
    public static boolean otherSetting6On = false;
    public static boolean otherSetting7On = false;
    public static boolean otherSetting8On = false;
    public static boolean otherSetting9On = false;
    
    @Override
    public void initGui()
    {
        legend.clear();
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        guiScreenWidth = scaledresolution.getScaledWidth();
        guiScreenHeight = scaledresolution.getScaledHeight();
        
        buttonList.clear();
        initAdjustModeButtons();
        initTerrainShapeButtons();
        initOnlyTerrainButtons();
        updateActiveButtons();


        //initialise the buttons for switching mainmodes
        int buttonWidth = 50, buttonHeight = 20;
        mainModeWindow = new GuiCustomWindow(0,0,117,30);
        buildButton = new GuiToggleButton(BUILD_BUTTON_ID,5,5,buttonWidth,buttonHeight,"BUILD");
        buildButton.setNameTooltip("BUILD","Placing and deleting blocks");

        editButton =new GuiToggleButton(EDIT_BUTTON_ID,61,5,buttonWidth,buttonHeight,"EDIT");
        editButton.active = true;
        editButton.setNameTooltip("EDIT","Edit the world");

        this.buttonList.add(buildButton);
        this.buttonList.add(editButton);

        int startY = calculateToolDisableButtonY();

        //the buttons indicating and allowing the toggling of direction, fill and tool modes
        //direction mode
        toolDisableButton = new GuiOnOffButton(TOOL_ENABLE_BUTTON_ID,width - 55, startY, ConfigurationHandler.general.TOOLS_ENABLED);
        toolDisableButton.stateIsOn = (ConfigurationHandler.general.TOOLS_ENABLED);

        if(ConfigurationHandler.general.TOOLS_ENABLED)
            toolDisableButton.setNameTooltip("TOGGLE TOOLS","Toggle adv. creation tools (now: ON)");
        else
            toolDisableButton.setNameTooltip("TOGGLE TOOLS","Toggle adv. creation tools (now: OFF)");

        buttonList.add(toolDisableButton);

        //initialise the legend images and text
        int x_offset = 101;

        this.startYLegend = guiScreenHeight -20;
        if(EditMode.showLegend)
            this.startYLegend = (guiScreenHeight / 2) -10;

        toggleLegendButton = new GuiToggleLongTextButton(LEGEND_BUTTON_ID,(guiScreenWidth / 2) + x_offset ,startYLegend,(guiScreenWidth )-((guiScreenWidth / 2) + x_offset)-1,20,legendTitle);
        toggleLegendButton.setNameTooltip("LEGEND","Tells you what all the colored cubes mean for " + EditMode.ADJUST_MODE.buttonText + " tool");

        if(!EditMode.showLegend)
        {
            toggleLegendButton.y = guiScreenHeight - toggleLegendButton.height;
        }

        int maxLegendEntries = EditMode.getMaxLegendEntries();
        Color currColor;
        String currText;


        int currY = startYLegend + toggleLegendButton.height ;
        scrollBarTop = currY;
        currY +=2;
        for (int i = 0; i < maxLegendEntries; i++)
        {
            currColor = EditMode.getLegendColor(i);

            if(currColor == null || !EditMode.showLegend)
            {
                currColor = LevelAdjustMode.unchangedColor;
                currText = "";
                GuiLegendEntry newEntry = new GuiLegendEntry((guiScreenWidth / 2) + x_offset,currY ,(guiScreenWidth )-((guiScreenWidth / 2) + x_offset),16,new String[][]{{currText}}, currColor);
                this.legend.add(newEntry);
                this.buttonList.add(newEntry);
                currY += newEntry.getTotalHeight() + 3;
                newEntry.visible = false;
            }
            else
            {
                currText = EditMode.getLegendText(i);
                GuiLegendEntry newEntry = new GuiLegendEntry((guiScreenWidth / 2) + x_offset,currY ,(guiScreenWidth )-((guiScreenWidth / 2) + x_offset),16,new String[][]{{currText}}, currColor);
                this.legend.add(newEntry);
                this.buttonList.add(newEntry);
                currY += newEntry.getTotalHeight() + 3;
            }


        }
        this.buttonList.add(toggleLegendButton);
        scrollBarBottom = this.guiScreenHeight;
        maxScroll = currY - scrollBarBottom;
        
        super.initGui();
    }
    
    public void initAdjustModeButtons()
    {
        //top wheel button custom button
        paintAdjustButton = new GuiCircularButton(PAINT_BUTTON_ID,
            (guiScreenWidth / 2) - 46, (guiScreenHeight / 2) - 95,0,0,
            91, 55, "Paint",60,120);
        ((GuiCircularButton) paintAdjustButton).setCircleCoord(guiScreenWidth/2,guiScreenHeight/2);
        paintAdjustButton.setNameTooltip(EditMode.ADJUST_MODES[0].buttonText, EditMode.ADJUST_MODES[0].tooltipText);
        buttonList.add(paintAdjustButton);
    
        //right-up wheel button custom button
        plantAdjustButton = new GuiCircularButton(PLANT_BUTTON_ID,
            (guiScreenWidth / 2) + 26, (guiScreenHeight / 2)-80,175,0,
            69, 78, "Plant",0,60);
        ((GuiCircularButton) plantAdjustButton).setCircleCoord(guiScreenWidth/2,guiScreenHeight/2);
        plantAdjustButton.setNameTooltip(EditMode.ADJUST_MODES[1].buttonText, EditMode.ADJUST_MODES[1].tooltipText);
        buttonList.add(plantAdjustButton);
    
        //left-up wheel button custom button
        paintBucketAdjustButton = new GuiCircularButton(PAINTBUCKET_BUTTON_ID,
            (guiScreenWidth / 2) - 95, (guiScreenHeight / 2)-80,100,0,
            69, 78, "PaintBucket",120,180);
        ((GuiCircularButton) paintBucketAdjustButton).setCircleCoord(guiScreenWidth/2,guiScreenHeight/2);
        paintBucketAdjustButton.setNameTooltip(EditMode.ADJUST_MODES[2].buttonText, EditMode.ADJUST_MODES[2].tooltipText);
        buttonList.add(paintBucketAdjustButton);

        //bottom wheel button custom button
        smoothSharpenToolButton = new GuiCircularButton(CIRCLE_BUTTON_ID,
                (guiScreenWidth / 2) - 46, (guiScreenHeight / 2) + 40, 0, 75,
                91, 55, "Smooth/Sharpen", 240, 300);
        ((GuiCircularButton) smoothSharpenToolButton).setCircleCoord(guiScreenWidth / 2, guiScreenHeight / 2);
        smoothSharpenToolButton.setNameTooltip(EditMode.ADJUST_MODES[4].buttonText, EditMode.ADJUST_MODES[4].tooltipText);
        this.buttonList.add(smoothSharpenToolButton);

        //right-down wheel button custom button
        digRaiseToolButton = new GuiCircularButton(SMOOTH_BUTTON_ID,
                (guiScreenWidth / 2) + 26, (guiScreenHeight / 2) + 2, 175, 80,
                69, 78, "Dig/Raise", 300, 360);
        ((GuiCircularButton) digRaiseToolButton).setCircleCoord(guiScreenWidth / 2, guiScreenHeight / 2);
        digRaiseToolButton.setNameTooltip(EditMode.ADJUST_MODES[3].buttonText, EditMode.ADJUST_MODES[3].tooltipText);
        this.buttonList.add(digRaiseToolButton);

        //left-down wheel button custom button
        levelAdjustButton = new GuiCircularButton(LEVEL_BUTTON_ID,
                (guiScreenWidth / 2) - 95, (guiScreenHeight / 2) + 2, 100, 80,
                69, 78, "Level", 180, 240);
        ((GuiCircularButton) levelAdjustButton).setCircleCoord(guiScreenWidth / 2, guiScreenHeight / 2);
        levelAdjustButton.setNameTooltip(EditMode.ADJUST_MODES[5].buttonText, EditMode.ADJUST_MODES[5].tooltipText);
        this.buttonList.add(levelAdjustButton);
        
    }

    public void initOnlyTerrainButtons()
    {
        onlyTerrainButton = new GuiToggleButton(FILL_BUTTON_ID, (guiScreenWidth / 2) + 110, (guiScreenHeight / 2) - 60, 50, 20, "ONLY TERRAIN");
        onlyTerrainButton.setNameTooltip(EnumTerrainMode.ONLY_TERRAIN.buttonText, EnumTerrainMode.ONLY_TERRAIN.tooltipText);
        this.buttonList.add(onlyTerrainButton);

        allBlockButton = new GuiToggleButton(NO_FILL_BUTTON_ID, (guiScreenWidth / 2) + 110, (guiScreenHeight / 2) - 40, 50, 20, "ALL");
        allBlockButton.setNameTooltip(EnumTerrainMode.ALl.buttonText, EnumTerrainMode.ALl.tooltipText);
        this.buttonList.add(allBlockButton);
    }

    public void initTerrainShapeButtons()
    {

        planeButton = addTerrainShapeButton(TERRAIN_SHAPE_PLANE_ID, -3, EnumTerrainShapeMode.PLANE);
        riseButton = addTerrainShapeButton(TERRAIN_SHAPE_RISE_ID, -2, EnumTerrainShapeMode.RISE);
        slopeButton = addTerrainShapeButton(TERRAIN_SHAPE_SLOPE_ID, -1, EnumTerrainShapeMode.SLOPE);
        hillButton = addTerrainShapeButton(TERRAIN_SHAPE_HILL_ID, 0, EnumTerrainShapeMode.HILL);
        valleyButton = addTerrainShapeButton(TERRAIN_SHAPE_VALLEY_ID, 1, EnumTerrainShapeMode.VALLEY);
        mountainButton = addTerrainShapeButton(TERRAIN_SHAPE_MOUNTAIN_ID, 2, EnumTerrainShapeMode.MOUNTAIN);
        cliffButton = addTerrainShapeButton(TERRAIN_SHAPE_CLIFF_ID, 3, EnumTerrainShapeMode.CLIFF);
        straightButton = addTerrainShapeButton(TERRAIN_SHAPE_STRAIGHT_ID, 4, EnumTerrainShapeMode.STRAIGHT);
    }

    private GuiCustomButton addTerrainShapeButton(int button_id, int placeIndex, EnumTerrainShapeMode shape)
    {
        int width = 60;

        GuiCustomButton newbutton = new GuiToggleButton(button_id, (guiScreenWidth / 2) - 115 - 50, (guiScreenHeight / 2) - 20 * placeIndex, width, 20, shape.getButtonText());
        newbutton.setNameTooltip(shape.getButtonText(), shape.getAdjustModeAppropriateTooltipText(EditMode.ADJUST_MODES[EditMode.ADJUST_MODE_INDEX]));

        this.buttonList.add(newbutton);
        return newbutton;
    }
   
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        long time = System.currentTimeMillis();
        drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        //draw the main gui window mainTexture
        int centerX = (guiScreenWidth / 2) - guiWidth / 2;
        int centerY = (guiScreenHeight / 2) - guiHeight / 2;

        if(EditMode.showLegend)
        {
            hoveredOnLegend =this.drawLegendBorders((guiScreenWidth / 2) + 100, this.startYLegend,guiScreenWidth-1,guiScreenHeight-1,mouseX,mouseY);
            this.drawScrollTab(guiScreenWidth-3, this.scrollBarTop+scroll,guiScreenWidth-2,(this.scrollBarBottom-this.maxScroll) +scroll,hoveredOnLegend);
        }
        
        //draw the main screen mainTexture
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(centerX, centerY, 0, 0, guiWidth, guiHeight);
        //draw the title of the window
        fontRenderer.drawString(title, (guiScreenWidth / 2) - fontRenderer.getStringWidth(title) / 2, (guiScreenHeight / 2) - guiHeight / 2 - 15, 0xFFFFFF);
        mainModeWindow.drawWindow(mc);
        super.drawScreen(mouseX, mouseY, partialTicks);
        toggleLegendButton.drawButton(mc,mouseX,mouseY,partialTicks);
        
        //draw the tooltip of the hovered button
        for(GuiButton button: buttonList)
        {
            if(button.isMouseOver())
            {
                if(currHoverButton != null && currHoverButton.equals(button))
                {
                    long diff = time-firstHoverTime;
                    if(diff > tooltipTimeDelay)
                        ((GuiCustomButton) button).drawTooltip(mouseX,mouseY,width,height);
                }
                else
                {
                    currHoverButton = button;
                    firstHoverTime = time;
                }
            }
        
        }


    }
    
    
    @Override
    public void handleInput() throws IOException
    {
        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
                this.mouseHandled = false;
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre(this))) continue;
                this.handleMouseInput();
                if (this.equals(this.mc.currentScreen) && !this.mouseHandled) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Post(this));
                updateActiveButtons();
            }
        }
        
        if(debug)
        {
            if (Keyboard.isCreated())
            {
                while (Keyboard.next())
                {
                    this.keyHandled = false;
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre(this))) continue;
                    this.handleKeyboardInput();
                    if (this.equals(this.mc.currentScreen) && !this.keyHandled) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post(this));
                }
            }
        }
        else
        {
            boolean ctrlPressed = false;
            if (Keyboard.isCreated())
            {
                ctrlPressed = handleCustomKeyboardInput();


                while (Keyboard.next())
                {
                    this.keyHandled = false;
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre(this))) continue;
                    this.handleKeyboardInput();
                    if (this.equals(this.mc.currentScreen) && !this.keyHandled) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post(this));
                }
            }
    
            if(!ctrlPressed)
                Minecraft.getMinecraft().displayGuiScreen(null);


        }
    }
    
    public boolean handleCustomKeyboardInput() throws IOException
    {
        return Keyboard.isKeyDown(Keybindings.OPEN_TOOL_MENU.getKeybind().getKeyCode());
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if(Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101 && (-100+mouseButton) == Keybindings.ZOOM_IN.getKeybind().getKeyCode())
        {
//            mouseScroll(1F); //TODO: add scrolling of terrainshapes in beta1.0.1
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }
        if(Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102 && (-100+mouseButton) == Keybindings.ZOOM_OUT.getKeybind().getKeyCode())
        {
//            mouseScroll(-1F); //TODO: add scrolling of terrainshapes in beta1.0.1
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        int DWheel = Mouse.getEventDWheel();
        if(DWheel != 0)
        {
            if(DWheel < 0.0001F)
//                if(DWheel < 0.0001F && Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102)
            {
                if(hoveredOnLegend && maxScroll > 0 )
                    scrollLegend(DWheel);
                else
                {
                    EditMode.TERRAIN_SHAPE_MODE = EditMode.TERRAIN_SHAPE_MODE.rotateOpositeMode();
                    updateActiveButtons();
                }

//                super.handleMouseInput();
                return;
            }
            else if(DWheel > 0.0001F)
//            else if(DWheel > 0.0001F && Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101)
            {
                if(hoveredOnLegend && maxScroll > 0 )
                    scrollLegend(DWheel);
                else
                {
                    EditMode.TERRAIN_SHAPE_MODE = EditMode.TERRAIN_SHAPE_MODE.rotateMode();
                    updateActiveButtons();
                }

//                super.handleMouseInput();
                return;
            }

//            mouseScroll(DWheel);
        }
        super.handleMouseInput();
    }

    protected void scrollLegend(float DWheel)
    {
        scroll = (int) (scroll - DWheel/12);
        if(scroll < 0 || maxScroll < 0 )
            scroll = 0;
        else if( scroll > maxScroll)
            scroll = maxScroll;

        int currY = this.startYLegend + toggleLegendButton.height + 4;
        for (GuiLegendEntry entry: legend)
        {
            entry.setY( currY - scroll);
            currY += entry.getTotalHeight() + 3;

            if(entry.y < toggleLegendButton.y || entry.getLegendText().length == 0)
                entry.visible = false;
            else
                entry.visible = true;

        }
    }

    protected void updateActiveButtons()
    {
        for (GuiButton button : this.buttonList)
        {
            if (button instanceof GuiCircularButton)
                ((GuiCircularButton) button).active = false;
            if (button instanceof GuiPlaneButton)
                ((GuiPlaneButton) button).active = false;
            if (button instanceof GuiToggleButton && !button.displayString.equals("EDIT"))
            {
                GuiToggleButton toggleButton = ((GuiToggleButton) button);
                toggleButton.active = false;
                if(toggleButton.id >= 17 && toggleButton.id <= 24)
                {
                    EnumTerrainShapeMode shape = EnumTerrainShapeMode.values()[toggleButton.id -17];
                    toggleButton.setNameTooltip(shape.getButtonText(), shape.getAdjustModeAppropriateTooltipText(EditMode.ADJUST_MODES[EditMode.ADJUST_MODE_INDEX]));
                }


            }

        }
        updateActiveAdjustMode();
        updateActiveTerrainShapeMode();
        updateActiveOnlyTerrainMode();
        updateLegend();
    }

    protected void updateActiveAdjustMode()
    {

        if (EditMode.ADJUST_MODE_INDEX == 0)
        {
            paintAdjustButton.active = true;
            setVisiblityTerrainShape(false);
            setVisiblityOnlyTerrain(false);
        }

        else if (EditMode.ADJUST_MODE_INDEX == 1)
        {
            plantAdjustButton.active = true;
            setVisiblityTerrainShape(false);
            setVisiblityOnlyTerrain(false);
        }

        else if (EditMode.ADJUST_MODE_INDEX == 2)
        {
            paintBucketAdjustButton.active = true;
            setVisiblityTerrainShape(false);
            setVisiblityOnlyTerrain(false);
        }

        else if (EditMode.ADJUST_MODE_INDEX == 4)
        {
            smoothSharpenToolButton.active = true;
            setVisiblityTerrainShape(true);
            setVisiblityOnlyTerrain(true);

        }
        else if (EditMode.ADJUST_MODE_INDEX == 5)
        {
            levelAdjustButton.active = true;
            setVisiblityTerrainShape(true);
            setVisiblityOnlyTerrain(true);
        }
        else if (EditMode.ADJUST_MODE_INDEX == 3)
        {
            digRaiseToolButton.active = true;
            setVisiblityTerrainShape(true);
            setVisiblityOnlyTerrain(true);
        }


    }

    protected void updateActiveTerrainShapeMode()
    {
        if (EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.PLANE)
            planeButton.active = true;
        else if (EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.RISE)
            riseButton.active = true;
        else if (EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.SLOPE)
            slopeButton.active = true;
        else if (EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.HILL)
            hillButton.active = true;
        else if (EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.VALLEY)
            valleyButton.active = true;
        else if (EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.MOUNTAIN)
            mountainButton.active = true;
        else if (EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.CLIFF)
            cliffButton.active = true;
        else if (EditMode.TERRAIN_SHAPE_MODE == EnumTerrainShapeMode.STRAIGHT)
            straightButton.active = true;
    }

    protected void updateActiveOnlyTerrainMode()
    {
        if (EditMode.ONLY_TERRAIN_MODE == EnumTerrainMode.ONLY_TERRAIN)
            onlyTerrainButton.active = true;
        else if (EditMode.ONLY_TERRAIN_MODE == EnumTerrainMode.ALl)
            allBlockButton.active = true;
    }

    protected void setVisiblityTerrainShape(boolean visible)
    {
        planeButton.visible = visible;
        riseButton.visible = visible;
        slopeButton.visible = visible;
        hillButton.visible = visible;
        valleyButton.visible = visible;
        mountainButton.visible = visible;
        cliffButton.visible = visible;
        straightButton.visible = visible;
    }

    protected void setVisiblityOnlyTerrain(boolean visible)
    {
        onlyTerrainButton.visible = visible;
        allBlockButton.visible = visible;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if(debug)
        {
            System.out.println("pressed keycode: " + keyCode);
            int up = 265;
            int right = 262;
            int down = 264;
            int left = 263;

            int numpad1 = 321;
            int numpad2 = 322;
            int numpad3 = 323;
            int numpad4 = 324;
            int numpad5 = 325;
            int numpad6 = 326;
            if (keyCode == up)
            {
                colorOn = !colorOn;
                System.out.println("colorOn: " + colorOn);
            }
            if (keyCode == numpad6)
            {
                colorMatOn = !colorMatOn;
                System.out.println("colorMatOn: " + colorMatOn);
            }
//                thisButton.y--;

            else if (keyCode == down)
            {
                blendOn = !blendOn;
                System.out.println("blendOn: " + blendOn);
            }

            if (keyCode == left)
            {
                lightOn = !lightOn;
                System.out.println("lightOn: " + lightOn);
            }

            else if (keyCode == right)
            {
                depthOn = !depthOn;
                System.out.println("depthOn: " + depthOn);
            }
            else if (keyCode == numpad1)
            {
                alphaOn = !alphaOn;
                System.out.println("alphaOn: " + alphaOn);
            }
            else if (keyCode == numpad2)
            {
                otherSetting1On = !otherSetting1On;
                System.out.println("otherSetting1On: " + otherSetting1On);
            }
            else if (keyCode == numpad3)
            {
                otherSetting2On = !otherSetting2On;
                System.out.println("otherSetting2On: " + otherSetting2On);
            }
            else if (keyCode == numpad4)
            {
                otherSetting3On = !otherSetting3On;
                System.out.println("otherSetting3On: " + otherSetting3On);
            }
            else if (keyCode == numpad5)
            {
                otherSetting4On = !otherSetting4On;
                System.out.println("otherSetting4On: " + otherSetting4On);
            }
//            System.out.println("keycode "+ keyCode + " x/y " + (thisButton.x - (guiScreenWidth / 2)) + "/" + (thisButton.y-(guiScreenHeight / 2)));
        }

        if(keyCode == 1)
            Minecraft.getMinecraft().displayGuiScreen(null);
        else if(keyCode == Keybindings.OPEN_REPORT_SCREEN.getKeybind().getKeyCode())
        {
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.OPEN_REPORT_SCREEN,"open ReportScreen");
            Report.saveScreenshot();

            Minecraft.getMinecraft().displayGuiScreen(new ReportScreen(this));
        }
        else if(Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101 && keyCode == Keybindings.ZOOM_IN.getKeybind().getKeyCode())
        {
//            mouseScroll(6);
            return ;
        }
        else if(Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102 && keyCode == Keybindings.ZOOM_OUT.getKeybind().getKeyCode())
        {
//            mouseScroll(-6);
            return ;
        }
        else if(keyCode == Keybindings.CHANGE_MODE.getKeybind().getKeyCode())
        {
            EnumMainMode prevMode = AdvCreation.getMode();
            AdvCreation.setMode(EnumMainMode.BUILD);
            //System.out.println(AdvCreation.mode);
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.CHANGE_MODE,prevMode.name() + " to " + AdvCreation.getMode().name());
            Minecraft.getMinecraft().displayGuiScreen(new GuiToolModeSelectionScreen());
        }
        else if(keyCode == Keybindings.CHANGE_TOOL_MODE.getKeybind().getKeyCode())
        {
            String prevtoolmode = EditMode.ADJUST_MODE.toolModeName;
            EditMode.rotateAdjustMode();
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.CHANGE_TOOL_MODE,prevtoolmode + " to  next" );
        }
        else if(keyCode == Keybindings.CHANGE_DIR_SHAPE_MODE.getKeybind().getKeyCode())
        {
            String prevDir = EditMode.TERRAIN_SHAPE_MODE.name();
            EditMode.TERRAIN_SHAPE_MODE = EditMode.TERRAIN_SHAPE_MODE.rotateMode();
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.CHANGE_DIR_SHAPE_MODE,prevDir + " to " + EditMode.TERRAIN_SHAPE_MODE.name());
        }
        else if(keyCode == Keybindings.CHANGE_FILL_TERRAIN_MODE.getKeybind().getKeyCode())
        {
            String prevFill = EditMode.ONLY_TERRAIN_MODE.buttonText;
            EditMode.ONLY_TERRAIN_MODE = EditMode.ONLY_TERRAIN_MODE.rotateMode();
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.CHANGE_FILL_TERRAIN_MODE,prevFill + " to " + EditMode.ONLY_TERRAIN_MODE.buttonText);
        }
        else if(keyCode == Keybindings.HOTKEY_TOOL_1.getKeybind().getKeyCode())
        {
            if(AdvCreation.getMode() == EnumMainMode.BUILD)
                BuildMode.changeToolModeTo(0);
            else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                EditMode.changeAdjustModeTo(0);
        }
        else if( keyCode == Keybindings.HOTKEY_TOOL_2.getKeybind().getKeyCode())
        {
            if(AdvCreation.getMode() == EnumMainMode.BUILD)
                BuildMode.changeToolModeTo(1);
            else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                EditMode.changeAdjustModeTo(1);
        }
        else if( keyCode == Keybindings.HOTKEY_TOOL_3.getKeybind().getKeyCode())
        {
            if(AdvCreation.getMode() == EnumMainMode.BUILD)
                BuildMode.changeToolModeTo(2);
            else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                EditMode.changeAdjustModeTo(2);
        }
        else if( keyCode == Keybindings.HOTKEY_TOOL_4.getKeybind().getKeyCode())
        {
            if(AdvCreation.getMode() == EnumMainMode.BUILD)
                BuildMode.changeToolModeTo(3);
            else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                EditMode.changeAdjustModeTo(3);
        }
        else if( keyCode == Keybindings.HOTKEY_TOOL_5.getKeybind().getKeyCode())
        {
            if(AdvCreation.getMode() == EnumMainMode.BUILD)
                BuildMode.changeToolModeTo(4);
            else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                EditMode.changeAdjustModeTo(4);
        }
        else if( keyCode == Keybindings.HOTKEY_TOOL_6.getKeybind().getKeyCode())
        {
            if(AdvCreation.getMode() == EnumMainMode.BUILD)
                BuildMode.changeToolModeTo(5);
            else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                EditMode.changeAdjustModeTo(5);
        }
        else if( keyCode == Keybindings.HOTKEY_TOOL_7.getKeybind().getKeyCode())
        {
            if(AdvCreation.getMode() == EnumMainMode.BUILD)
                BuildMode.changeToolModeTo(6);
            else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                EditMode.changeAdjustModeTo(6);
        }
        else if( keyCode == Keybindings.HOTKEY_TOOL_8.getKeybind().getKeyCode())
        {
            if(AdvCreation.getMode() == EnumMainMode.BUILD)
                BuildMode.changeToolModeTo(7);
            else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                EditMode.changeAdjustModeTo(7);
        }
        else if( keyCode == Keybindings.HOTKEY_TOOL_9.getKeybind().getKeyCode())
        {
            if(AdvCreation.getMode() == EnumMainMode.BUILD)
                BuildMode.changeToolModeTo(8);
            else if (AdvCreation.getMode() == EnumMainMode.EDIT)
                EditMode.changeAdjustModeTo(8);
        }

        updateActiveButtons();
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if(button.id == PAINT_BUTTON_ID)
            EditMode.changeAdjustModeTo(0);
        else if(button.id == PLANT_BUTTON_ID)
            EditMode.changeAdjustModeTo(1);
        else if(button.id == PAINTBUCKET_BUTTON_ID)
            EditMode.changeAdjustModeTo(2);
        else if(button.id == SMOOTH_BUTTON_ID)
            EditMode.changeAdjustModeTo(3);
        else if(button.id == CIRCLE_BUTTON_ID)
            EditMode.changeAdjustModeTo(4);
        else if(button.id == LEVEL_BUTTON_ID)
            EditMode.changeAdjustModeTo(5);
        else if(button.id == BUILD_BUTTON_ID)
        {
            AdvCreation.setMode(EnumMainMode.BUILD);
            Minecraft.getMinecraft().displayGuiScreen(new GuiToolModeSelectionScreen());
        }
        else if(button.id == EDIT_BUTTON_ID)
        {
            GuiToggleButton thisButton = (GuiToggleButton) button;
            thisButton.active = true;
        }
        else if(button.id == TERRAIN_SHAPE_PLANE_ID)
            EditMode.TERRAIN_SHAPE_MODE = EnumTerrainShapeMode.PLANE;
        else if(button.id == TERRAIN_SHAPE_RISE_ID)
            EditMode.TERRAIN_SHAPE_MODE = EnumTerrainShapeMode.RISE;
        else if(button.id == TERRAIN_SHAPE_SLOPE_ID)
            EditMode.TERRAIN_SHAPE_MODE = EnumTerrainShapeMode.SLOPE;
        else if(button.id == TERRAIN_SHAPE_HILL_ID)
            EditMode.TERRAIN_SHAPE_MODE = EnumTerrainShapeMode.HILL;
        else if(button.id == TERRAIN_SHAPE_VALLEY_ID)
            EditMode.TERRAIN_SHAPE_MODE = EnumTerrainShapeMode.VALLEY;
        else if(button.id == TERRAIN_SHAPE_MOUNTAIN_ID)
            EditMode.TERRAIN_SHAPE_MODE = EnumTerrainShapeMode.MOUNTAIN;
        else if(button.id == TERRAIN_SHAPE_CLIFF_ID)
            EditMode.TERRAIN_SHAPE_MODE = EnumTerrainShapeMode.CLIFF;
        else if(button.id == TERRAIN_SHAPE_STRAIGHT_ID)
            EditMode.TERRAIN_SHAPE_MODE = EnumTerrainShapeMode.STRAIGHT;
        else if(button == toggleLegendButton)
        {
            EditMode.showLegend = !EditMode.showLegend;
            updateLegend();
        }
        else if(button == allBlockButton)
        {
            EditMode.ONLY_TERRAIN_MODE = EnumTerrainMode.ALl;
        }
        else if(button == onlyTerrainButton)
        {
            EditMode.ONLY_TERRAIN_MODE = EnumTerrainMode.ONLY_TERRAIN;
        }
        if (button == toolDisableButton || button == toolDisableButton.tab)
        {

            ToolEnabled.toggleToolsEnabled();

            if(ConfigurationHandler.general.TOOLS_ENABLED)
                toolDisableButton.setNameTooltip("TOGGLE TOOLS","Toggle adv. creation tools (now: ON)");
            else
                toolDisableButton.setNameTooltip("TOGGLE TOOLS","Toggle adv. creation tools (now: OFF)");
            toolDisableButton.stateIsOn = ConfigurationHandler.general.TOOLS_ENABLED;
        }
    
        updateActiveButtons();
        
        super.actionPerformed(button);
    }

    private void updateLegend()
    {
        int x_offset = 100;
        int maxLegendEntries = legend.size();
        Color currColor;
        String currText;

        this.startYLegend = guiScreenHeight -20;
        if(EditMode.showLegend)
            this.startYLegend = (guiScreenHeight / 2) -10;

        int currY = startYLegend;
        scrollBarTop = currY;

        legendTitle = "Legend for " + EditMode.ADJUST_MODES[EditMode.ADJUST_MODE_INDEX].buttonText;
        if(toggleLegendButton != null)
        {
            toggleLegendButton.displayString = legendTitle;
            toggleLegendButton.formatCurrText();
            toggleLegendButton.setNameTooltip("LEGEND","Tells you what all the colored cubes mean for " + EditMode.ADJUST_MODES[EditMode.ADJUST_MODE_INDEX].buttonText + " tool");


            if(EditMode.showLegend)
            {
                toggleLegendButton.y = currY;
                currY += toggleLegendButton.height;
            }
            else
            {
                toggleLegendButton.y = guiScreenHeight - toggleLegendButton.height;
            }

        }

        scrollBarTop = currY;
        currY += 2;
        for (int i = 0; i < maxLegendEntries; i++)
        {
            GuiLegendEntry currEntry = legend.get(i);

            if(EditMode.showLegend && i < EditMode.getCurrLegendEntryCount())
            {
                currColor =  EditMode.getLegendColor(i);
                currText = EditMode.getLegendText(currColor);
                currEntry.setColor(currColor);
                currEntry.setLegendText(new String[][]{{currText}});
                currEntry.setY( currY - scroll);
                currEntry.formatCurrText();

                currY += currEntry.getTotalHeight() + 3;
                if(currEntry.y < toggleLegendButton.y)
                    currEntry.visible = false;
                else
                    currEntry.visible = true;
            }
            else
                currEntry.visible = false;


        }
        scrollBarBottom = this.guiScreenHeight;
        maxScroll = currY - scrollBarBottom;


        if(scroll < 0 || maxScroll < 0 )
            scroll = 0;
        else if(scroll > maxScroll)
            scroll = maxScroll;
    }


    private boolean drawLegendBorders(int startX, int startY, int endX, int endY, int mouseX, int mouseY)
    {
        int color = Color.BLACK.getRGB();

        boolean sideHovered = mouseX >= startX && mouseY >= startY && mouseX < endX && mouseY < endY;
        if(sideHovered)
            color = -6250336;


        this.drawVerticalLine(startX,startY,endY,color);
        this.drawVerticalLine(endX,startY,endY,color);
        this.drawHorizontalLine(startX,endX,startY,color);
        this.drawHorizontalLine(startX,endX,endY,color);
        GlStateManager.color(1.0f, 1.0f,1.0f,1.0f);

        return sideHovered;
    }

    private boolean drawScrollTab( int startX, int startY, int endX, int endY, boolean sideHovered)
    {
        int color = Color.BLACK.getRGB();

        if(sideHovered)
            color = -6250336;

        this.drawVerticalLine(startX,startY,endY,color);
        this.drawVerticalLine(endX,startY,endY,color);
        this.drawHorizontalLine(startX,endX,startY,color);
        this.drawHorizontalLine(startX,endX,endY,color);
        GlStateManager.color(1.0f, 1.0f,1.0f,1.0f);
        return sideHovered;
    }
}
