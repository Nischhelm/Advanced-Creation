package com.deadtiger.advcreation.client.gui.gui_screen.selection_wheel;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.tool_mode.CircleToolMode;
import com.deadtiger.advcreation.build_mode.tool_mode.CurveToolMode;
import com.deadtiger.advcreation.build_mode.tool_mode.LineToolMode;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.build_mode.utility.EnumFillMode;
import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.*;
import com.deadtiger.advcreation.client.gui.gui_screen.reportScreen.ReportScreen;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.client.input.KeyInputHandler;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.player.ToolEnabled;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.report.Report;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

import static com.deadtiger.advcreation.client.gui.gui_overlay.ToolDisableGuiOverlay.calculateToolDisableButtonY;

public class GuiToolModeSelectionScreen extends GuiScreen
{
    public static int mouseXBeforeOpening = 100;
    public static int mouseYBeforeOpening = 100;

    ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/gui/selectionwheel1.png");
    // size of the main gui window
    private int guiWidth = 200;
    private int guiHeight = 200;
    
    //size of the menu screen? is always a low resolution? because ... why not?
    private int guiScreenWidth = 480;
    private int guiScreenHeight = 270;
    
    //gui screen texts
    private String title = "Select a Tool";
    
    protected ResourceLocation BASE_TOOL_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/selectionwheelbuttons_9_good.png");
    protected ResourceLocation ACTIVE_TOOL_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/activewheelbuttons_9_good.png");
    protected ResourceLocation HOVER_TOOL_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/selectedwheelbuttons_9_good.png");
    
    //button on the Wheel GUI
    private GuiCustomButton singleToolButton;
    private static final int SINGE_BUTTON_ID = 0;
    private GuiCustomButton lineToolButton;
    private static final int LINE_BUTTON_ID = 1;
    private GuiCustomButton pullToolButton;
    private static final int PULL_BUTTON_ID = 2;
    private GuiCustomButton curveToolButton;
    private static final int CURVE_BUTTON_ID = 3;
    private GuiCustomButton rectangleToolButton;
    private static final int RECTANGLE_BUTTON_ID = 4;
    private GuiCustomButton circleToolButton;
    private static final int CIRCLE_BUTTON_ID = 5;
    private GuiCustomButton copyToolButton;
    private static final int COPY_BUTTON_ID = 12;
    private GuiCustomButton moveToolButton;
    private static final int MOVE_BUTTON_ID = 13;
    private GuiCustomButton fillGapToolButton;
    private static final int FILLGAP_BUTTON_ID = 14;
    
    
    //buttons to change the direction
    protected ResourceLocation BASE_DIR_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/base_direction_selection.png");
    protected ResourceLocation ACTIVE_DIR_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/active_direction_selection.png");
    protected ResourceLocation HOVER_DIR_TEXTURES = new ResourceLocation(Reference.MODID,"textures/gui/hover_direction_selection.png");
    
    private GuiCustomButton freeDirButton;
    private static final int FREE_DIR_BUTTON_ID = 6;
    private GuiCustomButton autoDirButton;
    private static final int AUTO_DIR_BUTTON_ID = 7;
    private GuiCustomButton groundDirButton;
    private static final int GRND_DIR_BUTTON_ID = 8;
    private GuiCustomButton XYDirButton;
    private static final int XY_DIR_BUTTON_ID = 9;
    private GuiCustomButton ZYDirButton;
    private static final int ZY_DIR_BUTTON_ID = 10;
    private GuiCustomButton XZDirButton;
    private static final int XZ_DIR_BUTTON_ID = 11;
    
    private GuiCustomButton fillButton;
    private static final int FILL_BUTTON_ID = 15;
    private GuiCustomButton noFillButton;
    private static final int NO_FILL_BUTTON_ID = 16;

    private GuiFreeToggleButton lineSnapCenterButton;
    private static final int LINE_SNAP_BUTTON_ID = 19;
    private GuiFreeToggleButton curveSnapCenterButton;
    private static final int CURVE_SNAP_BUTTON_ID = 20;
    private GuiFreeToggleButton circleSnapCenterButton;
    private static final int CIRCLE_SNAP_BUTTON_ID = 21;
    
    //keeps the current hovered button to support the timed tooltip
    private GuiButton currHoverButton = null;
    private long firstHoverTime = 0;
    private int tooltipTimeDelay = 500;
    
    public boolean debug = false;
    private int thisButtonIndex = 0;

    // the window
    private GuiCustomWindow mainModeWindow;
    // the buttons in the window
    private GuiToggleButton buildButton, editButton;
    private static final int BUILD_BUTTON_ID = 17, EDIT_BUTTON_ID = 18, TOOL_ENABLE_BUTTON_ID = 19;
    private GuiOnOffButton toolDisableButton;
    @Override
    public void initGui()
    {
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        guiScreenWidth = scaledresolution.getScaledWidth();
        guiScreenHeight = scaledresolution.getScaledHeight();
        
        buttonList.clear();
        initCenterSnapButtons();
        initToolModeButtons();
        initDirModeButtons();
        initFillButtons();
        updateActiveButtons();



        //initialise the buttons for switching mainmodes
        int buttonWidth = 50, buttonHeight = 20;
        mainModeWindow = new GuiCustomWindow(0,0,117,30);
        buildButton = new GuiToggleButton(BUILD_BUTTON_ID,5,5,buttonWidth,buttonHeight,"BUILD");
        buildButton.active = true;
        buildButton.setNameTooltip("BUILD","Placing and deleting blocks");

        editButton =new GuiToggleButton(EDIT_BUTTON_ID,61,5,buttonWidth,buttonHeight,"EDIT");
        editButton.setNameTooltip("EDIT","Edit the world");

        buttonList.add(buildButton);
        buttonList.add(editButton);

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

        super.initGui();
    }
    
    public void initToolModeButtons()
    {
        int innerRadius = 45;
        int outerRadius = 95;
        
        //top wheel button custom button
        singleToolButton = new GuiCircularButton(SINGE_BUTTON_ID,
            (guiScreenWidth / 2) - 30, (guiScreenHeight / 2) - 96,0,0,
            60, 56, "Single",70,110);
        initBigCircularButton((GuiCircularButton) singleToolButton,innerRadius,outerRadius);
        singleToolButton.setNameTooltip(BuildMode.TOOL_MODES[0].buttonText,BuildMode.TOOL_MODES[0].tooltipText);
        buttonList.add(singleToolButton);
    
        //right-up wheel button custom button
        lineToolButton = new GuiCircularButton(LINE_BUTTON_ID,
            (guiScreenWidth / 2) + 19, (guiScreenHeight / 2)-88,70,0,
            65, 67, "Line",30,70);
        initBigCircularButton((GuiCircularButton) lineToolButton,innerRadius,outerRadius);
        lineToolButton.setNameTooltip(BuildMode.TOOL_MODES[1].buttonText,BuildMode.TOOL_MODES[1].tooltipText);
        buttonList.add(lineToolButton);
    
        //left-up wheel button custom button
        fillGapToolButton = new GuiCircularButton(FILLGAP_BUTTON_ID,(guiScreenWidth / 2) - 82, (guiScreenHeight / 2)-88,140,0,
            65, 67, "FillGap",110,150);
        initBigCircularButton((GuiCircularButton) fillGapToolButton,innerRadius,outerRadius);
        fillGapToolButton.setNameTooltip(BuildMode.TOOL_MODES[8].buttonText,BuildMode.TOOL_MODES[8].tooltipText);
        buttonList.add(fillGapToolButton);
    
        //right-right-top wheel button custom button
        rectangleToolButton = new GuiCircularButton(RECTANGLE_BUTTON_ID,
            (guiScreenWidth / 2) + 40, (guiScreenHeight / 2)-43,60,75,
            58, 59, "Rect",350,30);
        initBigCircularButton((GuiCircularButton) rectangleToolButton,innerRadius,outerRadius);
        rectangleToolButton.setNameTooltip(BuildMode.TOOL_MODES[2].buttonText,BuildMode.TOOL_MODES[2].tooltipText);
        buttonList.add(rectangleToolButton);
        
        
        //right-right-bottom wheel button custom button
        curveToolButton = new GuiCircularButton(CURVE_BUTTON_ID,
            (guiScreenWidth / 2) +29, (guiScreenHeight / 2)+9,190,75,
            66, 62, "Curve",310,350);
        initBigCircularButton((GuiCircularButton) curveToolButton,innerRadius,outerRadius);
        curveToolButton.setNameTooltip(BuildMode.TOOL_MODES[3].buttonText,BuildMode.TOOL_MODES[3].tooltipText);
        buttonList.add(curveToolButton);
    
        
        //right-down wheel button custom button
        circleToolButton = new GuiCircularButton(CIRCLE_BUTTON_ID,
            (guiScreenWidth / 2) +3, (guiScreenHeight / 2)+34,64,150, 58,62, "Circle",270,310);
        initBigCircularButton((GuiCircularButton) circleToolButton,innerRadius,outerRadius);
        circleToolButton.setNameTooltip(BuildMode.TOOL_MODES[4].buttonText,BuildMode.TOOL_MODES[4].tooltipText);
        buttonList.add(circleToolButton);
        
        
        //left-down wheel button custom button
        pullToolButton = new GuiCircularButton(PULL_BUTTON_ID,
            (guiScreenWidth / 2) - 60, (guiScreenHeight / 2) + 34,0,150, 58,62, "Pull",230,270);
        initBigCircularButton((GuiCircularButton) pullToolButton,innerRadius,outerRadius);
        pullToolButton.setNameTooltip(BuildMode.TOOL_MODES[5].buttonText,BuildMode.TOOL_MODES[5].tooltipText);
        buttonList.add(pullToolButton);
        
        
        //left-left-bottom wheel button custom button
        copyToolButton = new GuiCircularButton(COPY_BUTTON_ID,
            (guiScreenWidth / 2) - 95, (guiScreenHeight / 2)+ 9,120,75,66,62, "Copy",190,230);
        initBigCircularButton((GuiCircularButton) copyToolButton,innerRadius,outerRadius);
        copyToolButton.setNameTooltip(BuildMode.TOOL_MODES[6].buttonText,BuildMode.TOOL_MODES[6].tooltipText);
        buttonList.add(copyToolButton);
        
        //left-left-top wheel button custom button
        moveToolButton = new GuiCircularButton(MOVE_BUTTON_ID,
            (guiScreenWidth / 2) -97, (guiScreenHeight / 2)-44,0,75,58,59, "Move/Del",150,190);
        initBigCircularButton((GuiCircularButton) moveToolButton,innerRadius,outerRadius);
        moveToolButton.setNameTooltip(BuildMode.TOOL_MODES[7].buttonText,BuildMode.TOOL_MODES[7].tooltipText);
        buttonList.add(moveToolButton);
       
        
    }
    
    private void initBigCircularButton(GuiCircularButton button, int innerRadius, int outerRadius)
    {
        button.setCircleCoord(guiScreenWidth/2,guiScreenHeight/2);
        button.setRadius(innerRadius, outerRadius);
        button.setTextures(BASE_TOOL_TEXTURES,HOVER_TOOL_TEXTURES,ACTIVE_TOOL_TEXTURES);
    }
    
    private void initSmallCircularButton(GuiCircularButton button, int innerRadius, int outerRadius)
    {
        button.setCircleCoord(guiScreenWidth/2,guiScreenHeight/2);
        button.setRadius(innerRadius, outerRadius);
        button.setTextures(BASE_DIR_TEXTURES,HOVER_DIR_TEXTURES,ACTIVE_DIR_TEXTURES);
    }
    
    
    public void initDirModeButtons()
    {
        int innerRadius = 25;
        int outerRadius = 40;
        
        //right-up wheel button custom button
        autoDirButton = new GuiCircularButton(AUTO_DIR_BUTTON_ID,
            (guiScreenWidth / 2) - 40, (guiScreenHeight / 2) - 40,50,0,
            40, 58, "Auto     ",90,210);
        initSmallCircularButton((GuiCircularButton) autoDirButton,innerRadius,outerRadius);
        autoDirButton.setNameTooltip(EnumDirectionMode.AUTO.buttonText,EnumDirectionMode.AUTO.tooltipText);
        buttonList.add(autoDirButton);
    
        //left-up wheel button custom button
        freeDirButton = new GuiCircularButton(FREE_DIR_BUTTON_ID,
            (guiScreenWidth / 2), (guiScreenHeight / 2) - 40,100,0,
            40, 58, "     Free",330,90);
        initSmallCircularButton((GuiCircularButton) freeDirButton,innerRadius,outerRadius);
        freeDirButton.setNameTooltip(EnumDirectionMode.FREE.buttonText,EnumDirectionMode.FREE.tooltipText);
        buttonList.add(freeDirButton);
    
    
        //left-up wheel button custom button
        groundDirButton = new GuiCircularButton(GRND_DIR_BUTTON_ID,
            (guiScreenWidth / 2)-35, (guiScreenHeight / 2) + 11,50,75,
            71, 29, "Ground",210,330);
        initSmallCircularButton((GuiCircularButton) groundDirButton,innerRadius,outerRadius);
        groundDirButton.setNameTooltip(EnumDirectionMode.GROUND.buttonText,EnumDirectionMode.GROUND.tooltipText);
        buttonList.add(groundDirButton);
        
        //XY plane custom button
        XYDirButton = new GuiPlaneButton(XY_DIR_BUTTON_ID,
            (guiScreenWidth / 2) - 18, (guiScreenHeight / 2)-16,0,0,
            18, 25, "");
        XYDirButton.setNameTooltip(EnumDirectionMode.XY.buttonText,EnumDirectionMode.XY.tooltipText);
        buttonList.add(XYDirButton);
    
        //ZY plane custom button
        ZYDirButton = new GuiPlaneButton(ZY_DIR_BUTTON_ID,
            (guiScreenWidth / 2) , (guiScreenHeight / 2)-16,25,0,
            18, 25, "");
        ((GuiPlaneButton) ZYDirButton).plane = EnumDirectionMode.ZY;
        ZYDirButton.setNameTooltip(EnumDirectionMode.ZY.buttonText,EnumDirectionMode.ZY.tooltipText);
        buttonList.add(ZYDirButton);
        
        //XZ plane custom button
        XZDirButton = new GuiPlaneButton(XZ_DIR_BUTTON_ID,
            (guiScreenWidth / 2) - 18, (guiScreenHeight / 2),0,31,
            36, 19, "");
        ((GuiPlaneButton)XZDirButton).plane = EnumDirectionMode.XZ;
        XZDirButton.setNameTooltip(EnumDirectionMode.XZ.buttonText,EnumDirectionMode.XZ.tooltipText);
        buttonList.add(XZDirButton);
        
    }
    
    public void initFillButtons()
    {
        fillButton = new GuiToggleButton(FILL_BUTTON_ID,(guiScreenWidth / 2) + 110, (guiScreenHeight / 2)- 20,50,20, "FILL");
        fillButton.setNameTooltip(EnumFillMode.FILL.buttonText,EnumFillMode.FILL.tooltipText);
        buttonList.add(fillButton);
        
        noFillButton = new GuiToggleButton(NO_FILL_BUTTON_ID,(guiScreenWidth / 2) + 110, (guiScreenHeight / 2),50,20, "NO FILL");
        noFillButton.setNameTooltip(EnumFillMode.NO_FILL.buttonText,EnumFillMode.NO_FILL.tooltipText);
        buttonList.add(noFillButton);
    }

    public void initCenterSnapButtons()
    {
        lineSnapCenterButton = new GuiFreeToggleButton(LINE_SNAP_BUTTON_ID,new ResourceLocation(Reference.MODID,"textures/gui/undo_gui_overlay.png"),(guiScreenWidth / 2) + 19 + 25, (guiScreenHeight / 2)-88 + 18,20,20
        ,120,120,140,120,120,90,140,90);
        lineSnapCenterButton.setActive(ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER);
        if(((GuiFreeToggleButton)lineSnapCenterButton ).isActive())
            lineSnapCenterButton.displayString = "Current State: Cursor snaps to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";
        else
            lineSnapCenterButton.displayString = "Current State: Cursor DOESN'T snap to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";

        this.buttonList.add(lineSnapCenterButton);

        curveSnapCenterButton = new GuiFreeToggleButton(CURVE_SNAP_BUTTON_ID,new ResourceLocation(Reference.MODID,"textures/gui/undo_gui_overlay.png"),(guiScreenWidth / 2) +29 + 30, (guiScreenHeight / 2)+9 + 15,20,20
       ,120,120,140,120,120,90,140,90);
        curveSnapCenterButton.setActive(ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER);
        if(((GuiFreeToggleButton)curveSnapCenterButton ).isActive())
            curveSnapCenterButton .displayString = "Current State: Cursor snaps to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";
        else
            curveSnapCenterButton .displayString = "Current State: Cursor DOESN'T snap to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";

        this.buttonList.add(curveSnapCenterButton);

        circleSnapCenterButton = new GuiFreeToggleButton(CIRCLE_SNAP_BUTTON_ID,new ResourceLocation(Reference.MODID,"textures/gui/undo_gui_overlay.png"),(guiScreenWidth / 2) +8 + 10, (guiScreenHeight / 2)+70 + 10,20,20
        ,120,120,140,120,120,90,140,90);
        circleSnapCenterButton.setActive(ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER);
        if(((GuiFreeToggleButton)circleSnapCenterButton ).isActive())
            circleSnapCenterButton .displayString = "Current State: Cursor snaps to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";
        else
            circleSnapCenterButton .displayString = "Current State: Cursor DOESN'T snap to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";

        this.buttonList.add(circleSnapCenterButton);
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

        //draw the main screen mainTexture
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(centerX, centerY, 0, 0, guiWidth, guiHeight);
        //draw the title of the window
        fontRenderer.drawString(title, (guiScreenWidth / 2) - fontRenderer.getStringWidth(title) / 2, (guiScreenHeight / 2) - guiHeight / 2 - 15, 0xFFFFFF);

        mainModeWindow.drawWindow(mc);

        super.drawScreen(mouseX, mouseY, partialTicks);

        //draw the tooltip of the hovered button



        for (GuiButton button : buttonList)
        {

            if (button.isMouseOver())
            {
                //making sure the snapbuttons inside the circular buttons are also shown
                GuiButton newButton = button;
                if(button == lineToolButton && lineSnapCenterButton.isMouseOver())
                {
                    newButton = lineSnapCenterButton;
                }
                if(button == curveToolButton && curveSnapCenterButton.isMouseOver())
                {
                    newButton = curveSnapCenterButton;
                }
                if(button == circleToolButton && circleSnapCenterButton.isMouseOver())
                {
                    newButton = circleSnapCenterButton;
                }


                if (currHoverButton != null && currHoverButton.equals(newButton))
                {
                    long diff = time - firstHoverTime;
                    if (diff > tooltipTimeDelay)
                    {
                        if(newButton instanceof GuiCustomButton)
                            ((GuiCustomButton) newButton).drawTooltip(mouseX, mouseY, width, height);
                        else if(newButton instanceof GuiFreeToggleButton)
                            CustomGuiUtils.drawHoveringText(newButton.displayString, mouseX-7, mouseY,width, height,-1,fontRenderer);
                    }

                }
                else
                {
                    currHoverButton = newButton;
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
            boolean keyBindIsDown= checkIfKeyBindIsDown();
            if(!keyBindIsDown)
                Minecraft.getMinecraft().displayGuiScreen(null);
            if (Keyboard.isCreated())
            {
                while (Keyboard.next())
                {
                    this.keyHandled = false;
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre(this)))
                        continue;
                    this.handleKeyboardInput();
                    if (this.equals(this.mc.currentScreen) && !this.keyHandled)
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post(this));
                }
            }
        }
    }
    
    public boolean checkIfKeyBindIsDown() throws IOException
    {
        boolean isHoldingToolSelectionWheelKey = false;
        if (Keyboard.isCreated())
        {
            try
            {
                isHoldingToolSelectionWheelKey = Keyboard.isKeyDown(Keybindings.OPEN_TOOL_MENU.getKeybind().getKeyCode());
            }
            catch(IndexOutOfBoundsException e)
            {

            }
        }
        if (Mouse.isCreated())
        {
            if (!isHoldingToolSelectionWheelKey)
            {
                isHoldingToolSelectionWheelKey = Mouse.isButtonDown(Keybindings.OPEN_TOOL_MENU.getKeybind().getKeyCode() + 100);
            }
        }


        return isHoldingToolSelectionWheelKey;
    }
    
    protected void updateActiveButtons()
    {
        for (GuiButton button : buttonList) {
            if (button instanceof GuiCircularButton)
                ((GuiCircularButton) button).active = false;
            if (button instanceof GuiPlaneButton)
                ((GuiPlaneButton) button).active = false;
            if(button instanceof GuiToggleButton && !button.displayString.equals("BUILD"))
                ((GuiToggleButton) button).active = false;
        }
         updateActiveToolMode();
        updateActiveDirMode();
        updateActiveFillMode();
        updateActiveSnapToCenterMode();
    }

    protected void updateActiveSnapToCenterMode()
    {
        if(BuildMode.TOOL_MODES[BuildMode.TOOLMODE_INDEX] instanceof LineToolMode)
        {
            lineSnapCenterButton.visible = true;
            curveSnapCenterButton.visible = false;
            circleSnapCenterButton.visible = false;
            lineSnapCenterButton.setActive(ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER);
        }
        else if(BuildMode.TOOL_MODES[BuildMode.TOOLMODE_INDEX] instanceof CurveToolMode)
        {
            curveSnapCenterButton.visible = true;
            lineSnapCenterButton.visible = false;
            circleSnapCenterButton.visible = false;
            curveSnapCenterButton.setActive(ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER);
        }
        else if(BuildMode.TOOL_MODES[BuildMode.TOOLMODE_INDEX] instanceof CircleToolMode)
        {
            curveSnapCenterButton.visible = false;
            lineSnapCenterButton.visible = false;
            circleSnapCenterButton.visible = true;
            curveSnapCenterButton.setActive(ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER);
        }
        else
        {
            lineSnapCenterButton.visible = false;
            curveSnapCenterButton.visible = false;
            circleSnapCenterButton.visible = false;
        }
    }
    
    protected void updateActiveToolMode()
    {

        if(BuildMode.TOOLMODE_INDEX == 0)
            singleToolButton.active = true;
        else if(BuildMode.TOOLMODE_INDEX == 1)
            lineToolButton.active = true;
        else if(BuildMode.TOOLMODE_INDEX == 2)
            rectangleToolButton.active = true;
        else if(BuildMode.TOOLMODE_INDEX == 3)
            curveToolButton.active = true;
        else if(BuildMode.TOOLMODE_INDEX == 4)
            circleToolButton.active = true;
        else if(BuildMode.TOOLMODE_INDEX == 5)
            pullToolButton.active = true;
        else if(BuildMode.TOOLMODE_INDEX == 6)
            copyToolButton.active = true;
        else if(BuildMode.TOOLMODE_INDEX == 7)
            moveToolButton.active = true;
        else if(BuildMode.TOOLMODE_INDEX == 8)
            fillGapToolButton.active = true;
        
        
    }
    
    protected void updateActiveDirMode()
    {
        
        if(BuildMode.DIRECTION_MODE == EnumDirectionMode.GROUND)
            groundDirButton.active = true;
        else if(BuildMode.DIRECTION_MODE == EnumDirectionMode.FREE)
            freeDirButton.active = true;
        else if(BuildMode.DIRECTION_MODE == EnumDirectionMode.AUTO)
            autoDirButton.active = true;
        else if(BuildMode.DIRECTION_MODE == EnumDirectionMode.XY)
            XYDirButton.active = true;
        else if(BuildMode.DIRECTION_MODE == EnumDirectionMode.ZY)
             ZYDirButton.active = true;
        else if(BuildMode.DIRECTION_MODE == EnumDirectionMode.XZ)
             XZDirButton.active = true;
    }
    
    protected void updateActiveFillMode()
    {
        if(BuildMode.FILL_MODE == EnumFillMode.FILL)
            fillButton.active = true;
        else if(BuildMode.FILL_MODE == EnumFillMode.NO_FILL)
            noFillButton.active = true;
    }
    
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if(debug)
        {
            int up = 200;
            int right= 203;
            int down = 208;
            int left= 205;
            GuiButton thisButton = buttonList.get(thisButtonIndex);
            if(keyCode == up)
                thisButton.y--;
            else if(keyCode == down)
                thisButton.y++;
            if(keyCode == left)
                thisButton.x--;
            else if(keyCode == right)
                thisButton.x++;
            System.out.println( "keycode "+ keyCode + " x/y " + (thisButton.x - (guiScreenWidth / 2)) + "/" + (thisButton.y-(guiScreenHeight / 2)) +" button " + thisButton.displayString );
        }

        if(keyCode == 1)
            Minecraft.getMinecraft().displayGuiScreen(null);
//        else if(keyCode == 48)
//            rotateThisButton();
        else if(keyCode == Keybindings.OPEN_REPORT_SCREEN.getKeybind().getKeyCode())
        {
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.OPEN_REPORT_SCREEN,"open ReportScreen");
            Report.saveScreenshot();

            Minecraft.getMinecraft().displayGuiScreen(new ReportScreen(this));
        }
        else if(keyCode == Keybindings.CHANGE_MODE.getKeybind().getKeyCode())
        {
            EnumMainMode prevMode = AdvCreation.getMode();
            AdvCreation.setMode(EnumMainMode.EDIT);
            //System.out.println(AdvCreation.mode);
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.CHANGE_MODE,prevMode.name() + " to " + AdvCreation.getMode().name());
            Minecraft.getMinecraft().displayGuiScreen(new GuiAdjustModeSelectionScreen());

        }
        else if(keyCode == Keybindings.CHANGE_TOOL_MODE.getKeybind().getKeyCode())
        {
            String prevAdjustMode = BuildMode.TOOLMODE.toolModeName;
            BuildMode.rotateToolMode();
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.CHANGE_TOOL_MODE,prevAdjustMode + " to  next" );
        }
        else if(keyCode == Keybindings.CHANGE_DIR_SHAPE_MODE.getKeybind().getKeyCode())
        {
            String prevDir = BuildMode.DIRECTION_MODE.name();
            BuildMode.DIRECTION_MODE = BuildMode.DIRECTION_MODE.rotateMode();
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.CHANGE_DIR_SHAPE_MODE,prevDir + " to " + BuildMode.DIRECTION_MODE.name());
        }
        else if(keyCode == Keybindings.CHANGE_FILL_TERRAIN_MODE.getKeybind().getKeyCode())
        {
            String prevFill = BuildMode.FILL_MODE.buttonText;
            BuildMode.FILL_MODE = BuildMode.FILL_MODE.rotateMode();
            KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.CHANGE_FILL_TERRAIN_MODE,prevFill + " to " + BuildMode.FILL_MODE.buttonText);
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
    
    protected void rotateThisButton()
    {
        thisButtonIndex++;
        if(thisButtonIndex >= buttonList.size())
            thisButtonIndex = 0;
        System.out.println(buttonList.get(thisButtonIndex));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if(button.id == SINGE_BUTTON_ID)
            BuildMode.changeToolModeTo(0);
        else if(button.id == LINE_BUTTON_ID)
            BuildMode.changeToolModeTo(1);
        else if(button.id == PULL_BUTTON_ID)
            BuildMode.changeToolModeTo(5);
        else if(button.id == CURVE_BUTTON_ID)
            BuildMode.changeToolModeTo(3);
        else if(button.id == RECTANGLE_BUTTON_ID)
            BuildMode.changeToolModeTo(2);
        else if(button.id == CIRCLE_BUTTON_ID)
            BuildMode.changeToolModeTo(4);
        else if(button.id == COPY_BUTTON_ID)
            BuildMode.changeToolModeTo(6);
        else if(button.id == MOVE_BUTTON_ID)
            BuildMode.changeToolModeTo(7);
        else if(button.id == FILLGAP_BUTTON_ID)
            BuildMode.changeToolModeTo(8);
        else if(button.id == FREE_DIR_BUTTON_ID)
            BuildMode.DIRECTION_MODE = EnumDirectionMode.FREE;
        else if(button.id == AUTO_DIR_BUTTON_ID)
            BuildMode.DIRECTION_MODE = EnumDirectionMode.AUTO;
        else if(button.id == GRND_DIR_BUTTON_ID)
            BuildMode.DIRECTION_MODE = EnumDirectionMode.GROUND;
        else if(button.id == XY_DIR_BUTTON_ID)
            BuildMode.DIRECTION_MODE = EnumDirectionMode.XY;
        else if(button.id == ZY_DIR_BUTTON_ID)
            BuildMode.DIRECTION_MODE = EnumDirectionMode.ZY;
        else if(button.id == XZ_DIR_BUTTON_ID)
            BuildMode.DIRECTION_MODE = EnumDirectionMode.XZ;
        else if(button.id == FILL_BUTTON_ID)
            BuildMode.FILL_MODE = EnumFillMode.FILL;
        else if(button.id == NO_FILL_BUTTON_ID)
            BuildMode.FILL_MODE = EnumFillMode.NO_FILL;
        else if(button.id == BUILD_BUTTON_ID)
        {
            GuiToggleButton thisButton = (GuiToggleButton) button;
            thisButton.active = true;
        }
        else if(button.id == EDIT_BUTTON_ID)
        {
            AdvCreation.setMode(EnumMainMode.EDIT);
            Minecraft.getMinecraft().displayGuiScreen(new GuiAdjustModeSelectionScreen());

        }
        else if(button.id == LINE_SNAP_BUTTON_ID)
        {
            ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER = !ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER;
            updateSnapButtonTooltips();
        }
        else if(button.id == CURVE_SNAP_BUTTON_ID)
        {
            ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER = !ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER;
            updateSnapButtonTooltips();
        }
        else if(button.id == CIRCLE_SNAP_BUTTON_ID)
        {
            ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER =  !ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER;
            updateSnapButtonTooltips();
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

    private void updateSnapButtonTooltips()
    {
        if (ConfigurationHandler.toolConfig.ALWAYS_SNAP_TO_BLOCK_CENTER)
        {
            lineSnapCenterButton.displayString = "Current State: Cursor snaps to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";
            curveSnapCenterButton.displayString = "Current State: Cursor snaps to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";
            circleSnapCenterButton.displayString = "Current State: Cursor snaps to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";
        }
        else
        {
            lineSnapCenterButton.displayString = "Current State: Cursor DOESN'T snap to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";
            curveSnapCenterButton.displayString = "Current State: Cursor DOESN'T snap to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";
            circleSnapCenterButton.displayString = "Current State: Cursor DOESN'T snap to center of block (" + Keybindings.TOGGLE_SNAP_TO_CENTER.getKeybind().getDisplayName() + ")";
        }
    }
}
