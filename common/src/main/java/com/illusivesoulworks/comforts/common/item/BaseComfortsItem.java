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

package com.illusivesoulworks.comforts.common.item;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.platform.Services;
import javax.annotation.Nonnull;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BaseComfortsItem extends BlockItem {

  public BaseComfortsItem(Block block) {
    super(block, new Item.Properties());
  }

  @Override
  protected boolean placeBlock(BlockPlaceContext context, @Nonnull BlockState state) {
    return context.getLevel().setBlock(context.getClickedPos(), state, 26);
  }
}
