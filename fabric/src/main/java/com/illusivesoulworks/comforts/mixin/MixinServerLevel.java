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

package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.ComfortsEvents;
import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"unused", "ConstantConditions"})
@Mixin(value = ServerLevel.class, priority = 1100)
public class MixinServerLevel {

  private long curTime;

  @Inject(at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerLevel.setDayTime(J)V"), method = "tick")
  private void comforts$setTimeOfDayPre(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
    curTime = ((ServerLevel) (Object) this).getDayTime();
  }

  @Inject(at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerLevel.setDayTime(J)V", shift = At.Shift.AFTER), method = "tick")
  private void comforts$setTimeOfDayPost(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
    ServerLevel world = (ServerLevel) (Object) this;
    long newTime = ComfortsEvents.getWakeTime(world, curTime, world.getDayTime());

    if (newTime != curTime) {
      world.setDayTime(newTime);
    }
  }
}
