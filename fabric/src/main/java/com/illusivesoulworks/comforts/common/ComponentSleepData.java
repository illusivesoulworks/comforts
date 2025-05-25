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

package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.common.capability.SleepDataImpl;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.ladysnake.cca.api.v3.component.Component;

public class ComponentSleepData extends SleepDataImpl implements Component {

  @Override
  public void readFromNbt(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
    this.read(tag.getCompound("Data").orElse(new CompoundTag()));
  }

  @Override
  public void writeToNbt(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
    tag.put("Data", this.write());
  }
}
