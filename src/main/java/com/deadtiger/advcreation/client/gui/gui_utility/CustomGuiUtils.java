package com.deadtiger.advcreation.client.gui.gui_utility;

import com.deadtiger.advcreation.client.gui.gui_overlay.gui_overlay_element.GuiOverlayBaseElement;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateBlock;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.*;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;

public class CustomGuiUtils
{
    
    public enum EnumMouseButtonClick {LEFT_CLICK, RIGHT_CLICK, MIDDLE_CLICK}
    
    private static float X_icon_Z = 8.0f * ((42.0f / 16.0f) / 3.0f);
    private static float Y_icon_Z = -8.0f * ((21.0f / 16.0f) / 3.0f);
    private static float Z_icon_Z = -8.0f * ((37.0f / 16.0f) / 3.0f);
    private static float X_icon_X = -8.0f * ((42.0f / 16.0f) / 3.0f);
    private static float Y_icon_X = -8.0f * ((21.0f / 16.0f) / 3.0f);
    private static float Z_icon_X = -8.0f * ((37.0f / 16.0f) / 3.0f);
    private static float X_icon_Y = (0.0f / 3.0f);
    private static float Y_icon_Y = -8.0f * ((52.0f / 16.0f) / 3.0f);
    private static float Z_icon_Y = 8.0f * ((32.0f / 16.0f) / 3.0f);
    
    
    public static float angle = 0.F;
    public static float x= 4.6F;
    public static float y= -2.3F;
    public static float z= 4.0F;
    
    public static boolean scaleOverride = false;
    public static float scaleOverrideNbr = 0;
    
    /***
     * Scale the window coordinates of a mouse click to the minecraft gui resolution
     * @param x
     * @param y
     * @return
     */
    public static int[] scaleMouseCoord(int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        //copied some code for the minecraft source that calculates the scaling of the mouse coordinates to the gui scale
        // the code comes from class minecraft.client.renderer.EntityRenderer on line 1092
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        int i1 = scaledresolution.getScaledWidth();
        int j1 = scaledresolution.getScaledHeight();
        int resizedX = x * i1 / mc.displayWidth;
        int resizedY = j1 - y * j1 / mc.displayHeight - 1;
        
        return new int[]{resizedX, resizedY};
    }
    
    public static boolean isWithin(int x, int y, int X_button, int Y_button, int width, int height) {
        if (x > X_button && x < X_button + width && y > Y_button && y < Y_button + height) {
            return true;
        }
        return false;
    }
    
    public static void drawBlockIcon(Minecraft mc, ItemStack icon_cobble, float scale, float totTranslationX, float totTranslationY, float totTranslationZ,EnumFacing dir,boolean up, boolean onSide) {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(totTranslationX, totTranslationY, totTranslationZ);
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.enableColorMaterial();

            renderItemAndEffectIntoGUI(icon_cobble, 0, 0,dir,up,onSide);
            
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
    }

    public static void drawHighlight( GuiOverlayBaseElement element, Color color)
    {
        int xStartHighlight = element.getX();
        int yStartHighlight = element.getY();
        int xEndHighlight = element.getX() + element.getWidth();
        int yEndHighlight = element.getY() + element.getHeight();


        drawHighlight(xStartHighlight, yStartHighlight, xEndHighlight, yEndHighlight, color);
    }

    public static void drawHighlight(int xStartHighlight, int yStartHighlight, int xEndHighlight, int yEndHighlight, Color color)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0,0,200);
        //draw the edges of the fps counter possibly highlighted
        net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect(0, xStartHighlight, yStartHighlight, xEndHighlight, yStartHighlight +1, color.getRGB(), color.getRGB());
        net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect(0,
                xEndHighlight -1, yStartHighlight, xEndHighlight, yEndHighlight, color.getRGB(), color.getRGB());
        net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect(0,
                xStartHighlight, yEndHighlight -1, xEndHighlight, yEndHighlight, color.getRGB(), color.getRGB());
        net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect(0,
                xStartHighlight, yStartHighlight, xStartHighlight +1, yEndHighlight, color.getRGB(), color.getRGB());
        GlStateManager.translate(0,0,-200);
        GlStateManager.popMatrix();
    }
    
    public static void drawHoveringText(ArrayList<String> text, int x, int y, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(text, x, y, screenWidth, screenHeight, maxTextWidth, font);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }
    
    public static void drawHoveringText(String text, int x, int y, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(Arrays.asList(text), x, y, screenWidth, screenHeight, maxTextWidth, font);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }
    public static void DrawTemplateIcon(Template template, float x_pos_middle, float y_pos_middle, float offset, Minecraft mc)
    {
        DrawTemplateIcon(template,x_pos_middle,y_pos_middle,offset,mc,1.0F);
    }
    public static void DrawTemplateIcon(Template template, float x_pos_middle, float y_pos_middle, float offset, Minecraft mc, float scaleMultiple) {
        template.tryCalculateProperties();
        int[] maxes = {template.extrema.maxX - template.extrema.minX, template.extrema.maxY - template.extrema.minY, template.extrema.maxZ - template.extrema.minY};
        int maxIndex = 0;
        for (int j = 0; j < maxes.length; j++) {
            if (maxes[maxIndex] < maxes[j])
                maxIndex = j;
        }
        float scale = 1.0F;
        float X_icon_right_X = (X_icon_X * scale) * maxes[0];
        float X_icon_right_Z = (X_icon_Z * scale) * maxes[2];
        float Y_icon_right_Y = (Y_icon_X * scale) * maxes[0] + (Y_icon_Y * scale) * maxes[1] + (Y_icon_Z * scale) * maxes[2];
        
        float currScale = 1.0f;
        
        if (maxes[maxIndex] == 0.0)
            currScale = 1.0f;
        else if (maxIndex == 0 || maxIndex == 2) {
            float icon = (-X_icon_right_X + X_icon_right_Z);
            currScale = calcCurrScale(icon);
//            System.out.println("currScale calc " + currScale + " icon "+ icon +  " scaleMultiple " + scaleMultiple + " scaleOverrideNbr "+ scaleOverrideNbr);
        } else if (maxIndex == 1) {
            currScale = calcCurrScale(Y_icon_right_Y);
//            System.out.println("currScale calc " + currScale + " icon "+ Y_icon_right_Y +  " scaleMultiple " + scaleMultiple+ " scaleOverrideNbr " + scaleOverrideNbr);
        }
       
        if(scaleOverride)
            currScale = scaleOverrideNbr;
        else
            scaleOverrideNbr = currScale;
        
        float addZ = 400f - 300f*currScale;
        
        
        currScale = currScale*scaleMultiple;
        
        int Xoffset = 0;
        int Yoffset = 0;
        float[] pos = {0, 0};
        if (template.getRotation() == EnumFacing.WEST) {
            Xoffset = (int) Math.floor(((X_icon_right_X * currScale) + X_icon_right_Z * currScale) / 2);
            Yoffset = (int) Math.floor((Y_icon_right_Y * currScale) / 2);
            
            pos[0] = x_pos_middle - Xoffset;// + (1-currScale)*10;
            pos[1] = y_pos_middle - Yoffset;
        } else if (template.getRotation() == EnumFacing.NORTH) {
            Xoffset = (int) Math.floor(((X_icon_right_X * currScale) - X_icon_right_Z * currScale) / 2);
            Y_icon_right_Y = -(Y_icon_X * scale) * maxes[0] + (Y_icon_Y * scale) * maxes[1] + (Y_icon_Z * scale) * maxes[2];
            Yoffset = (int) Math.floor((Y_icon_right_Y * currScale) / 2);
            
            pos[0] = x_pos_middle - Xoffset;//- Xoffset + (1-currScale)*10;
            pos[1] = y_pos_middle - Yoffset;
        } else if (template.getRotation() == EnumFacing.EAST) {
            Xoffset = (int) Math.floor(((-X_icon_right_X - X_icon_right_Z) * currScale) / 2);
            Y_icon_right_Y = -(Y_icon_X * scale) * maxes[0] + (Y_icon_Y * scale) * maxes[1] - (Y_icon_Z * scale) * maxes[2];
            Yoffset = (int) Math.floor((Y_icon_right_Y * currScale) / 2);
            
            pos[0] = x_pos_middle - Xoffset;// + (1-currScale)*10;
            pos[1] = y_pos_middle - Yoffset;
        } else if (template.getRotation() == EnumFacing.SOUTH) {
            Xoffset = (int) Math.floor((-(X_icon_right_X * currScale) + X_icon_right_Z * currScale) / 2);
            Y_icon_right_Y = (Y_icon_X * scale) * maxes[0] + (Y_icon_Y * scale) * maxes[1] - (Y_icon_Z * scale) * maxes[2];
            Yoffset = (int) Math.floor((Y_icon_right_Y * currScale) / 2);
            
            pos[0] = x_pos_middle - Xoffset;//- Xoffset + (1-currScale)*10;
            pos[1] = y_pos_middle - Yoffset;
        }
        
        float new_x_pos_middle = pos[0];
        float new_y_pos_middle = pos[1];
        
        for (int j = 0; j < template.getBlockListSize(); j++)
        {
            TemplateBlock tempBlock = template.getTempBlock(j);
            if(!tempBlock.isEnclosed())
            {
                ItemStack stack = tempBlock.getItem();



                drawBlockIcon(mc, tempBlock.getBlockState(),tempBlock.getTileEntity(),stack, new_x_pos_middle + offset, new_y_pos_middle,
                    tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset(), currScale,addZ);
            }
            
        }
    }
    
    public static float calcCurrScale(float icon) {
        float currScale;
        
        float base = 0.112f;
        float exp = -0.000632f;
        float treshold= 425f;
        
        if(icon < treshold)
        {
            currScale = (float) (1.36 - 0.211f * Math.log(Math.abs(icon)));
        }
        else
        {
            currScale = base * (float) Math.exp(icon*exp);
        }
        
        return currScale;
    }
    
    private static void drawBlockIcon(Minecraft mc, IBlockState state, TileEntity tileEntity,ItemStack itemStack, float x_pos_middle, float y_pos_middle, float x_offset_right, float y_offset_right, float z_offset_right, float scale, float addZ) {
        


        EnumFacing dir = EnumFacing.EAST;
        boolean up = false;
        boolean onSide = false;
        for(IProperty property: state.getPropertyKeys())
        {
            if(property instanceof PropertyDirection)
            {
                try
                {
                    PropertyDirection propertyDir = (PropertyDirection) property;
                    if(!(state.getBlock() instanceof BlockLog))
                        dir = state.getValue(propertyDir);
                    if(state.getBlock() instanceof BlockDirectional && dir.getAxis().isHorizontal())
                    {
                        onSide = true;
                        dir = dir.rotateY();
                    }

                }
                catch(Exception e)
                {
                    System.out.println("exception catch " + e);
                }
            }
            else if(state.getValue(property) instanceof BlockLog.EnumAxis)
            {
                if(state.getValue(property) ==  BlockLog.EnumAxis.X)
                {
                    dir = EnumFacing.NORTH;
                    onSide = true;
                }
                else if(state.getValue(property) == BlockLog.EnumAxis.Z)
                {
                    dir =  EnumFacing.WEST;
                    onSide = true;
                }
                
                //special case when using a log it doesn't not have the PropertyDirection property it has in stead an axis property
            }
            else if(property.getName().equals("half"))
            {
                if(!state.getValue(property).toString().equals("bottom"))
                    up = true;
            }
        }

        
        
        
        float X_icon_right = (X_icon_X * scale) * x_offset_right + (X_icon_Y * scale) * y_offset_right + (X_icon_Z * scale) * z_offset_right;
        float Y_icon_right = (Y_icon_X * scale) * x_offset_right + (Y_icon_Y * scale) * y_offset_right + (Y_icon_Z * scale) * z_offset_right;
        float Z_icon_right = (Z_icon_X * scale) * x_offset_right + (Z_icon_Y * scale) * y_offset_right + (Z_icon_Z * scale) * z_offset_right;
        
        float totTranslationX = x_pos_middle + X_icon_right;
        float totTranslationY = y_pos_middle + Y_icon_right;
        float totTranslationZ = addZ + Z_icon_right;
        
        CustomGuiUtils.drawBlockIcon(mc, itemStack, scale, totTranslationX, totTranslationY, totTranslationZ,dir,up,onSide);
    }
    
    //get icon for a template
    public static ResourceLocation getResourceLocationsByName(Template template,EnumFacing face, boolean selected)
    {
        File mcDataDir = Minecraft.getMinecraft().mcDataDir;
        File saves = new File(mcDataDir, "advcreation_templates");
        
        if (!saves.exists()) {
            if (!saves.mkdirs()) {
                return null;
            }
        } else if (!saves.isDirectory()) {
            return null;
        }
        File templateDir = new File(saves, template.getDirname());
        if(template.getNewDirName() != null)
            templateDir = new File(saves,template.getNewDirName());
        
        if (templateDir.exists()) {
            ResourceLocation output = null;
            String name = template.getName() + "_" + face.toString() + "_selected_" + selected;
            String fileName = face.toString() + "_selected_" + selected + ".png";
            
            File icon = new File(templateDir, fileName);
            if (icon.exists()) {
                InputStream inputstream  = null;
                try
                {
                    Minecraft mc = Minecraft.getMinecraft();
                    inputstream = new FileInputStream(icon);
                    output = mc.renderEngine.getDynamicTextureLocation(name, new DynamicTexture(ImageIO.read(inputstream)));
                    
                }
                catch (IOException ioexception)
                {
                    System.out.printf("Unable to load logo: {} \n", icon, ioexception);
                }
                finally
                {
//                    IOUtils.closeQuietly(inputstream);
                    try {
                        inputstream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                
            } else
                return null;
            return output;
        } else
            return null;
    }
    
    
    public static boolean delectIcons(String templateName)
    {
        File mcDataDir = Minecraft.getMinecraft().mcDataDir;
        File saves = new File(mcDataDir, "advcreation_templates");
    
        if(!saves.exists())
        {
            if (!saves.mkdirs())
                return false;
        }
        else if(!saves.isDirectory())
            return false;
        
        File templateDir = new File(saves, templateName.split(".nbt")[0]);
        if(templateDir.exists())
        {
            
            String[] fileNames = new String[8];
        
            EnumFacing orient = EnumFacing.WEST;
            boolean selected = false;
        
            //generate the file name to be loaded
            for (int i = 0; i < 4; i++) {
                fileNames[i] = orient.toString() + "_selected_" + selected + ".png";
                orient = orient.rotateY();
            }
            orient = EnumFacing.WEST;
            selected = true;
        
            for (int i = 4; i < 8; i++) {
                fileNames[i] = orient.toString() + "_selected_" + selected + ".png";
                orient = orient.rotateY();
            }
        
            for (int i = 0; i < fileNames.length; i++) {
                File icon = new File(templateDir, fileNames[i]);
                if (icon.exists()) {
                    icon.delete();
                }
                else
                    return  false;
            }
            return true;
        }
        
        return false;
    }
    
    
    public static void renderItemAndEffectIntoGUI(ItemStack stack, int xPosition, int yPosition, EnumFacing dir,boolean up,boolean onSide)
    {
        renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, stack, xPosition, yPosition,dir,up,onSide);
    }
    
    public static void renderItemAndEffectIntoGUI(@Nullable EntityLivingBase p_184391_1_, final ItemStack p_184391_2_, int p_184391_3_, int p_184391_4_, EnumFacing dir,boolean up,boolean onSide)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (!p_184391_2_.isEmpty())
        {
            mc.getRenderItem().zLevel += 50.0F;
            
            try
            {
                renderItemModelIntoGUI(p_184391_2_, p_184391_3_, p_184391_4_, mc.getRenderItem().getItemModelWithOverrides(p_184391_2_, (World)null, p_184391_1_),dir,up,onSide);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
                crashreportcategory.addDetail("Item Type", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return String.valueOf((Object)p_184391_2_.getItem());
                    }
                });
                crashreportcategory.addDetail("Item Aux", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return String.valueOf(p_184391_2_.getMetadata());
                    }
                });
                crashreportcategory.addDetail("Item NBT", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return String.valueOf((Object)p_184391_2_.getTagCompound());
                    }
                });
                crashreportcategory.addDetail("Item Foil", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return String.valueOf(p_184391_2_.hasEffect());
                    }
                });
                throw new ReportedException(crashreport);
            }
    
            mc.getRenderItem().zLevel -= 50.0F;
        }
    }
    
    protected static void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedmodel, EnumFacing dir, boolean up,boolean onSide)
    {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        setupGuiTransform(x, y, bakedmodel.isGui3d(),dir,up,onSide,stack.getItem() instanceof ItemBlockSpecial);
        bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
        mc.getRenderItem().renderItem(stack, bakedmodel);
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }
    
    private static void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d, EnumFacing dir,boolean up,boolean onSide, boolean noRot)
    {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.translate((float)xPosition, (float)yPosition, 100.0F + mc.getRenderItem().zLevel);
        GlStateManager.translate(8.0F, 8.0F, 0.0F);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.scale(16.0F, 16.0F, 16.0F);

        if(!noRot)
        {
            float x_h= 0F;
            float y_h= 1F;
            float z_h= 0.57F;

            float x_v= 4.6F;
            float y_v= -2.3F;
            float z_v= 4.0F;

            if(dir == EnumFacing.NORTH)
            {
                GlStateManager.rotate(90F,x_h,y_h,z_h);
            }
            else if(dir == EnumFacing.WEST)
            {
                GlStateManager.rotate(180F, x_h, y_h, z_h);
            }
            else if(dir == EnumFacing.SOUTH)
            {
                GlStateManager.rotate(270F,x_h,y_h,z_h);
            }

            //rotate block upside down
            if(up)
                GlStateManager.rotate(180F,x_v,y_v,z_v);

            //rotate block to its side
            if(onSide)
                GlStateManager.rotate(90F,x_v,y_v,z_v);
        }

        
        if (isGui3d)
        {
            GlStateManager.enableLighting();
        }
        else
        {
            GlStateManager.disableLighting();
        }
    }


    public static ArrayList<String> getListStringsContaining(String subString, ArrayList<String> list) {
        ArrayList<String> res = new ArrayList<>();
        for(String string: list)
        {
            if(string.contains(subString))
                res.add(string);
        }
        return res;
    }

    public static ArrayList<Integer> getListIndexContaining(String subString, ArrayList<String> list) {
        ArrayList<Integer> res = new ArrayList<>();
        for(int i =0 ; i< list.size();i++)
        {
            String string = list.get(i);
            if(string.contains(subString))
                res.add(i);
        }
        return res;
    }

    public static  HashMap<Integer,String> getMapIndexStringContaining(String subString, ArrayList<String> list) {
        HashMap<Integer,String> res = new  HashMap<Integer,String>();
        for(int i =0 ; i< list.size();i++)
        {
            String string = list.get(i);
            if(string.contains(subString))
                res.put(i,string);
        }
        return res;
    }

    public static  HashMap<Integer,String> getMapIndexStringContaining(String subString, HashMap<Integer,String> map) {
        HashMap<Integer,String> res = new  HashMap<Integer,String>();
        for(Map.Entry<Integer,String> entry:map.entrySet())
        {

            if(entry.getValue().contains(subString))
                res.put(entry.getKey(),entry.getValue());
        }
        return res;
    }


}
