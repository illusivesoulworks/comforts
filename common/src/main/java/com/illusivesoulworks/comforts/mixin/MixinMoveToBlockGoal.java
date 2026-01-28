package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.common.ComfortsMixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MoveToBlockGoal.class)
public class MixinMoveToBlockGoal {

  @Shadow
  @Final
  protected PathfinderMob mob;

  @Shadow
  protected BlockPos blockPos;

  @Inject(
      at = @At("RETURN"),
      method = "acceptedDistance",
      cancellable = true
  )
  private void comforts$acceptedDistance(CallbackInfoReturnable<Double> cir) {
    ComfortsMixinHooks.getAcceptedDistance((MoveToBlockGoal) (Object) this, this.blockPos, this.mob)
        .ifPresent(cir::setReturnValue);
  }
}
