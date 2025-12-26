package com.deadtiger.advcreation.ridingentity;

import com.deadtiger.advcreation.utility.FakeWorld;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandResultStats;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FakeEntityBoat extends EntityBoat
{
    public static FakeEntityBoat INSTANCE  = new FakeEntityBoat(FakeWorld.INSTANCE);

    public AxisAlignedBB fakeBB = new AxisAlignedBB(0,0,0,1,1,1);

    public FakeEntityBoat(World worldIn)
    {
        super(worldIn);
    }

    public FakeEntityBoat(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return true;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return entityIn.getCollisionBox(entityIn);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox()
    {
        if(this.getRidingEntity() != null)
            return this.getRidingEntity().getCollisionBoundingBox();
        return this.fakeBB;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public double getMountedYOffset()
    {
        return 0;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }

    @Override
    public void applyEntityCollision(Entity entityIn)
    {
    }

    @Override
    public Item getItemBoat()
    {
        return null;
    }

    @Override
    public void performHurtAnimation()
    {

    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {

    }

    @Override
    public EnumFacing getAdjustedHorizontalFacing()
    {
        return null;
    }

    @Override
    public void onUpdate()
    {

    }

    @Nullable
    @Override
    protected SoundEvent getPaddleSound()
    {
        return null;
    }

    @Override
    public void setPaddleState(boolean left, boolean right)
    {

    }

    @Override
    public float getRowingTime(int side, float limbSwing)
    {
        return 0f;
    }

    @Override
    public float getWaterLevelAbove()
    {
        return 0f;
    }

    @Override
    public float getBoatGlide()
    {
        return 0f;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {

    }

    @Override
    protected void applyYawToEntity(Entity entityToUpdate)
    {

    }

    @Override
    public void applyOrientationToEntity(Entity entityToUpdate)
    {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {

    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        return true;
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {

    }

    @Override
    public boolean getPaddleState(int side)
    {
        return false;
    }

    @Override
    public void setDamageTaken(float damageTaken)
    {

    }

    @Override
    public float getDamageTaken()
    {
        return 0f;
    }

    @Override
    public void setTimeSinceHit(int timeSinceHit)
    {

    }

    @Override
    public int getTimeSinceHit()
    {
        return 0;
    }

    @Override
    public void setForwardDirection(int forwardDirection)
    {

    }

    @Override
    public int getForwardDirection()
    {
        return 0;
    }

    @Override
    public void setBoatType(Type boatType)
    {

    }

    @Override
    public Type getBoatType()
    {
        return null;
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return true;
    }

    @Nullable
    @Override
    public Entity getControllingPassenger()
    {
        if(this.getRidingEntity() != null)
            return this.getRidingEntity();
        return null;
    }

    @Override
    public void updateInputs(boolean p_184442_1_, boolean p_184442_2_, boolean p_184442_3_, boolean p_184442_4_)
    {

    }

    @Override
    protected void addPassenger(Entity passenger)
    {
        this.rotationYaw = passenger.rotationYaw;

    }

    @Override
    public int getEntityId()
    {
        return super.getEntityId();
    }

    @Override
    public void setEntityId(int id)
    {
        super.setEntityId(id);
    }

    @Override
    public Set<String> getTags()
    {
        return super.getTags();
    }

    @Override
    public boolean addTag(String tag)
    {
        return super.addTag(tag);
    }

    @Override
    public boolean removeTag(String tag)
    {
        return super.removeTag(tag);
    }

    @Override
    public void onKillCommand()
    {

    }

    @Override
    public EntityDataManager getDataManager()
    {
        return null;
    }

    @Override
    public boolean equals(Object p_equals_1_)
    {
        return super.equals(p_equals_1_);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    protected void preparePlayerToSpawn()
    {

    }

    @Override
    public void setDead()
    {

    }

    @Override
    public void setDropItemsWhenDead(boolean dropWhenDead)
    {

    }

    @Override
    protected void setSize(float width, float height)
    {

    }

    @Override
    protected void setRotation(float yaw, float pitch)
    {

    }

    @Override
    public void setPosition(double x, double y, double z)
    {

    }

    @Override
    public void turn(float yaw, float pitch)
    {

    }

    @Override
    public void onEntityUpdate()
    {

    }

    @Override
    protected void decrementTimeUntilPortal()
    {

    }

    @Override
    public int getMaxInPortalTime()
    {
        return 0;
    }

    @Override
    protected void setOnFireFromLava()
    {

    }

    @Override
    public void setFire(int seconds)
    {

    }

    @Override
    public void extinguish()
    {

    }

    @Override
    protected void outOfWorld()
    {

    }

    @Override
    public boolean isOffsetPositionInLiquid(double x, double y, double z)
    {
        return false;
    }

    @Override
    public void move(MoverType type, double x, double y, double z)
    {

    }

    @Override
    public void resetPositionToBB()
    {

    }

    @Override
    protected SoundEvent getSwimSound()
    {
        return null;
    }

    @Override
    protected SoundEvent getSplashSound()
    {
        return null;
    }

    @Override
    protected void doBlockCollisions()
    {

    }

    @Override
    protected void onInsideBlock(IBlockState p_191955_1_)
    {

    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {

    }

    @Override
    protected float playFlySound(float p_191954_1_)
    {
        return 0;
    }

    @Override
    protected boolean makeFlySound()
    {
        return false;
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch)
    {

    }

    @Override
    public boolean isSilent()
    {
        return true;
    }

    @Override
    public void setSilent(boolean isSilent)
    {

    }

    @Override
    public boolean hasNoGravity()
    {
        return true;
    }

    @Override
    public void setNoGravity(boolean noGravity)
    {

    }

    @Override
    protected void dealFireDamage(int amount)
    {

    }

    @Override
    public void fall(float distance, float damageMultiplier)
    {

    }

    @Override
    public boolean isWet()
    {
        return false;
    }

    @Override
    public boolean isInWater()
    {
        return false;
    }

    @Override
    public boolean isOverWater()
    {
        return false;
    }

    @Override
    public boolean handleWaterMovement()
    {
        return false;
    }

    @Override
    protected void doWaterSplashEffect()
    {

    }

    @Override
    public void spawnRunningParticles()
    {

    }

    @Override
    protected void createRunningParticles()
    {

    }

    @Override
    public boolean isInsideOfMaterial(Material materialIn)
    {
        if(materialIn == Material.WATER)
            return true;
        else
            return false;
    }

    @Override
    public boolean isInLava()
    {
        return false;
    }

    @Override
    public void moveRelative(float strafe, float up, float forward, float friction)
    {

    }

    @Override
    public int getBrightnessForRender()
    {
        return 1;
    }

    @Override
    public float getBrightness()
    {
        return 1f;
    }

    @Override
    public void setWorld(World worldIn)
    {
        super.setWorld(worldIn);
    }

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {

    }

    @Override
    public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn)
    {

    }

    @Override
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)
    {

    }

    @Override
    public float getDistance(Entity entityIn)
    {
        return 0;
    }

    @Override
    public double getDistanceSq(double x, double y, double z)
    {
        return 0;
    }

    @Override
    public double getDistanceSq(BlockPos pos)
    {
        return 0 ;
    }

    @Override
    public double getDistanceSqToCenter(BlockPos pos)
    {
        return 0;
    }

    @Override
    public double getDistance(double x, double y, double z)
    {
        return 0;
    }

    @Override
    public double getDistanceSq(Entity entityIn)
    {
        return 0;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn)
    {

    }

    @Override
    public void addVelocity(double x, double y, double z)
    {

    }

    @Override
    protected void markVelocityChanged()
    {

    }

    @Override
    public Vec3d getLook(float partialTicks)
    {
        return Vec3d.ZERO;
    }

    @Override
    public Vec3d getPositionEyes(float partialTicks)
    {
        return Vec3d.ZERO;
    }

    @Nullable
    @Override
    public RayTraceResult rayTrace(double blockReachDistance, float partialTicks)
    {
        return null;
    }

    @Override
    public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_)
    {

    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z)
    {
        return false;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        return false;
    }

    @Override
    public boolean writeToNBTAtomically(NBTTagCompound compound)
    {
        return false;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound compound)
    {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {

    }

    @Override
    protected boolean shouldSetPosAfterLoading()
    {
        return false;
    }

    @Override
    protected NBTTagList newDoubleNBTList(double... numbers)
    {
        return super.newDoubleNBTList(numbers);
    }

    @Override
    protected NBTTagList newFloatNBTList(float... numbers)
    {
     return null;
    }

    @Nullable
    @Override
    public EntityItem dropItem(Item itemIn, int size)
    {
        return null;
    }

    @Nullable
    @Override
    public EntityItem dropItemWithOffset(Item itemIn, int size, float offsetY)
    {
        return null;
    }

    @Nullable
    @Override
    public EntityItem entityDropItem(ItemStack stack, float offsetY)
    {
        return null;
    }

    @Override
    public boolean isEntityAlive()
    {
        return false;
    }

    @Override
    public boolean isEntityInsideOpaqueBlock()
    {
        return false;
    }

    @Override
    public void updateRidden()
    {

    }

    @Override
    public double getYOffset()
    {
        return 0;
    }

    @Override
    public boolean startRiding(Entity entityIn)
    {
        return super.startRiding(entityIn);
    }

    @Override
    public boolean startRiding(Entity entityIn, boolean force)
    {
        return super.startRiding(entityIn, force);
    }

    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return super.canBeRidden(entityIn);
    }

    @Override
    public void removePassengers()
    {
        super.removePassengers();
    }

    @Override
    public void dismountRidingEntity()
    {
        super.dismountRidingEntity();
    }

    @Override
    protected void removePassenger(Entity passenger)
    {
        super.removePassenger(passenger);
    }

    @Override
    public float getCollisionBorderSize()
    {
        return 0f;
    }

    @Override
    public Vec3d getLookVec()
    {
        return Vec3d.ZERO;
    }

    @Override
    public Vec2f getPitchYaw()
    {
        return Vec2f.MIN;
    }

    @Override
    public Vec3d getForward()
    {
        return Vec3d.ZERO;
    }

    @Override
    public void setPortal(BlockPos pos)
    {

    }

    @Override
    public int getPortalCooldown()
    {
        return 0;
    }

    @Override
    public void setVelocity(double x, double y, double z)
    {

    }

    @Override
    public void handleStatusUpdate(byte id)
    {

    }

    @Override
    public Iterable<ItemStack> getHeldEquipment()
    {
        return null;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList()
    {
        return null;
    }

    @Override
    public Iterable<ItemStack> getEquipmentAndArmor()
    {
        return null;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
    {

    }

    @Override
    public boolean isBurning()
    {
        return false;
    }

    @Override
    public boolean isRiding()
    {
        return super.isRiding();
    }

    @Override
    public boolean isBeingRidden()
    {
        return super.isBeingRidden();
    }

    @Override
    public boolean isSneaking()
    {
        return false;
    }

    @Override
    public void setSneaking(boolean sneaking)
    {

    }

    @Override
    public boolean isSprinting()
    {
        return false;
    }

    @Override
    public void setSprinting(boolean sprinting)
    {

    }

    @Override
    public boolean isGlowing()
    {
        return false;
    }

    @Override
    public void setGlowing(boolean glowingIn)
    {

    }

    @Override
    public boolean isInvisible()
    {
        return true;
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player)
    {
        return true;
    }

    @Nullable
    @Override
    public Team getTeam()
    {
        return null;
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn)
    {
        return true;
    }

    @Override
    public boolean isOnScoreboardTeam(Team teamIn)
    {
        return false;
    }

    @Override
    public void setInvisible(boolean invisible)
    {

    }

    @Override
    protected boolean getFlag(int flag)
    {
        return true;
    }

    @Override
    protected void setFlag(int flag, boolean set)
    {

    }

    @Override
    public int getAir()
    {
        return 0;
    }

    @Override
    public void setAir(int air)
    {

    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt)
    {

    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn)
    {

    }

    @Override
    protected boolean pushOutOfBlocks(double x, double y, double z)
    {
        return false;
    }

    @Override
    public void setInWeb()
    {

    }

    @Override
    public String getName()
    {
        return super.getName();
    }

    @Nullable
    @Override
    public Entity[] getParts()
    {
        return super.getParts();
    }

    @Override
    public boolean isEntityEqual(Entity entityIn)
    {
        return super.isEntityEqual(entityIn);
    }

    @Override
    public float getRotationYawHead()
    {
        return 0;
    }

    @Override
    public void setRotationYawHead(float rotation)
    {

    }

    @Override
    public void setRenderYawOffset(float offset)
    {

    }

    @Override
    public boolean canBeAttackedWithItem()
    {
        return false;
    }

    @Override
    public boolean hitByEntity(Entity entityIn)
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "FakeBoat";
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source)
    {
        return true;
    }

    @Override
    public boolean getIsInvulnerable()
    {
        return true;
    }

    @Override
    public void setEntityInvulnerable(boolean isInvulnerable)
    {

    }

    @Override
    public void copyLocationAndAnglesFrom(Entity entityIn)
    {

    }

    @Nullable
    @Override
    public Entity changeDimension(int dimensionIn)
    {
        return null;
    }

    @Nullable
    @Override
    public Entity changeDimension(int dimensionIn, ITeleporter teleporter)
    {
        return null;
    }

    @Override
    public boolean isNonBoss()
    {
        return true;
    }

    @Override
    public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn)
    {
        return 1000f;
    }

    @Override
    public boolean canExplosionDestroyBlock(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn, float p_174816_5_)
    {
        return false;
    }

    @Override
    public int getMaxFallHeight()
    {
        return 1000;
    }

    @Override
    public Vec3d getLastPortalVec()
    {
        return Vec3d.ZERO;
    }

    @Override
    public EnumFacing getTeleportDirection()
    {
        return null;
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate()
    {
        return true;
    }

    @Override
    public void addEntityCrashInfo(CrashReportCategory category)
    {

    }

    @Override
    public void setUniqueId(UUID uniqueIdIn)
    {

    }

    @Override
    public boolean canRenderOnFire()
    {
        return false;
    }

    @Override
    public UUID getUniqueID()
    {
        return super.getUniqueID();
    }

    @Override
    public String getCachedUniqueIdString()
    {
        return super.getCachedUniqueIdString();
    }

    @Override
    public boolean isPushedByWater()
    {
        return false;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return super.getDisplayName();
    }

    @Override
    public void setCustomNameTag(String name)
    {
        super.setCustomNameTag(name);
    }

    @Override
    public String getCustomNameTag()
    {
        return super.getCustomNameTag();
    }

    @Override
    public boolean hasCustomName()
    {
        return super.hasCustomName();
    }

    @Override
    public void setAlwaysRenderNameTag(boolean alwaysRenderNameTag)
    {

    }

    @Override
    public boolean getAlwaysRenderNameTag()
    {
        return false;
    }

    @Override
    public void setPositionAndUpdate(double x, double y, double z)
    {

    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {

    }

    @Override
    public boolean getAlwaysRenderNameTagForRender()
    {
        return false;
    }

    @Override
    public EnumFacing getHorizontalFacing()
    {
        return null;
    }

    @Override
    protected HoverEvent getHoverEvent()
    {
        return null;
    }

    @Override
    public boolean isSpectatedByPlayer(EntityPlayerMP player)
    {
        return false;
    }

    @Override
    public AxisAlignedBB getEntityBoundingBox()
    {
        if(this.getRidingEntity() != null)
            return this.getRidingEntity().getEntityBoundingBox();
        return this.fakeBB;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if(this.getRidingEntity() != null)
            return this.getRidingEntity().getRenderBoundingBox();
        return this.fakeBB;
    }

    @Override
    public void setEntityBoundingBox(AxisAlignedBB bb)
    {

    }

    @Override
    public float getEyeHeight()
    {
        return 0f;
    }

    @Override
    public boolean isOutsideBorder()
    {
        return false;
    }

    @Override
    public void setOutsideBorder(boolean outsideBorder)
    {

    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn)
    {
       return false;
    }

    @Override
    public void sendMessage(ITextComponent component)
    {

    }

    @Override
    public boolean canUseCommand(int permLevel, String commandName)
    {
        return false;
    }

    @Override
    public BlockPos getPosition()
    {
        return BlockPos.ORIGIN;
    }

    @Override
    public Vec3d getPositionVector()
    {
        return super.getPositionVector();
    }

    @Override
    public World getEntityWorld()
    {
        return FakeWorld.INSTANCE;
    }

    @Override
    public Entity getCommandSenderEntity()
    {
        return null;
    }

    @Override
    public boolean sendCommandFeedback()
    {
        return false;
    }

    @Override
    public void setCommandStat(CommandResultStats.Type type, int amount)
    {

    }

    @Nullable
    @Override
    public MinecraftServer getServer()
    {
        return super.getServer();
    }

    @Override
    public CommandResultStats getCommandStats()
    {
        return null;
    }

    @Override
    public void setCommandStats(Entity entityIn)
    {

    }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand)
    {
        return null;
    }

    @Override
    public boolean isImmuneToExplosions()
    {
        return true;
    }

    @Override
    protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn)
    {

    }

    @Override
    public void onAddedToWorld()
    {

    }

    @Override
    public void onRemovedFromWorld()
    {

    }

    @Override
    public NBTTagCompound getEntityData()
    {
        return null;
    }

    @Override
    public boolean shouldRiderSit()
    {
        return false;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        return null;
    }

    @Override
    public UUID getPersistentID()
    {
        return super.getPersistentID();
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return false;
    }

    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
    {
        return false;
    }

    @Override
    public boolean canRiderInteract()
    {
        return true;
    }

    @Override
    public boolean shouldDismountInWater(Entity rider)
    {
        return false;
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
    public void deserializeNBT(NBTTagCompound nbt)
    {
        super.deserializeNBT(nbt);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        return super.serializeNBT();
    }

    @Override
    public boolean canTrample(World world, Block block, BlockPos pos, float fallDistance)
    {
        return false;
    }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player)
    {

    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player)
    {

    }

    @Override
    public float getRotatedYaw(Rotation transformRotation)
    {
        return 0f;
    }

    @Override
    public float getMirroredYaw(Mirror transformMirror)
    {
        return 0f;
    }

    @Override
    public boolean ignoreItemEntityData()
    {
        return true;
    }

    @Override
    public boolean setPositionNonDirty()
    {
        return false;
    }

    @Override
    public List<Entity> getPassengers()
    {
        return super.getPassengers();
    }

    @Override
    public boolean isPassenger(Entity entityIn)
    {
        return false;
    }

    @Override
    public Collection<Entity> getRecursivePassengers()
    {
        return super.getRecursivePassengers();
    }

    @Override
    public <T extends Entity> Collection<T> getRecursivePassengersByType(Class<T> entityClass)
    {
        return super.getRecursivePassengersByType(entityClass);
    }

    @Override
    public Entity getLowestRidingEntity()
    {
        return super.getLowestRidingEntity();
    }

    @Override
    public boolean isRidingSameEntity(Entity entityIn)
    {
        return super.isRidingSameEntity(entityIn);
    }

    @Override
    public boolean isRidingOrBeingRiddenBy(Entity entityIn)
    {
        return super.isRidingOrBeingRiddenBy(entityIn);
    }

    @Override
    public boolean canPassengerSteer()
    {
        return super.canPassengerSteer();
    }

    @Nullable
    @Override
    public Entity getRidingEntity()
    {
        return super.getRidingEntity();
    }

    @Override
    public EnumPushReaction getPushReaction()
    {
        return null;
    }

    @Override
    public SoundCategory getSoundCategory()
    {
        return null;
    }

    @Override
    protected int getFireImmuneTicks()
    {
        return 0;
    }
}
