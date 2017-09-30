/*
 * Copyright (c) 2017. C4, MIT License
 */

package c4.comforts.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class WellRested implements IWellRested {

    private long sleepTime = 0;
    private long wakeTime = 0;
    private long tiredTime = 0;
    private long timeSlept = 0;

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public long getWakeTime() { return wakeTime; }

    public void setWakeTime(long wakeTime) { this.wakeTime = wakeTime; }

    public long getTiredTime() { return tiredTime; }

    public void setTiredTime(long tiredTime) { this.tiredTime = tiredTime; }

    public static class Provider implements ICapabilitySerializable<NBTBase> {

        @CapabilityInject(IWellRested.class)
        public static final Capability<IWellRested> WELL_RESTED_CAP = null;

        private IWellRested instance = WELL_RESTED_CAP.getDefaultInstance();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == WELL_RESTED_CAP;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == WELL_RESTED_CAP ? WELL_RESTED_CAP.<T> cast(this.instance) : null;
        }

        @Override
        public NBTBase serializeNBT()
        {
            return WELL_RESTED_CAP.getStorage().writeNBT(WELL_RESTED_CAP, this.instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            WELL_RESTED_CAP.getStorage().readNBT(WELL_RESTED_CAP, this.instance, null, nbt);
        }
    }

    public static class Storage implements Capability.IStorage<IWellRested> {

        @Override
        public NBTBase writeNBT(Capability<IWellRested> capability, IWellRested instance, EnumFacing side) {

            NBTTagCompound compound = new NBTTagCompound();
            compound.setLong("sleepTime", instance.getSleepTime());
            compound.setLong("wakeTime", instance.getWakeTime());
            compound.setLong("tiredTime", instance.getTiredTime());
            return compound;
        }

        @Override
        public void readNBT(Capability<IWellRested> capability, IWellRested instance, EnumFacing side, NBTBase nbt) {

            NBTTagCompound compound = (NBTTagCompound) nbt;
            instance.setSleepTime(compound.getLong("sleepTime"));
            instance.setWakeTime(compound.getLong("wakeTime"));
            instance.setTiredTime(compound.getLong("tiredTime"));
        }
    }
}
