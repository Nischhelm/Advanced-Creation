package com.deadtiger.advcreation.build_template;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.client.render.RenderBlockHighlighting;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateManager;
import com.deadtiger.advcreation.utility.CursorVector;
import com.deadtiger.advcreation.utility.PlacementHelper;
import com.deadtiger.advcreation.utility.VecTransformer;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;

public class BuildTemplateMode
{
    public static BlockPos START_POS = null; //The actual starting click
    public static BlockPos END_POS = null;   //The actual ending click
    public static BlockPos TECHNICAL_START_POS = null; //the smallest of START_POS or END_POS
    public static BlockPos SIZE = null;
    public static BlockPos NEW_TECHNICAL_START_POS = null; // The new smallest POS after adjusting the selection
    public static BlockPos NEW_SIZE = null; //The new size after adjusting the selection

    public static BlockPos NEW_START_POS = null; // only for drawing the indication block outline
    public static BlockPos NEW_END_POS = null; // only for drawing the indication block outline

    public static String TEMPLATE_NAME = null;// template name without .nbt
    public static String FUNCTION = null;
    public static String CATEGORY = null;
    public static String STYLE = null;

    //processing of the alt-pressing to change the selected side of the selectionbox
    public static int ALT_PRESSES = 0;
    public static EnumFacing PREV_FIRST_SIDEHIT = null;
    public static EnumFacing CURR_SIDEHIT = null;
    public static Vec3d CURR_HITVEC = null;
    public static Vec3d SELECTED_VEC = null;
    public static EnumFacing SELECTED_SIDE = null;


    public static BlockPos PREV_POS = null;
    public static Vec3d PREV_HITVEC = null;
    public static CursorVector PREV_CURSOR_VEC = null;
    public static int PREV_X_OFFSET = 0;
    public static int PREV_Z_OFFSET = 0;
    public static boolean HELD_ALT = false;

    //template property options
    public static ArrayList<String> STYLES = new ArrayList<>(Arrays.asList(
            "futuristic", "cyberpunk", "steampunk", "arabic", "medieval", "eastern", "modern", "17th_century", "miscellaneous", "none", "greek", "roman", "gothic"));
    public static String STYLE_TIP = "What sort of architecture style does the template fit in? Can be based on time period, location or architectural classification";
    public static ArrayList<String> CATEGORIES = new ArrayList<>(Arrays.asList(
            "structure", "wall", "room", "door", "gate",
            "defensive_wall", "window", "balcony", "floor", "hallway", "tower", "roof", "miscellaneous", "section", "none", "street"));
    public static String CATEGORY_TIP = "Is this a structure on its own or is it intended as a specific part of a bigger structure?";
    public static ArrayList<String> FUNCTIONS = new ArrayList<>(Arrays.asList(
            "fortress", "house", "castle", "workshop",
            "windmill", "watermill", "factory", "skyscraper", "tower", "defensive_wall", "tree_hut", "street", "mineshaft",
            "sculpture", "miscellaneous", "bakery", "blacksmith", "palace", "mill", "mine", "baracks", "none", "any", "chapel", "church"));
    public static String FUNCTION_TIP = "What is the intended function of the structure or the structure it belongs to";


    public static int MOUSE_X_OFFSET = 0;
    public static int MOUSE_Y_OFFSET = 0;
    public static int MOUSE_Z_OFFSET = 0;

    private static BlockPos NORMAL_POS = null;

    public static TemplateBuildingMode MODE = TemplateBuildingMode.SELECT_START_POS;

    public static void setTemplateName(String name)
    {
        TEMPLATE_NAME = name;
    }

    public static void setStartPos(BlockPos pos)
    {
        if (pos == null)
        {
            START_POS = null;
            NEW_START_POS =null;
            return;
        }
        START_POS = new BlockPos(pos);
        NEW_START_POS = START_POS;

    }

    public static void setEndPos(BlockPos pos)
    {
        if (pos == null)
        {
            END_POS = null;
            NEW_END_POS = null;
            return;
        }
        END_POS = new BlockPos(pos);
        NEW_END_POS = END_POS;
    }


    public static void selectStartPosition(BlockPos pos)
    {
        setStartPos(pos);
        MODE = TemplateBuildingMode.SELECT_END_POS;
    }

    public static void selectEndPosition(BlockPos pos)
    {
        setEndPos(pos);
        MODE = TemplateBuildingMode.MAKE_ADJUSTMENTS;
    }

    public static void finishSaveTemplate()
    {
        if (MODE == TemplateBuildingMode.MAKE_ADJUSTMENTS && END_POS != null && START_POS != null)
        {
            Template template = createNewTemplate();
            template.setSize(SIZE);
            template.setName(TEMPLATE_NAME);
            template.setFunction(FUNCTION);
            template.setCategory(CATEGORY);
            template.setStyle(STYLE);
            template.setAuthor(Minecraft.getMinecraft().player.getName());
            template.setMcVersion(Reference.MC_VERSION);

            template.tryCalculateProperties();
            template.save();
            TemplateManager.FILENAME_LIST.add(template.getFilename());
            TemplateManager.TEMPLATES_LIST.add(template);
            TemplateManager.addNewTemplateMcVersionFound(template.getMcVersion());

            cancelTemplate();
        }
    }

    public static Template finishTemplate()
    {
        if (MODE == TemplateBuildingMode.MAKE_ADJUSTMENTS && END_POS != null && START_POS != null)
        {
            Template template = createNewTemplate();
            return template;
        }
        return null;
    }

    public static void cancelTemplate()
    {
        setStartPos(null);
        setEndPos(null);
        setTemplateName(null);
        MODE = TemplateBuildingMode.SELECT_START_POS;
        SELECTED_SIDE = null;
        SELECTED_VEC = Vec3d.ZERO;
        TEMPLATE_NAME = null;
        STYLE = null;
        CATEGORY = null;
        FUNCTION = null;
        GuiOverlayManager.setStartPos(null);
        GuiOverlayManager.setEndPos(null);
        GuiOverlayManager.setBlockCount(1,1,1,1);
        BuildTemplateMode.clearMouseOffset();
    }

    public static void leftClick(BlockPos pos)
    {
        if (MODE == TemplateBuildingMode.SELECT_START_POS)
        {
            selectStartPosition(pos.add(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET));
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());
        }
        else if (MODE == TemplateBuildingMode.SELECT_END_POS && START_POS != null)
        {
            selectEndPosition(pos.add(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET));
            updateTechnicalStartPos(END_POS);
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());
        }
        else if (MODE == TemplateBuildingMode.MAKE_ADJUSTMENTS && END_POS != null)
        {
            if (SELECTED_SIDE == null)
            {
                SELECTED_SIDE = CURR_SIDEHIT;
                SELECTED_VEC = CURR_HITVEC;
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Adjusting Selection", false, false);
            }
            else
            {
                SELECTED_SIDE = null;
                SELECTED_VEC = Vec3d.ZERO;
                GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());
            }
        }
        BuildTemplateMode.clearMouseOffset();


    }

    public static Template createNewTemplate()
    {
        Template template = new Template(NEW_SIZE);
        ArrayList<BlockPos> blockList = getBlockPosList();

        for (BlockPos position : blockList)
        {
            addWorldBlockToTemplate(template, position);
        }
        template.setX_offset_template(-NEW_SIZE.getX() / 2);
        template.setZ_offset_template(-NEW_SIZE.getZ() / 2);
        return template;

    }

    private static void addWorldBlockToTemplate(Template template, BlockPos position)
    {
        IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(position);
        TileEntity tileEntity = Minecraft.getMinecraft().world.getTileEntity(position);
        int u = position.getX() - NEW_TECHNICAL_START_POS.getX();
        int v = position.getY() - NEW_TECHNICAL_START_POS.getY();
        int w = position.getZ() - NEW_TECHNICAL_START_POS.getZ();


        EnumFacing side = EnumFacing.NORTH;
        if(blockState.getBlock() instanceof BlockSkull)
        {
            side = blockState.getValue(BlockSkull.FACING);
        }

        template.addBlock(side, u, v, w, blockState,tileEntity);
    }

    public static ArrayList<BlockPos> getPreviewBlockPosList(BlockPos pos)
    {
        ArrayList<BlockPos> newList = new ArrayList<>();
        if (MODE == TemplateBuildingMode.SELECT_START_POS)
        {
            //if mode is idle just return a list with the currently selected pos
            newList.add(pos);
            return newList;
        }
        else if (MODE == TemplateBuildingMode.SELECT_END_POS && START_POS != null)
        {
            //if the startposition is selected return a list from startposition to the current selected position
            return getBlockPosList();
        }
        else if (MODE == TemplateBuildingMode.MAKE_ADJUSTMENTS && END_POS != null)
        {
            //if the end position in selected return a list from the start position to end position held in this class
            return getBlockPosList();
        }
        return newList;
    }

    public static ArrayList<BlockPos> getBlockPosList()
    {
        ArrayList<BlockPos> res = new ArrayList<>();
        if (NEW_SIZE != null)
        {
            for (int u = 0; u < NEW_SIZE.getX(); u++)
            {
                //the ZY size
                for (int v = 0; v < NEW_SIZE.getY(); v++)
                {
                    //the XZ size
                    for (int w = 0; w < NEW_SIZE.getZ(); w++)
                    {
                        BlockPos position = NEW_TECHNICAL_START_POS.add(u, v, w);
                        res.add(position);
                    }
                }
            }
        }
        return res;
    }

    public static void updateTechnicalStartPos(BlockPos newEndPos)
    {
        int X_diff = newEndPos.getX() - START_POS.getX();
        int X_start = get_start(X_diff, START_POS.getX(), newEndPos.getX());
        int Y_diff = newEndPos.getY() - START_POS.getY();
        int Y_start = get_start(Y_diff, START_POS.getY(), newEndPos.getY());
        int Z_diff = newEndPos.getZ() - START_POS.getZ();
        int Z_start = get_start(Z_diff, START_POS.getZ(), newEndPos.getZ());

        //the blockpos combining the lowest position of start and end point
        TECHNICAL_START_POS = new BlockPos(X_start, Y_start, Z_start);
        SIZE = new BlockPos(Math.abs(X_diff) + 1, Math.abs(Y_diff) + 1, Math.abs(Z_diff) + 1);
        NEW_TECHNICAL_START_POS = TECHNICAL_START_POS;
        NEW_SIZE = SIZE;
    }

    public static void updateSelection(Minecraft mc, Vec3d hitVec)
    {
        if (SELECTED_SIDE != null && SELECTED_VEC != null && hitVec != null)
        {
            Vec3d cameraUnitVec = IsometricCamera.CAMERA_LOOK_VECTOR.scale(-1.0);
            if (cameraUnitVec != null)
            {
                if (SELECTED_SIDE == EnumFacing.EAST)
                {
                    Vec3d newVec = VecTransformer.transformXZorXYBasedOnCamera(hitVec, cameraUnitVec, ModEntity.currCursorVec);
                    if (newVec != null)
                    {
                        int toadd = (int) (newVec.x - SELECTED_VEC.x);
                        if ((-toadd) > SIZE.getX())
                            toadd = -(SIZE.getX() - 1);

                        NEW_SIZE = SIZE.add(toadd, 0, 0);
                        if(START_POS.getX() < END_POS.getX())
                            NEW_END_POS = END_POS.add(toadd,0,0);
                        else
                            NEW_START_POS = START_POS.add(toadd,0,0);
                    }
                }
                else if (SELECTED_SIDE == EnumFacing.WEST)
                {

                    Vec3d newVec = VecTransformer.transformXZorXYBasedOnCamera(hitVec, cameraUnitVec, ModEntity.currCursorVec);
                    if (newVec != null)
                    {
                        int toadd = (int) (newVec.x - SELECTED_VEC.x);
                        if ((toadd) > SIZE.getX())
                            toadd = (SIZE.getX() - 1);

                        NEW_TECHNICAL_START_POS = TECHNICAL_START_POS.add(toadd, 0, 0);
                        NEW_SIZE = SIZE.add(-toadd, 0, 0);
                        if(START_POS.getX() < END_POS.getX())
                            NEW_START_POS = START_POS.add(toadd,0,0);
                        else
                            NEW_END_POS = END_POS.add(toadd,0,0);
                    }
                }
                else if (SELECTED_SIDE == EnumFacing.UP)
                {

                    Vec3d newVec = VecTransformer.transformXYOrZYBasedOnCamera(hitVec, cameraUnitVec, ModEntity.currCursorVec);
                    if (newVec != null)
                    {
                        int toadd = (int) (newVec.y - SELECTED_VEC.y);
                        if ((-toadd) > SIZE.getY())
                            toadd = -(SIZE.getY() - 1);

                        NEW_SIZE = SIZE.add(0, toadd, 0);
                        if(START_POS.getY() < END_POS.getY())
                            NEW_END_POS = END_POS.add(0,toadd,0);
                        else
                            NEW_START_POS = START_POS.add(0,toadd,0);
                    }
                }
                else if (SELECTED_SIDE == EnumFacing.DOWN)
                {
                    Vec3d newVec = VecTransformer.transformXYOrZYBasedOnCamera(hitVec, cameraUnitVec, ModEntity.currCursorVec);
                    if (newVec != null)
                    {
                        //limit how small you can make the thing
                        int toadd = (int) (newVec.y - SELECTED_VEC.y);
                        if ((toadd) > SIZE.getY())
                            toadd = (SIZE.getY() - 1);

                        NEW_TECHNICAL_START_POS = TECHNICAL_START_POS.add(0, toadd, 0);
                        NEW_SIZE = SIZE.add(0, -toadd, 0);

                        if(START_POS.getY() < END_POS.getY())
                            NEW_START_POS = START_POS.add(0,toadd,0);
                        else
                            NEW_END_POS = END_POS.add(0,toadd,0);
                    }
                }
                else if (SELECTED_SIDE == EnumFacing.NORTH)
                {
                    Vec3d newVec = VecTransformer.transformXZorZYBasedOnCamera(hitVec, cameraUnitVec, ModEntity.currCursorVec);
                    if (newVec != null)
                    {
                        int toadd = (int) (newVec.z - SELECTED_VEC.z);
                        if ((toadd) > SIZE.getZ())
                            toadd = (SIZE.getZ() - 1);

                        NEW_TECHNICAL_START_POS = TECHNICAL_START_POS.add(0, 0, toadd);
                        NEW_SIZE = SIZE.add(0, 0, -toadd);
                        if(START_POS.getZ() < END_POS.getZ())
                            NEW_START_POS = START_POS.add(0,0,toadd);
                        else
                            NEW_END_POS = END_POS.add(0,0,toadd);
                    }
                }
                else if (SELECTED_SIDE == EnumFacing.SOUTH)
                {
                    Vec3d newVec = VecTransformer.transformXZorZYBasedOnCamera(hitVec, cameraUnitVec, ModEntity.currCursorVec);
                    if (newVec != null)
                    {
                        int toadd = (int) (newVec.z - SELECTED_VEC.z);
                        if ((-toadd) > SIZE.getZ())
                           toadd = -(SIZE.getZ() - 1);

                        NEW_SIZE = SIZE.add(0, 0, toadd);
                        if(START_POS.getZ() < END_POS.getZ())
                            NEW_END_POS = END_POS.add(0,0,toadd);
                        else
                            NEW_START_POS = START_POS.add(0,0,toadd);
                    }
                }
            }
        }
        else
        {
            TECHNICAL_START_POS = NEW_TECHNICAL_START_POS;
            SIZE = NEW_SIZE;
            END_POS = NEW_END_POS;
            START_POS = NEW_START_POS;

        }

        //count the amount of solid blocks
        ArrayList<BlockPos> blockList = getBlockPosList();
        updateCountDisplay(blockList);
        GuiOverlayManager.setEndPos(NEW_END_POS);
        GuiOverlayManager.setStartPos(NEW_START_POS);

    }

    public static void updateCountDisplay(ArrayList<BlockPos> blockList)
    {
        int blockCount = 0;
        for (BlockPos position : blockList)
        {
            IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(position);
            if (!PlacementHelper.isNotGroundMaterial(blockState))
                blockCount++;
        }

        if (NEW_SIZE != null)
//            GuiOverlayManager.setBlockCount(NEW_SIZE.getX(), NEW_SIZE.getY(), NEW_SIZE.getZ(), blockCount);
            GuiOverlayManager.setBlockCount(NEW_SIZE.getX(), NEW_SIZE.getY(), NEW_SIZE.getZ(), blockCount,START_POS,null,END_POS);
        else
            GuiOverlayManager.setBlockCount(0, 0, 0, blockCount,START_POS,null,END_POS);
//            GuiOverlayManager.setBlockCount(0, 0, 0, blockCount);
    }

    public static void drawHighlighting(Minecraft mc, BlockPos newEndPos, float partialTick)
    {
        if (MODE == TemplateBuildingMode.SELECT_START_POS)
        {
            //if mode is idle just return a list with the currently selected pos
        }
        else if (MODE == TemplateBuildingMode.SELECT_END_POS && START_POS != null)
        {
            //if the startposition is selected return a list from startposition to the current selected position
            updateTechnicalStartPos(newEndPos);
            //if the selected area is to big the not all of the blockhighlights are drawn to conserve computing power
            Boolean tobig = NEW_SIZE.getX() * NEW_SIZE.getY() * NEW_SIZE.getZ() > RenderBlockHighlighting.SIZE_LIMIT;
            float alpha = 0.05f;
            if (tobig)
                alpha = 0.5f;
            RenderBlockHighlighting. render(TECHNICAL_START_POS, TECHNICAL_START_POS.add(NEW_SIZE.getX() - 1, NEW_SIZE.getY() - 1, NEW_SIZE.getZ() - 1), tobig, alpha, mc, partialTick);
        }
        else if (MODE == TemplateBuildingMode.MAKE_ADJUSTMENTS && END_POS != null)
        {
            //if the end position in selected return a list from the start position to end position held in this class
            float alpha = 0.1f;
            Boolean tobig = NEW_SIZE.getX() * NEW_SIZE.getY() * NEW_SIZE.getZ() > RenderBlockHighlighting.SIZE_LIMIT;
            if (tobig)
                alpha = 0.5f;
            if (SELECTED_SIDE != null && CURR_SIDEHIT != null)
                alpha = 0.05f;
            RenderBlockHighlighting.render(NEW_TECHNICAL_START_POS, NEW_TECHNICAL_START_POS.add(NEW_SIZE.getX() - 1, NEW_SIZE.getY() - 1, NEW_SIZE.getZ() - 1), tobig, alpha, mc, partialTick);

        }
    }

    public static int drawSelectionBox(Minecraft mc, EntityPlayer player, BlockPos newEndPos, Vec3d hitvec, boolean highlightSides, float partialTicks)
    {

        BlockPos offsetNewEndPos = newEndPos.add(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET);
        if (MODE == TemplateBuildingMode.SELECT_START_POS)
        {
            //if mode is idle just return a list with the currently selected pos
            GuiOverlayManager.setStartPos(offsetNewEndPos);

        }
        else if (MODE == TemplateBuildingMode.SELECT_END_POS && START_POS != null)
        {
            GuiOverlayManager.setEndPos(offsetNewEndPos);
            //if the startposition is selected return a list from startposition to the current selected position
            updateTechnicalStartPos(offsetNewEndPos);
            RenderPreview.drawSelectionBoundingBox(TECHNICAL_START_POS, TECHNICAL_START_POS.add(NEW_SIZE.getX(), NEW_SIZE.getY(), NEW_SIZE.getZ()), hitvec, mc, player, false, partialTicks, 0.5F, 0.5F, 0.5F, 1.0F);
            AdvCreation.previewStartPos = TECHNICAL_START_POS;
            AdvCreation.previewEndPos = TECHNICAL_START_POS.add(NEW_SIZE.getX(), NEW_SIZE.getY(), NEW_SIZE.getZ());
        }
        else if (MODE == TemplateBuildingMode.MAKE_ADJUSTMENTS && END_POS != null)
        {
            //if the end position in selected return a list from the start position to end position held in this class
            RenderPreview.drawSelectionBoundingBox(NEW_TECHNICAL_START_POS, NEW_TECHNICAL_START_POS.add(NEW_SIZE.getX(), NEW_SIZE.getY(), NEW_SIZE.getZ()), hitvec, mc, player, highlightSides, partialTicks, 8.0f, 8.0f, 8.0f, 1.0F);
            AdvCreation.previewStartPos = NEW_TECHNICAL_START_POS;
            AdvCreation.previewEndPos = NEW_TECHNICAL_START_POS.add(NEW_SIZE.getX(), NEW_SIZE.getY(), NEW_SIZE.getZ());
        }


        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.enableColorLogic();

        int hashcode = 0;
        if(newEndPos != null)
        {
            RenderSelectionHighlight.drawFadingBlocksToPos(player, partialTicks,newEndPos, BuildTemplateMode.MOUSE_X_OFFSET,BuildTemplateMode.MOUSE_Y_OFFSET,BuildTemplateMode.MOUSE_Z_OFFSET);
            RenderSelectionHighlight.drawBlueBlockHighlight(player,offsetNewEndPos,partialTicks);
            if(MOUSE_X_OFFSET != 0 || MOUSE_Y_OFFSET != 0 || MOUSE_Z_OFFSET != 0)
                RenderSelectionHighlight.drawWhiteBlockHighlight(player,newEndPos,partialTicks);
            if(BuildTemplateMode.START_POS != null)
            {
                hashcode = AdvCreation.previewStartPos.hashCode() + AdvCreation.previewEndPos.hashCode();
                RenderSelectionHighlight.drawGreenBlockHighlight(player,partialTicks,BuildTemplateMode.NEW_START_POS);
                if(BuildTemplateMode.END_POS != null)
                    RenderSelectionHighlight.drawBlueBlockHighlight(player,BuildTemplateMode.NEW_END_POS,partialTicks);
            }


        }


        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogic();

        return hashcode;
    }

    public static void updateAlterModeInactive(BlockPos position, Vec3d hitVec)
    {
        BuildTemplateMode.setPrevPos(position);
        BuildTemplateMode.setPrevHitvec(hitVec);
        BuildTemplateMode.setPrevCursorVec(ModEntity.currCursorVec);
        BuildTemplateMode.setHeldAlt(false);
    }

    public static BlockPos getAlterModePosition(BlockPos position, Vec3d hitVec)
    {
        if (PREV_POS != null)
        {
            if (!HELD_ALT)
            {
                PREV_X_OFFSET = MOUSE_X_OFFSET;
                PREV_Z_OFFSET = MOUSE_Z_OFFSET;
                HELD_ALT = true;
            }

            Vec3d prevPosVec = new Vec3d(PREV_POS.getX() + 0.5, PREV_POS.getY() + 0.5, PREV_POS.getZ() + 0.5);

            Vec3d newPosVec2 = VecTransformer.transformHitVecToXZPlane(hitVec, prevPosVec, ModEntity.currCursorVec);
            BlockPos newPosition2 = new BlockPos(Math.floor(newPosVec2.x), Math.floor(newPosVec2.y), Math.floor(newPosVec2.z));

            Vec3d newPrevPosVec2 = VecTransformer.transformHitVecToXZPlane(PREV_HITVEC, prevPosVec, PREV_CURSOR_VEC);
            BlockPos newPrevPos2 = new BlockPos(Math.floor(newPrevPosVec2.x), Math.floor(newPrevPosVec2.y), Math.floor(newPrevPosVec2.z));

            MOUSE_X_OFFSET = (PREV_X_OFFSET + newPosition2.getX() - newPrevPos2.getX());
            MOUSE_Z_OFFSET = (PREV_Z_OFFSET + newPosition2.getZ() - newPrevPos2.getZ());

            GuiOverlayManager.setOffsetPos(new BlockPos(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET));

            return PREV_POS;
        }
        return position;

    }

    public static void setHeldAlt(boolean held)
    {
        HELD_ALT = held;
    }

    public static void setPrevPos(BlockPos pos)
    {
        PREV_POS = pos;
    }

    public static Vec3d getPrevHitvec()
    {
        return PREV_HITVEC;
    }

    public static void setPrevHitvec(Vec3d prevHitvec)
    {
        BuildTemplateMode.PREV_HITVEC = prevHitvec;
    }

    public static CursorVector getPrevCursorVec()
    {
        return PREV_CURSOR_VEC;
    }

    public static void setPrevCursorVec(CursorVector prevCursorVec)
    {
        BuildTemplateMode.PREV_CURSOR_VEC = prevCursorVec;
    }


    public static int get_start(int diff, int x, int x2)
    {
        int X_start = x;
        if (diff < 0)
        {
            X_start = x2;
        }
        return X_start;
    }

    public static String getFUNCTION()
    {
        return FUNCTION;
    }

    public static void setFUNCTION(String FUNCTION)
    {
        BuildTemplateMode.FUNCTION = FUNCTION;
    }

    public static String getCATEGORY()
    {
        return CATEGORY;
    }

    public static void setCATEGORY(String CATEGORY)
    {
        BuildTemplateMode.CATEGORY = CATEGORY;
    }

    public static String getSTYLE()
    {
        return STYLE;
    }

    public static void setSTYLE(String STYLE)
    {
        BuildTemplateMode.STYLE = STYLE;
    }

    public static void updateCoordInfoDisplay()
    {
        GuiOverlayManager.setShowMiddlePos(false);
        GuiOverlayManager.setShowEndPos(true);
    }

    public static void changeYCreateMode(float wheel)
    {
        if(!MODE.equals(TemplateBuildingMode.MAKE_ADJUSTMENTS))
        {
            if (wheel > 0)
                MOUSE_Y_OFFSET++;
            else
                MOUSE_Y_OFFSET--;
        }


        GuiOverlayManager.setOffsetPos(new BlockPos(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET));
    }

    public static void clearMouseOffset()
    {
        MOUSE_X_OFFSET = 0;
        MOUSE_Y_OFFSET = 0;
        MOUSE_Z_OFFSET = 0;
    }
}
