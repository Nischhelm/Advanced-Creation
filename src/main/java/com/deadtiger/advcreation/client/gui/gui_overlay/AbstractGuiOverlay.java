package com.deadtiger.advcreation.client.gui.gui_overlay;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseElement;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.logging.Logging;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.template.TemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

public abstract class AbstractGuiOverlay extends Gui
{
    protected final Minecraft mc = Minecraft.getMinecraft();
    
    protected ResourceLocation mainTexture = new ResourceLocation(Reference.MODID,"textures/gui/buildmode_gui_overlay.png");
    
    //remembers if this screen has already been initialised
    protected boolean isInitialised = false;

    //the list of elements
    protected ArrayList<GuiOverlayBaseElement> elementlist = new ArrayList<>();
    //the highest XZ value in all of the elements and child elements in elementlist
    int maxZ;
    
    //keeps track of which element was hovered on last for purposes of the tooltip timing
    protected GuiOverlayBaseElement lastHoverOnElement = null;
    
    //measure time spend hovering on a button and only showing tooltip after a time
    protected long beginHover = 0;
    protected long timeDelay = 500; //delay in ms before tooltip is shown


    protected void initGuiOverlay()
    {
        maxZ = 0;
        for (GuiOverlayBaseElement element : elementlist)
        {
            if(element.getZ() > maxZ)
            {
                maxZ = element.getZ();
            }
        }
        isInitialised = true;
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event)
    {
        GuiOverlayManager.updateGuiVisibility();
        if((GuiOverlayManager.isGuiOverlayVisible()) && IsometricCamera.isPlayerInIsometricPerspective() &&
               ((event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) ||
               (event.getType() == RenderGameOverlayEvent.ElementType.TEXT ))) {
            Minecraft mc = Minecraft.getMinecraft();
    
            int X_mouse = Mouse.getX();
            int Y_mouse = Mouse.getY();
    
            // ### GUI DIMENSIONS ###
            final ScaledResolution scaledresolution = new ScaledResolution(mc);
            int width = scaledresolution.getScaledWidth();
            int height = scaledresolution.getScaledHeight();
    
            //#### SCALE  MOUSE COORD ###
            //scale mouse coordinate from window to minecraft gui scale
            int[] resizedCoord = CustomGuiUtils.scaleMouseCoord(X_mouse, Y_mouse);
            int X_resized = resizedCoord[0];
            int Y_resized = resizedCoord[1];
    
            long time = System.currentTimeMillis();
    
    
            if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
        
                //initialise guiOverlay
                if (!isInitialised) {
                    initGuiOverlay();
                }


                    preHoverOnElements(X_resized, Y_resized, time);

                    // ### CHECK HOVER ON BUTTON ###
                    resetAllHover();
                    checkHoverOnElements(X_resized, Y_resized, time);

                    preDrawElements(X_resized, Y_resized, time);


                //draw all elements
                drawAllElements(time);
            }
            else if (event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS && Minecraft.getMinecraft().currentScreen == null)
            {
                preDrawTooltips(X_resized, Y_resized, time);
                if(Minecraft.getMinecraft().currentScreen == null)
                {
                    // ### DRAW TOOL TIP ###
                    // It should be draw over everything
                    drawTooltips(width, height, X_resized, Y_resized, time);
                }
                postDrawTooltips(X_resized, Y_resized, time);
            }
        }
    }

    //Override these methods to inject changes before or after stages of the rendering
    protected void preHoverOnElements(int x_resized, int y_resized, long time) {}

    protected void preDrawElements(int x_resized, int y_resized, long time) {}
    
    protected void preDrawTooltips(int x_resized, int y_resized, long time) {}

    protected void postDrawTooltips(int x_resized, int y_resized, long time) {}

    protected void drawTooltips(int screenWidth, int screenHeight, int x_resized, int y_resized, long time)
    {
        if(beginHover != 0 && time-beginHover >= timeDelay) {
            if (lastHoverOnElement != null)
            {
                lastHoverOnElement.drawTooltip(x_resized, y_resized, screenWidth, screenHeight);
            }
        }
    }
    
    protected void drawAllElements(long time)
    {
        GlStateManager.enableAlpha();
        for(int z_i = 0;z_i <= maxZ;z_i++)
        {
            for (GuiOverlayBaseElement element : elementlist)
                element.draw(this,z_i, time);
            
        }
        GlStateManager.disableAlpha();
    }
    
    protected long checkHoverOnElements(int x_resized, int y_resized,long time)
    {
        //if click is within any of the button do that action
        GuiOverlayBaseElement currHoverOnElement = null;
        
        for(int z = maxZ;z >= 0;z--)
        {
            //Check if you are hovering on any of the element
            for (GuiOverlayBaseElement element : elementlist) {
                GuiOverlayBaseElement newHoverOnElement = element.checkHoverOn(x_resized, y_resized,z);
                if(newHoverOnElement != null)
                {
                    if(currHoverOnElement== null)
                    {
                        currHoverOnElement = newHoverOnElement;
                        if ((beginHover == 0) || (!lastHoverOnElement.getName().equals(newHoverOnElement.getName())))
                        {
                            beginHover = time;
                        }
                    }
                }
            }
            
        }
        lastHoverOnElement = currHoverOnElement;
        if(lastHoverOnElement == null)
            beginHover = 0;
        return time;
    }

    public void resetAllHover()
    {
        //reset the condition of all elements
        for (GuiOverlayBaseElement element : elementlist)
        {
            element.resetAllHover();
        }
        
    }


    /***
     * checks if the player leftclicks on one of the buttons or on the window of the gui Overlay
     * @param x
     * @param y
     * @param worldIsRemote
     * @return true when the player clicks within the window
     */
    public boolean leftClick(int x, int y, boolean worldIsRemote)
    {
        //#### SCALE  MOUSE COORD ###
        //scale mouse coordinate from window to minecraft gui scale
        int[] resizedCoord = CustomGuiUtils.scaleMouseCoord(x,y );
        int X_resized =  resizedCoord[0];
        int Y_resized =  resizedCoord[1];
    
        long time = System.currentTimeMillis();
        
        if(lastHoverOnElement != null)
        {
            boolean wasAlreadySelected = lastHoverOnElement.isSelected();
            GuiOverlayBaseElement newElement = lastHoverOnElement.trySelect(X_resized,Y_resized, CustomGuiUtils.EnumMouseButtonClick.LEFT_CLICK, time);
            if(newElement != null)
            {
                if(!wasAlreadySelected && newElement.isEnabled())
                    actionPerformed(newElement, X_resized,Y_resized, time, worldIsRemote);
                return true;
            }
        }
       
        return false;
    }

    /***
     * checks if the player rightclicks on one of the buttons or on the window of the gui Overlay
     * @param x
     * @param y
     * @param worldIsRemote
     * @return true when the player clicks within the window
     */
    public boolean rightClick(int x, int y, boolean worldIsRemote)
    {
        //#### SCALE  MOUSE COORD ###
        //scale mouse coordinate from window to minecraft gui scale
        int[] resizedCoord = CustomGuiUtils.scaleMouseCoord(x,y );
        int X_resized =  resizedCoord[0];
        int Y_resized =  resizedCoord[1];
        
        long time = System.currentTimeMillis();
    
        if(lastHoverOnElement != null)
        {
            boolean wasAlreadySelected = lastHoverOnElement.isSelected();
            GuiOverlayBaseElement newElement = lastHoverOnElement.trySelect(X_resized,Y_resized, CustomGuiUtils.EnumMouseButtonClick.LEFT_CLICK, time);
            if(newElement != null)
            {
                if(!wasAlreadySelected && newElement.isEnabled())
                    actionRightClickPerformed(newElement, X_resized,Y_resized, time, worldIsRemote);
                return true;
            }
            
//
//            GuiOverlayBaseElement newElement = lastHoverOnElement.trySelect(X_resized,Y_resized, GuiUtils.EnumMouseButtonClick.RIGHT_CLICK, time);
//            if(newElement != null)
//            {
//                return true;
//            }
        }
        
        return false;
    }
    
    protected void actionPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {
        logGuiOverlayPress(AdvCreation.getMode(),element,"");
    
    }
    
    protected void actionRightClickPerformed(GuiOverlayBaseElement element, int mouseX, int mouseY, long time, boolean worldIsRemote)
    {

        logGuiOverlayPress(AdvCreation.getMode(),element,"");
    }
    
    public void updateChangedMainMode(EnumMainMode mode)
    {
    }

    public boolean isInitialised() {
        return isInitialised;
    }
    
    public void setInitialised(boolean initialised) {
        isInitialised = initialised;
    }
    
    public void setVisibility(boolean visible)
    {
        for (GuiOverlayBaseElement element : elementlist)
        {
            element.setVisibility(visible);
        }
    }

    public static void logGuiOverlayPress(EnumMainMode mode, GuiOverlayBaseElement baseElement, String comment)
    {

        comment = "button on GuiOverlay/HUD";

        if(mode.equals(EnumMainMode.BUILD))
        {
            String newComment = "pressed '" + baseElement.getName() + "' on '" + AdvCreation.getMode().name() +"' in '" + BuildMode.TOOLMODE.toolModeName + "' at '" + BuildMode.RIGHT_CLICK_NUMBER + "' with deletemode '" + BuildMode.DELETE_MODE;
            Logging.logMouseClick(mode.index,BuildMode.TOOLMODE.identificationIndex,baseElement.index,BuildMode.RIGHT_CLICK_NUMBER,true, newComment);
        }
        else if(mode.equals(EnumMainMode.EDIT))
        {
            String newComment = "pressed '" + baseElement.getName() + "' on '" + AdvCreation.getMode().name() +"' in '" + EditMode.ADJUST_MODE.toolModeName + "' at '" + EditMode.RIGHT_CLICK_NUMBER + "' with deletemode '" + EditMode.DELETE_MODE;
            Logging.logMouseClick(mode.index,BuildMode.TOOLMODE.identificationIndex,baseElement.index,BuildMode.RIGHT_CLICK_NUMBER,true, newComment);
        }
        else if(mode.equals(EnumMainMode.PLACE))
        {
            int select_index = GuiTemplaceInventoryScreenFunctionality.getSelected_index();
            String newComment = "pressed '"+ baseElement.getName() + "' on '" + AdvCreation.getMode().name() +"' with template None :" + comment;
            if(TemplateManager.TEMPLATES_LIST.size() > select_index && 0 <= select_index )
            {
                String filename = TemplateManager.FILENAME_LIST.get(select_index);
//                        Logging.logMouseClick(AdvCreation.getMode().index,1,2, 1, true);
                newComment = "pressed '"+ baseElement.getName() + "' on '" + AdvCreation.getMode().name() +"' with " + filename+ "' :" + comment;
            }
            Logging.logMouseClick(AdvCreation.getMode().index, PlaceTemplateMode.getCurrToolId(),baseElement.index,1,true,newComment);

        }
        else
        {
            String newComment = "pressed '"+ baseElement.getName() +"' on '" + AdvCreation.getMode().name() + "' at '" + BuildTemplateMode.MODE.name() + "' :" + comment;
            Logging.logMouseClick(AdvCreation.getMode().index,1,baseElement.index,BuildTemplateMode.MODE.index,true,newComment);

        }
    }

    public void updateToolModeIndication(EnumMainMode mode)
    {
    }

    public void updateToolModeIndication(EnumMainMode mode, String overwriteText)
    {
    }

    public void updateToolModeIndication(EnumMainMode mode, boolean skipable)
    {

    }

    public void updateToolModeIndication(EnumMainMode mode, String overwriteText, boolean fullbar , boolean secondColor)
    {
    }

    public void updateToolModeIndication(EnumMainMode mode, String overwriteText,boolean fullbar ,boolean secondColor, int overwriteColor)
    {
    }

    public void updateToolModeIndication(EnumMainMode mode, String overwriteText, boolean fullbar, boolean secondColor, int overwriteColor, boolean holdMessage)
    {

    }

    protected int applyColor(int overwriteColor, int currColor)
    {
        if (overwriteColor == 0)
            return currColor;
        else
            return overwriteColor;
    }

    public void setRelativeBarLength(int fraction, int max)
    {

    }

    public void resetBarLength()
    {
    }

}
