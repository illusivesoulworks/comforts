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

import com.illusivesoulworks.comforts.ComfortsConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface ISleepData {

  ResourceLocation ID = new ResourceLocation(ComfortsConstants.MOD_ID, "sleep_data");
  String WAKE_TAG = "wakeTime";
  String TIRED_TAG = "tiredTime";
  String SLEEP_TAG = "sleepTime";

  long getSleepTime();

  void setSleepTime(long time);

  long getWakeTime();

  void setWakeTime(long wakeTime);

  long getTiredTime();

  void setTiredTime(long tiredTime);

  BlockPos getAutoSleepPos();

  void setAutoSleepPos(BlockPos pos);

  void copyFrom(ISleepData other);

  CompoundTag write();

  void read(CompoundTag tag);
}
