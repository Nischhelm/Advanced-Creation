package com.deadtiger.advcreation.build_mode.utility;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.block_blacklist.BlockBlackListManager;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.edit_mode.utility.EnumTerrainMode;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.utility.FakeWorld;
import com.deadtiger.advcreation.utility.PlacementHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.UUID;

import static com.deadtiger.advcreation.utility.PlacementHelper.isNotGroundMaterial;

public class HelpFunctions
{
    public static Vec3d Vec3iToVec3d(Vec3i vec1)
    {
        return new Vec3d(vec1.getX(), vec1.getY(), vec1.getZ());
    }

    public static TemplateBlock getTemplateBlockFromItemBed(RayTraceResult objectMouseOver, ItemStack itemStack, Vec3d hitVec, BlockPos position)
    {
        IBlockState handBlockState;
        handBlockState = Blocks.BED.getDefaultState().withProperty(BlockBed.OCCUPIED, false).withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);
        TileEntityBed tileEntityBed = new TileEntityBed();

        //code from ItemBed.onItemUse
        tileEntityBed.setItemValues(itemStack);


        if (objectMouseOver.sideHit.getAxis().isHorizontal())
            handBlockState = PlacementHelper.orientBasedOnSideHit(handBlockState, objectMouseOver.sideHit, hitVec);
        else
            handBlockState = PlacementHelper.orientBasedOnCamera(handBlockState);

        return new TemplateBlock(objectMouseOver.sideHit, position, handBlockState, tileEntityBed);
    }

    public static TemplateBlock getTemplateBlockFromItemDoor(RayTraceResult objectMouseOver, ItemDoor handStack, Vec3d hitVec, BlockPos position)
    {
        IBlockState handBlockState;
        handBlockState = handStack.block.getDefaultState().withProperty(BlockDoor.POWERED, false).withProperty(BlockDoor.OPEN, false);

//                          //Some code to change the blockState to be orientated based on the camera look or on the side hit
        if (objectMouseOver.sideHit.getAxis().isHorizontal())
            handBlockState = PlacementHelper.orientBasedOnSideHit(handBlockState, objectMouseOver.sideHit, hitVec);
        else
            handBlockState = PlacementHelper.orientBasedOnCamera(handBlockState);


        return new TemplateBlock(objectMouseOver.sideHit, position, handBlockState.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER));
    }

    public static TemplateBlock getTemplateBlockFromItemBlock(RayTraceResult objectMouseOver, EntityPlayer entityplayer, ItemBlock handStack, Vec3d hitVec, BlockPos position)
    {
        IBlockState handBlockState;
        int i = handStack.getMetadata(entityplayer.getHeldItemMainhand().getMetadata());
        handBlockState = handStack.getBlock().getStateForPlacement(entityplayer.world,
                objectMouseOver.getBlockPos(), objectMouseOver.sideHit,
                getHitCoord(hitVec.x), getHitCoord(hitVec.y), getHitCoord(hitVec.z),
                i, entityplayer, EnumHand.MAIN_HAND);


        IBlockState currState = entityplayer.world.getBlockState(position);
        if(isTheSameSlab(objectMouseOver.sideHit, handStack, i, currState) &&
                (handBlockState.getValue(BlockSlab.HALF) != currState.getValue(BlockSlab.HALF) ||
                        (currState.getValue( BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM && objectMouseOver.sideHit == EnumFacing.UP) ||
                        (currState.getValue( BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP && objectMouseOver.sideHit == EnumFacing.DOWN)))
        {
            handBlockState = getDoubleSlab((ItemSlab) handStack).getStateFromMeta(i);
        }


        //Some code to change the blockState to be orientated based on the camera look or on the side hit
        if (objectMouseOver.sideHit.getAxis().isHorizontal())
            handBlockState = PlacementHelper.orientBasedOnSideHit(handBlockState, objectMouseOver.sideHit, hitVec);
        else
        {
            //add redstone torch to this check should also work for redstone torches
            if (handBlockState.getBlock().equals(Blocks.TORCH) || handBlockState.getBlock().equals(Blocks.REDSTONE_TORCH))
                handBlockState = PlacementHelper.rotateToFace(handBlockState, objectMouseOver.sideHit);
            else
                handBlockState = PlacementHelper.orientBasedOnCamera(handBlockState);
        }

        return new TemplateBlock(objectMouseOver.sideHit, position, handBlockState, AdvCreation.proxy.createTileEntityOf(handBlockState));
    }

    public static boolean isTheSameSlab(EnumFacing hitface, Item handStack, int i, IBlockState currState)
    {
        return (currState.getBlock() instanceof BlockSlab &&
                handStack instanceof ItemSlab &&
                currState.getBlock() == ((ItemSlab) handStack).getBlock()
        ) &&
                (currState.getBlock().getMetaFromState(currState.withProperty(BlockSlab.HALF,BlockSlab.EnumBlockHalf.BOTTOM)) == i);
    }

    public static TemplateBlock getTemplateBlockFromItemBlockSpecial(RayTraceResult objectMouseOver, EntityPlayer entityplayer, ItemBlockSpecial handStack, Vec3d hitVec, BlockPos position)
    {
        IBlockState handBlockState;
        int i = handStack.getMetadata(entityplayer.getHeldItemMainhand().getMetadata());
        handBlockState = handStack.getBlock().getStateForPlacement(entityplayer.world,
                objectMouseOver.getBlockPos(), objectMouseOver.sideHit,
                getHitCoord(hitVec.x), getHitCoord(hitVec.y), getHitCoord(hitVec.z),
                i, entityplayer, EnumHand.MAIN_HAND);

        //Some code to change the blockState to be orientated based on the camera look or on the side hit
        if (objectMouseOver.sideHit.getAxis().isHorizontal())
            handBlockState = PlacementHelper.orientBasedOnSideHit(handBlockState, objectMouseOver.sideHit, hitVec);
        else
        {
            //add redstone torch to this check should also work for redstone torches
            if (handBlockState.getBlock().equals(Blocks.TORCH) || handBlockState.getBlock().equals(Blocks.REDSTONE_TORCH))
                handBlockState = PlacementHelper.rotateToFace(handBlockState, objectMouseOver.sideHit);
            else
                handBlockState = PlacementHelper.orientBasedOnCamera(handBlockState);
        }

        return new TemplateBlock(objectMouseOver.sideHit, position, handBlockState, AdvCreation.proxy.createTileEntityOf(handBlockState));
    }

    public static TemplateBlock getTemplateBlockFromItemSign(RayTraceResult objectMouseOver, EntityPlayer entityplayer, ItemStack handStack, Vec3d hitVec, BlockPos position)
    {
        EnumFacing facing = objectMouseOver.sideHit;
        World worldIn = entityplayer.world;
        BlockPos pos = objectMouseOver.getBlockPos();
        EntityPlayer player = entityplayer;
        IBlockState state;
        if (facing == EnumFacing.UP)
        {
            double angle = 0.0;
            if (ModEntity.currMouseVec != null)
                angle = -((Math.atan2(ModEntity.currMouseVec.x, ModEntity.currMouseVec.z) / Math.PI) * 180.0 - 180.0);
            int j = MathHelper.floor((double) ((angle) * 16.0F / 360.0F) + 0.5D) & 15;
            state = Blocks.STANDING_SIGN.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(j));

        }
        else if (facing != EnumFacing.DOWN)
        {

            state = Blocks.WALL_SIGN.getDefaultState().withProperty(BlockWallSign.FACING, facing);
            state = PlacementHelper.orientBasedOnSideHit(state, objectMouseOver.sideHit.getOpposite(), hitVec);
        }
        else
        {
            state = Blocks.AIR.getDefaultState();
        }

        //Some code to change the blockState to be orientated based on the camera look or on the side hit
//        if (objectMouseOver.sideHit.getAxis().isHorizontal())

//        else
//        {
//            if (state.getBlock().equals(Blocks.TORCH))
//                state = PlacementHelper.rotateToFace(state, objectMouseOver.sideHit);
//            else
//                state = PlacementHelper.orientBasedOnCamera(state);
//        }

        TileEntitySign tileEntitySign = (TileEntitySign) state.getBlock().createTileEntity(FakeWorld.INSTANCE.setPreviewBlockState(state), state);
        if (tileEntitySign != null)
        {
            tileEntitySign.setWorld(FakeWorld.INSTANCE);
            tileEntitySign.setEditable(true);
            tileEntitySign.setPlayer(entityplayer);
        }

        return new TemplateBlock(objectMouseOver.sideHit, position, state, tileEntitySign);
    }

    public static TemplateBlock getTemplateBlockFromItemSkull(RayTraceResult objectMouseOver, EntityPlayer entityplayer, ItemStack handStack, Vec3d hitVec, BlockPos position)
    {
        EnumFacing facing = objectMouseOver.sideHit;
        World worldIn = entityplayer.world;
        BlockPos pos = objectMouseOver.getBlockPos();
        EntityPlayer player = entityplayer;
        IBlockState state;
        TileEntity tileEntity = null;

        if (facing == EnumFacing.DOWN)
        {
            state = Blocks.AIR.getDefaultState();
        }
        else
        {

            if (player.canPlayerEdit(pos, facing, handStack))
            {
                state = Blocks.SKULL.getDefaultState().withProperty(BlockSkull.FACING, facing);
                int i = 0;

                if (facing == EnumFacing.UP)
                {
                    double angle = 0.0;
                    if (ModEntity.currMouseVec != null)
                        angle = -((Math.atan2(ModEntity.currMouseVec.x, ModEntity.currMouseVec.z) / Math.PI) * 180.0);

                    i = MathHelper.floor((double) ((angle) * 16.0F / 360.0F) + 0.5D) & 15;
                }


                tileEntity = state.getBlock().createTileEntity(worldIn, state);

                if (tileEntity instanceof TileEntitySkull)
                {
                    TileEntitySkull tileentityskull = (TileEntitySkull) tileEntity;

                    if (handStack.getMetadata() == 3)
                    {
                        GameProfile gameprofile = null;

                        if (handStack.hasTagCompound())
                        {
                            NBTTagCompound nbttagcompound = handStack.getTagCompound();

                            if (nbttagcompound.hasKey("SkullOwner", 10))
                            {
                                gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                            }
                            else if (nbttagcompound.hasKey("SkullOwner", 8) && !StringUtils.isBlank(nbttagcompound.getString("SkullOwner")))
                            {
                                gameprofile = new GameProfile((UUID) null, nbttagcompound.getString("SkullOwner"));
                            }
                        }

                        tileentityskull.setPlayerProfile(gameprofile);
                    }
                    else
                    {
                        tileentityskull.setType(handStack.getMetadata());
                    }

                    tileentityskull.setSkullRotation(i);
                    Blocks.SKULL.checkWitherSpawn(worldIn, pos, tileentityskull);
                    return new TemplateBlock(facing, position, state, tileentityskull);
                }

                return new TemplateBlock(facing, position, state, tileEntity);

            }
            else
            {
                state = Blocks.AIR.getDefaultState();
            }
        }

        return new TemplateBlock(objectMouseOver.sideHit, position, state, null);
    }

    public static TemplateBlock getTemplateBlockFromItemBanner(RayTraceResult objectMouseOver, EntityPlayer entityplayer, ItemStack handStack, Vec3d hitVec, BlockPos position)
    {
        EnumFacing facing = objectMouseOver.sideHit;
        World worldIn = entityplayer.world;
        BlockPos pos = objectMouseOver.getBlockPos();
        EntityPlayer player = entityplayer;
        IBlockState state = Blocks.AIR.getDefaultState();
        TileEntity tileEntity = null;

        //code from ItemBanner.onItemUse
        IBlockState iblockstate = worldIn.getBlockState(pos);
        boolean flag = iblockstate.getBlock().isReplaceable(worldIn, pos);

        if (facing != EnumFacing.DOWN && (iblockstate.getMaterial().isSolid() || flag) && (!flag || facing == EnumFacing.UP))
        {
            pos = pos.offset(facing);
            ItemStack itemstack = handStack;

            pos = flag ? pos.down() : pos;

            if (facing == EnumFacing.UP)
            {
                double angle = 0.0;
                if (ModEntity.currMouseVec != null)
                    angle = -((Math.atan2(ModEntity.currMouseVec.x, ModEntity.currMouseVec.z) / Math.PI) * 180.0);

                int i = MathHelper.floor((double) ((angle + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                state = Blocks.STANDING_BANNER.getDefaultState().withProperty(BlockBanner.ROTATION, Integer.valueOf(i));

            }
            else
            {
                state = Blocks.WALL_BANNER.getDefaultState().withProperty(BlockBanner.FACING, facing);
            }

            tileEntity = AdvCreation.proxy.createTileEntityOf(state);

            if (tileEntity instanceof TileEntityBanner)
            {
                ((TileEntityBanner) tileEntity).setItemValues(itemstack, false);
            }

        }


        return new TemplateBlock(facing, pos, state, tileEntity);

    }

    public static TemplateBlock getTemplateBlockFromItemRedstone(RayTraceResult objectMouseOver, EntityPlayer entityplayer, ItemStack handStack, Vec3d hitVec, BlockPos position)
    {
        EnumFacing facing = objectMouseOver.sideHit;
        BlockPos pos = objectMouseOver.getBlockPos();
        World worldIn = entityplayer.world;
        boolean flag = entityplayer.world.getBlockState(pos).getBlock().isReplaceable(entityplayer.world, pos);
        BlockPos blockpos = flag ? pos : pos.offset(facing);
        ItemStack itemstack = handStack;

        if (entityplayer.canPlayerEdit(blockpos, facing, itemstack) && worldIn.mayPlace(worldIn.getBlockState(blockpos).getBlock(), blockpos, false, facing, entityplayer) && Blocks.REDSTONE_WIRE.canPlaceBlockAt(worldIn, blockpos))
        {
//            worldIn.setBlockState(blockpos, Blocks.REDSTONE_WIRE.getDefaultState());

            return new TemplateBlock(facing,blockpos,Blocks.REDSTONE_WIRE.getDefaultState(),null);
        }
        else
        {
            return new TemplateBlock(facing,blockpos,Blocks.AIR.getDefaultState(),null);
        }
    }




    public static void editPlacedSign(ItemStack handStack, World worldIn, BlockPos pos, EntityPlayer player)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        boolean serverAllowsEdit = !ItemBlock.setTileEntityNBT(worldIn, player, pos, handStack);
        if (tileentity instanceof TileEntitySign && player instanceof EntityPlayerSP && serverAllowsEdit)
        {

            ((EntityPlayerSP) player).openEditSign((TileEntitySign)tileentity);
        }
    }

    public static float getHitCoord(double singleCoord)
    {
        float hitX = (float) Math.abs(singleCoord);
        hitX = hitX - (float) Math.floor(hitX);
        hitX = ((singleCoord > 0.0f) ? hitX : -hitX);
        return hitX;
    }

    /**d
     *
     * @return an 3D arraylist from minX to maxX and from minY to MaxY containing all TemplateBlock with the current XY value
     */
    public static ArrayList<ArrayList<ArrayList<TemplateBlock>>> getRowDirectionXZY(ArrayList<TemplateBlock> blockList)
    {
        ExtremaXYZ extrema = new ExtremaXYZ(blockList);
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> rowsXY = initialise3DArrayList( extrema.maxX, extrema.maxY, extrema.maxZ, extrema.minX, extrema.minY, extrema.minZ);

        //fill the ArrayLists with the appropriate templateBlocks
        for (TemplateBlock tempBlock: blockList)
        {
            rowsXY.get(tempBlock.getX_offset() -extrema.minX)
                    .get(tempBlock.getZ_offset() -extrema.minZ)
                    .set(tempBlock.getY_offset() -extrema.minY,tempBlock);
        }
        return rowsXY;
    }

    public static ArrayList<ArrayList<ArrayList<TemplateBlock>>> getRowDirectionXYZ(ArrayList<TemplateBlock> blockList)
    {
        ExtremaXYZ extrema = new ExtremaXYZ(blockList);
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> rowsXY = initialise3DArrayList(extrema.maxX, extrema.maxZ, extrema.maxY, extrema.minX, extrema.minZ, extrema.minY);

        //fill the ArrayLists with the appropriate templateBlocks
        for (TemplateBlock tempBlock: blockList)
        {
            rowsXY.get(tempBlock.getX_offset() -extrema.minX)
                    .get(tempBlock.getY_offset() -extrema.minY)
                    .set(tempBlock.getZ_offset() -extrema.minZ,tempBlock);
        }
        return rowsXY;
    }

    public static ArrayList<ArrayList<ArrayList<TemplateBlock>>> getRowDirectionYZX(ArrayList<TemplateBlock> blockList)
    {

        ExtremaXYZ extrema = new ExtremaXYZ(blockList);
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> rowsXY = initialise3DArrayList( extrema.maxY, extrema.maxX, extrema.maxZ, extrema.minY, extrema.minX, extrema.minZ);

        //fill the ArrayLists with the appropriate templateBlocks
        for (TemplateBlock tempBlock: blockList)
        {
            rowsXY.get(tempBlock.getY_offset() -extrema.minY)
                    .get(tempBlock.getZ_offset() -extrema.minZ)
                    .set(tempBlock.getX_offset() -extrema.minX,tempBlock);
        }
        return rowsXY;
    }

    public static ArrayList<ArrayList<ArrayList<TemplateBlock>>> initialise3DArrayList(int maxX, int maxY, int maxZ, int minX, int minY, int minZ)
    {
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> rowsXY = new ArrayList<ArrayList<ArrayList<TemplateBlock>>>();
        for (int i = 0; i <= maxX - minX; i++)
        {
            ArrayList<ArrayList<TemplateBlock>> rowZ = new ArrayList<ArrayList<TemplateBlock>>();
            for (int k = 0; k <= maxZ - minZ; k++)
            {
                ArrayList<TemplateBlock> rowY = new ArrayList<TemplateBlock>();
                for (int j = 0; j <= maxY - minY; j++)
                {
                    rowY.add(null);
                }
                rowZ.add(rowY);
            }
            rowsXY.add(rowZ);
        }
        return rowsXY;
    }

    public static Vec3d parseVec(Vec3d vec)
    {
        double newX = getNewSingleCoord(vec.x);
        double newZ = getNewSingleCoord(vec.z);
        double newY = getNewSingleCoord(vec.y);
        return new Vec3d(newX,newY,newZ);
    }

    protected static double getNewSingleCoord(double singleCoord) {
        double absX = Math.abs(singleCoord);
        double floorX = Math.floor(absX);
        double newX;

        //only middle of blocks
        newX = floorX + 0.5;

        if(singleCoord < 0)
            newX = -newX;
        return newX;
    }


    /**
     * Method used for pull to ground into multiple directions
     * @param list
     * @param face
     */
    public static void updateListToFirstSolidBlockInDir(ArrayList<TemplateBlock> list, EnumFacing face)
    {
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> rows = null;
        //get the currtoolBlock is 3D matrix format
        if(face.equals(EnumFacing.NORTH) || face.equals(EnumFacing.SOUTH))
            rows = getRowDirectionXYZ(list);
        if(face.equals(EnumFacing.EAST) || face.equals(EnumFacing.WEST))
            rows = getRowDirectionYZX(list);
        if(face.equals(EnumFacing.UP) || face.equals(EnumFacing.DOWN))
            rows = getRowDirectionXZY(list);

        for (ArrayList<ArrayList<TemplateBlock>> rowX: rows)
        {
            for (ArrayList<TemplateBlock> rowZ: rowX)
            {

                //if the row was not empty
                if( rowZ.get(0)!= null)
                {
                    BlockPos worldPos = getStartingWorldPos(face, rowZ, rowZ.size());

                    Minecraft mc = Minecraft.getMinecraft();
                    IBlockState iblockstate2 = mc.world.getBlockState(worldPos);//this block needs to be solid

                    int maxTravel = 50;
                    int heightCount= 0;
                    //go up till the lowest block is right above the surface
                    while(isNotGroundMaterial(iblockstate2) && heightCount < (maxTravel))
                    {
                        worldPos = movePosInFaceDir(face, worldPos);
                        iblockstate2 = mc.world.getBlockState(worldPos);
                        heightCount++;
                    }

                    //substract the count of blocks from all valid blocks in this row
                    for (TemplateBlock block: rowZ)
                    {
                        if(block !=null)
                        {
                            moveBlockInFaceDir(face, heightCount, block);
                        }

                    }
                }
            }

        }
    }

    public static void updateListToAirBlockAboveFirstSolidBlockInDir(ArrayList<TemplateBlock> list, EnumFacing face, int maxTravel)
    {
        updateListToAirBlockAboveFirstSolidBlockInDir(list, face, maxTravel,false);
    }

    /**
     * Method used for pull to ground into multiple EnumFacings
     * @param list
     * @param face
     * @param maxTravel
     */
    public static void updateListToAirBlockAboveFirstSolidBlockInDir(ArrayList<TemplateBlock> list, EnumFacing face, int maxTravel, boolean moveUp)
    {
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> rows = null;
        //get the currtoolBlock is 3D matrix format
        if(face.equals(EnumFacing.NORTH) || face.equals(EnumFacing.SOUTH))
            rows = getRowDirectionXYZ(list);
        if(face.equals(EnumFacing.EAST) || face.equals(EnumFacing.WEST))
            rows = getRowDirectionYZX(list);
        if(face.equals(EnumFacing.UP) || face.equals(EnumFacing.DOWN))
            rows = getRowDirectionXZY(list);

        for (ArrayList<ArrayList<TemplateBlock>> rowX: rows)
        {
            for (ArrayList<TemplateBlock> rowZ: rowX)
            {

                //if the row was not empty
                if( rowZ.get(0)!= null)
                {
                    //get the coordinates from the lowest block for when you are building something like a wall with blocks stacked
                    BlockPos worldPos = rowZ.get(0).getBlockPos();
                    if(moveUp)
                        worldPos = getStartingWorldPos(face.getOpposite(), rowZ,maxTravel/2);

                    Minecraft mc = Minecraft.getMinecraft();
                    IBlockState iblockstate2 = mc.world.getBlockState(worldPos);//this block needs to be solid

                    IBlockState iblockstate3 = mc.world.getBlockState(worldPos);//the block on top of it needs to be air

                    if ( face.equals(EnumFacing.SOUTH))
                        iblockstate3 = mc.world.getBlockState(worldPos.south());
                    else if (face.equals(EnumFacing.NORTH))
                        iblockstate3 = mc.world.getBlockState(worldPos.north());
                    else if (face.equals(EnumFacing.EAST))
                        iblockstate3 = mc.world.getBlockState(worldPos.east());
                    else if (face.equals(EnumFacing.WEST))
                        iblockstate3 = mc.world.getBlockState(worldPos.west());
                    else if (face.equals(EnumFacing.UP) )
                        iblockstate3 = mc.world.getBlockState(worldPos.up());
                    else if (face.equals(EnumFacing.DOWN))
                        iblockstate3 = mc.world.getBlockState(worldPos.down());

                    int heightCount= 0;
                    //go up till the lowest block is right above the surface
                    while(!(!isNotGroundMaterial(iblockstate2) && isNotGroundMaterial(iblockstate3)) && heightCount < (maxTravel))
                    {
                        worldPos = movePosInFaceDir(face, worldPos);
                        iblockstate2 = mc.world.getBlockState(worldPos);

                        if ( face.equals(EnumFacing.SOUTH))
                            iblockstate3 = mc.world.getBlockState(worldPos.south());
                        else if (face.equals(EnumFacing.NORTH))
                            iblockstate3 = mc.world.getBlockState(worldPos.north());
                        else if (face.equals(EnumFacing.EAST))
                            iblockstate3 = mc.world.getBlockState(worldPos.east());
                        else if (face.equals(EnumFacing.WEST))
                            iblockstate3 = mc.world.getBlockState(worldPos.west());
                        else if (face.equals(EnumFacing.UP) )
                            iblockstate3 = mc.world.getBlockState(worldPos.up());
                        else if (face.equals(EnumFacing.DOWN))
                            iblockstate3 = mc.world.getBlockState(worldPos.down());

                        heightCount++;
                    }




                    int newHeightcount = 0;
                    worldPos = rowZ.get(0).getBlockPos();
                    iblockstate2 = mc.world.getBlockState(worldPos);//this block needs to be solid
                    iblockstate3 = mc.world.getBlockState(worldPos);//the block on top of it needs to be air

                    //keep going down while the block is made from an non-ground material
                    while(!(!isNotGroundMaterial(iblockstate2) && isNotGroundMaterial(iblockstate3)) && newHeightcount > (-maxTravel))
                    {
                        worldPos = movePosInFaceDir(face.getOpposite(), worldPos);
                        iblockstate2 = mc.world.getBlockState(worldPos);

                        if ( face.equals(EnumFacing.SOUTH))
                            iblockstate3 = mc.world.getBlockState(worldPos.south());
                        else if (face.equals(EnumFacing.NORTH))
                            iblockstate3 = mc.world.getBlockState(worldPos.north());
                        else if (face.equals(EnumFacing.EAST))
                            iblockstate3 = mc.world.getBlockState(worldPos.east());
                        else if (face.equals(EnumFacing.WEST))
                            iblockstate3 = mc.world.getBlockState(worldPos.west());
                        else if (face.equals(EnumFacing.UP) )
                            iblockstate3 = mc.world.getBlockState(worldPos.up());
                        else if (face.equals(EnumFacing.DOWN))
                            iblockstate3 = mc.world.getBlockState(worldPos.down());

                        newHeightcount--;
                    }


                    if(heightCount > Math.abs(newHeightcount))
                        heightCount = newHeightcount;



                    //substract the count of blocks from all valid blocks in this row
                    for (TemplateBlock block: rowZ)
                    {
                        if(block !=null)
                        {
                            moveBlockInFaceDir(face, heightCount, block);
                        }

                    }
                }
            }

        }
    }





    private static void moveBlockInFaceDir(EnumFacing face, int heightCount, TemplateBlock block)
    {
        if ( face.equals(EnumFacing.SOUTH))
            block.setZ_offset(block.getZ_offset() + heightCount);
        else if (face.equals(EnumFacing.NORTH))
            block.setZ_offset(block.getZ_offset() - heightCount);
        else if (face.equals(EnumFacing.EAST))
            block.setX_offset(block.getX_offset() + heightCount);
        else if (face.equals(EnumFacing.WEST))
            block.setX_offset(block.getX_offset() - heightCount);
        else if (face.equals(EnumFacing.UP) )
            block.setY_offset(block.getY_offset() + heightCount);
        else if (face.equals(EnumFacing.DOWN))
            block.setY_offset(block.getY_offset() - heightCount);
    }

    private static BlockPos movePosInFaceDir(EnumFacing face, BlockPos worldPos)
    {
        if ( face.equals(EnumFacing.SOUTH))
            worldPos = worldPos.south();
        else if (face.equals(EnumFacing.NORTH))
            worldPos = worldPos.north();
        else if (face.equals(EnumFacing.EAST))
            worldPos = worldPos.east();
        else if (face.equals(EnumFacing.WEST))
            worldPos = worldPos.west();
        else if (face.equals(EnumFacing.UP) )
            worldPos = worldPos.up();
        else if (face.equals(EnumFacing.DOWN))
            worldPos = worldPos.down();
        return worldPos;
    }

    private static BlockPos getStartingWorldPos(EnumFacing face, ArrayList<TemplateBlock> rowZ, int distance)
    {
        BlockPos currPos = rowZ.get(0).getBlockPos();
        //start from the lowest block and count how many block to the ground
        BlockPos worldPos = null;
        int maxY = distance;
        if ( face.equals(EnumFacing.SOUTH))
            worldPos = new BlockPos(currPos).add(0,0,maxY);
        else if (face.equals(EnumFacing.NORTH))
            worldPos = new BlockPos(currPos).add(0,0,-maxY);
        else if (face.equals(EnumFacing.EAST))
            worldPos = new BlockPos(currPos).add(maxY,0,0);
        else if (face.equals(EnumFacing.WEST))
            worldPos = new BlockPos(currPos).add(-maxY,0,0);
        else if (face.equals(EnumFacing.UP) )
            worldPos = new BlockPos(currPos).add(0,maxY,0);
        else if (face.equals(EnumFacing.DOWN))
            worldPos = new BlockPos(currPos).add(0,-maxY,0);
        return worldPos;
    }

    public static void formatNewBlockAndAddToBuildMode(RayTraceResult objectMouseOver, EntityPlayer entityplayer, Item handStack, Vec3d hitVec, BlockPos position, boolean rightclick)
    {
        formatNewBlockAndAddToBuildMode(objectMouseOver,entityplayer,handStack,hitVec,position,rightclick,false);
    }

    public static void formatNewBlockAndAddToBuildMode(RayTraceResult objectMouseOver, EntityPlayer entityplayer, Item handStack, Vec3d hitVec, BlockPos position, boolean rightclick, boolean bothHitAndHandAreSlab)
    {
        TemplateBlock block = new TemplateBlock(objectMouseOver.sideHit ,objectMouseOver.getBlockPos(),Blocks.AIR.getDefaultState());
        boolean valid = false;

        boolean blackListed = BlockBlackListManager.IsOnBlackList(handStack);
        if(blackListed)
            GuiOverlayManager.announceBlacklistMatch();

        if(rightclick && !blackListed)
        {
            if (handStack instanceof ItemBanner)
            {
                block = getTemplateBlockFromItemBanner(objectMouseOver, entityplayer, entityplayer.getHeldItemMainhand(), hitVec, position);
                valid = true;
            }
            else if (handStack instanceof ItemBlock && !(((ItemBlock) handStack).getBlock() instanceof BlockTripWire ))
            {
                block = getTemplateBlockFromItemBlock(objectMouseOver, entityplayer, (ItemBlock) handStack, hitVec, position);
                valid = true;
            }
            else if ( handStack instanceof ItemBlockSpecial)
            {
                block = getTemplateBlockFromItemBlockSpecial(objectMouseOver, entityplayer, (ItemBlockSpecial) handStack, hitVec, position);
                valid = true;
            }
            else if (handStack instanceof ItemRedstone)
            {
                block = getTemplateBlockFromItemRedstone(objectMouseOver, entityplayer, entityplayer.getHeldItemMainhand(), hitVec, position);
                valid = true;
            }
            else if (handStack instanceof ItemDoor)
            {
                block = getTemplateBlockFromItemDoor(objectMouseOver, (ItemDoor) handStack, hitVec, position);
                valid = true;
            }
            else if (handStack instanceof ItemBed)
            {
                block = getTemplateBlockFromItemBed(objectMouseOver, entityplayer.getHeldItemMainhand(), hitVec, position);
                valid = true;
            }
            else if (handStack instanceof ItemSign)
            {
                block = getTemplateBlockFromItemSign(objectMouseOver, entityplayer, entityplayer.getHeldItemMainhand(), hitVec, position);
                valid = true;
            }
            else if (handStack instanceof ItemSkull)
            {
                block = getTemplateBlockFromItemSkull(objectMouseOver, entityplayer, entityplayer.getHeldItemMainhand(), hitVec, position);
                valid = true;
            }
//            else if (handStack instanceof Item)
//            {
//                block = getTemplateBlockFromItemSkull(objectMouseOver, entityplayer, entityplayer.getHeldItemMainhand(), hitVec, position);
//                valid = true;
//            }
        }
        else
            valid = true;

        IBlockState currState = entityplayer.world.getBlockState(objectMouseOver.getBlockPos());
        AxisAlignedBB boundingBox = currState.getBlock().getBoundingBox(currState,entityplayer.getEntityWorld(),objectMouseOver.getBlockPos());
        EnumFacing hitFace = objectMouseOver.sideHit;
        double currHitblockWidth = 1.0;
        if(hitFace.getAxis() == EnumFacing.Axis.X)
            currHitblockWidth = MathHelper.absMax(boundingBox.maxX,boundingBox.minX) ;
        else if(hitFace.getAxis() == EnumFacing.Axis.Y)
            currHitblockWidth = MathHelper.absMax(boundingBox.maxY,boundingBox.minY);
        else if(hitFace.getAxis() == EnumFacing.Axis.Z)
            currHitblockWidth = MathHelper.absMax(boundingBox.maxZ,boundingBox.minZ);


        if(currHitblockWidth > 1000.0 || currHitblockWidth < 0)
            currHitblockWidth = 0.5;

        if(valid)
            BuildMode.addNewBlock(block, hitVec, objectMouseOver.getBlockPos(), objectMouseOver.sideHit,currHitblockWidth, bothHitAndHandAreSlab);
        else
            BuildMode.addNewBlock(new TemplateBlock(objectMouseOver.sideHit, position, Blocks.AIR.getDefaultState()), hitVec, objectMouseOver.getBlockPos(), objectMouseOver.sideHit,currHitblockWidth, bothHitAndHandAreSlab);
    }

    public static boolean stateIsAllowed(IBlockState state, EnumTerrainMode onlyTerrainMode)
    {
        if(state == null)
            return false;
        if(onlyTerrainMode == null || onlyTerrainMode == EnumTerrainMode.ALl)
            return true;
        else
        {
            Material stateMaterial = state.getMaterial();
            Block block = state.getBlock();
            String blockName = block.getUnlocalizedName();

            if((stateMaterial == Material.SAND || stateMaterial == Material.ROCK || stateMaterial == Material.GROUND ||stateMaterial == Material.GRASS ||stateMaterial == Material.CLAY) &&
                    !(blockName.contains("polish") || blockName.contains("cut") || blockName.contains("cobble") ||
                            blockName.contains("brick") || blockName.contains("stair")|| blockName.contains("slab")||
                            blockName.contains("smooth") || blockName.contains("pillar") || blockName.contains("concrete") ||
                            blockName.contains("glazed") ) &&
                    !(block instanceof BlockContainer))
                return true;

        }
        return false;
    }

    public static boolean isSameBlockType(IBlockState state, IBlockState state2)
    {
        return isSameBlockType(state,state2.getBlock());
    }

    public static boolean isSameBlockType(IBlockState state, Block block)
    {
        return isSameBlockType(state.getBlock(),block);
    }

    public static boolean isSameBlockType(Block block1, Block block2)
    {
        return block1.getDefaultState().equals(block2.getDefaultState());
    }

    public static Block getDoubleSlab(ItemSlab itemSlab)
    {
        return ((Block) ObfuscationReflectionHelper.getPrivateValue(ItemSlab.class,itemSlab,"field_179226_c"));
    }

}
