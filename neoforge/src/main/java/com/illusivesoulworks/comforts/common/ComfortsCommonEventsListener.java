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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.util.ClockAdjustment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class ComfortsCommonEventsListener {

  @SubscribeEvent
  public void onPlayerTick(final PlayerTickEvent.Post evt) {
    ComfortsEvents.resetSleepCounter(evt.getEntity());
  }

  @SubscribeEvent
  public void onSleepTimeCheck(final CanContinueSleepingEvent evt) {
    evt.getEntity().getSleepingPos().ifPresent(sleepingPos -> {
      ComfortsConstants.Result result =
          ComfortsEvents.canSleep(evt.getEntity().level(), sleepingPos);

      if (result == ComfortsConstants.Result.ALLOW) {
        evt.setContinueSleeping(true);
      } else if (result == ComfortsConstants.Result.DENY) {
        evt.setContinueSleeping(false);
      }
    });
  }

  @SubscribeEvent
  public void onSleepFinished(final SleepFinishedTimeEvent evt) {
    LevelAccessor levelAccessor = evt.getLevel();

    if (levelAccessor instanceof ServerLevel serverLevel) {
      long currentTime = serverLevel.getOverworldClockTime();
      long adjustedTime = currentTime;

      try {
        adjustedTime = getClockAdjustmentTime(evt.getAdjustment(), serverLevel, currentTime);
      } catch (IllegalStateException e) {
        ComfortsConstants.LOG.error("Failed to adjust wake time accurately.", e);
      }
      long newTime = ComfortsEvents.getWakeTime(serverLevel, currentTime);

      if (newTime != adjustedTime) {
        evt.setAdjustment(new ClockAdjustment.Absolute(newTime));
      }
    }
  }

  private static long getClockAdjustmentTime(ClockAdjustment originalAdjustment, ServerLevel level, long currentTime)
          throws IllegalStateException {
    if (originalAdjustment instanceof ClockAdjustment.Absolute(long ticks)) {
      return ticks;
    } else if (originalAdjustment instanceof ClockAdjustment.Relative(long ticks)) {
      return currentTime + ticks;
    } else if (originalAdjustment instanceof ClockAdjustment.Marker(ResourceKey<ClockTimeMarker> key)) {
      ClockTimeMarker marker = ComfortsEvents.getMarkerFromOverworldClock(level, key);

      if (marker != null) {
        return marker.resolveTimeToMoveTo(currentTime);
      }
    }
    return currentTime;
  }

  @SubscribeEvent
  public void onPlayerWakeUp(final PlayerWakeUpEvent evt) {
    ComfortsEvents.onWakeUp(evt.getEntity());
  }
}
