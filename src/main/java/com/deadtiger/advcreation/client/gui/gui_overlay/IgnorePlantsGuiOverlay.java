package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseButton;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseElement;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayCustomWindow;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class IgnorePlantsGuiOverlay extends AbstractGuiOverlay
{
    private final int buttonSize = 20;
    private final int windowWidth = 55, windowHeight = 30;
    
    //buttons and elements
    // the window
    private static GuiOverlayBaseElement window;
    // the buttons in the window
    private static GuiOverlayBaseButton ignorePlantsButton, dontIgnorePlantsButton;
    
    @Override
    protected void initGuiOverlay() {
        elementlist.clear();
    
        mainTexture = new ResourceLocation(Reference.MODID,"textures/gui/undo_gui_overlay.png");
    
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        
        // the window containing mode buttons
        int baseX = width - windowWidth ;

        window = new GuiOverlayCustomWindow(baseX,0,windowWidth,windowHeight);
        window.setName("ignore plant window");
        // the buttons in the window
        ignorePlantsButton = new GuiOverlayBaseButton(mainTexture, baseX + 5,5,1,0,120,buttonSize,buttonSize,40,20);
        ignorePlantsButton.setName("Ignore Plants");
        ignorePlantsButton.setTooltip("Ignore Plants In Selection");
        ignorePlantsButton.setButtonKeyToDisplayInTooltip(Keybindings.TOGGLE_IGNORE_PLANTS.getKeybind());
        ignorePlantsButton.index = 22;

        dontIgnorePlantsButton = new GuiOverlayBaseButton(mainTexture,width-buttonSize-5,5,1,0,90,buttonSize,buttonSize,40,20);
        dontIgnorePlantsButton.setName("Include Plants");
        dontIgnorePlantsButton.setTooltip("Include Plants In Selection");
        dontIgnorePlantsButton.setButtonKeyToDisplayInTooltip(Keybindings.TOGGLE_IGNORE_PLANTS.getKeybind());
        dontIgnorePlantsButton.index = 22;

        window.addElement(ignorePlantsButton);
        window.addElement(dontIgnorePlantsButton);

        
        //all first level element to elementlist
        elementlist.add(window);
        super.initGuiOverlay();
    }

    @Override
    protected void preHoverOnElements(int x_resized, int y_resized, long time)
    {
        if(!IsometricCamera.IGNORE_PLANTS)
        {
            dontIgnorePlantsButton.setSelected(true);
            ignorePlantsButton.setSelected(false);
        }
        else
        {
            dontIgnorePlantsButton.setSelected(false);
            ignorePlantsButton.setSelected(true);
        }
        
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        if(window.getX() != width-windowWidth)
        {
            window.moveAllTo(width-windowWidth,window.getY());
        }

        super.preHoverOnElements(x_resized, y_resized, time);
    }
    
    @Override
    protected void preDrawElements(int x_resized, int y_resized, long time)
    {
    

    }
    
    @Override
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        if(element != null)
        {
            super.actionPerformed(element,mouseX,mouseY,time,worldIsRemote);
            if (element.getName().equals(ignorePlantsButton.getName()))
                IsometricCamera.IGNORE_PLANTS = true;
            else if (element.getName().equals(dontIgnorePlantsButton.getName()))
                IsometricCamera.IGNORE_PLANTS = false;
        }

    }
    
}
