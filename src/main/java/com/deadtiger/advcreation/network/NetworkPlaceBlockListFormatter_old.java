package com.deadtiger.advcreation.network;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.EnumMainMode;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.network.message.MessagePlaceListsTemplateBlock;
import com.deadtiger.advcreation.network.network_utility.ByteBufCustomUtils;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.undo_actions.UndoFunctionality;
import com.deadtiger.advcreation.utility.PlacementHelper;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class NetworkPlaceBlockListFormatter_old
{

    public static boolean PLACEMENT_OPERATION = false;
    public static boolean LARGE_PLACEMENT = false;

    public static ArrayDeque<ArrayList<TemplateBlock>> BLOCKS_TO_CHECK_FOR_PLACEMENT = new ArrayDeque<>();
    public static ArrayList<TemplateBlock> CURR_MESSAGE_BLOCKS_TO_CHECK_FOR_PLACEMENT = null;
    public static boolean CURR_MESSAGE_CHECK_COMPLETE = true;
    public static long LAST_PLACEMENT_CHECK = 0;
    public static Comparator<ChunkPos> chunkPosComparator = new ChunkPosComparator();

    // variables to allow messages to be sent again after a few unsuccessfull checks of placed blocks.
    public static final int checkTimesBeforeResend = 5;
    public static  int checkTimesCount = 0;

    public static int currMessageNeededToCompleteAction = 0;
    public static int confirmedPlaceMessageCompletion = 0;

    public static int sleeptimeAfterCheckOrSend = 500;

    public static void sendBlocksListsRelToPosToServer(BlockPos position2, ArrayList<TemplateBlock> firstBlocksToServer, ArrayList<TemplateBlock> furnitureBlocks, BlockPos startUpdatePos, BlockPos endUpdatePos, Action currAction)
    {
        //if the amount of blocks is smaller than 1 message don't bother splitting per chunk
        if(firstBlocksToServer.size() + furnitureBlocks.size() < 2950 )
        {
            splitAndSendBlocksRelToPosToServer(position2,firstBlocksToServer,furnitureBlocks,startUpdatePos,endUpdatePos);
            return;
        }
        else if(firstBlocksToServer.size() + furnitureBlocks.size() < (2950*10))
        {
            splitAndSendBlocksRelToPosToServer(position2,firstBlocksToServer,furnitureBlocks,startUpdatePos,endUpdatePos);
            notifyStartPlacementOperation(true);
            return;
        }
        if(firstBlocksToServer.size() + furnitureBlocks.size() > 90000)
            currAction.setGiganticAction(true);

        notifyStartPlacementOperation(true);

        //split blocks in messages per chunk
        HashMap<ChunkPos, PlaceControlledBlockList> chunkPosToPlaceControlledBlockListHashMap = new HashMap<>();

        for (TemplateBlock block : firstBlocksToServer)
        {
            BlockPos offset = position2.add(block.getBlockPos());
            ChunkPos chunkPos = new ChunkPos(offset);
            if(chunkPosToPlaceControlledBlockListHashMap.containsKey(chunkPos))
                chunkPosToPlaceControlledBlockListHashMap.get(chunkPos).addBlock(block,true);
            else
                chunkPosToPlaceControlledBlockListHashMap.put(chunkPos,new PlaceControlledBlockList(block,true));
        }

        for (TemplateBlock block : furnitureBlocks)
        {
            BlockPos offset = position2.add(block.getBlockPos());
            ChunkPos chunkPos = new ChunkPos(offset);
            if(chunkPosToPlaceControlledBlockListHashMap.containsKey(chunkPos))
                chunkPosToPlaceControlledBlockListHashMap.get(chunkPos).addBlock(block,false);
            else
                chunkPosToPlaceControlledBlockListHashMap.put(chunkPos,new PlaceControlledBlockList(block,false));
        }

        //short the list key ChunkPos so that the structure is built up gradually in a diagonal fashion
        List<ChunkPos> sortedKeys = new ArrayList(chunkPosToPlaceControlledBlockListHashMap.keySet());
        Collections.sort(sortedKeys, chunkPosComparator);

        for (ChunkPos chunkPos: sortedKeys)
        {
            PlaceControlledBlockList blocksInChunk = chunkPosToPlaceControlledBlockListHashMap.get(chunkPos);
            splitAndSendBlocksRelToPosToServer(position2,blocksInChunk.firstBlocksToServer,blocksInChunk.furnitureBlocks,startUpdatePos,endUpdatePos);
        }
    }

    public static Integer getTagBytes(NBTTagCompound tag)
    {
        int tagBytes = 384/4;
        for (String NBTtagKey: tag.getKeySet())
        {
            NBTBase base =  tag.getTag(NBTtagKey);
            switch (base.getId())
            {
                case 0:
                    //NBTTagEnd
                    tagBytes += 64/4;
                    break;
                case 1:
                    //NBTTagByte
                    tagBytes += 72/4;
                    break;
                case 2:
                    //NBTTagShort
                    tagBytes += 80/4;
                    break;
                case 3:
                    //NBTTagInt
                    tagBytes += 96/4;
                    break;
                case 4:
                    //NBTTagLong
                    tagBytes += 128/4;
                    break;
                case 5:
                    //NBTTagFloat
                    tagBytes += 96/4;
                    break;
                case 6:
                    //NBTTagDouble
                    tagBytes += 128/4;
                    break;
                case 7:
                    //NBTTagByteArray
                    tagBytes += 192/4;
                    NBTTagByteArray tagArray = (NBTTagByteArray) base;
                    tagBytes += (8*tagArray.getByteArray().length)/4;
                    break;
                case 8:
                    //NBTTagString
                    tagBytes += 288/4;
                    NBTTagString tagString = (NBTTagString) base;
                    tagBytes +=16;

                    byte[] data = tagString.getString().getBytes();

                    if (data == null)
                        return null;

                    int len = data.length;
                    int utflen = 0;

                    for (int i = 0; i < len; i++)
                    {
                        int c = data[i];
                        if ((c >= 0x0001) && (c <= 0x007F)) utflen += 1;
                        else if (c > 0x07FF)                utflen += 3;
                        else                                utflen += 2;
                    }
                    tagBytes += (8*utflen)/4;
                    break;
                case 9:
                    //NBTTagList
                    tagBytes += 296/4;
                    NBTTagList tagList= (NBTTagList) base;
                    tagBytes += (32*tagList.tagCount())/4;
                    break;
                case 10:
                    //NBTTagCompound
                    tagBytes += 384/4;
                    NBTTagCompound tagComp= (NBTTagCompound) base;
                    tagBytes += (32*tagComp.getSize())/4;
                    break;
                case 11:
                    //NBTTagIntArray
                    tagBytes += 192/4;
                    NBTTagIntArray tagIntArray = (NBTTagIntArray) base;
                    tagBytes += (32*tagIntArray.getIntArray().length)/4;
                    break;

                case 12:
                    //NBTTagLongArray
                    tagBytes += 192/4;
                    NBTTagLongArray tagLongArray = (NBTTagLongArray) base;
                    tagBytes += (64*10);
                    break;
            }
        }
        return tagBytes;
    }

    public static void sendBlocksListsRelToPosToServer(BlockPos position2, ArrayList<TemplateBlock> firstBlocksToServer, ArrayList<TemplateBlock> furnitureBlocks, BlockPos startUpdatePos, BlockPos endUpdatePos)
    {
        ArrayList<ArrayList<IBlockState>> stateLegendList = new ArrayList<>();
        stateLegendList.add(new ArrayList<>());
        ArrayList<ArrayList<MessagePlaceListsTemplateBlock.CompressedTemplateBlock>> firstPlacedList = new ArrayList<>();
        firstPlacedList.add(new ArrayList<>());
        ArrayList<ArrayList<MessagePlaceListsTemplateBlock.CompressedTemplateBlock>> secondPlacedList = new ArrayList<>();
        secondPlacedList.add(new ArrayList<>());

        int messageIndex = 0;
        int byteCount = 0;
        int maxByteSize = 30000;

        for (TemplateBlock block : firstBlocksToServer)
        {
            //check if templateblock can be safely sent to the server otherwise replace it with an air block
            TemplateBlock safeBlock = ByteBufCustomUtils.getSafeTemplateBlock(block,Minecraft.getMinecraft().player);

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
                firstPlacedList.get(messageIndex).add(new MessagePlaceListsTemplateBlock.CompressedTemplateBlock(stateIndex, safeBlock.getBlockPos()));
            else
            {
                NBTTagCompound tag = safeBlock.getTileEntity().serializeNBT();
                firstPlacedList.get(messageIndex).add(new MessagePlaceListsTemplateBlock.CompressedTemplateBlock(stateIndex, safeBlock.getBlockPos(),tag));
                Integer tagBytes = getTagBytes(tag);
                if (tagBytes == null)
                    tagBytes += 8;
                byteCount += tagBytes;
            }

            byteCount += 18;

            if (byteCount > maxByteSize)
            {
                System.out.println("messageIndex " + messageIndex + " byteCount " + byteCount);
                //basically create a new message when the current message is full
                stateLegendList.add(new ArrayList<>());
                firstPlacedList.add(new ArrayList<>());
                secondPlacedList.add(new ArrayList<>());
                messageIndex++;
                byteCount = 0;
            }

        }

        for (TemplateBlock block : furnitureBlocks)
        {
            //check if templateblock can be safely sent to the server otherwise replace it with an air block
            TemplateBlock safeBlock = ByteBufCustomUtils.getSafeTemplateBlock(block,Minecraft.getMinecraft().player);

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
                secondPlacedList.get(messageIndex).add(new MessagePlaceListsTemplateBlock.CompressedTemplateBlock(stateIndex, safeBlock.getBlockPos()));
            else
            {
                NBTTagCompound tag = safeBlock.getTileEntity().serializeNBT();
                secondPlacedList.get(messageIndex).add(new MessagePlaceListsTemplateBlock.CompressedTemplateBlock(stateIndex, safeBlock.getBlockPos(),safeBlock.getTileEntity().serializeNBT()));
                Integer tagBytes = getTagBytes(tag);
                if (tagBytes == null)
                    tagBytes += 8;
                byteCount += tagBytes;
            }
            byteCount += 18;

            if (byteCount > maxByteSize)
            {
                System.out.println("messageIndex " + messageIndex + " byteCount " + byteCount);
                //basically create a new message when the current message is full
                stateLegendList.add(new ArrayList<>());
                firstPlacedList.add(new ArrayList<>());
                secondPlacedList.add(new ArrayList<>());
                messageIndex++;
                byteCount = 0;
           }
        }
        System.out.println("messageIndex " + messageIndex + " byteCount " + byteCount );

        for (int i = 0; i <= messageIndex; i++)
        {
            NetworkManager.PLACE_MESSAGE_SEND_QUEUE.addFirst(new MessagePlaceListsTemplateBlock(position2, i, messageIndex, stateLegendList.get(i), firstPlacedList.get(i), secondPlacedList.get(i)));
        }
    }

    public static class ChunkPosComparator implements Comparator<ChunkPos>
    {
        @Override
        public int compare(ChunkPos o1, ChunkPos o2)
        {
            return  o1.x*o1.z - o2.x*o2.z;
        }
    }

    public static class PlaceControlledBlockList
    {
        public ArrayList<TemplateBlock> firstBlocksToServer;
        public ArrayList<TemplateBlock> furnitureBlocks;

        public PlaceControlledBlockList()
        {
            firstBlocksToServer = new ArrayList<>();
            furnitureBlocks = new ArrayList<>();

        }
        public PlaceControlledBlockList(TemplateBlock block, boolean firstBlocks)
        {
            this();
            this.addBlock(block,firstBlocks);
        }

        public void addBlock(TemplateBlock block, boolean firstBlocks)
        {
            if(firstBlocks)
            {
                firstBlocksToServer.add(block);
            }
            else
            {
                furnitureBlocks.add(block);
            }
        }
    }


    public static void splitAndSendBlocksRelToPosToServer(BlockPos position2, ArrayList<TemplateBlock> firstBlocksToServer, ArrayList<TemplateBlock> furnitureBlocks, BlockPos startUpdatePos, BlockPos endUpdatePos)
    {
        ArrayList<ArrayList<IBlockState>> stateLegendList = new ArrayList<>();
        stateLegendList.add(new ArrayList<>());
        ArrayList<ArrayList<MessagePlaceListsTemplateBlock.CompressedTemplateBlock>> firstPlacedList = new ArrayList<>();
        firstPlacedList.add(new ArrayList<>());
        ArrayList<ArrayList<MessagePlaceListsTemplateBlock.CompressedTemplateBlock>> secondPlacedList = new ArrayList<>();
        secondPlacedList.add(new ArrayList<>());

        ArrayList<ArrayList<TemplateBlock>> clientCheckPlacedList = new ArrayList<>();
        clientCheckPlacedList.add(new ArrayList<>());

        int messageIndex = 0;
        int byteCount = 0;
        int maxByteSize = 29000;


        int blockCount = 0;         //Block count to know when to add a lock te clientCheckPlacedList
        int blockCheckDelay = 10;   //How many blocks need to be skipped before a next block is added to clientCheckPlacedList

        if(firstBlocksToServer.size() < 2900)
            blockCheckDelay = firstBlocksToServer.size()/60;
        if(blockCheckDelay == 0)
            blockCheckDelay = 1;

        for (TemplateBlock block : firstBlocksToServer)
        {

            //check if templateblock can be safely sent to the server otherwise replace it with an air block
            TemplateBlock safeBlock = ByteBufCustomUtils.getSafeTemplateBlock(block, Minecraft.getMinecraft().player);

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

            if(block.getTileEntity() == null)
                firstPlacedList.get(messageIndex).add(new MessagePlaceListsTemplateBlock.CompressedTemplateBlock(stateIndex, block.getBlockPos()));
            else
            {
                NBTTagCompound tag = block.getTileEntity().serializeNBT();
                firstPlacedList.get(messageIndex).add(new MessagePlaceListsTemplateBlock.CompressedTemplateBlock(stateIndex, block.getBlockPos(),tag));
                Integer tagBytes = getTagBytes(tag);
                if (tagBytes == null)
                    tagBytes += 0;
                byteCount += tagBytes;
            }
            byteCount += 10;
            blockCount++;

            //add certain blocks to the clientCheckPlacedList to be checked if they are actually present in the client world
            if (blockCount % blockCheckDelay == 0)
            {
                tryAddBlockToClientCheckPlacedList(position2, clientCheckPlacedList, messageIndex, block, state);
            }

            if (byteCount > maxByteSize)
            {
//                System.out.println("messageIndex " + messageIndex + " byteCount " + byteCount);
                //basically create a new message when the current message is full
                stateLegendList.add(new ArrayList<>());
                firstPlacedList.add(new ArrayList<>());
                secondPlacedList.add(new ArrayList<>());
                messageIndex++;
                byteCount = 0;

                clientCheckPlacedList.add(new ArrayList<>());
                blockCount = 0;
            }

        }

        if(furnitureBlocks.size() < 2900)
            blockCheckDelay = furnitureBlocks.size()/30;
        if(blockCheckDelay == 0)
            blockCheckDelay = 1;

        for (TemplateBlock block : furnitureBlocks)
        {
            IBlockState state = block.getBlockState();
            int stateIndex = 0;
            if (stateLegendList.get(messageIndex).contains(state))
                stateIndex = stateLegendList.get(messageIndex).indexOf(state);
            else
            {
                stateIndex = stateLegendList.get(messageIndex).size();
                stateLegendList.get(messageIndex).add(state);
                byteCount += 8;
            }

            if(block.getTileEntity() == null)
                secondPlacedList.get(messageIndex).add(new MessagePlaceListsTemplateBlock.CompressedTemplateBlock(stateIndex, block.getBlockPos()));
            else
            {
                NBTTagCompound tag = block.getTileEntity().serializeNBT();
                secondPlacedList.get(messageIndex).add(new MessagePlaceListsTemplateBlock.CompressedTemplateBlock(stateIndex, block.getBlockPos(),block.getTileEntity().serializeNBT()));
                Integer tagBytes = getTagBytes(tag);
                if (tagBytes == null)
                    tagBytes += 0;
                byteCount += tagBytes;
            }
            byteCount += 10;
            blockCount++;

            //add certain blocks to the clientCheckPlacedList to be checked if they are actually present in the client world
            if (blockCount % blockCheckDelay == 0)
            {
                tryAddBlockToClientCheckPlacedList(position2, clientCheckPlacedList, messageIndex, block, state);
            }

            if (byteCount > maxByteSize)
            {
//                System.out.println("messageIndex " + messageIndex + " byteCount " + byteCount);
                //basically create a new message when the current message is full
                stateLegendList.add(new ArrayList<>());
                firstPlacedList.add(new ArrayList<>());
                secondPlacedList.add(new ArrayList<>());
                messageIndex++;
                byteCount = 0;

                clientCheckPlacedList.add(new ArrayList<>());
                blockCount = 0;
           }

        }
//        System.out.println("messageIndex " + messageIndex + " byteCount " + byteCount );


        for (int i = 0; i <= messageIndex; i++)
        {
            NetworkManager.PLACE_MESSAGE_SEND_QUEUE.addFirst(new MessagePlaceListsTemplateBlock(position2, i, messageIndex, stateLegendList.get(i), firstPlacedList.get(i), secondPlacedList.get(i)));
            BLOCKS_TO_CHECK_FOR_PLACEMENT.addFirst(clientCheckPlacedList.get(i));
            currMessageNeededToCompleteAction++;
        }
    }

    private static void tryAddBlockToClientCheckPlacedList(BlockPos position2, ArrayList<ArrayList<TemplateBlock>> clientCheckPlacedList, int messageIndex, TemplateBlock block, IBlockState state)
    {
        BlockPos offset = position2.add(block.getBlockPos());
        if (!(offset.getY() < 1 || state.getBlock() instanceof BlockFalling || state.getBlock() instanceof BlockDynamicLiquid || state.getMaterial()== Material.SNOW || PlacementHelper.isPlant(state)|| PlacementHelper.isWireRailOrNeedsConnection(state) || state.getMaterial() == Material.FIRE))
            clientCheckPlacedList.get(messageIndex).add(new TemplateBlock(block.getFace(), offset, state));
    }


    public static void setCurrMessageBlocksToCheckForPlacement(ArrayList<TemplateBlock> blocks)
    {
        CURR_MESSAGE_BLOCKS_TO_CHECK_FOR_PLACEMENT = blocks;
        CURR_MESSAGE_CHECK_COMPLETE = blocks == null || blocks.isEmpty();
        if(CURR_MESSAGE_CHECK_COMPLETE)
        {
            notifyEndPlacement();
            System.out.println("No Blocks to check?");
        }

    }

    @SideOnly(Side.CLIENT)
    public static boolean checkPlacement()
    {
        if(CURR_MESSAGE_CHECK_COMPLETE || CURR_MESSAGE_BLOCKS_TO_CHECK_FOR_PLACEMENT == null)
            return true;

        boolean allThere = false;
        long currTime = System.currentTimeMillis();
//        System.out.println((currTime - LAST_PLACEMENT_CHECK) + " ms since last check");
        if(currTime - LAST_PLACEMENT_CHECK > 5)
        {
            if(checkTimesCount > checkTimesBeforeResend)
            {
                NetworkManager.RESEND_LAST_PLACE_MESSAGE_SENT = true;
                checkTimesCount =0;
                return false;
            }

            allThere = true;
            World world = Minecraft.getMinecraft().world;
            for (TemplateBlock block: CURR_MESSAGE_BLOCKS_TO_CHECK_FOR_PLACEMENT)
            {
                if(world != null && block != null && block.getBlockPos() != null && block.getBlockState() != null &&
                        !world.getBlockState(block.getBlockPos()).getBlock().getDefaultState() .getMaterial().equals(block.getBlockState().getBlock().getDefaultState().getMaterial()))
                {
                    allThere = false;
                    break;
                }
            }

            if(allThere)
            {
                confirmedPlaceMessageCompletion++;
                checkTimesCount =0;
                CURR_MESSAGE_CHECK_COMPLETE = true;
                CURR_MESSAGE_BLOCKS_TO_CHECK_FOR_PLACEMENT =null;

                if(BLOCKS_TO_CHECK_FOR_PLACEMENT.isEmpty())
                {
                    confirmedPlaceMessageCompletion = 0;
                    currMessageNeededToCompleteAction = 0;

                    notifyEndPlacement();
                    if(AdvCreation.getMode() == EnumMainMode.PLACE)
                    {
                        GuiOverlayManager.TEMPLATE_SELECTION.resetBarLength();
                        GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode(),false);
                    }
                    else
                    {
                        GuiOverlayManager.INVENTORY_SELECTION.resetBarLength();
                        GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());
                    }

                }
            }
            else
            {
//                TemplateBlock block = CURR_MESSAGE_BLOCKS_TO_CHECK_FOR_PLACEMENT.get(0);
//                Minecraft.getMinecraft().world.sendBlockUpdated(block.getBlockPos(),block.getBlockState(),block.getBlockState(),31);
                checkTimesCount++;
            }


            LAST_PLACEMENT_CHECK =   System.currentTimeMillis();
//            try
//            {
//                Thread.sleep(NetworkPlaceBlockListFormatter.sleeptimeAfterCheckOrSend);
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
        }

        return  allThere;
    }

    public static void cancelPlacement()
    {
        NetworkManager.PLACE_MESSAGE_SEND_QUEUE.clear();
        NetworkManager.LAST_PLACE_MESSAGE_SENT_TO_SERVER = null;
        BLOCKS_TO_CHECK_FOR_PLACEMENT.clear();

        checkTimesCount =0;
        CURR_MESSAGE_CHECK_COMPLETE = true;
        CURR_MESSAGE_BLOCKS_TO_CHECK_FOR_PLACEMENT =null;
        confirmedPlaceMessageCompletion = 0;
        currMessageNeededToCompleteAction = 0;
        notifyEndPlacement();
        if(AdvCreation.getMode() == EnumMainMode.PLACE)
        {
//            GuiOverlayManager.TEMPLATE_SELECTION.resetBarLength();
//            GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode(),false);
            GuiOverlayManager.TEMPLATE_SELECTION.updateToolModeIndication(AdvCreation.getMode(),"Canceled",true,true);
        }
        else
        {
//            GuiOverlayManager.INVENTORY_SELECTION.resetBarLength();
//            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode());
            GuiOverlayManager.INVENTORY_SELECTION.updateToolModeIndication(AdvCreation.getMode(),"Canceled",true,true);
        }
    }

    public static void notifyStartPlacementOperation()
    {
        notifyStartPlacementOperation(LARGE_PLACEMENT);
    }
    public static void notifyStartPlacementOperation(boolean largeOperation)
    {
        PLACEMENT_OPERATION = true;
        LARGE_PLACEMENT = largeOperation;
    }

    public static void notifyEndPlacement()
    {
        PLACEMENT_OPERATION = false;
        LARGE_PLACEMENT = false;
        UndoFunctionality.alreadyDidUndoRedoAction = false;
    }

    public static boolean isPlacementOperationInProgress()
    {
        return PLACEMENT_OPERATION;
    }

    public static boolean isLargePlacementOperationInProgress()
    {
        return PLACEMENT_OPERATION && LARGE_PLACEMENT;
    }
}
