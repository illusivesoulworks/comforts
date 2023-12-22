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

package com.illusivesoulworks.comforts.common.block.entity;

import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class SleepingBagBlockEntity extends BaseComfortsBlockEntity {

  public SleepingBagBlockEntity(BlockPos pos, BlockState state) {
    super(ComfortsRegistry.SLEEPING_BAG_BLOCK_ENTITY.get(), pos, state);
  }

  public SleepingBagBlockEntity(BlockPos pos, BlockState state, DyeColor colorIn) {
    super(ComfortsRegistry.SLEEPING_BAG_BLOCK_ENTITY.get(), pos, state, colorIn);
  }

  @Nonnull
  @Override
  public Component getName() {
    return this.name != null ? this.name : Component.translatable(
        "block.comforts.sleeping_bag_" + this.getColor().getSerializedName());
  }
}
