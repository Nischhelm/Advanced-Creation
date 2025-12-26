package com.deadtiger.advcreation.build_mode.tool_mode;

import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.place_template.PlaceTemplateMode;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.undo_actions.Action;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SingleToolMode extends BaseToolMode
{
    
    private static TemplateBlock StartDeleteBlock;
    private static TemplateBlock StartDeleteBlock2;

    private static boolean hitTopOfBlock;
    
    public SingleToolMode()
    {
        this.finalRightClick = 0;
        this.toolModeName = "Single Mode";
        this.buttonText = "SINGLE";
        this.tooltipText = "Draw/Delete Single";
        this.identificationIndex = 1;
    }

    @Override
    public int addToRightClickNumber() {
        return 1;
    }
    
    @Override
    public void addNewBlockRightClick0(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face) {
//        super.addNewBlockRightClick0(block, hitVec, hitBlockPos, face);
        addNewStartBlock(block,hitVec,face, BuildMode.CURR_HITBLOCK_WIDTH);

        //Always keep an instance of the position that has to be deleted when you leftclick
//        setStartDeleteBlock(new TemplateBlock(face,block.getBlockPos().subtract(face.getDirectionVec()).add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET), Blocks.AIR.getDefaultState()));
        BuildMode.updateCurrToolBlocks(block,hitVec);

        BlockPos deleteBlockPos = BuildMode.START_BLOCK.getBlockPos().subtract(face.getDirectionVec());
        if(BuildMode.isUsingAbsCoord() || BuildMode.BOTH_HIT_AND_HAND_ARE_SLAB)
            deleteBlockPos = block.getBlockPos();

        setStartDeleteBlock(new TemplateBlock(face,deleteBlockPos, Blocks.AIR.getDefaultState()));


        if(BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockDoor)
//            setStartDeleteBlock2(new TemplateBlock(face,block.getBlockPos().subtract(face.getDirectionVec()).up().add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET), Blocks.AIR.getDefaultState()));
            setStartDeleteBlock2(new TemplateBlock(face,deleteBlockPos.up(), Blocks.AIR.getDefaultState()));

        if(BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockChest || BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockEnderChest)
        {
            for (IProperty prop: BuildMode.START_BLOCK.getBlockState().getPropertyKeys())
            {
                if(prop instanceof PropertyDirection)
                {
                    BuildMode.START_BLOCK.setBlockState(BuildMode.START_BLOCK.getBlockState().withProperty( prop,((EnumFacing)BuildMode.START_BLOCK.getBlockState().getValue(prop)).getOpposite()));
                }
            }
        }
        GuiOverlayManager.setBlockCount(1,1, 1, 1);
        GuiOverlayManager.setOffsetPos(new BlockPos(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET));
//        BuildMode.updateCurrToolBlocks(block,hitVec);
    }

    @Override
    public void addNewBlockRightClick1(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {

    }

    @Override
    public void updateBlocksRightClick0(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec)
    {
        IBlockState state = BuildMode.START_BLOCK.getBlockState();


        if(state.getBlock() instanceof BlockDoor)
        {
            boolean hinge = false;
            EnumFacing doorFace = BuildMode.START_BLOCK.getBlockState().getValue(BlockDoor.FACING);

            for(int i=0;i< rotationCount;i++)
            {

                    doorFace = doorFace.rotateYCCW();
            }

            if(hitVec == null)
                hitVec = Vec3d.ZERO;

            if(BuildMode.START_BLOCK.getFace().getAxis() == EnumFacing.Axis.Y)
            {
                if(doorFace == EnumFacing.WEST)
                {
                    double hitZ = hitVec.z - Math.floor(hitVec.z);
                    hinge = (hitZ < 0.5) ;
                }
                else if(doorFace == EnumFacing.EAST)
                {
                    double hitZ = hitVec.z - Math.floor(hitVec.z);
                    hinge = (hitZ > 0.5) ;
                }
                else if(doorFace == EnumFacing.NORTH)
                {
                    double hitX = hitVec.x - Math.floor(hitVec.x);
                    hinge = (hitX > 0.5) ;
                }
                else
                {
                    double hitX = hitVec.x - Math.floor(hitVec.x);
                    hinge = (hitX < 0.5) ;
                }

            }

            else
            {
                if(doorFace == EnumFacing.WEST)
                {
                    double hitZ = hitVec.z - Math.floor(hitVec.z);
                    hinge = (hitZ > 0.5) ;
                }
                else if(doorFace == EnumFacing.EAST)
                {
                    double hitZ = hitVec.z - Math.floor(hitVec.z);
                    hinge = (hitZ < 0.5) ;
                }
                else if(doorFace == EnumFacing.NORTH)
                {
                    double hitX = hitVec.x - Math.floor(hitVec.x);
                    hinge = (hitX < 0.5) ;
                }
                else
                {
                    double hitX = hitVec.x - Math.floor(hitVec.x);
                    hinge = (hitX > 0.5) ;
                }

            }

            TemplateBlock block = new TemplateBlock(BuildMode.START_BLOCK.getFace(),BuildMode.START_BLOCK.getBlockPos(), state.withProperty(BlockDoor.HINGE, hinge ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER));
            BuildMode.CURR_TOOL_BLOCKS.add(block);
            TemplateBlock block2 = new TemplateBlock(BuildMode.START_BLOCK.getFace(),BuildMode.START_BLOCK.getBlockPos().up(), state.withProperty(BlockDoor.HINGE, hinge ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER));
            BuildMode.CURR_TOOL_BLOCKS.add(block2);
        }
        else if(state.getBlock() instanceof BlockBed)
        {
            EnumFacing facing = state.getValue(BlockBed.FACING);
            TemplateBlock block = new TemplateBlock(BuildMode.START_BLOCK.getFace(),BuildMode.START_BLOCK.getBlockPos(), state,BuildMode.START_BLOCK.getTileEntity());
            BuildMode.CURR_TOOL_BLOCKS.add(block);
            TemplateBlock block2 = new TemplateBlock(BuildMode.START_BLOCK.getFace(),BuildMode.START_BLOCK.getBlockPos().add(facing.getDirectionVec()), state.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD),BuildMode.START_BLOCK.getTileEntity());
            BuildMode.CURR_TOOL_BLOCKS.add(block2);
        }
        else
        {
            BuildMode.CURR_TOOL_BLOCKS.add(BuildMode.START_BLOCK);
        }


    }


    @Override
    public void applyRotation()
    {
        if(BuildMode.START_BLOCK != null)
        {
            if(BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockDoor || BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockBed)
            {
                for(int i=0;i< rotationCount;i++)
                {
                    BuildMode.START_BLOCK.rotateOrientation();
                    for(TemplateBlock block : BuildMode.CURR_TOOL_BLOCKS)
                    {
                        block.rotateOrientation();
                    }
                }
            }
            else
                super.applyRotation();

            if(BuildMode.START_BLOCK.getBlockState().getBlock() instanceof BlockBed)
                BuildMode.CURR_TOOL_BLOCKS.get(1).setBlockPos(BuildMode.START_BLOCK.getBlockPos().add(BuildMode.START_BLOCK.getBlockState().getValue(BlockBed.FACING).getDirectionVec()));

        }


    }



    @Override
    public boolean startFinishBuilding(int rightClickNumber)
    {
        //if you are in deletemode then te currtoolBlocks list is not place instead the SingleToolMode will handle the
        // placement it self in the leftClick function
        if(BuildMode.DELETE_MODE)
        {
//            BuildMode.cancelBuilding();
            return true;
        }
        else if(BuildMode.START_BLOCK.getBlockState() != null)
        {
            return super.startFinishBuilding(rightClickNumber);
        }
        
        return false;
    }

    @Override
    public boolean rightClick(Action currAction, EntityPlayer player, int rightClickNumber) {
        if(super.rightClick(currAction,player,rightClickNumber))
        {
//            PlaceTemplateMode.placeBlock(BuildMode.START_BLOCK.getBlockPos(), player, currAction, new TemplateBlock(BuildMode.START_BLOCK.getFace(),0,0,0,BuildMode.START_BLOCK.getBlockState(),BuildMode.START_BLOCK.getTileEntity()));
            return true;
        }
        return false;
    }

    @Override
    public boolean leftClick(Action currAction, EntityPlayer player, int rightClickNumber)
    {
        if(super.leftClick(currAction,player,rightClickNumber) && !BuildMode.isUsingAbsCoord())
        {
            PlaceTemplateMode.placeBlock(getStartDeleteBlock().getBlockPos().add(BuildMode.MOUSE_X_OFFSET,BuildMode.MOUSE_Y_OFFSET,BuildMode.MOUSE_Z_OFFSET), player, currAction, new TemplateBlock(BuildMode.START_BLOCK.getFace(),0,0,0,getStartDeleteBlock().getBlockState()));
            return true;
        }
       
        return false;
    }
    
    public static TemplateBlock getStartDeleteBlock()
    {
        if(StartDeleteBlock != null)
            return StartDeleteBlock;
        else
            return new TemplateBlock(EnumFacing.NORTH,BlockPos.ORIGIN,Blocks.AIR.getDefaultState());
    }
    
    public static void setStartDeleteBlock(TemplateBlock startDeleteBlock) {
        StartDeleteBlock = startDeleteBlock;
    }

    public static TemplateBlock getStartDeleteBlock2() {
        return StartDeleteBlock2;
    }

    public static void setStartDeleteBlock2(TemplateBlock startDeleteBlock) {
        StartDeleteBlock2 = startDeleteBlock;
    }


    public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door, boolean isRightHinge, EntityPlayer player, Action action)
    {
        BlockPos blockpos = pos.offset(facing.rotateY());
        BlockPos blockpos1 = pos.offset(facing.rotateYCCW());
        int i = (worldIn.getBlockState(blockpos1).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos1.up()).isNormalCube() ? 1 : 0);
        int j = (worldIn.getBlockState(blockpos).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos.up()).isNormalCube() ? 1 : 0);
        boolean flag = worldIn.getBlockState(blockpos1).getBlock() == door || worldIn.getBlockState(blockpos1.up()).getBlock() == door;
        boolean flag1 = worldIn.getBlockState(blockpos).getBlock() == door || worldIn.getBlockState(blockpos.up()).getBlock() == door;

        if ((!flag || flag1) && j <= i)
        {
            if (flag1 && !flag || j < i)
            {
                isRightHinge = false;
            }
        }
        else
        {
            isRightHinge = true;
        }

        BlockPos blockpos2 = pos.up();
        boolean flag2 = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockpos2);
        IBlockState iblockstate = door.getDefaultState().withProperty(BlockDoor.FACING, facing).withProperty(BlockDoor.HINGE, isRightHinge ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT).withProperty(BlockDoor.POWERED, Boolean.valueOf(flag2)).withProperty(BlockDoor.OPEN, Boolean.valueOf(flag2));
        PlaceTemplateMode.placeBlock(BlockPos.ORIGIN, player,action,new TemplateBlock(EnumFacing.NORTH,pos, iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)));
        PlaceTemplateMode.placeBlock(BlockPos.ORIGIN, player,action,new TemplateBlock(EnumFacing.NORTH,blockpos2, iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)));
    }

    @Override
    public boolean hasEndPosition()
    {
        return false;
    }
}
