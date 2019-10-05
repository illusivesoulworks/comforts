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

package top.theillusivec4.comforts.common.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import top.theillusivec4.comforts.Comforts;
import top.theillusivec4.comforts.common.tileentity.SleepingBagTileEntity;

public class SleepingBagBlock extends ComfortsBaseBlock {

  private static final VoxelShape SLEEPING_BAG_SHAPE = Block
      .makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
  private final DyeColor color;

  public SleepingBagBlock(DyeColor color) {
    super(BedType.SLEEPING_BAG, color,
        Block.Properties.create(Material.WOOL).sound(SoundType.CLOTH).hardnessAndResistance(0.1F));
    this.color = color;
    this.setRegistryName(Comforts.MODID, "sleeping_bag_" + color.getTranslationKey());
  }

  @Nonnull
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
      ISelectionContext context) {
    return SLEEPING_BAG_SHAPE;
  }

  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new SleepingBagTileEntity(this.color);
  }
}
