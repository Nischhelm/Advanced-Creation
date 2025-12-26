package com.deadtiger.advcreation.client.gui.gui_utility;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public abstract class VideoTexture extends AbstractTexture
{
    public abstract Dimension getFrameSize();
    public abstract int getFrameCount();
    public abstract void loadTexture(int index) throws IOException;
    public abstract boolean hasValidResource();
    public abstract void loadProperties(ResourceLocation propertiesResource, IResourceManager resourceManager);

    public abstract void unloadResources();
}
