package com.deadtiger.advcreation.edit_mode.adjust_modes;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_mode.BuildMode;
import com.deadtiger.advcreation.build_mode.utility.EnumDirectionMode;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.client.gui.GuiOverlayManager;
import com.deadtiger.advcreation.handler.ConfigurationHandler;
import com.deadtiger.advcreation.template.TemplateBlock;
import com.deadtiger.advcreation.client.render.RenderTemplate;
import com.deadtiger.advcreation.client.render.RenderPreview;
import com.deadtiger.advcreation.edit_mode.EditMode;
import com.deadtiger.advcreation.tree_creator.CreateAbstrTree;
import com.deadtiger.advcreation.undo_actions.Action;
import com.deadtiger.advcreation.utility.FakeWorld;
import com.deadtiger.advcreation.utility.PlacementHelper;
import com.deadtiger.advcreation.utility.shape_creator.CircleCreator;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;

public class PlantAdjustMode extends BaseAdjustMode
{
    //good positions to create nice circles
    public boolean addPoint = false;
    public boolean jumpToPoint = true;
    public ArrayList<Double> middleCircleLengths = new ArrayList<>();
    public HashMap<Double, Vec3d> middleCircleDict = new HashMap<>();
    
    public int height = 20;
    public int startHeight = -5;
    
    public boolean leftClicked = false;

    public ArrayList<TemplateBlock> treeBlocks = new ArrayList<>();
    public ArrayList<TemplateBlock> grassBlocks = new ArrayList<>();


    public double prevRadius = 0.0;
    public BlockPos prevPos = BlockPos.ORIGIN;
    public IBlockState prevHandState = null;

    public boolean regenerateKeyPressed = false;

    private final ArrayList<Vec3d> previewCircleHelpLineVecPoints = new ArrayList<>();


    public PlantAdjustMode()
    {
        this.finalRightClick = 0;
        this.toolModeName = "Plant Mode";
        this.buttonText = "PLANT";
        this.tooltipText = "Place/Delete Plants";
        this.radius = 5.0;
        this.identificationIndex = 3;

        addLegendEntry(LevelAdjustMode.existingLowerLevelColor,"Will be deleted");
        addLegendEntry(LevelAdjustMode.unchangedColor,"Would be deleted in 'remove plants mode'");
    }

    public void addRadius(double addRadius)
    {
        double maxCircleSize = ConfigurationHandler.toolConfig.CIRCLE_MAX_RADIUS;
        this.radius += addRadius;
        if (this.radius < 0.0)
            this.radius = 0.0;
        else if (this.radius > maxCircleSize)
            this.radius = maxCircleSize;

//        System.out.println("addRadius " + addRadius + " this.radius " + this.radius);
    }

    @Override
    public void addRadius(double addRadius,int repeat)
    {
        double maxCircleSize = ConfigurationHandler.toolConfig.CIRCLE_MAX_RADIUS;
        if(repeat > 1)
            System.out.println("repeat is: " + repeat);

        this.radius += addRadius*repeat;;
        if (this.radius < 0.0)
            this.radius = 0.0;
        else if (this.radius > maxCircleSize)
            this.radius = maxCircleSize;

//        System.out.println("addRadius " + addRadius + " this.radius " + this.radius);
    }
    
    @Override
    public boolean rightClick(Action currAction, EntityPlayer player, int rightClickNumber) {
        if (rightClickNumber >= this.finalRightClick )
        {
            //remove all those air blocks that are used for removal and only add the tree blocks
            EditMode.CURR_TOOL_BLOCKS_LOCK.blockingAttemptAtLocking();
            EditMode.CURR_TOOL_BLOCKS.clear();
            EditMode.CURR_TOOL_BLOCKS.addAll(CreateAbstrTree.addPosToTempateBlockList(treeBlocks, EditMode.START_BLOCK.getBlockPos()) );
            EditMode.CURR_TOOL_BLOCKS_LOCK.releaseLock();
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean leftClick(Action currAction, EntityPlayer player, int rightClickNumber)
    {
        leftClicked = (rightClickNumber >= this.finalRightClick ) && (EditMode.CURR_TOOL_BLOCKS.size() != 0);
        return leftClicked;
    }
    
    @Override
    public boolean startCancelBuilding(int rightClickNumber)
    {
        return EditMode.CURR_TOOL_BLOCKS.size() != 0;
    }

    @Override
    public void addNewBlockRightClick0(TemplateBlock block, Vec3d hitVec, BlockPos hitBlockPos, EnumFacing face)
    {
        EditMode.setStartBlock(new TemplateBlock(face,block.getBlockPos(), Blocks.AIR.getDefaultState()));
        EditMode.setSTARTVEC(hitVec);
        EditMode.setEndVec(hitVec);
        EditMode.updateCurrToolBlocks(block,hitVec);
    }
    
    @Override
    public void updateBlocksRightClick0(TemplateBlock endGlobalblock, Vec3d hitVec, Vec3d newStartVec) {
        CircleCreator.INSTANCE.init();

        normalHitPos = new BlockPos(newStartVec);
        GuiOverlayManager.setPlacePointedCoordinate(normalHitPos);
        GuiOverlayManager.setStartPos(normalHitPos);

        // has 1 of coordinates of the fill Vectors in the same order as the fillVectors
        HashMap<Vec3d,TemplateBlock> blockDict = new HashMap<Vec3d,TemplateBlock>();
        EditMode.FILL_VECTOR_MAP = new HashMap<>();

        //the initial radiusVec
        Vec3d radiusVec = new Vec3d(radius,0,0);
        EditMode.LINEVEC = radiusVec;
        double radiusLength = radiusVec.lengthVector();

        //constraining the max radius
        if (radiusLength > 50.0) {
            double scale = 50.0 / radiusLength;
            radiusLength = 50.0;
            radiusVec = radiusVec.scale(scale);
        }

        // Jumps to one of the nice circles in the mapRadiusLengthToRadialVector HashMap when the length of the radius is close to it
        // These are recorded in the XZ plane and are transformed to other planes when necessary
        if (CircleCreator.INSTANCE.jumpToHardcodedRadius) {
            if (CircleCreator.INSTANCE.isInsideOfHardcodedRadiusRange(radiusLength))
            {
                radiusLength = CircleCreator.INSTANCE.getClosestHardcodedCircleLength(radiusLength);
                radiusVec = CircleCreator.INSTANCE.transformHardcodedCircleToCurrDir(radiusVec, radiusLength,EditMode.WORK_DIRECTION_MODE);
                EditMode.setColor(CircleCreator.INSTANCE.jumpToHardcodedRadiusColor);
            } else
                EditMode.setColor(CircleCreator.INSTANCE.freeRadiusColor);
        } else
            EditMode.setColor(CircleCreator.INSTANCE.freeRadiusColor);
        
        //logic to add a point to the middleCircleDicts that allows to save nice circle sizes when leftclicked
        if (CircleCreator.INSTANCE.addHardcodedRadius)
            CircleCreator.INSTANCE.addHardcodedRadius(radiusVec, radiusLength);

//        if (radiusLength > 1.0)
//        {
            ArrayList<Double> singleBlockKeys = new ArrayList<>();
            ArrayList<BlockPos> positionsList = CircleCreator.INSTANCE.generateCirclePositions( newStartVec, radiusVec, radiusLength,previewCircleHelpLineVecPoints,EditMode.WORK_DIRECTION_MODE);

            for (BlockPos pos: positionsList)
            {
                CircleCreator.INSTANCE.placePosition(pos,EditMode.START_BLOCK, singleBlockKeys,blockDict,true,EditMode.FILL_VECTOR_MAP,EnumDirectionMode.XZ,EditMode.STARTVEC);
            }

            //get all the keys for which there was no second block and place them too
            for(double key: singleBlockKeys)
            {
                PlacementHelper.placePositionInDict(new BlockPos(EditMode.FILL_VECTOR_MAP.get(key).startVec),EditMode.START_BLOCK,blockDict);
            }

            //check if the selection contains any plant,log or leaves blocks only add those
            World world = Minecraft.getMinecraft().world;
            ArrayList<TemplateBlock> plantBlockToDelete = new ArrayList<>();
            for (TemplateBlock tempBlock:blockDict.values())
            {
                for(int i = -10; i< 30;i++)
                {
                    BlockPos pos  = tempBlock.getBlockPos().add(0,i,0);
                    IBlockState currState = world.getBlockState(pos);
                    if(PlacementHelper.isPlant(currState) || PlacementHelper.isLog(currState))
                        plantBlockToDelete.add(new TemplateBlock(tempBlock.getFace(),pos,tempBlock.getBlockState(),tempBlock.getTileEntity()));

                }
            }
            EditMode.CURR_TOOL_BLOCKS.addAll(plantBlockToDelete);
            if(EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.attemptLocking())
            {
                EditMode.CURR_PREVIEW_OUTLINE_BLOCKS.clear();
                EditMode.CURR_PREVIEW_OUTLINE_BLOCKS.addAll(plantBlockToDelete);
                EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.releaseLock();
            }

//        }
//        else
//        {
//            TemplateBlock tempBlock = new TemplateBlock(EditMode.START_BLOCK.getFace(),new BlockPos(newStartVec),EditMode.START_BLOCK.getBlockState(),EditMode.START_BLOCK.getTileEntity());
//            BlockPos pos  = tempBlock.getBlockPos();
//            IBlockState currState = Minecraft.getMinecraft().world.getBlockState(pos);
//            if(PlacementHelper.isPlant(currState) )
//                EditMode.CURR_TOOL_BLOCKS.add(tempBlock);
//        }



        Minecraft mc = Minecraft.getMinecraft();
        if(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)
        {
            int meta = mc.player.getHeldItemMainhand().getItem().getMetadata(mc.player.getHeldItemMainhand().getMetadata());
            IBlockState  handBlockState = ((ItemBlock) Minecraft.getMinecraft().player.getHeldItemMainhand().getItem())
                    .getBlock().getStateForPlacement(mc.world, BlockPos.ORIGIN, EnumFacing.NORTH,0,0,0,
                            meta,mc.player, EnumHand.MAIN_HAND);

            if(handBlockState == null)
                handBlockState = ((ItemBlock) Minecraft.getMinecraft().player.getHeldItemMainhand().getItem())
                        .getBlock().getDefaultState();

            if(prevRadius != radius || regenerateKeyPressed || (prevHandState != null && !prevHandState.equals(handBlockState) || treeBlocks.isEmpty() ))
            {
                if(handBlockState.getBlock() instanceof BlockBush)
                {
                    handBlockState = generateRandomBushesInRadius(mc, handBlockState);
                    treeBlocks = grassBlocks;
                }
                else
                {
                    initTreeCreators();

                    CreateAbstrTree treeGen = CreateAbstrTree.getRightTreeGenerator(handBlockState,(int)this.radius);

                    //if a tree generator (treeGen) is connected to current handBlockState generate a tree
                    ArrayList<TemplateBlock> list = null;
                    if(treeGen != null)
                        list = treeGen.generate(Minecraft.getMinecraft().world, AdvCreation.rand, EditMode.START_BLOCK.getBlockPos(),(int) radius,0);
                    else
                        treeBlocks.removeAll(treeBlocks);

                    if(list != null)
                    {
                        BlockPos st = EditMode.START_BLOCK.getBlockPos();
                        BlockPos invers = new BlockPos(-st.getX(),-st.getY(),-st.getZ());
                        treeBlocks = CreateAbstrTree.addPosToTempateBlockList(list,invers);
                    }
                }

                prevRadius = radius;
                prevPos = EditMode.START_BLOCK.getBlockPos();


            }
            else if(handBlockState.getBlock() instanceof BlockBush && prevPos != EditMode.START_BLOCK.getBlockPos())
            {
                treeBlocks = new ArrayList<>();
                World worldIn = mc.world;
                for (TemplateBlock block: grassBlocks)
                {
                    BlockPos position = new BlockPos(block.getX_offset(),5,block.getZ_offset());

                    //Look for the ground position to place the grassblock
                    for(int j = 0; j <10 ; j++)
                    {
                        if(PlacementHelper.isNotGroundMaterial(worldIn.getBlockState(EditMode.START_BLOCK.getBlockPos().add(position.down()))))
                            position =position.down();
                        else
                            break;
                    }

                    if(block.getBlockState().getBlock() instanceof BlockDoublePlant)
                        addOtherHalfOfDoublePlant(worldIn, block, position);
                    else
                    {
                        if ( Blocks.TALLGRASS.canBlockStay(worldIn, EditMode.START_BLOCK.getBlockPos().add(position), block.getBlockState()))
                            treeBlocks.add(new TemplateBlock(block.getFace(), position,block.getBlockState(),block.getTileEntity()));
                    }
                }
            }
            prevHandState = handBlockState;
        }
        else
        {
            treeBlocks = new ArrayList<>();
        }
        regenerateKeyPressed = false;

    }



    @Override
    protected void drawHelpLines(EntityPlayer entityplayer, Float partialTicks)
    {
        if(EditMode.STARTVEC != null)
        {
            //draws the cross indicating the mouse position
            Vec3d middleVec = EditMode.STARTVEC;
            RenderTemplate.drawMiddleCross(entityplayer, partialTicks, middleVec);

            if (EditMode.LINEVEC != null)
            {
                float[] comp;
                if(EditMode.DELETE_MODE)
                    comp= CircleCreator.INSTANCE.jumpToHardcodedRadiusColor.getColorComponents(null);
                else
                    comp= CircleCreator.INSTANCE.freeRadiusColor.getColorComponents(null);

                // draw the lines of the circle
                Vec3d newCursorVec = RenderTemplate.drawCircleHelplines(entityplayer, partialTicks, middleVec,previewCircleHelpLineVecPoints,comp[0] ,comp[1] ,comp[2] );

//                //draw the radius line
//                RenderTemplate.drawLine(middleVec.x, middleVec.y, middleVec.z, newCursorVec.x,
//                        newCursorVec.y, newCursorVec.z, entityplayer, 0, partialTicks,
//                        BuildMode.RED, BuildMode.GREEN, BuildMode.BLUE);
            }
        }
    }
    
    @Override
    protected int drawPreviewBlocks(EntityPlayer entityplayer, Vec3d hitVec, EnumFacing face, Float partialTicks) {
        int hashcode = 0;
        if(EditMode.CURR_PREVIEW_BLOCKS_LOCK.attemptLocking())
        {
            EditMode.CURR_PREVIEW_BLOCKS.clear();
            if(!EditMode.DELETE_MODE)
                EditMode.CURR_PREVIEW_BLOCKS.addAll(CreateAbstrTree.addPosToTempateBlockList(treeBlocks, EditMode.START_BLOCK.getBlockPos()));

            for (TemplateBlock block : EditMode.CURR_PREVIEW_BLOCKS)
            {
                if(block.getBlockState().getMaterial() == Material.AIR)
                {
                    RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                        face, block.getBlockPos());

                    if(EditMode.DELETE_MODE)
                        RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks,1.0F, 0.0F,0.0F);
                    else
                        RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks,0.0F, 0.0F,1.0F);

                }
                else if(!EditMode.DELETE_MODE)
                {
                    RenderPreview.drawPreviewBlock(block.getBlockPos(), new TemplateBlock(block.getFace(), 0, 0, 0, block.getBlockState(),block.getTileEntity())
                        , entityplayer, partialTicks,face );
                }
                int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();

                hashcode += ~~currHashcode;
            }
            EditMode.CURR_PREVIEW_BLOCKS_LOCK.releaseLock();



        }
        if(EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.attemptLocking())
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableColorLogic();
            GlStateManager.disableLighting();

            for (TemplateBlock block : EditMode.CURR_PREVIEW_OUTLINE_BLOCKS)
            {
                RayTraceResult new_position = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec,
                        face, block.getBlockPos());

                if (EditMode.DELETE_MODE)
                    RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks, 1.0F, 0.0F, 0.0F);
                else
                    RenderTemplate.drawSelectionBox(entityplayer, new_position, 0, partialTicks, 0.0F, 0.0F, 1.0F);
                int currHashcode = block.getBlockState().hashCode() + block.getBlockPos().hashCode();

                hashcode += ~~currHashcode;
            }

            EditMode.CURR_PREVIEW_OUTLINE_BLOCKS_LOCK.releaseLock();

            GlStateManager.enableLighting();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableColorLogic();
        }

        return hashcode;
    }

    private void addOtherHalfOfDoublePlant(World worldIn, TemplateBlock block, BlockPos position)
    {
        if(block.getBlockState().getValue( BlockDoublePlant.HALF).equals( BlockDoublePlant.EnumBlockHalf.UPPER))
        {
            if ( Blocks.TALLGRASS.canBlockStay(worldIn, EditMode.START_BLOCK.getBlockPos().add(position), block.getBlockState().withProperty(BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.LOWER)))
                treeBlocks.add(new TemplateBlock(block.getFace(), position.up(), block.getBlockState(),block.getTileEntity()));
        }
        else
        {
            if ( Blocks.TALLGRASS.canBlockStay(worldIn, EditMode.START_BLOCK.getBlockPos().add(position), block.getBlockState()))
                treeBlocks.add(new TemplateBlock(block.getFace(), position, block.getBlockState(),block.getTileEntity()));
        }
    }

    private void initTreeCreators()
    {
        CreateAbstrTree.createTreeCreators();
        for(int i = 0; i < CreateAbstrTree.GEN_TREES.size(); i++)
        {
            CreateAbstrTree.GEN_TREES.get(i).initTreeGen();
        }
    }

    private IBlockState generateRandomBushesInRadius(Minecraft mc, IBlockState handBlockState)
    {
        World worldIn = mc.world;
        grassBlocks = new ArrayList<>();

        int amount = 1;
        if(this.radius > 1.0)
            amount = (int)((2 + AdvCreation.rand.nextInt((int)this.radius)) *this.radius);

        for(int i = 0;  i < (amount);i++)
        {
            int limit = (int)this.radius*2;
            int x= 0;
            int z = 0;
            if(this.radius > 1.0)
            {
                x = AdvCreation.rand.nextInt(limit);
                x= x-((int)this.radius);
                int zlimit = Math.abs( (int) Math.sqrt((Math.pow(this.radius,2) - Math.pow(x,2)  )));
                z = 0;
                if(zlimit > 0)
                    z = AdvCreation.rand.nextInt(zlimit*2) - zlimit;
            }

            BlockPos position = new BlockPos(x, 5,z);

            for(int j = 0; j <10 ; j++)
            {
                if(PlacementHelper.isNotGroundMaterial(worldIn.getBlockState(EditMode.START_BLOCK.getBlockPos().add(position.down()))))
                    position =position.down();
                else
                    break;
            }

            if ( Blocks.TALLGRASS.canBlockStay(worldIn, EditMode.START_BLOCK.getBlockPos().add(position), handBlockState))
            {
                if(handBlockState.getBlock() instanceof BlockDoublePlant)
                {
                    int faceId = AdvCreation.rand.nextInt(3);
                    handBlockState = handBlockState.withProperty(BlockDoublePlant.FACING,  EnumFacing.getHorizontal(faceId));



                    grassBlocks.add(new TemplateBlock(EditMode.START_BLOCK.getFace(), position, handBlockState, AdvCreation.proxy.createTileEntityOf(handBlockState)));
                    grassBlocks.add(new TemplateBlock(EditMode.START_BLOCK.getFace(), position.up(), handBlockState.withProperty(BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.UPPER), AdvCreation.proxy.createTileEntityOf(handBlockState)));
                }
                else
                    grassBlocks.add(new TemplateBlock(EditMode.START_BLOCK.getFace(), position, handBlockState, AdvCreation.proxy.createTileEntityOf(handBlockState)));
            }
        }
        return handBlockState;
    }

    @Override
    public String getGuiOverlayMessage(boolean deleteMode)
    {

        if(deleteMode)
            return TextFormatting.RED + "Remove Plants" + TextFormatting.WHITE + " Mode";
        else
            return "Put Plants Mode";

    }
}
