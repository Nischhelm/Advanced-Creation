package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseElement;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayCustomButton;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayCustomWindow;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.BaseAdjustMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.DigRaiseAdjustMode;
import com.deadtiger.advcreation.edit_mode.adjust_modes.LevelAdjustMode;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class EditToolsGuiOverlay extends AbstractGuiOverlay
{
    private final int buttonHeight = 20, buttonWidth = 45;
    public static final int windowWidth = 55, windowHeight = 55 , windowHeight2 =110;
    
    //buttons and elements
    // the windows
    private static GuiOverlayBaseElement window;
    // the button displaying the current adjustMode
    private static GuiOverlayCustomButton adjustSelectionButton;

    // the windows
    private static GuiOverlayBaseElement window2;

    // the buttons in the foundation popup window
    private static GuiOverlayCustomButton shapeSelectionButton;

    // the buttons in the air popup window
    private static GuiOverlayCustomButton adjustSelectionButton2;

    // the buttons in the walls popup window
    private static GuiOverlayCustomButton terrainSelectionButton;
    
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
        window = new GuiOverlayCustomWindow(width-windowWidth,30,windowWidth,windowHeight);
        window.setName("Edit_adjust_window");
        


        //adjust
        adjustSelectionButton = new GuiOverlayCustomButton("AdjustMode",width-windowWidth+5,foundationWindowY+15,buttonWidth,buttonHeight);
        adjustSelectionButton.setName("adjustSelect");
        adjustSelectionButton.setTooltip("Select A Adjust Mode");
        adjustSelectionButton.setButtonKeyToDisplayInTooltip(Keybindings.OPEN_TOOL_MENU.getKeybind());
        adjustSelectionButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_TOOL_MODE.getKeybind());
        adjustSelectionButton.index = 19;

        window.addElement(adjustSelectionButton);
        
        //all first level element to elementlist
        elementlist.add(window);

        //alternative window when you are using a tool with multiple settings
        // the window containing mode buttons
        window2 = new GuiOverlayCustomWindow(width-windowWidth,foundationWindowY,windowWidth,windowHeight2);
        window2.setName("Edit_adjust_window2");

        int airWindowY = foundationWindowY +30;
        int wallsWindowY = airWindowY + 30;

        //the buttons indicating and allowing the toggling of terrain shape, only terrain and tool modes
        //terrain shape mode
        shapeSelectionButton = new GuiOverlayCustomButton("Terrain Shape",width-windowWidth+1,airWindowY+15,buttonWidth+7,buttonHeight);
        shapeSelectionButton.setName("terrainShapeSelect");
        shapeSelectionButton.setTooltip("Select a Terrain Shape");
        shapeSelectionButton.setButtonKeyToDisplayInTooltip(Keybindings.OPEN_TOOL_MENU.getKeybind());
        shapeSelectionButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_DIR_SHAPE_MODE.getKeybind());
        shapeSelectionButton.setTimedDeactivation(true);
        shapeSelectionButton.index = 18;

        //tool mode
        adjustSelectionButton2 = new GuiOverlayCustomButton("Tool",width-windowWidth+5,foundationWindowY+15,buttonWidth,buttonHeight);
        adjustSelectionButton2.setName("toolSelect2");
        adjustSelectionButton2.setTooltip("This Only Indicates Current Tool");
        adjustSelectionButton2.setButtonKeyToDisplayInTooltip(Keybindings.OPEN_TOOL_MENU.getKeybind());
        adjustSelectionButton2.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_TOOL_MODE.getKeybind());
        adjustSelectionButton2.setTimedDeactivation(true);
        adjustSelectionButton2.index = 19;
        //fill mode
        terrainSelectionButton = new GuiOverlayCustomButton("OnlyTerrain",width-windowWidth+5,wallsWindowY+15,buttonWidth,buttonHeight);
        terrainSelectionButton.setName("onlyTerrainSelect");
        terrainSelectionButton.setTooltip("Select A Only Terrain Mode");
        terrainSelectionButton.setButtonKeyToDisplayInTooltip(Keybindings.OPEN_TOOL_MENU.getKeybind());
        terrainSelectionButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_FILL_TERRAIN_MODE.getKeybind());
        terrainSelectionButton.setTimedDeactivation(true);
        terrainSelectionButton.index = 20;

        window2.addElement(shapeSelectionButton);
        window2.addElement(adjustSelectionButton2);
        window2.addElement(terrainSelectionButton);

        //all first level element to elementlist
        elementlist.add(window2);

        if(AdvCreation.getMode() == EnumMainMode.EDIT)
        {
            setVisibility(true);
            if(EditMode.ADJUST_MODE.usesOnlyTerrainOption())
                window.setVisibility(false);
            else
                window2.setVisibility(false);
        }
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
        
        if(window.getX() != width-windowWidth)
        {
            window.moveAllTo(width-windowWidth,window.getY());
        }
        if(window2.getX() != width-windowWidth)
        {
            window2.moveAllTo(width-windowWidth,window2.getY());
        }

        super.preHoverOnElements(x_resized, y_resized, time);
    }

    @Override
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        if(worldIsRemote)
        {
            if (element != null && ((prevClickTime == 0 || ((time - prevClickTime) > clickDelayTime)) || !AdvCreation.leftClickDownClient)) {
                super.actionPerformed(element,mouseX,mouseY,time,worldIsRemote);
                prevClickTime = time;
                //open popupWindow buttons
                if (element.getName().equals(adjustSelectionButton.getName())) {
                    EditMode.rotateAdjustMode();
                }
                else if (element.getName().equals(terrainSelectionButton.getName())) {
                    EditMode.rotateOnlyTerrainMode();
                }
                else if (element.getName().equals(shapeSelectionButton.getName())) {
                    EditMode.rotateTerrainShapeMode();
                }
                else if (element.getName().equals(adjustSelectionButton2.getName())) {
                    EditMode.rotateAdjustMode();
                }
        
            }
        }
    }
    
    @Override
    protected void actionRightClickPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        if(worldIsRemote) {
            if (element != null && ((prevClickTime == 0 || ((time - prevClickTime) > clickDelayTime)) || !AdvCreation.rightClickDownClient)) {
                prevClickTime = time;
                //open popupWindow buttons
                if (element.getName().equals(adjustSelectionButton.getName())) {
                    EditMode.rotateOpositeAdjustMode();
                }
                else if (element.getName().equals(terrainSelectionButton.getName())) {
                    EditMode.rotateOnlyTerrainMode();
                }
                else if (element.getName().equals(shapeSelectionButton.getName())) {
                    EditMode.rotateOpositeTerrainShapeMode();
                }
                else if (element.getName().equals(adjustSelectionButton2.getName())) {
                    EditMode.rotateOpositeAdjustMode();
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
        if(AdvCreation.getMode() == EnumMainMode.EDIT)
        {
            BaseAdjustMode newMode = EditMode.ADJUST_MODES[EditMode.ADJUST_MODE_INDEX];
            if(newMode.usesOnlyTerrainOption())
            {
                window2.setVisibility(true);
                window.setVisibility(false);
                adjustSelectionButton2.setText(newMode.buttonText);
                adjustSelectionButton2.setTooltip(newMode.tooltipText);

                terrainSelectionButton.setText(EditMode.ONLY_TERRAIN_MODE.buttonText);
                terrainSelectionButton.setTooltip(EditMode.ONLY_TERRAIN_MODE.tooltipText);

                shapeSelectionButton.setText(EditMode.TERRAIN_SHAPE_MODE.getButtonText());

                if( newMode instanceof DigRaiseAdjustMode)
                    shapeSelectionButton.setTooltip( EditMode.TERRAIN_SHAPE_MODE.getDigRaiseTooltipText());
                else if( newMode instanceof LevelAdjustMode)
                    shapeSelectionButton.setTooltip( EditMode.TERRAIN_SHAPE_MODE.getLevelTooltipText());
                else
                    shapeSelectionButton.setTooltip(EditMode.TERRAIN_SHAPE_MODE.tooltipText);

                if(newMode.usesTerrainShapeOption())
                    shapeSelectionButton.setEnabled(true);
                else
                    shapeSelectionButton.setEnabled(false);
            }
            else
            {
                window.setVisibility(true);
                window2.setVisibility(false);
                adjustSelectionButton.setText(newMode.buttonText);
                adjustSelectionButton.setTooltip(newMode.tooltipText);
            }



            adjustSelectionButton2.setText(newMode.buttonText);
            adjustSelectionButton2.setTooltip(newMode.tooltipText);
        }


    }
    
    @Override
    public void updateChangedMainMode(EnumMainMode mode)
    {
        if(mode == EnumMainMode.EDIT)
            this.setVisibility(true);
        else
            this.setVisibility(false);
    }

    public boolean allowRightClickException(Item item)
    {
        return false;
    }

    public boolean managesPreviewBlocksItself()
    {
        return false;
    }

    public boolean managesDeleteModeItself()
    {
        return false;
    }

    public boolean usesOnlyTerrainOption()
    {
        return false;
    }

    public boolean usesTerrainShapeOption()
    {
        return false;
    }
}
