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

package top.theillusivec4.comforts.common.tileentity;

import net.minecraft.item.DyeColor;
import top.theillusivec4.comforts.common.ComfortsRegistry;

public class SleepingBagTileEntity extends ComfortsBaseTileEntity {

  public SleepingBagTileEntity() {
    super(ComfortsRegistry.SLEEPING_BAG_TE);
  }

  public SleepingBagTileEntity(DyeColor colorIn) {
    super(ComfortsRegistry.SLEEPING_BAG_TE, colorIn);
  }
}
