package com.deadtiger.advcreation.client.gui.gui_screen.warningScreen;

import com.deadtiger.advcreation.client.gui.gui_screen.custom_gui_elements.GuiCustomWindow;
import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

public class GuiGeneralWarningScreen extends GuiScreen
{
    public GuiGeneralWarningScreen()
    {
        super();
    }

    ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/gui/buildmode_gui_overlay.png");
    // size of the main gui window
    private int guiWidth = 249;
    private int guiHeight = 130;

    //size of the menu screen? is always a low resolution? because ... why not?
    private int guiScreenWidth = 480;
    private int guiScreenHeight = 270;

    //gui screen texts
    private String[][] warningMessage;
    private ArrayList<ArrayList<String>> formattedMessage;
    private String title;

    private GuiCustomWindow window;

    private FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    int maxLengthOfFirstPart=0;
    int totalStringLimit = 0;
    double textScale = 1.0;

    //button on the GUI
    private GuiButton confirmButton;
    private GuiButton cancelButton;

    private Function confirmAction;
    private Function cancelAction;

    public GuiGeneralWarningScreen( String[][] warningMessage, Function confirmAction, Function cancelAction)
    {
        super();
        this.title = "Warning";

        this.warningMessage = warningMessage;
        this.confirmAction = confirmAction;
        this.cancelAction = cancelAction;
    }

    @Override
    public void initGui()
    {
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();

        if(height < 245)
            textScale = 1.1;
        else
            textScale = 0.9;

        this.buttonList.clear();
        int windowWidth = guiWidth;

        int textWidth = ((guiWidth) - 10);
        maxLengthOfFirstPart = GuiScreenTextPrinter.getMaxLengthOfFirstPartOfBody(warningMessage);
        totalStringLimit = GuiScreenTextPrinter.calcStringLimit(textWidth, GuiScreenTextPrinter.charSize*textScale)-6;
        formattedMessage = GuiScreenTextPrinter.formatBody(warningMessage,maxLengthOfFirstPart,totalStringLimit);


        int windowHeight = 25 + 30 + (int) (formattedMessage.size()*(11*textScale));
        guiHeight = windowHeight;
        int buttonHeight = 20;

        int centerX = (this.width / 2);
        int centerY = (this.height / 2);


        window = new GuiCustomWindow(centerX - windowWidth/2, centerY - windowHeight/2, windowWidth, windowHeight);

        //left arrow custom button
        confirmButton = new GuiButton( 0,centerX - 120, centerY + windowHeight/2 - 25,
                120, buttonHeight, "Yes");
        this.buttonList.add(confirmButton);

        cancelButton = new GuiButton( 1,centerX , centerY + windowHeight/2 - 25,
                120, buttonHeight, "No");
        this.buttonList.add(cancelButton);



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
        Color titleColor = new Color(255, 106,0);
        GuiScreenTextPrinter.drawText(title, (width / 2) - fontRenderer.getStringWidth (title) / 2, (height / 2) - guiHeight / 2 + 4,titleColor.getRGB() ,textScale*1.2,false,false);
        GuiScreenTextPrinter.drawBodyNoSchadow((width / 2) - guiWidth/2 + 5 ,(height / 2) - guiHeight / 2 + 25,formattedMessage,maxLengthOfFirstPart,(int)(11*textScale),textScale,0x111111 );

        //draws all the buttons on top
        super.drawScreen(mouseX, mouseY, partialTicks);


    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        int enter = Keyboard.KEY_RETURN;//GLFW.GLFW_KEY_ENTER;
        int escape = Keyboard.KEY_ESCAPE; //GLFW.GLFW_KEY_ESCAPE;
        if(keyCode == enter)
        {
            confirmAction.apply(null);
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
        else if(keyCode == escape)
        {
            cancelAction.apply(null);
            Minecraft.getMinecraft().displayGuiScreen(null);
        }


        super.keyTyped(typedChar, keyCode);
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if(button.id == this.cancelButton.id)
        {
            cancelAction.apply(null);
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
        else if( button.id == this.confirmButton.id)
        {
            confirmAction.apply(null);
            Minecraft.getMinecraft().displayGuiScreen(null);
        }

        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return true;
    }

}
