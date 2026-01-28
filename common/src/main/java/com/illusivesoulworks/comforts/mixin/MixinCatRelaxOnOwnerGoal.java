package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.common.ComfortsMixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cat.CatRelaxOnOwnerGoal.class)
public abstract class MixinCatRelaxOnOwnerGoal {

  @Shadow
  @Final
  private Cat cat;

  @Shadow
  @Nullable
  private Player ownerPlayer;

  @Shadow
  @Nullable
  private BlockPos goalPos;

  @Inject(
      at = @At(
          target = "net/minecraft/world/level/Level.getBlockState (Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
          value = "INVOKE_ASSIGN"
      ),
      method = "canUse",
      cancellable = true
  )
  private void comforts$canUse(CallbackInfoReturnable<Boolean> cir) {
    BlockPos blockPos = ComfortsMixinHooks.getRelaxableCatBlock(this.cat, this.ownerPlayer);

    if (blockPos != null) {
      this.goalPos = blockPos;

      if (!this.spaceIsOccupied()) {
        cir.setReturnValue(true);
      }
    }
  }

  @Shadow
  protected abstract boolean spaceIsOccupied();
}
