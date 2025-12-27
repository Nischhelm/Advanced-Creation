package com.deadtiger.advcreation.client.gui.gui_utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

public class MP4Decoder
{
    protected FrameGrab grab = null;

    protected int width = 0; // full image width
    protected int height = 0; // full image height
    protected ColorSpace color;

    protected int ix, iy, iw, ih; // current image rectangle
    protected Rectangle lastRect; // last image rect
    protected BufferedImage image; // current frame
    protected BufferedImage lastImage; // previous frame

    protected ArrayList frames = new ArrayList(); // frames read from current file
    protected int frameCount = 0;

    public boolean buffered;

    protected ResourceLocation textureLocation;
    protected IResourceManager resourceManager;
    protected String name;

    protected int last_index = -1;

    Path temp = null;
    FileInputStream input;

    public MP4Decoder(ResourceLocation textureLocation, IResourceManager resourceManager, String name)
    {
        this.textureLocation = textureLocation;
        this.resourceManager = resourceManager;
        this.name = name;
        String tempFileName =  "advcreationmod-" + name;
        try
        {
            temp = Files.createTempFile(tempFileName, ".ext");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean createTempFile(ResourceLocation textureLocation, IResourceManager resourceManager, String name)
    {
        try
        {
            if(input != null)
                input.close();

            IResource iresource =  resourceManager.getResource(textureLocation);
            Files.copy(iresource.getInputStream(), temp, StandardCopyOption.REPLACE_EXISTING);

        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public void preloadVid(ResourceLocation resource, IResourceManager resourceManager , String name)
    {

        Picture picture;
        boolean properties_read = false;
        while (true)
        {
            try
            {
                if (!(null != (picture = grab.getNativeFrame()))) break;
                if(!properties_read)
                {

                    this.width = picture.getWidth();
                    this.height = picture.getHeight();
                    this.color = picture.getColor();
                }

                BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
                lastImage = bufferedImage;
                frames.add(new GifDecoder.GifFrame(bufferedImage,0));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        frameCount = frames.size();

        File mcDataDir = Minecraft.getMinecraft().gameDir;
        File saves = new File(mcDataDir, "mp4Files");
        ensureDirectoryExists(saves);
        File file = new File(saves,name + ".txt");
        createFile(file);

        buffered = true;

    }

    public void createFrameGrabber()
    {
        try
        {
            if(input != null)
                input.close();
            input = new FileInputStream(temp.toString());
            this.grab = FrameGrab.createFrameGrab(new FileChannelWrapper(input.getChannel()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public boolean ensureDirectoryExists(File saves)
    {
        if (!saves.exists())
        {
            if (!saves.mkdirs())
            {
                return false;
            }
        }
        else if (!saves.isDirectory())
        {
            return false;
        }
        return true;
    }

    private boolean createFile(File file2)
    {

        OutputStream outputstream = null;
        boolean flag2;

        try
        {
            outputstream = new FileOutputStream(file2);
            try (PrintWriter p = new PrintWriter(outputstream))
            {
                p.println("height: " + height);
                p.println("width: " + width);
                p.println("frameCount: " + frameCount);
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            return false;
        }
        catch (Throwable var13)
        {
            flag2 = false;
        }
        finally
        {
            try
            {
                outputstream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return flag2;
    }

    public BufferedImage getFrame(int n)
    {
        BufferedImage im = null;
        if(n > frameCount || last_index == n)
            return null;
        else
            last_index = n;

        if(buffered)
        {
            if ((n >= 0) && (n < frameCount)) {
                im = ((GifDecoder.GifFrame) frames.get(n)).image;
            }
        }
        else
        {
            Picture picture;
            try
            {
                if (null != (picture = grab.getNativeFrame()))
//                if (null != (picture = grab.seekToFramePrecise(n).getNativeFrame()))
                {

                    im = AWTUtil.toBufferedImage(picture);
                }
                else
                {
                    grab = grab.seekToFramePrecise(0);
                }
//                else
//                {
//                    if(input != null)
//                        input.close();
//                    System.out.println("This GIF is done " + frameCount + " frames, resetting now");
//
//                    try
//                    {
//                        input = new FileInputStream(temp.toString());
//                        this.grab = FrameGrab.createFrameGrab(new FileChannelWrapper(input.getChannel()));
//                    }
//                    catch (FileNotFoundException e)
//                    {
//                        e.printStackTrace();
//                    }
//                    catch (JCodecException e)
//                    {
//                        e.printStackTrace();
//                    }
//                    catch (IOException e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                }
            }
            catch (Exception e)
            {
//                e.printStackTrace();
            }

        }
        return im;
    }

    public Dimension getFrameSize() {
        return new Dimension(width, height);
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void loadProperties(ResourceLocation propertiesLocation, IResourceManager resourceManager)
    {
        try
        {
            IResource iresource =  resourceManager.getResource(propertiesLocation);
            Scanner reader = new Scanner(iresource.getInputStream(),"utf-8");
            while (reader.hasNextLine())
            {
                String data = reader.nextLine();
                if (data.contains("height:"))
                {
                    this.height = Integer.parseInt(data.split(":")[1].trim());

                }
                if (data.contains("width:"))
                    this.width = Integer.parseInt(data.split(":")[1].trim());
                if (data.contains("frameCount:"))
                    this.frameCount = Integer.parseInt(data.split(":")[1].trim());
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void deleteTempFile()
    {
        if(temp != null )
        {

            try
            {
                if(input != null)
                    input.close();
                Files.deleteIfExists(temp);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
