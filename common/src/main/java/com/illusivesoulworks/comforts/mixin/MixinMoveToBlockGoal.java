package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.common.ComfortsMixinHooks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MoveToBlockGoal.class)
public class MixinMoveToBlockGoal {

  @Shadow
  @Final
  protected PathfinderMob mob;

  @Shadow
  protected BlockPos blockPos;

  @WrapOperation(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/core/BlockPos.closerToCenterThan(Lnet/minecraft/core/Position;D)Z"
      ),
      method = "tick"
  )
  private boolean comforts$closerToCenterThan(BlockPos instance, Position position, double distance,
                                              Operation<Boolean> original) {

    if (ComfortsMixinHooks.isApplicableGoal((MoveToBlockGoal) (Object) this,
                                            this.mob.level().getBlockState(this.blockPos))) {
      return ComfortsMixinHooks.withinAcceptableDistance(this.blockPos, position, distance);
    }
    return original.call(instance, position, distance);
  }
}
