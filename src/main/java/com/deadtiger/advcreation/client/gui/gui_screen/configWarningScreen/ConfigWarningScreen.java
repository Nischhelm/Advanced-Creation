package com.deadtiger.advcreation.client.gui.gui_screen.configWarningScreen;

import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter;
import com.google.common.base.Strings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.IOException;
import java.util.ArrayList;

public class ConfigWarningScreen extends GuiScreen
{
    GuiScreen parentScreen;
    ModContainer mod;

    GuiButton continueButton;
    GuiButton backButton;

    private int guiScreenWidth = 480;
    private int guiScreenHeight = 270;

    public ConfigWarningScreen(GuiScreen parentScreen, ModContainer mod)
    {
        super();
        this.parentScreen = parentScreen;
        this.mod = mod;

    }

    @Override
    public void initGui()
    {
        continueButton = new GuiButton(1,guiScreenWidth/2,guiScreenHeight/2,100,20,"Continue");
        this.buttonList.add(continueButton);
        backButton = new GuiButton(2,guiScreenWidth/2-100,guiScreenHeight/2,100,20,"Back");
        this.buttonList.add(backButton);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        String[][] warningMessage = {{"If you are seeing this screen, you might have an old config file and so the mod options might not open."},
                {"If this is the case, delete the 'config/advcreation.cfg' file in the .minecraft folder and then restart minecraft."},{"This will result in your settings being reset to default."}};


        int textWidth = (this.guiScreenWidth-150);
        int maxLengthOfFirstPart = GuiScreenTextPrinter.getMaxLengthOfFirstPartOfBody(warningMessage);
        int totalStringLimit = GuiScreenTextPrinter.calcStringLimit(textWidth, GuiScreenTextPrinter.charSize);
        ArrayList<ArrayList<String>> formatedText = GuiScreenTextPrinter.formatBody(warningMessage, maxLengthOfFirstPart, totalStringLimit);

        GuiScreenTextPrinter.drawBody(50,guiScreenHeight/2-75,formatedText,maxLengthOfFirstPart,12,1.0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if(button.id == 1)
        {
            try
            {
                IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(this.mod);
                GuiScreen newScreen = guiFactory.createConfigGui(this);
                this.mc.displayGuiScreen(newScreen);
            }
            catch (Exception e)
            {
                System.out.println(e);
                mc.displayGuiScreen(this.parentScreen);
            }

        }
        else if(button.id == 2)
        {
            mc.displayGuiScreen(this.parentScreen);
        }
        super.actionPerformed(button);
    }

    public static ModContainer getModContainer(GuiModList modList)
    {
        ArrayList<ModContainer> mods = new ArrayList<ModContainer>();
        FMLClientHandler.instance().addSpecialModEntries(mods);
        // Add child mods to their parent's list
        for (ModContainer mod : Loader.instance().getModList())
        {
            if (mod.getMetadata() != null && mod.getMetadata().parentMod == null && !Strings.isNullOrEmpty(mod.getMetadata().parent))
            {
                String parentMod = mod.getMetadata().parent;
                ModContainer parentContainer = Loader.instance().getIndexedModList().get(parentMod);
                if (parentContainer != null)
                {
                    mod.getMetadata().parentMod = parentContainer;
                    parentContainer.getMetadata().childMods.add(mod);
                    continue;
                }
            }
            else if (mod.getMetadata() != null && mod.getMetadata().parentMod != null)
            {
                continue;
            }
            mods.add(mod);
        }

        for(int i = 0;i<mods.size() ; i++)
        {
            if(modList.modIndexSelected(i))
            {
                return mods.get(i);
            }
        }
        return null;
    }
}
