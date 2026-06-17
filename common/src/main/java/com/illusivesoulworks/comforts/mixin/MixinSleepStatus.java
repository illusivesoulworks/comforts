package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.common.ComfortsEvents;
import com.illusivesoulworks.comforts.common.util.ServerAware;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SleepStatus.class)
public class MixinSleepStatus implements ServerAware {

  @Shadow
  private int activePlayers;

  @Unique
  private ServerLevel comforts$serverLevel;

  @Inject(
      at = @At("HEAD"),
      method = "sleepersNeeded",
      cancellable = true
  )
  private void comforts$sleepersNeeded(int sleepPercentageNeeded, CallbackInfoReturnable<Integer> cir) {

    if (this.comforts$getServer().isBrightOutside()) {
      int result = ComfortsEvents.sleepersNeeded(this.activePlayers);

      if (result > 0) {
        cir.setReturnValue(result);
      }
    }
  }

  @Override
  public void comforts$setServer(ServerLevel server) {
    this.comforts$serverLevel = server;
  }

  @Override
  public ServerLevel comforts$getServer() {
    return this.comforts$serverLevel;
  }
}
