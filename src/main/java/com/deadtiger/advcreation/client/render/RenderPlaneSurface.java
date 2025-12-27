package com.deadtiger.advcreation.client.render;

import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

/***
 * This code comes from an Awesome mod named Effortless building
 *  Mod site : https://www.curseforge.com/minecraft/mc-mods/effortless-building
 *  source :  https://bitbucket.org/Requios/effortless-building/src/master/src/main/java/nl/requios/effortlessbuilding/render/
 *
 * The sole purpose is to render the transperent planes that indicate in which surface the mouse is moving within the 3D space
 *
 */
@SideOnly(Side.CLIENT)
public class RenderPlaneSurface
{
    
    protected static Color COLOR_X = new Color(255, 72, 52);
    protected static Color COLOR_Y = new Color(67, 204, 51);
    protected static Color COLOR_Z = new Color(52, 247, 255);
    protected static final int LINE_ALPHA = 200;
    protected static final int PLANE_ALPHA = 75;
    protected static final Vec3d EPSILON = new Vec3d(0.001, 0.001, 0.001); //prevents z-fighting

    
    public static void render(Vec3d pos, double radius, EnumDirectionMode dirMode, float partialTicks, double extendLines) {
        if (pos != null)
            pos = pos.add(EPSILON);
        else
            return;
    
        COLOR_X = new Color(0, 200, 0);
        COLOR_Y = new Color(0, 0, 200);
        COLOR_Z = new Color(200, 0, 0);

        boolean enabled = true;
        boolean mirrorX = (dirMode == EnumDirectionMode.ZY);
        boolean mirrorY = (dirMode == EnumDirectionMode.XZ);
        boolean mirrorZ = (dirMode == EnumDirectionMode.XY);

        GlStateManager.disableLighting();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        if (enabled && dirMode != null) {
            if (pos == null)
                return;
        
            if (mirrorX)
                renderZY(Minecraft.getMinecraft(),pos,radius,partialTicks,0.0F,0.8f,0.0f,0.3f, extendLines);
            if (mirrorY)
                renderXZ(Minecraft.getMinecraft(),pos,radius,partialTicks,0.0F,0.0f,0.8f,0.3f, extendLines);
            if (mirrorZ)
                renderXY(Minecraft.getMinecraft(),pos,radius,partialTicks,0.8F,0.0f,0.0f,0.3f, extendLines);
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableLighting();
    }
    
    
    public static void renderZY(Minecraft mc, Vec3d pos, double radius, float partialTicks, float red, float green, float blue, float alpha, double extendLines)
    {
        EntityPlayer entityplayer = mc.player;
        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;
        
        Vec3d posA = new Vec3d(pos.x, pos.y - radius, pos.z - radius);
        Vec3d posB = new Vec3d(pos.x, pos.y + radius, pos.z + radius);
    
        double x1 = posA.x - d0;
        double y1 = posA.y - d1;
        double z1 = posA.z - d2;
        double x2 = posB.x - d0;
        double y2 = posB.y - d1;
        double z2 = posB.z - d2;

        drawLine(x1, pos.y - d1,z1-extendLines, x1,pos.y - d1, z2+extendLines, entityplayer, 0, partialTicks, 0.0F, 1.0F, 0.0F);
        drawLine(x1, y1-extendLines,pos.z - d2, x1,y2+extendLines, pos.z - d2, entityplayer, 0, partialTicks, 0.0F, 1.0F, 0.0F);
        renderPlayerPlane(x1, y1, z1, x2, y2, z2, red, green, blue, alpha);
    }



    public static void renderXY(Minecraft mc, Vec3d pos, double radius, float partialTicks, float red, float green, float blue, float alpha, double extendLines)
    {
        EntityPlayer entityplayer = mc.player;
        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;

        Vec3d posA = new Vec3d(pos.x - radius, pos.y - radius, pos.z);
        Vec3d posB = new Vec3d(pos.x + radius, pos.y + radius, pos.z);

        double x1 = posA.x - d0;
        double y1 = posA.y - d1;
        double z1 = posA.z - d2;
        double x2 = posB.x - d0;
        double y2 = posB.y - d1;
        double z2 = posB.z - d2;

        drawLine(x1-extendLines, pos.y - d1,z2, x2+extendLines,pos.y - d1, z2, entityplayer, 0, partialTicks, 1.0F,0.0F,  0.0F);
        drawLine(pos.x - d0, y1-extendLines,z2, pos.x - d0,y2+extendLines, z2, entityplayer, 0, partialTicks, 1.0F,0.0F,  0.0F);
        renderPlayerPlane(x1,y1,z1, x2,y2,z2,red,green,blue,alpha);
    }

    public static void renderXZ(Minecraft mc, Vec3d pos, double radius, float partialTicks, float red, float green, float blue, float alpha, double extendLines)
    {
        EntityPlayer entityplayer = mc.player;
        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;

        Vec3d posA = new Vec3d(pos.x - radius, pos.y, pos.z - radius);
        Vec3d posB = new Vec3d(pos.x + radius, pos.y, pos.z + radius);

        double x1 = posA.x - d0;
        double y1 = posA.y - d1;
        double z1 = posA.z - d2;
        double x2 = posB.x - d0;
        double y2 = posB.y - d1;
        double z2 = posB.z - d2;

        drawLine(x1-extendLines, y1,pos.z- d2, x2+extendLines,y1, pos.z- d2, entityplayer, 0, partialTicks, 0.0F, 0.0F, 1.0F);
        drawLine(pos.x - d0, y1,z1-extendLines, pos.x - d0,y1, z2+extendLines, entityplayer, 0, partialTicks, 0.0F, 0.0F, 1.0F);
        renderPlayerPlane(x1,y1,z1,x2,y2,z2,red,green,blue,alpha);

    }

    public static void renderPlayerPlane(double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha)
    {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        //ZY
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        if(y1 == y2)
        {
            bufferbuilder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
        }
        else
        {
            bufferbuilder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
        }

        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void renderHighlightZY(Minecraft mc,Vec3d posA,Vec3d posB, float partialTicks,float red,float green,float blue,float alpha)
    {
        EntityPlayer entityplayer = mc.player;
        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;
        
        double x = posA.x - d0;
        double y = posA.y - d1;
        double z = posA.z - d2;
        double X = posB.x - d0;
        double Y = posB.y - d1;
        double Z = posB.z - d2;
        
//        drawLine (posA.x,posA.y,posA.z ,posA.x,posA.y, posB.z, entityplayer, 0, partialTicks, 0.0F, 1.0F, 0.0F);
//        drawLine(posA.x,posA.y,posB.z ,posA.x,posB.y, posB.z, entityplayer, 0, partialTicks, 0.0F, 1.0F, 0.0F);
//        drawLine(posA.x,posB.y, posB.z,posA.x,posB.y,posA.z, entityplayer, 0, partialTicks, 0.0F, 1.0F, 0.0F);
//        drawLine(posA.x,posB.y,posA.z,posA.x,posA.y, posA.z, entityplayer, 0, partialTicks, 0.0F, 1.0F, 0.0F);

        renderPlayerPlane(x, y, z, x, Y, Z, red, green, blue, alpha);
    }
    
    public static void renderHighlightXY(Minecraft mc,Vec3d posA,Vec3d posB, float partialTicks,float red,float green,float blue,float alpha)
    {
        EntityPlayer entityplayer = mc.player;
        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;
        
        double x = posA.x - d0;
        double y = posA.y - d1;
        double z = posA.z - d2;
        double X = posB.x - d0;
        double Y = posB.y - d1;
        double Z = posB.z - d2;

//        drawLine (posA.x,posA.y,posA.z ,posB.x,posA.y, posA.z, entityplayer, 0, partialTicks,  1.0F,0.0F, 0.0F);
//        drawLine(posB.x,posA.y,posA.z ,posB.x,posB.y, posA.z, entityplayer, 0, partialTicks,  1.0F,0.0F, 0.0F);
//        drawLine(posB.x,posB.y, posA.z,posA.x,posB.y,posA.z, entityplayer, 0, partialTicks, 1.0F,0.0F, 0.0F);
//        drawLine(posA.x,posB.y,posA.z,posA.x,posA.y, posA.z, entityplayer, 0, partialTicks,  1.0F,0.0F, 0.0F);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        
        //XY
        Tessellator tessellator1 = Tessellator.getInstance();
        BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
        bufferbuilder1.begin(5, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder1.pos(x, Y, Z).color(red, green, blue, alpha).endVertex();
        bufferbuilder1.pos(x, y, Z).color(red, green, blue, alpha).endVertex();
        bufferbuilder1.pos(X, Y, Z).color(red, green, blue, alpha).endVertex();
        bufferbuilder1.pos(X, y, Z).color(red, green, blue, alpha).endVertex();
        tessellator1.draw();
        
        
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void renderHighlightXZ(Minecraft mc,Vec3d posA,Vec3d posB, float partialTicks,float red,float green,float blue,float alpha)
    {
        EntityPlayer entityplayer = mc.player;
        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;

        double x = posA.x - d0;
        double y = posA.y - d1;
        double z = posA.z - d2;
        double X = posB.x - d0;
        double Y = posB.y - d1;
        double Z = posB.z - d2;

        //XZ
//        drawLine (posA.x,posA.y,posA.z ,posB.x,posA.y, posA.z, entityplayer, 0, partialTicks,0.0F, 0.0F,  1.0F);
//        drawLine(posB.x,posA.y,posA.z ,posB.x,posA.y, posB.z, entityplayer, 0, partialTicks,0.0F, 0.0F,  1.0F);
//        drawLine(posB.x,posA.y, posB.z,posA.x,posA.y,posB.z, entityplayer, 0, partialTicks,0.0F, 0.0F, 1.0F);
//        drawLine(posA.x,posA.y,posB.z,posA.x,posA.y, posA.z, entityplayer, 0, partialTicks,0.0F, 0.0F,  1.0F);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, z).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(X, y, z).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, Z).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(X, y, Z).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    
    public static void render(Vec3d pos1, Vec3d pos2, EnumDirectionMode dirMode, float partialTicks) {
        begin(partialTicks);
        beginLines();
    

        
        COLOR_X = new Color(200, 0, 0);
        COLOR_Y = new Color(0, 0,200);
        COLOR_Z = new Color(0, 200, 0);
        
        //Mirror lines and areas

        boolean enabled = true;
        boolean mirrorX = (dirMode == EnumDirectionMode.ZY);
        boolean mirrorY = (dirMode == EnumDirectionMode.XZ);
        boolean mirrorZ = (dirMode == EnumDirectionMode.XY);

        int x = -1;
        int y = -1;
        int z = -1;
            
        if ( enabled && dirMode != null)
        {
            if(pos1 == null)
                return;

            pos1 = pos1.add(EPSILON);
            pos2 = pos2.add(EPSILON);
            
            if (mirrorX)
            {
                drawMirrorPlane(new Vec3d(pos1.x, pos1.y - y, pos1.z - z),
                        new Vec3d(pos2.x, pos2.y + y, pos2.z + z), COLOR_X,
                        true, true, true);
            }
            if (mirrorY)
            {
                drawMirrorPlaneY(new Vec3d(pos1.x - x, pos1.y, pos1.z - z),
                        new Vec3d(pos2.x + x, pos2.y, pos2.z + z), COLOR_Y,
                        true, true);
            }
            if (mirrorZ)
            {
                drawMirrorPlane(new Vec3d(pos1.x - x, pos1.y - y, pos1.z),
                        new Vec3d(pos2.x + x, pos2.y + y, pos2.z), COLOR_Z,
                        true, true, true);
            }

        }
        
        endLines();
        end();
    }

    public static void drawLine(double worldBeginX,double worldBeginY,double worldBeginZ,double worldEndX,double worldEndY,double worldEndZ, EntityPlayer player, int execute, float partialTicks,float red,float green,float blue)
    {
        if (execute == 0)
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            
            RenderTemplate.drawSelectionLine(worldBeginX,worldBeginY, worldBeginZ,
                worldEndX,worldEndY, worldEndZ, red, green, blue, 0.8F);
            
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
    
    
    protected static void drawMirrorPlaneY(Vec3d posA, Vec3d posB, Color c, boolean drawLines, boolean drawPlanes)
    {
        
        GL11.glColor4d(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        
        if (drawPlanes) {
            bufferBuilder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            
            bufferBuilder.pos(posA.x, posA.y, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), PLANE_ALPHA).endVertex();
            bufferBuilder.pos(posA.x, posA.y, posB.z).color(c.getRed(), c.getGreen(), c.getBlue(), PLANE_ALPHA).endVertex();
            bufferBuilder.pos(posB.x, posA.y, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), PLANE_ALPHA).endVertex();
            bufferBuilder.pos(posB.x, posA.y, posB.z).color(c.getRed(), c.getGreen(), c.getBlue(), PLANE_ALPHA).endVertex();
            
            tessellator.draw();
        }
        
        if (drawLines) {
            Vec3d middle = posA.add(posB).scale(0.5);
            bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            
            bufferBuilder.pos(middle.x, middle.y, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), LINE_ALPHA).endVertex();
            bufferBuilder.pos(middle.x, middle.y, posB.z).color(c.getRed(), c.getGreen(), c.getBlue(), LINE_ALPHA).endVertex();
            bufferBuilder.pos(posA.x, middle.y, middle.z).color(c.getRed(), c.getGreen(), c.getBlue(), LINE_ALPHA).endVertex();
            bufferBuilder.pos(posB.x, middle.y, middle.z).color(c.getRed(), c.getGreen(), c.getBlue(), LINE_ALPHA).endVertex();
            
            tessellator.draw();
        }
    }
    
    
    protected static void drawMirrorPlane(Vec3d posA, Vec3d posB, Color c, boolean drawLines, boolean drawPlanes, boolean drawVerticalLines) {
        
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        float red = 0.0F;
        float green = 0.8F;
        float blue = 0.0F;
        float alpha = 0.3F;

        if (drawPlanes) {
            bufferBuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
            bufferBuilder.pos(posA.x, posA.y, posA.z).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(posA.x, posA.y, posB.z).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(posA.x, posB.y, posA.z).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(posA.x, posB.y, posB.z).color(red, green, blue, alpha).endVertex();
            tessellator.draw();
            
        }
        
        if (drawLines) {
            Vec3d middle = posA.add(posB).scale(0.5);
            bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            
            bufferBuilder.pos(posA.x, middle.y, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), LINE_ALPHA).endVertex();
            bufferBuilder.pos(posB.x, middle.y, posB.z).color(c.getRed(), c.getGreen(), c.getBlue(), LINE_ALPHA).endVertex();
            if (drawVerticalLines) {
                bufferBuilder.pos(middle.x, posA.y, middle.z).color(c.getRed(), c.getGreen(), c.getBlue(), LINE_ALPHA).endVertex();
                bufferBuilder.pos(middle.x, posB.y, middle.z).color(c.getRed(), c.getGreen(), c.getBlue(), LINE_ALPHA).endVertex();
            }
            
            tessellator.draw();
        }
    }
    
    protected static void beginLines() {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glLineWidth(2);
    }
    
    protected static void endLines() {
        GL11.glPopAttrib();
    }
    
    private static void end() {
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
    
    private static void begin(float partialTicks) {
        EntityPlayer player = Minecraft.getMinecraft().player;
    
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
    
        Vec3d playerPos = new Vec3d(playerX, playerY, playerZ);
        
        GL11.glPushMatrix();
        GL11.glTranslated(-playerPos.x, -playerPos.y, -playerPos.z);
        
        GL11.glDepthMask(false);
    }
}
