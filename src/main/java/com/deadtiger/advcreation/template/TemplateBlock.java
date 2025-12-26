package com.deadtiger.advcreation.template;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.utility.TileEntityPlacementHelper;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.InvocationTargetException;

import static net.minecraft.util.EnumFacing.*;

public class TemplateBlock
{
    private EnumFacing face;
    private int X_offset;
    private int Y_offset;
    private int Z_offset;
    private IBlockState blockState;
    private TileEntity tileEntity; //added in alpha1.6
    private boolean enclosed;

    private boolean placedOnSide; //added in alpha1.6 currently only used for BlockSkull
    //the above variable is necessary because the TileEntitySkull holds a rotation int for when it is on the ground
    // and an EnumFace for when its on the a side. It doesn't say whether the current TileEntity is placed on the ground or on a side

    public enum EnumBlockType
    { INSIDE,OUTSIDE,WALL,FURNITURE,PLANT}
    
    EnumBlockType type = EnumBlockType.INSIDE;
    

    public TemplateBlock()
    {
        this(NORTH,0,0,0,(new BlockStone()).getDefaultState(),EnumBlockType.INSIDE,null);
    }
    
    public TemplateBlock(EnumFacing face, BlockPos blockPos, IBlockState blockState)
    {
        this(face,blockPos.getX(),blockPos.getY(),blockPos.getZ(),blockState,EnumBlockType.INSIDE,null);
    }

    public TemplateBlock(EnumFacing face, BlockPos blockPos, IBlockState blockState, TileEntity tileEntity)
    {
        this(face,blockPos.getX(),blockPos.getY(),blockPos.getZ(),blockState,EnumBlockType.INSIDE,false,tileEntity);
    }

    public TemplateBlock(EnumFacing face, BlockPos blockPos, IBlockState blockState, TileEntity tileEntity, EnumBlockType type)
    {
        this(face,blockPos.getX(),blockPos.getY(),blockPos.getZ(),blockState,type,false,tileEntity);
    }

    public TemplateBlock(EnumFacing face, int x_offset, int y_offset, int z_offset, IBlockState blockState)
    {
        this(face,x_offset,y_offset,z_offset,blockState,EnumBlockType.INSIDE,null);
    }

    public TemplateBlock(EnumFacing face, int x_offset, int y_offset, int z_offset, IBlockState blockState,TileEntity tileEntity)
    {
        this(face,x_offset,y_offset,z_offset,blockState,EnumBlockType.INSIDE,tileEntity);
    }
    
    public TemplateBlock(EnumFacing face, int x_offset, int y_offset, int z_offset, IBlockState blockState, EnumBlockType type,TileEntity tileEntity)
    {
        this(face,x_offset,y_offset,z_offset,blockState,type,false,tileEntity);
    }

    public TemplateBlock(EnumFacing face, int x_offset, int y_offset, int z_offset, IBlockState blockState, EnumBlockType type, boolean enclosed,TileEntity tileEntity)
    {
        this.face = face;
        X_offset = x_offset;
        Y_offset = y_offset;
        Z_offset = z_offset;
        this.blockState = blockState;
        this.type = type;
        this.enclosed = enclosed;
        if(tileEntity == null)
            this.tileEntity = tileEntity;
        else
        {
            try
            {
                this.tileEntity = AdvCreation.proxy.createTileEntityOf(blockState);
                TileEntityPlacementHelper.formatTileEntityForBlock(blockState,this.tileEntity,tileEntity,null,null);

            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }

    }
    
    public TemplateBlock(EnumFacing face, int x_offset, int y_offset, int z_offset, IBlockState blockState, EnumBlockType type, boolean enclosed)
    {
        this(face,x_offset,y_offset,z_offset,blockState,type,enclosed,null);
    }

    public TemplateBlock(EnumFacing face, int x_offset, int y_offset, int z_offset, Block block)
    {
        this(face,x_offset,y_offset,z_offset, block.getDefaultState(),EnumBlockType.INSIDE,null);
    }
    
    public TemplateBlock(TemplateBlock templateBlock)
    {
        this(templateBlock.getFace(),templateBlock.getX_offset(),templateBlock.getY_offset(),templateBlock.getZ_offset(), templateBlock.getBlockState(),templateBlock.getType(), templateBlock.isEnclosed());
    }

    public EnumFacing getFace() {
        return face;
    }
    
    public void setFace(EnumFacing face) {
        this.face = face;
    }
    
    public int getX_offset() {
        return X_offset;
    }
    
    public void setX_offset(int x_offset) {
        X_offset = x_offset;
    }
    
    public int getY_offset() {
        return Y_offset;
    }
    
    public void setY_offset(int y_offset) {
        Y_offset = y_offset;
    }
    
    public int getZ_offset() {
        return Z_offset;
    }
    
    public void setZ_offset(int z_offset) {
        Z_offset = z_offset;
    }
    
    public BlockPos getBlockPos()
    {
        return new BlockPos(getX_offset(),getY_offset(),getZ_offset());
    }
    
    public void setBlockPos(BlockPos pos)
    {
        setX_offset(pos.getX());
        setY_offset(pos.getY());
        setZ_offset(pos.getZ());
    }
    
    public TemplateBlock add_offset(int x, int y, int z)
    {
        setX_offset(getX_offset() + x);
        setY_offset(getY_offset() + y);
        setZ_offset(getZ_offset() + z);
        
        return this;
    }
    
    public IBlockState getBlockState() {
        return blockState;
    }
    
    public void setBlockState(IBlockState blockState) {
        this.blockState = blockState;
    }

    public EnumBlockType getType() {
        return type;
    }
    
    public void setType(EnumBlockType type) {
        this.type = type;
    }
    
    public void rotateOrientation()
    {
        for(IProperty property: blockState.getPropertyKeys())
        {
            if(property ==  BlockHorizontal.FACING)
            {
                try
                {
                    EnumFacing face = blockState.getValue(BlockHorizontal.FACING);
                    if(!face.equals(UP) && !face.equals(DOWN) && !(blockState.getBlock() instanceof BlockLog))
                    {
                        IBlockState newBlockState = blockState.withProperty(BlockHorizontal.FACING,rotateY_opposite(blockState.getValue(BlockHorizontal.FACING)));
                        setBlockState(newBlockState);
                    }

                }
                catch(Exception e)
                {
                    System.out.println("exception catch " + e);
                }

            }
            else if(property instanceof PropertyDirection)
            {
                try
                {
                    PropertyDirection propertyDir = (PropertyDirection) property;
                    EnumFacing face = blockState.getValue(propertyDir);
                    if(!face.equals(UP) && !face.equals(DOWN) && !(blockState.getBlock() instanceof BlockLog))
                    {
                        IBlockState newBlockState = blockState.withProperty(propertyDir,rotateY_opposite(blockState.getValue(propertyDir)));
                        setBlockState(newBlockState);
                    }
                
                }
                catch(Exception e)
                {
                    System.out.println("exception catch " + e);
                }
            
            }
            else if(blockState.getValue(property) instanceof BlockLog.EnumAxis)
            {
                //special case when using a log it doesn't not have the PropertyDirection property it has in stead an axis property
                BlockLog.EnumAxis axis = (BlockLog.EnumAxis)blockState.getValue(property);

                IBlockState newBlockState = null;
                switch(axis)
                {
                
                    case X:
                        newBlockState = blockState.withProperty(property,BlockLog.EnumAxis.Z);
                        setBlockState(newBlockState);
                        break;
                    case Z:
                        newBlockState = blockState.withProperty(property,BlockLog.EnumAxis.X);
                        setBlockState(newBlockState);
                        break;
                }
            
            }
            if(property instanceof PropertyInteger && property.getName().equals("rotation"))
            {
                // some blocks like standing signs have an Integer property that keeps track of the rotation
                // these blocks can have more horizontal rotations than the 4 compas directions

                PropertyInteger propertyRotInt = (PropertyInteger) property;
                int rotation = blockState.getValue(propertyRotInt);
                int oldRotation = new Integer(rotation);
                rotation += 1;
                if(rotation > 15)
                    rotation = 0;

                IBlockState newBlockState = blockState.withProperty(propertyRotInt,rotation);
//                System.out.println("old " + oldRotation + " new " +rotation);
                setBlockState(newBlockState);
            }
        
        }

        // some blocks contain tileentities that store rotation information BlockSkull is a good example
        if(blockState.getBlock() instanceof BlockSkull && tileEntity != null )
        {
            TileEntitySkull tileEntitySkull = (TileEntitySkull) tileEntity;
            tileEntitySkull.setSkullRotation(tileEntitySkull.getSkullRotation()-1);
        }

        if(blockState.getBlock() instanceof ITileEntityProvider)
        {
            if(this.tileEntity == null)
            {
                this.tileEntity = AdvCreation.proxy.createTileEntityOf(this.blockState);
                Object[] properties = blockState.getPropertyKeys().toArray();
            }
        }


        if(this.getFace().getAxis() != EnumFacing.Axis.Y)
            this.setFace(this.getFace().rotateYCCW());

    }

    public void rotateOrientation90Degree()
    {
        for(IProperty property: blockState.getPropertyKeys())
        {
            if(property instanceof PropertyDirection)
            {
                try
                {
                    PropertyDirection propertyDir = (PropertyDirection) property;
                    EnumFacing face = blockState.getValue(propertyDir);
                    if(!face.equals(UP) && !face.equals(DOWN) && !(blockState.getBlock() instanceof BlockLog))
                    {
                        IBlockState newBlockState = blockState.withProperty(propertyDir,rotateY_opposite(blockState.getValue(propertyDir)));
                        setBlockState(newBlockState);
                    }

                }
                catch(Exception e)
                {
                    System.out.println("exception catch " + e);
                }

            }
            else if(blockState.getValue(property) instanceof BlockLog.EnumAxis)
            {
                //special case when using a log it doesn't not have the PropertyDirection property it has in stead an axis property
                BlockLog.EnumAxis axis = (BlockLog.EnumAxis)blockState.getValue(property);

                IBlockState newBlockState = null;
                switch(axis)
                {

                    case X:
                        newBlockState = blockState.withProperty(property,BlockLog.EnumAxis.Z);
                        setBlockState(newBlockState);
                        break;
                    case Z:
                        newBlockState = blockState.withProperty(property,BlockLog.EnumAxis.X);
                        setBlockState(newBlockState);
                        break;
                }

            }
            if(property instanceof PropertyInteger && property.getName().equals("rotation"))
            {
                // some blocks like standing signs have an Integer property that keeps track of the rotation
                // these blocks can have more horizontal rotations than the 4 compas directions

                PropertyInteger propertyRotInt = (PropertyInteger) property;
                int rotation = blockState.getValue(propertyRotInt);
                int oldRotation = new Integer(rotation);
                rotation += 4;
                if(rotation < 0)
                    rotation = 16 +rotation;
                if(rotation > 15)
                    rotation = rotation-16;

                IBlockState newBlockState = blockState.withProperty(propertyRotInt,rotation);
                setBlockState(newBlockState);
            }

        }

        // some blocks contain tileentities that store rotation information BlockSkull is a good example
        if(blockState.getBlock() instanceof BlockSkull && blockState.getValue(BlockSkull.FACING) == UP && tileEntity != null )
        {
            TileEntitySkull tileEntitySkull = (TileEntitySkull) tileEntity;
            int newRotation = tileEntitySkull.getSkullRotation()-4;
            if(newRotation < 0)
                newRotation = 16 +newRotation;
            else if(newRotation > 15)
                newRotation = newRotation -16;

            tileEntitySkull.setSkullRotation(newRotation);
        }

        if(blockState.getBlock() instanceof ITileEntityProvider)
        {
            if(this.tileEntity == null)
            {
                this.tileEntity = AdvCreation.proxy.createTileEntityOf(this.blockState);
                Object[] properties = blockState.getPropertyKeys().toArray();
            }

        }

        if(this.getFace().getAxis() != EnumFacing.Axis.Y)
            this.setFace(this.getFace().rotateYCCW());

    }

    public EnumFacing getBlockOrientation()
    {
        EnumFacing res = NORTH;
        for(IProperty property: blockState.getPropertyKeys())
        {
            if(property instanceof PropertyDirection)
            {
                try
                {
                    PropertyDirection propertyDir = (PropertyDirection) property;
                    res = blockState.getValue(propertyDir);

                }
                catch(Exception e)
                {
                    System.out.println("exception catch " + e);
                }

            }
            else if(blockState.getValue(property) instanceof BlockLog.EnumAxis)
            {
                //special case when using a log it doesn't not have the PropertyDirection property it has in stead an axis property
                BlockLog.EnumAxis axis = (BlockLog.EnumAxis)blockState.getValue(property);

                IBlockState newBlockState = null;
                switch(axis)
                {
                    case X:
                        res = WEST;
                        break;
                    case Z:
                        res = NORTH;
                        break;
                }
            }
        }
        if(blockState.getBlock() instanceof BlockSkull)
        {
            TileEntitySkull tileEntitySkull = (TileEntitySkull) tileEntity;
            if(getFace() == UP)
            {
                if((tileEntitySkull.getSkullRotation() >= 15) && (tileEntitySkull.getSkullRotation() <= 1) )
                    res = NORTH;
                else if((tileEntitySkull.getSkullRotation() >= 2) && (tileEntitySkull.getSkullRotation() <= 6))
                    res = EAST;
                else if((tileEntitySkull.getSkullRotation() >= 7) && (tileEntitySkull.getSkullRotation() <= 9))
                    res = SOUTH;
                else if((tileEntitySkull.getSkullRotation() >= 10) && (tileEntitySkull.getSkullRotation() <= 14))
                    res = WEST;
            }
        }

        return res;
    }
    
    protected EnumFacing rotateY_opposite(EnumFacing facing)
    {
        switch (facing)
        {
            case NORTH:
                return WEST;
            case WEST:
                return SOUTH;
            case SOUTH:
                return EAST;
            case EAST:
                return NORTH;
            default:
                throw new IllegalStateException("Unable to get ZY-rotated facing of " + this);
        }
    }

    public ItemStack getItem()
    {
        int i = 0;
        ItemStack stack = new ItemStack(this.getBlockState().getBlock());

//        if(this.getBlockState().getBlock() instanceof BlockRedstoneRepeater)
//            System.out.println("hey");

//        if((stack.isEmpty() && this.getBlockState().getBlock() != Blocks.AIR)
//        {
//            if (tileEntity != null)
//            {
                if( tileEntity instanceof TileEntitySkull)
                {
                    i = ((TileEntitySkull)tileEntity).getSkullType();
                    stack = new ItemStack(Items.SKULL, 1, i);
                }
                else if(tileEntity instanceof TileEntitySign)
                {
                    stack = new ItemStack(Items.SIGN);
                }
                else if(tileEntity instanceof TileEntityBanner)
                {
                    stack = ((TileEntityBanner) this.getTileEntity()).getItem();
                }
                else if(blockState.getBlock() instanceof BlockRedstoneWire)
                {
                    stack = new ItemStack(Items.REDSTONE);
                }

//            }
//        }
        if(stack.isEmpty())
        {
            try
            {
                stack = this.getBlockState().getBlock().getItem(null,null,this.getBlockState());
            }
            catch(Exception e)
            {
                System.out.println(e);
            }

        }

        return stack;
    }


    public boolean isEnclosed() {
        return enclosed;
    }
    
    public void setEnclosed(boolean enclosed) {
        this.enclosed = enclosed;
    }

    public TileEntity getTileEntity()
    {
        return tileEntity;
    }

    public void setTileEntity(TileEntity tileEntity)
    {
        this.tileEntity = tileEntity;
    }

    public TemplateBlock createCopyWith(BlockPos blockPos)
    {
        return new TemplateBlock(this.getFace(),blockPos,this.getBlockState(),this.getTileEntity());
    }


}
