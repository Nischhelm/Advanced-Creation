package com.deadtiger.advcreation.client.gui.gui_utility;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

@SideOnly(Side.CLIENT)
public class GifTexture extends VideoTexture
{
    private static final Logger LOG = Logger.getGlobal();
    protected final ResourceLocation textureLocation;
    GifDecoder decoder;
    IResource  iresource = null;
    BufferedImage lastImage = null;
    boolean flag;
    boolean flag1;

    public GifTexture(ResourceLocation textureResourceLocation,IResourceManager resourceManager)
    {
        this.textureLocation = textureResourceLocation;
        decoder = new GifDecoder();

        try {
            this.iresource = resourceManager.getResource(this.textureLocation);
            decoder.read(iresource.getInputStream());

            if (iresource.hasMetadata())
            {
                try
                {
                    TextureMetadataSection texturemetadatasection = (TextureMetadataSection)iresource.getMetadata("texture");

                    if (texturemetadatasection != null)
                    {
                        flag = texturemetadatasection.getTextureBlur();
                        flag1 = texturemetadatasection.getTextureClamp();
                    }
                }
                catch (RuntimeException runtimeexception)
                {
                    LOG.warning( "Failed reading metadata of: {}" + this.textureLocation + runtimeexception);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(iresource);

        }


    }


    public Dimension getFrameSize()
    {
        return decoder.getFrameSize();
    }

    public int getFrameCount()
    {
        return decoder.getFrameCount();
    }



    public void loadTexture(int index) throws IOException
    {
        this.deleteGlTexture();
        try
        {

            BufferedImage image = decoder.getFrame(index);
            if(image!= null)
            {
                System.out.println("renew image");
                lastImage = image;
            }
            else
                System.out.println("use last image");


            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), lastImage, flag, flag1);
        }
        finally
        {
            IOUtils.closeQuietly((Closeable)iresource);
        }
    }

    public static ArrayList<BufferedImage> readBufferedImage(InputStream imageStream) throws IOException
    {
        ArrayList<BufferedImage> bufferedimage =  new ArrayList<BufferedImage>();

        try
        {
            GifDecoder decoder = new GifDecoder();
            decoder.read(imageStream);
            for(int i = 0;i<decoder.getFrameCount();i++ )
            {
                bufferedimage.add(decoder.getFrame(i) );
                i += 1;
            }

        }
        finally
        {
            IOUtils.closeQuietly(imageStream);
            return bufferedimage;
        }


    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {

    }

    public boolean hasValidResource()
    {
        if(decoder.getFrame(0) != null)
            return true;
        return false;
    }

    @Override
    public void loadProperties(ResourceLocation propertiesResource, IResourceManager resourceManager)
    {

    }

    @Override
    public void unloadResources()
    {

    }
}
