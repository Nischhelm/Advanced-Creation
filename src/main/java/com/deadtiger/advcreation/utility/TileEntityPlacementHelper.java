package com.deadtiger.advcreation.utility;

//import com.bewitchment.common.block.tile.entity.TileEntityStatue;
import com.deadtiger.advcreation.plugin.transformer.GeneralTransformer;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TileEntityPlacementHelper
{
    private static HashMap<Block,Method> mapBlockToTileEntityFormatFunction = new HashMap<>();

    static
    {
        try
        {
//            addformatFunctionToBlock(Blocks.SKULL,"formatSkullTileEntity");
            addformatFunctionToBlock(Blocks.STANDING_SIGN,"formatSignTileEntity");
            addformatFunctionToBlock(Blocks.WALL_SIGN,"formatSignTileEntity");
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    public static void formatTileEntityForBlock(IBlockState state, TileEntity placedTileEntity, TileEntity sentTileEntity, BlockPos pos, World worldIn) throws InvocationTargetException, IllegalAccessException
    {
        if(mapBlockToTileEntityFormatFunction.containsKey(state.getBlock()))
        {
            Method function = mapBlockToTileEntityFormatFunction.get(state.getBlock());
            function.invoke(null,placedTileEntity,sentTileEntity,pos,worldIn);
        }
        else
        {
            if(placedTileEntity != null && sentTileEntity != null)
            {
                BlockPos placedPos = placedTileEntity.getPos();

                if((worldIn != null &&  worldIn.isRemote) && placedTileEntity.getWorld() == null)
                    placedTileEntity.setWorld(FakeWorld.INSTANCE.setPreviewBlockState(state));
                if((worldIn != null && worldIn.isRemote) && sentTileEntity.getWorld() == null)
                    sentTileEntity.setWorld(FakeWorld.INSTANCE.setPreviewBlockState(state));

//                if(sentTileEntity instanceof TileEntityStatue)
//                    ((TileEntityStatue) sentTileEntity).name = "statue";
                placedTileEntity.deserializeNBT(sentTileEntity.serializeNBT());
                placedTileEntity.setPos(placedPos);
                placedTileEntity.markDirty();
            }

        }

    }

    public static void addformatFunctionToBlock(Block block, String methodName) throws NoSuchMethodException
    {
       mapBlockToTileEntityFormatFunction.put(block, TileEntityPlacementHelper.class.getMethod(methodName, TileEntity.class, TileEntity.class, BlockPos.class, World.class));
    }

    public static void formatSkullTileEntity(TileEntity placedTileEntity, TileEntity sentTileEntity, BlockPos pos, World worldIn)
    {
        TileEntitySkull sentTileEntitySkull = null;
        if(sentTileEntity != null)
            sentTileEntitySkull = (TileEntitySkull) sentTileEntity;
        else
            System.out.println("No TileEntity received");

        if(placedTileEntity instanceof TileEntitySkull && sentTileEntity != null)
        {
            TileEntitySkull tileentityskull = (TileEntitySkull)placedTileEntity;

            GameProfile gameprofile = sentTileEntitySkull.getPlayerProfile();
            if (gameprofile != null)
            {

                tileentityskull.setPlayerProfile(gameprofile);
            }
            else
            {
                tileentityskull.setType(sentTileEntitySkull.getSkullType());
            }

            tileentityskull.setSkullRotation(sentTileEntitySkull.getSkullRotation());
            if(pos != null && worldIn != null)
                Blocks.SKULL.checkWitherSpawn(worldIn, pos, tileentityskull);
        }
    }

    public static void formatSignTileEntity(TileEntity placedTileEntity, TileEntity sentTileEntity, BlockPos pos, World worldIn)
    {
        TileEntitySign sentTileEntitySign = null;
        if(sentTileEntity != null)
            sentTileEntitySign = (TileEntitySign) sentTileEntity;
        else
            System.out.println("No TileEntity received");

        if(placedTileEntity instanceof TileEntitySign && sentTileEntity != null)
        {
            TileEntitySign tileentitySign = (TileEntitySign) placedTileEntity;

            tileentitySign.setPlayer(sentTileEntitySign.getPlayer());
            for(int i=0; i< sentTileEntitySign.signText.length;i++)
            {
                tileentitySign.signText[i]=sentTileEntitySign.signText[i];
            }
            tileentitySign.setEditable(true);
        }
    }

    public static boolean blockHasTileEntity(Block block)
    {
        return mapBlockToTileEntityFormatFunction.containsKey(block);
    }
}
