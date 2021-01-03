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

package top.theillusivec4.comforts.common;

import java.util.EnumMap;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import top.theillusivec4.comforts.common.tileentity.HammockTileEntity;
import top.theillusivec4.comforts.common.tileentity.SleepingBagTileEntity;

public class ComfortsRegistry {

  public static final EnumMap<DyeColor, Block> SLEEPING_BAGS = new EnumMap<>(DyeColor.class);

  public static final EnumMap<DyeColor, Block> HAMMOCKS = new EnumMap<>(DyeColor.class);

  @ObjectHolder("comforts:rope_and_nail")
  public static final Block ROPE_AND_NAIL;

  @ObjectHolder("comforts:sleeping_bag")
  public static final TileEntityType<SleepingBagTileEntity> SLEEPING_BAG_TE;

  @ObjectHolder("comforts:hammock")
  public static final TileEntityType<HammockTileEntity> HAMMOCK_TE;

  static {
    ROPE_AND_NAIL = null;
    SLEEPING_BAG_TE = null;
    HAMMOCK_TE = null;
  }
}
