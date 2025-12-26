package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseButton;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseElement;
import com.deadtiger.advcreation.client.player.IsometricMovement;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class CameraLevelGuiOverlay extends AbstractGuiOverlay
{
    private ResourceLocation tabs;
    
    //size variables
    private int sliderInnerY;
    private int sliderTop, sliderBottom;
    
    private int slideTabHeight,slideTabX, slideTabWidth;
    private int groundButtonX,groundButtonWidth,groundButtonHeight;
    
    //functional variables
    private  int slideTabCurrY; // the offset of the tab in ZY direction up that can be changed by dragging or the Ytarget in Kadvcreation
    private  int groundButtonCurrY ;
    private  int offsetFromTabTop; //offset of mouse click from middle of the slide tab
    private  boolean slideTabWasSelected; //The slide tab was selected and will keep being selected untill the mouse is released
    
    //buttons and elements
    // the sliderWindow
    private  GuiOverlayBaseElement sliderWindow;
    // the buttons in the sliderWindow
    private  GuiOverlayBaseButton slideTab,groundButton;

    private  double guiToYtargetScale;
    
    

    @Override
    protected void initGuiOverlay()
    {
        elementlist.clear();
        
        //size variables
        int sliderY = 30, slideTabY = 6;
        
        slideTabHeight = 15;
        slideTabX = 6;
        slideTabWidth = 12;
    
        groundButtonX =0;
        groundButtonWidth = 24;
        groundButtonHeight = 15;
        
        //slider inner dimension
        sliderInnerY = 200;
        sliderTop = sliderY + slideTabY;
        sliderBottom = sliderY + slideTabY + sliderInnerY;
    
        //functional parameters
        //slider & ground button ZY position
        slideTabCurrY = -1;
        groundButtonCurrY = sliderY + slideTabY;
        
        //offset of mouse click from middle of the slide tab
        offsetFromTabTop = 0;
        slideTabWasSelected = false;
        
        guiToYtargetScale = ConfigurationHandler.cameraConfig.MAX_CAMERA_HEIGHT/(sliderInnerY - slideTabHeight); // the amount of ZY target change per pixel change
        
        mainTexture  = new ResourceLocation(Reference.MODID,"textures/gui/groundslider_gui_overlay.png");
        tabs = new ResourceLocation(Reference.MODID,"textures/gui/tabs.png");
        
        // the sliderWindow containing mode buttons
        sliderWindow = new GuiOverlayBaseElement(mainTexture,0,sliderY,0,0,24,212);
        sliderWindow.setName("Slider Window");
        // the buttons in the sliderWindow
        slideTab = new GuiOverlayBaseButton(tabs,slideTabX,slideTabY,2,232,0,slideTabWidth,slideTabHeight,12,12);
        slideTab.setName("Slide Tab");
        slideTab.setTooltip("Change camera height");
        groundButton = new GuiOverlayBaseButton(mainTexture,groundButtonX,0,1,24,0,groundButtonWidth,groundButtonHeight,48,24);
        groundButton.setName("Ground Button");
        groundButton.setTooltip("Follow ground height");
        sliderWindow.addElement(slideTab);
        sliderWindow.addElement(groundButton);
    
  
        //all first level element to elementlist
        elementlist.add(sliderWindow);
        super.initGuiOverlay();
    }

    @Override
    protected void preHoverOnElements(int x_resized, int y_resized, long time)
    {
        if(slideTabCurrY <= 0)
        {
            setSlideTabCurrY(IsometricMovement.getTargetY());
        }
        
        //### PROCESS SLIDE TAB MOVEMENT ###
        // if the tab is selected and the leftbutton is still down, change the position of the tab and
        // change the Ytarget to the new position
        if(slideTabWasSelected)
        {
            slideTabCurrY = y_resized + offsetFromTabTop;

            //confine the tabs to the size of the inner area of the slider
            if(slideTabCurrY < sliderTop)
            {
                slideTabCurrY = sliderTop;
            }
            else if(slideTabCurrY > sliderBottom - slideTabHeight)
            {
                slideTabCurrY = sliderBottom - slideTabHeight;
            }

            if(Mouse.isButtonDown(0))
                slideTab.setSelected(true);
            else
            {


                //calculate pixel distance from tab to bottom of the slider
                int slideTabOffsetY =  sliderBottom - slideTabHeight  - slideTabCurrY ;
                //scale it to the height of the player
                double newTargetY = slideTabOffsetY*guiToYtargetScale;

                IsometricMovement.teleportTo(newTargetY);

                //get the new targetY from AdvCreation and set the tab to it
                setSlideTabCurrY(IsometricMovement.getTargetY());

                slideTab.setSelected(false);
                slideTabWasSelected = false;
            }
        }
        else
        {
            slideTab.setSelected(false);
            slideTabWasSelected = false;
        }
    
        //### GROUND BUTTON POSITION ###
        //calculate the y of the groundbutton
        //get the new targetY from AdvCreation and set the tab to it
        setGroundButtonCurrY(IsometricMovement.getGroundY());
    
        //### GROUND BUTTON ACTIVATED ###
        //actions when the groundbutton is active
        if (IsometricMovement.isSetTargetToGround())
        {
            groundButton.setSelected(true);
            slideTabCurrY = groundButtonCurrY;
        }
        else
        {
            groundButton.setSelected(false);
        }
    
    
        slideTab.setY(slideTabCurrY);
        groundButton.setY(groundButtonCurrY);
    
        super.preHoverOnElements(x_resized, y_resized, time);
    }

    @Override
    protected void preDrawElements(int x_resized, int y_resized, long time)
    {
        if(slideTabWasSelected && Mouse.isButtonDown(0))
        {
            resetAllHover();
            slideTab.setHoverOn(true);
        }
        
        super.preDrawElements(x_resized, y_resized, time);
    }

    @Override
    protected void preDrawTooltips(int x_resized, int y_resized, long time)
    {
        //### DRAW BLOCK ICON ###
        CustomGuiUtils.drawBlockIcon(mc,new ItemStack(Blocks.GRASS),0.75F,slideTabX,groundButtonCurrY+1,0, EnumFacing.EAST,false,false);
        super.preDrawTooltips(x_resized, y_resized, time);
    }

    @Override
    public boolean leftClick(int x, int y, boolean worldIsRemote) {
        if(slideTabWasSelected && Mouse.isButtonDown(0))
        {
            return true;
        }
        return super.leftClick(x, y, worldIsRemote);
    }

    @Override
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        if(element != null)
        {
            super.actionPerformed(element, mouseX, mouseY, time, worldIsRemote);
            // if the tab is selected don't perform any other action and deactivate the ground button
            if (element.getName().equals(slideTab.getName()))
            {
                offsetFromTabTop = slideTabCurrY - mouseY;
                // the player will nog longer follow the ground
                IsometricMovement.setSetTargetToGround(false);
                slideTabWasSelected = true;
            }
            else if (element.getName().equals(groundButton.getName()))
            {
                IsometricMovement.setSetTargetToGround(true);
                IsometricMovement.TELEPORT_ACTIVE = true;
            }
            
        }

    }
    
    public int getSlideTabCurrY() {
        return slideTabCurrY;
    }
    
    public void setSlideTabCurrY(double targetY)
    {
        int newY = (sliderBottom - slideTabHeight) - (int) (targetY/guiToYtargetScale);
        if(newY < sliderTop)
        {
            newY = sliderTop;
        }
        else if(newY > sliderBottom - slideTabHeight)
        {
            newY = sliderBottom - slideTabHeight;
        }
        
        this.slideTabCurrY = newY;
    }
    
    public  int getGroundButtonCurrY() {
        return groundButtonCurrY;
    }
    
    public  void setGroundButtonCurrY(double groundCoord) {
        this.groundButtonCurrY = (sliderBottom - slideTabHeight) - (int) (groundCoord/guiToYtargetScale);
    }
}
