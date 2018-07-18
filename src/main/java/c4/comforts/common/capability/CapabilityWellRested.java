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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class CapabilityWellRested {

    @CapabilityInject(IWellRested.class)
    public static final Capability<IWellRested> RESTED_CAP = null;

    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Comforts.MODID, "wellRested");

    private static final String WAKE_TAG = "wakeTime";
    private static final String TIRED_TAG = "tiredTime";

    public static void register() {
        CapabilityManager.INSTANCE.register(IWellRested.class, new Capability.IStorage<IWellRested>() {
            @Override
            public NBTBase writeNBT(Capability<IWellRested> capability, IWellRested instance, EnumFacing side) {

                NBTTagCompound compound = new NBTTagCompound();
                compound.setLong(WAKE_TAG, instance.getWakeTime());
                compound.setLong(TIRED_TAG, instance.getTiredTime());
                return compound;
            }

            @Override
            public void readNBT(Capability<IWellRested> capability, IWellRested instance, EnumFacing side, NBTBase nbt) {

                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.setWakeTime(compound.getLong(WAKE_TAG));
                instance.setTiredTime(compound.getLong(TIRED_TAG));
            }
        }, WellRested::new);

        if (ConfigHandler.wellRested) {
            MinecraftForge.EVENT_BUS.register(new EventHandler());
        }
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static IWellRested getRested(final EntityPlayer playerIn) {

        if (playerIn != null && playerIn.hasCapability(RESTED_CAP, DEFAULT_FACING)) {
            return playerIn.getCapability(RESTED_CAP, DEFAULT_FACING);
        }

        return null;
    }

    public static ICapabilityProvider createProvider(final IWellRested rested) {
        return new Provider(rested, RESTED_CAP, DEFAULT_FACING);
    }

    public interface IWellRested {

        long getWakeTime();

        void setWakeTime(long wakeTime);

        long getTiredTime();

        void setTiredTime(long tiredTime);

    }

    public static class WellRested implements IWellRested {

        private long wakeTime = 0;
        private long tiredTime = 0;

        public long getWakeTime() {
            return wakeTime;
        }

        public void setWakeTime(long wakeTime) {
            this.wakeTime = wakeTime;
        }

        public long getTiredTime() {
            return tiredTime;
        }

        public void setTiredTime(long tiredTime) {
            this.tiredTime = tiredTime;
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTBase> {

        final Capability<IWellRested> capability;
        final EnumFacing facing;
        final IWellRested instance;

        Provider(final IWellRested instance, final Capability<IWellRested> capability, @Nullable final EnumFacing facing) {
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

        final Capability<IWellRested> getCapability() {
            return capability;
        }

        EnumFacing getFacing() {
            return facing;
        }

        final IWellRested getInstance() {
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
                evt.addCapability(ID, createProvider(new WellRested()));
            }
        }

        @SubscribeEvent
        public void onPlayerRespawn(PlayerEvent.Clone evt) {
            IWellRested newWellRested = getRested(evt.getEntityPlayer());
            IWellRested oldWellRested = getRested(evt.getOriginal());

            if (newWellRested != null && oldWellRested != null) {
                newWellRested.setWakeTime(oldWellRested.getWakeTime());
                newWellRested.setTiredTime(oldWellRested.getTiredTime());
            }
        }

        @SubscribeEvent
        public void onPlayerSleep(PlayerSleepInBedEvent evt) {

            EntityPlayer player = evt.getEntityPlayer();

            if (!player.world.isRemote) {
                IWellRested wellRested = getRested(player);
                long worldTime = player.getEntityWorld().getWorldTime();

                if (wellRested != null) {

                    if (wellRested.getWakeTime() > worldTime) {
                        wellRested.setWakeTime(0);
                        wellRested.setTiredTime(0);
                    }

                    if (wellRested.getTiredTime() > worldTime) {
                        evt.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("capability.comforts" +
                                ".notSleepy"), true);
                        evt.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
                    }
                }
            }
        }

        @SubscribeEvent
        public void onPlayerWakeUp(PlayerWakeUpEvent evt) {
            EntityPlayer player = evt.getEntityPlayer();
            CapabilitySleepTime.ISleepTime sleepTime = CapabilitySleepTime.getSleepTime(player);
            IWellRested wellRested = getRested(player);

            if (sleepTime != null && wellRested != null) {
                long wakeTime = player.world.getWorldTime();
                long timeSlept = wakeTime - sleepTime.getSleepTime();

                if (timeSlept > 500L) {
                    wellRested.setWakeTime(wakeTime);
                    wellRested.setTiredTime(wakeTime + (long) (timeSlept / ConfigHandler.sleepyRatio));
                }
            }
        }
    }
}
