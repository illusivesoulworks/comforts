package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.common.ComfortsMixinHooks;
import com.illusivesoulworks.comforts.common.ComfortsTags;
import com.illusivesoulworks.comforts.common.block.BaseComfortsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CatSitOnBlockGoal.class)
public abstract class MixinCatSitOnBlockGoal {

  @Inject(
      at = @At(
          value = "INVOKE_ASSIGN",
          target = "net/minecraft/world/level/LevelReader.getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"),
      method = "isValidTarget",
      locals = LocalCapture.CAPTURE_FAILSOFT,
      cancellable = true)
  private void comforts$isValidTarget(LevelReader levelReader, BlockPos blockPos,
                                      CallbackInfoReturnable<Boolean> cir, BlockState blockState) {

    if (ComfortsMixinHooks.isValidCatBlock(blockState)) {
      cir.setReturnValue(true);
    }
  }
}
