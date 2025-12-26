package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;

import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseElement;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayCustomButton;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayCustomWindow;
import com.deadtiger.advcreation.client.gui.gui_screen.foundation_block_inventory_screen.FoundationBlockInventoryScreen;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import static com.deadtiger.advcreation.place_template.PlaceTemplateMode.EnumAirMode.*;
import static com.deadtiger.advcreation.place_template.PlaceTemplateMode.EnumWallMode.*;
import static com.deadtiger.advcreation.place_template.PlaceTemplateMode.EnumFoundationMode.*;

public class PlaceToolsGuiOverlay extends AbstractGuiOverlay
{
    private int buttonHeight = 20, buttonWidth = 45;
    public static final int windowWidth = 55, windowHeight = 110;
    private final int foundationWindowWidth = 155, foundationWindowHeight = 55;
    private int foundationMaterialX, foundationMaterialY;

    //buttons and elements
    // the windows
    private static GuiOverlayBaseElement window, foundationPopupWindow, airPopupWindow, wallPopupWindow;
    // the buttons in the foundation popup window
    private static GuiOverlayCustomButton noFoundationButton;
    private static GuiOverlayCustomButton oneLayerFoundationButton;
    private static GuiOverlayCustomButton toGroundFoundationButton;
    private static GuiOverlayCustomButton foundationSelectionButton;
    private static GuiOverlayCustomButton foundationMaterialButton;

    // the buttons in the air popup window
    private static GuiOverlayCustomButton noAirButton;
    private static GuiOverlayCustomButton onlyInsideAirButton;
    private static GuiOverlayCustomButton allAirButton;
    private static GuiOverlayCustomButton airSelectionButton;

    // the buttons in the walls popup window
    private static GuiOverlayCustomButton noWallsButton;
    private static GuiOverlayCustomButton noIntersectingWallsButton;
    private static GuiOverlayCustomButton allWallsButton;
    private static GuiOverlayCustomButton wallsSelectionButton;

    @Override
    protected void initGuiOverlay()
    {
        elementlist.clear();

        mainTexture = new ResourceLocation(Reference.MODID, "textures/gui/undo_gui_overlay.png");

        buttonHeight = 20;

        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        int foundationWindowY = 30;

        // the window containing mode buttons
        window = new GuiOverlayCustomWindow(width - windowWidth, foundationWindowY, windowWidth, windowHeight);
        window.setName("place_tools_window");


        foundationPopupWindow = new GuiOverlayCustomWindow(width - windowWidth - foundationWindowWidth, foundationWindowY, foundationWindowWidth, foundationWindowHeight);
        foundationPopupWindow.setName("foundation_window");

        // the buttons in the foundation window
        noFoundationButton = new GuiOverlayCustomButton(NO_FOUNDATION.buttonText, width - windowWidth - foundationWindowWidth + 5, foundationWindowY + 15, buttonWidth, buttonHeight);
        noFoundationButton.setName("noFoundation");
        noFoundationButton.setTooltip("Add nothing");
        noFoundationButton.index = 25;

        oneLayerFoundationButton = new GuiOverlayCustomButton(FOUNDATION_LAYER.buttonText, width - windowWidth - foundationWindowWidth + 5 + buttonWidth, foundationWindowY + 15, buttonWidth, buttonHeight);
        oneLayerFoundationButton.setName("OneFoundationLayer");
        oneLayerFoundationButton.setTooltip("Add one layer");
        oneLayerFoundationButton.index = 26;

        toGroundFoundationButton = new GuiOverlayCustomButton(FOUNDATION_TO_GROUND.buttonText, width - windowWidth - foundationWindowWidth + 5 + buttonWidth * 2, foundationWindowY + 15, buttonWidth, buttonHeight);
        toGroundFoundationButton.setName("ToGround");
        toGroundFoundationButton.setTooltip("Add blocks untill ground");
        toGroundFoundationButton.index = 27;

        foundationPopupWindow.addElement(noFoundationButton);
        foundationPopupWindow.addElement(oneLayerFoundationButton);
        foundationPopupWindow.addElement(toGroundFoundationButton);
        foundationPopupWindow.setVisibility(false);


        int airWindowY = foundationWindowY + 30;
        airPopupWindow = new GuiOverlayCustomWindow(width - windowWidth - foundationWindowWidth, airWindowY, foundationWindowWidth, foundationWindowHeight);
        airPopupWindow.setName("air_window");

        // the buttons in the air window
        noAirButton = new GuiOverlayCustomButton(NO_AIR.buttonText, width - windowWidth - foundationWindowWidth + 5, airWindowY + 15, buttonWidth, buttonHeight);
        noAirButton.setName("no_air");
        noAirButton.setTooltip("Do not place any air");
        noAirButton.index = 28;

        onlyInsideAirButton = new GuiOverlayCustomButton(INSIDE_AIR.buttonText, width - windowWidth - foundationWindowWidth + 5 + buttonWidth, airWindowY + 15, buttonWidth, buttonHeight);
        onlyInsideAirButton.setName("inside_air");
        onlyInsideAirButton.setTooltip("Only place the inside air");
        onlyInsideAirButton.index = 29;

        allAirButton = new GuiOverlayCustomButton(ALL_AIR.buttonText, width - windowWidth - foundationWindowWidth + 5 + buttonWidth * 2, airWindowY + 15, buttonWidth, buttonHeight);
        allAirButton.setName("all_air");
        allAirButton.setTooltip("Place both inside and outside air");
        allAirButton.index = 30;

        airPopupWindow.addElement(noAirButton);
        airPopupWindow.addElement(onlyInsideAirButton);
        airPopupWindow.addElement(allAirButton);
        airPopupWindow.setVisibility(false);


        int wallsWindowY = airWindowY + 30;
        wallPopupWindow = new GuiOverlayCustomWindow(width - windowWidth - foundationWindowWidth, wallsWindowY, foundationWindowWidth, foundationWindowHeight);
        wallPopupWindow.setName("wall_window");

        // the buttons in the walls window
        noWallsButton = new GuiOverlayCustomButton(NO_WALLS.buttonText, width - windowWidth - foundationWindowWidth + 5, wallsWindowY + 15, buttonWidth, buttonHeight);
        noWallsButton.setName("no_walls");
        noWallsButton.setTooltip("Do not place template walls");
        noWallsButton.index = 31;

        noIntersectingWallsButton = new GuiOverlayCustomButton(NON_INTERSECTING_WALLS.buttonText, width - windowWidth - foundationWindowWidth + 5 + buttonWidth, wallsWindowY + 15, buttonWidth, buttonHeight);
        noIntersectingWallsButton.setName("no_intersecting_walls");
        noIntersectingWallsButton.setTooltip("Only place wall when no solid block is there");
        noIntersectingWallsButton.index = 32;

        allWallsButton = new GuiOverlayCustomButton(ALL_WALLS.buttonText, width - windowWidth - foundationWindowWidth + 5 + buttonWidth * 2, wallsWindowY + 15, buttonWidth, buttonHeight);
        allWallsButton.setName("all_walls");
        allWallsButton.setTooltip("Place all template walls");
        allWallsButton.index = 33;

        wallPopupWindow.addElement(noWallsButton);
        wallPopupWindow.addElement(noIntersectingWallsButton);
        wallPopupWindow.addElement(allWallsButton);
        wallPopupWindow.setVisibility(false);


        //the buttons to open the popup windows
        //foundation
        foundationSelectionButton = new GuiOverlayCustomButton("Foundation", width - windowWidth + 5, foundationWindowY + 15, buttonWidth, buttonHeight);
        foundationSelectionButton.setName("FoundationSelect");
        foundationSelectionButton.setTooltip("Select a foundation");
        foundationSelectionButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_DIR_SHAPE_MODE.getKeybind());

        //foundation material button
        foundationMaterialX = width - windowWidth + 5 + 16;
        foundationMaterialY = foundationWindowY + 15 + 8;
        foundationMaterialButton = new GuiOverlayCustomButton("", foundationMaterialX, foundationMaterialY, 12, 11);
        foundationMaterialButton.setName("FoundationMaterial");
        foundationMaterialButton.setTooltip("Select a material for the foundation");
        foundationMaterialButton.index = 34;
        foundationSelectionButton.addElement(foundationMaterialButton);

        //air
        airSelectionButton = new GuiOverlayCustomButton("Air", width - windowWidth + 5, airWindowY + 15, buttonWidth, buttonHeight);
        airSelectionButton.setName("AirSelect");
        airSelectionButton.setTooltip("Select what Air to place");
        airSelectionButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_TOOL_MODE.getKeybind());

        //walls
        wallsSelectionButton = new GuiOverlayCustomButton("Walls", width - windowWidth + 5, wallsWindowY + 15, buttonWidth, buttonHeight);
        wallsSelectionButton.setName("WallSelect");
        wallsSelectionButton.setTooltip("Select which Walls to place");
        wallsSelectionButton.setButtonToggleKeyToDisplayInTooltip(Keybindings.CHANGE_FILL_TERRAIN_MODE.getKeybind());

        window.addElement(foundationSelectionButton);
        window.addElement(airSelectionButton);
        window.addElement(wallsSelectionButton);

        //all first level element to elementlist
        elementlist.add(window);
        elementlist.add(foundationPopupWindow);
        elementlist.add(airPopupWindow);
        elementlist.add(wallPopupWindow);

        if (AdvCreation.getMode() == EnumMainMode.PLACE)
        {
            setVisibility(true);
        }
        else
        {
            setVisibility(false);
        }


        super.initGuiOverlay();
    }

    private void updateFoundationMaterialButtonVisibility(boolean free)
    {
        if (foundationMaterialButton != null && foundationSelectionButton != null)
        {
            if ((PlaceTemplateMode.FOUNDATION == FOUNDATION_LAYER || PlaceTemplateMode.FOUNDATION == FOUNDATION_TO_GROUND) && free)
            {
                foundationMaterialButton.setVisibility(true);
                foundationSelectionButton.textYOffset = -5;
            }
            else
            {
                foundationMaterialButton.setVisibility(false);
                foundationSelectionButton.textYOffset = 0;
            }
        }
    }

    @Override
    protected void preHoverOnElements(int x_resized, int y_resized, long time)
    {
        updateSelectionButtonsText();

        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        if (window.getX() != width - windowWidth)
        {
            window.moveAllTo(width - windowWidth, window.getY());
            foundationPopupWindow.moveAllTo(width - windowWidth - foundationWindowWidth, foundationPopupWindow.getY());
            airPopupWindow.moveAllTo(width - windowWidth - foundationWindowWidth, airPopupWindow.getY());
            wallPopupWindow.moveAllTo(width - windowWidth - foundationWindowWidth, wallPopupWindow.getY());
        }


        super.preHoverOnElements(x_resized, y_resized, time);
    }

    @Override
    protected void preDrawTooltips(int x_resized, int y_resized, long time)
    {
        if (foundationMaterialButton.isVisible())
            CustomGuiUtils.drawBlockIcon(mc, new ItemStack(PlaceTemplateMode.FOUNDATION_BLOCKSTATE.getBlock()), 0.6F, foundationMaterialButton.getX() + 1, foundationMaterialButton.getY() + 2, 0, EnumFacing.EAST, false, false);
        super.preDrawTooltips(x_resized, y_resized, time);
    }

    @Override
    public boolean leftClick(int x, int y, boolean worldIsRemote)
    {
        boolean leftClickOnThis = super.leftClick(x, y, worldIsRemote);
        if (!leftClickOnThis)
        {
            foundationPopupWindow.setVisibility(false);
            airPopupWindow.setVisibility(false);
            wallPopupWindow.setVisibility(false);
        }
        return leftClickOnThis;
    }

    @Override
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        if (element != null)
        {
            super.actionPerformed(element, mouseX, mouseY, time, worldIsRemote);
            //open popupWindow buttons
            if (element.getName().equals(foundationSelectionButton.getName()))
            {
                foundationPopupWindow.setVisibility(true);
                airPopupWindow.setVisibility(false);
                wallPopupWindow.setVisibility(false);
            }
            else if (element.getName().equals(airSelectionButton.getName()))
            {
                airPopupWindow.setVisibility(true);
                foundationPopupWindow.setVisibility(false);
                wallPopupWindow.setVisibility(false);
            }
            else if (element.getName().equals(wallsSelectionButton.getName()))
            {
                wallPopupWindow.setVisibility(true);
                foundationPopupWindow.setVisibility(false);
                airPopupWindow.setVisibility(false);
            }

            //buttons withing the foundation popup window
            else if (element.getName().equals(noFoundationButton.getName()))
            {
                PlaceTemplateMode.FOUNDATION = PlaceTemplateMode.EnumFoundationMode.NO_FOUNDATION;
                foundationPopupWindow.setVisibility(false);
                foundationSelectionButton.setText(noFoundationButton.getText());
                foundationSelectionButton.textYOffset = 0;
                updateFoundationMaterialButtonVisibility(false);
            }
            else if (element.getName().equals(oneLayerFoundationButton.getName()))
            {
                PlaceTemplateMode.FOUNDATION = PlaceTemplateMode.EnumFoundationMode.FOUNDATION_LAYER;
                foundationPopupWindow.setVisibility(false);
                foundationSelectionButton.setText(oneLayerFoundationButton.getText());
                foundationSelectionButton.textYOffset = -5;
                updateFoundationMaterialButtonVisibility(true);
            }
            else if (element.getName().equals(toGroundFoundationButton.getName()))
            {
                PlaceTemplateMode.FOUNDATION = PlaceTemplateMode.EnumFoundationMode.FOUNDATION_TO_GROUND;
                foundationPopupWindow.setVisibility(false);
                foundationSelectionButton.setText(toGroundFoundationButton.getText());
                foundationSelectionButton.textYOffset = -5;
                updateFoundationMaterialButtonVisibility(true);
            }
            //buttons within the air popup window
            else if (element.getName().equals(noAirButton.getName()))
            {
                PlaceTemplateMode.AIR = NO_AIR;
                airPopupWindow.setVisibility(false);
                airSelectionButton.setText(noAirButton.getText());
            }
            else if (element.getName().equals(onlyInsideAirButton.getName()))
            {
                PlaceTemplateMode.AIR = PlaceTemplateMode.EnumAirMode.INSIDE_AIR;
                airPopupWindow.setVisibility(false);
                airSelectionButton.setText(onlyInsideAirButton.getText());
            }
            else if (element.getName().equals(allAirButton.getName()))
            {
                PlaceTemplateMode.AIR = PlaceTemplateMode.EnumAirMode.ALL_AIR;
                airPopupWindow.setVisibility(false);
                airSelectionButton.setText(allAirButton.getText());
            }
            //buttons within the walls popup window
            else if (element.getName().equals(noWallsButton.getName()))
            {
                PlaceTemplateMode.WALL = PlaceTemplateMode.EnumWallMode.NO_WALLS;
                wallPopupWindow.setVisibility(false);
                wallsSelectionButton.setText(noWallsButton.getText());
            }
            else if (element.getName().equals(noIntersectingWallsButton.getName()))
            {
                PlaceTemplateMode.WALL = PlaceTemplateMode.EnumWallMode.NON_INTERSECTING_WALLS;
                wallPopupWindow.setVisibility(false);
                wallsSelectionButton.setText(noIntersectingWallsButton.getText());
            }
            else if (element.getName().equals(allWallsButton.getName()))
            {
                PlaceTemplateMode.WALL = PlaceTemplateMode.EnumWallMode.ALL_WALLS;
                wallPopupWindow.setVisibility(false);
                wallsSelectionButton.setText(allWallsButton.getText());
            }
            else if (element.getName().equals(foundationMaterialButton.getName()))
            {
                mc.displayGuiScreen(new FoundationBlockInventoryScreen(mc.player));
            }
        }

    }

    @Override
    public void updateChangedMainMode(EnumMainMode mode)
    {
        if (mode == EnumMainMode.PLACE)
            this.setVisibility(true);
        else
            this.setVisibility(false);
    }

    @Override
    public void setVisibility(boolean visible)
    {
        for (GuiOverlayBaseElement element : elementlist)
        {
            if ((element.getName() != foundationPopupWindow.getName()) &&
                    (element.getName() != airPopupWindow.getName()) &&
                    (element.getName() != wallPopupWindow.getName()))
                element.setVisibility(visible);
        }
        updateFoundationMaterialButtonVisibility(visible);
    }

    public void updateSelectionButtonsText()
    {
        foundationSelectionButton.setText(PlaceTemplateMode.FOUNDATION.buttonText);
        airSelectionButton.setText(PlaceTemplateMode.AIR.buttonText);
        wallsSelectionButton.setText(PlaceTemplateMode.WALL.buttonText);
    }
}
