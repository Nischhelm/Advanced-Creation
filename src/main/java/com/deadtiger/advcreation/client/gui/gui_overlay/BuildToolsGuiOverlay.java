package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseElement;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayCustomButton;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayCustomWindow;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class BuildToolsGuiOverlay extends AbstractGuiOverlay
{
    private final int buttonHeight = 20, buttonWidth = 45;
    public static final int windowWidth = 55, windowHeight = 110;
    
    //buttons and elements
    // the windows
    private static GuiOverlayBaseElement window;
    // the buttons in the foundation popup window
    private static GuiOverlayCustomButton dirSelectionButton;

    // the buttons in the air popup window
    private static GuiOverlayCustomButton toolSelectionButton;

    // the buttons in the walls popup window
    private static GuiOverlayCustomButton fillSelectionButton;
    
    //time delay between clicks on the button so you don't click it twice
    long prevClickTime = 0;
    long clickDelayTime = 200;
    
    @Override
    protected void initGuiOverlay() {
        elementlist.clear();
    
        mainTexture = new ResourceLocation(Reference.MODID,"textures/gui/undo_gui_overlay.png");
    
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        int foundationWindowY = 30;

        // the window containing mode buttons
        window = new GuiOverlayCustomWindow(width-windowWidth,foundationWindowY,windowWidth,windowHeight);
        window.setName("build_tools_window");
        

    
        int airWindowY = foundationWindowY +30;
        int wallsWindowY = airWindowY + 30;
        
        //the buttons indicating and allowing the toggling of direction, fill and tool modes
        //direction mode
        dirSelectionButton = new GuiOverlayCustomButton("Direction",width-windowWidth+5,airWindowY+15,buttonWidth,buttonHeight);
        dirSelectionButton.setName("directionSelect");
        dirSelectionButton.setTooltip("Select A Direction");
        dirSelectionButton.setButtonKeyToDisplayInTooltip(Keybindings.OPEN_TOOL_MENU.getKeybind());
        dirSelectionButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_DIR_SHAPE_MODE.getKeybind());
        dirSelectionButton.setTimedDeactivation(true);
        dirSelectionButton.index = 18;

        //tool mode
        toolSelectionButton = new GuiOverlayCustomButton("Tool",width-windowWidth+5,foundationWindowY +15,buttonWidth,buttonHeight);
        toolSelectionButton.setName("toolSelect");
        toolSelectionButton.setTooltip("This Only Indicates Current Tool");
        toolSelectionButton.setButtonKeyToDisplayInTooltip(Keybindings.OPEN_TOOL_MENU.getKeybind());
        toolSelectionButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_TOOL_MODE.getKeybind());
        toolSelectionButton.setTimedDeactivation(true);
        toolSelectionButton.index = 19;
        //fill mode
        fillSelectionButton = new GuiOverlayCustomButton("Filling",width-windowWidth+5,wallsWindowY+15,buttonWidth,buttonHeight);
        fillSelectionButton.setName("fillSelect");
        fillSelectionButton.setTooltip("Select A Fill Mode");
        fillSelectionButton.setButtonKeyToDisplayInTooltip(Keybindings.OPEN_TOOL_MENU.getKeybind());
        fillSelectionButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_FILL_TERRAIN_MODE.getKeybind());
        fillSelectionButton.setTimedDeactivation(true);
        fillSelectionButton.index = 20;
        
        window.addElement(dirSelectionButton);
        window.addElement(toolSelectionButton);
        window.addElement(fillSelectionButton);
        
        //all first level element to elementlist
        elementlist.add(window);
        
        if(AdvCreation.getMode() == EnumMainMode.BUILD)
            setVisibility(true);
        else
            setVisibility(false);
        
        super.initGuiOverlay();
    }

    @Override
    protected void preHoverOnElements(int x_resized, int y_resized, long time)
    {
        updateSelectionButtonsText();
        
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        //is the game window still the same size as before?
        if(window.getX() != width-windowWidth)
        {
            window.moveAllTo(width-windowWidth,window.getY());
        }
        
        super.preHoverOnElements(x_resized, y_resized, time);
    }

    @Override
    public boolean leftClick(int x, int y, boolean worldIsRemote) {
        boolean leftClickOnThis = super.leftClick(x, y, worldIsRemote);
        return leftClickOnThis;
    }

    @Override
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        if(worldIsRemote)
        {
            super.actionPerformed(element,mouseX,mouseY,time,worldIsRemote);
            if(element != null && ((prevClickTime == 0 || ((time - prevClickTime) > clickDelayTime))|| !AdvCreation.rightClickDownClient) )
            {
                prevClickTime = time;
                //open popupWindow buttons
                if (element.getName().equals(dirSelectionButton.getName()))
                {
                    BuildMode.DIRECTION_MODE = BuildMode.DIRECTION_MODE.rotateMode();
                }
                else if (element.getName().equals(toolSelectionButton.getName()))
                {
                    BuildMode.rotateToolMode();
                }
                else if (element.getName().equals(fillSelectionButton.getName()))
                {
                    BuildMode.FILL_MODE = BuildMode.FILL_MODE.rotateMode();
                }
        
           
            }
        }

       
    }
    
    protected void actionRightClickPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        if(worldIsRemote)
        {
            if(element != null && ((prevClickTime == 0 || ((time - prevClickTime) > clickDelayTime)) || !AdvCreation.rightClickDownClient))
            {
                prevClickTime = time;
                if (element.getName().equals(dirSelectionButton.getName()))
                {
                    BuildMode.DIRECTION_MODE = BuildMode.DIRECTION_MODE.rotateOpositeMode();
                }
                else if (element.getName().equals(toolSelectionButton.getName()))
                {
                    BuildMode.rotateOpositeToolMode();
                }
                else if (element.getName().equals(fillSelectionButton.getName()))
                {
                    BuildMode.FILL_MODE = BuildMode.FILL_MODE.rotateMode();
                }
            }
        }
       
    }
    
    @Override
    public void setVisibility(boolean visible)
    {
        for (GuiOverlayBaseElement element : elementlist)
        {
            element.setVisibility(visible);
        }
    }
    
    public void updateSelectionButtonsText()
    {
        dirSelectionButton.setText(BuildMode.DIRECTION_MODE.buttonText);
        dirSelectionButton.setTooltip(BuildMode.DIRECTION_MODE.tooltipText);
        toolSelectionButton.setText(BuildMode.TOOL_MODES[BuildMode.TOOLMODE_INDEX].buttonText);
        toolSelectionButton.setTooltip(BuildMode.TOOL_MODES[BuildMode.TOOLMODE_INDEX].tooltipText);
        fillSelectionButton.setText(BuildMode.FILL_MODE.buttonText);
        fillSelectionButton.setTooltip(BuildMode.FILL_MODE.tooltipText);
    }
    
    @Override
    public void updateChangedMainMode(EnumMainMode mode)
    {
        if(mode == EnumMainMode.BUILD)
            this.setVisibility(true);
        else
            this.setVisibility(false);
    }
}
