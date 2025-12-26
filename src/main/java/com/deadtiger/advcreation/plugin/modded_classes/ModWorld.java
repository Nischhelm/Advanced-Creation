package com.deadtiger.advcreation.plugin.modded_classes;

import com.deadtiger.advcreation.client.player.IsometricCamera;
import com.deadtiger.advcreation.utility.PlacementHelper;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ModWorld
{
    /**
     * Performs a raycast against all blocks in the world. Args : Vec1, Vec2, stopOnLiquid,
     * ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock
     */
    @Nullable
    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock,World world)
    {
        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z))
        {
            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z))
            {
                int i = MathHelper.floor(vec32.x);
                int j = MathHelper.floor(vec32.y);
                int k = MathHelper.floor(vec32.z);
                int l = MathHelper.floor(vec31.x);
                int i1 = MathHelper.floor(vec31.y);
                int j1 = MathHelper.floor(vec31.z);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                String unlocalized_name = block.getUnlocalizedName();
    
    
    
                boolean isCeiling = true;
                if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && (block.canCollideCheck(iblockstate, stopOnLiquid) || (block instanceof BlockLiquid) ))
//                if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid))
                {
                    RayTraceResult raytraceresult = iblockstate.collisionRayTrace(world, blockpos, vec31, vec32);
                    
                    //TODO: debug ignore fluids functionality
                    if( IsometricCamera.IGNORE_FLUIDS &&
                            (block instanceof BlockLiquid))
                    {
                        raytraceresult = null;
                    }

                    if( IsometricCamera.isLeafRaytracingDisabled() &&
                            (!PlacementHelper.isLog(block.getDefaultState()) && PlacementHelper.isPlant(block.getDefaultState())))
                    {
                        raytraceresult = null;
                    }
    
//                    if(ModBlockRendererDispatcher.cuttThroughOn)
//                    {
                            if(!unlocalized_name.equals("tile.air"))
                            {
                                raytraceresult = null;
                                
                                
                            }
                            else
                            {
                                isCeiling = false;
                            }
                            
//                    }
                    
                    
                    if (raytraceresult != null)
                    {
                        return raytraceresult;
                    }
                }
                
                RayTraceResult raytraceresult2 = null;
                int k1 = 200;
                if(IsometricCamera.isPlayerInIsometricPerspective())
                {
                    k1 = 1000;
                }

               
                
                while (k1-- >= 0)
                {
                    if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z))
                    {
                        return null;
                    }
                    
                    if (l == i && i1 == j && j1 == k)
                    {
                        if(returnLastUncollidableBlock)
                            return raytraceresult2;
                        else
                            return null;
                    }
                    
                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;
                    
                    if (i > l)
                    {
                        d0 = (double)l + 1.0D;
                    }
                    else if (i < l)
                    {
                        d0 = (double)l + 0.0D;
                    }
                    else
                    {
                        flag2 = false;
                    }
                    
                    if (j > i1)
                    {
                        d1 = (double)i1 + 1.0D;
                    }
                    else if (j < i1)
                    {
                        d1 = (double)i1 + 0.0D;
                    }
                    else
                    {
                        flag = false;
                    }
                    
                    if (k > j1)
                    {
                        d2 = (double)j1 + 1.0D;
                    }
                    else if (k < j1)
                    {
                        d2 = (double)j1 + 0.0D;
                    }
                    else
                    {
                        flag1 = false;
                    }
                    
                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;
                    
                    if (flag2)
                    {
                        d3 = (d0 - vec31.x) / d6;
                    }
                    
                    if (flag)
                    {
                        d4 = (d1 - vec31.y) / d7;
                    }
                    
                    if (flag1)
                    {
                        d5 = (d2 - vec31.z) / d8;
                    }
                    
                    if (d3 == -0.0D)
                    {
                        d3 = -1.0E-4D;
                    }
                    
                    if (d4 == -0.0D)
                    {
                        d4 = -1.0E-4D;
                    }
                    
                    if (d5 == -0.0D)
                    {
                        d5 = -1.0E-4D;
                    }
                    
                    EnumFacing enumfacing;
                    
                    if (d3 < d4 && d3 < d5)
                    {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    }
                    else if (d4 < d5)
                    {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    }
                    else
                    {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }
                    
                    l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();
                    String unlocalized_name1 = block1.getUnlocalizedName();
                    
//                    if(ModBlockRendererDispatcher.cuttThroughOn)
//                    {
//                        System.out.println("k1 " + k1 +" Block " + block1.getUnlocalizedName() + " canCollide "+block1.canCollideCheck(iblockstate1, stopOnLiquid) + " ceiling " + isCeiling);
                        if(isCeiling)
                        {
                            //System.out.println("k1 " + k1 +" Block " + block1.getUnlocalizedName() + " canCollide "+block1.canCollideCheck(iblockstate1, stopOnLiquid)+ "!leaves " + !unlocalized_name1.equals("tile.leaves")+ " !tallgrass " + !(block1 instanceof BlockTallGrass));

                            if(!((block1 instanceof BlockLiquid || unlocalized_name1.equals("tile.air")) && block1 != Blocks.BEDROCK))
                                continue;
                            else
                                isCeiling =false;
                        }

//                    }
                    
                    if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB)
                    {

                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid) || (block1 instanceof BlockLiquid  && !IsometricCamera.IGNORE_FLUIDS) )
//                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid))
                        {
                            
                            RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(world, blockpos, vec31, vec32);

//                            if( IsometricCamera.IGNORE_FLUIDS &&
//                                    (block instanceof BlockLiquid))
//                            {
//                                raytraceresult1 = null;
//                            }

                            if( IsometricCamera.isLeafRaytracingDisabled() &&
                                    (!PlacementHelper.isLog(block1.getDefaultState()) && PlacementHelper.isPlant(block1.getDefaultState())))
                            {
                                //System.out.println("k1 " + k1 +" Block " + block1.getUnlocalizedName() + " canCollide "+block1.canCollideCheck(iblockstate1, stopOnLiquid)+ "!leaves " + !unlocalized_name1.equals("tile.leaves")+ " !tallgrass " + !(block1 instanceof BlockTallGrass));
                                raytraceresult1 = null;
                            }
                            
                            
    
                            if (raytraceresult1 != null)
                            {
                                return raytraceresult1;
                            }
                        }
                        else
                        {
                            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }
               
                if(returnLastUncollidableBlock)
                    return  raytraceresult2 ;
                else
                    return null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public static RayTraceResult raytraceEntities(Entity entity, Vec3d vec31, Vec3d vec32,World world)
        {
        //try to find if there is a living entity or item under the cursor give that priority
        float d0 = ModPlayerControllerMP.getCustomReachDistance();
        Vec3d lookDirVec = entity.getLook(1.0F);
        Vec3d positionEyesVec = entity.getPositionEyes(1f);

        //get all entities in the vision of the player
        List<Entity> list = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().grow(d0, d0, d0), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity p_apply_1_)
            {
                return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
            }
        }));

        for (int j = 0; j < list.size(); ++j)
        {

            Entity entity1 = list.get(j);
            //get the boundingbox of the entity
            AxisAlignedBB entityCollisionBox = entity1.getEntityBoundingBox().grow((double) entity1.getCollisionBorderSize());
            //calculate if the entity's boundingbox intercedes with the cursor vector
            RayTraceResult raytraceresult = entityCollisionBox.calculateIntercept(vec31, vec32);
            if(raytraceresult != null)
            {
                raytraceresult.typeOfHit = RayTraceResult.Type.ENTITY;
                raytraceresult.entityHit = entity1;
                return raytraceresult;
            }

        }
        return  null;
    }
}
