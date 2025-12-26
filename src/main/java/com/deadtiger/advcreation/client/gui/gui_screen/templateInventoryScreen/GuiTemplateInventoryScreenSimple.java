package com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.*;
import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.client.input.KeyInputHandler;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.client.gui.gui_screen.reportScreen.ReportScreen;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.report.Report;
import com.deadtiger.advcreation.template.TemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GuiTemplateInventoryScreenSimple extends GuiScreen
{
    ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/gui/custom_windows.png");
    ResourceLocation templateBackground = new ResourceLocation(Reference.MODID, "textures/gui/template_selection_gui_overlay.png");
    ResourceLocation sliderBackground = new ResourceLocation(Reference.MODID, "textures/gui/groundslider_gui_overlay.png");
    ResourceLocation slideTabTexture = new ResourceLocation(Reference.MODID, "textures/gui/tabs.png");
    ResourceLocation refreshTexture = new ResourceLocation(Reference.MODID, "textures/gui/widgets.png");
    ResourceLocation trashTexture = new ResourceLocation(Reference.MODID, "textures/gui/trashbutton.png");


    // size of the main gui window
    private int guiWidth = 256;
    private int guiHeight = 256;
    
    //size of the menu screen? is always a low resolution? because ... why not?
    private int guiScreenWidth = 480;
    private int guiScreenHeight = 270;
    
    //makes all guiScreen icon to actual size
    private double normalGuiScreenWidth = 480.0;
    private double normalguiScreenHeight = 270.0;

    private int windowWidth = 230;
    private int startHeight;
    private int xOffset = 7;
    
    //gui screen texts
    private String title = "Select a template";
    
    private ArrayList<Integer> displayedTemplateIndices = new ArrayList<>();
    int display_index =0;
    
    private GuiTemplateButton mouseTemplate;
    private GuiFreeButton slideTab;
    private GuiFreeButton refreshInventoryButton;
    private GuiButton moveToTrashButton;
    private HashMap<Integer,Integer> slideTabPostions = new HashMap<>();// pixel pos => display index
    private int distSlideTabMouse = 0; //distance between the mouse and slidetab center when dragging
    
    //keypress  keycode
    private static final int LEFT_ARROW_KEY_ID = 203;
    private static final int DOWN_ARROW_KEY_ID = 208;
    private static final int RIGHT_ARROW_KEY_ID = 205;
    private static final int UP_ARROW_KEY_ID = 200;
    private static final int PAGE_UP_KEY_ID = 201;
    private static final int PAGE_DOWN_KEY_ID = 209;
    private static final int ROTATE_TEMPLATE_ID = 19;
    
    
    DisplayMode oldDisplayMode = null;
    boolean wasFullscreen = false;
    boolean createIcon = false;
    boolean deleteIcons = false;
    boolean activateIconCreation = false;


    //buffer for image being made
    ArrayList<Integer[]> listOfCoordinateToCreateIcons = new ArrayList<>();
    ArrayList<Template> listOfTemplates = new ArrayList<>();
    Integer[][] listCoordinates = {{ 366,501},{537,501},{708,501},{879,501},{366,327},{537,327},{708,327},{879,327}};

    //fields for search boxes
    private static String currTemplateName;
    private GuiTextFieldFillIn templateName;
    private static String currCategoryName;
    private GuiTextFieldDropDown category;
    private static String currStyleName;
    private GuiTextFieldDropDown style;
    private static String currFunctionName;
    private GuiTextFieldDropDown function;
    public static ArrayList<String> currMcVersionNames = new ArrayList<>();
    private static boolean currMcVersionInitialised = false;

    private boolean firstClick = false;

    private ArrayList<GuiTextFieldOption> dropDownOptions = new ArrayList<>();
    private ArrayList<GuiTextFieldFillIn> customTextFields = new ArrayList<>();
    private ArrayList<GuiFreeToggleButton> versionTabs = new ArrayList<>();
    
    public static float div = 10;
    public static boolean add = true;

    public static int keyScrollSpeed = 120;

    private ArrayList<GuiTemplateButton> listOfHotbarTemplateButtons = new ArrayList<>();


    @Override
    public void initGui() {
        buttonList.clear();

        versionTabs.clear();

        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        guiScreenWidth = scaledresolution.getScaledWidth();
        guiScreenHeight = scaledresolution.getScaledHeight();
    
        int centerX = (guiScreenWidth / 2) - guiWidth / 2;
        int centerY = (guiScreenHeight / 2) - guiHeight / 2;
        startHeight = centerY +25+ 5 + 10 + 8;

        // Draw the templates in GUI
        ArrayList<Template> templates = TemplateManager.TEMPLATES_LIST;
        display_index = GuiTemplaceInventoryScreenFunctionality.displayed_index;

        //refrech button to reload the template from the advcreation_templates_zips folder
        refreshInventoryButton = new GuiFreeButton(12,refreshTexture,(guiScreenWidth / 2) + windowWidth/2 -xOffset +1+9,startHeight + 59 + 60 +10+10,20,20,40,206,40,226);
        buttonList.add(refreshInventoryButton);

        moveToTrashButton = new GuiFreeButton(13,trashTexture,(guiScreenWidth / 2) + windowWidth/2 -xOffset + 55, startHeight + 60,60,60,0,0,140,0);

//        moveToTrashButton = new GuiFreeButton(12,refreshTexture,(guiScreenWidth / 2) + windowWidth/2 -xOffset +1+9,startHeight + 59 + 60 +10+10,20,20,b ->
//        {
//            this.refreshTemplates();
//        },40,206,40,226);
        buttonList.add(moveToTrashButton);


//        //a dummy button the fix a weird bug
//        buttonList.add(new GuiButton(1000,0,0,0,0,""));
        // the template buttons in the inventory
        for (int i = 0; i < 8; i++)
        {
            GuiTemplateButton newButton = null;
            newButton = new GuiTemplateButton(i,(guiScreenWidth / 2) - windowWidth/2 + 57*(i%4) + 58/2+1 - xOffset, startHeight + 58 * (i/4) + 58/2+1);
            buttonList.add(newButton);
            
            if (isaValidTemplateIndex(TemplateManager.TEMPLATES_LIST, display_index + i))
            {
                Template template = templates.get(display_index + i);
                newButton.setTemplate(template, display_index + i);
            }

        }
    
        //create the template buttons for the current hotbar of the player and copy the current selecte template indices to this screen
        for (int i = 0; i < 4; i++)
        {
    
            Template template = null;
            int index = GuiOverlayManager.getTemplateIndexOfTemplateButton(i);
            
            if (isaValidTemplateIndex(TemplateManager.TEMPLATES_LIST, index)) {
               
               template = templates.get( index);
               
            }
            
            GuiTemplateButton newButton = new GuiTemplateButton(8+i,(guiScreenWidth / 2) - windowWidth/2 + 57*(i%4) + 58/2+1 - xOffset, startHeight + 59 + 60 + 10 + 58/2+1);
            newButton.setTemplate(template,  index);
            buttonList.add(newButton);
            listOfHotbarTemplateButtons.add(newButton);
        }

        // the template icon that is attached to the mouse when you select a template
        mouseTemplate = new GuiTemplateButton(12,(guiScreenWidth / 2), (guiScreenHeight/2));
        mouseTemplate.visible =false;

        createAndInitialiseSlideTab();

        //the search textfields on top
        int textFieldWidth = 119;
        int textFieldHeight = 15;
        //textfield to enter the template name you are searching
        templateName = new GuiTextFieldFillIn(-1, fontRenderer, centerX + 7 + 0*(textFieldWidth+5),
                centerY+8+6, textFieldWidth, textFieldHeight,"Template Name");
        if(currTemplateName != null && !currTemplateName.isEmpty())
            templateName.setText(currTemplateName);

        function = new GuiTextFieldDropDown(-1, fontRenderer, centerX + 7 + 1*(textFieldWidth+5) ,
                centerY+8+6, textFieldWidth , textFieldHeight, BuildTemplateMode.FUNCTIONS,dropDownOptions,"Function");
        if(currFunctionName != null && !currFunctionName.isEmpty())
            function.setText(currFunctionName);

        category = new GuiTextFieldDropDown(-1, fontRenderer, centerX + 7 + 0*(textFieldWidth+5),
                centerY+25+6 , textFieldWidth, textFieldHeight,BuildTemplateMode.CATEGORIES,dropDownOptions,"Category");
        if(currCategoryName != null && !currCategoryName.isEmpty())
            category.setText(currCategoryName);

        style = new GuiTextFieldDropDown(-1, fontRenderer, centerX + 7 + 1*(textFieldWidth+5),
                centerY+25+6 , textFieldWidth, textFieldHeight,BuildTemplateMode.STYLES,dropDownOptions,"Style");
        if(currStyleName != null && !currStyleName.isEmpty())
            style.setText(currStyleName);

        customTextFields.clear();
        customTextFields.add(style);
        customTextFields.add(category);
        customTextFields.add(function);
        customTextFields.add(templateName);

        GuiFreeToggleButton newButton = new GuiFreeToggleButton(13,slideTabTexture,(guiScreenWidth / 2) - windowWidth/2 -xOffset + 14, startHeight + 58 + 59 + 5,28,12,0,80,0,80,0,116,0,116) ;
        System.out.println("currMcversionTab text " + Reference.MC_VERSION);
        newButton.displayString =  Reference.MC_VERSION;

        if(currMcVersionInitialised)
        {
            if(currMcVersionNames.contains(newButton.displayString))
            {
                newButton.setActive(true);
            }
        }
        else
        {
            newButton.setActive(true);
            if(!currMcVersionNames.contains(newButton.displayString))
            {
                currMcVersionNames.add(newButton.displayString);
            }
            currMcVersionInitialised = true;
        }

        versionTabs.add(newButton);
        this.buttonList.add(newButton);
        int better_i = 0;
        boolean currVersionFound = false;
        for (int i = 0; i < TemplateManager.templateMcVersionsFound.size(); i++)
        {
            if(TemplateManager.templateMcVersionsFound.get(i).equals(Reference.MC_VERSION))
            {
                currVersionFound = true;
                continue;
            }
            if(!currVersionFound)
                better_i = i+1;
            else
                better_i = i;
            newButton = new GuiFreeToggleButton(13,slideTabTexture,(guiScreenWidth / 2) - windowWidth/2 -xOffset + 28*(better_i )+ 14, startHeight + 58 + 59 + 5,28,12
                    ,0,80,0,80,0,116,0,116) ;
            newButton.displayString = (TemplateManager.templateMcVersionsFound.get(i));
            System.out.println("templateMcVersionsFound.get("+ i + ") " + TemplateManager.templateMcVersionsFound.get(i));
            versionTabs.add(newButton);
            if(currMcVersionNames.contains(newButton.displayString))
                newButton.setActive(true);
            this.buttonList.add(newButton);
        }
        super.initGui();
        this.updateDisplayIndices();
    }

    private void toggleVersionButton(GuiFreeToggleButton b)
    {
        GuiFreeToggleButton thisButton  = b;
        String thisButtonVersion = thisButton.displayString;
        if(thisButton.isActive())
        {
            if(!currMcVersionNames.contains(thisButtonVersion))
                currMcVersionNames.add(thisButtonVersion);
        }
        else
        {
            if(currMcVersionNames.contains(thisButtonVersion))
                currMcVersionNames.remove(thisButtonVersion);
        }
    }



    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
       
        drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        // ### GUI DIMENSIONS ###
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        guiScreenWidth = scaledresolution.getScaledWidth();
        guiScreenHeight = scaledresolution.getScaledHeight();
        
        
        //draw the main gui window mainTexture
        int centerX = (guiScreenWidth / 2) - guiWidth / 2;
        int centerY = (guiScreenHeight / 2) - guiHeight / 2;

        //draw the main screen mainTexture
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(centerX, centerY+60, 0, 75, guiWidth, guiHeight-75);
        drawTexturedModalRect(centerX, centerY+10, 0, 0, guiWidth, 50);
    
        //draw boxes with templates in
        mc.renderEngine.bindTexture(templateBackground);

        drawTexturedModalRect((guiScreenWidth / 2) - windowWidth/2 -xOffset, startHeight , 0, 0, windowWidth, 59);
        drawTexturedModalRect((guiScreenWidth / 2) - windowWidth/2 -xOffset, startHeight + 58, 0, 0, windowWidth, 59);
        drawTexturedModalRect((guiScreenWidth / 2) - windowWidth/2 -xOffset, startHeight + 59 + 60 +10, 0, 0, windowWidth, 59);

        //draw slider background
        mc.renderEngine.bindTexture(sliderBackground);
        drawTexturedModalRect((guiScreenWidth / 2) + windowWidth/2 -xOffset +1, startHeight , 5, 5, 16, 117);
        drawTexturedModalRect((guiScreenWidth / 2) + windowWidth/2 -xOffset +1, startHeight + 115 , 5, 205, 16, 2);

        //debugging functionality to delete all the generated icons from the template
        if(deleteIcons)
        {
            deleteIcons();
            deleteIcons = false;
        }

//
        ArrayList<Template> templates = new ArrayList<>();
       for(int ind:displayedTemplateIndices)
       {
           templates.add(TemplateManager.TEMPLATES_LIST.get(ind));
       }


        int i = 0;

        for (GuiButton button:buttonList)
        {
            if(i > 7)
                break;

            if(button instanceof GuiTemplateButton)
            {

                GuiTemplateButton templateButton = ((GuiTemplateButton )button);
                if (isaValidTemplateIndex(templates, display_index + i))
                {
                    Template template = templates.get(display_index + i);
                    templateButton.setTemplate(template, displayedTemplateIndices.get(display_index + i));
                    //  check if the template has icons otherwise make them
                    if((!createIcon && template.getIcon(false) == null))
                    {
                       Integer[] coordinates = listCoordinates[templateButton.id];
                       listOfCoordinateToCreateIcons.add(coordinates);
                       listOfTemplates.add(template);
                       activateIconCreation = true;
                    }
                }
                else
                    templateButton.setTemplate(null,-1);
                i++;

            }
        }
        if(activateIconCreation)
        {
            activateIconCreation = false;
            mc.displayGuiScreen(new GuiCreateIconScreen(null,-1,listOfTemplates));
        }

        //draws all the buttons on top
        super.drawScreen(mouseX, mouseY, partialTicks);
//
        if(refreshInventoryButton.isMouseOver())
            refreshInventoryButton.drawButton(mc,mouseX,mouseY,partialTicks,20,226);
        else
            refreshInventoryButton.drawButton(mc,mouseX,mouseY,partialTicks,20,206);

        for (GuiFreeToggleButton versionButton : versionTabs)
        {
            Color fontColor = new Color(0.3f,0.3f,0.3f);
            if(versionButton.isActive())
            {
                fontColor = new Color(1.0f,1.0f,1.0f);
                GuiScreenTextPrinter.drawText(versionButton.displayString, versionButton.x+3, versionButton.y+1, fontColor.getRGB(),0.75);
            }
            else
            {
                GuiScreenTextPrinter.drawText(versionButton.displayString, versionButton.x+3, versionButton.y+1, fontColor.getRGB(),0.75,false,false);

            }
            //            fontRenderer.drawShadow ( new MatrixStack(),);

            if(versionButton.isMouseOver())
                CustomGuiUtils.drawHoveringText("Show templates from MC" + versionButton.displayString, mouseX+7, mouseY,width,height,-1,fontRenderer);
        }

        GlStateManager.color(1f,1f,1f);

        //draw the template attached to the mouse over the rest of the buttons
        if(!createIcon && mouseTemplate.getTemplateIndex() != -1)
        {

            mouseTemplate.x = mouseX-25;
            mouseTemplate.y = mouseY-25;
            mouseTemplate.drawButton(mc,0,0,partialTicks);
        }

        slideTab.drawButton(Minecraft.getMinecraft(),mouseX,mouseY,partialTicks,232,0);

        //draw textfield for searching
        for(GuiTextFieldFillIn field:customTextFields)
        {
            field.drawTextBox(mouseX,mouseY,partialTicks);
        }

        if(refreshInventoryButton.isMouseOver())
            CustomGuiUtils.drawHoveringText("Refresh inventory: Load new templates in 'advcreation_templates_zip' folder", mouseX+7, mouseY,width,height,-1,fontRenderer);

        if(moveToTrashButton.isMouseOver())
        {
            if(mouseTemplate.templateIndex == -1)
                CustomGuiUtils.drawHoveringText("Drop a template here to discard it", mouseX+7, mouseY,width,height,-1,fontRenderer);
            else
            {
                String templateName =  TemplateManager.TEMPLATES_LIST.get(mouseTemplate.templateIndex).getName();
                CustomGuiUtils.drawHoveringText("Discard template '" + templateName + "'", mouseX+7, mouseY,width,height,-1,fontRenderer);
            }
        }

        //draw tooltip when hovering over block
        for (GuiButton button: buttonList)
        {
            if(button.isMouseOver())
            {
                if(button instanceof GuiTemplateButton)
                {
                    drawTemplateInfoTooltip(mouseX, mouseY, ((GuiTemplateButton) button));
                }
            }
        }

        for (GuiFreeToggleButton versionButton : versionTabs)
        {

            if(versionButton.isMouseOver())
                CustomGuiUtils.drawHoveringText("Show templates from MC" + versionButton.displayString, mouseX+7, mouseY,width,height,-1,fontRenderer);
        }

        GlStateManager.color(1f,1f,1f,1f);
        GlStateManager.colorLogicOp(5379);
    }

    @Override
    public void handleMouseInput() throws IOException {

        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if((scroll < 0.0001F && Keybindings.ZOOM_OUT.getKeybind().getKeyCode() == -102) ||
                        (scroll > 0.0001F && Keybindings.ZOOM_IN.getKeybind().getKeyCode() == -101))
        {
            mouseScroll(scroll);

        }

        if(slideTab.active )
            if(Mouse.isButtonDown(0))
                moveSlideTab();
            else
                slideTab.active = false;
    }

    private void mouseScroll(int scroll)
    {
        //try to scroll the textfields first
        boolean textfieldScrolled = false;
        for(GuiTextFieldFillIn field:customTextFields)
        {
            boolean scrolled = field.scrollOptions(scroll);
            if (!textfieldScrolled)
                textfieldScrolled = scrolled;
        }
        if(!textfieldScrolled)
        {
            scrollTemplateInventory(scroll);
        }
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        this.mouseHandled =false;
        boolean clicked = false;


        if(Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101 && (-100+mouseButton) == Keybindings.ZOOM_IN.getKeybind().getKeyCode())
        {

            mouseScroll(keyScrollSpeed);
            return ;
        }
        if(Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102 && (-100+mouseButton) == Keybindings.ZOOM_OUT.getKeybind().getKeyCode())
        {
            mouseScroll(-keyScrollSpeed);
            return ;
        }
        else
        {
            //check if one of the searchfield text fields is clicked
            for(int i = customTextFields.size()-1; i >= 0; i--)
            {
                GuiTextFieldFillIn field = customTextFields.get(i);
                if(!clicked)
                {
                    clicked = field.mouseClicked(mouseX,mouseY,mouseButton);

                    if(!mouseHandled)
                        mouseHandled = clicked;
                    //if the field is already focussed and you click on it the first time it will still highlight the text
                    if(!firstClick && mouseHandled)
                    {
                        field.setFirstClick(false);
                        firstClick = true;
                    }
                }
                else
                {
                    field.setFocused(false);
                }

            }
        }

        //if mouse was handled by the textfields update the templates on display
        if(mouseHandled)
            updateDisplayIndices();
        if(!this.mouseHandled)
            super.mouseClicked(mouseX, mouseY, mouseButton);

        //no button is clicked empty the mouseheld template
        if(!this.mouseHandled)
        {
            if(GuiTemplaceInventoryScreenFunctionality.selected_index == mouseTemplate.templateIndex)
                GuiTemplaceInventoryScreenFunctionality.setSelected_index(-1);

            mouseTemplate.setTemplate(null,-1);
            mouseTemplate.visible =false;
        }
    
        if(slideTab.mousePressed(mc,mouseX,mouseY))
        {
            distSlideTabMouse = mouseY - slideTab.y;
        }
    }
    
    public void updateButton() { }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if(button.id >= 0 && button.id <= 7)
        {
            //one of the 8 template icons in the inventory is clicked
            GuiTemplateButton templateButton = (GuiTemplateButton) button;
            mouseTemplate.setTemplate(templateButton.getTemplate(),templateButton.templateIndex);
            mouseTemplate.visible =true;
        }
        else if(button.id >= 8 && button.id <= 11)
        {
            if(mouseTemplate.getTemplateIndex() != -1)
            {
                if(GuiTemplaceInventoryScreenFunctionality.getSelected_index()== ((GuiTemplateButton) button).templateIndex)
                    GuiTemplaceInventoryScreenFunctionality.setSelected_index(mouseTemplate.templateIndex);


                //if the mouse has a template selected place the template in the player hotbar
                int i = button.id - 8;
                GuiOverlayManager.setTemplateOfTemplateButton(i,mouseTemplate.getTemplate(),mouseTemplate.templateIndex);

                ((GuiTemplateButton) button).setTemplate(mouseTemplate.getTemplate(),mouseTemplate.templateIndex);
                mouseTemplate.setTemplate(null,-1);
                mouseTemplate.visible =false;
            }
            else
            {
                //if the mouse has nothing selected take the template of the player slot
                int i = button.id - 8;
                GuiOverlayManager.setTemplateOfTemplateButton(i,null,-1);

                mouseTemplate.setTemplate(((GuiTemplateButton) button).getTemplate(),((GuiTemplateButton) button).getTemplateIndex());
                ((GuiTemplateButton) button).setTemplate(null,-1);
                mouseTemplate.visible = true;
            }
            
        }
        else if(button.id == 12)
        {
            TemplateManager.MAP_FILENAME_TO_FOLDERNAME.clear();
            TemplateManager.FILENAME_LIST.clear();
            TemplateManager.TEMPLATES_LIST.clear();
            TemplateManager.MAP_FILENAME_TO_PROPERTIES_PATH.clear();

            //extract all the new zip files in the advcreation_templates_zips
            TemplateManager.extractTemplateZips();
            //can this be a good place to load files from the advcreation_templates folder?
            TemplateManager.loadTemplates();

            this.initGui();
        }
        else if(button==moveToTrashButton)
        {
            if(mouseTemplate.template != null)
            {
                if(GuiTemplaceInventoryScreenFunctionality.selected_index == mouseTemplate.templateIndex)
                    GuiTemplaceInventoryScreenFunctionality.setSelected_index(-1);

                mouseTemplate.template.moveToTrash();

                for (int i = 0; i < 4; i++)
                {
                    if(GuiOverlayManager.getTemplateIndexOfTemplateButton(i) ==  mouseTemplate.templateIndex)
                        GuiOverlayManager.setTemplateOfTemplateButton(i,null,-1);
                    if(listOfHotbarTemplateButtons.get(i).templateIndex == mouseTemplate.templateIndex)
                        listOfHotbarTemplateButtons.get(i).setTemplate(null,-1);
                }

                TemplateManager.TEMPLATES_LIST.remove(mouseTemplate.templateIndex);
                TemplateManager.FILENAME_LIST.remove(mouseTemplate.templateIndex);
                mouseTemplate.setTemplate(null,-1);
                updateDisplayIndices();
            }
        }
        else if(versionTabs.contains(button) )
        {
            toggleVersionButton((GuiFreeToggleButton) button);
            this.updateDisplayIndices();
        }

        this.mouseHandled = true;
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        DisplayMode display = null;

        //first check if the player is trying to type into one of the search textfields if not then proceed to hotkey functionality
        boolean typed = false;
        for(GuiTextFieldFillIn field:customTextFields)
        {
            if(!typed)
                typed = field.textboxKeyTyped(typedChar, keyCode);
        }
        if(!typed)
        {
            if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode())
                Minecraft.getMinecraft().displayGuiScreen(null);
            else if(keyCode == 100)
                Minecraft.getMinecraft().displayGuiScreen(null);
            else if(keyCode == 203) //left arrow
            {
            }
            else if(keyCode == 205) //right arrow
            {
            }
            else if(keyCode == 208) //down arrow
            {
            }
            else if(keyCode == 200) //up arrow
            {
            }
            else if(keyCode == 201) //pqge up
            {
            }
            else if(keyCode == 209) //page down
            {
            }
            else if(keyCode == Keybindings.ROTATE_RIGHT.getKeybind().getKeyCode())
            {
                // rotation of the templates
                for (Template template : TemplateManager.TEMPLATES_LIST)
                {
                    template.rotateY();
                }
            }
            else if(keyCode == Keybindings.OPEN_REPORT_SCREEN.getKeybind().getKeyCode())
            {
                KeyInputHandler.logKeyPress(AdvCreation.getMode(),Keybindings.OPEN_REPORT_SCREEN,"open ReportScreen");
                Report.saveScreenshot();

                Minecraft.getMinecraft().displayGuiScreen(new ReportScreen(this));
            }
            else if(Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101 && keyCode == Keybindings.ZOOM_IN.getKeybind().getKeyCode())
            {
                mouseScroll(keyScrollSpeed);
            }
            else if(Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102 && keyCode == Keybindings.ZOOM_OUT.getKeybind().getKeyCode())
            {
                mouseScroll(-keyScrollSpeed);
            }
            super.keyTyped(typedChar, keyCode);
        }
        if(typed || keyCode == 14)
        {
            updateDisplayIndices();
        }

    }

    private void updateDisplayIndices() {
        currTemplateName = templateName.getText();
        if(templateName.getText().equals( templateName.getDefaultText()))
            currTemplateName = "";

        currFunctionName = function.getText();
        if(function.getText().equals( function.getDefaultText()))
            currFunctionName = "";

        currCategoryName = category.getText();
        if(category.getText().equals( category.getDefaultText()))
            currCategoryName = "";

        currStyleName = style.getText();
        if(style.getText().equals( style.getDefaultText()))
            currStyleName = "";

        ArrayList<Integer> newIndices = CustomGuiUtils.getListIndexContaining(currTemplateName, TemplateManager.FILENAME_LIST);
        newIndices = getIndexListWhereMcVersionContains(currMcVersionNames,newIndices);
        newIndices = getIndexListWhereFunctionContains(currFunctionName,newIndices);
        newIndices = getIndexListWhereCategoryContains(currCategoryName,newIndices);
        newIndices = getIndexListWhereStyleContains(currStyleName,newIndices);

        if(displayedTemplateIndices.size()!= newIndices.size())
        {
            displayedTemplateIndices = newIndices;
            calculateSlideTabPositions( (int)(Math.floor(displayedTemplateIndices.size()/4.0)));
            display_index = 0;
        }
    }

    public ArrayList<Integer> getIndexListWhereMcVersionContains(ArrayList<String> mcVersionNames ,ArrayList<Integer> indices)
    {
        boolean allFalse = true;
        //if no versiontab is active is selected just return everything
        for (GuiFreeToggleButton tab: versionTabs)
        {
            if(tab.isActive())
            {
                allFalse = false;
                break;
            }
        }

        if(allFalse)
            return indices;
        //check if all versions are in the mcVersionNames list, if they are then there is no need to filter
        boolean allMcVerions = true;
        for (String currVersion: TemplateManager.templateMcVersionsFound)
        {
            if(!mcVersionNames.contains(currVersion))
            {
                allMcVerions = false;
                break;
            }


        }
        if(allMcVerions)
            return indices;
        else
        {
            //filter the templates based on the current selected versions
            ArrayList<Integer> res = new ArrayList<>();
            for(int ind:indices)
            {
                String string = TemplateManager.TEMPLATES_LIST.get(ind).getMcVersion();
                for (String currVersion: mcVersionNames)
                {
                    if(string.equals(currVersion))
                        res.add(ind);
                }

            }return res;


        }




    }


    public ArrayList<Integer> getIndexListWhereCategoryContains(String category,ArrayList<Integer> indices)
    {
        ArrayList<Integer> res = new ArrayList<>();

        for(int ind:indices)
        {
            String string = TemplateManager.TEMPLATES_LIST.get(ind).getCategory();
            if(string.contains(category))
                res.add(ind);
        }
        return res;
    }

    public ArrayList<Integer> getIndexListWhereFunctionContains(String function,ArrayList<Integer> indices)
    {

        ArrayList<Integer> res = new ArrayList<>();

        for(int ind:indices)
        {
            String string = TemplateManager.TEMPLATES_LIST.get(ind).getFunction();
            if(string.contains(function))
                res.add(ind);
        }

        return res;
    }

    public ArrayList<Integer> getIndexListWhereStyleContains(String style,ArrayList<Integer> indices)
    {
        ArrayList<Integer> res = new ArrayList<>();
        for(int ind:indices)
        {
            String string = TemplateManager.TEMPLATES_LIST.get(ind).getStyle();
            if(string.contains(style))
                res.add(ind);
        }
        return res;
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    private void createAndInitialiseSlideTab()
    {
        slideTab = new GuiFreeButton(13,slideTabTexture,(guiScreenWidth / 2) + windowWidth/2 -xOffset +1 +7, startHeight+8,12,15,232,0,244,0);
        displayedTemplateIndices.clear();
        for(int i = 0; i< TemplateManager.TEMPLATES_LIST.size(); i++)
        {
            displayedTemplateIndices.add(i);
        }

        //calculate the all position the slideTab can be in and the corresponding display index
        calculateSlideTabPositions( (int)(Math.floor(displayedTemplateIndices.size()/4.0)));

        //set the slideTab at the position of the current display index
        int value = GuiTemplaceInventoryScreenFunctionality.displayed_index- (GuiTemplaceInventoryScreenFunctionality.displayed_index%4);
        setSlideTabAt(value);
    }

    public boolean isaValidTemplateIndex(ArrayList<Template> templatesList, int templateIndex)
    {
        return templatesList.size() > templateIndex && 0 <= templateIndex;
    }

    private void setSlideTabAt(int value) {
        if (slideTabPostions.containsValue(value)) {
            int key = 0;
            for (HashMap.Entry<Integer, Integer> entry : slideTabPostions.entrySet()) {
                if (entry.getValue() == value) {
                    key = entry.getKey();
                    break;
                }
            }

            slideTab.y = key;
        }
    }

    private void calculateSlideTabPositions(int listSize) {
        slideTabPostions.clear();
        int pages = listSize;
        if(pages == 0)
            pages = 1;

        int pixelPerPage = 115 /pages;
        for(int i = 0;i < pages;i++)
        {
            slideTabPostions.put(i*pixelPerPage + startHeight +1,i*4);
        }
    }

    private void drawTemplateInfoTooltip(int mouseX, int mouseY, GuiTemplateButton tempButton)
    {
        if (isaValidTemplateIndex(TemplateManager.TEMPLATES_LIST, tempButton.getTemplateIndex())) {
            //filename
            String filename = TemplateManager.FILENAME_LIST.get(tempButton.getTemplateIndex());
            ArrayList<String> text = new ArrayList<>();

            Template template =  tempButton.getTemplate();
            //category
            String cat = template.getCategory();
            //templates size
            BlockPos size = template.getSize();
            String sizeText = size.getX() + "x" + size.getY() + "x" + size.getX();
            //style
            String style = template.getStyle();
            //function
            String function = template.getFunction();

            int filenameLength = getStringCharWidth(filename);
            int longestLenght = filenameLength;

            String secondLine = padBetweenTextToLength( function,TextFormatting.GOLD,sizeText,TextFormatting.YELLOW , filenameLength);

            //align the third line with second or first line
            int secondLineLength = getStringCharWidth(padBetweenTextToLength( function,sizeText , filenameLength));
            String thirdLine = "";
            int thirdLineLength = getStringCharWidth(thirdLine);
            if(secondLineLength > filenameLength)
            {
                thirdLine = padBetweenTextToLength(style,TextFormatting.AQUA,cat,TextFormatting.BLUE,secondLineLength);
                thirdLineLength = getStringCharWidth(padBetweenTextToLength(style,cat,secondLineLength));
                longestLenght = secondLineLength;
            }
            else
            {
                thirdLine = padBetweenTextToLength(style,TextFormatting.AQUA,cat,TextFormatting.BLUE,filenameLength);
                thirdLineLength = getStringCharWidth(padBetweenTextToLength(style,cat,filenameLength));
            }


            //check if the thirdline is not longer than the second line outerwise the secondline is re-aligned
            if(thirdLineLength> secondLineLength)
            {
                secondLine = padBetweenTextToLength( function,TextFormatting.GOLD,sizeText,TextFormatting.YELLOW , thirdLineLength);
                longestLenght = thirdLineLength;
            }




            text.add(filename);
            text.add(secondLine);
            text.add(thirdLine);

            //add a warning message is the version of template is not compatible with the current minecraft version
            if(!TemplateManager.isAllowedMcVersion(template.getMcVersion()))
            {
                String versionIncompatibilityMessage = template.getMcVersion() + "(Some block types might be missing)";
                if(getStringCharWidth(versionIncompatibilityMessage) > longestLenght)
                {
                    String newLine = versionIncompatibilityMessage;
                    String nextLine = newLine;
                    while(getStringCharWidth(nextLine) > longestLenght)
                    {
                        newLine = fontRenderer.trimStringToWidth(nextLine,longestLenght);
                        String newNextLine = nextLine.replace(newLine,"");
                        //make the split at the last space
                        if(newLine.charAt(newLine.length()-1) == ' ')
                            newLine = newLine.trim();
                        else if(!newNextLine.isEmpty() && newNextLine.charAt(0) == ' ')
                            nextLine.trim();
                        else
                        {
                            int lastSpaceIndex = newLine.lastIndexOf(" ");
                            newLine = newLine.substring(0,lastSpaceIndex);
                            newNextLine = nextLine.replace(newLine,"").trim();
                        }

                        text.add(TextFormatting.RED +newLine);
                        nextLine = newNextLine;
                    }
                    if(!newLine.trim().isEmpty())
                        text.add(TextFormatting.RED + nextLine.trim());


                }
                else
                {
                    String fourthLine = TextFormatting.RED + versionIncompatibilityMessage;
                    text.add(fourthLine);
                }
            }
            CustomGuiUtils.drawHoveringText(text, mouseX -7, mouseY,width,height,-1,fontRenderer);
        }
    }

    public String padBetweenTextToLength(String text1, String text2, int length)
    {
        return padBetweenTextToLength(text1,null,text2,null,length);
    }

    public String padBetweenTextToLength(String text1,TextFormatting color1, String text2,TextFormatting color2, int length)
    {
        text1 = text1.trim();
        text2 = text2.trim();
        int catLength = getStringCharWidth(text1);
        int sizeLength = getStringCharWidth(text2);
        float space = length - (catLength + sizeLength);
        int nbrSpaces = (int)Math.ceil(space/fontRenderer.getCharWidth('_'));
        String secondLine = "";
        if(space > 6)
        {
            String spaces = "                                                                                    ";
            if(color1!= null && color2 != null)
                secondLine = (color1 + text1 + fontRenderer.trimStringToWidth(spaces,(int)space) + color2 + text2);
            else
                secondLine = (text1 + fontRenderer.trimStringToWidth(spaces,(int)space) + text2);


        }
        else
        {
            if(color1!= null && color2 != null)
                secondLine = (color1 + text1 + " " + color2 + text2);
            else
                secondLine = (text1 + " " + text2);
        }
        return secondLine;
    }

    public int getStringCharWidth(String text) {
        int filenameLength = 0;
        for (char c : text.toCharArray()) {
            filenameLength += fontRenderer.getCharWidth(c);
        }
        return filenameLength;
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

    private void moveSlideTab()
    {
        int[] mouse = CustomGuiUtils.scaleMouseCoord( Mouse.getX(),Mouse.getY());
        int mouseX = mouse[0];
        int mouseY = mouse[1]-distSlideTabMouse;
        int minMouse = 1000;
        int minKey = 0;
        for(int pos:slideTabPostions.keySet()) {
            int dist = Math.abs(pos - mouseY);
            if (minMouse > dist)
            {
                minMouse = dist;
                minKey = pos;
            }
        }
        slideTab.y = minKey;
        display_index = slideTabPostions.get(minKey);
        if(displayedTemplateIndices.size() == TemplateManager.TEMPLATES_LIST.size())
            GuiTemplaceInventoryScreenFunctionality.displayed_index = display_index;
    }

    private void scrollTemplateInventory(int scroll)
    {
        //add scroll to display index but make sure it doesn't go outside of the list size
        int t = scroll /30;

        display_index -= t;
        if(display_index < 0)
            display_index =0;
        else if(display_index + 4 > displayedTemplateIndices.size())
        {
            if(TemplateManager.TEMPLATES_LIST.size() < 4)
                display_index = 0;
            else
                display_index = displayedTemplateIndices.size()-displayedTemplateIndices.size()%8;
        }

        if(displayedTemplateIndices.size() == TemplateManager.TEMPLATES_LIST.size())
            GuiTemplaceInventoryScreenFunctionality.displayed_index = display_index;

        setSlideTabAt(display_index);
    }

    private void deleteIcons()
    {
        int b = 0;
        for (GuiButton button:buttonList)
        {
            if (b > 8)
                break;

            if (button instanceof GuiTemplateButton)
            {
                if(((GuiTemplateButton) button).getTemplate() != null)
                {
                    CustomGuiUtils.delectIcons(((GuiTemplateButton) button).getTemplate().getName());
                    ((GuiTemplateButton) button).getTemplate().deleteIcons();
                }
            }
            b++;
        }
    }

}

