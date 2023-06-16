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

package com.illusivesoulworks.comforts.platform;

import com.illusivesoulworks.comforts.common.ComfortsComponents;
import com.illusivesoulworks.comforts.common.ComfortsQuiltNetwork;
import com.illusivesoulworks.comforts.common.capability.ISleepData;
import com.illusivesoulworks.comforts.platform.services.ISleepEvents;
import java.util.Optional;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;

public class QuiltSleepEvents implements ISleepEvents {

  @Override
  public Player.BedSleepingProblem getSleepResult(Player player, BlockPos pos) {
    return EntitySleepEvents.ALLOW_SLEEPING.invoker().allowSleep(player, pos);
  }

  @Override
  public boolean isAwakeTime(Player player, BlockPos pos) {
    boolean day = player.level().isDay();
    InteractionResult result =
        EntitySleepEvents.ALLOW_SLEEP_TIME.invoker().allowSleepTime(player, pos, !day);

    if (result != InteractionResult.PASS) {
      return !result.consumesAction();
    }
    return day;
  }

  @Override
  public Optional<? extends ISleepData> getSleepData(Player player) {
    return ComfortsComponents.SLEEP_TRACKER.maybeGet(player);
  }

  @Override
  public void sendAutoSleepPacket(ServerPlayer player, BlockPos pos) {
    ComfortsQuiltNetwork.sendAutoSleep(player, pos);
  }

  @Override
  public void sendPlaceBagPacket(ServerPlayer serverPlayer, UseOnContext context) {
    ComfortsQuiltNetwork.sendPlaceBag(serverPlayer, context.getHand(), context.getClickLocation(),
        context.getClickedFace(), context.getClickedPos(), context.isInside());
  }
}
