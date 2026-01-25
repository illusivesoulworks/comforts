package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.common.ComfortsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
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

    if (this.ownerPlayer != null) {
      BlockPos pos = this.ownerPlayer.blockPosition();
      BlockState blockstate = this.cat.level().getBlockState(pos);

      if (blockstate.is(ComfortsTags.Blocks.HAMMOCKS) || blockstate.is(
          ComfortsTags.Blocks.SLEEPING_BAGS)) {
        this.goalPos = blockstate.getOptionalValue(
                HorizontalDirectionalBlock.FACING)
            .map((direction) -> pos.relative(direction.getOpposite()))
            .orElseGet(() -> new BlockPos(pos));

        if (!this.spaceIsOccupied()) {
          cir.setReturnValue(true);
        }
      }
    }
  }

  @Shadow
  protected abstract boolean spaceIsOccupied();
}
