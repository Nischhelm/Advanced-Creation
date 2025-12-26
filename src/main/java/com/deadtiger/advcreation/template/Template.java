package com.deadtiger.advcreation.template;

import com.deadtiger.advcreation.AdvCreation;
import com.deadtiger.advcreation.build_mode.utility.ExtremaXYZ;
import com.deadtiger.advcreation.build_mode.utility.HelpFunctions;
import com.deadtiger.advcreation.client.gui.gui_utility.CustomGuiUtils;
import com.deadtiger.advcreation.plugin.modded_classes.ModEntity;
import com.deadtiger.advcreation.reference.Reference;
import com.deadtiger.advcreation.utility.*;
import com.google.common.hash.Hashing;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import static net.minecraft.util.EnumFacing.*;

public class Template
{
    public static final Template EMPTY = new Template();

    private String name = "test_name";
    private String McVersion = Reference.MC_VERSION;
    private String propertiesPath;
    private String folderName;
    private String newDirName = null;
    private String prevNewDirName = null;
    private int templateHashcode = 0;

    /**
     * The author of this template.
     */
    private String author = "?";
    private String style = "medieval";  //modern, 17th century, cyberpunk, none, eastern, arabic,
    private String category = "structure"; // room, roof, wall,
    private String function = "house"; //bakery, blacksmith, palace, milling, mining, baracks,

    private ArrayList<TemplateBlock> blockList = new ArrayList<>();
    private ArrayList<TemplateBlock> replacedList = new ArrayList<>();
    private BlockPos size;
    public long solidBlockCount = 0;
    public long insideAirCount = 0;
    public long outsideAirCount = 0;

    //The rotation of the template when building and when displayed in menu
    private EnumFacing rotation;
    private boolean mirroredZY = false;

    //the offset off the template relative to the point the cursor is pointing to, Happens only when placing
    private int X_offset_template;
    private int Y_offset_template;
    private int Z_offset_template;

    //template properties
    public boolean calculatedProperties = false;
    public ExtremaXYZ extrema = null;
    public ArrayList<BlockPos> bottomBlocks = new ArrayList<>(); //list of lowest layer solid and inside blocks
    public ArrayList<BlockPos> insideAir = new ArrayList<>();
    public ArrayList<BlockPos> outsideAir = new ArrayList<>();
    public ArrayList<BlockPos> wallBlock = new ArrayList<>();
    public ArrayList<BlockPos> wallDependentBlock = new ArrayList<>(); //place last they need a wall next to them

    private ArrayList<ArrayList<ArrayList<TemplateBlock>>> XYZOrderedBlockLists = null;

    public int middleX = 0;
    public int middleY = 0;

    // if this template is to big some properties are not being calculated and the preview looks a bit different
    public boolean tooBig = false;

    private ResourceLocation[] icons = new ResourceLocation[8];

    public Template()
    {
        this(new BlockPos(0, 0, 0));
    }

    public Template(BlockPos size)
    {
        this.size = size;

        this.rotation = WEST;

        this.X_offset_template = 0;
        this.Y_offset_template = 0;
        this.Z_offset_template = 0;
    }

    // a constructor that will load the template with the given name from the advcreation_templates folder

    /*
    only works on templates template files generates with version alph1.1 and older
     */
    @Deprecated
    public Template(int i, String filename)
    {
        this();
        this.readTemplate(i, filename, false, null);
        name = filename;
        if (name.contains(".nbt"))
            name = name.split(".nbt")[0];
    }

    public Template(int i,String filename,String propertiesFilePath,String folderName)
    {
        this();
        this.propertiesPath = propertiesFilePath;
        this.folderName = folderName;

        this.readTemplatePropertiesFile(this.propertiesPath);
        this.readTemplate(i, filename, true, this.folderName);
        this.newDirName = folderName;
        this.setMcVersion(extractMcVersionFromFolderName(folderName));
    }

    public String extractMcVersionFromFolderName(String folderName)
    {
        String splitFolderName1[] = folderName.split("_ver");
        String lastPart = splitFolderName1[splitFolderName1.length - 1];
        String versionText = lastPart;
        if(lastPart.contains("-alpha"))
            versionText = lastPart.split("-alpha")[0];
        else if(lastPart.contains("-beta"))
            versionText = lastPart.split("-beta")[0];
        else
            versionText = lastPart.split("_")[0];


        String version = versionText.replace("-",".");
        return version;
    }

    public void rotateY()
    {
        this.rotation = this.rotation.rotateY();

        rotateBlocks();

        int x = getX_offset_template();
        int z = getZ_offset_template();

        setX_offset_template(z);
        setZ_offset_template(-x);
    }

    public void rotateBlocks()
    {
        for (TemplateBlock tempBlock : blockList)
        {
            tempBlock.rotateOrientation90Degree();
        }
    }

    public void mirrorZY()
    {
        mirroredZY = !mirroredZY;
    }

    public boolean save()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.world.isRemote && !StringUtils.isNullOrEmpty(this.name))
        {
            boolean flag1 = writeTemplate();
            boolean flag2 = zipTemplate();
            return flag1 & flag2;
        }
        else
        {
            return false;
        }
    }


    public int generateTemplateHashcode()
    {
        int newHashcode = 0;
        newHashcode += ~~blockList.hashCode();
        newHashcode += ~~author.hashCode();
        newHashcode += ~~name.hashCode();
        newHashcode += ~~size.hashCode();
        newHashcode += ~~category.hashCode();
        newHashcode += ~~style.hashCode();
        newHashcode += ~~function.hashCode();
        return newHashcode;

    }

    public boolean writeTemplate()
    {
        File mcDataDir = Minecraft.getMinecraft().gameDir;
        File saves = new File(mcDataDir, "advcreation_templates");
        templateHashcode = generateTemplateHashcode();

        if (!ensureDirectoryExists(saves)) return false;

        File newDir = new File(saves, this.getDirname());

        if (this.getNewDirName() != null)
            newDir = new File(saves, this.getNewDirName());

        if (newDir.mkdir())
        {
            System.out.println("new directory created " + newDir.getAbsolutePath());
            boolean flag = createNbtFile(newDir);
            boolean flag2 = createPropertiesFile(newDir);

            return flag && flag2;
        }
        else
            return false;
    }

    public boolean moveToTrash()
    {
        boolean trashedSuccesfully = false;
        File mcDataDir = Minecraft.getMinecraft().gameDir;
        File saves = new File(mcDataDir, "advcreation_templates");
        File zipped_templates = new File(mcDataDir, "advcreation_templates_zips");

        String newDirName = this.getDirname();
        if (this.getNewDirName() != null)
            newDirName =  this.getNewDirName();

        File zip_file = new File(zipped_templates, newDirName + ".zip");
        if(zip_file.exists())
        {
            try
            {
                File trashFolder = new File(mcDataDir, "advcreation_templates_zips_trash");
                if (!ensureDirectoryExists(trashFolder))
                    return false;

                File trashFile = new File(trashFolder, newDirName + ".zip");

                //copy zip_file to trashfile
                Files.copy(zip_file.toPath(),trashFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                if(trashFile.exists())
                {
                    TemplateFolderUtility.deleteFolder(new File(saves,newDirName));
                    Files.delete(zip_file.toPath());
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


        }

        return trashedSuccesfully;

    }
    public boolean zipTemplate()
    {
        return zipTemplate(false);
    }

    public boolean zipTemplate(boolean trash)
    {
        boolean zippedSuccessfully = false;
        File mcDataDir = Minecraft.getMinecraft().gameDir;
        File saves = new File(mcDataDir, "advcreation_templates");
        File newDir = new File(saves, this.getDirname());

        if (this.getNewDirName() != null)
            newDir = new File(saves, this.getNewDirName());

        File zipped_templates = new File(mcDataDir, "advcreation_templates_zips");
        if(trash)
            zipped_templates = new File(mcDataDir, "advcreation_templates_zips_trash");
        if (!ensureDirectoryExists(zipped_templates))
            return false;

        if (zipped_templates.exists())
        {
            String dirName = newDir.getName();
            String sourcePathString = saves.getAbsolutePath();
            String destPath = zipped_templates.getAbsolutePath();

            zippedSuccessfully = ZipUtility.zipDirectory(dirName, sourcePathString, destPath);

            File zip_file = new File(zipped_templates, dirName + ".zip");
            try
            {
                //calculate a verifiable hashcode of the current zipfile
                byte[] data = Files.readAllBytes(zip_file.toPath());

                String newhash = Hashing.md5().hashBytes(data).toString();
                System.out.println("hashcode of original " + dirName + ": " + newhash);

                String newName = this.getName() + "_ver" + Reference.VERSION_STRING + "_hash" + newhash;

                //zip the file again under the name with the new hashcode
                zippedSuccessfully = ZipUtility.zipDirectory(dirName, newName, sourcePathString, destPath);

                //delete the old zip file
                zip_file.delete();

                if (zippedSuccessfully)
                {
                    //unzip it in the saves folder
                    try
                    {
                        File newZipFile = new File(destPath, newName + ".zip");
                        if(!trash)
                            UnzipUtility.unzip(newZipFile.getAbsolutePath(), saves.getAbsolutePath() + "//" + newName);
                        this.setNewDirName(newName);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    //delete the original folder
                    File oldFolder = new File(sourcePathString, dirName);
                    System.out.println("attempt to delete old folder: " + oldFolder.getAbsolutePath());

                    TemplateFolderUtility.deleteFolder(oldFolder);
                }
                else
                    System.out.println("unable to create new zip: " + newName + " from " + dirName);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return zippedSuccessfully;
    }

    public boolean createOnlyPropertiesFile()
    {
        File mcDataDir = Minecraft.getMinecraft().gameDir;
        File saves = new File(mcDataDir, "advcreation_templates");

        if (!ensureDirectoryExists(saves)) return false;

        File newDir = new File(saves, getDirname());
        if (newDir.exists())
        {
            return createPropertiesFile(newDir);
        }
        else
            return false;
    }

    public boolean ensureDirectoryExists(File saves)
    {
        if (!saves.exists())
        {
            if (!saves.mkdirs())
            {
                return false;
            }
        }
        else if (!saves.isDirectory())
        {
            return false;
        }
        return true;
    }

    private boolean createPropertiesFile(File newDir)
    {
        File file2 = new File(newDir, "properties.txt");


        OutputStream outputstream = null;
        boolean flag2;

        try
        {
            outputstream = new FileOutputStream(file2);
            try (PrintWriter p = new PrintWriter(outputstream))
            {
                p.println("template_name: " + name);
                p.println("hashcode: " + this.templateHashcode);
                p.println("author: " + author);
                p.println("template_size: " + size.getX() + 'x' + size.getY() + 'x' + size.getZ());
                p.println("actual_size: " + (extrema.minX - extrema.minX + 1) + 'x' + (extrema.maxY - extrema.minY + 1) + 'x' + (extrema.maxZ - extrema.minZ + 1));
                p.println("solid_block_count: " + solidBlockCount);
                p.println("inside_air_count: " + insideAirCount);
                p.println("outside_air_count: " + outsideAirCount);
                p.println("style: " + style);
                p.println("category: " + category);
                p.println("function: " + function);
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            return false;
        }
        catch (Throwable var13)
        {
            flag2 = false;
        }
        finally
        {
            IOUtils.closeQuietly(outputstream);
        }
        return flag2;
    }

    private boolean createNbtFile(File newDir)
    {
        File file2 = new File(newDir, getFilename());

        OutputStream outputstream = null;
        boolean flag;

        try
        {
            NBTTagCompound nbttagcompound = this.writeToNBT(new NBTTagCompound());
            outputstream = new FileOutputStream(file2);
            CompressedStreamTools.writeCompressed(nbttagcompound, outputstream);
            return true;
        }
        catch (Throwable var13)
        {
            flag = false;
        }
        finally
        {
            IOUtils.closeQuietly(outputstream);
        }

        return flag;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        BasicPalette template$basicpalette = new BasicPalette();

        //The list of blocks, their position and blockstate
        NBTTagList nbttaglist = new NBTTagList();
        for (TemplateBlock template$blockinfo : this.blockList)
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("pos", this.writeInts(template$blockinfo.getX_offset(), template$blockinfo.getY_offset(), template$blockinfo.getZ_offset()));
            nbttagcompound.setInteger("state", template$basicpalette.idFor(template$blockinfo.getBlockState()));

            if (template$blockinfo.getTileEntity() != null)
            {
                //code from structure/.../Template.takeBlocksFromWorld
                NBTTagCompound tileEntityTag = template$blockinfo.getTileEntity().writeToNBT(new NBTTagCompound());
                nbttagcompound.setTag("nbt", tileEntityTag);
            }

            nbttaglist.appendTag(nbttagcompound);
        }

        //list of entity infor always empty but I dare not remove it entirely
        NBTTagList nbttaglist1 = new NBTTagList();


        NBTTagList nbttaglist2 = new NBTTagList();

        for (IBlockState iblockstate : template$basicpalette)
        {
            nbttaglist2.appendTag(NBTUtil.writeBlockState(new NBTTagCompound(), iblockstate));
        }

        nbt.setTag("palette", nbttaglist2);
        nbt.setTag("blocks", nbttaglist);
        nbt.setTag("entities", nbttaglist1);
        nbt.setTag("size", this.writeInts(this.size.getX(), this.size.getY(), this.size.getZ()));
        nbt.setString("author", this.author);
        nbt.setInteger("DataVersion", 1139);
        net.minecraftforge.fml.common.FMLCommonHandler.instance().getDataFixer().writeVersionData(nbt);
        return nbt;
    }

    private NBTTagList writeInts(int... values)
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i : values)
        {
            nbttaglist.appendTag(new NBTTagInt(i));
        }

        return nbttaglist;
    }

    private NBTTagList writeDoubles(double... values)
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (double d0 : values)
        {
            nbttaglist.appendTag(new NBTTagDouble(d0));
        }

        return nbttaglist;
    }

    //load templates
    public boolean readTemplate(int i, String filename, boolean hashcode, String folderName)
    {
        File mcDataDir = Minecraft.getMinecraft().gameDir;
        File folder = new File(mcDataDir, "advcreation_templates");
        File saves = new File(folder, filename.split(".nbt")[0]);
        File template = null;
        if (folderName != null)
        {
            //new folders in alpha1.3
            saves = new File(folder, folderName);
            template = new File(saves, filename);
        }
        else
        {
            if (hashcode)
                saves = new File(folder, getDirname());
            template = new File(saves, filename);

            if (i == 0)
                template = new File(folder, filename);
        }

        InputStream inputstream = null;
        boolean flag;

        try
        {
            inputstream = new FileInputStream(template);
            this.readTemplateFromStream( inputstream);
            flag = true;
        }
        catch (Throwable var10)
        {
            flag = false;
            System.out.println(filename + " Failed to load");
        }
        finally
        {
            IOUtils.closeQuietly(inputstream);
        }

        if (flag)
        {
            readTemplatePropertiesFile(saves);
        }

        return flag;
    }

    private boolean readTemplatePropertiesFile(String propertiesFilePath)
    {
        Scanner reader = null;
        boolean res = false;

        File propertiesfile = new File(propertiesFilePath);

        if (propertiesfile.exists())
        {
            try
            {
                reader = new Scanner(propertiesfile);

                //readlines for input stream and parse into properties
                while (reader.hasNextLine())
                {
                    String data = reader.nextLine();
                    if (data.contains("template_name:"))
                    {
                        name = data.split(":")[1].trim();
                        if (name.contains(".nbt"))
                            name = name.split(".nbt")[0];
                    }
                    if (data.contains("style:"))
                        style = data.split(":")[1].trim();
                    if (data.contains("category:"))
                        category = data.split(":")[1].trim();
                    if (data.contains("function:"))
                        function = data.split(":")[1].trim();
                    if (data.contains("author:"))
                        author = data.split(":")[1].trim();
                    if (data.contains("hashcode:"))
                    {
                        String hashcodeString = data.split(":")[1].trim();
                        templateHashcode = Integer.parseInt(hashcodeString);
                    }
                }
                res = true;
            }
            catch (FileNotFoundException e)
            {
                System.out.println("Something went wrong try to load " + propertiesFilePath);
                e.printStackTrace();
                res = false;
            }
            finally
            {
                if (reader != null)
                    reader.close();
            }

        }
        else
        {
            System.out.println("No file found at " + propertiesFilePath);
            res = false;
        }

        return res;
    }

    private boolean readTemplatePropertiesFile(File templateDir)
    {
        Scanner reader = null;
        boolean res = false;

        File propertiesfile = new File(templateDir, "properties.txt");

        return this.readTemplatePropertiesFile(propertiesfile.getAbsolutePath());
    }

    /**
     * reads a template from an inputstream
     */
    private void readTemplateFromStream(InputStream stream) throws IOException
    {
        NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(stream);

        if (!nbttagcompound.hasKey("DataVersion", 99))
        {
            nbttagcompound.setInteger("DataVersion", 500);
        }
        DataFixer fixer = Minecraft.getMinecraft().getDataFixer();
        read(fixer.process(FixTypes.STRUCTURE, nbttagcompound));
    }

    public void read(NBTTagCompound compound)
    {
        this.blockList.clear();

        NBTTagList nbttaglist = compound.getTagList("size", 3);
        this.size = new BlockPos(nbttaglist.getIntAt(0), nbttaglist.getIntAt(1), nbttaglist.getIntAt(2));
        this.author = compound.getString("author");
        BasicPalette template$basicpalette = new BasicPalette();
        NBTTagList nbttaglist1 = compound.getTagList("palette", 10);

        for (int i = 0; i < nbttaglist1.tagCount(); ++i)
        {
            template$basicpalette.addMapping(NBTUtil.readBlockState(nbttaglist1.getCompoundTagAt(i)), i);
        }

        NBTTagList nbttaglist3 = compound.getTagList("blocks", 10);

        for (int j = 0; j < nbttaglist3.tagCount(); ++j)
        {
            NBTTagCompound nbttagcompound = nbttaglist3.getCompoundTagAt(j);
            NBTTagList nbttaglist2 = nbttagcompound.getTagList("pos", 3);
            BlockPos blockpos = new BlockPos(nbttaglist2.getIntAt(0), nbttaglist2.getIntAt(1), nbttaglist2.getIntAt(2));
            IBlockState iblockstate = template$basicpalette.stateFor(nbttagcompound.getInteger("state"));
            TileEntity tileEntity = null;

            if (nbttagcompound.hasKey("nbt"))
            {
                NBTTagCompound nbttagcompound1;
                nbttagcompound1 = nbttagcompound.getCompoundTag("nbt");
                tileEntity = AdvCreation.proxy.createTileEntityOf(iblockstate);
                tileEntity.deserializeNBT(nbttagcompound1);
            }
            //The face of the templateblock is important for some Block types like BlockSkull
            EnumFacing side = NORTH;
            if(iblockstate.getBlock() instanceof BlockSkull)
            {
                side = iblockstate.getValue(BlockSkull.FACING);
            }


            //TODO: TemplateBlock facing maybe needs to be featured in the template
            this.blockList.add(new TemplateBlock(side, blockpos, iblockstate,tileEntity));
        }
    }

    static class BasicPalette implements Iterable<IBlockState>
    {
        public static final IBlockState DEFAULT_BLOCK_STATE = Blocks.AIR.getDefaultState();
        final ObjectIntIdentityMap<IBlockState> ids;
        private int lastId;

        private BasicPalette()
        {
            this.ids = new ObjectIntIdentityMap<IBlockState>(16);
        }

        public int idFor(IBlockState state)
        {
            int i = this.ids.get(state);

            if (i == -1)
            {
                i = this.lastId++;
                this.ids.put(state, i);
            }

            return i;
        }

        @Nullable
        public IBlockState stateFor(int id)
        {
            IBlockState iblockstate = this.ids.getByValue(id);
            return iblockstate == null ? DEFAULT_BLOCK_STATE : iblockstate;
        }

        public Iterator<IBlockState> iterator()
        {
            return this.ids.iterator();
        }

        public void addMapping(IBlockState p_189956_1_, int p_189956_2_)
        {
            this.ids.put(p_189956_1_, p_189956_2_);
        }
    }

    public int getBlockListSize()
    {
        return this.blockList.size();
    }

    public ArrayList<TemplateBlock> getOffsetRotatedBlockList()
    {
        ArrayList<TemplateBlock> newBlockList = new ArrayList<>();
        //first get all the blocks with the proper offset and note if they contain wall/fence/pane blocks
        for (int j = 0; j < this.getBlockListSize(); j++)
        {
            TemplateBlock tempBlock = this.getTempBlockOffset(j);
            newBlockList.add(tempBlock);
        }

        return  newBlockList;
    }

    public void addBlock(EnumFacing face, int x_offset, int y_offset, int z_offset, Block block)
    {
        addBlock(new TemplateBlock(face, x_offset, y_offset, z_offset, block));
    }

    public void addBlock(EnumFacing face, int x_offset, int y_offset, int z_offset, IBlockState blockstate,TileEntity tileEntity)
    {
        addBlock(new TemplateBlock(face, x_offset, y_offset, z_offset, blockstate,tileEntity));
    }

    public void addBlock(TemplateBlock templateBlock)
    {
        blockList.add(templateBlock);
    }

    public TemplateBlock getTempBlock(int index)
    {
        return getTempBlock(blockList.get(index));
    }

    public TemplateBlock getTempBlock(TemplateBlock tempBlock)
    {
        int x_off = tempBlock.getX_offset();
        if (mirroredZY)
        {
            if(extrema == null)
                this.tryCalculateProperties();

            if ((extrema.minX - extrema.minX) % 2 == 0)
                x_off = -(x_off - (size.getX()));
            else
                x_off = -(x_off - (size.getX() - 1));
        }

        TemplateBlock block;
        switch (rotation)
        {
            case EAST:
                block = new TemplateBlock(tempBlock.getFace(), -x_off, tempBlock.getY_offset(),
                        -tempBlock.getZ_offset(), tempBlock.getBlockState(), tempBlock.getType(), tempBlock.isEnclosed(),tempBlock.getTileEntity());

                if(mirroredZY )
                {
                    if(block.getBlockState().getBlock() instanceof BlockSkull &&  block.getBlockState().getValue(BlockSkull.FACING) == UP)
                    {
                        TileEntitySkull tileEntitySkull = (TileEntitySkull) block.getTileEntity();
                        int skullRot = tileEntitySkull.getSkullRotation();

                        if(skullRot != 0)
                            tileEntitySkull.setSkullRotation(16-skullRot);
                    }
                    else if(block.getBlockState().getBlock() instanceof BlockStandingSign)
                    {

                        int signRot = block.getBlockState().getValue(BlockStandingSign.ROTATION);

                        if(signRot != 0)
                            block.setBlockState(block.getBlockState().withProperty(BlockStandingSign.ROTATION, 16-signRot));

                    }
                    else if(block.getBlockOrientation().getAxis() == Axis.X)
                    {
                        block.rotateOrientation90Degree();
                        block.rotateOrientation90Degree();
                    }
                }

                return block;

            case NORTH:
                block = new TemplateBlock(tempBlock.getFace(), tempBlock.getZ_offset(), tempBlock.getY_offset(),
                        -x_off, tempBlock.getBlockState(), tempBlock.getType(), tempBlock.isEnclosed(),tempBlock.getTileEntity());

                if(mirroredZY )
                {
                    if(tempBlock.getBlockState().getBlock() instanceof BlockSkull &&  tempBlock.getBlockState().getValue(BlockSkull.FACING) == UP )
                    {
                        TileEntitySkull tileEntitySkull = (TileEntitySkull) block.getTileEntity();
                        int skullRot = tileEntitySkull.getSkullRotation();
                        skullRot = 12 - (skullRot-12);
                        if(skullRot < 0)
                            skullRot = 16 +skullRot;
                        if(skullRot > 15)
                            skullRot = skullRot-16;
                        tileEntitySkull.setSkullRotation(skullRot);
                    }
                    else if(block.getBlockState().getBlock() instanceof BlockStandingSign)
                    {

                        int signRot = block.getBlockState().getValue(BlockStandingSign.ROTATION);

                        if(signRot != 0)
                        {
                            signRot = 12 - (signRot-12);
                            if(signRot < 0)
                                signRot = 16 +signRot;
                            if(signRot > 15)
                                signRot = signRot-16;
                            block.setBlockState(block.getBlockState().withProperty(BlockStandingSign.ROTATION, signRot));
                        }



                    }
                    else if(tempBlock.getBlockOrientation().getAxis() == Axis.Z)
                    {
                        block.rotateOrientation90Degree();
                        block.rotateOrientation90Degree();
                    }
                }

                return block;

            case WEST:
                block = new TemplateBlock(tempBlock.getFace(), x_off, tempBlock.getY_offset(),
                        tempBlock.getZ_offset(), tempBlock.getBlockState(), tempBlock.getType(), tempBlock.isEnclosed(),tempBlock.getTileEntity());

                if(mirroredZY )
                {
                    if(tempBlock.getBlockState().getBlock() instanceof BlockSkull &&  tempBlock.getBlockState().getValue(BlockSkull.FACING) == UP)
                    {
                        TileEntitySkull tileEntitySkull = (TileEntitySkull) block.getTileEntity();
                        int skullRot = tileEntitySkull.getSkullRotation();
                        if(skullRot != 0)
                            tileEntitySkull.setSkullRotation(16-skullRot);
                    }
                    else if(block.getBlockState().getBlock() instanceof BlockStandingSign)
                    {

                        int signRot = block.getBlockState().getValue(BlockStandingSign.ROTATION);

                        if(signRot != 0)
                            block.setBlockState(block.getBlockState().withProperty(BlockStandingSign.ROTATION, 16-signRot));

                    }
                    else if(tempBlock.getBlockOrientation().getAxis() == Axis.X)
                    {
                        block.rotateOrientation90Degree();
                        block.rotateOrientation90Degree();
                    }
                }

                return block;

            case SOUTH:
                block = new TemplateBlock(tempBlock.getFace(), -tempBlock.getZ_offset(), tempBlock.getY_offset(),
                    x_off, tempBlock.getBlockState(), tempBlock.getType(), tempBlock.isEnclosed(),tempBlock.getTileEntity());


                if(mirroredZY )
                {
                    if(tempBlock.getBlockState().getBlock() instanceof BlockSkull &&  tempBlock.getBlockState().getValue(BlockSkull.FACING) == UP)
                    {
                        TileEntitySkull tileEntitySkull = (TileEntitySkull) block.getTileEntity();
                        int skullRot = tileEntitySkull.getSkullRotation();
                        skullRot = 12 - (skullRot-12);
                        if(skullRot < 0)
                            skullRot = 16 +skullRot;
                        if(skullRot > 15)
                            skullRot = skullRot-16;
                        tileEntitySkull.setSkullRotation(skullRot);
                    }
                    else if(block.getBlockState().getBlock() instanceof BlockStandingSign)
                    {

                        int signRot = block.getBlockState().getValue(BlockStandingSign.ROTATION);

                        if(signRot != 0)
                        {
                            signRot = 12 - (signRot-12);
                            if(signRot < 0)
                                signRot = 16 +signRot;
                            if(signRot > 15)
                                signRot = signRot-16;
                            block.setBlockState(block.getBlockState().withProperty(BlockStandingSign.ROTATION, signRot));
                        }


                    }
                    else if(tempBlock.getBlockOrientation().getAxis() == Axis.Z)
                    {
                        block.rotateOrientation90Degree();
                        block.rotateOrientation90Degree();
                    }
                }

                return block;
        }


        return tempBlock;
    }

    private boolean isSpecialOrientedBlock(TemplateBlock tempBlock)
    {
        Block blockFromTempBlock = tempBlock.getBlockState().getBlock();
        return ((blockFromTempBlock instanceof BlockSkull) && (tempBlock.getBlockState().getValue(BlockSkull.FACING) == UP)) || (blockFromTempBlock instanceof BlockStandingSign);
    }

    public BlockPos getOrientedSize()
    {
        switch (rotation)
        {
            case EAST:
                return new BlockPos(-this.size.getX(), this.size.getY(), -this.size.getZ());
            case NORTH:
                return new BlockPos(this.size.getZ(), this.size.getY(), -this.size.getX());
            case WEST:
                return this.size;
            case SOUTH:
                return new BlockPos(-this.size.getZ(), this.size.getY(), this.size.getX());
        }
        return this.size;
    }

    public BlockPos getCorrectSelectionBoxPos(BlockPos selection)
    {
        switch (rotation)
        {
            case EAST:
                return selection.add(1, 0, 1);
            case NORTH:
                return selection.add(0, 0, 1);
            case WEST:
                return selection;
            case SOUTH:
                return selection.add(1, 0, 0);
        }
        return this.size;
    }

    public BlockPos getBottomBlockPos(int index)
    {
        BlockPos blockPos = bottomBlocks.get(index);
        switch (rotation)
        {
            case EAST:
                return new BlockPos(-blockPos.getX(), blockPos.getY(), -blockPos.getZ());
            case NORTH:
                return new BlockPos(blockPos.getZ(), blockPos.getY(), -blockPos.getX());
            case WEST:
                return new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            case SOUTH:
                return new BlockPos(-blockPos.getZ(), blockPos.getY(), blockPos.getX());
        }
        return blockPos;
    }

    public void tryCalculateProperties()
    {
        if (!calculatedProperties)
        {
            outsideAirCount = 0;
            insideAirCount = 0;
            solidBlockCount = 0;

            //calculate the total blocks amount to see if it is too big
            int blocks = size.getX() * size.getY() * size.getZ();
            tooBig = blocks > (38 * 20 * 62);

            //### CALCULATE SOLID BLOCK BOUNDARIES ###
            //calculate max offsets
            extrema = new ExtremaXYZ(blockList);
            assignTypesToBlocks();

            //check which blocks have blocks adjacent to it in x,y,z directions
            //those don't have to be drawn in previews.
            ArrayList<ArrayList<ArrayList<TemplateBlock>>> newarray = assignEnclosedStatusToBlocks();

            //### CALCULATE INSIDE BLOCKS
            //but only for buildings smaller then a certain size otherwise it takes too long
            if (!tooBig)
            {
                calcOutsideAir(newarray);
                //### CALCULATE BOTTOM LAYER POS ###
                calcBottomLayer(newarray);
            }

            //calculate the middle block of the mass
            calcMiddleOfTemplate();

            insideAirCount = getBlockListSize() - outsideAirCount - solidBlockCount;
            calculatedProperties = true;
        }

    }

    private void calcMiddleOfTemplate()
    {
        middleX = (int) Math.floor((extrema.maxX + 1 - extrema.minX) / 2.0) + extrema.minX;
        middleY = (int) Math.floor(((extrema.maxZ + 1) - extrema.minZ) / 2.0) + extrema.minZ;

        if (getRotation() == SOUTH)
        {
            setX_offset_template(middleY);
            setZ_offset_template(-middleX);
        }
        else if (getRotation() == WEST)
        {
            setX_offset_template(-middleX);
            setZ_offset_template(-middleY);
        }
        else if (getRotation() == NORTH)
        {
            setX_offset_template(-middleY);
            setZ_offset_template(middleX);
        }
        else if (getRotation() == EAST)
        {
            setX_offset_template(middleX);
            setZ_offset_template(middleY);
        }
    }

    private void calcBottomLayer(ArrayList<ArrayList<ArrayList<TemplateBlock>>> newarray)
    {
        for (int x2 = 0; x2 <= (extrema.maxX) - (extrema.minX); x2++)
        {
            for (int z2 = 0; z2 <= (extrema.maxZ) - (extrema.minZ); z2++)
            {
                TemplateBlock bottomBlock = newarray.get(x2).get(0).get(z2);
                if(bottomBlock == null)
                    continue;
                if ((bottomBlock.getType() == TemplateBlock.EnumBlockType.WALL) ||
                        (bottomBlock.getType() == TemplateBlock.EnumBlockType.INSIDE) ||
                        (bottomBlock.getType() == TemplateBlock.EnumBlockType.FURNITURE))
                {
                    bottomBlocks.add(bottomBlock.getBlockPos());
                }
            }
        }
    }

    private void calcOutsideAir(ArrayList<ArrayList<ArrayList<TemplateBlock>>> newarray)
    {
        scanInZDirection(newarray);
        scanInXDirection(newarray);
        scanInYDirection(newarray);
    }

    private void scanInYDirection(ArrayList<ArrayList<ArrayList<TemplateBlock>>> newarray)
    {
        //Scan from top to bottom with a 3x1x3 to see if where the outside air is
        //from y = maxY to y = extrema.minY
        ArrayList<Integer> z = new ArrayList<>();
        ArrayList<Integer> x = new ArrayList<>();
        for (int i = -1; i <= (extrema.maxZ + 1) - (extrema.minZ - 1); i++)
        {
            limitScanSqaure(z, i, extrema.maxZ, extrema.minZ);

            for (int j = -1; j <= (extrema.maxX + 1) - (extrema.minX - 1); j++)
            {
                limitScanSqaure(x, j, extrema.maxX, extrema.minX);

                boolean stop = false;
                for (int k = (extrema.maxY) - (extrema.minY); k >= 0; k--)
                {
                    //check if all blocks in the 3x1x3 block are not a solid material
                    //stop traverling in this ZY direction if there is a solid material
                    stop = check2DBlockYDirr(newarray, z, x, i, j, stop, k);
                    if (stop)
                        break;

                    //this is only executed when all block in the 3x1x3 are not solid
                    for (int zO : z)
                    {
                        for (int xO : x)
                        {
                            TemplateBlock block = newarray.get(j + xO).get(k).get(i + zO);
                            if(block == null)
                                continue;


                            if (block.getType() != TemplateBlock.EnumBlockType.OUTSIDE)
                            {
                                block.setType(TemplateBlock.EnumBlockType.OUTSIDE);
                                outsideAirCount++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void scanInXDirection(ArrayList<ArrayList<ArrayList<TemplateBlock>>> newarray)
    {
        //Scan from every side with a 3x1x3 to see if where the outside air is
        //from x = extrema.minX to x = maxX & from x = maxX to x = maxX
        ArrayList<Integer> z = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        for (int i = -1; i <= (extrema.maxZ + 1) - (extrema.minZ - 1); i++)
        {
            limitScanSqaure(z, i, extrema.maxZ, extrema.minZ);

            for (int j = -1; j <= (extrema.maxY + 1) - (extrema.minY - 1); j++)
            {
                limitScanSqaure(y, j, extrema.maxY, extrema.minY);


                boolean stop = false;
                for (int k = 0; k <= (extrema.maxX) - (extrema.minX); k++)
                {
                    stop = check2DBlockXDirr(newarray, z, y, i, j, stop, k);
                    if (stop)
                        break;

                    //this is only executed when all block in the 3x1x3 are not solid
                    for (int zO : z)
                    {
                        for (int yO : y)
                        {
                            TemplateBlock block = newarray.get(k).get(j + yO).get(i + zO);
                            if(block == null)
                                continue;

                            if (block.getType() != TemplateBlock.EnumBlockType.OUTSIDE)
                            {
                                block.setType(TemplateBlock.EnumBlockType.OUTSIDE);
                                outsideAirCount++;
                            }

                        }
                    }
                }
                stop = false;
                for (int k = (extrema.maxX) - (extrema.minX); k >= 0; k--)
                {
                    //check if all blocks in the 3x1x3 block are not a solid material
                    //stop traverling in this XY direction if there is a solid material
                    stop = check2DBlockXDirr(newarray, z, y, i, j, stop, k);
                    if (stop)
                        break;

                    //this is only executed when all block in the 3x1x3 are not solid
                    for (int zO : z)
                    {
                        for (int yO : y)
                        {
                            TemplateBlock block = newarray.get(k).get(j + yO).get(i + zO);
                            if(block == null)
                                continue;
                            if (block.getType() != TemplateBlock.EnumBlockType.OUTSIDE)
                            {
                                block.setType(TemplateBlock.EnumBlockType.OUTSIDE);
                                outsideAirCount++;
                            }

                        }

                    }
                }
            }
        }
    }

    private void scanInZDirection(ArrayList<ArrayList<ArrayList<TemplateBlock>>> newarray)
    {
        //Scan from every side with a 3x1x3 to see if where the outside air is
        //from z = extrema.minZ to z = extrema.maxZ & from z = extrema.maxZ to z = extrema.maxZ
        ArrayList<Integer> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        for (int i = -1; i <= (extrema.maxX + 1) - (extrema.minX - 1); i++)
        {
            limitScanSqaure(x, i, extrema.maxX, extrema.minX);

            for (int j = -1; j <= (extrema.maxY + 1) - (extrema.minY - 1); j++)
            {
                limitScanSqaure(y, j, extrema.maxY, extrema.minY);


                boolean stop = false;
                for (int k = 0; k <= (extrema.maxZ) - (extrema.minZ); k++)
                {
                    stop = check2DBlockZDirr(newarray, x, y, i, j, stop, k);
                    if (stop)
                        break;

                    //this is only executed when all block in the 3x1x3 are not solid
                    for (int xO : x)
                    {
                        for (int yO : y)
                        {
                            TemplateBlock block = newarray.get(i + xO).get(j + yO).get(k);
                            if(block == null)
                                continue;

                            if (block.getType() != TemplateBlock.EnumBlockType.OUTSIDE)
                            {
                                block.setType(TemplateBlock.EnumBlockType.OUTSIDE);
                                outsideAirCount++;
                            }
                        }

                    }
                }
                stop = false;
                for (int k = (extrema.maxZ) - (extrema.minZ); k >= 0; k--)
                {
                    //check if all blocks in the 3x1x3 block are not a solid material
                    //stop traverling in this XZ direction if there is a solid material
                    stop = check2DBlockZDirr(newarray, x, y, i, j, stop, k);
                    if (stop)
                        break;

                    //this is only executed when all block in the 3x1x3 are not solid
                    for (int xO : x)
                    {
                        for (int yO : y)
                        {
                            TemplateBlock block = newarray.get(i + xO).get(j + yO).get(k);
                            if(block == null)
                                continue;

                            if (block.getType() != TemplateBlock.EnumBlockType.OUTSIDE)
                            {
                                block.setType(TemplateBlock.EnumBlockType.OUTSIDE);
                                outsideAirCount++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void limitScanSqaure(ArrayList<Integer> sqaure, int i, int max, int min)
    {
        sqaure.clear();
        if ((i >= 0) && (i <= max - min))
            sqaure.add(0);
        if ((i - 1 >= 0) && (i - 1 <= max - min))
            sqaure.add(-1);
        if ((i + 1 >= 0) && (i + 1 <= max - min))
            sqaure.add(1);
    }

    private ArrayList<ArrayList<ArrayList<TemplateBlock>>> assignEnclosedStatusToBlocks()
    {
        XYZOrderedBlockLists = getRowDirectionXYZ();

        BlockPos[] pos = {new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0),
                new BlockPos(0, 1, 0), new BlockPos(0, -1, 0),
                new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};

        for (int i = 1; i < (extrema.maxX) - (extrema.minX); i++)
        {
            for (int j = 1; j < (extrema.maxY) - (extrema.minY); j++)
            {

                for (int k = 1; k < (extrema.maxZ) - (extrema.minZ); k++)
                {
                    TemplateBlock tempBlock = XYZOrderedBlockLists.get(i).get(j).get(k);
                    if (tempBlock.getType() == TemplateBlock.EnumBlockType.FURNITURE ||
                            tempBlock.getType() == TemplateBlock.EnumBlockType.WALL)
                    {
                        //if one of the adjacent blocks is not ground then it is open and not enclosed
                        boolean open = false;
                        for (BlockPos newpos : pos)
                        {
                            if(newpos == null)
                                continue;

                            TemplateBlock block = XYZOrderedBlockLists.get(i + newpos.getX()).get(j + newpos.getY()).get(k + newpos.getZ());

                            if(block == null)
                                continue;

                            IBlockState blockState = block.getBlockState();  //TODO: there is a null pointer exception when trying to open a 1.16.5 template.
                            if (PlacementHelper.isNotGroundMaterial(blockState) || PlacementHelper.isNotFullBlock(blockState))
                            {
                                open = true;
                                break;
                            }
                        }
                        if (!open)
                            tempBlock.setEnclosed(true);

                    }
                }
            }
        }
        return XYZOrderedBlockLists;
    }

    private void assignTypesToBlocks()
    {
        for (int j = 0; j < getBlockListSize(); j++)
        {
            TemplateBlock newTempBlock = blockList.get(j);

            if( newTempBlock == null)
                continue;

            if (newTempBlock.getBlockState().getMaterial() != Material.AIR)
            {
                if (isFurnitureBlock(newTempBlock.getBlockState()))
                    newTempBlock.setType(TemplateBlock.EnumBlockType.FURNITURE);
                else if (isNotGroundMaterial(newTempBlock.getBlockState()))
                    newTempBlock.setType(TemplateBlock.EnumBlockType.PLANT);
                else
                    newTempBlock.setType(TemplateBlock.EnumBlockType.WALL);

                solidBlockCount++;
            }
            else if (tooBig)
            {
                //make everything outside air if you are not going to calculate the inside air
                newTempBlock.setType(TemplateBlock.EnumBlockType.OUTSIDE);
            }
            else
            {
                //erase any traces of previous properties calculation
                newTempBlock.setType(TemplateBlock.EnumBlockType.INSIDE);
            }

        }
    }

    protected boolean check2DBlockZDirr(ArrayList<ArrayList<ArrayList<TemplateBlock>>> newarray, ArrayList<Integer> x, ArrayList<Integer> y, int i, int j, boolean stop, int k)
    {
        //check if all blocks in the 3x1x3 block are not a solid material
        //stop traverling in this XZ direction if there is a solid material
        for (int xO : x)
        {
            for (int yO : y)
            {
                int totX = i + xO, totY = j + yO, totZ = k;
//                if (!isNotGroundMaterial(newarray.get(totX).get(totY).get(totZ).getBlockState()))
                TemplateBlock block = newarray.get(totX).get(totY).get(totZ);
                if(block == null)
                    continue;
                if (block.getBlockState().getMaterial() != Material.AIR)
                {
                    stop = true;
                    break;
                }
            }
            if (stop)
                break;
        }
        return stop;
    }

    protected boolean check2DBlockXDirr(ArrayList<ArrayList<ArrayList<TemplateBlock>>> newarray, ArrayList<Integer> z, ArrayList<Integer> y, int i, int j, boolean stop, int k)
    {
        //check if all blocks in the 3x1x3 block are not a solid material
        //stop traverling in this XZ direction if there is a solid material
        for (int zO : z)
        {
            for (int yO : y)
            {
                int totX = k, totY = j + yO, totZ = i + zO;
//                if (!isNotGroundMaterial(newarray.get(totX).get(totY).get(totZ).getBlockState()))
                TemplateBlock block = newarray.get(totX).get(totY).get(totZ);
                if(block == null)
                    continue;
                if (block.getBlockState().getMaterial() != Material.AIR)
                {
                    stop = true;
                    break;
                }
            }
            if (stop)
                break;
        }
        return stop;
    }

    protected boolean check2DBlockYDirr(ArrayList<ArrayList<ArrayList<TemplateBlock>>> newarray, ArrayList<Integer> z, ArrayList<Integer> x, int i, int j, boolean stop, int k)
    {
        //check if all blocks in the 3x1x3 block are not a solid material
        //stop traverling in this XZ direction if there is a solid material
        for (int zO : z)
        {
            for (int xO : x)
            {
                int totX = j + xO, totY = k, totZ = i + zO;
//                if (!isNotGroundMaterial(newarray.get(totX).get(totY).get(totZ).getBlockState()))

                TemplateBlock block = newarray.get(totX).get(totY).get(totZ);
                if(block == null)
                    continue;
                if (block.getBlockState().getMaterial() != Material.AIR)
                {
                    stop = true;
                    break;
                }
            }
            if (stop)
                break;
        }
        return stop;
    }

    public TemplateBlock getTempBlockOffset(TemplateBlock tempBlock)
    {
        return getTempBlock(tempBlock).add_offset(X_offset_template, Y_offset_template, Z_offset_template);
    }

    public TemplateBlock getTempBlockOffset(int index)
    {
        TemplateBlock tempBlock = getTempBlock(index).add_offset(X_offset_template, Y_offset_template, Z_offset_template);
        return tempBlock;
    }

    public BlockPos getBottomBlockPosOffset(int index)
    {
        return getBottomBlockPos(index).add(X_offset_template, Y_offset_template, Z_offset_template);
    }

    /**
     * d
     *
     * @return an 3D arraylist from extrema.minX to extrema.maxX and from extrema.minY to MaxY containing all TemplateBlock with the current XY value
     */
    public ArrayList<ArrayList<ArrayList<TemplateBlock>>> getRowDirectionXYZ()
    {
        ArrayList<ArrayList<ArrayList<TemplateBlock>>> rowsXY = new ArrayList<ArrayList<ArrayList<TemplateBlock>>>();

        //create all the ArrayLists
        for (int i = 0; i <= extrema.maxX - extrema.minX; i++)
        {
            ArrayList<ArrayList<TemplateBlock>> rowY = new ArrayList<ArrayList<TemplateBlock>>();
            for (int j = 0; j <= extrema.maxY - extrema.minY; j++)
            {
                ArrayList<TemplateBlock> rowZ = new ArrayList<TemplateBlock>();
                for (int k = 0; k <= extrema.maxZ - extrema.minZ; k++)
                {
                    rowZ.add(null);
                }
                rowY.add(rowZ);
            }
            rowsXY.add(rowY);
        }

        //fill the ArrayLists with the appropriate templateBlocks
        for (int j = 0; j < getBlockListSize(); j++)
        {
            //TemplateBlock tempBlock = getTempBlockOffset(j);
            TemplateBlock tempBlock = blockList.get(j);
            int x = tempBlock.getX_offset(), y = tempBlock.getY_offset(), z = tempBlock.getZ_offset();
            if ((extrema.maxX < x) ||
                    (extrema.maxY < y) || (extrema.maxZ < z) ||
                    (extrema.minX > x) || (extrema.minY > y) ||
                    (extrema.minZ > z))
            {
                tempBlock.setType(TemplateBlock.EnumBlockType.OUTSIDE);
                continue;
            }


            int newX = x - extrema.minX, newY = y - extrema.minY, newZ = z - extrema.minZ;
            rowsXY.get(newX).get(newY).set(newZ, tempBlock);
        }
        return rowsXY;
    }

    public boolean isEmpty()
    {
        if (this == EMPTY)
        {
            return true;
        }
        return blockList.isEmpty();

    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getMcVersion()
    {
        return McVersion;
    }

    public void setMcVersion(String mcVersion)
    {
        McVersion = mcVersion;
    }

    public void addX_offset_template()
    {
        this.X_offset_template++;
    }

    public void decrX_offset_template()
    {
        this.X_offset_template--;
    }

    public void addY_offset_template()
    {
        this.Y_offset_template++;
    }

    public void decrY_offset_template()
    {
        this.Y_offset_template--;
    }

    public void addZ_offset_template()
    {
        this.Z_offset_template++;
    }

    public void decrZ_offset_template()
    {
        this.Z_offset_template--;
    }

    public int getX_offset_template()
    {
        return X_offset_template;
    }

    public void setX_offset_template(int x_offset_template)
    {
        X_offset_template = x_offset_template;
    }

    public int getY_offset_template()
    {
        return Y_offset_template;
    }

    public void setY_offset_template(int y_offset_template)
    {
        Y_offset_template = y_offset_template;
    }

    public int getZ_offset_template()
    {
        return Z_offset_template;
    }

    public void setZ_offset_template(int z_offset_template)
    {
        Z_offset_template = z_offset_template;
    }

    public void addReplacedBlock(TemplateBlock templateBlock)
    {
        replacedList.add(templateBlock);
    }

    public BlockPos getSize()
    {
        return size;
    }

    public void setSize(BlockPos size)
    {
        this.size = size;
    }

    public void setAuthor(String authorIn)
    {
        this.author = authorIn;
    }

    public static boolean isNotGroundMaterial(IBlockState iBlockState)
    {
        Material mat = iBlockState.getMaterial();
        return (mat == Material.AIR ||
                mat == Material.SNOW ||
                mat == Material.VINE ||
                mat == Material.PLANTS ||
                mat == Material.CAKE ||
                mat == Material.SPONGE ||
                mat == Material.LEAVES ||
                mat == Material.CACTUS);
    }

    public static boolean isFurnitureBlock(IBlockState iBlockState)
    {
        Block block = iBlockState.getBlock();
        return ((block instanceof BlockStairs) ||
                (block instanceof BlockSlab) ||
                (block instanceof BlockTorch) ||
                (block instanceof BlockAnvil) ||
                (block instanceof BlockBed) ||
                (block instanceof BlockWorkbench ||
                (block instanceof BlockDirectional) ||
                (block instanceof BlockBasePressurePlate) ||
                (block instanceof BlockContainer) ||
                (block instanceof BlockFence) ||
                (block instanceof BlockFenceGate) ||
                (block instanceof BlockDoor) ||
                (block instanceof BlockTrapDoor) ||
                (block instanceof BlockTripWire) ||
                (block instanceof BlockTripWireHook)||
                (block instanceof BlockCake) ||
                (block instanceof BlockRedstoneWire) ||
                (block instanceof BlockRedstoneDiode) ||
                (block instanceof BlockCauldron) ||
                (block instanceof BlockCarpet) ||
                (block instanceof BlockLever) ||
                (block instanceof BlockRailBase)));
    }

    public EnumFacing getRotation()
    {
        return rotation;
    }

//    public DynamicTexture getIcon(boolean selected)
//    {
//
//
//
//        if(icons == null)
//        {
//
//            icons = GuiUtils.getResourceLocationsByName(this.getName());
//
//        }
//        if(icons != null)
//        {
//            int i = this.getCustomFaceIndex(this.rotation);
//
//            if(selected)
//                i += 4;
//
//            return icons[i];
//        }
//        return  null;
//    }

    public ResourceLocation getIcon(boolean selected)
    {
        int i = this.getCustomFaceIndex(this.rotation);

        if (selected)
            i += 4;

        if (icons[i] == null)
        {

            icons[i] = CustomGuiUtils.getResourceLocationsByName(this, this.rotation, selected);

        }
        if (icons != null)
        {
            return icons[i];
        }
        return null;
    }


//    public ResourceLocation getIcon(boolean selected)
//    {
//        ResourceLocation icon = GuiUtils.getResourceLocationsByName(this.getName(),this.rotation,selected);
//
//        if(icon != null)
//        {
//            return icon;
//        }
//        return  null;
//    }

    public void deleteIcons()
    {
        icons = new ResourceLocation[8];
    }


    private int getCustomFaceIndex(EnumFacing face)
    {
        if (face == EnumFacing.WEST)
            return 0;
        else if (face == EnumFacing.NORTH)
            return 1;
        else if (face == EnumFacing.EAST)
            return 2;
        else if (face == EnumFacing.SOUTH)
            return 3;
        return 0;
    }

    public RayTraceResult checkCollision(BlockPos position, Vec3d start, Vec3d end)
    {
        for (int j = 0; j < getBlockListSize(); j++)
        {
            TemplateBlock tempBlock = getTempBlockOffset(j);

            if (tempBlock.getBlockState().getBlock().getTranslationKey().equals("tile.air") || tempBlock.isEnclosed())
                continue;

            BlockPos new_pos = position.add(tempBlock.getX_offset(), tempBlock.getY_offset(), tempBlock.getZ_offset());

            AxisAlignedBB selectionBox = new AxisAlignedBB(new_pos, new_pos.add(1, 1, 1));
            RayTraceResult raytraceresult = selectionBox.grow(0.0020000000949949026D).calculateIntercept(ModEntity.currCursorVec.start, ModEntity.currCursorVec.end);
            if (raytraceresult != null)
                return raytraceresult;

        }
        return null;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getFunction()
    {
        return function;
    }

    public void setFunction(String function)
    {
        this.function = function;
    }

    public String getFilename()
    {
        return this.name + ".nbt";
    }

    public String getDirname()
    {
        return this.name + "_hash" + this.templateHashcode;
    }

    public String getZipFilename()
    {
        return this.name + "_hash" + this.templateHashcode + ".zip";
    }

    public String getNewDirName()
    {
        return newDirName;
    }

    public void setNewDirName(String newDirName)
    {
        this.newDirName = newDirName;
    }

    public String getNewZipFileName()
    {
        return newDirName + ".zip";
    }

    public ArrayList<ArrayList<ArrayList<TemplateBlock>>> getXYZOrderedBlockLists()
    {
        return XYZOrderedBlockLists;
    }

    public ArrayList<TemplateBlock> getBlockList()
    {
        return blockList;
    }

    public boolean isMirroredZY()
    {
        return mirroredZY;
    }


}
