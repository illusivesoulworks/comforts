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

import com.illusivesoulworks.comforts.common.ComfortsConfig;
import com.illusivesoulworks.comforts.platform.Services;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public class SleepingBagItem extends BaseComfortsItem {

  public SleepingBagItem(Block block) {
    super(block);
  }

  @Nonnull
  @Override
  public InteractionResult useOn(@Nonnull UseOnContext context) {
    final InteractionResult result = super.useOn(context);
    final Player player = context.getPlayer();

    if (player instanceof ServerPlayer serverPlayer && result.consumesAction()
        && ComfortsConfig.SERVER.autoUse.get() && !player.isCrouching()) {
      final BlockPos pos = context.getClickedPos().above();
      Services.SLEEP_EVENTS.getSleepData(player).ifPresent(data -> data.setAutoSleepPos(pos));
      Services.SLEEP_EVENTS.sendAutoSleepPacket(serverPlayer, pos);
    }
    return result;
  }
}
