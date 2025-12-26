package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.*;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.client.player.ToolEnabled;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.message.MessageUpdatePlayerSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;

public class ToolDisableGuiOverlay extends AbstractGuiOverlay {
    private int buttonWidth = 55, buttonHeight = 20;
    //buttons and elements
    // the window
    static GuiOverlayOnOffButton toolDisableButton;


    @Override
    protected void initGuiOverlay() {
        elementlist.clear();

        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        int buttonSize = 20;

        //start with y with the height of the undoGuiOverlay window
        int startY = calculateToolDisableButtonY();

        //the buttons indicating and allowing the toggling of direction, fill and tool modes
        //direction mode
        toolDisableButton = new GuiOverlayOnOffButton(width - buttonWidth, startY, ConfigurationHandler.general.TOOLS_ENABLED);
        toolDisableButton.stateIsOn = (ConfigurationHandler.general.TOOLS_ENABLED);
        toolDisableButton.setName("toolsOnOffButton");
        updateToolDisableTooltip();
        toolDisableButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.TOGGLE_TOOLS_ENABLED.getKeybind());
        toolDisableButton.index = 66;

        elementlist.add(toolDisableButton);

        super.initGuiOverlay();
    }

    @Override
    protected void preHoverOnElements(int x_resized, int y_resized, long time)
    {
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        int startY = calculateToolDisableButtonY();
        if(startY != toolDisableButton.getY() || toolDisableButton.getX() != width - buttonWidth)
        {
            toolDisableButton.moveAllTo(width - buttonWidth,startY);
        }

        toolDisableButton.stateIsOn = ConfigurationHandler.general.TOOLS_ENABLED;

        super.preHoverOnElements(x_resized,y_resized,time);
    }


    public static int calculateToolDisableButtonY()
    {
        EnumMainMode mode = AdvCreation.getMode();
        int startY = UndoGuiOverlay.windowHeight;

        if (mode == EnumMainMode.BUILD)
            startY += BuildToolsGuiOverlay.windowHeight;
        else if (mode == EnumMainMode.EDIT)
        {
            if (EditMode.ADJUST_MODES[EditMode.ADJUST_MODE_INDEX].usesOnlyTerrainOption())
                startY += EditToolsGuiOverlay.windowHeight2;
            else
                startY += EditToolsGuiOverlay.windowHeight;
        }
        else if (mode == EnumMainMode.PLACE)
            startY += PlaceToolsGuiOverlay.windowHeight;

        return startY;
    }

    @Override
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote) {
        if (element != null) {
            super.actionPerformed(element, mouseX, mouseY, time, worldIsRemote);
            if (element == toolDisableButton || element == toolDisableButton.getElementlist().get(0))
            {
                ToolEnabled.toggleToolsEnabled();
            }

        }

    }



    public static void updateToolDisableTooltip()
    {
        if(ConfigurationHandler.general.TOOLS_ENABLED)
            toolDisableButton.setTooltip("Toggle adv. creation tools (now: ON)");
        else
            toolDisableButton.setTooltip("Toggle adv. creation tools (now: OFF)");
    }


}
