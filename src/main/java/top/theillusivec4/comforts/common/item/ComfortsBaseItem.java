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

package top.theillusivec4.comforts.common.item;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import top.theillusivec4.comforts.Comforts;

public class ComfortsBaseItem extends BlockItem {

  public ComfortsBaseItem(Block block) {
    super(block, new Item.Properties().group(Comforts.CREATIVE_TAB));
    this.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
  }

  @Override
  protected boolean placeBlock(BlockItemUseContext context, @Nonnull BlockState state) {
    return context.getWorld().setBlockState(context.getPos(), state, 26);
  }
}
