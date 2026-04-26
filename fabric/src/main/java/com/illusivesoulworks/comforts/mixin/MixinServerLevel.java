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

import com.illusivesoulworks.comforts.common.ComfortsEvents;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"unused", "ConstantConditions"})
@Mixin(value = ServerLevel.class, priority = 1100)
public class MixinServerLevel {

  @Unique
  private long curTime;

  @Inject(at = @At(value = "INVOKE", target = "net/minecraft/world/clock/ServerClockManager.moveToTimeMarker(Lnet/minecraft/core/Holder;Lnet/minecraft/resources/ResourceKey;)Z"), method = "tick")
  private void comforts$setTimeOfDayPre(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
    curTime = ((ServerLevel) (Object) this).getDefaultClockTime();
  }

  @Inject(at = @At(value = "INVOKE", target = "net/minecraft/world/clock/ServerClockManager.moveToTimeMarker(Lnet/minecraft/core/Holder;Lnet/minecraft/resources/ResourceKey;)Z", shift = At.Shift.AFTER), method = "tick")
  private void comforts$setTimeOfDayPost(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
    ServerLevel world = (ServerLevel) (Object) this;
    long newTime = ComfortsEvents.getWakeTime(world, curTime, world.getDefaultClockTime());

    if (newTime != curTime) {
      Optional<Holder<WorldClock>> defaultClock = world.dimensionType().defaultClock();
      defaultClock.ifPresent(clock -> world.clockManager().setTotalTicks(clock, newTime));
    }
  }
}
