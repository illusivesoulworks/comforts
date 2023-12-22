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

package com.illusivesoulworks.comforts.common.block;

import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.block.entity.BaseComfortsBlockEntity;
import com.illusivesoulworks.comforts.common.block.entity.SleepingBagBlockEntity;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SleepingBagBlock extends BaseComfortsBlock {

  private static final VoxelShape SLEEPING_BAG_SHAPE = Block
      .box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
  private final DyeColor color;

  public SleepingBagBlock(DyeColor color) {
    super(BedType.SLEEPING_BAG, color,
        Block.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.1F));
    this.color = color;
  }

  @Nonnull
  @Override
  public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn,
                             @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
    return SLEEPING_BAG_SHAPE;
  }

  @Override
  public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
    return new SleepingBagBlockEntity(pos, state, this.color);
  }

  @Override
  public BlockEntityType<? extends BaseComfortsBlockEntity> getBlockEntityType() {
    return ComfortsRegistry.SLEEPING_BAG_BLOCK_ENTITY.get();
  }
}
