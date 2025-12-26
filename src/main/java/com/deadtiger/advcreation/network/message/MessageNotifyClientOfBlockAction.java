package com.deadtiger.advcreation.network.message;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.network.NetworkHandler;
import com.deadtiger.advcreation.network.network_utility.ByteBufCustomUtils;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.undo_actions.UndoFunctionality;
import com.deadtiger.advcreation.utility.FakeWorld;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.deadtiger.advcreation.network.message.MessagePlaceListsTemplateBlock.CompressedTemplateBlock;

public class MessageNotifyClientOfBlockAction extends MessageBase<MessageNotifyClientOfBlockAction>
{

    public String playerName;
    public ArrayList<IBlockState> stateLegendList;
    public ArrayList<CompressedTemplateBlock> replacedBlocks;

    public MessageNotifyClientOfBlockAction()
    {

    }

    public MessageNotifyClientOfBlockAction(ArrayList<IBlockState> stateLegendList, ArrayList<CompressedTemplateBlock> replacedBlocks, String playerName)
    {
        this.playerName = playerName;
        this.stateLegendList = stateLegendList;
        this.replacedBlocks = replacedBlocks;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientSide(MessageNotifyClientOfBlockAction message, EntityPlayer player)
    {
        if(Minecraft.getMinecraft().playerController.isInCreativeMode())
        {
            Action vanillaPlayerAction = new Action();
            for (CompressedTemplateBlock compBlock: message.replacedBlocks)
            {
                IBlockState blockState = message.stateLegendList.get(compBlock.blockStateIndex);
                TileEntity tileEntity = null;
                if(blockState.getBlock().hasTileEntity(blockState) && compBlock.getTag() != null)
                {
                    tileEntity = AdvCreation.proxy.createTileEntityOf(blockState);
                    tileEntity.deserializeNBT(compBlock.getTag());
                    if(tileEntity instanceof TileEntitySign)
                    {
                        TileEntitySign tileEntitySign = (TileEntitySign) tileEntity ;
                        tileEntitySign.setEditable(true);
                        tileEntitySign.setPlayer(Minecraft.getMinecraft().player);
                    }

                }

                TemplateBlock oldBlock = new TemplateBlock(EnumFacing.NORTH, compBlock.getPos(), blockState,tileEntity);
                // add new action to action history
                vanillaPlayerAction.add(oldBlock);
            }

            UndoFunctionality.addActionToHistory(vanillaPlayerAction);
        }
    }

    @Override
    public void handleServerSide(MessageNotifyClientOfBlockAction message, EntityPlayer player)
    {

    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int playerNameLength = buf.readShort();
        this.playerName = (String) buf.readCharSequence(playerNameLength, Charset.defaultCharset());

        int sizeStateLegendList = buf.readInt();
        this.stateLegendList = new ArrayList<>();
        for (int i = 0; i < sizeStateLegendList; i++)
        {
            this.stateLegendList.add(ByteBufCustomUtils.readIBlockState(buf));
        }

        int sizeFirstList = buf.readInt();
        this.replacedBlocks = new ArrayList<>();
        for (int i = 0; i < sizeFirstList; i++)
        {
            this.replacedBlocks.add(ByteBufCustomUtils.readCompressedTemplateBlock(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeShort(this.playerName.length());
        buf.writeCharSequence(this.playerName, Charset.defaultCharset());

        //write the list of blockstates in the message
        buf.writeInt(stateLegendList.size());
        for (IBlockState iBlockState : stateLegendList)
        {
            ByteBufCustomUtils.writeIBlockState(buf, iBlockState);
        }
        //write the list of blocks to be placed first
        buf.writeInt(replacedBlocks.size());
        for (CompressedTemplateBlock templateBlock : replacedBlocks)
        {
            ByteBufCustomUtils.writeCompressedTemplateBlock(buf, templateBlock);
        }
    }



    public static void sendBlocksListToClient( ArrayList<TemplateBlock> firstBlocksToServer, EntityPlayerMP player)
    {
        ArrayList<IBlockState> stateLegendList = new ArrayList<>();
        ArrayList<CompressedTemplateBlock> replacedBlocks = new ArrayList<>();

        CompressBlocks(firstBlocksToServer, stateLegendList, replacedBlocks);

        NetworkHandler.sendBlockActionToClient(new MessageNotifyClientOfBlockAction(stateLegendList,replacedBlocks, player.getName()),player);

    }

    private static void CompressBlocks(ArrayList<TemplateBlock> BlocksToServer, ArrayList<IBlockState> stateLegendList,
                                      ArrayList<CompressedTemplateBlock> activePlacedList)
    {
        for (TemplateBlock block : BlocksToServer)
        {
            //check if templateblock can be safely sent to the client otherwise replace it with an air block
//            TemplateBlock safeBlock = ByteBufCustomUtils.getSafeTemplateBlock(block);
//            IBlockState state = safeBlock.getBlockState();
            IBlockState state = block.getBlockState();
            int stateIndex;
            if (stateLegendList.contains(state))
                stateIndex = stateLegendList.indexOf(state);
            else
            {
                stateIndex = stateLegendList.size();
                stateLegendList.add(state);
            }

            if(block.getTileEntity() == null)
                activePlacedList.add(new CompressedTemplateBlock(stateIndex, block.getBlockPos()));
            else
            {
                NBTTagCompound tag = block.getTileEntity().serializeNBT();
                activePlacedList.add(new CompressedTemplateBlock(stateIndex, block.getBlockPos(), tag));
            }

        }
    }


}
