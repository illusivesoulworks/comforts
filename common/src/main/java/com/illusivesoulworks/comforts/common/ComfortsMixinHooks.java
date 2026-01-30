package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.common.block.BaseComfortsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class ComfortsMixinHooks {

  public static boolean isValidCatBlock(BlockState blockState) {
    return isValidBlockPart(ComfortsTags.Blocks.SLEEPING_BAGS, blockState)
        || isValidBlockPart(ComfortsTags.Blocks.HAMMOCKS, blockState);
  }

  public static boolean isApplicableGoal(MoveToBlockGoal goal, BlockState state) {
    boolean catGoal = goal instanceof CatSitOnBlockGoal || goal instanceof CatLieOnBedGoal;
    return catGoal && isValidCatBlock(state);
  }

  public static boolean withinAcceptableDistance(BlockPos targetPosition, Position position,
                                                 double distance) {
    double d0 = targetPosition.getX() + 0.5D - position.x();
    double d1 = targetPosition.getY() - position.y();
    double d2 = targetPosition.getZ() + 0.5D - position.z();
    return (d0 * d0 + d1 * d1 + d2 * d2) <= distance;
  }

  public static BlockPos getRelaxableCatBlock(Cat cat, Player ownerPlayer) {

    if (ownerPlayer != null) {
      BlockPos pos = ownerPlayer.blockPosition();
      BlockState blockstate = cat.level().getBlockState(pos);

      if (blockstate.is(ComfortsTags.Blocks.HAMMOCKS) || blockstate.is(
          ComfortsTags.Blocks.SLEEPING_BAGS)) {
        Direction direction = blockstate.getValue(HorizontalDirectionalBlock.FACING).getOpposite();
        return pos.relative(direction);
      }
    }
    return null;
  }

  private static boolean isValidBlockPart(TagKey<Block> tagKey, BlockState blockState) {
    return blockState.is(tagKey, state -> state.getOptionalValue(BaseComfortsBlock.PART)
        .map(part -> part != BedPart.HEAD).orElse(true));
  }
}
