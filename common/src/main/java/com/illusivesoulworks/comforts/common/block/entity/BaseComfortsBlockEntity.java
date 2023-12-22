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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseComfortsBlockEntity extends BlockEntity implements Nameable {

  private DyeColor color;
  @Nullable
  protected Component name;

  public BaseComfortsBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos,
                                 BlockState state) {
    super(blockEntityType, pos, state);
  }

  public BaseComfortsBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state,
                                 DyeColor colorIn) {
    this(blockEntityType, pos, state);
    this.setColor(colorIn);
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  public DyeColor getColor() {

    if (this.color == null) {
      this.color = ((BedBlock) this.getBlockState().getBlock()).getColor();
    }
    return this.color;
  }

  public void setColor(DyeColor color) {
    this.color = color;
  }

  @Override
  public boolean hasCustomName() {
    return this.name != null;
  }

  @Nullable
  @Override
  public Component getCustomName() {
    return this.name;
  }

  public void setName(@Nonnull Component name) {
    this.name = name;
  }
}
