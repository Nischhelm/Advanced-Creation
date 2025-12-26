package com.deadtiger.advcreation.client.gui.gui_screen.helpScreen.elements;

import com.deadtiger.advcreation.client.gui.gui_utility.GifTexture;
import com.deadtiger.advcreation.client.gui.gui_utility.MP4Texture;
import com.deadtiger.advcreation.client.gui.gui_utility.VideoTexture;
import com.deadtiger.advcreation.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class GuiAnimatedImage extends Gui
{

    protected static final ResourceLocation WINDOW_TEXTURES = new ResourceLocation(Reference.MODID, "textures/gui/test.gif");
    protected ResourceLocation texture;

    protected String name;

    /**
     * Button width in pixels
     */
    public int width;
    /**
     * Button height in pixels
     */
    public int height;
    /**
     * The x position of this control.
     */
    public int x;
    /**
     * The y position of this control.
     */
    public int y;
    public int originalY;
    private int offsetY;

    public int currFrame = 0;
    public int totalframeCount = 0;
    public long lastTime = 0;

//    private GifTexture itextureobject;
    private VideoTexture itextureobject;

    private boolean freezeEnd = false;


    /**
     * Hides the button completely if false.
     */
    public boolean visible;

    public GuiAnimatedImage(int x, int y, int width, int height, Minecraft mc, String name)
    {
        this.x = x;
        this.y = y;
        this.originalY = y;
        this.width = width;
        this.height = height;
        visible = true;
        this.name = name;
        itextureobject = new GifTexture(WINDOW_TEXTURES, mc.getResourceManager());
        totalframeCount = itextureobject.getFrameCount();
    }

    public GuiAnimatedImage(int x, int y, int width, int height, String gifResource, Minecraft mc, String name)
    {
        this.x = x;
        this.y = y;
        this.originalY = y;
        this.width = width;
        this.height = height;
        visible = true;
        this.texture = new ResourceLocation(Reference.MODID, gifResource);
        this.name = name;
    }

    public GuiAnimatedImage(int x, int y, Minecraft mc)
    {
        this(x, y, 100, 100, mc, "none");
        Dimension dim = itextureobject.getFrameSize();
        this.width = (int) dim.getWidth();
        this.height = (int) dim.getHeight();
    }

    public GuiAnimatedImage(int x, int y, int width, String gifResource, Minecraft mc,String name)
    {
        this(x, y, width, 100, gifResource, mc, name);
    }

    public void initGif(Minecraft mc)
    {
        if(texture.getNamespace().contains(".gif"))
            itextureobject = new GifTexture(texture, mc.getResourceManager());
        if(texture.getNamespace().contains(".mp4"))
            itextureobject = new MP4Texture(texture,new ResourceLocation(Reference.MODID, "textures/gui/mp4s/" + this.name +".txt"), mc.getResourceManager(),this.name);


        totalframeCount = itextureobject.getFrameCount();
        Dimension dim = itextureobject.getFrameSize();
        double scale = width / dim.getWidth();
        this.height = (int) (dim.getHeight() * scale);
        resetGif();
    }

    public void removeGif()
    {
        if(itextureobject != null)
        {
            itextureobject.unloadResources();
            itextureobject = null;
        }


    }

    public void drawWindow(Minecraft mc)
    {
        if (this.visible && itextureobject.hasValidResource())
        {
            try
            {
                itextureobject.loadTexture(currFrame);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GlStateManager.bindTexture(itextureobject.getGlTextureId());

            double progressBarUnit = (this.width / (double) totalframeCount);

            drawModalRectWithCustomSizedTexture(x - 1, y - 1 + offsetY, 0, 0, this.width, this.height, this.width, this.height);
            drawHorizontalLine(x - 1, x + ((int) (progressBarUnit * currFrame)), y - 1 + this.height + offsetY, Color.ORANGE.getRGB());
            GlStateManager.color(1.0f, 1.0f, 1.0f);
        }
    }

    public void tick(long time)
    {
        //10 frames per second
        if (time - lastTime > 100)
        {
            nextFrame();

            lastTime = time;
        }
    }

    public void nextFrame()
    {
        if (totalframeCount != 0 && currFrame >= totalframeCount)
        {
            if (!freezeEnd)
                resetGif();
        }
        else
            currFrame += 1;
    }

    public int getOffsetY()
    {
        return offsetY;
    }

    public void setOffsetY(int offsetY)
    {
        this.offsetY = offsetY;
    }

    public void setFreezeEnd(boolean freeze)
    {
        this.freezeEnd = freeze;
    }

    public void resetGif()
    {
        if (itextureobject instanceof MP4Texture)

        currFrame = 0;
    }
}
