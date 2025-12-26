package com.deadtiger.advcreation.client.gui.gui_screen.reportScreen;

import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.*;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.GuiCustomWindow;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.report.Report;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

import static com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter.drawText;

public class ReportScreen extends GuiScreen {
    ResourceLocation texture = new ResourceLocation(Reference.MODID,"textures/gui/buildmode_gui_overlay.png");
    // size of the main gui window
    private int guiWidth = 400;
    private int guiHeight = 230;
    
    //size of the menu screen? is always a low resolution? because ... why not?
    private int guiScreenWidth = 480;
    private int guiScreenHeight = 270;
    
    //gui screen texts
    private String title = "Save Report";
    private String subTitle1 = "I might make these reports public under your minecraft";
    private String subTitle2 = "username. So don't send anything you would regret later.";

    
    private GuiCustomWindow window;
    
    //button on the GUI
    private GuiButton saveButton;
    private static final int SAVE_BUTTON_ID = 0;
    private GuiButton cancelButton;
    private static final int CANCEL_BUTTON_ID = 1;
    private GuiButton deleteButton;
    private static final int DELETE_BUTTON_ID = 7;

    private GuiTextFieldFillIn subject;
    private static final int TEXT_FIELD_ID = 2;
    private GuiTextFieldTextBox description;
    private static final int DESCR_FIELD_ID = 6;

    
    private GuiTextFieldDropDown reportType;
    private GuiBaseButton reportTypeHelp;
    private static final int TYPE_FIELD_ID = 3;
    private GuiTextFieldDropDown topic;
    private GuiBaseButton topicHelp;
    private static final int TOPIC_FIELD_ID = 4;
    private GuiTextFieldDropDown rating;
    private GuiBaseButton ratingHelp;
    private static final int RATING_FIELD_ID = 5;

    private static final int ESCAPE_KEY_ID = 1;

    
    private ArrayList<GuiTextFieldOption> dropDownOptions = new ArrayList<>();
    
    private ArrayList<GuiTextFieldFillIn> customTextFields = new ArrayList<>();
    private ArrayList<GuiBaseButton> customButtons = new ArrayList<>();
    
    private boolean firstClick = false;

    private boolean drawRatingMissingMessage = false;
    private boolean drawSubjectMissingMessage = false;
    private long drawMissingMessageTime = 0;

    private GuiScreen previousScreen = null;

    public ReportScreen() {
        super();
    }

    public ReportScreen(GuiScreen previousScreen) {
        super();
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int centerX = (guiScreenWidth / 2) - guiWidth / 2;
        int centerY = (guiScreenHeight / 2) - guiHeight / 2;
        
        int windowWidth = guiWidth;
        int windowHeight = guiHeight;
        int buttonHeight = 20;
        
        window = new GuiCustomWindow(centerX , centerY,  windowWidth, windowHeight);

        int secondScreenX = 250;

        //left arrow custom button
        saveButton = new GuiButton(SAVE_BUTTON_ID, secondScreenX + centerX + 10, centerY + windowHeight-45,
                67, buttonHeight, "Save");
        buttonList.add(saveButton);
        
        cancelButton = new GuiButton(CANCEL_BUTTON_ID, secondScreenX + centerX +75, centerY + windowHeight-45,
                70, buttonHeight, "Suspend");
        buttonList.add(cancelButton);

        deleteButton = new GuiButton(DELETE_BUTTON_ID, secondScreenX + centerX +10, centerY + windowHeight-25,
                135, buttonHeight, "Delete Report");
        buttonList.add(deleteButton);

        int descreasedWidth = 65;
        int new_height = centerY + 5 ;
        reportType = new GuiTextFieldDropDown(TYPE_FIELD_ID, fontRenderer, secondScreenX + centerX+ 11,
                new_height, 178- descreasedWidth, buttonHeight,Report.TYPES,dropDownOptions,"Type");
        reportTypeHelp =   new GuiFreeButton(0,texture,secondScreenX +(centerX + 11 + 178- descreasedWidth )+11,
                new_height+10,20,20,174,5,200,5);
        reportType.setFocused(true);

        new_height += (buttonHeight + 5);
        topic = new GuiTextFieldDropDown(TOPIC_FIELD_ID, fontRenderer, secondScreenX + centerX+ 11,
                new_height, 178  - descreasedWidth, buttonHeight,Report.TOPICS,dropDownOptions,"Topic");
        topicHelp =   new GuiFreeButton(0,texture,secondScreenX +(centerX + 11 + 178- descreasedWidth )+11,
                new_height+10,20,20,174,5,200,5);

        new_height += (buttonHeight + 5);
        subject = new GuiTextFieldFillIn(TEXT_FIELD_ID, fontRenderer, secondScreenX + centerX + 11,
                new_height, 198- descreasedWidth,buttonHeight ,"Subject");

        new_height += (buttonHeight + 5);
        description = new GuiTextFieldTextBox(TEXT_FIELD_ID, fontRenderer, secondScreenX + centerX + 11,
                new_height, 198- descreasedWidth,buttonHeight*4 ,"Description");

        new_height = centerY + windowHeight-32 - (buttonHeight + 15);
        rating = new GuiTextFieldDropDown(RATING_FIELD_ID, fontRenderer, secondScreenX + centerX+ 11,
                new_height, 178 - descreasedWidth, buttonHeight,Report.RATINGS,dropDownOptions,"Rating");
        rating.setMaxOptionDisplay(2);
        ratingHelp =   new GuiFreeButton(0,texture,secondScreenX +(centerX + 11 + 178- descreasedWidth )+11,new_height+10,20,20,174,5,200,5);


        customTextFields.add(rating);
        customTextFields.add(subject);
        customTextFields.add(description);
        customTextFields.add(topic);
        customTextFields.add(reportType);

        customButtons.add(topicHelp);
        customButtons.add(reportTypeHelp);
        customButtons.add(ratingHelp);

        //fill in the textfield with data from an existing report
        if(Report.INSTANCE != null)
        {
            reportType.setText(Report.INSTANCE.getType());
            topic.setText(Report.INSTANCE.getTopic());
            subject.setText(Report.INSTANCE.getSubject());
            description.setText(Report.INSTANCE.getDescription());
            rating.setText(rating.getOptions().get(Report.INSTANCE.getRating()));
        }
        else
            Report.INSTANCE = new Report();

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        //draw the main gui window mainTexture
        
        window.drawWindow(mc);
        //draw the title of the window
        fontRenderer.drawString(title, (guiScreenWidth / 2) - fontRenderer.getStringWidth(title) / 2, (guiScreenHeight / 2) - guiHeight / 2 + 4, 0x000000);

        drawText(subTitle1, (guiScreenWidth / 2) - guiWidth / 2 +10, (guiScreenHeight / 2) - guiHeight / 2 + 4 + 15, 0x000000, 0.8);
        drawText(subTitle2, (guiScreenWidth / 2) - guiWidth / 2 +10, (guiScreenHeight / 2) - guiHeight / 2 + 4 + 15 + 10, 0x000000, 0.8);

        //draws all the buttons on top
        super.drawScreen(mouseX, mouseY, partialTicks);

        for(GuiBaseButton button:customButtons)
        {
            button.drawButton(mc, mouseX, mouseY, partialTicks, 174, 5);
        }

        for(GuiTextFieldFillIn field:customTextFields)
        {
            field.drawTextBox(mouseX,mouseY,partialTicks);
        }


        if(Report.INSTANCE.screenshot != null)
        {
            int centerX = (guiScreenWidth / 2) - guiWidth / 2;
            int centerY = (guiScreenHeight / 2) - guiHeight / 2;

            int previewWidth = guiHeight;
            int previewHeight = guiHeight ;
            if(Report.SCREENSHOT_WIDTH > Report.SCREENSHOT_HEIGHT)
            {
                double factor = Report.SCREENSHOT_HEIGHT /(double) Report.SCREENSHOT_WIDTH;
                previewHeight = (int)(factor*previewWidth);
            }
            else
            {
                double factor = Report.SCREENSHOT_WIDTH /(double) Report.SCREENSHOT_HEIGHT;
                previewWidth = (int)(factor*previewHeight);
            }

            mc.renderEngine.bindTexture( Report.INSTANCE.screenshot);
            drawModalRectWithCustomSizedTexture(centerX + 10,centerY + guiHeight/2 - previewHeight/2, 0,0, previewWidth, previewHeight,previewWidth,previewHeight);
        }

        //draw message about missing data
        if(drawRatingMissingMessage)
        {
            drawHoveringText("Please choose a rating",rating.x,rating.y);
        }
        if(drawSubjectMissingMessage)
        {
            drawHoveringText("Please add a subject",subject.x,subject.y);
        }

        for(GuiBaseButton button:customButtons)
        {
            if(button.isMouseOver())
            {
                if(button == reportTypeHelp)
                    CustomGuiUtils.drawHoveringText(Report.TIP_REPORT_TYPE, mouseX+7, mouseY,width,height,-1,fontRenderer);
                if(button == topicHelp)
                    CustomGuiUtils.drawHoveringText(Report.TIP_REPORT_TOPIC, mouseX+7, mouseY,width,height,-1,fontRenderer);
                if(button == ratingHelp)
                    CustomGuiUtils.drawHoveringText(Report.TIP_REPORT_RATING, mouseX+7, mouseY,width,height,-1,fontRenderer);
            }

        }

    }
    
    
    public void updateButton() {
    
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case SAVE_BUTTON_ID:
                attemptSaveReport();
                break;
            case CANCEL_BUTTON_ID:
                Report.INSTANCE.deleteScreenshot();
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                break;
            case DELETE_BUTTON_ID:
                Report.INSTANCE.deleteScreenshot();
                Report.INSTANCE = null;
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                break;
            
        }
        
        super.actionPerformed(button);
    }



    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        boolean typed = false;
        for(GuiTextFieldFillIn field:customTextFields)
        {
            if(!typed)
            {
                typed = field.textboxKeyTyped(typedChar, keyCode);
                if(typed)
                {
                    if(field == reportType)
                        Report.INSTANCE.setType( field.getText());
                    else if(field == topic)
                        Report.INSTANCE.setTopic(field.getText());
                    else if(field == subject)
                        Report.INSTANCE.setSubject(field.getText());
                    else if(field == description)
                        Report.INSTANCE.setDescription(field.getText());
                    else if(field == rating)
                        Report.INSTANCE.setTextRating(field.getText());
                }
            }
        }
        if(!typed) {
            if (keyCode == ESCAPE_KEY_ID)
            {
                Report.INSTANCE.deleteScreenshot();
                if(previousScreen != null)
                    Minecraft.getMinecraft().displayGuiScreen(previousScreen);
                else
                    this.mc.displayGuiScreen((GuiScreen)null);
                if (this.mc.currentScreen == null)
                {
                    this.mc.setIngameFocus();
                }
            }
        }
    }


    
    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
      
        int scroll = Mouse.getEventDWheel();
        if(scroll !=0)
        {
            for(GuiTextFieldFillIn field:customTextFields)
            {
                field.scrollOptions(scroll);
            }
            
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {

        boolean clicked = false;
        for(int i = customTextFields.size()-1; i >= 0; i--)
        {
            GuiTextFieldFillIn field = customTextFields.get(i);
            if(!clicked)
            {
                clicked = field.mouseClicked(mouseX,mouseY,mouseButton);
                //if the field is already focussed and you click on it the first time it will still highlight the text
                if(!firstClick && clicked)
                {
                    field.setFirstClick(false);
                    firstClick = true;
                }
                if(clicked)
                {
                    processClickedField(field);
                }
            }
            else
            {
                field.setFocused(false);
            }
            
        }
        if(!clicked)
            super.mouseClicked(mouseX,mouseY,mouseButton);
    }

    private void processClickedField(GuiTextFieldFillIn field)
    {
        if(field == rating)
            drawRatingMissingMessage = false;
        else if(field == subject)
            drawSubjectMissingMessage = false;

        if(field == reportType)
            Report.INSTANCE.setType( field.getText());
        else if(field == topic)
            Report.INSTANCE.setTopic(field.getText());
        else if(field == subject)
            Report.INSTANCE.setSubject(field.getText());
        else if(field == description)
            Report.INSTANCE.setDescription(field.getText());
        else if(field == rating)
            Report.INSTANCE.setTextRating(field.getText());
    }

    private void attemptSaveReport()
    {
        boolean save = true;

        if(reportType.getText().equals(reportType.getDefaultText()) || reportType.getText().isEmpty())
            Report.INSTANCE.setType("other");
        else
            Report.INSTANCE.setType(reportType.getText());

        if(rating.getText().equals(rating.getDefaultText()) || rating.getText().isEmpty())
        {
            save = false;
            drawRatingMissingMessage = true;
        }
        else
        {
            save = Report.INSTANCE.setTextRating(rating.getText());
        }

        if(topic.getText().equals(topic.getDefaultText()) || topic.getText().isEmpty())
            Report.INSTANCE.setTopic("other");
        else
            Report.INSTANCE.setTopic(topic.getText());

        if (subject.getText().equals(subject.getDefaultText()) || subject.getText().isEmpty())
        {
            save = false;
            drawSubjectMissingMessage = true;
        }
        else
            Report.INSTANCE.setSubject(subject.getText());

        if(!description.getText().isEmpty())
        {
            Report.INSTANCE.setDescription(description.getText());
        }
        else
            Report.INSTANCE.setDescription("empty");

        if(save)
        {
            Report.INSTANCE.saveReport();
            Report.INSTANCE = null;
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
        else
        {
            drawMissingMessageTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }


}
