package com.deadtiger.advcreation.client.render;

import com.deadtiger.advcreation.block.ModBlocks;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.build_template.BuildTemplateMode;
import com.deadtiger.advcreation.client.gui.gui_screen.selection_wheel.GuiAdjustModeSelectionScreen;
import com.deadtiger.advcreation.client.input.KeyInputHandler;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.utility.FakeWorld;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderShulker;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class RenderPreview
{
    public static ArrayList<TemplateBlock> previewList = new ArrayList<>();
    public static BlockPos selectionStartPos = BlockPos.ORIGIN;
    public static BlockPos selectionEndPos = BlockPos.ORIGIN;

    private static final ResourceLocation ENDER_CHEST_TEXTURE = new ResourceLocation("textures/entity/chest/ender.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
    private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");
    private static final ResourceLocation DRAGON_TEXTURES = new ResourceLocation("textures/entity/enderdragon/dragon.png");

    private static final ResourceLocation[] TEXTURES;

    public static int drawTransparentPreviewBlock(BlockPos selectedWorldPosition, TemplateBlock tempBlock, EntityPlayer entityplayer, double partialTicks, EnumFacing hitSide)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableLighting();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogic();

        int hashcode = drawBarePreviewBlock(selectedWorldPosition,tempBlock,entityplayer,partialTicks,hitSide);

        GlStateManager.enableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogic();
        GlStateManager.popMatrix();
        return hashcode;
    }


    public static int drawPreviewBlock(BlockPos selectedWorldPosition, TemplateBlock tempBlock, EntityPlayer entityplayer, double partialTicks, EnumFacing hitSide)
    {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableColorLogic();
        int hashcode = drawBarePreviewBlock(selectedWorldPosition,tempBlock,entityplayer,partialTicks,hitSide);


        GlStateManager.enableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogic();
        GlStateManager.popMatrix();
        return hashcode;
    }

    public static int drawBarePreviewBlock(BlockPos selectedWorldPosition, TemplateBlock tempBlock, EntityPlayer entityplayer, double partialTicks, EnumFacing hitSide)
    {

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            int hashcode = 0;
            if(tempBlock.getBlockState().getBlock() instanceof BlockChest || tempBlock.getBlockState().getBlock() instanceof BlockEnderChest)
            {
                BlockPos new_pos = renderChestBlock(selectedWorldPosition, tempBlock, entityplayer, partialTicks);
                hashcode = tempBlock.getBlockState().hashCode() + new_pos.hashCode();
                previewList.add(new TemplateBlock(tempBlock.getFace(), new_pos, tempBlock.getBlockState()));
            }
            else if(tempBlock.getBlockState().getBlock() instanceof BlockShulkerBox )
            {
                BlockPos new_pos = renderShulkerBox(selectedWorldPosition, tempBlock, entityplayer, partialTicks);
                hashcode = tempBlock.getBlockState().hashCode() + new_pos.hashCode();
                previewList.add(new TemplateBlock(tempBlock.getFace(), new_pos, tempBlock.getBlockState()));
            }
            else if(tempBlock.getBlockState().getBlock() instanceof BlockBed)
            {
                BlockPos new_pos = renderBedBlock(selectedWorldPosition, tempBlock, entityplayer, partialTicks);
                hashcode = tempBlock.getBlockState().hashCode() + new_pos.hashCode();
                previewList.add(new TemplateBlock(tempBlock.getFace(), new_pos, tempBlock.getBlockState()));
            }
            else if(tempBlock.getBlockState().getBlock() instanceof BlockSign)
            {
                BlockPos new_pos = renderSignBlock(selectedWorldPosition, tempBlock, entityplayer, partialTicks);
                hashcode = tempBlock.getBlockState().hashCode() + new_pos.hashCode();
                previewList.add(new TemplateBlock(tempBlock.getFace(), new_pos, tempBlock.getBlockState()));
            }
            else if(tempBlock.getBlockState().getBlock() instanceof BlockSkull)
            {
                BlockPos new_pos = renderSkull(selectedWorldPosition, tempBlock, entityplayer, partialTicks,hitSide);
                hashcode = tempBlock.getBlockState().hashCode() + new_pos.hashCode();
                previewList.add(new TemplateBlock(tempBlock.getFace(), new_pos, tempBlock.getBlockState(),tempBlock.getTileEntity()));
            }
            else if(tempBlock.getBlockState().getBlock() instanceof BlockBanner)
            {
                BlockPos new_pos = renderBanner(selectedWorldPosition, tempBlock, entityplayer, partialTicks,hitSide);
                hashcode = tempBlock.getBlockState().hashCode() + new_pos.hashCode();
                previewList.add(new TemplateBlock(tempBlock.getFace(), new_pos, tempBlock.getBlockState(),tempBlock.getTileEntity()));
            }
            else if(tempBlock.getBlockState().getBlock() instanceof ITileEntityProvider && tempBlock.getTileEntity() != null && TileEntityRendererDispatcher.instance.<TileEntity>getRenderer(tempBlock.getTileEntity())!= null)
            {
                GlStateManager.pushMatrix();
                {
                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder vertexbuffer = tessellator.getBuffer();

                    //Compensate for the render giving everything an offset relative to the player
                    //Interpolating everything back to 0,0,0. These are transforms you can find at RenderEntity class
                    double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * partialTicks;
                    double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * partialTicks;
                    double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * partialTicks;
                    //Apply 0-our transforms to set everything back to 0,0,0
                    TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = TileEntityRendererDispatcher.instance.<TileEntity>getRenderer(tempBlock.getTileEntity());

//                BlockPos new_pos = renderBanner(selectedWorldPosition, tempBlock, entityplayer, partialTicks,hitSide);
                    BlockPos new_pos = selectedWorldPosition.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());
                    hashcode = tempBlock.getBlockState().hashCode() + new_pos.hashCode();

                    TileEntity tile = tempBlock.getTileEntity();
                    tile.setWorld(FakeWorld.INSTANCE.setPreviewBlockState(tempBlock.getBlockState()));
                    if (tile.getWorld().getBlockState(tile.getPos()).getPropertyKeys().contains(BlockHorizontal.FACING))
                    {
                        EnumFacing face = tile.getWorld().getBlockState(tile.getPos()).getValue(BlockHorizontal.FACING);

                    }
                    tileentityspecialrenderer.render(tile,new_pos.getX()-d0,new_pos.getY()-d1,new_pos.getZ()-d2,(float)partialTicks,-1,1f);
//
                    previewList.add(new TemplateBlock(tempBlock.getFace(), new_pos, tempBlock.getBlockState(),tile));


                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();

//                    hashcode = tempBlock.getBlockState().hashCode() + new_pos.hashCode();
//                    previewList.add(new TemplateBlock(tempBlock.getFace(), new_pos, tempBlock.getBlockState(),tempBlock.getTileEntity()));
                }
                GlStateManager.popMatrix();
            }
            else
            {
                GlStateManager.pushMatrix();
                {
                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                    if(GuiAdjustModeSelectionScreen.otherSetting6On)
                    {
                        Minecraft.getMinecraft().renderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(0,0,0,0,0,0), 1f, 1f, 1f, 0.6f);
                    }


                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder vertexbuffer = tessellator.getBuffer();

                    //Compensate for the render giving everything an offset relative to the player
                    //Interpolating everything back to 0,0,0. These are transforms you can find at RenderEntity class
                    double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * partialTicks;
                    double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * partialTicks;
                    double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * partialTicks;
                    //Apply 0-our transforms to set everything back to 0,0,0
                    vertexbuffer.setTranslation(-d0, -d1, -d2);

                    vertexbuffer.begin(7, DefaultVertexFormats.BLOCK);
                    BlockPos new_pos = selectedWorldPosition.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());
                    GlStateManager.translate(new_pos.getX(), new_pos.getY(), new_pos.getZ());

                    BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                    IBlockState state = tempBlock.getBlockState().getBlock().getExtendedState(tempBlock.getBlockState(), entityplayer.world, new_pos);

                    if(state.getBlock() == Blocks.WATER)
                    {
                        state = ModBlocks.WATER_PREVIEW.getDefaultState();
                    }
                    if(state.getBlock() == Blocks.LAVA)
                    {
                        state = ModBlocks.LAVA_PREVIEW.getDefaultState();
                    }

                    if(state.getBlock() instanceof BlockWall)
                    {
                        for (IProperty prop: state.getPropertyKeys())
                        {
                            if(prop instanceof PropertyBool && prop.getName().equals("up"))
                            {
                                state = state.withProperty(prop,true);
                            }
                        }
                    }

                    if(GuiAdjustModeSelectionScreen.otherSetting1On)
                    {
                        //test disabling and enabling GlState settings
                        if(GuiAdjustModeSelectionScreen.alphaOn)
                            GlStateManager.enableAlpha();
                        else
                            GlStateManager.disableAlpha();
                        if(GuiAdjustModeSelectionScreen.blendOn)
                            GlStateManager.enableBlend();
                        else
                            GlStateManager.disableBlend();
                        if(GuiAdjustModeSelectionScreen.colorOn)
                            GlStateManager.enableColorLogic();
                        else
                            GlStateManager.disableColorLogic();
                        if(GuiAdjustModeSelectionScreen.depthOn)
                            GlStateManager.enableDepth();
                        else
                            GlStateManager.disableDepth();
                        if(GuiAdjustModeSelectionScreen.colorMatOn)
                            GlStateManager.enableColorMaterial();
                        else
                            GlStateManager.disableColorMaterial();
                        if(GuiAdjustModeSelectionScreen.otherSetting2On)
                            GlStateManager.color(1f,1f,1f,0.6f);
                        if(GuiAdjustModeSelectionScreen.otherSetting3On)
                            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                        if(GuiAdjustModeSelectionScreen.otherSetting4On)
                            GlStateManager.depthMask(true);
                        if(GuiAdjustModeSelectionScreen.otherSetting5On)
                            GlStateManager.enableTexture2D();

                    }



                    blockrendererdispatcher.getBlockModelRenderer().renderModelFlat(
                            entityplayer.world,
                            blockrendererdispatcher.getModelForState(state),
                            state,
                            new BlockPos(0, 0, 0),
                            vertexbuffer, false, MathHelper.getPositionRandom(selectedWorldPosition));

                    tessellator.draw();
                    vertexbuffer.setTranslation(0.0, 0.0, 0.0);

                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();

                    if(GuiAdjustModeSelectionScreen.otherSetting1On)
                    {
                        if (!GuiAdjustModeSelectionScreen.colorOn)
                            GlStateManager.enableColorLogic();
                        else
                            GlStateManager.disableColorLogic();
                        if (!GuiAdjustModeSelectionScreen.blendOn)
                            GlStateManager.enableBlend();
                        else
                            GlStateManager.disableBlend();
                        if (!GuiAdjustModeSelectionScreen.alphaOn)
                            GlStateManager.enableAlpha();
                        else
                            GlStateManager.disableAlpha();
                        if (!GuiAdjustModeSelectionScreen.depthOn)
                            GlStateManager.enableDepth();
                        else
                            GlStateManager.disableDepth();
                        if(!GuiAdjustModeSelectionScreen.colorMatOn)
                            GlStateManager.enableColorMaterial();
                        else
                            GlStateManager.disableColorMaterial();
                        if(!GuiAdjustModeSelectionScreen.otherSetting2On)
                            GlStateManager.color(1f,1f,1f,0f);
                        if(GuiAdjustModeSelectionScreen.otherSetting6On)
                            Minecraft.getMinecraft().renderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(0,0,0,0,0,0), 1f, 1f, 1f, 0f);


                    }

                    hashcode = tempBlock.getBlockState().hashCode() + new_pos.hashCode();
                    previewList.add(new TemplateBlock(tempBlock.getFace(), new_pos, tempBlock.getBlockState()));
                }
                GlStateManager.popMatrix();
            }

        return hashcode;
    }

    private static BlockPos renderChestBlock(BlockPos selectedWorldPosition, TemplateBlock tempBlock, EntityPlayer entityplayer, double partialTicks) {

        TileEntityChest chestBasic = new TileEntityChest(BlockChest.Type.BASIC);

        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * partialTicks;
        BlockPos new_pos = selectedWorldPosition.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());

        //render the chest myself
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5379); //CRITICAL !!!!
        GlStateManager.enableColorMaterial();
        int i;


        ModelChest modelchest = new ModelChest();
        if(tempBlock.getBlockState().getBlock() instanceof BlockEnderChest)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(ENDER_CHEST_TEXTURE);
        }
        else
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_NORMAL);
        }


        GlStateManager.pushMatrix();
        {
            GlStateManager.enableRescaleNormal();
            GlStateManager.translate((float) new_pos.getX() - d0, (float) new_pos.getY() - d1 + 1.0F, (float) new_pos.getZ() - d2 + 1.0F);
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);

            int j = 0;
            i = tempBlock.getBlockState().getBlock().getMetaFromState(tempBlock.getBlockState());
//            if (i == 2)
//                j = 0;
//            if (i == 3)
//                j = 180;
//            if (i == 4)
//                j = -90;
//            if (i == 5)
//                j = 90;

            if (i == 2)
                j = 180;
            if (i == 3)
                j = 0;
            if (i == 4)
                j = 90;
            if (i == 5)
                j = -90;

            GlStateManager.rotate((float) j, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            modelchest.renderAll();
            GlStateManager.disableRescaleNormal();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableColorLogic();
        GlStateManager.disableColorMaterial();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        return new_pos;
    }

    private static BlockPos renderSignBlock(BlockPos selectedWorldPosition, TemplateBlock tempBlock, EntityPlayer entityplayer, double partialTicks) {

        BlockSign blockSign = (BlockSign) tempBlock.getBlockState().getBlock();
        int metaData = blockSign.getMetaFromState(tempBlock.getBlockState());
        TileEntitySign tileEntitySign = (TileEntitySign) blockSign.createNewTileEntity(entityplayer.world,metaData);

        renderSign(tileEntitySign,selectedWorldPosition.getX() + tempBlock.getX_offset(),selectedWorldPosition.getY() + tempBlock.getY_offset(),selectedWorldPosition.getZ()+ tempBlock.getZ_offset(),(float)partialTicks,-1,1F,tempBlock.getBlockState().getBlock(),metaData,entityplayer);

        return selectedWorldPosition;
    }

    //most of this code comes from TileEntitySignRenderer.render
    public static void renderSign(  TileEntitySign te, double x, double y, double z, float partialTicks, int destroyStage, float alpha,Block block,int metaData,EntityPlayer entityplayer)
    {
        TileEntitySignRenderer renderer = (TileEntitySignRenderer)  TileEntityRendererDispatcher.instance.renderers.get(TileEntitySign.class);
        ModelSign model = new ModelSign();
        ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");

        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * partialTicks;

        Minecraft.getMinecraft().entityRenderer.enableLightmap();

        //the rest of this code is taken from TileEntitySignRenderer.render
        GlStateManager.pushMatrix();
        //code  from TileEntityRendererDispatcher.render
        RenderHelper.enableStandardItemLighting();
//        int a = entityplayer.world.getCombinedLight(te.getPos(), 0);
        int a = 15728832;
        int b = a % 65536;
        int c = a / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)b, (float)c);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        //enablingColorLogic and ColorMaterial worked to fix the transparency issue!!
//        GlStateManager.disableAlpha();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5379); //CRITICAL !!!!
        GlStateManager.enableColorMaterial();
        float f = 0.6666667F;

        if (block == Blocks.STANDING_SIGN)
        {
            GlStateManager.translate((float)x + 0.5F -d0, (float)y + 0.5F -d1, (float)z + 0.5F -d2);
            float f1 = (float)(metaData * 360) / 16.0F;
            GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);
            model.signStick.showModel = true;
        }
        else
        {
            int k = metaData;
            float f2 = 0.0F;

            if (k == 2)
            {
                f2 = 180.0F;
            }

            if (k == 4)
            {
                f2 = 90.0F;
            }

            if (k == 5)
            {
                f2 = -90.0F;
            }

            GlStateManager.translate((float)x + 0.5F-d0, (float)y + 0.5F-d1, (float)z + 0.5F -d2);
            GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
            model.signStick.showModel = false;
        }


        TileEntityRendererDispatcher.instance.renderEngine.bindTexture(SIGN_TEXTURE);


        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
        model.renderSign();
        GlStateManager.popMatrix();
        FontRenderer fontrenderer = renderer.getFontRenderer();
        float f3 = 0.010416667F;
        GlStateManager.translate(0.0F, 0.33333334F, 0.046666667F);
        GlStateManager.scale(0.010416667F, -0.010416667F, 0.010416667F);
        GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
        GlStateManager.depthMask(false);
        int i = 0;

        if (destroyStage < 0)
        {
            for (int j = 0; j < te.signText.length; ++j)
            {
                if (te.signText[j] != null)
                {
                    ITextComponent itextcomponent = te.signText[j];
                    List<ITextComponent> list = GuiUtilRenderComponents.splitText(itextcomponent, 90, fontrenderer, false, true);
                    String s = list != null && !list.isEmpty() ? ((ITextComponent)list.get(0)).getFormattedText() : "";

                    if (j == te.lineBeingEdited)
                    {
                        s = "> " + s + " <";
                        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - te.signText.length * 5, 0);
                    }
                    else
                    {
                        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - te.signText.length * 5, 0);
                    }
                }
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        GlStateManager.enableAlpha();
        GlStateManager.disableColorLogic();
        GlStateManager.disableColorMaterial();
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        GlStateManager.popMatrix();


    }

    private static BlockPos renderBedBlock(BlockPos selectedWorldPosition, TemplateBlock tempBlock, EntityPlayer entityplayer, double partialTicks) {

        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * partialTicks;
        BlockPos new_pos = selectedWorldPosition.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());

        //render the chest myself
        ModelBed model = new ModelBed();
        TileEntityBed te = new TileEntityBed();
        if(tempBlock.getTileEntity() != null)
            te = (TileEntityBed) tempBlock.getTileEntity();

        int metadata = tempBlock.getBlockState().getBlock().getMetaFromState(tempBlock.getBlockState());

        boolean flag = true;
        boolean flag1 = flag ? BlockBed.isHeadPiece(metadata) : true;
        EnumDyeColor enumdyecolor = te != null ? te.getColor() : EnumDyeColor.RED;
        int i = flag ? metadata & 3 : 0;

        ResourceLocation resourcelocation = TEXTURES[enumdyecolor.getMetadata()];

        if (resourcelocation != null)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(resourcelocation);
        }

        double x = new_pos.getX()-d0 ;
        double y = new_pos.getY()-d1 ;
        double z = new_pos.getZ()-d2 ;
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5379); //CRITICAL !!!!
        GlStateManager.enableColorMaterial();

        if (flag)
        {
            renderPiece(flag1, x, y, z, i, 1.0f,model);
        }
        else
        {
            GlStateManager.pushMatrix();
            renderPiece(true, x, y, z, i, 1.0f,model);
            renderPiece(false, x, y, z - 1.0D, i, 1.0f,model);
            GlStateManager.popMatrix();
        }
        GlStateManager.disableColorLogic();
        GlStateManager.disableColorMaterial();
        return new_pos;
    }

    private static void renderPiece(boolean p_193847_1_, double x, double y, double z, int p_193847_8_, float alpha, ModelBed model)
    {
        model.preparePiece(p_193847_1_);
        GlStateManager.pushMatrix();
        {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;

            if (p_193847_8_ == EnumFacing.NORTH.getHorizontalIndex())
                f = 0.0F;
            else if (p_193847_8_ == EnumFacing.SOUTH.getHorizontalIndex())
            {
                f = 180.0F;
                f1 = 1.0F;
                f2 = 1.0F;
            }
            else if (p_193847_8_ == EnumFacing.WEST.getHorizontalIndex())
            {
                f = -90.0F;
                f2 = 1.0F;
            }
            else if (p_193847_8_ == EnumFacing.EAST.getHorizontalIndex())
            {
                f = 90.0F;
                f1 = 1.0F;
            }

            GlStateManager.translate((float) x + f1, (float) y + 0.5625F, (float) z + f2);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f, 0.0F, 0.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            model.render();
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.popMatrix();
    }

    static
    {
        EnumDyeColor[] aenumdyecolor = EnumDyeColor.values();
        TEXTURES = new ResourceLocation[aenumdyecolor.length];

        for (EnumDyeColor enumdyecolor : aenumdyecolor)
        {
            TEXTURES[enumdyecolor.getMetadata()] = new ResourceLocation("textures/entity/bed/" + enumdyecolor.getDyeColorName() + ".png");
        }
    }

    public static void drawSelectionBoundingBox(BlockPos startPos, BlockPos endPos, Vec3d hitvec , Minecraft mc, EntityPlayer player, boolean highlightSides, double partialTicks, float red, float green, float blue, float alpha) {
        RenderGlobal renderglobal = mc.renderGlobal;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        AxisAlignedBB selectionBox = new AxisAlignedBB(startPos, endPos);
        renderglobal.drawSelectionBoundingBox(selectionBox.grow(0.0020000000949949026D).offset(-d0, -d1, -d2), red, green, blue, alpha);

        if(highlightSides)
        {
            AxisAlignedBB box= selectionBox.grow(0.0020000000949949026D);
            if(BuildTemplateMode.SELECTED_SIDE == null)
            {
                RayTraceResult raytraceresult = selectionBox.grow(0.0020000000949949026D).calculateIntercept(ModEntity.currCursorVec.start, ModEntity.currCursorVec.end);

                if(raytraceresult != null )
                {
                    //when the first side hit is different from the previous reset the altpresses functionality
                    if(raytraceresult.sideHit == BuildTemplateMode.PREV_FIRST_SIDEHIT) {
                        if (BuildTemplateMode.ALT_PRESSES == 0) {
                            renderHighlightedSide(raytraceresult.sideHit, box, (float) partialTicks);
                            BuildTemplateMode.CURR_SIDEHIT = raytraceresult.sideHit;
                            BuildTemplateMode.CURR_HITVEC = raytraceresult.hitVec;
                        } else {
                            RayTraceResult raytraceresult1 = selectionBox.grow(0.0020000000949949026D).calculateIntercept(raytraceresult.hitVec.add(ModEntity.currMouseVec.normalize()), ModEntity.currCursorVec.end);
                            if (raytraceresult1 != null) {
                                if (BuildTemplateMode.ALT_PRESSES == 1)
                                {
                                    renderHighlightedSide(raytraceresult1.sideHit, box, (float) partialTicks);
                                    BuildTemplateMode.CURR_SIDEHIT = raytraceresult1.sideHit;
                                    BuildTemplateMode.CURR_HITVEC = raytraceresult1.hitVec;
                                }
                                else
                                {
                                    BuildTemplateMode.ALT_PRESSES = 0;
                                    renderHighlightedSide(raytraceresult.sideHit, box, (float) partialTicks);
                                    BuildTemplateMode.CURR_SIDEHIT = raytraceresult.sideHit;
                                    BuildTemplateMode.CURR_HITVEC = raytraceresult.hitVec;
                                }
                            } else {
                                BuildTemplateMode.ALT_PRESSES = 0;
                                renderHighlightedSide(raytraceresult.sideHit, box, (float) partialTicks);
                                BuildTemplateMode.CURR_SIDEHIT = raytraceresult.sideHit;
                                BuildTemplateMode.CURR_HITVEC = raytraceresult.hitVec;
                            }


                        }
                    }
                    else
                    {
                        renderHighlightedSide(raytraceresult.sideHit, box, (float) partialTicks);
                        BuildTemplateMode.CURR_SIDEHIT = raytraceresult.sideHit;
                        BuildTemplateMode.CURR_HITVEC = raytraceresult.hitVec;
                        BuildTemplateMode.ALT_PRESSES = 0;
                        BuildTemplateMode.PREV_FIRST_SIDEHIT = raytraceresult.sideHit;
                    }

                }
                else
                {
                    BuildTemplateMode.ALT_PRESSES = 0;
                    BuildTemplateMode.PREV_FIRST_SIDEHIT = null;
                    BuildTemplateMode.CURR_SIDEHIT = null;
                    BuildTemplateMode.CURR_HITVEC = null;
                }

                //reset the alt key is it is nolong pressed, not good here but it is convenient
                if(!KeyInputHandler.getAltKey().isDown())
                    KeyInputHandler.alterToolMode = false;
            }
            else
            {
                renderHighlightedSide(BuildTemplateMode.SELECTED_SIDE, box, (float) partialTicks);
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

    }

    private static void renderHighlightedSide(EnumFacing sideHit, AxisAlignedBB box, float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        if(sideHit == EnumFacing.EAST)
            RenderPlaneSurface.renderHighlightZY(Minecraft.getMinecraft(),new Vec3d(box.maxX, box.minY, box.minZ),new Vec3d(box.maxX, box.maxY, box.maxZ), partialTicks,0.0f,1.0f,0.0f,0.3f);
        else if(sideHit == EnumFacing.WEST)
            RenderPlaneSurface.renderHighlightZY(Minecraft.getMinecraft(),new Vec3d(box.minX, box.minY, box.minZ),new Vec3d(box.minX, box.maxY, box.maxZ), partialTicks,0.0f,1.0f,0.0f,0.3f);
        else if(sideHit == EnumFacing.UP)
            RenderPlaneSurface.renderHighlightXZ(Minecraft.getMinecraft(),new Vec3d(box.minX, box.maxY, box.minZ),new Vec3d(box.maxX, box.maxY, box.maxZ), partialTicks,0.0f,0.0f,1.0f,0.3f);
        else if(sideHit == EnumFacing.DOWN)
            RenderPlaneSurface.renderHighlightXZ(Minecraft.getMinecraft(),new Vec3d(box.minX, box.minY, box.minZ),new Vec3d(box.maxX, box.minY, box.maxZ), partialTicks,0.0f,0.0f,1.0f,0.3f);
        else if(sideHit == EnumFacing.NORTH)
            RenderPlaneSurface.renderHighlightXY(Minecraft.getMinecraft(),new Vec3d(box.minX, box.minY, box.minZ),new Vec3d(box.maxX, box.maxY, box.minZ), partialTicks,1.0f,0.0f,0.0f,0.3f);
        else if(sideHit == EnumFacing.SOUTH)
            RenderPlaneSurface.renderHighlightXY(Minecraft.getMinecraft(),new Vec3d(box.minX, box.minY, box.maxZ),new Vec3d(box.maxX, box.maxY, box.maxZ), partialTicks,1.0f,0.0f,0.0f,0.3f);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableLighting();
    }

    private static  BlockPos renderShulkerBox(BlockPos selectedWorldPosition, TemplateBlock tempBlock, EntityPlayer entityplayer, double partialTicks)
    {
        EnumFacing enumfacing = EnumFacing.UP;


        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * partialTicks;
        BlockPos new_pos = selectedWorldPosition.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());

        IBlockState iblockstate = tempBlock.getBlockState();
        BlockShulkerBox block = (BlockShulkerBox) iblockstate.getBlock();
        TileEntityShulkerBox tileEntity = (TileEntityShulkerBox) block.createNewTileEntity(entityplayer.world,block.getMetaFromState(iblockstate));

        enumfacing = (EnumFacing)iblockstate.getValue(BlockShulkerBox.FACING);

        //taken from RenderItem.renderItemModel
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);


        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();



        Minecraft.getMinecraft().renderEngine.bindTexture(RenderShulker.SHULKER_ENDERGOLEM_TEXTURE[tileEntity.getColor().getMetadata()]);


        GlStateManager.pushMatrix();


        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5379); //CRITICAL !!!!
        GlStateManager.enableColorMaterial();

        GlStateManager.enableRescaleNormal();
        RenderHelper.enableStandardItemLighting();
        int a = 15728832;
        int b = a % 65536;
        int c = a / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)b, (float)c);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);

        GlStateManager.translate((float) new_pos.getX()  + 0.5F - d0, (float) new_pos.getY() - d1 + 1.5F, (float) new_pos.getZ() - d2 + 0.5F);
//        GlStateManager.translate((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.0F, 1.0F, 0.0F);
        float f = 0.9995F;
        GlStateManager.scale(0.9995F, 0.9995F, 0.9995F);
        GlStateManager.translate(0.0F, -1.0F, 0.0F);

        switch (enumfacing)
        {
            case DOWN:
                GlStateManager.translate(0.0F, 2.0F, 0.0F);
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            case UP:
            default:
                break;
            case NORTH:
                GlStateManager.translate(0.0F, 1.0F, 1.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                break;
            case SOUTH:
                GlStateManager.translate(0.0F, 1.0F, -1.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.translate(-1.0F, 1.0F, 0.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case EAST:
                GlStateManager.translate(1.0F, 1.0F, 0.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        }


        ModelShulker modelShulker = new ModelShulker();
        modelShulker.base.render(0.0625F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(270.0F * 0.0F, 0.0F, 1.0F, 0.0F);
        modelShulker.lid.render(0.0625F);
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableColorLogic();
        GlStateManager.disableColorMaterial();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        return new_pos;
    }


    public static BlockPos renderSkull(BlockPos selectedWorldPosition, TemplateBlock tempBlock, EntityPlayer entityplayer, double partialTicks, EnumFacing hitSide)
    {
        ModelBase modelbase = new ModelSkeletonHead(0, 0, 64, 32);
        Minecraft mc = Minecraft.getMinecraft();
        TileEntityRendererDispatcher.instance.prepare(mc.world, mc.getTextureManager(), mc.fontRenderer, mc.getRenderViewEntity(), mc.objectMouseOver, (float) partialTicks);
        mc.getRenderManager().cacheActiveRenderInfo(mc.world, mc.fontRenderer, mc.getRenderViewEntity(), mc.pointedEntity, mc.gameSettings, (float) partialTicks);


        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * partialTicks;
        BlockPos new_pos = selectedWorldPosition.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());

        IBlockState iblockstate = tempBlock.getBlockState();
        BlockSkull block = (BlockSkull) iblockstate.getBlock();
//        TileEntitySkull tileEntity = (TileEntitySkull) block.createNewTileEntity(entityplayer.world,block.getMetaFromState(iblockstate));
        TileEntitySkull tileEntity = (TileEntitySkull) tempBlock.getTileEntity();

        if(tileEntity != null)
        {
            GameProfile profile = tileEntity.getPlayerProfile();



            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.enableColorLogic();
            GlStateManager.enableColorMaterial();
            GlStateManager.colorLogicOp(5379); //CRITICAL !!!!

            RenderHelper.enableStandardItemLighting();
//        int a = entityplayer.world.getCombinedLight(te.getPos(), 0);
            int a = 15728832;
            int b = a % 65536;
            int c = a / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)b, (float)c);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);


            float rotationIn = (float)(tileEntity.getSkullRotation() * 360) / 16.0F;

            int skullType = tileEntity.getSkullType();
            switch (skullType)
            {
                case 0:
                default:
                    Minecraft.getMinecraft().renderEngine.bindTexture(SKELETON_TEXTURES);
                    break;
                case 1:
                    Minecraft.getMinecraft().renderEngine.bindTexture(WITHER_SKELETON_TEXTURES);
                    break;
                case 2:
                    Minecraft.getMinecraft().renderEngine.bindTexture(ZOMBIE_TEXTURES);
                    modelbase = new ModelHumanoidHead();
                    break;
                case 3:
                    modelbase = new ModelHumanoidHead();
                    ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();

                    if (profile != null)
                    {
                        Minecraft minecraft = Minecraft.getMinecraft();
                        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

                        if (map.containsKey(MinecraftProfileTexture.Type.SKIN))
                        {
                            resourcelocation = minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                        }
                        else
                        {
                            UUID uuid = EntityPlayer.getUUID(profile);
                            resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
                        }
                    }

                    Minecraft.getMinecraft().renderEngine.bindTexture(resourcelocation);
                    break;
                case 4:
                    Minecraft.getMinecraft().renderEngine.bindTexture(CREEPER_TEXTURES);
                    break;
                case 5:
                    Minecraft.getMinecraft().renderEngine.bindTexture(DRAGON_TEXTURES);
                    modelbase = new ModelDragonHead(0.0F);
            }


            GlStateManager.pushMatrix();
            GlStateManager.disableCull();

            float x = (float) (new_pos.getX() - d0);
            float y =  (float) (new_pos.getY() - d1);
            float z =  (float) (new_pos.getZ() - d2);

            if (tempBlock.getFace() == EnumFacing.UP)
            {
                GlStateManager.translate(x + 0.5F, y, z + 0.5F);
            }
            else
            {
                EnumFacing facingSide = tempBlock.getFace();
                for(IProperty property: iblockstate.getPropertyKeys())
                {
                    if (property instanceof PropertyDirection)
                    {
                        facingSide = (EnumFacing) iblockstate.getValue(property);
                    }
                }

                switch (facingSide)
                {
                    case NORTH:
                        GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.74F);
                        rotationIn = 0.0F;
                        break;
                    case SOUTH:
                        GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.26F);
                        rotationIn = 180.0F;
                        break;
                    case WEST:
                        GlStateManager.translate(x + 0.74F, y + 0.25F, z + 0.5F);
                        rotationIn = 270.0F;
                        break;
                    case EAST:
                    default:
                        GlStateManager.translate(x + 0.26F, y + 0.25F, z + 0.5F);
                        rotationIn = 90.0F;
                }
            }

            float f = 0.0625F;
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            GlStateManager.enableAlpha();



            if (skullType == 3)
            {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            }

            modelbase.render((Entity)null, 0.0f, 0.0F, 0.0F, rotationIn, 0.0F, 0.0625F);
//        GlStateManager.enableCull();
//        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            GlStateManager.disableBlend();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableColorLogic();
            GlStateManager.disableColorMaterial();
            RenderHelper.disableStandardItemLighting();

            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        return new_pos;
    }

    public static BlockPos renderBanner(BlockPos selectedWorldPosition, TemplateBlock tempBlock, EntityPlayer entityplayer, double partialTicks, EnumFacing hitSide)
    {
        ModelBanner bannerModel = new ModelBanner();
        Minecraft mc = Minecraft.getMinecraft();

        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * partialTicks;
        BlockPos new_pos = selectedWorldPosition.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());

        TileEntityBanner te = (TileEntityBanner) tempBlock.getTileEntity();

        boolean flag = te != null;
//        boolean flag1 =  te.getBlockType() == Blocks.STANDING_BANNER;
        boolean flag1 =  tempBlock.getBlockState().getBlock() instanceof BlockBanner.BlockBannerStanding;
        int i = flag ? tempBlock.getBlockState().getBlock().getMetaFromState(tempBlock.getBlockState()) : 0;
        long j = 646055L;
        GlStateManager.pushMatrix();
        float f = 0.6666667F;

        float x = (float) (new_pos.getX() - d0);
        float y =  (float) (new_pos.getY() - d1);
        float z =  (float) (new_pos.getZ() - d2);

        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableColorLogic();
        GlStateManager.enableColorMaterial();
        GlStateManager.colorLogicOp(5379); //CRITICAL !!!!

        RenderHelper.enableStandardItemLighting();
//        int a = entityplayer.world.getCombinedLight(te.getPos(), 0);
        int a = 15728832;
        int b = a % 65536;
        int c = a / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)b, (float)c);

        if (flag1)
        {
            GlStateManager.translate((float)x + (0.5F), (float)y + (0.5F), (float)z + (0.5F));
//            GlStateManager.translate((float)x , (float)y + (0.5F*f), (float)z );
//            GlStateManager.translate((float)x, (float)y + 0.8F, (float)z);
//            GlStateManager.translate((float)x, (float)y , (float)z );
            int rot = tempBlock.getBlockState().getValue(BlockBanner.ROTATION);
            if(rot != 0)
                rot = 16-rot;
            float f1 = (float)( rot* 360) / 16.0F;
            GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
            bannerModel.bannerStand.showModel = true;
        }
        else
        {
            float f2 = 0.0F;

            if (i == 2)
            {
                f2 = 180.0F;
            }

            if (i == 4)
            {
                f2 = 90.0F;
            }

            if (i == 5)
            {
                f2 = -90.0F;
            }

            GlStateManager.translate((float)x + (0.5F*f), (float)y - (0.16666667F*f), (float)z + (0.5F*f));
            GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
            bannerModel.bannerStand.showModel = false;
        }

        BlockPos blockpos = te.getPos();
//        float f3 = (float)((blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + (float)j + partialTicks);
//        bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f3 * (float)Math.PI * 0.02F)) * (float)Math.PI;
        GlStateManager.enableRescaleNormal();
        ResourceLocation resourcelocation = getBannerResourceLocation(te);

        if (resourcelocation != null)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(resourcelocation);
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
            bannerModel.renderBanner();
            GlStateManager.popMatrix();
        }
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableColorLogic();
        GlStateManager.disableColorMaterial();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        return new_pos;
    }

    public static BlockPos renderRedstoneWire(BlockPos selectedWorldPosition, TemplateBlock tempBlock, EntityPlayer entityplayer, double partialTicks, EnumFacing hitSide)
    {
        ModelBanner bannerModel = new ModelBanner();
        Minecraft mc = Minecraft.getMinecraft();

        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * partialTicks;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * partialTicks;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * partialTicks;
        BlockPos new_pos = selectedWorldPosition.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());

        TileEntityBanner te = (TileEntityBanner) tempBlock.getTileEntity();

        boolean flag = te != null;
//        boolean flag1 =  te.getBlockType() == Blocks.STANDING_BANNER;
        boolean flag1 =  tempBlock.getBlockState().getBlock() instanceof BlockBanner.BlockBannerStanding;
        int i = flag ? tempBlock.getBlockState().getBlock().getMetaFromState(tempBlock.getBlockState()) : 0;
        long j = 646055L;
        GlStateManager.pushMatrix();
        float f = 0.6666667F;

        float x = (float) (new_pos.getX() - d0);
        float y =  (float) (new_pos.getY() - d1);
        float z =  (float) (new_pos.getZ() - d2);

        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableColorLogic();
        GlStateManager.enableColorMaterial();
        GlStateManager.colorLogicOp(5379); //CRITICAL !!!!

        RenderHelper.enableStandardItemLighting();
//        int a = entityplayer.world.getCombinedLight(te.getPos(), 0);
        int a = 15728832;
        int b = a % 65536;
        int c = a / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)b, (float)c);

        if (flag1)
        {
            GlStateManager.translate((float)x + (0.5F), (float)y + (0.5F), (float)z + (0.5F));
//            GlStateManager.translate((float)x , (float)y + (0.5F*f), (float)z );
//            GlStateManager.translate((float)x, (float)y + 0.8F, (float)z);
//            GlStateManager.translate((float)x, (float)y , (float)z );
            int rot = tempBlock.getBlockState().getValue(BlockBanner.ROTATION);
            if(rot != 0)
                rot = 16-rot;
            float f1 = (float)( rot* 360) / 16.0F;
            GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
            bannerModel.bannerStand.showModel = true;
        }
        else
        {
            float f2 = 0.0F;

            if (i == 2)
            {
                f2 = 180.0F;
            }

            if (i == 4)
            {
                f2 = 90.0F;
            }

            if (i == 5)
            {
                f2 = -90.0F;
            }

            GlStateManager.translate((float)x + (0.5F*f), (float)y - (0.16666667F*f), (float)z + (0.5F*f));
            GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
            bannerModel.bannerStand.showModel = false;
        }

        BlockPos blockpos = te.getPos();
//        float f3 = (float)((blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + (float)j + partialTicks);
//        bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f3 * (float)Math.PI * 0.02F)) * (float)Math.PI;
        GlStateManager.enableRescaleNormal();
        ResourceLocation resourcelocation = getBannerResourceLocation(te);

        if (resourcelocation != null)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(resourcelocation);
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
            bannerModel.renderBanner();
            GlStateManager.popMatrix();
        }
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableColorLogic();
        GlStateManager.disableColorMaterial();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        return new_pos;
    }

    private static ResourceLocation getBannerResourceLocation(TileEntityBanner bannerObj)
    {
        return BannerTextures.BANNER_DESIGNS.getResourceLocation(bannerObj.getPatternResourceLocation(), bannerObj.getPatternList(), bannerObj.getColorList());
    }


    public static void  drawBlockOutline(EntityPlayer entityplayer, Float partialTicks, BlockPos normalHitPos, float r, float g, float b)
    {
        RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK,  Vec3d.ZERO, EnumFacing.NORTH, normalHitPos);
        RenderTemplate.drawBlockSelectionBox(entityplayer, new_position, 0, partialTicks,  r, g,b);
    }

    public static void  drawBlockOutline(EntityPlayer entityplayer, Float partialTicks, BlockPos normalHitPos, float r, float g, float b,float alpha)
    {
        RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK,  Vec3d.ZERO, EnumFacing.NORTH, normalHitPos);
        RenderTemplate.drawBlockSelectionBox(entityplayer, new_position, 0, partialTicks,  r, g,b,alpha);
    }
}


