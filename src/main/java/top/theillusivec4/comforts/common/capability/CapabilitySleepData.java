/*
 * Copyright (C) 2017-2019  C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.common.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.comforts.Comforts;

public class CapabilitySleepData {

  @CapabilityInject(ISleepData.class)
  public static final Capability<ISleepData> SLEEP_DATA_CAP = null;

  public static final ResourceLocation ID = new ResourceLocation(Comforts.MODID, "sleep_data");

  private static final String WAKE_TAG = "wakeTime";
  private static final String TIRED_TAG = "tiredTime";
  private static final String SLEEP_TAG = "sleepTime";

  public static void register() {
    MinecraftForge.EVENT_BUS.register(new CapabilityEvents());
    CapabilityManager.INSTANCE.register(ISleepData.class, new Capability.IStorage<ISleepData>() {

      @Override
      public INBT writeNBT(Capability<ISleepData> capability, ISleepData instance, Direction side) {
        CompoundNBT compound = new CompoundNBT();
        compound.putLong(WAKE_TAG, instance.getWakeTime());
        compound.putLong(TIRED_TAG, instance.getTiredTime());
        compound.putLong(SLEEP_TAG, instance.getSleepTime());
        return compound;
      }

      @Override
      public void readNBT(Capability<ISleepData> capability, ISleepData instance, Direction side,
          INBT nbt) {
        CompoundNBT compound = (CompoundNBT) nbt;
        instance.setWakeTime(compound.getLong(WAKE_TAG));
        instance.setTiredTime(compound.getLong(TIRED_TAG));
        instance.setSleepTime(compound.getLong(SLEEP_TAG));
      }
    }, SleepDataWrapper::new);
  }

  @SuppressWarnings("ConstantConditions")
  public static LazyOptional<ISleepData> getCapability(final PlayerEntity player) {
    return player.getCapability(SLEEP_DATA_CAP);
  }

  public interface ISleepData {

    long getSleepTime();

    void setSleepTime(long time);

    long getWakeTime();

    void setWakeTime(long wakeTime);

    long getTiredTime();

    void setTiredTime(long tiredTime);

    BlockPos getAutoSleepingPos();

    void setAutoSleepingPos(BlockPos pos);

    void copyFrom(ISleepData other);
  }

  public static class SleepDataWrapper implements ISleepData {

    long sleepTime = 0;
    long wakeTime = 0;
    long tiredTime = 0;
    boolean autoSleeping = false;
    BlockPos autoSleepingPos = null;

    @Override
    public long getSleepTime() {
      return sleepTime;
    }

    @Override
    public void setSleepTime(long time) {
      sleepTime = time;
    }

    @Override
    public long getWakeTime() {
      return wakeTime;
    }

    @Override
    public void setWakeTime(long time) {
      wakeTime = time;
    }

    @Override
    public long getTiredTime() {
      return tiredTime;
    }

    @Override
    public void setTiredTime(long time) {
      tiredTime = time;
    }

    @Override
    public BlockPos getAutoSleepingPos() {
      return autoSleepingPos;
    }

    @Override
    public void setAutoSleepingPos(BlockPos pos) {
      autoSleepingPos = pos;
    }

    @Override
    public void copyFrom(ISleepData other) {
      this.setSleepTime(other.getSleepTime());
      this.setTiredTime(other.getTiredTime());
      this.setWakeTime(other.getWakeTime());
    }
  }

  public static class Provider implements ICapabilitySerializable<INBT> {

    final LazyOptional<ISleepData> optional;
    final ISleepData data;

    Provider() {
      this.data = new SleepDataWrapper();
      this.optional = LazyOptional.of(() -> data);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> capability, Direction side) {
      return SLEEP_DATA_CAP.orEmpty(capability, optional);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public INBT serializeNBT() {
      return SLEEP_DATA_CAP.writeNBT(data, null);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void deserializeNBT(INBT nbt) {
      SLEEP_DATA_CAP.readNBT(data, null, nbt);
    }
  }

  public static class CapabilityEvents {

    @SubscribeEvent
    public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
      Entity entity = evt.getObject();

      if (entity instanceof PlayerEntity) {
        evt.addCapability(CapabilitySleepData.ID, new Provider());
      }
    }

    @SubscribeEvent
    public void onPlayerDeath(final PlayerEvent.Clone evt) {

      if (evt.isWasDeath()) {
        PlayerEntity player = evt.getPlayer();
        PlayerEntity original = evt.getOriginal();
        original.revive();
        CapabilitySleepData.getCapability(player).ifPresent(
            sleepdata -> CapabilitySleepData.getCapability(original)
                .ifPresent(sleepdata::copyFrom));
      }
    }
  }
}
