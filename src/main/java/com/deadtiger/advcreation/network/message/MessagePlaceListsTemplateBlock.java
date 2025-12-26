package com.deadtiger.advcreation.network.message;

import com.deadtiger.advcreation.network.NetworkManager;
import com.deadtiger.advcreation.network.network_utility.ByteBufCustomUtils;
import com.deadtiger.advcreation.template.TemplateBlock;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.ArrayList;

import static net.minecraft.item.ItemBlock.setTileEntityNBT;

public class MessagePlaceListsTemplateBlock extends MessageBase<MessagePlaceListsTemplateBlock>
{

    public BlockPos startPos;
    public ArrayList<IBlockState> stateLegendList;
    public ArrayList<CompressedTemplateBlock> firstPlacedList;
    public ArrayList<CompressedTemplateBlock> secondPlacedList;
    public int messageNbr;
    public int totMessages;

    public MessagePlaceListsTemplateBlock()
    {
    }

    public MessagePlaceListsTemplateBlock(BlockPos startPos, ArrayList<TemplateBlock> firstPlacedList, ArrayList<TemplateBlock> secondPlacedList, BlockPos startUpdatePos, BlockPos endUpdatePos)
    {
        this.startPos = startPos;

        this.stateLegendList = new ArrayList<>();
        this.firstPlacedList = new ArrayList<>();
        this.secondPlacedList = new ArrayList<>();

        for (TemplateBlock block : firstPlacedList)
        {
            IBlockState state = block.getBlockState();
            int stateIndex = 0;
            if (stateLegendList.contains(state))
                stateIndex = stateLegendList.indexOf(state);
            else
            {
                stateIndex = stateLegendList.size();
                stateLegendList.add(state);
            }
            if(block.getTileEntity() != null)
                this.firstPlacedList.add(new CompressedTemplateBlock(stateIndex, block.getBlockPos(),block.getTileEntity().serializeNBT()));
            else
                this.firstPlacedList.add(new CompressedTemplateBlock(stateIndex, block.getBlockPos()));
        }

        for (TemplateBlock block : secondPlacedList)
        {
            IBlockState state = block.getBlockState();
            int stateIndex = 0;
            if (stateLegendList.contains(state))
                stateIndex = stateLegendList.indexOf(state);
            else
            {
                stateIndex = stateLegendList.size();
                stateLegendList.add(state);
            }

            if(block.getTileEntity() != null)
                this.secondPlacedList.add(new CompressedTemplateBlock(stateIndex, block.getBlockPos(),block.getTileEntity().serializeNBT()));
            else
                this.secondPlacedList.add(new CompressedTemplateBlock(stateIndex, block.getBlockPos()));
        }
    }


    public MessagePlaceListsTemplateBlock(BlockPos startPos, BlockPos startUpdatePos, BlockPos endUpdatePos, ArrayList<IBlockState> stateLegendList, ArrayList<CompressedTemplateBlock> firstPlacedList, ArrayList<CompressedTemplateBlock> secondPlacedList)
    {
        this.startPos = startPos;

        this.stateLegendList = stateLegendList;
        this.firstPlacedList = firstPlacedList;
        this.secondPlacedList = secondPlacedList;
    }

    public MessagePlaceListsTemplateBlock(BlockPos startPos, int messageNbr, int totMessages, ArrayList<IBlockState> stateLegendList, ArrayList<CompressedTemplateBlock> firstPlacedList, ArrayList<CompressedTemplateBlock> secondPlacedList)
    {
        this.startPos = startPos;
        this.messageNbr = messageNbr;
        this.totMessages = totMessages;

        this.stateLegendList = stateLegendList;
        this.firstPlacedList = firstPlacedList;
        this.secondPlacedList = secondPlacedList;
    }

    @Override
    public void handleClientSide(MessagePlaceListsTemplateBlock message, EntityPlayer player)
    {

    }

    @Override
    public void handleServerSide(MessagePlaceListsTemplateBlock message, EntityPlayer player)
    {
        //place the new message in the queue for that player
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        if (!NetworkManager.PLACE_MESSAGE_RECEIVE_QUEUE.containsKey(playerMP))
            NetworkManager.PLACE_MESSAGE_RECEIVE_QUEUE.put(playerMP, new ArrayDeque<>());

        NetworkManager.PLACE_MESSAGE_RECEIVE_QUEUE.get(playerMP).add(message);

        //if all message are not receives yet do not place the template yet
//        if (message.messageNbr == message.totMessages)
            NetworkManager.PLACE_RECEIVED_TEMPLATE.put(playerMP, true);
//        else
//            NetworkManager.PLACE_RECEIVED_TEMPLATE.put(playerMP, false);

    }

    public static boolean placeBlockAt(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        ItemStack dummyStack = new ItemStack(newState.getBlock());
        if (!world.setBlockState(pos, newState, 11)) return false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == newState.getBlock())
        {
            setTileEntityNBT(world, player, pos, dummyStack);
            state.getBlock().onBlockPlacedBy(world, pos, state, player, dummyStack);

            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, dummyStack);
        }

        return true;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.startPos = ByteBufCustomUtils.readBlockPos(buf);
        this.messageNbr = buf.readInt();
        this.totMessages = buf.readInt();

        int sizeStateLegendList = buf.readInt();
        this.stateLegendList = new ArrayList<>();
        for (int i = 0; i < sizeStateLegendList; i++)
        {
            this.stateLegendList.add(ByteBufCustomUtils.readIBlockState(buf));
        }

        int sizeFirstList = buf.readInt();
        this.firstPlacedList = new ArrayList<>();
        for (int i = 0; i < sizeFirstList; i++)
        {
            this.firstPlacedList.add(ByteBufCustomUtils.readCompressedTemplateBlock(buf));
        }

        //write the furniture an other blocks that depend on the placement of the first block in a seperatelist
        int sizeSecondList = buf.readInt();
        this.secondPlacedList = new ArrayList<>();
        for (int i = 0; i < sizeSecondList; i++)
        {
            this.secondPlacedList.add(ByteBufCustomUtils.readCompressedTemplateBlock(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufCustomUtils.writeBlockPos(buf, this.startPos);
        buf.writeInt(this.messageNbr);
        buf.writeInt(this.totMessages);

        //write the list of blockstates in the message
        buf.writeInt(stateLegendList.size());
        for (int i = 0; i < stateLegendList.size(); i++)
        {
            ByteBufCustomUtils.writeIBlockState(buf, stateLegendList.get(i));
        }

        //write the list of blocks to be placed first
        buf.writeInt(firstPlacedList.size());
        for (int i = 0; i < firstPlacedList.size(); i++)
        {
            ByteBufCustomUtils.writeCompressedTemplateBlock(buf, firstPlacedList.get(i));
        }

        //write the furniture an other blocks that depend on the placement of the first block in a seperatelist
        buf.writeInt(secondPlacedList.size());
        for (int i = 0; i < secondPlacedList.size(); i++)
        {
            ByteBufCustomUtils.writeCompressedTemplateBlock(buf, secondPlacedList.get(i));
        }
    }

    public static class CompressedTemplateBlock
    {
        public int blockStateIndex;
        public BlockPos pos;
        public NBTTagCompound tag;

        public CompressedTemplateBlock(int blockStateIndex, BlockPos pos)
        {
            this(blockStateIndex,pos,null);
        }

        public CompressedTemplateBlock(int blockStateIndex, BlockPos pos, NBTTagCompound tag)
        {
            this.blockStateIndex = blockStateIndex;
            this.pos = pos;
            this.tag = tag;
        }

        public int getBlockStateIndex()
        {
            return blockStateIndex;
        }

        public void setBlockStateIndex(int blockStateIndex)
        {
            this.blockStateIndex = blockStateIndex;
        }

        public BlockPos getPos()
        {
            return pos;
        }

        public void setPos(BlockPos pos)
        {
            this.pos = pos;
        }

        public NBTTagCompound getTag()
        {
            return tag;
        }

        public void setTag(NBTTagCompound tag)
        {
            this.tag = tag;
        }
    }
}
