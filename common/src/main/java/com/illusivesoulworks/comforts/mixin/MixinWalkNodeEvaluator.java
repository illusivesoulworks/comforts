package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.common.block.HammockBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkNodeEvaluator.class)
public abstract class MixinWalkNodeEvaluator extends NodeEvaluator {

  @Inject(
      at = @At("HEAD"),
      method = "checkNeighbourBlocks",
      cancellable = true
  )
  private static void comforts$checkNeighbourBlocks(PathfindingContext context,
                                                    int x, int y, int z,
                                                    PathType pathType,
                                                    CallbackInfoReturnable<PathType> cir) {
    BlockState state = context.level().getBlockState(new BlockPos(x, y + 1, z));

    if (state.getBlock() instanceof HammockBlock) {
      cir.setReturnValue(PathType.BLOCKED);
    }
  }

  @WrapOperation(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/level/pathfinder/WalkNodeEvaluator.tryJumpOn(IIIIDLnet/minecraft/core/Direction;Lnet/minecraft/world/level/pathfinder/PathType;Lnet/minecraft/core/BlockPos$MutableBlockPos;)Lnet/minecraft/world/level/pathfinder/Node;"),
      method = "findAcceptedNode"
  )
  private Node comforts$findAcceptedNode(WalkNodeEvaluator instance, int x,
                                         int y, int z,
                                         int verticalDeltaLimit,
                                         double nodeFloorLevel,
                                         Direction direction,
                                         PathType pathType,
                                         BlockPos.MutableBlockPos pos,
                                         Operation<Node> original) {

    if (this.mob instanceof Cat) {
      BlockState state = this.mob.level().getBlockState(new BlockPos(x, y + 1, z));

      if (state.getBlock() instanceof HammockBlock) {
        return original.call(instance, x, y, z, verticalDeltaLimit + 1, nodeFloorLevel, direction,
                             pathType, pos);
      }
    }
    return original.call(instance, x, y, z, verticalDeltaLimit, nodeFloorLevel, direction, pathType,
                         pos);
  }
}
