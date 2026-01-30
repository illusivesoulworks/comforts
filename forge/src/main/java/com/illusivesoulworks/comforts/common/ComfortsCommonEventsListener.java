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

package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.ComfortsConstants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

public class ComfortsCommonEventsListener {

  @SubscribeEvent
  public void onPlayerTick(final TickEvent.PlayerTickEvent.Post evt) {
    ComfortsEvents.resetSleepCounter(evt.player());
  }

  @SubscribeEvent
  public void onSleepTimeCheck(final SleepingTimeCheckEvent evt) {
    evt.getSleepingLocation().ifPresent(pos -> {
      ComfortsConstants.Result result = ComfortsEvents.canSleep(evt.getEntity().level(), pos);

      switch (result) {
        case ALLOW -> evt.setResult(Result.ALLOW);
        case DENY -> evt.setResult(Result.DENY);
      }
    });
  }

  @SubscribeEvent
  public void onSleepFinished(final SleepFinishedTimeEvent evt) {
    LevelAccessor levelAccessor = evt.getLevel();

    if (levelAccessor instanceof ServerLevel serverLevel) {
      long newTime = evt.getNewTime();
      long time = ComfortsEvents.getWakeTime(serverLevel, serverLevel.getDayTime(), newTime);

      if (newTime != time) {
        evt.setTimeAddition(time);
      }
    }
  }

  @SubscribeEvent
  public void onPlayerWakeUp(final PlayerWakeUpEvent evt) {
    ComfortsEvents.onWakeUp(evt.getEntity());
  }

  @SubscribeEvent
  public void onPlayerSleep(final PlayerSleepInBedEvent evt) {
    Player.BedSleepingProblem result = ComfortsEvents.onSleep(evt.getEntity());

    if (result != null) {
      evt.setResult(result);
    }
  }
}
