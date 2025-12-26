package com.deadtiger.advcreation.client.gui.gui_utility;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;

public class MP4Texture extends VideoTexture
{
    protected final ResourceLocation textureLocation;
    String name;
    MP4Decoder decoder;
    IResource iresource = null;
    BufferedImage lastImage = null;
    boolean flag = false;
    boolean flag1 = true;

    boolean buffered = false;
    boolean tempfile_exists = false;

    public MP4Texture(ResourceLocation textureLocation,ResourceLocation propertiesFile,IResourceManager resourceManager,String name)
    {
        this.textureLocation = textureLocation;
        decoder = new MP4Decoder(textureLocation,resourceManager,name);
        tempfile_exists = decoder.createTempFile(textureLocation, decoder.resourceManager, name);
        this.name = name;
        loadProperties( propertiesFile, resourceManager);
        decoder.createFrameGrabber();
        if(decoder.frameCount == 0 && tempfile_exists)
            decoder.preloadVid(textureLocation,resourceManager,name);

    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException
    {



    }

    @Override
    public Dimension getFrameSize()
    {
        return decoder.getFrameSize();
    }

    @Override
    public int getFrameCount()
    {
        return decoder.getFrameCount();
    }

    @Override
    public void loadTexture(int index) throws IOException
    {
        this.deleteGlTexture();
        try
        {
            BufferedImage image = decoder.getFrame(index);
            if(image != null)
            {
                lastImage = image;
            }
            if(lastImage!= null)
                TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), lastImage, flag, flag1);
        }
        finally
        {
            IOUtils.closeQuietly((Closeable)iresource);
        }
    }

    @Override
    public boolean hasValidResource()
    {
        return tempfile_exists;
    }

    @Override
    public void loadProperties(ResourceLocation propertiesLocation, IResourceManager resourceManager)
    {
        decoder.loadProperties(propertiesLocation,resourceManager);
    }

    @Override
    public void unloadResources()
    {
        decoder.deleteTempFile();
    }
}
