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

package top.theillusivec4.comforts.common.block;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import top.theillusivec4.comforts.ComfortsMod;
import top.theillusivec4.comforts.common.tileentity.SleepingBagTileEntity;

public class SleepingBagBlock extends ComfortsBaseBlock {

  private static final VoxelShape SLEEPING_BAG_SHAPE = Block
      .box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
  private final DyeColor color;

  public SleepingBagBlock(DyeColor color) {
    super(BedType.SLEEPING_BAG, color,
        Block.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.1F));
    this.color = color;
    this.setRegistryName(ComfortsMod.MOD_ID, "sleeping_bag_" + color.getName());
  }

  @Nonnull
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos,
      CollisionContext context) {
    return SLEEPING_BAG_SHAPE;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new SleepingBagTileEntity(pos, state, this.color);
  }
}
