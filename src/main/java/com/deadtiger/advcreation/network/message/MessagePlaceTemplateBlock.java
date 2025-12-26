package com.deadtiger.advcreation.network.message;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.network.network_utility.ByteBufCustomUtils;
import com.deadtiger.advcreation.utility.FakeWorld;
import com.deadtiger.advcreation.utility.TileEntityPlacementHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static net.minecraft.item.ItemBlock.setTileEntityNBT;

public class MessagePlaceTemplateBlock extends MessageBase<MessagePlaceTemplateBlock>
{

    public int x;
    public int y;
    public int z;
    public boolean replacement;
    public IBlockState iBlockState;
    public TileEntity tileEntity;


    public MessagePlaceTemplateBlock()
    {
    }

    public MessagePlaceTemplateBlock(int x, int y, int z, boolean replacement, IBlockState blockState)
    {
        this(x,y,z,replacement,blockState,null);
    }

    public MessagePlaceTemplateBlock(int x, int y, int z, boolean replacement, IBlockState blockState,TileEntity tileEntity)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.replacement = replacement;
        this.iBlockState = blockState;
        this.tileEntity = tileEntity;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleClientSide(MessagePlaceTemplateBlock message, EntityPlayer player)
    {
        //when this message is sent from server to client it means the server just placed a block that the player needs to edit
        //if the block placed was a sign then you need to put the player in the sign edit mode
        if(message.iBlockState.getBlock() instanceof BlockSign)
        {
            Minecraft mc = Minecraft.getMinecraft();
            HelpFunctions.editPlacedSign(mc.player.getHeldItemMainhand(),mc.world, new BlockPos(message.x,message.y,message.z),mc.player);
        }
    }

    @Override
    public void handleServerSide(MessagePlaceTemplateBlock message, EntityPlayer player)
    {
        BlockPos pos = new BlockPos(message.x, message.y, message.z);
        IBlockState state = message.iBlockState;

        player.world.setBlockState(pos, state, 3);

        if(message.tileEntity != null)
        {
            TileEntity placedTileEntity = player.world.getTileEntity(pos);
            try
            {
                TileEntityPlacementHelper.formatTileEntityForBlock(state,placedTileEntity,message.tileEntity,pos, player.world);
            }
            catch (InvocationTargetException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }


//          TODO: NOT sure if deleting this will not cause any other problems but for chests it broke stuff
//        Block block = state.getBlock();
//        IBlockState worldState = player.world.getBlockState(pos);
//        if (worldState.getBlock() == block)
//        {
//            ItemStack dummyStack = new ItemStack(block);
//            setTileEntityNBT(player.world, player, pos, dummyStack);
//            message.iBlockState.getBlock().onBlockPlacedBy(player.world, pos, state, player, dummyStack);
//
//            if (player instanceof EntityPlayerMP)
//                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, dummyStack);
//        }
    }

    public static boolean placeBlockAt(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        ItemStack dummyStack = new ItemStack(newState.getBlock());
        if (!world.setBlockState(pos, newState, 11)) return false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == newState.getBlock())
        {
            setTileEntityNBT(world, player, pos, dummyStack);
            newState.getBlock().onBlockPlacedBy(world, pos, newState, player, dummyStack);

            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, dummyStack);
        }

        return true;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.replacement = buf.readBoolean();
        this.iBlockState = ByteBufCustomUtils.readIBlockState(buf);
        try
        {
            NBTTagCompound tag = ByteBufCustomUtils.readCompoundTag(buf);
            if(tag == null)
                tileEntity = null;
            else
            {

                tileEntity = AdvCreation.proxy.createTileEntityOf(iBlockState);
                tileEntity.readFromNBT(tag);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeBoolean(this.replacement);
        ByteBufCustomUtils.writeIBlockState(buf, iBlockState);
        if(tileEntity == null)
            ByteBufCustomUtils.writeCompoundTag(buf,null);
        else
            ByteBufCustomUtils.writeCompoundTag(buf,tileEntity.serializeNBT());

    }


}
