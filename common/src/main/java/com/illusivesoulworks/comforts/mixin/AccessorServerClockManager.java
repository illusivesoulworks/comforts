package com.illusivesoulworks.comforts.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerClockManager.class)
public interface AccessorServerClockManager {

  @Invoker
  ServerClockManager.ClockInstance callGetInstance(Holder<WorldClock> definition);
}
