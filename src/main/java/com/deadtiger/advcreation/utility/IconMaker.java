package com.deadtiger.advcreation.utility;

import com.deadtiger.advcreation.mixin.accessor.ScreenshotHelperAccessor;
import com.deadtiger.advcreation.template.Template;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import org.lwjgl.BufferUtils;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IconMaker
{
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    /**
     * Saves a screenshot in the game directory with a time-stamped filename.
     * Returns an ITextComponent indicating the success/failure of the saving.
     */
    public static BufferedImage saveScreenshot(Template template, boolean selected, File gameDirectory, int width, int height, Framebuffer buffer, BufferedImage imageBuffer, boolean save)
    {
        return saveScreenshot(template, selected, gameDirectory, (String) null, 0, 0, width, height, 0, 0, buffer, imageBuffer, save);
    }

    /**
     * Saves a screenshot in the game directory with a time-stamped filename.
     * Returns an ITextComponent indicating the success/failure of the saving.
     */
    public static BufferedImage saveScreenshot(Template template, boolean selected, File gameDirectory, int x, int y, int width, int height, int x_icon, int y_icon, Framebuffer buffer, BufferedImage imageBuffer, boolean save)
    {
        return saveScreenshot(template, selected, gameDirectory, (String) null, x, y, width, height, x_icon, y_icon, buffer, imageBuffer, save);
    }

    /**
     * Saves a screenshot in the game directory with the given file name (or null to generate a time-stamped name).
     * Returns an ITextComponent indicating the success/failure of the saving.
     */
    public static BufferedImage saveScreenshot(Template template, boolean selected, File gameDirectory, @Nullable String screenshotName, int x, int y, int width, int height, int x_icon, int y_icon, Framebuffer buffer, BufferedImage imageBuffer, boolean save)
    {
        try
        {
            BufferedImage bufferedimage = createScreenshot(x, y, width, height, x_icon, y_icon, buffer, imageBuffer);

            if (save)
            {

                File mcDataDir = Minecraft.getMinecraft().gameDir;
                File file1 = new File(mcDataDir, "advcreation_templates");

                if (!file1.exists())
                {
                    if (!file1.mkdirs())
                    {
                        return null;
                    }
                }
                else if (!file1.isDirectory())
                {
                    return null;
                }

                File templateDir = new File(file1, template.getDirname());
                if (template.getNewDirName() != null)
                    templateDir = new File(file1, template.getNewDirName());

                if (templateDir.exists())
                {
                    EnumFacing orient = template.getRotation();
                    String fileName = orient.toString() + "_selected_" + selected + ".png";

                    File file2 = new File(templateDir, fileName);
                    file2 = file2.getCanonicalFile(); // FORGE: Fix errors on Windows with paths that include \.\
                    net.minecraftforge.client.event.ScreenshotEvent event = net.minecraftforge.client.ForgeHooksClient.onScreenshot(bufferedimage, file2);
                    if (event.isCanceled()) return null;
                    else file2 = event.getScreenshotFile();
                    ImageIO.write(bufferedimage, "png", file2);
                    ITextComponent itextcomponent = new TextComponentString(file2.getName());
                    itextcomponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
                    itextcomponent.getStyle().setUnderlined(Boolean.valueOf(true));
                    if (event.getResultMessage() != null) return null;
                    return null;
                }
                else
                    return null;
            }
            else
                return bufferedimage;

        }
        catch (Exception exception)
        {
            return null;
        }
    }


    public static BufferedImage createScreenshot(int x, int y, int width, int height, int x_icon, int y_icon, Framebuffer framebufferIn, BufferedImage imageBuffer)
    {
        int frameWidth = width;
        int frameHeight = height;

        int i = frameWidth * frameHeight;

        if (ScreenshotHelperAccessor.getPixelBuffer() == null || ScreenshotHelperAccessor.getPixelBuffer().capacity() < i)
        {
            ScreenshotHelperAccessor.setPixelBuffer(BufferUtils.createIntBuffer(i));
            ScreenshotHelperAccessor.setPixelValues(new int[i]);
        }

        GlStateManager.glPixelStorei(3333, 1);
        GlStateManager.glPixelStorei(3317, 1);
        ScreenshotHelperAccessor.getPixelBuffer().clear();

        GlStateManager.glReadPixels(x, y, frameWidth, frameHeight, 32993, 33639, ScreenshotHelperAccessor.getPixelBuffer());

        ScreenshotHelperAccessor.getPixelBuffer().get(ScreenshotHelperAccessor.getPixelValues());
        TextureUtil.processPixelValues(ScreenshotHelperAccessor.getPixelValues(), frameWidth, frameHeight);

        if (imageBuffer == null)
        {
            if (width > 256)
            {
                imageBuffer = new BufferedImage(width, height, 1);
            }
            else
            {
                imageBuffer = new BufferedImage(156, 156, 1);
            }
        }

        try
        {
            imageBuffer.setRGB(x_icon, y_icon, width, height, ScreenshotHelperAccessor.getPixelValues(), 0, width);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        return imageBuffer;
    }

    /**
     * Creates a unique PNG file in the given directory named by a timestamp.  Handles cases where the timestamp alone
     * is not enough to create a uniquely named file, though it still might suffer from an unlikely race condition where
     * the filename was unique when this method was called, but another process or thread created a file at the same
     * path immediately after this method returned.
     */
    private static File getTimestampedPNGFileForDirectory(File gameDirectory)
    {
        String s = DATE_FORMAT.format(new Date()).toString();
        int i = 1;

        while (true)
        {
            File file1 = new File(gameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");

            if (!file1.exists())
                return file1;

            ++i;
        }
    }
}
