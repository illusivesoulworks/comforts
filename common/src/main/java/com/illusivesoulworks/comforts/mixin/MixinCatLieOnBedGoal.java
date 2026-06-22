package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.common.ComfortsMixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatLieOnBedGoal.class)
public class MixinCatLieOnBedGoal {

  @Inject(
      at = @At("RETURN"),
      method = "isValidTarget",
      cancellable = true)
  private void comforts$isValidTarget(LevelReader level, BlockPos pos,
                                      CallbackInfoReturnable<Boolean> cir) {

    if (!cir.getReturnValue() && ComfortsMixinHooks.isValidCatBlock(
        level.getBlockState(pos))) {
      cir.setReturnValue(true);
    }
  }
}
