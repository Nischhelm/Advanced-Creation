package com.deadtiger.advcreation.network.message;

import com.deadtiger.advcreation.network.NetworkManager;
import com.deadtiger.advcreation.network.NetworkPlaceBlockListFormatter;
import com.deadtiger.advcreation.network.network_utility.ByteBufCustomUtils;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.template.TemplateBlock;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.deadtiger.advcreation.network.message.MessagePlaceListsTemplateBlock.CompressedTemplateBlock;

public class MessagePreviewListsTemplateBlock extends MessageBase<MessagePreviewListsTemplateBlock>
{

    public String playerName;
    public boolean drawOutlineOnly;
    public boolean drawSelectionBox;
    public BlockPos startPos;
    public BlockPos endPos;
    public ArrayList<IBlockState> stateLegendList;
    public ArrayList<CompressedTemplateBlock> firstPlacedList;
    public ArrayList<CompressedTemplateBlock> secondPlacedList;

    public int messageNbr;
    public int totMessages;

    public MessagePreviewListsTemplateBlock()
    {
    }

    public MessagePreviewListsTemplateBlock(BlockPos startPos, int messageNbr, int totMessages, ArrayList<IBlockState> stateLegendList, ArrayList<CompressedTemplateBlock> firstPlacedList, ArrayList<CompressedTemplateBlock> secondPlacedList, String playerName, boolean drawOutlineOnly)
    {
        this(startPos, BlockPos.ORIGIN, messageNbr, totMessages, stateLegendList, firstPlacedList, secondPlacedList, playerName, false, drawOutlineOnly);
    }

    public MessagePreviewListsTemplateBlock(BlockPos startPos, BlockPos endPos, int messageNbr, int totMessages, ArrayList<IBlockState> stateLegendList, ArrayList<CompressedTemplateBlock> firstPlacedList, ArrayList<CompressedTemplateBlock> secondPlacedList, String playerName, boolean drawSelectionBox, boolean drawOutlineOnly)
    {
        this.playerName = playerName;
        this.startPos = startPos;
        this.endPos = endPos;
        this.messageNbr = messageNbr;
        this.totMessages = totMessages;

        this.stateLegendList = stateLegendList;
        this.firstPlacedList = firstPlacedList;
        this.secondPlacedList = secondPlacedList;
        this.drawSelectionBox = drawSelectionBox;
        this.drawOutlineOnly = drawOutlineOnly;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientSide(MessagePreviewListsTemplateBlock message, EntityPlayer player)
    {
        NetworkManager.PREVIEW_MESSAGE_RECEIVED_LOCK.blockingAttemptAtLocking();
        if (message.messageNbr == 0)
        {
            ArrayList<MessagePreviewListsTemplateBlock> messages = new ArrayList<MessagePreviewListsTemplateBlock>();
            messages.add(message);
            NetworkManager.PREVIEW_MESSAGE_RECEIVED_LIST.put(message.playerName, messages);
        }
        else
        {
            if (NetworkManager.PREVIEW_MESSAGE_RECEIVED_LIST.containsKey(message.playerName))
                NetworkManager.PREVIEW_MESSAGE_RECEIVED_LIST.get(message.playerName).add(message);
        }
        NetworkManager.PREVIEW_MESSAGE_RECEIVED_LOCK.releaseLock();
    }

    @Override
    public void handleServerSide(MessagePreviewListsTemplateBlock message, EntityPlayer player)
    {
        NetworkHandler.sendPreviewBlocksToAllClients(message);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int playerNameLength = buf.readShort();
        this.playerName = (String) buf.readCharSequence(playerNameLength, Charset.defaultCharset());

        this.startPos = ByteBufCustomUtils.readBlockPos(buf);
        this.drawSelectionBox = buf.readBoolean();
        this.endPos = ByteBufCustomUtils.readBlockPos(buf);
        this.messageNbr = buf.readInt();
        this.totMessages = buf.readInt();

        this.drawOutlineOnly = buf.readBoolean();

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
        buf.writeShort(this.playerName.length());
        buf.writeCharSequence(this.playerName, Charset.defaultCharset());

        ByteBufCustomUtils.writeBlockPos(buf, this.startPos);
        buf.writeBoolean(this.drawSelectionBox);
        ByteBufCustomUtils.writeBlockPos(buf, this.endPos);

        buf.writeInt(this.messageNbr);
        buf.writeInt(this.totMessages);

        buf.writeBoolean(this.drawOutlineOnly);

        //write the list of blockstates in the message
        buf.writeInt(stateLegendList.size());
        for (IBlockState iBlockState : stateLegendList)
        {
            ByteBufCustomUtils.writeIBlockState(buf, iBlockState);
        }
        //write the list of blocks to be placed first
        buf.writeInt(firstPlacedList.size());
        for (CompressedTemplateBlock templateBlock : firstPlacedList)
        {
            ByteBufCustomUtils.writeCompressedTemplateBlock(buf, templateBlock);
        }

        //write the furniture an other blocks that depend on the placement of the first block in a seperatelist
        buf.writeInt(secondPlacedList.size());
        for (CompressedTemplateBlock compressedTemplateBlock : secondPlacedList)
        {
            ByteBufCustomUtils.writeCompressedTemplateBlock(buf, compressedTemplateBlock);
        }
    }

    public static void sendBlocksListsRelToPosToServer(BlockPos position2, ArrayList<TemplateBlock> firstBlocksToServer, ArrayList<TemplateBlock> furnitureBlocks, String playerName, boolean drawOutlineOnly)
    {
        ArrayList<ArrayList<IBlockState>> stateLegendList = new ArrayList<>();
        stateLegendList.add(new ArrayList<>());
        ArrayList<ArrayList<CompressedTemplateBlock>> firstPlacedList = new ArrayList<>();
        firstPlacedList.add(new ArrayList<>());
        ArrayList<ArrayList<CompressedTemplateBlock>> secondPlacedList = new ArrayList<>();
        secondPlacedList.add(new ArrayList<>());

        int messageIndex = splitBlockListsIntoMessages(firstBlocksToServer, furnitureBlocks, stateLegendList, firstPlacedList, secondPlacedList, 29000);

        for (int i = 0; i <= messageIndex; i++)
        {
            NetworkHandler.sendPreviewBlocksToServer(new MessagePreviewListsTemplateBlock(position2, i, messageIndex, stateLegendList.get(i), firstPlacedList.get(i), secondPlacedList.get(i), playerName, drawOutlineOnly));
        }
    }

    public static void sendBlocksListsRelToPosToServer(BlockPos position2, ArrayList<TemplateBlock> firstBlocksToServer, ArrayList<TemplateBlock> furnitureBlocks, String playerName, BlockPos startPos, BlockPos endPos)
    {
        ArrayList<ArrayList<IBlockState>> stateLegendList = new ArrayList<>();
        stateLegendList.add(new ArrayList<>());
        ArrayList<ArrayList<CompressedTemplateBlock>> firstPlacedList = new ArrayList<>();
        firstPlacedList.add(new ArrayList<>());
        ArrayList<ArrayList<CompressedTemplateBlock>> secondPlacedList = new ArrayList<>();
        secondPlacedList.add(new ArrayList<>());

        int messageIndex = splitBlockListsIntoMessages(firstBlocksToServer, furnitureBlocks, stateLegendList, firstPlacedList, secondPlacedList, 29000);

        for (int i = 0; i <= messageIndex; i++)
        {
            NetworkHandler.sendPreviewBlocksToServer(new MessagePreviewListsTemplateBlock(startPos, endPos, i, messageIndex, stateLegendList.get(i), firstPlacedList.get(i), secondPlacedList.get(i), playerName, true, false));
        }
    }

    public static void sendBlocksListsRelToPosToServer(BlockPos position2, ArrayList<TemplateBlock> firstBlocksToServer, ArrayList<TemplateBlock> furnitureBlocks, String playerName, BlockPos startPos, BlockPos endPos, boolean drawSelectionBox)
    {
        ArrayList<ArrayList<IBlockState>> stateLegendList = new ArrayList<>();
        stateLegendList.add(new ArrayList<>());
        ArrayList<ArrayList<CompressedTemplateBlock>> firstPlacedList = new ArrayList<>();
        firstPlacedList.add(new ArrayList<>());
        ArrayList<ArrayList<CompressedTemplateBlock>> secondPlacedList = new ArrayList<>();
        secondPlacedList.add(new ArrayList<>());

        int messageIndex = splitBlockListsIntoMessages(firstBlocksToServer, furnitureBlocks, stateLegendList, firstPlacedList, secondPlacedList, 29000);

        for (int i = 0; i <= messageIndex; i++)
        {
            NetworkHandler.sendPreviewBlocksToServer(new MessagePreviewListsTemplateBlock(startPos, endPos, i, messageIndex, stateLegendList.get(i), firstPlacedList.get(i), secondPlacedList.get(i), playerName, drawSelectionBox, false));
        }
    }

    public static int splitBlockListsIntoMessages(ArrayList<TemplateBlock> firstBlocksToServer, ArrayList<TemplateBlock> furnitureBlocks, ArrayList<ArrayList<IBlockState>> stateLegendList, ArrayList<ArrayList<CompressedTemplateBlock>> firstPlacedList, ArrayList<ArrayList<CompressedTemplateBlock>> secondPlacedList, int i2)
    {
        int messageIndex = 0;
        int byteCount = 0;
        int[] res = CompressBlocksIntoMessageLists(firstBlocksToServer, stateLegendList,
                firstPlacedList, secondPlacedList, messageIndex, i2,byteCount);

        messageIndex = res[0];
        byteCount = res[1];
        res = CompressBlocksIntoMessageLists(furnitureBlocks, stateLegendList,
                secondPlacedList, firstPlacedList, messageIndex, i2,byteCount);
        messageIndex = res[0];
        byteCount = res[1];

        return messageIndex;
    }

    private static int[] CompressBlocksIntoMessageLists(ArrayList<TemplateBlock> BlocksToServer, ArrayList<ArrayList<IBlockState>> stateLegendList,
                                                        ArrayList<ArrayList<CompressedTemplateBlock>> activePlacedList,
                                                        ArrayList<ArrayList<CompressedTemplateBlock>> otherPlacedList,
                                                        int messageIndex, int maxByteSize, int byteCount)
    {
        int totalCount = 0;
        for (TemplateBlock block : BlocksToServer)
        {
            //check if templateblock can be safely sent to the server otherwise replace it with an air block
            TemplateBlock safeBlock = ByteBufCustomUtils.getSafeTemplateBlock(block);

            IBlockState state = safeBlock.getBlockState();
            int stateIndex = 0;
            if (stateLegendList.get(messageIndex).contains(state))
                stateIndex = stateLegendList.get(messageIndex).indexOf(state);
            else
            {
                stateIndex = stateLegendList.get(messageIndex).size();
                stateLegendList.get(messageIndex).add(state);
                byteCount += 8;
            }




            if(safeBlock.getTileEntity() == null)
                activePlacedList.get(messageIndex).add(new CompressedTemplateBlock(stateIndex, safeBlock.getBlockPos()));
            else
            {
                NBTTagCompound tag = safeBlock.getTileEntity().serializeNBT();
                activePlacedList.get(messageIndex).add(new CompressedTemplateBlock(stateIndex, safeBlock.getBlockPos(),tag));
                Integer tagBytes = NetworkPlaceBlockListFormatter.getTagBytes(tag);
                if (tagBytes == null)
                    tagBytes += 8;
                byteCount += tagBytes;
            }

            byteCount += 18;


            if (byteCount > maxByteSize)
            {
                //basically create a new message when the current message is full
                stateLegendList.add(new ArrayList<>());
                activePlacedList.add(new ArrayList<>());
                otherPlacedList.add(new ArrayList<>());
                messageIndex++;
                totalCount += byteCount;
                byteCount = 0;

            }

        }
        totalCount += byteCount;
        return new int[]{messageIndex, byteCount};
    }
}
