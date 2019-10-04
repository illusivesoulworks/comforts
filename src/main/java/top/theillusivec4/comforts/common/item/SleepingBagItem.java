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

package top.theillusivec4.comforts.common.item;

import javax.annotation.Nonnull;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.ComfortsConfig;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;

public class SleepingBagItem extends ComfortsBaseItem {

  public SleepingBagItem(Block block) {
    super(block);
  }

  @Nonnull
  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    ActionResultType result = super.onItemUse(context);
    PlayerEntity player = context.getPlayer();

    if (player != null && result == ActionResultType.SUCCESS && ComfortsConfig.SERVER.autoUse.get()
        && !player.isSneaking()) {
      BlockPos pos = context.getPos().up();
      World world = context.getWorld();
      BlockState state = world.getBlockState(pos);

      if (state.get(BedBlock.PART) != BedPart.HEAD) {
        pos = pos.offset(state.get(HorizontalBlock.HORIZONTAL_FACING));
      }
      final BlockPos blockpos = pos;
      CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {
        sleepdata.setSleeping(true);
        sleepdata.setSleepingPos(blockpos);
      });
      return ActionResultType.SUCCESS;
    }
    return result;
  }
}
