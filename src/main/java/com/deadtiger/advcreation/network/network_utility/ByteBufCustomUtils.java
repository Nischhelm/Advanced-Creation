package com.deadtiger.advcreation.network.network_utility;

import com.deadtiger.advcreation.network.message.MessagePlaceListsTemplateBlock;
import com.deadtiger.advcreation.template.TemplateBlock;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.Sys;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;

public class ByteBufCustomUtils
{
    public static HashMap<Block, Item> customBlockItemMap = new HashMap<>();
    public static HashMap<Item, Block> customItemBlockMap = new HashMap<>();
    public static HashMap<Block, Integer> customBlockIdMap = new HashMap<>();
    public static HashMap<Integer, Block> customIdBlockMap = new HashMap<>();

    public static boolean initialisedMapClient = false;
    public static boolean initialisedMapServer = false;

    public static void writeIBlockState(ByteBuf to, IBlockState state)
    {

        initialiseCustomBlockItemMap();

        PacketBuffer pb = new PacketBuffer(to);
        if (state.getMaterial() == Material.AIR)
            pb.writeShort(-1);
        else
        {
            int id = getItemIdFromState(state);
            pb.writeShort(id);
//            if (Item.getItemFromBlock(state.getBlock()) instanceof ItemAir || item == null)
//                System.out.println("slipped through the cracks " + "itemblock not found of state " + state.getClass() + " with block " + state.getBlock().getClass());
            //if the item is airblock there are no properties to worry about
            if(id == -1)
                return;
            for (IProperty key : state.getPropertyKeys())
            {
                if (state.getValue(key) instanceof Enum<?>)
                    writeEnumValue(pb,(Enum<?>) state.getValue(key));
                else if (state.getValue(key) instanceof Boolean)
                    pb.writeBoolean((Boolean) state.getValue(key));
                else if (state.getValue(key) instanceof Integer)
                    pb.writeInt((Integer) state.getValue(key));
            }
        }
    }

    private static int getItemIdFromState(IBlockState state)
    {
        Item item = net.minecraftforge.registries.GameData.getBlockItemMap().get(state.getBlock());
        int id = -1;
        if (item != null)
            id = Item.getIdFromItem(item);
        else
        {
            if (customBlockItemMap.containsKey(state.getBlock()))
            {
                id = Item.getIdFromItem(customBlockItemMap.get(state.getBlock()));

            }
            else if (customBlockIdMap.containsKey(state.getBlock()))
                id = customBlockIdMap.get(state.getBlock());
            else
                System.out.println("itemblock not found of state  " + state.getBlock());
        }
        return id;
    }


    public static IBlockState readIBlockState(ByteBuf from)
    {
        initialiseCustomItemBlockMap();

        PacketBuffer pb = new PacketBuffer(from);
        int id = pb.readShort();
        if (id == -1)
            return Blocks.AIR.getDefaultState();
        else
        {
            IBlockState state = Blocks.AIR.getDefaultState();
            if (id >= 0)
            {
                Item item = Item.getItemById(id);

                if (item instanceof ItemBlock)
                    state = ((ItemBlock) item).getBlock().getDefaultState();
                else if (item instanceof ItemDoor)
                    state = customItemBlockMap.get(item).getDefaultState();
                else if (item instanceof ItemBed)
                    state = Blocks.BED.getDefaultState();
                else if (item instanceof ItemSign)
                    state = Blocks.WALL_SIGN.getDefaultState();
                else
                    System.out.println("there is another block i don't know of");
            }
            else if (customIdBlockMap.containsKey(id))
                state = customIdBlockMap.get(id).getDefaultState();

            for (IProperty key : state.getPropertyKeys())
            {
                Class<Enum<?>> enumClass = (Class<Enum<?>>) state.getValue(key).getClass();

                if (state.getValue(key) instanceof Enum<?>)
                {
                    Enum<?> enu;
                    if(state.getBlock() instanceof BlockSilverfish)
                        enu = readSilverFishEnumValue(pb);
                    else
                        enu = readEnumValue(enumClass, pb);
                    state = enumHelper(enu, state, key);
                }
                else if (state.getValue(key) instanceof Boolean)
                    state = state.withProperty(key, pb.readBoolean());
                else if (state.getValue(key) instanceof Integer)
                    state = state.withProperty(key,(int)pb.readInt());



            }
            return state;
        }


    }

    private static void initialiseCustomBlockItemMap()
    {

        if (!initialisedMapClient)
        {
            //custom items and id's for the client to encode blocks
            customBlockItemMap.put(Blocks.JUNGLE_DOOR, Items.JUNGLE_DOOR);
            customBlockItemMap.put(Blocks.ACACIA_DOOR, Items.ACACIA_DOOR);
            customBlockItemMap.put(Blocks.DARK_OAK_DOOR, Items.DARK_OAK_DOOR);
            customBlockItemMap.put(Blocks.BIRCH_DOOR, Items.BIRCH_DOOR);
            customBlockItemMap.put(Blocks.IRON_DOOR, Items.IRON_DOOR);
            customBlockItemMap.put(Blocks.OAK_DOOR, Items.OAK_DOOR);
            customBlockItemMap.put(Blocks.SPRUCE_DOOR, Items.SPRUCE_DOOR);
            customBlockItemMap.put(Blocks.BED, Items.BED);

            customBlockIdMap.put(Blocks.WALL_SIGN, -2);
            customBlockIdMap.put(Blocks.STANDING_SIGN, -3);
            customBlockIdMap.put(Blocks.DOUBLE_STONE_SLAB, -4);
            customBlockIdMap.put(Blocks.DOUBLE_WOODEN_SLAB, -5);
            customBlockIdMap.put(Blocks.FIRE, -6);
            customBlockIdMap.put(Blocks.WATER, -7);
            customBlockIdMap.put(Blocks.FLOWING_WATER, -8);
            customBlockIdMap.put(Blocks.WHEAT, -9);
            customBlockIdMap.put(Blocks.PUMPKIN_STEM, -10);
            customBlockIdMap.put(Blocks.BEETROOTS, -11);
            customBlockIdMap.put(Blocks.MELON_STEM, -12);
            customBlockIdMap.put(Blocks.SKULL, -13);
            customBlockIdMap.put(Blocks.WALL_BANNER,-14);
            customBlockIdMap.put(Blocks.REDSTONE_WIRE,-15);
            customBlockIdMap.put(Blocks.UNPOWERED_REPEATER,-16);
            customBlockIdMap.put(Blocks.UNPOWERED_COMPARATOR,-17);
            customBlockIdMap.put(Blocks.CAKE,-18);
            customBlockIdMap.put(Blocks.BREWING_STAND,-19);
            customBlockIdMap.put(Blocks.CAULDRON,-20);
            customBlockIdMap.put(Blocks.TRIPWIRE,-21);
            customBlockIdMap.put(Blocks.LAVA, -22);
            customBlockIdMap.put(Blocks.PURPUR_DOUBLE_SLAB,-23);
            customBlockIdMap.put(Blocks.DOUBLE_STONE_SLAB2,-24);

            initialisedMapClient = true;
        }


    }

    private static void initialiseCustomItemBlockMap()
    {
        if (!initialisedMapServer)
        {
            //custom blocks for the the server to decode blocks
            customItemBlockMap.put(Items.JUNGLE_DOOR, Blocks.JUNGLE_DOOR);
            customItemBlockMap.put(Items.ACACIA_DOOR, Blocks.ACACIA_DOOR);
            customItemBlockMap.put(Items.DARK_OAK_DOOR, Blocks.DARK_OAK_DOOR);
            customItemBlockMap.put(Items.BIRCH_DOOR, Blocks.BIRCH_DOOR);
            customItemBlockMap.put(Items.IRON_DOOR, Blocks.IRON_DOOR);
            customItemBlockMap.put(Items.OAK_DOOR, Blocks.OAK_DOOR);
            customItemBlockMap.put(Items.SPRUCE_DOOR, Blocks.SPRUCE_DOOR);
            customItemBlockMap.put(Items.BED, Blocks.BED);

            customIdBlockMap.put(-2, Blocks.WALL_SIGN);
            customIdBlockMap.put(-3, Blocks.STANDING_SIGN);
            customIdBlockMap.put(-4, Blocks.DOUBLE_STONE_SLAB);
            customIdBlockMap.put(-5, Blocks.DOUBLE_WOODEN_SLAB);
            customIdBlockMap.put(-6, Blocks.FIRE);
            customIdBlockMap.put(-7, Blocks.WATER);
            customIdBlockMap.put(-8, Blocks.FLOWING_WATER);
            customIdBlockMap.put(-9, Blocks.WHEAT);
            customIdBlockMap.put(-10, Blocks.PUMPKIN_STEM);
            customIdBlockMap.put(-11, Blocks.BEETROOTS);
            customIdBlockMap.put(-12, Blocks.MELON_STEM);
            customIdBlockMap.put(-13, Blocks.SKULL);
            customIdBlockMap.put(-14,Blocks.WALL_BANNER);
            customIdBlockMap.put(-15,Blocks.REDSTONE_WIRE);
            customIdBlockMap.put(-16,Blocks.UNPOWERED_REPEATER);
            customIdBlockMap.put(-17,Blocks.UNPOWERED_COMPARATOR);
            customIdBlockMap.put(-18,Blocks.CAKE);
            customIdBlockMap.put(-19,Blocks.BREWING_STAND);
            customIdBlockMap.put(-20,Blocks.CAULDRON);
            customIdBlockMap.put(-21,Blocks.TRIPWIRE);
            customIdBlockMap.put(-22,Blocks.LAVA);
            customIdBlockMap.put(-23,Blocks.PURPUR_DOUBLE_SLAB);
            customIdBlockMap.put(-24,Blocks.DOUBLE_STONE_SLAB2);

            initialisedMapServer = true;
        }

    }

    /***
     *
     * writes only that which is strictly necesary for placement of te templateblocks
     * only blockpos and blockstate
     *
     * @param to
     * @param templateBlock
     */
    public static void writeCompressedTemplateBlock(ByteBuf to, MessagePlaceListsTemplateBlock.CompressedTemplateBlock templateBlock)
    {
        PacketBuffer pb = new PacketBuffer(to);
        pb.writeBlockPos(templateBlock.getPos());
        pb.writeShort(templateBlock.getBlockStateIndex());
        pb.writeCompoundTag(templateBlock.getTag());
    }

    public static MessagePlaceListsTemplateBlock.CompressedTemplateBlock readCompressedTemplateBlock(ByteBuf from)
    {

        PacketBuffer pb = new PacketBuffer(from);
        BlockPos pos = pb.readBlockPos();
        int state = pb.readShort();
        NBTTagCompound tag = null;
        try
        {
            tag = pb.readCompoundTag();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new MessagePlaceListsTemplateBlock.CompressedTemplateBlock(state, pos, tag);
    }

    public static PacketBuffer writeEnumValue(PacketBuffer pb, Enum<?> value)
    {
        return pb.writeVarInt(value.ordinal());
    }

    public static <T extends Enum<?>> T readEnumValue(Class<T> enumClass, PacketBuffer pb)
    {
        int readInt = pb.readVarInt();
        return (T) ((Enum[]) enumClass.getEnumConstants())[readInt];
    }

    public static BlockSilverfish.EnumType readSilverFishEnumValue(PacketBuffer pb)
    {
        int readInt = pb.readVarInt();
        return  BlockSilverfish.EnumType.values()[readInt];
    }


    public static <T extends Enum<T>, V extends T> IBlockState enumHelper(Enum<T> enu, IBlockState state, IProperty key)
    {
        return state.withProperty(key, (V) enu);
    }

    public static BlockPos readBlockPos(ByteBuf buff)
    {
        PacketBuffer pb = new PacketBuffer(buff);
        return pb.readBlockPos();
    }

    public static void writeBlockPos(ByteBuf buff, BlockPos pos)
    {
        PacketBuffer pb = new PacketBuffer(buff);
        pb.writeBlockPos(pos);
    }

    public static <T extends Enum<?>> T readCustomEnumValue(ByteBuf buf, Class<T> enumClass)
    {
        PacketBuffer pb = new PacketBuffer(buf);
        return readEnumValue(enumClass, pb);
    }

    public static void writeCustomEnumValue(ByteBuf buf, Enum<?> enu)
    {
        PacketBuffer pb = new PacketBuffer(buf);
        writeEnumValue(pb,enu);
    }

    public static void writeCompoundTag(ByteBuf buf,@Nullable NBTTagCompound nbt)
    {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeCompoundTag(nbt);
    }

    @Nullable
    public static NBTTagCompound readCompoundTag(ByteBuf buf) throws IOException
    {
        PacketBuffer pb = new PacketBuffer(buf);
        return pb.readCompoundTag();
    }

    /***
     * Check if the blockstate is able to be translated to a valid itemId if not then replace it with an BlockAir and
     * send a message to the player that the block was not recognized
     * This should prevent any crashes from happening because of errors in the sending of the templateblock to the server
     * example crash:
     * [11:47:29] [Netty Server IO #1/ERROR] [FML]: FMLIndexedMessageCodec exception caught
     * io.netty.handler.codec.DecoderException: io.netty.handler.codec.EncoderException: java.io.EOFException: fieldSize is too long! Length is 65535, but maximum is 8
     *
     * @param block
     * @return
     */
    public static TemplateBlock getSafeTemplateBlock(TemplateBlock block)
    {
        return getSafeTemplateBlock(block,null);
    }

    public static TemplateBlock getSafeTemplateBlock(TemplateBlock block, EntityPlayerSP player)
    {
        initialiseCustomBlockItemMap();
        if (block.getBlockState().getMaterial() == Material.AIR)
            return block;
        else
        {
            int id = getItemIdFromState(block.getBlockState());
            if(id == -1)
            {
                if(player != null)
                    player.sendStatusMessage(new TextComponentString("Block " + block.getBlockState().getBlock().getLocalizedName() + " is incompatible with the Advanced Creation mod. Place it in First-Person perspective or contact the mod dev: GraphiteGames").setStyle((new Style()).setColor(TextFormatting.RED)),false);
                return new TemplateBlock(EnumFacing.NORTH,block.getBlockPos(), Blocks.AIR.getDefaultState());
            }

        }
        return block;
    }

}
