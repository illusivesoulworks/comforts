/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.comforts.common.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class SleepDataImpl implements ISleepData {

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

  @Override
  public CompoundTag write() {
    CompoundTag compound = new CompoundTag();
    compound.putLong(WAKE_TAG, this.getWakeTime());
    compound.putLong(TIRED_TAG, this.getTiredTime());
    compound.putLong(SLEEP_TAG, this.getSleepTime());
    return compound;
  }

  @Override
  public void read(CompoundTag tag) {
    this.setWakeTime(tag.getLong(WAKE_TAG));
    this.setTiredTime(tag.getLong(TIRED_TAG));
    this.setSleepTime(tag.getLong(SLEEP_TAG));
  }
}
