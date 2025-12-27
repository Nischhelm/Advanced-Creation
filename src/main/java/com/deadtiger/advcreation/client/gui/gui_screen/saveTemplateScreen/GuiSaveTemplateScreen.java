package com.deadtiger.advcreation.client.gui.gui_screen.saveTemplateScreen;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.*;
import com.deadtiger.advcreation.client.gui.gui_screen.reportScreen.ReportScreen;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.client.input.KeyInputHandler;
import com.deadtiger.advcreation.client.input.Keybindings;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.report.Report;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class GuiSaveTemplateScreen extends GuiScreen
{
    ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/gui/buildmode_gui_overlay.png");
    // size of the main gui window
    private int guiWidth = 219;
    private int guiHeight = 130;

    //size of the menu screen? is always a low resolution? because ... why not?
    private int guiScreenWidth = 480;
    private int guiScreenHeight = 270;

    //gui screen texts
    private String title = "Save template as";

    private GuiCustomWindow window;

    //button on the GUI
    private GuiButton saveButton;
    private static final int SAVE_BUTTON_ID = 0;
    private GuiButton cancelButton;
    private static final int CANCEL_BUTTON_ID = 1;
    private GuiTextFieldFillIn templateName;
    private static final int TEXT_FIELD_ID = 2;

    private GuiTextFieldDropDown category;
    private GuiBaseButton catHelp;
    private static final int CAT_FIELD_ID = 3;
    private GuiTextFieldDropDown style;
    private GuiBaseButton styleHelp;
    private static final int STYLE_FIELD_ID = 4;
    private GuiTextFieldDropDown function;
    private GuiBaseButton functionHelp;
    private static final int FUNC_FIELD_ID = 5;

    private ArrayList<GuiTextFieldOption> dropDownOptions = new ArrayList<>();

    private ArrayList<GuiTextFieldFillIn> customTextFields = new ArrayList<>();
    private ArrayList<GuiBaseButton> customButtons = new ArrayList<>();

    private boolean firstClick = false;

    @Override
    public void initGui()
    {

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();


        buttonList.clear();
        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (height / 2) - guiHeight / 2;

        int windowWidth = guiWidth;
        int windowHeight = guiHeight;
        int buttonHeight = 20;

        window = new GuiCustomWindow(centerX, centerY, windowWidth, windowHeight);

        //left arrow custom button
        saveButton = new GuiButton(SAVE_BUTTON_ID, centerX + 10, centerY + windowHeight - 25,
                100, buttonHeight, "Save");
        buttonList.add(saveButton);

        cancelButton = new GuiButton(CANCEL_BUTTON_ID, centerX + 110, centerY + windowHeight - 25,
                100, buttonHeight, "Cancel");
        buttonList.add(cancelButton);

        templateName = new GuiTextFieldFillIn(TEXT_FIELD_ID, fontRenderer, centerX + 11, centerY + 5,
                198, buttonHeight, "Template Name");
        templateName.setFocused(true);

        function = new GuiTextFieldDropDown(FUNC_FIELD_ID, fontRenderer, centerX + 11, centerY + 5 + 1 * (buttonHeight + 5),
                178, buttonHeight, BuildTemplateMode.FUNCTIONS, dropDownOptions, "Function");
        functionHelp = new GuiFreeButton(0, texture, (centerX + 11 + 178) + 11, centerY + 5 + 1 * (buttonHeight + 5) + 10,
                20, 20, 174, 5, 200, 5);

        category = new GuiTextFieldDropDown(CAT_FIELD_ID, fontRenderer, centerX + 11, centerY + 5 + 2 * (buttonHeight + 5),
                178, buttonHeight, BuildTemplateMode.CATEGORIES, dropDownOptions, "Category");
        catHelp = new GuiFreeButton(0, texture, (centerX + 11 + 178) + 11, centerY + 5 + 2 * (buttonHeight + 5) + 10,
                20, 20, 174, 5, 200, 5);


        style = new GuiTextFieldDropDown(STYLE_FIELD_ID, fontRenderer, centerX + 11, centerY + 5 + 3 * (buttonHeight + 5),
                178, buttonHeight, BuildTemplateMode.STYLES, dropDownOptions, "Style");
        styleHelp = new GuiFreeButton(0, texture, (centerX + 11 + 178) + 11, centerY + 5 + 3 * (buttonHeight + 5) + 10,
                20, 20, 174, 5, 200, 5);

        customTextFields.add(style);
        customTextFields.add(category);
        customTextFields.add(function);
        customTextFields.add(templateName);

        customButtons.add(styleHelp);
        customButtons.add(catHelp);
        customButtons.add(functionHelp);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        //draw the main gui window mainTexture

        window.drawWindow(mc);
        //draw the title of the window
        fontRenderer.drawString(title, (width / 2) - fontRenderer.getStringWidth(title) / 2, (height / 2) - guiHeight / 2 + 4, 0x000000);

        //draws all the buttons on top
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiBaseButton button : customButtons)
        {
            button.drawButton(mc, mouseX, mouseY, partialTicks);
        }

        for (GuiTextFieldFillIn field : customTextFields)
        {
            field.drawTextBox(mouseX, mouseY, partialTicks);
        }

        for (GuiBaseButton button : customButtons)
        {
            if (button.isMouseOver())
            {
                if (button == catHelp)
                    CustomGuiUtils.drawHoveringText(BuildTemplateMode.CATEGORY_TIP, mouseX + 7, mouseY, width, height, -1, fontRenderer);
                if (button == styleHelp)
                    CustomGuiUtils.drawHoveringText(BuildTemplateMode.STYLE_TIP, mouseX + 7, mouseY, width, height, -1, fontRenderer);
                if (button == functionHelp)
                    CustomGuiUtils.drawHoveringText(BuildTemplateMode.FUNCTION_TIP, mouseX + 7, mouseY, width, height, -1, fontRenderer);
            }

        }

    }


    public void updateButton()
    {

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case SAVE_BUTTON_ID:
                saveAction();
                break;
            case CANCEL_BUTTON_ID:
                System.out.println("cancel button clicked");
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;

        }

        super.actionPerformed(button);
    }

    private void saveAction()
    {
        System.out.println("save button clicked");
        if (category.getText().equals(category.getDefaultText()) || category.getText().isEmpty())
            BuildTemplateMode.setCATEGORY("None");
        else
            BuildTemplateMode.setCATEGORY(category.getText());

        if (function.getText().equals(function.getDefaultText()) || function.getText().isEmpty())
            BuildTemplateMode.setFUNCTION("None");
        else
            BuildTemplateMode.setFUNCTION(function.getText());

        if (style.getText().equals(style.getDefaultText()) || style.getText().isEmpty())
            BuildTemplateMode.setSTYLE("None");
        else
            BuildTemplateMode.setSTYLE(style.getText());

        if (!templateName.getText().isEmpty() && !templateName.getText().equals(templateName.getDefaultText()))
        {
            BuildTemplateMode.setTemplateName(templateName.getText().replace(" ","_"));
            BuildTemplateMode.finishSaveTemplate();
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        boolean typed = false;
        if (keyCode == Keybindings.CONFIRM_TEMPLATE_CREATION.getKeybind().getKeyCode())
        {
            KeyInputHandler.logKeyPress(AdvCreation.getMode(), Keybindings.CONFIRM_TEMPLATE_CREATION, "Save template");
            saveAction();
            typed = true;
        }

        for (GuiTextFieldFillIn field : customTextFields)
        {
            if (!typed)
                typed = field.textboxKeyTyped(typedChar, keyCode);
        }
        if (!typed)
        {
            super.keyTyped(typedChar, keyCode);
            if (keyCode == Keybindings.OPEN_REPORT_SCREEN.getKeybind().getKeyCode())
            {
                KeyInputHandler.logKeyPress(AdvCreation.getMode(), Keybindings.OPEN_REPORT_SCREEN, "open ReportScreen");
                Report.saveScreenshot();
                Minecraft.getMinecraft().displayGuiScreen(new ReportScreen(this));
            }
            else if(Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101 && keyCode == Keybindings.ZOOM_IN.getKeybind().getKeyCode())
            {
                for (GuiTextFieldFillIn field : customTextFields)
                {
                    field.scrollOptions(120);
                }
            }
            else if(Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102 && keyCode == Keybindings.ZOOM_OUT.getKeybind().getKeyCode())
            {
                for (GuiTextFieldFillIn field : customTextFields)
                {
                    field.scrollOptions(-120);
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        int scroll = Mouse.getEventDWheel();
        if (scroll != 0)
        {
            if(scroll < 0.0001F && Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102)
            {
                return;
            }
            else if(scroll > 0.0001F && Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101)
            {
                return;
            }

            for (GuiTextFieldFillIn field : customTextFields)
            {
                field.scrollOptions(scroll);
            }

        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {

        boolean clicked = false;

        if(Keybindings.ZOOM_IN.getKeybind().getKeyCode() != -101 && (-100+mouseButton) == Keybindings.ZOOM_IN.getKeybind().getKeyCode())
        {
            for (GuiTextFieldFillIn field : customTextFields)
            {
                clicked = field.scrollOptions(120);
                if(clicked)
                    break;
            }
        }
        else if(Keybindings.ZOOM_OUT.getKeybind().getKeyCode() != -102 && (-100+mouseButton) == Keybindings.ZOOM_OUT.getKeybind().getKeyCode())
        {
            for (GuiTextFieldFillIn field : customTextFields)
            {
                clicked = field.scrollOptions(-120);
                if(clicked)
                    break;
            }
        }
        else
        {
            for (int i = customTextFields.size() - 1; i >= 0; i--)
            {
                GuiTextFieldFillIn field = customTextFields.get(i);
                if (!clicked)
                {
                    clicked = field.mouseClicked(mouseX, mouseY, mouseButton);
                    //if the field is already focussed and you click on it the first time it will still highlight the text
                    if (!firstClick && clicked)
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


        if (!clicked)
            super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }


}
