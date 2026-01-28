package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.ComfortsEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class MixinPlayer {

  @WrapOperation(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/entity/player/Player.stopSleepInBed(ZZ)V"
      ),
      method = "tick"
  )
  private void stopSleepInBed(Player player, boolean wakeImmediately,
                              boolean updateLevelForSleepingPlayers, Operation<Void> original) {
    boolean allowedSleep = player.getSleepingPos()
        .map(pos -> ComfortsEvents.canSleep(player.level(), pos) == ComfortsConstants.Result.ALLOW)
        .orElse(false);

    if (!allowedSleep) {
      original.call(player, wakeImmediately, updateLevelForSleepingPlayers);
    }
  }
}
