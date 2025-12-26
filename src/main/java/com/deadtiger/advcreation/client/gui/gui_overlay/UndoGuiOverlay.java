package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseButton;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseElement;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayCustomWindow;
import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.undo_actions.UndoFunctionality;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class UndoGuiOverlay extends AbstractGuiOverlay
{
    private final int buttonSize = 20;
    public static final int windowWidth = 55, windowHeight = 30;

    //buttons and elements
    // the window
    private static GuiOverlayBaseElement window;
    // the buttons in the window
    private static GuiOverlayBaseButton undoButton, redoButton;

    @Override
    protected void initGuiOverlay()
    {
        elementlist.clear();

        mainTexture = new ResourceLocation(Reference.MODID, "textures/gui/undo_gui_overlay.png");

        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        int windowY = 0;

        // the window containing mode buttons
        window = new GuiOverlayCustomWindow(width - windowWidth, windowY, windowWidth, windowHeight);
        window.setName("UndoRedo window");
        // the buttons in the window
        undoButton = new GuiOverlayBaseButton(mainTexture, width - windowWidth + 5, windowY + 5, 1, 0, 30, buttonSize, buttonSize, 40, 20);
        undoButton.setName("Undo");
        undoButton.setTooltip("Undo the last action");
        undoButton.setDisabledTooltip("Disabled While Placing Blocks");
        undoButton.setX_disabledOffset(60);
        undoButton.setTimedDeactivation(true);
        undoButton.setButtonKeyToDisplayInTooltip(Keybindings.UNDO_ACTION.getKeybind());
        undoButton.index = 14;
        redoButton = new GuiOverlayBaseButton(mainTexture, width - buttonSize - 5, windowY + 5, 1, 0, 60, buttonSize, buttonSize, 40, 20);
        redoButton.setName("Redo");
        redoButton.setTooltip("Redo and undone action");
        redoButton.setDisabledTooltip("Disabled While Placing Blocks");
        redoButton.setX_disabledOffset(60);
        redoButton.setTimedDeactivation(true);
        redoButton.setButtonKeyToDisplayInTooltip(Keybindings.REDO_ACTION.getKeybind());
        redoButton.index = 15;

        window.addElement(undoButton);
        window.addElement(redoButton);


        //all first level element to elementlist
        elementlist.add(window);
        super.initGuiOverlay();
    }

    @Override
    protected void preHoverOnElements(int x_resized, int y_resized, long time)
    {
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        if (window.getX() != width - windowWidth)
        {
            window.moveAllTo(width - windowWidth, window.getY());
        }

        if(undoButton.isEnabled() && redoButton.isEnabled() && NetworkPlaceBlockListFormatter.PLACEMENT_OPERATION)
        {
            undoButton.setEnabled(false);
            redoButton.setEnabled(false);
        }
        else if(!undoButton.isEnabled() && !redoButton.isEnabled() && !NetworkPlaceBlockListFormatter.PLACEMENT_OPERATION)
        {
            undoButton.setEnabled(true);
            redoButton.setEnabled(true);
        }

        super.preHoverOnElements(x_resized, y_resized, time);
    }

    @Override
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        if (element != null)
        {
            super.actionPerformed(element, mouseX, mouseY, time, worldIsRemote);
            if (element.getName().equals(undoButton.getName()))
            {
                UndoFunctionality.activateUndo();
            }
            else if (element.getName().equals(redoButton.getName()))
            {
                UndoFunctionality.activateRedo();
            }
        }

    }

}
