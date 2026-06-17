package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.ComfortsEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class MixinPlayer {

  @Inject(
      at = @At("RETURN"),
      method = "tick"
  )
  private void comforts$tick(CallbackInfo ci) {
    ComfortsEvents.resetSleepCounter((Player) (Object) this);
  }

  @WrapOperation(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/entity/player/Player.stopSleepInBed(ZZ)V"
      ),
      method = "tick"
  )
  private void comforts$stopSleepInBed(Player player, boolean forcefulWakeUp,
                                       boolean updateLevelList, Operation<Void> original) {
    boolean allowedSleep = player.getSleepingPos()
        .map(pos -> ComfortsEvents.canSleep(player.level(), pos) == ComfortsConstants.Result.ALLOW)
        .orElse(false);

    if (!allowedSleep) {
      original.call(player, forcefulWakeUp, updateLevelList);
    }
  }
}
