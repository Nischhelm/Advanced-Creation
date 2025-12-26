package com.deadtiger.advcreation.place_template;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.client.gui.gui_screen.templateInventoryScreen.GuiTemplaceInventoryScreenFunctionality;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.client.render.RenderSelectionHighlight;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.network.message.MessagePlaceTemplateBlock;
import com.deadtiger.advcreation.network.network_utility.ByteBufCustomUtils;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.template.Template;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.template.TemplateManager;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.undo_actions.UndoFunctionality;
import com.deadtiger.advcreation.utility.CursorVector;
import com.deadtiger.advcreation.utility.TileEntityPlacementHelper;
import com.deadtiger.advcreation.utility.VecTransformer;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.deadtiger.advcreation.utility.PlacementHelper.isNotGroundMaterial;

/**
 * Static class controlling the placement of templates in the PLACE mode and the placement of CopyPaste and Move ToolMode
 * in the BUILD mode.
 * This class can either take the current selected template or can be given a template and primarily also a position
 * it can place or render a preview of a template in the chosen position
 * <p>
 * also has the code for all the user settings like foundations/ air settings/ wall settings
 * All the logic to place and preview these are done here
 */
public class PlaceTemplateMode
{
    //private static EnumFoundationMode  foundation = EnumFoundationMode.NO_FOUNDATION;
    public static EnumFoundationMode FOUNDATION = EnumFoundationMode.NO_FOUNDATION;
    public static IBlockState FOUNDATION_BLOCKSTATE = Blocks.COBBLESTONE.getDefaultState();
    private static int MAX_FOUNDATION_LENGTH = 50;

    public static EnumAirMode AIR = EnumAirMode.INSIDE_AIR;
    public static EnumWallMode WALL = EnumWallMode.ALL_WALLS;

    public static BlockPos PREV_POS = null;
    public static Vec3d PREV_HITVEC = null;
    public static CursorVector PREV_CURSOR_VEC = null;
    public static int PREV_X_OFFSET = 0;
    public static int PREV_Z_OFFSET = 0;
    public static boolean HELD_ALT = false;

    public static int MOUSE_X_OFFSET = 0;
    public static int MOUSE_Y_OFFSET = 0;
    public static int MOUSE_Z_OFFSET = 0;

    public static boolean HOLD_PREVIEW = false; // hold the preview in place

    public static BlockPos FIXED_POS = null;

    public static EnumFacing ROTATION = EnumFacing.WEST;

    public static HashMap<EnumFoundationMode, HashMap<EnumAirMode, HashMap<EnumWallMode, Integer>>> IDENTIFICATION_INDICES = new HashMap<>();

    public static boolean placingTemplate = false;
    public static ArrayList<TemplateBlock> blocksToCheckForPlacement = new ArrayList<>();
    public static long lastPlacementCheck = 0;

    public static ArrayList<BlockPos> CURR_PREVIEW_POS = new ArrayList<>();
    public static ArrayList<BlockPos> CURR_PREVIEW_CURSOR_POS = new ArrayList<>();
    public static BlockPos PREV_SELECTION_POS = BlockPos.ORIGIN;
    public static String PREV_TEMPLATE_NAME = "";

    public static boolean FORCE_REDRAW = false;


    public static void reset()
    {
        clearMouseOffset();
        blocksToCheckForPlacement.clear();
        if(HOLD_PREVIEW)
            toggleHoldPreview();
    }

    public enum EnumFoundationMode
    {
        FOUNDATION_LAYER("One Layer"), FOUNDATION_TO_GROUND("To Ground"), NO_FOUNDATION("No Foundation");

        public EnumFoundationMode rotateMode()
        {
            switch (this)
            {
                case NO_FOUNDATION:
                    return FOUNDATION_LAYER;
                case FOUNDATION_LAYER:
                    return FOUNDATION_TO_GROUND;
                case FOUNDATION_TO_GROUND:
                    return NO_FOUNDATION;
                default:
                    throw new IllegalStateException("Invalid Direction Mode!!");
            }
        }


        public String buttonText;

        EnumFoundationMode(String buttonText)
        {
            this.buttonText = buttonText;
        }
    }

    public enum EnumAirMode
    {
        NO_AIR("No Air"),
        INSIDE_AIR("Inside Air"),
        ALL_AIR("All Air");

        public EnumAirMode rotateMode()
        {
            switch (this)
            {
                case NO_AIR:
                    return INSIDE_AIR;
                case INSIDE_AIR:
                    return ALL_AIR;
                case ALL_AIR:
                    return NO_AIR;
                default:
                    throw new IllegalStateException("Invalid Direction Mode!!");
            }
        }

        public String buttonText;

        EnumAirMode(String buttonText)
        {
            this.buttonText = buttonText;
        }
    }

    public enum EnumWallMode
    {
        ALL_WALLS("All Walls"),
        NON_INTERSECTING_WALLS("New Walls"),
        NO_WALLS("No Walls");

        public EnumWallMode rotateMode()
        {
            switch (this)
            {
                case NO_WALLS:
                    return NON_INTERSECTING_WALLS;
                case NON_INTERSECTING_WALLS:
                    return ALL_WALLS;
                case ALL_WALLS:
                    return NO_WALLS;
                default:
                    throw new IllegalStateException("Invalid Direction Mode!!");
            }
        }

        public String buttonText;

        EnumWallMode(String buttonText)
        {
            this.buttonText = buttonText;
        }
    }

    public static void init()
    {
        EnumFoundationMode[] foundationValues = EnumFoundationMode.values();
        EnumAirMode[] airValues = EnumAirMode.values();
        EnumWallMode[] wallValues = EnumWallMode.values();

        for (int u = 0; u < foundationValues.length; u++)
        {
            IDENTIFICATION_INDICES.put(foundationValues[u], new HashMap<>());
            for (int v = 0; v < airValues.length; v++)
            {
                IDENTIFICATION_INDICES.get(foundationValues[u]).put(airValues[v], new HashMap<>());
                for (int w = 0; w < wallValues.length; w++)
                {
                    IDENTIFICATION_INDICES.get(foundationValues[u]).get(airValues[v]).put(wallValues[w], u * 9 + v * 3 + w);
                }
            }
        }
    }

    public static void placeTemplate(BlockPos position, EntityPlayer player)
    {
        BlockPos position2 = position.add(MOUSE_X_OFFSET, MOUSE_Y_OFFSET, MOUSE_Z_OFFSET);

        ArrayList<TemplateBlock> firstBlocksToServer = new ArrayList<>();
        //furniture is placed after the walls and air
        ArrayList<TemplateBlock> furnitureBlocks = new ArrayList<>();
        BlockPos startUpdatePos = new BlockPos(position2);
        BlockPos endUpdatePos = new BlockPos(position2);

        if (HOLD_PREVIEW)
            position2 = FIXED_POS.add(MOUSE_X_OFFSET, MOUSE_Y_OFFSET, MOUSE_Z_OFFSET);

        if ((AIR == EnumAirMode.NO_AIR &&
                WALL == EnumWallMode.NO_WALLS &&
                FOUNDATION == EnumFoundationMode.NO_FOUNDATION))
            return;
        else
        {
            //only place the template when there is something to place so atleast one of the modes needs to be not NO_...
            int select_index = GuiTemplaceInventoryScreenFunctionality.selected_index;
            Action currAction = new Action();
            if (!(TemplateManager.TEMPLATES_LIST.size() > select_index && 0 <= select_index))
                return;
            else
            {
                Template template = TemplateManager.TEMPLATES_LIST.get(select_index);

                for (int j = 0; j < template.getBlockListSize(); j++)
                {
                    TemplateBlock tempBlock = template.getTempBlockOffset(j);

                    //draw air or not
                    if (isUnwantedAirBlock(tempBlock)) continue;
                    //draw wall or not
                    if (isUnwantedWallBlock(player, position2, tempBlock)) continue;

                    if (tempBlock.getType() == TemplateBlock.EnumBlockType.FURNITURE)
                    {
                        furnitureBlocks.add(tempBlock);
                        continue;
                    }

                    endUpdatePos = endUpdatePos.add(template.getSize());
                    placeBlockClient(position2, player, currAction, tempBlock);
                    firstBlocksToServer.add(tempBlock);
//                    if (j % blockCheckDelay == 0)
//                    {
//                        BlockPos offset = position2.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());
//                        if (offset.getY() < 1)
//                            break;
//                        blocksToCheckForPlacement.add(new TemplateBlock(tempBlock.getFace(), offset, tempBlock.getBlockState()));
//                    }

                }


                //place all the furniture blocks after the walls and air are set
                for (TemplateBlock tempBlock : furnitureBlocks)
                {
                    placeBlockClient(position2, player, currAction, tempBlock);
                    BlockPos offset = position2.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());

//                    if (offset.getY() < 1)
//                        break;
//                    blocksToCheckForPlacement.add(new TemplateBlock(tempBlock.getFace(),offset, tempBlock.getBlockState()));

                }

                if (FOUNDATION == EnumFoundationMode.FOUNDATION_LAYER)
                    startUpdatePos = placeFoundationLayer(player, position2, firstBlocksToServer, startUpdatePos, currAction, template);
                else if (FOUNDATION == EnumFoundationMode.FOUNDATION_TO_GROUND)
                    startUpdatePos = placeFoundationToGround(player, position2, firstBlocksToServer, startUpdatePos, currAction, template);


                //prepare for sending to server
                UndoFunctionality.addActionToHistory(currAction);
                NetworkPlaceBlockListFormatter.sendBlocksListsRelToPosToServer(position2, firstBlocksToServer, furnitureBlocks, startUpdatePos, endUpdatePos,currAction);

            }
        }
    }

    private static BlockPos placeFoundationToGround(EntityPlayer player, BlockPos position2, ArrayList<TemplateBlock> firstBlocksToServer, BlockPos startUpdatePos, Action currAction, Template template)
    {
        int maxNewFoundationLength = 0;
        int blockCheckDelay = 50;
        for (int j = 0; j < template.bottomBlocks.size(); j++)
        {
            //for each block under the bottom of the building keep going down untill you hit a solid block
            BlockPos blockPos = template.getBottomBlockPosOffset(j).down();
            BlockPos worldPos = position2.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());

            IBlockState iblockstate2 = player.world.getBlockState(worldPos);
            int foundationLength = 0;

            //keep going down while the block is made from an invalid material
            while (isNotGroundMaterial(iblockstate2) && foundationLength < MAX_FOUNDATION_LENGTH)
            {
                TemplateBlock newTempBlock = new TemplateBlock(EnumFacing.NORTH, blockPos, FOUNDATION_BLOCKSTATE);
                newTempBlock.setType(TemplateBlock.EnumBlockType.WALL);

                placeBlockClient(position2, player, currAction, newTempBlock);
                firstBlocksToServer.add(newTempBlock);

//                if(j % blockCheckDelay == 0)
//                {
//                    BlockPos offset = position2.add(newTempBlock.getX_offset(), newTempBlock.getY_offset(), newTempBlock.getZ_offset());
//                    if (offset.getY() < 1)
//                        break;
//                    blocksToCheckForPlacement.add(new TemplateBlock(newTempBlock.getFace(),offset, newTempBlock.getBlockState()));
//
//                }

                blockPos = blockPos.down();
                worldPos = position2.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                iblockstate2 = player.world.getBlockState(worldPos);
                foundationLength++;

            }
            if (foundationLength > maxNewFoundationLength)
                maxNewFoundationLength = foundationLength;

        }
        startUpdatePos = startUpdatePos.add(0, -maxNewFoundationLength, 0);
        return startUpdatePos;
    }

    private static BlockPos placeFoundationLayer(EntityPlayer player, BlockPos position2, ArrayList<TemplateBlock> firstBlocksToServer, BlockPos startUpdatePos, Action currAction, Template template)
    {
        int blockCheckDelay = 50;
        for (int j = 0; j < template.bottomBlocks.size(); j++)
        {
            BlockPos blockPos = template.getBottomBlockPosOffset(j);
            TemplateBlock newTempBlock = new TemplateBlock(EnumFacing.NORTH, blockPos.add(0, -1, 0), FOUNDATION_BLOCKSTATE);
            newTempBlock.setType(TemplateBlock.EnumBlockType.WALL);


            placeBlockClient(position2, player, currAction, newTempBlock);
            firstBlocksToServer.add(newTempBlock);

            if(j % blockCheckDelay == 0)
            {
                BlockPos offset = position2.add(newTempBlock.getX_offset(), newTempBlock.getY_offset(), newTempBlock.getZ_offset());
                if (offset.getY() < 1)
                    break;
                blocksToCheckForPlacement.add(new TemplateBlock(newTempBlock.getFace(),offset, newTempBlock.getBlockState()));

            }
        }
        startUpdatePos = startUpdatePos.add(0, -1, 0);
        return startUpdatePos;
    }

    private static boolean isUnwantedWallBlock(EntityPlayer player, BlockPos position2, TemplateBlock tempBlock)
    {
        if (WALL == EnumWallMode.NO_WALLS)
        {
            if (tempBlock.getType() == TemplateBlock.EnumBlockType.WALL || tempBlock.getType() == TemplateBlock.EnumBlockType.PLANT)
                return true;

        }
        else if (WALL == EnumWallMode.NON_INTERSECTING_WALLS)
        {
            if (tempBlock.getType() == TemplateBlock.EnumBlockType.WALL || tempBlock.getType() == TemplateBlock.EnumBlockType.PLANT)
            {
                BlockPos worldPos = position2.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());
                IBlockState iblockstate2 = player.world.getBlockState(worldPos);
                if (!isNotGroundMaterial(iblockstate2))
                    return true;
            }

        }
        return false;
    }

    private static boolean isUnwantedAirBlock(TemplateBlock tempBlock)
    {
        if (AIR == EnumAirMode.INSIDE_AIR)
        {
            if (tempBlock.getType() == TemplateBlock.EnumBlockType.OUTSIDE)
                return true;
        }
        else if (AIR == EnumAirMode.NO_AIR)
        {
            if (tempBlock.getType() == TemplateBlock.EnumBlockType.OUTSIDE ||
                    tempBlock.getType() == TemplateBlock.EnumBlockType.INSIDE)
                return true;
        }
        return false;
    }

    public static void placeNewTemplate(Template newTemplate, BlockPos position, Action currAction, EntityPlayer player)
    {
        BlockPos position2 = position.add(MOUSE_X_OFFSET, MOUSE_Y_OFFSET, MOUSE_Z_OFFSET);
        BlockPos startUpdatePos = new BlockPos(position2);
        BlockPos endUpdatePos = new BlockPos(position2);
        ArrayList<TemplateBlock> torchBlocks = new ArrayList<>();
        ArrayList<TemplateBlock> firstBlocksToServer = new ArrayList<>();
//        if (!placingTemplate)
//        {
//            placingTemplate = true;
//            blocksToCheckForPlacement.clear();
//            int blockCheckDelay = 50;


            for (int j = 0; j < newTemplate.getBlockListSize(); j++)
            {
                TemplateBlock tempBlock = newTemplate.getTempBlockOffset(j);

                if (tempBlock.getBlockState().equals(Blocks.AIR.getDefaultState()))
                    continue;

                if (tempBlock.getBlockState().getBlock() instanceof BlockTorch)
                {
                    torchBlocks.add(tempBlock);
                    continue;
                }

                placeBlockClient(position2, player, currAction, tempBlock);
                firstBlocksToServer.add(tempBlock);
//                if (j % blockCheckDelay == 0)
//                {
//                    BlockPos offset = position2.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());
//                    if (offset.getY() < 1)
//                        break;
//                    blocksToCheckForPlacement.add(new TemplateBlock(tempBlock.getFace(), offset, tempBlock.getBlockState()));
//
//                }
            }

            for (TemplateBlock tempBlock : torchBlocks)
            {
                placeBlockClient(position2, player, currAction, tempBlock);
//                BlockPos offset = position2.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());
//                if (offset.getY() < 1)
//                    break;
//                blocksToCheckForPlacement.add(new TemplateBlock(tempBlock.getFace(), offset, tempBlock.getBlockState()));

            }
            endUpdatePos = endUpdatePos.add(newTemplate.getSize());
            NetworkPlaceBlockListFormatter.sendBlocksListsRelToPosToServer(position2, firstBlocksToServer, torchBlocks, startUpdatePos, endUpdatePos,currAction);

//        }
    }

    public static void placeBlockServer(BlockPos position, TemplateBlock tempBlock, EntityPlayerMP playerMP)
    {
        BlockPos offset = position.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());
        //stop at the start of the bedrock layer otherwise the whole world crashes and is broken permanently it seems
        if (offset.getY() < 1)
            return;

        //Save the blockstates of the blocks that are being replace for the undo featur
        //set the new blockstate in the world
        if (tempBlock.getBlockState().getMaterial() == Material.AIR)
        {
            IBlockState iblockstate = playerMP.world.getBlockState(offset);
            boolean flag = iblockstate.getBlock().removedByPlayer(iblockstate, playerMP.world, offset, playerMP, false);

            if(!flag)
                flag = playerMP.world.setBlockState(offset, tempBlock.getBlockState(), 3);

            if (flag)
            {
                iblockstate.getBlock().onPlayerDestroy(playerMP.world, offset, iblockstate);
            }
            playerMP.connection.sendPacket(new SPacketBlockChange(playerMP.world, offset));
        }
        else
        {
//            if (PlacementHelper.isNotGroundMaterial(tempBlock.getBlockState()))
//            {
//                playerMP.world.setBlockState(offset, tempBlock.getBlockState(), 3);
//            }
//            else
//            {
//                if (!playerMP.world.setBlockState(offset, tempBlock.getBlockState(), 3))
//                {
////                    ItemBlock itemblock = new ItemBlock(tempBlock.getBlockState().getBlock());
////                    IBlockState state = playerMP.world.getBlockState(offset);
//
////                    ItemBlock.setTileEntityNBT(playerMP.world, playerMP, offset, new ItemStack(itemblock));
////                    tempBlock.getBlockState().getBlock().onBlockPlacedBy(playerMP.world, offset, state, playerMP, new ItemStack(itemblock));
////
////                    CriteriaTriggers.PLACED_BLOCK.trigger(playerMP, offset, new ItemStack(itemblock));
//                }
//            }
            playerMP.world.setBlockState(offset, tempBlock.getBlockState(), 3);
//            if(TileEntityPlacementHelper.blockHasTileEntity(tempBlock.getBlockState().getBlock()))
            if(tempBlock.getTileEntity() != null)
            {
                try
                {
                    TileEntityPlacementHelper.formatTileEntityForBlock(tempBlock.getBlockState(),playerMP.world.getTileEntity(offset),tempBlock.getTileEntity(),offset,playerMP.world);
                }
                catch (InvocationTargetException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void placeBlockClient(BlockPos position, EntityPlayer player, Action currAction, TemplateBlock tempBlock)
    {
        BlockPos offset = position.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());
        //stop at the start of the bedrock layer otherwise the whole world crashes and is broken permanently it seems
        if (offset.getY() < 1)
            return;

        //Save the blockstates of the blocks that are being replace for the undo feature
        IBlockState currBlockState = player.world.getBlockState(offset);
        TileEntity tileEntity = player.world.getTileEntity(offset);
        currAction.add(EnumFacing.NORTH, offset, currBlockState,tileEntity);
    }

    public static void placeBlock(BlockPos position, EntityPlayer player, Action currAction, TemplateBlock tempBlock)
    {
        BlockPos offset = position.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());
        //stop at the start of the bedrock layer otherwise the whole world crashes and is broken permanently it seems
        if (offset.getY() < 1)
            return;

        //Save the blockstates of the blocks that are being replace for the undo feature
        IBlockState currBlockState = player.world.getBlockState(offset);
        TileEntity tileEntity = player.world.getTileEntity(offset);
        currAction.add(EnumFacing.NORTH, offset, currBlockState,tileEntity);
        //check if templateblock can be safely sent to the server otherwise replace it with an air block
        TemplateBlock safeTempBlock =  ByteBufCustomUtils.getSafeTemplateBlock(tempBlock,Minecraft.getMinecraft().player);

        Minecraft mc = Minecraft.getMinecraft();
        NetHandlerPlayClient connection = null;
        if (player instanceof EntityPlayerSP)
            connection = ((EntityPlayerSP) player).connection;
        if (connection != null)
        {
            NetworkHandler.sendToServer(new MessagePlaceTemplateBlock(offset.getX(), offset.getY(), offset.getZ(), true, safeTempBlock.getBlockState(),safeTempBlock.getTileEntity()));
        }
    }

    public static int drawPreview(BlockPos position, EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        int select_index = GuiTemplaceInventoryScreenFunctionality.selected_index;

        int hashcode = 0;
        BlockPos position2 = position;

        Template template = null;
        BlockPos selectionPos = null;

        if (TemplateManager.TEMPLATES_LIST.size() > select_index && 0 <= select_index)
        {
            // display boundingboxes for each block in the template
            template = TemplateManager.TEMPLATES_LIST.get(select_index);
            template.tryCalculateProperties();




            if (FIXED_POS != null)
                position2 = FIXED_POS.add(MOUSE_X_OFFSET, MOUSE_Y_OFFSET, MOUSE_Z_OFFSET);
            selectionPos = position2.add(template.getX_offset_template(), template.getY_offset_template(), template.getZ_offset_template());

        }

        boolean method1BlockIndices = false;

        if(template != null && template.tooBig && template.getName().equals(PREV_TEMPLATE_NAME) && (!CURR_PREVIEW_POS.isEmpty()))
            method1BlockIndices = true;

        RenderPreview.previewList.clear();

        if (template != null)
        {
            RenderPreview.selectionStartPos = template.getCorrectSelectionBoxPos(selectionPos);
            RenderPreview.selectionEndPos = RenderPreview.selectionStartPos.add(template.getOrientedSize());

            if(method1BlockIndices)
            {
                //this 1st method is the most optimal method to draw the preview where indices of the blocks were saved by the previous drawing

                ArrayList<ArrayList<ArrayList<TemplateBlock>>> XYZOrderedBlockLists = template.getXYZOrderedBlockLists();
                for (int i = 0; i < CURR_PREVIEW_POS.size(); i++)
                {
                    BlockPos pos = CURR_PREVIEW_POS.get(i);
                    TemplateBlock tempBlock = template.getTempBlockOffset(XYZOrderedBlockLists.get(pos.getX()).get(pos.getY()).get(pos.getZ()) );
                    hashcode += ~~RenderPreview.drawPreviewBlock( position2, tempBlock, entityplayer, partialTicks,face);
                }

                if(position != null && !position.equals(PREV_SELECTION_POS))
                {
                    CURR_PREVIEW_POS = new ArrayList<>();
                    PREV_SELECTION_POS = position;
                    hashcode += drawFilteredCursorPreviewBlocks(template, position2, position, entityplayer, partialTicks,face,true,CURR_PREVIEW_CURSOR_POS);
                }
                else
                {
                    for (int i = 0; i < CURR_PREVIEW_CURSOR_POS.size(); i++)
                    {
                        BlockPos pos = CURR_PREVIEW_CURSOR_POS.get(i);
                        TemplateBlock tempBlock = template.getTempBlockOffset(XYZOrderedBlockLists.get(pos.getX()).get(pos.getY()).get(pos.getZ()) );
                        hashcode += ~~RenderPreview.drawPreviewBlock(position2, tempBlock, entityplayer, partialTicks,face);
                    }
                }
            }
            else
            {
                CURR_PREVIEW_POS = new ArrayList<>();
                CURR_PREVIEW_CURSOR_POS = new ArrayList<>();
                // display boundingboxes for each block in the template
                template.tryCalculateProperties();
                PREV_TEMPLATE_NAME = template.getName();


                if (!HOLD_PREVIEW)
                {
                    position2 = position.add(MOUSE_X_OFFSET, MOUSE_Y_OFFSET, MOUSE_Z_OFFSET);
                    selectionPos = position2.add(template.getX_offset_template(), template.getY_offset_template(), template.getZ_offset_template());
                }

                if (!template.tooBig)
                {
                    hashcode += ~~drawPreviewBlocks(template, position2, entityplayer, partialTicks);

                    PlaceTemplateMode.drawSelectionBox(Minecraft.getMinecraft(), entityplayer, template.getCorrectSelectionBoxPos(selectionPos), hitVec, template.getOrientedSize(), false, partialTicks);

                    if (FOUNDATION == EnumFoundationMode.FOUNDATION_LAYER)
                    {

                        for (int j = 0; j < template.bottomBlocks.size(); j++)
                        {
                            BlockPos blockPos = template.getBottomBlockPosOffset(j);
                            TemplateBlock newTempBlock = new TemplateBlock(EnumFacing.NORTH, blockPos.down(), FOUNDATION_BLOCKSTATE);
                            newTempBlock.setType(TemplateBlock.EnumBlockType.WALL);
                            hashcode += ~~RenderPreview.drawPreviewBlock(position2, newTempBlock, entityplayer, partialTicks, EnumFacing.NORTH);
                        }


                    }
                    else if (FOUNDATION == EnumFoundationMode.FOUNDATION_TO_GROUND)
                    {

                        for (int j = 0; j < template.bottomBlocks.size(); j++)
                        {
                            //for each block under the bottom of the building keep going down untill you hit a solid block
                            BlockPos blockPos = template.getBottomBlockPosOffset(j).down();
                            BlockPos worldPos = position2.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                            IBlockState iblockstate2 = entityplayer.world.getBlockState(worldPos);
                            int foundationLength = 0;

                            //keep going down while the block is made from an invalid material
                            while (isNotGroundMaterial(iblockstate2) && foundationLength < MAX_FOUNDATION_LENGTH)
                            {
                                TemplateBlock newTempBlock = new TemplateBlock(EnumFacing.NORTH, blockPos, FOUNDATION_BLOCKSTATE);
                                newTempBlock.setType(TemplateBlock.EnumBlockType.WALL);
                                hashcode += ~~RenderPreview.drawPreviewBlock(position2, newTempBlock, entityplayer, partialTicks, EnumFacing.NORTH);

                                blockPos = blockPos.down();
                                worldPos = position2.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                iblockstate2 = entityplayer.world.getBlockState(worldPos);
                                foundationLength++;
                            }

                        }
                    }

                    //draw red selection boxes in the inside of the building
                    for (int j = 0; j < template.getBlockListSize(); j++)
                    {
                        TemplateBlock tempBlock = template.getTempBlockOffset(j);

                        if (tempBlock.getType() == TemplateBlock.EnumBlockType.INSIDE)
                        {
                            drawPreviewBlockHighlight(position2, tempBlock, entityplayer, hitVec, face, partialTicks);
                            int currHashcode = position2.add(tempBlock.getBlockPos()).hashCode() + tempBlock.getBlockState().hashCode();

                            hashcode += ~~currHashcode;
                        }
                    }
                    BlockPos size = template.getSize();
                    GuiOverlayManager.setBlockCount(size.getX(), size.getY(), size.getZ(), (int) template.solidBlockCount);
                }
                else
                {
                    GuiOverlayManager.setBlockCount(0, 0, 0, 0);
                    hashcode = drawFilteredEdgePreviewBlocks( template, position2, entityplayer,face, partialTicks,CURR_PREVIEW_POS);
                    hashcode += drawFilteredCursorPreviewBlocks(template, position2, position, entityplayer, partialTicks,face,true,CURR_PREVIEW_CURSOR_POS);

                }
                FORCE_REDRAW = false;
            }
            RenderPreview.selectionStartPos = template.getCorrectSelectionBoxPos(selectionPos);
            RenderPreview.selectionEndPos = RenderPreview.selectionStartPos.add(template.getOrientedSize());


            PlaceTemplateMode.drawSelectionBox(Minecraft.getMinecraft(), entityplayer, RenderPreview.selectionStartPos, hitVec, template.getOrientedSize(), false, partialTicks);

            GuiOverlayManager.setStartPos(RenderPreview.selectionStartPos);
            GuiOverlayManager.setEndPos(RenderPreview.selectionEndPos.add(-1,-1,-1));
            GuiOverlayManager.setMiddlePos(position2);

            GlStateManager.enableColorMaterial();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableColorLogic();

//             drawOffsetIndication(entityplayer,partialTicks);
            if(FIXED_POS != null)
            {
                RenderSelectionHighlight.drawFadingBlocksToPos(entityplayer, partialTicks,FIXED_POS,PlaceTemplateMode.MOUSE_X_OFFSET,PlaceTemplateMode.MOUSE_Y_OFFSET,PlaceTemplateMode.MOUSE_Z_OFFSET);
                RenderSelectionHighlight.drawWhiteBlockHighlight(entityplayer,FIXED_POS,partialTicks);
                if(HOLD_PREVIEW && position != null)
                    RenderSelectionHighlight.drawWhiteBlockHighlight(entityplayer,position,partialTicks);

            }

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableColorLogic();



        }
        else
        {
            CURR_PREVIEW_POS = new ArrayList<>();
            CURR_PREVIEW_CURSOR_POS = new ArrayList<>();
            PREV_TEMPLATE_NAME = "";

//            GuiOverlayManager.setBlockCountVisibility(false);
            GuiOverlayManager.setPosVisibility(true);
            GuiOverlayManager.setStartPos(position.add(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET));

            GlStateManager.enableColorMaterial();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableColorLogic();

//             drawOffsetIndication(entityplayer,partialTicks);
            if(position != null)
            {
                RenderSelectionHighlight.drawFadingBlocksToPos(entityplayer, partialTicks,position,PlaceTemplateMode.MOUSE_X_OFFSET,PlaceTemplateMode.MOUSE_Y_OFFSET,PlaceTemplateMode.MOUSE_Z_OFFSET);
                RenderSelectionHighlight.drawWhiteBlockHighlight(entityplayer,position,partialTicks);

            }

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableColorLogic();

        }

        GuiOverlayManager.setOffsetPos(new BlockPos(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET));
//        GuiOverlayManager.setPlacePointedCoordinate(position);
        GuiOverlayManager.setPlacePointedCoordinate(position.add(MOUSE_X_OFFSET,MOUSE_Y_OFFSET,MOUSE_Z_OFFSET));
        GuiOverlayManager.setDeletePointedCoordinate(null);

        return hashcode;
    }

    public static void updateCoordInfoDisplay()
    {
        if (TemplateManager.TEMPLATES_LIST.size() > GuiTemplaceInventoryScreenFunctionality.selected_index && 0 <= GuiTemplaceInventoryScreenFunctionality.selected_index)
        {
            GuiOverlayManager.setShowMiddlePos(true);
            GuiOverlayManager.setShowEndPos(true);
        }
        else
        {
            GuiOverlayManager.setShowMiddlePos(false);
            GuiOverlayManager.setShowEndPos(false);
        }
    }

    public static void drawSelectionBox(Minecraft mc, EntityPlayer player, BlockPos newEndPos, Vec3d hitvec, BlockPos size, boolean highlightSides, float partialTicks)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();


        RenderPreview.drawSelectionBoundingBox(newEndPos, newEndPos.add(size.getX(), size.getY(), size.getZ()), hitvec, mc, player, highlightSides, partialTicks, 8.0f, 8.0f, 8.0f, 1.0F);

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
    }


    public static int drawPreviewBlocks(Template template, BlockPos position, EntityPlayer entityplayer, Float partialTicks)
    {
        ArrayList<TemplateBlock> offsetRotatedBlockList = template.getOffsetRotatedBlockList();

        int hashcode = 0;
        for (int j = 0; j < offsetRotatedBlockList.size(); j++)
        {
            TemplateBlock tempBlock = offsetRotatedBlockList.get(j);

            if (tempBlock.getBlockState().getBlock().getTranslationKey().equals("tile.air") || tempBlock.isEnclosed())
                continue;

            hashcode += ~~RenderPreview.drawPreviewBlock(position, tempBlock, entityplayer, partialTicks,EnumFacing.UP);

        }
        return hashcode;
    }


//    public static int drawFilteredPreviewBlocks( Template template, BlockPos position, BlockPos selectedPos, EntityPlayer entityplayer, Float partialTicks)
//    {
//        return  drawFilteredPreviewBlocks(template,position,selectedPos,entityplayer,partialTicks,true);
//    }
//
//    /**
//     * Draw preview of the template but do not draw the blocks beneath the surface
//     * When the template is very big you only draw an area arround the cursor and the sides to preserve render compute power
//     * @param template
//     * @param position
//     * @param selectedPos
//     * @param entityplayer
//     * @param partialTicks
//     * @return
//     */
//    public static int drawFilteredPreviewBlocks(Template template, BlockPos position, BlockPos selectedPos, EntityPlayer entityplayer, Float partialTicks,boolean cursorFocus)
//    {
//        int hashcode = 0;
//        for (int j = 0; j < template.getBlockListSize(); j++)
//        {
//            TemplateBlock tempBlock = template.getTempBlockOffset(j);
//
//            if (tempBlock.getBlockState().getBlock().getTranslationKey().equals("tile.air") || tempBlock.isEnclosed())
//                continue;
//
//
//            BlockPos blkOffset = tempBlock.getBlockPos();
//            BlockPos realWorldPos = position.add(blkOffset.getX(), blkOffset.getY(), blkOffset.getZ());
//
//            int viewBoxSize = 15;
//            boolean inViewBoxArroundCursor = cursorFocus && isInViewBoxArroundCursor(selectedPos, realWorldPos, viewBoxSize);
//            boolean onEdge = isOnEdge(template, position, realWorldPos, (int) ConfigurationHandler.toolConfig.PREVIEW_BLOCK_LIMIT);
//
//            if (onEdge || inViewBoxArroundCursor)
//                hashcode += ~~RenderPreview.drawPreviewBlock(position, tempBlock, entityplayer, partialTicks,EnumFacing.UP);
//        }
//        return hashcode;
//    }

    private static boolean isInViewBoxArroundCursor(BlockPos selectedPos, BlockPos realWorldPos, int viewBoxSize)
    {
        return (selectedPos.getZ() < realWorldPos.getZ() + viewBoxSize && selectedPos.getZ() > realWorldPos.getZ() - viewBoxSize) &&
                (selectedPos.getX() < realWorldPos.getX() + viewBoxSize && selectedPos.getX() > realWorldPos.getX() - viewBoxSize);
    }

    /**
     * Draw preview of the template but do not draw the blocks beneath the surface
     * When the template is very big you only draw an area arround the cursor and the sides to preserve render compute power
     *
     * @param template
     * @param position
     * @param entityplayer
     * @param partialTicks
     * @return
     */
    public static int drawFilteredEdgePreviewBlocks(Template template, BlockPos position, EntityPlayer entityplayer, EnumFacing face, Float partialTicks, ArrayList<BlockPos> blockPosListToFill)
    {
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> XYZarray =  template.getXYZOrderedBlockLists();
        int showLimit = (int) Math.floor(ConfigurationHandler.toolConfig.PREVIEW_BLOCK_LIMIT);
        BlockPos size = template.getSize();

        int showLimitX = size.getX() <= showLimit*2? size.getX()/2:showLimit;
        int showLimitY = size.getY() <= showLimit*2? size.getY()/2:showLimit;
        int showLimitZ = size.getZ() <= showLimit*2? size.getZ()/2:showLimit;

        int hashcode = 0;
        //check in X direction
        for (int x = 0; x < showLimitX; x++)
        {
            for (int y = 0; y < size.getY() ; y++)
            {
                for (int z = 0; z < size.getZ(); z++)
                {
                    hashcode = drawPreviewBlockAndAddToBlockPosList(template, position, XYZarray.get(x).get(y).get(z), x, y, z, entityplayer, partialTicks,face, blockPosListToFill, hashcode);
                    int newX = size.getX()-1 - x;
                    hashcode = drawPreviewBlockAndAddToBlockPosList( template, position, XYZarray.get(newX).get(y).get(z), newX, y, z, entityplayer, partialTicks,face, blockPosListToFill, hashcode);
                }
            }
        }

        if(ConfigurationHandler.toolConfig.PREVIEW_TOPBOTTOM_BIG_TEMPLATE)
        {
            // check in Y direction
            for (int x = showLimit; x < size.getX() - showLimit; x++)
            {
                for (int y = 0; y < showLimitY; y++)
                {
                    for (int z = showLimit; z < size.getZ() - showLimit; z++)
                    {
                        hashcode = drawPreviewBlockAndAddToBlockPosList( template, position, XYZarray.get(x).get(y).get(z), x, y, z, entityplayer, partialTicks,face, blockPosListToFill, hashcode);
                        int newY = size.getY()-1 - y;
                        hashcode = drawPreviewBlockAndAddToBlockPosList( template, position, XYZarray.get( x).get(newY).get(z), x, newY, z, entityplayer, partialTicks,face, blockPosListToFill, hashcode);
                    }
                }
            }
        }


        // check in Z direction
        for (int x = showLimit; x < size.getX() - showLimit; x++)
        {
            for (int y =0; y < size.getY(); y++)
            {
                for (int z = 0; z < showLimitZ; z++)
                {
                    hashcode = drawPreviewBlockAndAddToBlockPosList( template, position, XYZarray.get(x).get(y).get(z), x, y, z, entityplayer, partialTicks,face, blockPosListToFill, hashcode);
                    int newZ = size.getZ()-1 -z;
                    hashcode = drawPreviewBlockAndAddToBlockPosList( template, position, XYZarray.get(x).get(y).get(newZ), x, y, newZ, entityplayer, partialTicks,face, blockPosListToFill, hashcode);
                }
            }
        }

        return hashcode;
    }

    private static int drawPreviewBlockAndAddToBlockPosList( Template template, BlockPos position, TemplateBlock tempBlock, int x, int y, int z, EntityPlayer entityplayer, Float partialTicks, EnumFacing face, ArrayList<BlockPos> blockPosListToFill, int hashcode)
    {
        if (!(tempBlock.getBlockState().getBlock().getTranslationKey().equals("tile.air") || tempBlock.isEnclosed()))
        {
            if (blockPosListToFill != null)
                blockPosListToFill.add(new BlockPos(x, y, z));

            hashcode += ~~RenderPreview.drawPreviewBlock(position, template.getTempBlockOffset(tempBlock), entityplayer, partialTicks,face);
        }
        return hashcode;
    }

    public static int drawFilteredCursorPreviewBlocks( Template template, BlockPos position, BlockPos selectedPos, EntityPlayer entityplayer, Float partialTicks,EnumFacing face,boolean cursorFocus, ArrayList<BlockPos> blockPosListToFill)
    {
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> XYZarray =  template.getXYZOrderedBlockLists();
        int showLimit = (int) Math.floor((ConfigurationHandler.toolConfig.PREVIEW_BLOCK_LIMIT)*1.5);
        BlockPos size = template.getSize();

        int templateMinXPos = position.getX() - template.getX_offset_template();
        int templateMinZPos = position.getZ() - template.getZ_offset_template();

        int selectedPosX = selectedPos.getX() - RenderPreview.selectionStartPos.getX();
        int selectedPosZ = selectedPos.getZ() - RenderPreview.selectionStartPos.getZ();

        int x_off = selectedPosX;
        switch (template.getRotation())
        {
            case EAST:
                selectedPosX = -x_off;
                selectedPosZ = -selectedPosZ;
                break;
            case NORTH:
                selectedPosX = -selectedPosZ;
                selectedPosZ = x_off;
                break;
            case SOUTH:
                selectedPosX = selectedPosZ;
                selectedPosZ = -x_off;
                break;
        }

        if (template.isMirroredZY())
        {
            if(template.extrema == null)
                return 0;
            if ((template.extrema.minX - template.extrema.maxX) % 2 == 0)
                selectedPosX = -(selectedPosX - (size.getX()));
            else
                selectedPosX = -(selectedPosX - (size.getX() - 1));
        }

        if(selectedPosX < 0 || selectedPosX > template.getSize().getX() ||
                selectedPosZ < 0 || selectedPosZ > template.getSize().getZ())
            return 0;

        int minX = selectedPosX - showLimit;
        int minZ = selectedPosZ- showLimit;

        int maxX = (minX + showLimit*2);
        int maxZ = (minZ + showLimit*2);

        if(minX < 0)
        {
            minX = 0;
            maxX = showLimit*2;
        }
        if(minZ < 0)
        {
            minZ = 0;
            maxZ = showLimit*2;
        }
        if(maxX > template.getSize().getX())
            maxX = template.getSize().getX();
        if(maxZ > template.getSize().getZ())
            maxZ = template.getSize().getZ();


        int hashcode = 0;
        //check in X direction
        for (int x = minX; x < maxX; x++)
        {
            for (int y = 0; y < size.getY(); y++)
            {
                for (int z = minZ; z < maxZ; z++)
                {
                    hashcode = drawPreviewBlockAndAddToBlockPosList(template, position, XYZarray.get(x).get(y).get(z), x, y, z, entityplayer, partialTicks,face, blockPosListToFill, hashcode);
                }
            }
        }

        return hashcode;
    }


    public static boolean isOnEdge(Template template, BlockPos position, BlockPos realWorldPos, int showLimit)
    {
        BlockPos corrPosition = template.getCorrectSelectionBoxPos(position.add(template.getX_offset_template(), template.getY_offset_template(), template.getZ_offset_template()));
        BlockPos size = template.getSize();
        return (((realWorldPos.getZ() > corrPosition.getZ() && realWorldPos.getZ() < corrPosition.getZ() + showLimit) ||
                (realWorldPos.getZ() > corrPosition.getZ() + size.getZ() - showLimit && realWorldPos.getZ() < corrPosition.getZ() + size.getZ()))
                ||
                (realWorldPos.getX() > corrPosition.getX() && realWorldPos.getX() < corrPosition.getX() + showLimit) ||
                (realWorldPos.getX() > corrPosition.getX() + size.getX() - showLimit && realWorldPos.getX() < corrPosition.getX() + size.getX())
                ||
                (realWorldPos.getY() > corrPosition.getY() && realWorldPos.getY() < corrPosition.getY() + showLimit) ||
                (realWorldPos.getY() > corrPosition.getY() + size.getY() - showLimit && realWorldPos.getY() < corrPosition.getY() + size.getY()));
    }

    public static void drawPreviewBlockHighlight(BlockPos position, TemplateBlock tempBlock, EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks)
    {
        RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                face, position.add(tempBlock.getX_offset(),
                tempBlock.getY_offset(), tempBlock.getZ_offset()));

        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks, 1.0F, 0.0F, 0.0F);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
    }

    /**
     * Get the position to preview the template with the addition of the offset that is created when pressing ALT while
     * panning or scrolling to move it horizontally or vertically
     * @param position
     * @param hitVec
     * @return
     */
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

            return PREV_POS;
        }
        return position;

    }

    public static void updateParametersInNonAlterMode(TemplateBlock endGlobalblock, Vec3d hitVec, CursorVector cursorVector)
    {
        setPrevPos(endGlobalblock.getBlockPos());
        setPrevHitvec(hitVec);
        setPrevCursorVec(cursorVector);
        setHeldAlt(false);
    }


    public static void changeYTemplate(double wheel)
    {
        if (wheel > 0)
            MOUSE_Y_OFFSET++;
        else
            MOUSE_Y_OFFSET--;
    }

    public static void clearMouseOffset()
    {
        MOUSE_X_OFFSET = 0;
        MOUSE_Y_OFFSET = 0;
        MOUSE_Z_OFFSET = 0;
    }


    public static void rotate()
    {
        ROTATION = ROTATION.rotateY();
//        rotateMouseOffset();
    }

    public static void mirrorZY()
    {
        Template template = null;
        int selected_index = GuiTemplaceInventoryScreenFunctionality.selected_index;
        if (TemplateManager.TEMPLATES_LIST.size() > selected_index && 0 <= selected_index)
        {
            template = TemplateManager.TEMPLATES_LIST.get(GuiTemplaceInventoryScreenFunctionality.selected_index);
        }
        if (template != null)
        {
            template.mirrorZY();
        }
    }

    public static void setHeldAlt(boolean held)
    {
        HELD_ALT = held;
    }

    public static void setPrevPos(BlockPos pos)
    {
        PREV_POS = pos;
    }

    public static void setFixedPos(BlockPos pos)
    {
        FIXED_POS = pos;
    }

    public static Vec3d getPrevHitvec()
    {
        return PREV_HITVEC;
    }

    public static void setPrevHitvec(Vec3d prevHitvec)
    {
        PlaceTemplateMode.PREV_HITVEC = prevHitvec;
    }

    public static CursorVector getPrevCursorVec()
    {
        return PREV_CURSOR_VEC;
    }

    public static void setPrevCursorVec(CursorVector prevCursorVec)
    {
        PlaceTemplateMode.PREV_CURSOR_VEC = prevCursorVec;
    }

    public static void updateAlterModeInactive(BlockPos position, Vec3d hitVec)
    {
        if (!HOLD_PREVIEW)
        {
            setFixedPos(position);

        }
        PlaceTemplateMode.setPrevPos(position);
        PlaceTemplateMode.setPrevHitvec(hitVec);
        PlaceTemplateMode.setPrevCursorVec(ModEntity.currCursorVec);
        PlaceTemplateMode.setHeldAlt(false);
    }

    public static void toggleHoldPreview()
    {
        if (HOLD_PREVIEW)
        {
            int select_index = GuiTemplaceInventoryScreenFunctionality.selected_index;
            if (TemplateManager.TEMPLATES_LIST.size() > select_index && 0 <= select_index)
            {
                // display boundingboxes for each block in the template
                Template template = TemplateManager.TEMPLATES_LIST.get(select_index);

                //if you are pointing within the selection box of the template that point is used as the mouse attachment point
                BlockPos position2 = Minecraft.getMinecraft().player.getPosition();
                if (FIXED_POS != null)
                    position2 = FIXED_POS.add(MOUSE_X_OFFSET, MOUSE_Y_OFFSET, MOUSE_Z_OFFSET);
                BlockPos hitPos = getTemplateSelectionBoxHitPos(template, position2);
                if(hitPos != null)
                {
                    MOUSE_X_OFFSET = position2.getX() - hitPos.getX();
                    MOUSE_Z_OFFSET = position2.getZ() - hitPos.getZ();
                }
            }
            HOLD_PREVIEW = false;
            GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Attach Template To Mouse", false, false);

        }
        else
        {
            HOLD_PREVIEW = true;
            GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode(), "Hold Template In Place", false, false);

        }


    }

    private static BlockPos getTemplateSelectionBoxHitPos(Template template, BlockPos position2)
    {
        BlockPos corrPosition = template.getCorrectSelectionBoxPos(position2.add(template.getX_offset_template(), template.getY_offset_template(), template.getZ_offset_template()));
        BlockPos size = template.getSize();
        AxisAlignedBB selectionBox = new AxisAlignedBB(corrPosition, corrPosition.add(size.getX(), 0, size.getZ()));
        RayTraceResult raytraceresult = selectionBox.calculateIntercept(ModEntity.currCursorVec.start, ModEntity.currCursorVec.end);

        BlockPos hitPos = null;
        if (raytraceresult != null)
        {
            Vec3d hitvec = raytraceresult.hitVec;
            hitPos = new BlockPos(Math.floor(hitvec.x), Math.floor(hitvec.y), Math.floor(hitvec.z));
        }
        return hitPos;
    }

    public static int getCurrToolId()
    {
        return IDENTIFICATION_INDICES.get(FOUNDATION).get(AIR).get(WALL);
    }

}
