package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseButton;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseElement;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayCustomButton;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayCustomWindow;
import com.deadtiger.advcreation.plugin.modded_classes.ModBlockRendererDispatcher;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class CutThroughGuiOverlay extends AbstractGuiOverlay
{
    private final int buttonSize = 20;
    private final int windowWidth = 55, windowHeight = 30;
    
    //buttons and elements
    // the window
    private static GuiOverlayBaseElement window,refreshWindow;
    // the buttons in the window
    private static GuiOverlayBaseButton noCutThroughButton, cutThroughButton;
        private static GuiOverlayBaseElement refreshButton;
    
    @Override
    protected void initGuiOverlay() {
        elementlist.clear();
    
        mainTexture = new ResourceLocation(Reference.MODID,"textures/gui/cut_through_gui_overlay.png");
    
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        
        // the window containing mode buttons
        int baseX = width - windowWidth - windowWidth;

        window = new GuiOverlayCustomWindow(baseX,0,windowWidth,windowHeight);
        window.setName("cut-through window");
        // the buttons in the window
        noCutThroughButton = new GuiOverlayBaseButton(mainTexture, baseX + 5,5,1,0,0,buttonSize,buttonSize,40,20);
        noCutThroughButton.setName("Full Landscape");
        noCutThroughButton.setTooltip("Show Full Landscape");
        noCutThroughButton.setButtonKeyToDisplayInTooltip(Keybindings.TOGGLE_CUTTHROUGH.getKeybind());
        noCutThroughButton.index = 22;

        cutThroughButton = new GuiOverlayBaseButton(mainTexture,width-buttonSize-5-windowWidth,5,1,0,30,buttonSize,buttonSize,40,20);
        cutThroughButton.setName("Cut-Through");
        cutThroughButton.setTooltip("Show Cut-through Of Landscape");
        cutThroughButton.setButtonKeyToDisplayInTooltip(Keybindings.TOGGLE_CUTTHROUGH.getKeybind());
        cutThroughButton.index = 22;

        window.addElement(noCutThroughButton);
        window.addElement(cutThroughButton);
        
        //the button to refresh the terrain
        refreshWindow = new GuiOverlayCustomWindow(width-windowWidth-windowWidth - windowHeight,0,windowHeight,windowHeight);
        refreshWindow.setName("refresh_window");
        
        refreshButton = new GuiOverlayCustomButton("RE",width-windowWidth-windowWidth - windowHeight+5,5,buttonSize,buttonSize);
        refreshButton.setName("refresh");
        refreshButton.setTooltip("Refresh terrain rendering");
        refreshButton.setButtonKeyToDisplayInTooltip(Keybindings.REFRESH_TERRAIN.getKeybind());
        refreshButton.index = 23;
        
        refreshWindow.addElement(refreshButton);
        
        //all first level element to elementlist
        elementlist.add(window);
        elementlist.add(refreshWindow);
        super.initGuiOverlay();
    }

    @Override
    protected void preHoverOnElements(int x_resized, int y_resized, long time)
    {
        if(ModBlockRendererDispatcher.cuttThroughOn)
        {
            cutThroughButton.setSelected(true);
            noCutThroughButton.setSelected(false);
        }
        else
        {
            cutThroughButton.setSelected(false);
            noCutThroughButton.setSelected(true);
        }
        
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        if(window.getX() != width-windowWidth-windowWidth)
        {
            window.moveAllTo(width-windowWidth-windowWidth,window.getY());
        }
        if(refreshWindow.getX() != width-windowWidth-windowWidth - windowHeight)
        {
            refreshWindow.moveAllTo(width-windowWidth-windowWidth - windowHeight,window.getY());
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
            if (element.getName().equals(noCutThroughButton.getName()))
                ModBlockRendererDispatcher.cuttThroughOn = false;
            else if (element.getName().equals(cutThroughButton.getName()))
                ModBlockRendererDispatcher.cuttThroughOn = true;
            else if(element.getName().equals(refreshButton.getName()))
                ModBlockRendererDispatcher.refreshTerrain = true;
        }

    }
    
}
