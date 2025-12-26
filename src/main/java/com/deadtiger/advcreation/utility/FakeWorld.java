package com.deadtiger.advcreation.utility;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSetMultimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.*;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.*;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FakeWorld extends World
{

    private static ISaveHandler saveHandler = new SaveHandler(null,"nowhere",false,new DataFixer(1));
    private static WorldInfo worldInfo;
    private static WorldProvider provider = new WorldProviderSurface();
    private static Profiler profiler = new Profiler();

    private Biome fakeBiome = new BiomeForest(BiomeForest.Type.NORMAL,new Biome.BiomeProperties("fake"));
    private Chunk fakeChunk;
    private AxisAlignedBB fakeBoundingBox = new AxisAlignedBB(0,1,0,1,0,1);
    private IBlockState currentPreviewBlockState;

    public static FakeWorld INSTANCE = new FakeWorld();



    static
    {
        Constructor<WorldInfo> worldConstructor = ObfuscationReflectionHelper.findConstructor(WorldInfo.class);
        try
        {
            worldInfo = worldConstructor.newInstance();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }


    }

    public FakeWorld()
    {
        super(saveHandler, worldInfo, provider, profiler,true);
        this.fakeChunk = new EmptyChunk(this,0,0);
        this.currentPreviewBlockState= null;
    }

    public FakeWorld(IBlockState currentPreviewBlockState )
    {
        super(saveHandler, worldInfo, provider, profiler,true);
        this.fakeChunk = new EmptyChunk(this,0,0);
        this.currentPreviewBlockState= currentPreviewBlockState;
    }

    @Override
    protected IChunkProvider createChunkProvider()
    {
        return null;
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty)
    {
        return false;
    }

    protected FakeWorld(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client)
    {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }

    @Override
    public World init()
    {
        return  this;
    }

    @Override
    public Biome getBiome(BlockPos pos)
    {
        return fakeBiome;
    }

    @Override
    public Biome getBiomeForCoordsBody(BlockPos pos)
    {
        return fakeBiome;
    }

    @Override
    public BiomeProvider getBiomeProvider()
    {
        return super.getBiomeProvider();
    }

    @Override
    public void initialize(WorldSettings settings)
    {
    }


    @Override
    public void setInitialSpawnLocation()
    {
    }

    @Override
    public IBlockState getGroundAboveSeaLevel(BlockPos pos)
    {
        return Blocks.GRASS.getDefaultState();
    }

    @Override
    public boolean isValid(BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isOutsideBuildHeight(BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean isAirBlock(BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isBlockLoaded(BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty)
    {
        return true;
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int radius)
    {
        return true;
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty)
    {
        return true;
    }

    @Override
    public boolean isAreaLoaded(BlockPos from, BlockPos to)
    {
        return true;
    }

    @Override
    public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty)
    {
        return true;
    }

    @Override
    public boolean isAreaLoaded(StructureBoundingBox box)
    {
        return true;
    }

    @Override
    public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty)
    {
        return true;
    }

    @Override
    public Chunk getChunkFromBlockCoords(BlockPos pos)
    {
        return fakeChunk;
    }

    @Override
    public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ)
    {
        return fakeChunk;
    }

    @Override
    public boolean isChunkGeneratedAt(int x, int z)
    {
        return true;
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags)
    {
        return true;
    }

    @Override
    public void markAndNotifyBlock(BlockPos pos, @Nullable Chunk chunk, IBlockState iblockstate, IBlockState newState, int flags)
    {
    }

    @Override
    public boolean setBlockToAir(BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock)
    {
        return true;
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState state)
    {
        return true;
    }

    @Override
    public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {

    }

    @Override
    public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType, boolean p_175722_3_)
    {

    }

    @Override
    public void markBlocksDirtyVertical(int x, int z, int y1, int y2)
    {
    }

    @Override
    public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax)
    {

    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {

    }

    @Override
    public void updateObservingBlocksAt(BlockPos pos, Block blockType)
    {

    }

    @Override
    public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType, boolean updateObservers)
    {

    }

    @Override
    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide)
    {

    }

    @Override
    public void neighborChanged(BlockPos pos, Block blockIn, BlockPos fromPos)
    {

    }

    @Override
    public void observedNeighborChanged(BlockPos pos, Block p_190529_2_, BlockPos p_190529_3_)
    {

    }

    @Override
    public boolean isBlockTickPending(BlockPos pos, Block blockType)
    {
        return false;
    }

    @Override
    public boolean canSeeSky(BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean canBlockSeeSky(BlockPos pos)
    {
        return true;
    }

    @Override
    public int getLight(BlockPos pos)
    {
        return EnumSkyBlock.SKY.defaultLightValue;
    }

    @Override
    public int getLightFromNeighbors(BlockPos pos)
    {
        return 0;
    }

    @Override
    public int getLight(BlockPos pos, boolean checkNeighbors)
    {
        return EnumSkyBlock.SKY.defaultLightValue;
    }

    @Override
    public BlockPos getHeight(BlockPos pos)
    {
        return BlockPos.ORIGIN;
    }

    @Override
    public int getHeight(int x, int z)
    {
        return 50;
    }

    @Override
    public int getChunksLowestHorizon(int x, int z)
    {
        return 50;
    }

    @Override
    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos)
    {
        return EnumSkyBlock.SKY.defaultLightValue;
    }

    @Override
    public int getLightFor(EnumSkyBlock type, BlockPos pos)
    {
        return EnumSkyBlock.SKY.defaultLightValue;
    }

    @Override
    public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue)
    {

    }

    @Override
    public void notifyLightSet(BlockPos pos)
    {

    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue)
    {
        return EnumSkyBlock.SKY.defaultLightValue;
    }

    @Override
    public float getLightBrightness(BlockPos pos)
    {
        return 1f;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos)
    {
        return this.currentPreviewBlockState;
    }

    @Override
    public boolean isDaytime()
    {
        return true;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end)
    {
        return null;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, boolean stopOnLiquid)
    {
        return null;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
    {
        return null;
    }

    @Override
    public void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
    {

    }

    @Override
    public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
    {

    }

    @Override
    public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay)
    {

    }

    @Override
    public void playRecord(BlockPos blockPositionIn, @Nullable SoundEvent soundEventIn)
    {

    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {

    }

    @Override
    public void spawnAlwaysVisibleParticle(int p_190523_1_, double p_190523_2_, double p_190523_4_, double p_190523_6_, double p_190523_8_, double p_190523_10_, double p_190523_12_, int... p_190523_14_)
    {
            }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
            }

    @Override
    public boolean addWeatherEffect(Entity entityIn)
    {
        return false;
    }

    @Override
    public boolean spawnEntity(Entity entityIn)
    {
        return true;
    }

    @Override
    public void onEntityAdded(Entity entityIn)
    {
        super.onEntityAdded(entityIn);
    }

    @Override
    public void onEntityRemoved(Entity entityIn)
    {

    }

    @Override
    public void removeEntity(Entity entityIn)
    {

    }

    @Override
    public void removeEntityDangerously(Entity entityIn)
    {

    }

    @Override
    public void addEventListener(IWorldEventListener listener)
    {

    }

    @Override
    public List<AxisAlignedBB> getCollisionBoxes(@Nullable Entity entityIn, AxisAlignedBB aabb)
    {
        return new ArrayList<>();
    }

    @Override
    public void removeEventListener(IWorldEventListener listener)
    {

    }

    @Override
    public boolean isInsideWorldBorder(Entity p_191503_1_)
    {
        return true;
    }

    @Override
    public boolean collidesWithAnyBlock(AxisAlignedBB bbox)
    {
        return false;
    }

    @Override
    public int calculateSkylightSubtracted(float partialTicks)
    {
        return 0;
    }

    @Override
    public float getSunBrightnessFactor(float partialTicks)
    {
        return 1f;
    }

    @Override
    public float getSunBrightness(float partialTicks)
    {
        return 1f;
    }

    @Override
    public float getSunBrightnessBody(float partialTicks)
    {
        return 1f;
    }

    @Override
    public Vec3d getSkyColor(Entity entityIn, float partialTicks)
    {
        return new Vec3d(0.5f,0.5f,1.0f);
    }

    @Override
    public Vec3d getSkyColorBody(Entity entityIn, float partialTicks)
    {
        return new Vec3d(0.5f,0.5f,1.0f);
    }

    @Override
    public float getCelestialAngle(float partialTicks)
    {
        return 1f;
    }

    @Override
    public int getMoonPhase()
    {
        return 0;
    }

    @Override
    public float getCurrentMoonPhaseFactor()
    {
        return 1f;
    }

    @Override
    public float getCurrentMoonPhaseFactorBody()
    {
        return 1f;
    }

    @Override
    public float getCelestialAngleRadians(float partialTicks)
    {
        return 1f;
    }

    @Override
    public Vec3d getCloudColour(float partialTicks)
    {
        return new Vec3d(1.0f,1.0f,1.0f);
    }

    @Override
    public Vec3d getCloudColorBody(float partialTicks)
    {
        return new Vec3d(1.0f,1.0f,1.0f);
    }

    @Override
    public Vec3d getFogColor(float partialTicks)
    {
        return new Vec3d(1.0f,1.0f,1.0f);
    }

    @Override
    public BlockPos getPrecipitationHeight(BlockPos pos)
    {
        return BlockPos.ORIGIN;
    }

    @Override
    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos)
    {
        return BlockPos.ORIGIN;
    }

    @Override
    public float getStarBrightness(float partialTicks)
    {
        return 0f;
    }

    @Override
    public float getStarBrightnessBody(float partialTicks)
    {
        return 0f;
    }

    @Override
    public boolean isUpdateScheduled(BlockPos pos, Block blk)
    {
        return true;
    }

    @Override
    public void scheduleUpdate(BlockPos pos, Block blockIn, int delay)
    {
    }

    @Override
    public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority)
    {

    }

    @Override
    public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority)
    {

    }

    @Override
    public void updateEntities()
    {

    }

    @Override
    protected void tickPlayers()
    {

    }

    @Override
    public boolean addTileEntity(TileEntity tile)
    {
        return true;
    }

    @Override
    public void addTileEntities(Collection<TileEntity> tileEntityCollection)
    {

    }

    @Override
    public void updateEntity(Entity ent)
    {

    }

    @Override
    public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate)
    {

    }

    @Override
    public boolean checkNoEntityCollision(AxisAlignedBB bb)
    {
        return true;
    }

    @Override
    public boolean checkNoEntityCollision(AxisAlignedBB bb, @Nullable Entity entityIn)
    {
        return true;
    }

    @Override
    public boolean checkBlockCollision(AxisAlignedBB bb)
    {
        return false;
    }

    @Override
    public boolean containsAnyLiquid(AxisAlignedBB bb)
    {
        return false;
    }

    @Override
    public boolean isFlammableWithin(AxisAlignedBB bb)
    {
        return false;
    }

    @Override
    public boolean handleMaterialAcceleration(AxisAlignedBB bb, Material materialIn, Entity entityIn)
    {
        return true;
    }

    @Override
    public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn)
    {
        return true;
    }

    @Override
    public Explosion createExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean isSmoking)
    {
        return null;
    }

    @Override
    public Explosion newExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
    {
        return null;
    }

    @Override
    public float getBlockDensity(Vec3d vec, AxisAlignedBB bb)
    {
        return 1f;
    }

    @Override
    public boolean extinguishFire(@Nullable EntityPlayer player, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    public String getDebugLoadedEntities()
    {
        return "";
    }

    @Override
    public String getProviderName()
    {
        return super.getProviderName();
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos)
    {
        return null;
    }

    @Override
    public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn)
    {

    }

    @Override
    public void removeTileEntity(BlockPos pos)
    {

    }

    @Override
    public void markTileEntityForRemoval(TileEntity tileEntityIn)
    {

    }

    @Override
    public boolean isBlockFullCube(BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isBlockNormalCube(BlockPos pos, boolean _default)
    {
        return true;
    }

    @Override
    public void calculateInitialSkylight()
    {

    }

    @Override
    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful)
    {

    }

    @Override
    public void tick()
    {

    }

    @Override
    protected void calculateInitialWeather()
    {

    }

    @Override
    public void calculateInitialWeatherBody()
    {

    }

    @Override
    protected void updateWeather()
    {

    }

    @Override
    public void updateWeatherBody()
    {

    }

    @Override
    protected void playMoodSoundAndCheckLight(int p_147467_1_, int p_147467_2_, Chunk chunkIn)
    {

    }

    @Override
    protected void updateBlocks()
    {

    }

    @Override
    public void immediateBlockTick(BlockPos pos, IBlockState state, Random random)
    {

    }

    @Override
    public boolean canBlockFreezeWater(BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean canBlockFreezeNoWater(BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj)
    {
        return true;
    }

    @Override
    public boolean canBlockFreezeBody(BlockPos pos, boolean noWaterAdj)
    {
        return true;
    }

    @Override
    public boolean canSnowAt(BlockPos pos, boolean checkLight)
    {
        return true;
    }

    @Override
    public boolean canSnowAtBody(BlockPos pos, boolean checkLight)
    {
        return true;
    }

    @Override
    public boolean checkLight(BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean tickUpdates(boolean runAllPending)
    {
        return true;
    }

    @Nullable
    @Override
    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean remove)
    {
        return null;
    }

    @Nullable
    @Override
    public List<NextTickListEntry> getPendingBlockUpdates(StructureBoundingBox structureBB, boolean remove)
    {
        return null;
    }

    @Override
    public List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb)
    {
        return new ArrayList<>();
    }

    @Override
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate)
    {
        return new ArrayList<>();
    }

    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, Predicate<? super T> filter)
    {
        return new ArrayList<>();
    }

    @Override
    public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, Predicate<? super T> filter)
    {
        return new ArrayList<>();
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> classEntity, AxisAlignedBB bb)
    {
        return new ArrayList<>();
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter)
    {
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo)
    {
        return null;
    }

    @Nullable
    @Override
    public Entity getEntityByID(int id)
    {
        return null;
    }

    @Override
    public List<Entity> getLoadedEntityList()
    {
        return new ArrayList<>();
    }

    @Override
    public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity)
    {

    }

    @Override
    public int countEntities(Class<?> entityType)
    {
        return 0;
    }

    @Override
    public void loadEntities(Collection<Entity> entityCollection)
    {

    }

    @Override
    public void unloadEntities(Collection<Entity> entityCollection)
    {

    }

    @Override
    public boolean mayPlace(Block blockIn, BlockPos pos, boolean skipCollisionCheck, EnumFacing sidePlacedOn, @Nullable Entity placer)
    {
        return true;
    }

    @Override
    public int getSeaLevel()
    {
        return 50;
    }

    @Override
    public void setSeaLevel(int seaLevelIn)
    {

    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction)
    {
        return 0;
    }

    @Override
    public WorldType getWorldType()
    {
        return super.getWorldType();
    }

    @Override
    public int getStrongPower(BlockPos pos)
    {
        return 0;
    }

    @Override
    public boolean isSidePowered(BlockPos pos, EnumFacing side)
    {
        return false;
    }

    @Override
    public int getRedstonePower(BlockPos pos, EnumFacing facing)
    {
        return 0;
    }

    @Override
    public boolean isBlockPowered(BlockPos pos)
    {
        return false;
    }

    @Override
    public int isBlockIndirectlyGettingPowered(BlockPos pos)
    {
        return 0;
    }

    @Nullable
    @Override
    public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance)
    {
        return null;
    }

    @Nullable
    @Override
    public EntityPlayer getNearestPlayerNotCreative(Entity entityIn, double distance)
    {
        return null;
    }

    @Nullable
    @Override
    public EntityPlayer getClosestPlayer(double posX, double posY, double posZ, double distance, boolean spectator)
    {
        return null;
    }

    @Nullable
    @Override
    public EntityPlayer getClosestPlayer(double x, double y, double z, double p_190525_7_, Predicate<Entity> p_190525_9_)
    {
        return null;
    }

    @Override
    public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range)
    {
        return false;
    }

    @Nullable
    @Override
    public EntityPlayer getNearestAttackablePlayer(Entity entityIn, double maxXZDistance, double maxYDistance)
    {
        return null;
    }

    @Nullable
    @Override
    public EntityPlayer getNearestAttackablePlayer(BlockPos pos, double maxXZDistance, double maxYDistance)
    {
        return null;
    }

    @Nullable
    @Override
    public EntityPlayer getNearestAttackablePlayer(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Function<EntityPlayer, Double> playerToDouble, @Nullable Predicate<EntityPlayer> p_184150_12_)
    {
        return null;
    }

    @Nullable
    @Override
    public EntityPlayer getPlayerEntityByName(String name)
    {
        return null;
    }

    @Nullable
    @Override
    public EntityPlayer getPlayerEntityByUUID(UUID uuid)
    {
        return null;
    }

    @Override
    public void sendQuittingDisconnectingPacket()
    {

    }

    @Override
    public void checkSessionLock() throws MinecraftException
    {

    }

    @Override
    public void setTotalWorldTime(long worldTime)
    {

    }

    @Override
    public long getSeed()
    {
        return 0;
    }

    @Override
    public long getTotalWorldTime()
    {
        return 0;
    }

    @Override
    public long getWorldTime()
    {
        return 0;
    }

    @Override
    public void setWorldTime(long time)
    {

    }

    @Override
    public BlockPos getSpawnPoint()
    {
        return BlockPos.ORIGIN;
    }

    @Override
    public void setSpawnPoint(BlockPos pos)
    {
    }

    @Override
    public void joinEntityInSurroundings(Entity entityIn)
    {

    }

    @Override
    public boolean isBlockModifiable(EntityPlayer player, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean canMineBlockBody(EntityPlayer player, BlockPos pos)
    {
        return true;
    }

    @Override
    public void setEntityState(Entity entityIn, byte state)
    {
    }

    @Override
    public IChunkProvider getChunkProvider()
    {
        return super.getChunkProvider();
    }

    @Override
    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam)
    {

    }

    @Override
    public ISaveHandler getSaveHandler()
    {
        return super.getSaveHandler();
    }

    @Override
    public WorldInfo getWorldInfo()
    {
        return super.getWorldInfo();
    }

    @Override
    public GameRules getGameRules()
    {
        return super.getGameRules();
    }

    @Override
    public void updateAllPlayersSleepingFlag()
    {
    }

    @Override
    public float getThunderStrength(float delta)
    {
        return 0f;
    }

    @Override
    public void setThunderStrength(float strength)
    {

    }

    @Override
    public float getRainStrength(float delta)
    {
        return 0f;
    }

    @Override
    public void setRainStrength(float strength)
    {

    }

    @Override
    public boolean isThundering()
    {
        return false;
    }

    @Override
    public boolean isRaining()
    {
        return false;
    }

    @Override
    public boolean isRainingAt(BlockPos position)
    {
        return false;
}

    @Override
    public boolean isBlockinHighHumidity(BlockPos pos)
    {
        return false;
    }

    @Nullable
    @Override
    public MapStorage getMapStorage()
    {
        return null;
    }

    @Override
    public void setData(String dataID, WorldSavedData worldSavedDataIn)
    {
    }

    @Nullable
    @Override
    public WorldSavedData loadData(Class<? extends WorldSavedData> clazz, String dataID)
    {
        return null;
    }

    @Override
    public int getUniqueDataId(String key)
    {
        return 0;
    }

    @Override
    public void playBroadcastSound(int id, BlockPos pos, int data)
    {

    }

    @Override
    public void playEvent(int type, BlockPos pos, int data)
    {

    }

    @Override
    public void playEvent(@Nullable EntityPlayer player, int type, BlockPos pos, int data)
    {

    }

    @Override
    public int getHeight()
    {
        return 50;
    }

    @Override
    public int getActualHeight()
    {
        return 50;
    }

    @Override
    public Random setRandomSeed(int p_72843_1_, int p_72843_2_, int p_72843_3_)
    {
        return new Random();
    }

    @Override
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report)
    {
        return new CrashReportCategory(new CrashReport("fdfds",new Throwable()),"fake");
    }

    @Override
    public double getHorizon()
    {
        return 5;
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
    }

    @Override
    public Calendar getCurrentDate()
    {
        return super.getCurrentDate();
    }

    @Override
    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable NBTTagCompound compound)
    {
    }

    @Override
    public Scoreboard getScoreboard()
    {
        return new Scoreboard();
    }

    @Override
    public void updateComparatorOutputLevel(BlockPos pos, Block blockIn)
    {

    }

    @Override
    public DifficultyInstance getDifficultyForLocation(BlockPos pos)
    {
        return super.getDifficultyForLocation(pos);
    }

    @Override
    public EnumDifficulty getDifficulty()
    {
        return super.getDifficulty();
    }

    @Override
    public int getSkylightSubtracted()
    {
        return 15;
    }

    @Override
    public void setSkylightSubtracted(int newSkylightSubtracted)
    {

    }

    @Override
    public int getLastLightningBolt()
    {
        return 0;
    }

    @Override
    public void setLastLightningBolt(int lastLightningBoltIn)
    {

    }

    @Override
    public VillageCollection getVillageCollection()
    {
        return new VillageCollection("");
    }

    @Override
    public WorldBorder getWorldBorder()
    {
        return super.getWorldBorder();
    }

    @Override
    public boolean isSpawnChunk(int x, int z)
    {
        return true;
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default)
    {
        return true;
    }

    @Override
    public ImmutableSetMultimap<ChunkPos, ForgeChunkManager.Ticket> getPersistentChunks()
    {
        return ImmutableSetMultimap.of();
    }

    @Override
    public Iterator<Chunk> getPersistentChunkIterable(Iterator<Chunk> chunkIterator)
    {
        return new Iterator<Chunk>()
        {
            @Override
            public boolean hasNext()
            {
                return false;
            }

            @Override
            public Chunk next()
            {
                return null;
            }
        };
    }

    @Override
    public int getBlockLightOpacity(BlockPos pos)
    {
        return 15;
    }

    @Override
    public int countEntities(EnumCreatureType type, boolean forSpawnCount)
    {
        return 0;
    }

    @Override
    public void markTileEntitiesInChunkForRemoval(Chunk chunk)
    {
    }

    @Override
    protected void initCapabilities()
    {

    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return null;
    }

    @Override
    public MapStorage getPerWorldStorage()
    {
        return super.getPerWorldStorage();
    }

    @Override
    public void sendPacketToServer(Packet<?> packetIn)
    {

    }

    @Override
    public LootTableManager getLootTableManager()
    {
        return super.getLootTableManager();
    }

    @Nullable
    @Override
    public BlockPos findNearestStructure(String p_190528_1_, BlockPos p_190528_2_, boolean p_190528_3_)
    {
        return BlockPos.ORIGIN;
    }

    public World setPreviewBlockState(IBlockState blockState)
    {
        this.currentPreviewBlockState = blockState;
        return this;
    }
}
