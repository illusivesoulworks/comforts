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

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ComfortsCommonEventsListener {

  @SubscribeEvent
  public void onPlayerSetSpawn(final PlayerSetSpawnEvent evt) {

    if (!ComfortsEvents.canSetSpawn(evt.getEntity(), evt.getNewSpawn())) {
      evt.setCanceled(true);
    }
  }

  @SubscribeEvent
  public void onSleepTimeCheck(final SleepingTimeCheckEvent evt) {
    evt.getSleepingLocation().ifPresent(pos -> {
      ComfortsEvents.Result result = ComfortsEvents.checkTime(evt.getEntity().level(), pos);

      switch (result) {
        case ALLOW -> evt.setResult(Event.Result.ALLOW);
        case DENY -> evt.setResult(Event.Result.DENY);
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
