package top.theillusivec4.comforts.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.block.RopeAndNailBlock;

public class HammockItem extends AbstractComfortsItem {

  public HammockItem(Block block) {
    super(block);
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    final World world = context.getWorld();
    final BlockPos pos = context.getBlockPos();
    final BlockState state = world.getBlockState(pos);
    final PlayerEntity player = context.getPlayer();

    if (state.getBlock() instanceof RopeAndNailBlock) {
      final Direction direction = state.get(HorizontalFacingBlock.FACING);
      final BlockPos blockpos = pos.offset(direction, 3);
      final BlockState blockstate = world.getBlockState(blockpos);

      if (hasPartneredRopes(state, blockstate)) {
        ActionResult result = this.place(ItemPlacementContext
            .offset(new ItemPlacementContext(context), context.getBlockPos().offset(direction),
                direction));

        if (result.isAccepted()) {
          world.setBlockState(pos, state.with(RopeAndNailBlock.SUPPORTING, true));
          world.setBlockState(blockpos, blockstate.with(RopeAndNailBlock.SUPPORTING, true));
        } else {

          if (player != null) {
            player.sendMessage(new TranslatableText("block.comforts.hammock.no_space"), true);
          }
        }
        return result;
      } else if (player != null) {
        boolean flag = hasPartneredRopes(state, world.getBlockState(pos.offset(direction, 1)));
        flag = flag || hasPartneredRopes(state, world.getBlockState(pos.offset(direction, 2)));

        if (flag) {
          player.sendMessage(new TranslatableText("block.comforts.hammock.no_space"), true);
        } else {
          player.sendMessage(new TranslatableText("block.comforts.hammock.missing_rope"), true);
        }
      }
    } else if (player != null) {
      player.sendMessage(new TranslatableText("block.comforts.hammock.no_rope"), true);
    }
    return ActionResult.FAIL;
  }

  private boolean hasPartneredRopes(BlockState state, BlockState otherState) {
    return otherState.getBlock() instanceof RopeAndNailBlock &&
        otherState.get(HorizontalFacingBlock.FACING) ==
            state.get(HorizontalFacingBlock.FACING).getOpposite() &&
        !state.get(RopeAndNailBlock.SUPPORTING) && !otherState.get(RopeAndNailBlock.SUPPORTING);
  }
}
