package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.common.ComfortsTags;
import com.illusivesoulworks.comforts.common.block.BaseComfortsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CatSitOnBlockGoal.class)
public class MixinCatSitOnBlockGoal {

  @Inject(
      at = @At(
          value = "INVOKE_ASSIGN",
          target = "net/minecraft/world/level/LevelReader.getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"),
      method = "isValidTarget",
      locals = LocalCapture.CAPTURE_FAILSOFT,
      cancellable = true)
  private void comforts$isValidTarget(LevelReader levelReader, BlockPos blockPos,
                                      CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
    if (blockState.is(ComfortsTags.Blocks.SLEEPING_BAGS,
                      state -> state.getOptionalValue(BaseComfortsBlock.PART)
                          .map(part -> part != BedPart.HEAD).orElse(true)) ||
        blockState.is(ComfortsTags.Blocks.HAMMOCKS,
                      state -> state.getOptionalValue(BaseComfortsBlock.PART)
                          .map(part -> part != BedPart.HEAD).orElse(true))) {
      cir.setReturnValue(true);
    }
  }
}
