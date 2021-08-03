/*
 * Copyright (c) 2017-2020 C4
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.comforts.ComfortsMod;

public class CapabilitySleepData {

  @CapabilityInject(ISleepData.class)
  public static final Capability<ISleepData> SLEEP_DATA_CAP = null;

  public static final ResourceLocation ID = new ResourceLocation(ComfortsMod.MOD_ID, "sleep_data");

  private static final String WAKE_TAG = "wakeTime";
  private static final String TIRED_TAG = "tiredTime";
  private static final String SLEEP_TAG = "sleepTime";

  public static void register() {
    MinecraftForge.EVENT_BUS.register(new CapabilityEvents());
    CapabilityManager.INSTANCE.register(ISleepData.class);
  }

  @SuppressWarnings("ConstantConditions")
  public static LazyOptional<ISleepData> getCapability(final Player player) {
    return player.getCapability(SLEEP_DATA_CAP);
  }

  public interface ISleepData {

    long getSleepTime();

    void setSleepTime(long time);

    long getWakeTime();

    void setWakeTime(long wakeTime);

    long getTiredTime();

    void setTiredTime(long tiredTime);

    BlockPos getAutoSleepPos();

    void setAutoSleepPos(BlockPos pos);

    void copyFrom(ISleepData other);
  }

  public static class SleepDataWrapper implements ISleepData {

    long sleepTime = 0;
    long wakeTime = 0;
    long tiredTime = 0;
    BlockPos autoSleepPos = null;

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
    public BlockPos getAutoSleepPos() {
      return autoSleepPos;
    }

    @Override
    public void setAutoSleepPos(BlockPos pos) {
      autoSleepPos = pos;
    }

    @Override
    public void copyFrom(ISleepData other) {
      this.setSleepTime(other.getSleepTime());
      this.setTiredTime(other.getTiredTime());
      this.setWakeTime(other.getWakeTime());
    }
  }

  public static class Provider implements ICapabilitySerializable<Tag> {

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

    @Override
    public Tag serializeNBT() {
      CompoundTag compound = new CompoundTag();
      compound.putLong(WAKE_TAG, data.getWakeTime());
      compound.putLong(TIRED_TAG, data.getTiredTime());
      compound.putLong(SLEEP_TAG, data.getSleepTime());
      return compound;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
      CompoundTag compound = (CompoundTag) nbt;
      data.setWakeTime(compound.getLong(WAKE_TAG));
      data.setTiredTime(compound.getLong(TIRED_TAG));
      data.setSleepTime(compound.getLong(SLEEP_TAG));
    }
  }

  public static class CapabilityEvents {

    @SubscribeEvent
    public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
      Entity entity = evt.getObject();

      if (entity instanceof Player) {
        evt.addCapability(CapabilitySleepData.ID, new Provider());
      }
    }

    @SubscribeEvent
    public void onPlayerDeath(final PlayerEvent.Clone evt) {

      if (evt.isWasDeath()) {
        final Player player = evt.getPlayer();
        final Player original = evt.getOriginal();
        original.revive();
        CapabilitySleepData.getCapability(player).ifPresent(
            sleepdata -> CapabilitySleepData.getCapability(original)
                .ifPresent(sleepdata::copyFrom));
      }
    }
  }
}
