package com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen;

import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.GuiBigTemplateButton;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.template.TemplateManager;
import com.deadtiger.advcreation.utility.IconMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GuiCreateIconScreen extends GuiScreen
{
    ResourceLocation templateBackground = new ResourceLocation(Reference.MODID, "textures/gui/template_selection_gui_overlay.png");
    
    
    public GuiBigTemplateButton templateButton;
    public Template template;
    public int index;
    
    private int guiScreenWidth = 480;
    private int guiScreenHeight = 270;
    
    //makes all guiScreen icon to actual size
    private double normalGuiScreenWidth = 480.0;
    private double normalguiScreenHeight = 270.0;
    //1280×720
    private int windowWidth = 270;
    private int windowHeight = 270;
    
    //save screen dimensions and other information
    DisplayMode oldDisplayMode = null;
    boolean wasFullscreen = false;
    EnumFacing previousRotation = EnumFacing.WEST;
    
    ArrayList<Template> listOfTemplates = new ArrayList<>();
    int waitTimer = 0;
    int extraWaitTimer = 0;
    int waitTimerStart = 4;
    int templateIndex = 0;
    //buffer for image being made
    BufferedImage bufferedimage;
    int j;
    int yCoord = 0;
    
    boolean debug = false;
    private float div = 10f;
    
    public GuiCreateIconScreen(Template template, int index, ArrayList<Template> listOfTemplates) {
        this.template = template;
        this.index = index;
        this.listOfTemplates = listOfTemplates;
    }
    
    public GuiCreateIconScreen(Template template, int index) {
        this.template = template;
        this.index = index;
        this.listOfTemplates = new ArrayList<>();
        this.debug = true;
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        
        
        this.j = 0;
        templateButton = new GuiBigTemplateButton(0,270 /2 ,  270/2,windowWidth,windowHeight);
//        templateButton = new GuiBigTemplateButton(0,guiScreenWidth/2 ,  guiScreenHeight/2,260,260);
        templateButton.setTemplate(template,index);
        templateButton.setDrawNewIcon(true);
        buttonList.add(templateButton);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
    
//        drawTexturedModalRect((guiScreenWidth / 2) - windowWidth/2 , 0 , 5, 5, 50, 50);
        drawModalRectWithCustomSizedTexture((guiScreenWidth / 2) - windowWidth/2 , 0 , 5, 5, 50, 50,guiScreenWidth,guiScreenWidth);
        super.drawScreen(mouseX, mouseY, partialTicks);
    
        String createIconsTitle = "!! PLEASE WAIT, icons are being made !!";
        drawTitle(createIconsTitle,10, (guiScreenHeight ) + 20);
    
        if(!debug)
        {
            int offset = -35;
            int offset_size = 65;
    
            if(waitTimer == 0)
            {

                if(!TemplateManager.TEMPLATES_LIST.isEmpty())
                    previousRotation = TemplateManager.TEMPLATES_LIST.get(0).getRotation();
        
                for (Template template : TemplateManager.TEMPLATES_LIST)
                {
                    while(template.getRotation() != EnumFacing.WEST)
                        template.rotateY();
                }
        
                oldDisplayMode = new DisplayMode(mc.displayWidth,mc.displayHeight);
        
                if(Display.isFullscreen())
                {
                    wasFullscreen = true;
                    mc.toggleFullscreen();
                }
        
        
                //change the display to the right resolution
                DisplayMode display = new DisplayMode(720,720);
                try {
                    Display.setDisplayMode(display);
//                    mc.displayWidth = (int)normalGuiScreenWidth*3;
//                    mc.displayHeight = (int)normalguiScreenHeight*3;
            
                } catch (LWJGLException e) {
                    e.printStackTrace();
                }


                this.setGuiSize(mc.displayWidth,mc.displayHeight);
                this.setWorldAndResolution(mc,mc.displayWidth,mc.displayHeight);


                waitTimer++;
        
            }
            else if(waitTimer == 1)
            {
                if(extraWaitTimer < 10)
                    extraWaitTimer++;
                else
                    waitTimer++;
        
            }
            else if(waitTimer == 2)
            {
                templateButton.setOverRideHover(true);
                templateButton.setHovered(false);
                yCoord = mc.displayHeight-240-(270-offset);
                if(j-1>= 0)
                    bufferedimage = IconMaker.saveScreenshot(listOfTemplates.get(j-1),false, mc.gameDir,0,yCoord,guiScreenWidth+offset_size,guiScreenWidth+offset_size, 0,0,mc.getFramebuffer(),bufferedimage,true);


                if(j<listOfTemplates.size())
                {
                    templateButton.setTemplate(listOfTemplates.get(j),j);
                    j++;
                }
                else
                {
                    j = 0;
                    for (Template template : TemplateManager.TEMPLATES_LIST)
                    {
                        template.rotateY();
                    }
                    waitTimer++;
                }
        
        
        
            }
            else if(waitTimer == 3)
            {
                templateButton.setOverRideHover(true);
                templateButton.setHovered(false);
                if(j-1>= 0)
                    bufferedimage = IconMaker.saveScreenshot(listOfTemplates.get(j-1),false, mc.gameDir,0,yCoord,guiScreenWidth+offset_size,guiScreenWidth+offset_size, 0,0,mc.getFramebuffer(),bufferedimage,true);
        
                if(j<listOfTemplates.size())
                {
                    templateButton.setTemplate(listOfTemplates.get(j),j);
                    j++;
                }
                else
                {
                    j = 0;
                    for (Template template : TemplateManager.TEMPLATES_LIST) {
                        template.rotateY();
                    }
                    waitTimer++;
                }
            }
            else if(waitTimer == 4)
            {
                templateButton.setOverRideHover(true);
                templateButton.setHovered(false);
                if(j-1>= 0)
                    bufferedimage = IconMaker.saveScreenshot(listOfTemplates.get(j-1),false, mc.gameDir,0,yCoord,guiScreenWidth+offset_size,guiScreenWidth+offset_size, 0,0,mc.getFramebuffer(),bufferedimage,true);
        
                if(j<listOfTemplates.size())
                {
                    templateButton.setTemplate(listOfTemplates.get(j),j);
                    j++;
                }
                else
                {
                    j = 0;
                    for (Template template : TemplateManager.TEMPLATES_LIST) {
                        template.rotateY();
                    }
                    waitTimer++;
                }
            }
            else if(waitTimer == 5)
            {
                templateButton.setOverRideHover(true);
                templateButton.setHovered(false);
                if(j-1>= 0)
                    bufferedimage = IconMaker.saveScreenshot(listOfTemplates.get(j-1),false, mc.gameDir,0,yCoord,guiScreenWidth+offset_size,guiScreenWidth+offset_size, 0,0,mc.getFramebuffer(),bufferedimage,true);
        
                if(j<listOfTemplates.size())
                {
                    templateButton.setTemplate(listOfTemplates.get(j),j);
                    j++;
                }
                else
                {
                    j = 0;
                    for (Template template : TemplateManager.TEMPLATES_LIST) {
                        template.rotateY();
                    }
                    waitTimer++;
                }
            }
            else if(waitTimer == 6)
            {
                templateButton.setOverRideHover(true);
                templateButton.setHovered(true);
                if(j-1>= 0)
                    bufferedimage = IconMaker.saveScreenshot(listOfTemplates.get(j-1),true, mc.gameDir,0,yCoord,guiScreenWidth+offset_size,guiScreenWidth+offset_size, 0,0,mc.getFramebuffer(),bufferedimage,true);
        
                if(j<listOfTemplates.size())
                {
                    templateButton.setTemplate(listOfTemplates.get(j),j);
                    j++;
                }
                else
                {
                    j = 0;
                    for (Template template : TemplateManager.TEMPLATES_LIST) {
                        template.rotateY();
                    }
                    waitTimer++;
                }
            }
            else if(waitTimer == 7)
            {
                templateButton.setOverRideHover(true);
                templateButton.setHovered(true);
                if(j-1>= 0)
                    bufferedimage = IconMaker.saveScreenshot(listOfTemplates.get(j-1),true, mc.gameDir,0,yCoord,guiScreenWidth+offset_size,guiScreenWidth+offset_size, 0,0,mc.getFramebuffer(),bufferedimage,true);
        
                if(j<listOfTemplates.size())
                {
                    templateButton.setTemplate(listOfTemplates.get(j),j);
                    j++;
                }
                else
                {
                    j = 0;
                    for (Template template : TemplateManager.TEMPLATES_LIST) {
                        template.rotateY();
                    }
                    waitTimer++;
                }
            }
            else if(waitTimer == 8)
            {
                templateButton.setOverRideHover(true);
                templateButton.setHovered(true);
                if(j-1>= 0)
                {
                    bufferedimage = IconMaker.saveScreenshot(listOfTemplates.get(j-1),true, mc.gameDir,0,yCoord,guiScreenWidth+offset_size,guiScreenWidth+offset_size, 0,0,mc.getFramebuffer(),bufferedimage,true);
                    listOfTemplates.get(j-1).calculatedProperties = false;
                    listOfTemplates.get(j-1).tryCalculateProperties();
                    listOfTemplates.get(j-1).createOnlyPropertiesFile();
                }
        
                if(j<listOfTemplates.size())
                {
                    templateButton.setTemplate(listOfTemplates.get(j),j);
                    j++;
                }
                else
                {
                    j = 0;
                    for (Template template : TemplateManager.TEMPLATES_LIST) {
                        template.rotateY();
                    }
                    waitTimer++;
                }
            }
            else if(waitTimer == 9)
            {
                templateButton.setOverRideHover(true);
                templateButton.setHovered(true);
                if(j-1>= 0)
                {
                    bufferedimage = IconMaker.saveScreenshot(listOfTemplates.get(j-1),true, mc.gameDir,0,yCoord,guiScreenWidth+offset_size,guiScreenWidth+offset_size, 0,0,mc.getFramebuffer(),bufferedimage,true);
//                    listOfTemplates.get(j-1).tryCalculateProperties();
//                    listOfTemplates.get(j-1).createOnlyPropertiesFile();
                    listOfTemplates.get(j-1).zipTemplate();
                }
        
                if(j<listOfTemplates.size())
                {
                    templateButton.setTemplate(listOfTemplates.get(j),j);
                    j++;
                }
                else
                {
                    j = 0;
                    for (Template template : TemplateManager.TEMPLATES_LIST) {
                        template.rotateY();
                    }
                    waitTimer++;
                }
            }
            else if(waitTimer == 10)
            {
                // set the displayMode to the old display size again
        
                if(wasFullscreen)
                {
                    mc.toggleFullscreen();
                }
                else
                {
                    try
                    {
                        Display.setDisplayMode(oldDisplayMode);
                
                    } catch (LWJGLException e) {
                        e.printStackTrace();
                    }
            
                    mc.updateDisplay();

                    Display.setResizable(false);
                    Display.setResizable(true);
                }
                File mcDataDir = Minecraft.getMinecraft().gameDir;
                File saves = new File(mcDataDir,"advcreation_templates");



                mc.displayGuiScreen(new GuiTemplateInventoryScreenSimple());
            }
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(debug)
        {
            mc.displayGuiScreen(new GuiTemplateInventoryScreenSimple());
        }
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(debug)
        {
            int ROTATE_TEMPLATE_ID = 19;
            switch (keyCode)
            {
                case 203: //left arrow
                    index = templateButton.getTemplateIndex() -1;
                    if(index >= 0 && index < TemplateManager.TEMPLATES_LIST.size())
                        template = TemplateManager.TEMPLATES_LIST.get(index);
                    else
                        template = null;
                    templateButton.setTemplate(template,index);
                    break;
                case 205:   //right arrow
                    index = templateButton.getTemplateIndex() +1;
                    if(index >= 0 && index < TemplateManager.TEMPLATES_LIST.size())
                        template = TemplateManager.TEMPLATES_LIST.get(index);
                    else
                        template = null;
                    templateButton.setTemplate(template,index);
                    break;
                    
                case 48: //B
                    CustomGuiUtils.scaleOverride = !CustomGuiUtils.scaleOverride;
    
                case 200: //up arrow
                    CustomGuiUtils.scaleOverrideNbr += 1/div;
                    break;
                case 208: //down arrow
                    CustomGuiUtils.scaleOverrideNbr -= 1/div;
                    break;
    
                case 201://page up
                    div = div*10F;
                    System.out.println(div);
                    break;
                case 209:// page down
                    div = div/10F;
                    System.out.println(div);
                    break;
                case 19:
        
                    // rotation of the templates
                    for (Template template : TemplateManager.TEMPLATES_LIST) {
                        template.rotateY();
                    }
                    System.out.println("rotate to " + TemplateManager.TEMPLATES_LIST.get(0).getRotation());
        
                    break;
            }
        }
    
       
    }
    public void drawTitle(String title, double x, double y)
    {
        double scale = 1.5;
        GlStateManager.pushMatrix();
        {
            GlStateManager.scale(scale, scale, scale);
            fontRenderer.drawString(title, (int) Math.ceil(x * (1 / scale)), (int) Math.ceil(y * (1 / scale)), 0xFFFFFF);
        }
        GlStateManager.popMatrix();
    }
    
}
