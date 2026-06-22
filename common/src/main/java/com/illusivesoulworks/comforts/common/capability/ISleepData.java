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
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface ISleepData {

  Identifier ID =
      Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "sleep_data");
  String WAKE_TAG = "wakeTime";
  String TIRED_TAG = "tiredTime";
  String SLEEP_TAG = "sleepTime";

  long getSleepTime();

  void setSleepTime(long time);

  long getWakeTime();

  void setWakeTime(long wakeTime);

  long getTiredTime();

  void setTiredTime(long tiredTime);

  @Nullable BlockPos getAutoSleepPos();

  void setAutoSleepPos(@Nullable BlockPos pos);

  void copyFrom(ISleepData other);

  void write(ValueOutput output);

  void read(ValueInput input);
}
