/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.common.capability;

import c4.comforts.Comforts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class CapabilitySleepTime {

    @CapabilityInject(ISleepTime.class)
    public static final Capability<ISleepTime> SLEEP_TIME_CAP = null;

    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Comforts.MODID, "sleepTime");

    public static void register() {
        CapabilityManager.INSTANCE.register(ISleepTime.class, new Capability.IStorage<ISleepTime>() {
            @Override
            public NBTBase writeNBT(Capability<ISleepTime> capability, ISleepTime instance, EnumFacing side) {
                return new NBTTagLong(instance.getSleepTime());
            }

            @Override
            public void readNBT(Capability<ISleepTime> capability, ISleepTime instance, EnumFacing side, NBTBase nbt) {
                instance.setSleepTime(((NBTTagLong) nbt).getLong());
            }
        }, SleepTime::new);

        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static ISleepTime getSleepTime(final EntityPlayer playerIn) {

        if (playerIn != null && playerIn.hasCapability(SLEEP_TIME_CAP, DEFAULT_FACING)) {
            return playerIn.getCapability(SLEEP_TIME_CAP, DEFAULT_FACING);
        }

        return null;
    }

    public static ICapabilityProvider createProvider(final ISleepTime sleeping) {
        return new Provider(sleeping, SLEEP_TIME_CAP, DEFAULT_FACING);
    }

    public interface ISleepTime {

        long getSleepTime();

        void setSleepTime(long time);
    }

    public static class SleepTime implements ISleepTime {

        private long sleepTime;

        @Override
        public long getSleepTime() {
            return sleepTime;
        }

        @Override
        public void setSleepTime(long time) {
            sleepTime = time;
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTBase> {

        final Capability<ISleepTime> capability;
        final EnumFacing facing;
        final ISleepTime instance;

        Provider(final ISleepTime instance, final Capability<ISleepTime> capability,
                 @Nullable final EnumFacing facing) {
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

        final Capability<ISleepTime> getCapability() {
            return capability;
        }

        EnumFacing getFacing() {
            return facing;
        }

        final ISleepTime getInstance() {
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
                evt.addCapability(ID, createProvider(new SleepTime()));
            }
        }

        @SubscribeEvent
        public void onPlayerSleep(PlayerSleepInBedEvent evt) {

            EntityPlayer player = evt.getEntityPlayer();
            World world = player.getEntityWorld();

            if (!world.isRemote) {
                ISleepTime sleepTime = getSleepTime(player);

                if (sleepTime != null) {
                    sleepTime.setSleepTime(world.getWorldTime());
                }
            }
        }
    }
}
