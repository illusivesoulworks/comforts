package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.common.block.BaseComfortsBlock;
import com.illusivesoulworks.comforts.common.block.HammockBlock;
import com.illusivesoulworks.comforts.common.block.SleepingBagBlock;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
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

  public static BlockPos getRelaxableCatBlock(Cat cat, Player ownerPlayer) {

    if (ownerPlayer != null) {
      BlockPos pos = ownerPlayer.blockPosition();
      BlockState blockstate = cat.level().getBlockState(pos);

      if (blockstate.is(ComfortsTags.Blocks.HAMMOCKS) || blockstate.is(
          ComfortsTags.Blocks.SLEEPING_BAGS)) {
        return blockstate.getOptionalValue(HorizontalDirectionalBlock.FACING)
            .map((direction) -> pos.relative(direction.getOpposite()))
            .orElseGet(() -> new BlockPos(pos));
      }
    }
    return null;
  }

  public static Optional<Double> getAcceptedDistance(MoveToBlockGoal moveToBlockGoal, BlockPos pos,
                                                     Mob mob) {

    if ((moveToBlockGoal instanceof CatLieOnBedGoal)
        || (moveToBlockGoal instanceof CatSitOnBlockGoal)) {

      if (pos != null) {
        Block block = mob.level().getBlockState(pos).getBlock();

        if (block instanceof HammockBlock || block instanceof SleepingBagBlock) {
          return Optional.of(1.8D);
        }
      }
    }
    return Optional.empty();
  }

  private static boolean isValidBlockPart(TagKey<Block> tagKey, BlockState blockState) {
    return blockState.is(tagKey, state -> state.getOptionalValue(BaseComfortsBlock.PART)
        .map(part -> part != BedPart.HEAD).orElse(true));
  }
}
