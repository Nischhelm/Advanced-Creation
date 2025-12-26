package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.elements;

import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiHelpScreenVisual;
import com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.GuiScreenTextPrinter;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.awt.*;
import java.util.ArrayList;

public class GuiTextButton extends GuiButton
{
    private ArrayList<GuiTextButton> sideMenuChildButtons = new ArrayList<>();
    private boolean visibleChildren = false;
    private GuiTextButton nextButton;
    public GuiTextButton parentButton = null;

    private int original_y;
    private int original_x;
    private int offsetY;

    double widthscale = 1.0;

    private boolean active = false;
    private boolean nonFunctional = false;
    private boolean bordered = false;

    private Color lineColor;


    protected static final ResourceLocation KEY_TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/buttons.png");
    protected static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/buildmode_gui_overlay.png");


    public GuiTextButton(int buttonId, int x, int y, String buttonText)
    {
        this(buttonId, x, y, buttonText, true);
    }

    public GuiTextButton(int buttonId, int x, int y, String buttonText, boolean visible)
    {
        this(buttonId,x,y,buttonText,visible,false,Color.WHITE,x);
    }

    public GuiTextButton(int buttonId, int x, int y, String buttonText, boolean visible, boolean bordered)
    {
        this(buttonId,x,y,buttonText,visible,bordered,Color.WHITE,x);
    }

    public GuiTextButton(int buttonId, int x, int y, String buttonText, boolean visible, boolean bordered,Color lineColor,int original_x)
    {
        super(buttonId, x, y, buttonText);
        this.original_y = y;
        this.original_x = original_x;
        this.visible = visible;
        this.lineColor = lineColor;

        Minecraft mc = Minecraft.getMinecraft();
        widthscale = 0.8 - 0.3 * ((1920 - mc.displayWidth) / 1600.0);
        this.bordered = bordered;

    }


    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int newY = this.y + offsetY;

            this.hovered = mouseX >= this.x && mouseY >= newY && mouseX < this.x + this.width && mouseY < newY + this.height;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }

            if (this.nonFunctional)
            {
                j = Color.gray.getRGB();
            }
            else if (this.enabled)
            {
                if (this.hovered)
                {
                    j = 16777120;
                }
                if (this.active)
                {
                    j = Color.RED.getRGB();
                }
            }

            if (!sideMenuChildButtons.isEmpty())
            {
                mc.renderEngine.bindTexture(KEY_TEXTURE);
                int width = 21;
                int height = 14;

                int newWidth = 14;
                double scale = newWidth / (double) width;
                int newHeight = (int) (height * scale);

                if (this.isVisibleChildren())
                    drawModalRectWithCustomSizedTexture(this.x + this.width - newWidth-5, newY + 5, (int) (143 * scale), (int) (20 * scale), newWidth, newHeight, (int) (256 * scale), (int) (256 * scale));
                else
                    drawModalRectWithCustomSizedTexture(this.x + this.width - newWidth-5, newY + 5, (int) (175 * scale), (int) (0 * scale), newWidth, newHeight, (int) (256 * scale), (int) (256 * scale));

            }
            if(bordered)
            {
//                GuiScreenTextPrinter.drawText(this.displayString, this.x + this.width / 6, newY + (this.height - 8) / 2, j, widthscale * ((100.0+((GuiHelpScreenVisual.textSizeValue-100.0)/2.0))/100.0));

                GuiScreenTextPrinter.drawText(this.displayString, this.x + this.width / 6, newY + (this.height - 8) / 2, j, widthscale * ((100.0+((ConfigurationHandler.general.HELP_TEXT_SIZE-100.0)/2.0))/100.0));


                int rgb = lineColor.getRGB();
                float[] comp = lineColor.getColorComponents(null);
//                int widthPreface = this.x + this.width / 6 -5 - this.original_x;
//                GlStateManager._color4f(comp[0],comp[1],comp[2],1f);
//                Minecraft.getInstance().getTextureManager().bind(BLANK_TEXTURE);
                GuiUtils.drawGradientRect(0, this.x,newY,this.x + this.width / 6 -5,newY + height,rgb,rgb);
                int newX = this.x;
                int rowIndex = 0;
                for (int i = 0; i < GuiHelpScreenVisual.rowColors.length; i++)
                {
                    if(GuiHelpScreenVisual.rowColors[i].equals(this.lineColor))
                    {
                        rowIndex = i;
                        break;
                    }
                }
                while(newX > this.original_x && rowIndex > 0)
                {
                    rowIndex--;
                    rgb = GuiHelpScreenVisual.rowColors[rowIndex].getRGB();
                    GuiUtils.drawGradientRect(0, newX-5,newY,newX,newY + height,rgb,rgb);
                    newX = newX - 5;
                }

//                CustomGuiUtils.drawModalRectWithCustomSizedTexture(this.original_x,newY ,0,150,widthPreface,this.height,widthPreface,this.height,0.5f);
//                GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
                //                CustomGuiUtils.drawHorizontalLine(stack.last().pose(),this.original_x+1, this.x + this.width ,newY, rgb);
//                CustomGuiUtils.drawVerticalLine(stack.last().pose(),this.original_x + this.width-1, newY, newY+this.getTotalHeight(), rgb);
//                CustomGuiUtils.drawHorizontalLine(stack.last().pose(),this.original_x+1,this.x + this.width-1  ,newY+this.getTotalHeight(), rgb);
//                CustomGuiUtils.drawVerticalLine(stack.last().pose(),this.original_x+1,  newY,newY+this.getTotalHeight(), rgb);
            }
            else
                GuiScreenTextPrinter.drawText(this.displayString, this.x + this.width / 4, newY + (this.height - 8) / 2, j, widthscale * ((100.0+((ConfigurationHandler.general.HELP_TEXT_SIZE-100.0)/2.0))/100.0));

            GlStateManager.disableBlend();
        }

    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        int newY = this.y + offsetY;
        return this.enabled && this.visible && mouseX >= this.x && mouseY >= newY && mouseX < this.x + this.width && mouseY < newY + this.height;
    }

    public int getHeight()
    {
        return this.height;
    }

    public void addChildButton(GuiTextButton button)
    {
        sideMenuChildButtons.add(button);
    }

    public void addNextButton(GuiTextButton button)
    {
        nextButton = button;
    }

    public boolean isVisibleChildren()
    {
        return visibleChildren;
    }

    public int setVisibleChildren(boolean visibleChildren)
    {

        this.visibleChildren = visibleChildren;
        for (GuiTextButton button : sideMenuChildButtons)
        {
            button.visible = visibleChildren;
            if (!visibleChildren)
                button.setVisibleChildren(false);
        }
        int new_height = this.y + this.getTotalHeight();
        if (nextButton != null)
            new_height = nextButton.adjustPosition(this.y + this.getTotalHeight());

        return new_height;
    }

    public int adjustPosition(int newPosY)
    {
        this.y = newPosY;
        if (!sideMenuChildButtons.isEmpty())
        {
            sideMenuChildButtons.get(0).adjustPosition(this.y + this.getHeight());
        }
        int new_height = this.y + this.getTotalHeight();
        if (nextButton != null)
            new_height = nextButton.adjustPosition(this.y + this.getTotalHeight());
        return new_height;
    }

    public int toggleVisibleChildren()
    {
        return setVisibleChildren(!isVisibleChildren());

    }

    public int getTotalHeight()
    {
        int totalHeight = this.height;

        if (isVisibleChildren())
        {
            for (GuiTextButton button : sideMenuChildButtons)
            {
                totalHeight += button.getTotalHeight();
            }

        }
        return totalHeight;
    }

    public int getOriginal_y()
    {
        return original_y;
    }

    public void setOriginal_y(int original_y)
    {
        this.original_y = original_y;
    }

    public int getOffsetY()
    {
        return offsetY;
    }

    public void setOffsetY(int offsetY)
    {
        this.offsetY = offsetY;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public boolean isNonFunctional()
    {
        return nonFunctional;
    }

    public void setNonFunctional(boolean nonFunctional)
    {
        this.nonFunctional = nonFunctional;
    }

    public void setParentButton(GuiTextButton parent)
    {
        this.parentButton = parent;
    }

    public GuiTextButton getNextButton()
    {
        return nextButton;
    }

    public void setNextButton(GuiTextButton nextButton)
    {
        this.nextButton = nextButton;
    }

    public ArrayList<GuiTextButton> getSideMenuChildButtons()
    {
        return sideMenuChildButtons;
    }

    public void setSideMenuChildButtons(ArrayList<GuiTextButton> sideMenuChildButtons)
    {
        this.sideMenuChildButtons = sideMenuChildButtons;
    }

    public double getWidthscale()
    {
        return widthscale;
    }

    public void setWidthscale(double widthscale)
    {
        this.widthscale = widthscale;
    }
}
