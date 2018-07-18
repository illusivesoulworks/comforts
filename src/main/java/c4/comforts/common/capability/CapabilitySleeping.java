/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common.capability;

import c4.comforts.Comforts;
import c4.comforts.common.ConfigHandler;
import c4.comforts.common.blocks.BlockSleepingBag;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class CapabilitySleeping {

    @CapabilityInject(ISleeping.class)
    public static final Capability<ISleeping> SLEEP_CAP = null;

    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Comforts.MODID, "sleeping");

    private static final String SLEEP_TAG = "Sleeping";
    private static final String POS_TAG = "Pos";

    public static void register() {
        CapabilityManager.INSTANCE.register(ISleeping.class, new Capability.IStorage<ISleeping>() {
            @Override
            public NBTBase writeNBT(Capability<ISleeping> capability, ISleeping instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setBoolean(SLEEP_TAG, instance.isSleeping());
                compound.setTag(POS_TAG, NBTUtil.createPosTag(instance.getPos()));
                return compound;
            }

            @Override
            public void readNBT(Capability<ISleeping> capability, ISleeping instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.setSleeping(compound.getBoolean(SLEEP_TAG));
                instance.setPos(NBTUtil.getPosFromTag(compound.getCompoundTag(POS_TAG)));
            }
        }, Sleeping::new);

        if (ConfigHandler.autoUse) {
            MinecraftForge.EVENT_BUS.register(new EventHandler());
        }
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static ISleeping getSleeping(final EntityPlayer playerIn) {

        if (playerIn != null && playerIn.hasCapability(SLEEP_CAP, DEFAULT_FACING)) {
            return playerIn.getCapability(SLEEP_CAP, DEFAULT_FACING);
        }
        return null;
    }

    public static ICapabilityProvider createProvider(final ISleeping sleeping) {
        return new Provider(sleeping, SLEEP_CAP, DEFAULT_FACING);
    }

    public interface ISleeping {

        boolean isSleeping();

        void setSleeping(boolean value);

        BlockPos getPos();

        void setPos(BlockPos pos);
    }

    public static class Sleeping implements ISleeping {

        private boolean isSleeping = false;
        private BlockPos blockPos = BlockPos.ORIGIN;

        @Override
        public boolean isSleeping() {
            return isSleeping;
        }

        @Override
        public void setSleeping(boolean value) {
            isSleeping = value;
        }

        @Override
        public BlockPos getPos() {
            return blockPos;
        }

        @Override
        public void setPos(BlockPos pos) {
            blockPos = pos;
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTBase> {

        final Capability<ISleeping> capability;
        final EnumFacing facing;
        final ISleeping instance;

        Provider(final ISleeping instance, final Capability<ISleeping> capability, @Nullable final EnumFacing facing) {
            this.instance = instance;
            this.capability = capability;
            this.facing = facing;
        }

        @Override
        public boolean hasCapability(@Nullable final Capability<?> capability, final EnumFacing facing) {
            return capability == getCapability();
        }

        @Override
        public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
            return capability == getCapability() ? getCapability().cast(this.instance) : null;
        }

        final Capability<ISleeping> getCapability() {
            return capability;
        }

        EnumFacing getFacing() {
            return facing;
        }

        final ISleeping getInstance() {
            return instance;
        }

        @Override
        public NBTBase serializeNBT() {
            return getCapability().writeNBT(getInstance(), getFacing());
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            getCapability().readNBT(getInstance(), getFacing(), nbt);
        }
    }

    private static class EventHandler {

        @SubscribeEvent
        public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {

            if (evt.getObject() instanceof EntityPlayer) {
                evt.addCapability(ID, createProvider(new Sleeping()));
            }
        }

        @SubscribeEvent
        public void onPostPlayerTick(TickEvent.PlayerTickEvent evt) {

            if (evt.phase == TickEvent.Phase.END && evt.side == Side.SERVER) {
                EntityPlayer player = evt.player;
                CapabilitySleeping.ISleeping sleeping = CapabilitySleeping.getSleeping(player);

                if (!player.isPlayerSleeping() && sleeping != null && sleeping.isSleeping()) {
                    World world = player.world;
                    BlockPos blockpos = sleeping.getPos();
                    IBlockState state = world.getBlockState(blockpos);

                    if (world.isBlockLoaded(blockpos) && state.getBlock() instanceof BlockSleepingBag) {
                        EntityPlayer.SleepResult sleepResult =
                                ((BlockSleepingBag) state.getBlock()).doSleep(world, blockpos, state, player);

                        if (sleepResult != EntityPlayer.SleepResult.OK) {
                            sleeping.setSleeping(false);
                            sleeping.setPos(BlockPos.ORIGIN);
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public void onPlayerWakeUp(PlayerWakeUpEvent evt) {
            EntityPlayer player = evt.getEntityPlayer();
            World world = player.world;

            if (!world.isRemote) {
                BlockPos pos = player.bedLocation;
                IBlockState state = world.getBlockState(pos);

                if (state.getBlock() instanceof BlockSleepingBag) {
                    ISleeping sleeping = getSleeping(player);

                    if (sleeping != null && sleeping.isSleeping()) {
                        sleeping.setSleeping(false);
                        sleeping.setPos(BlockPos.ORIGIN);
                        ItemStack stack = state.getBlock().getPickBlock(state, null, world, pos, player);

                        BlockPos pos1 = pos.offset(state.getValue(BlockSleepingBag.FACING).getOpposite());
                        world.setBlockToAir(pos);
                        world.setBlockToAir(pos1);

                        if (!player.capabilities.isCreativeMode) {
                            ItemHandlerHelper.giveItemToPlayer(player, stack, player.inventory.currentItem);
                        }
                    }
                }
            }
        }
    }
}
