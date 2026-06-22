package com.illusivesoulworks.comforts.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ServerClockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ServerClockManager.ClockInstance.class)
public interface AccessorClockInstance {

  @Accessor
  Map<ResourceKey<ClockTimeMarker>, ClockTimeMarker> getTimeMarkers();
}
