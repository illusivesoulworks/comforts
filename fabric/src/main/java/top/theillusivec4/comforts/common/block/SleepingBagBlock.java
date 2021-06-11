package top.theillusivec4.comforts.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import top.theillusivec4.comforts.common.block.entity.SleepingBagBlockEntity;

public class SleepingBagBlock extends AbstractComfortsBlock {

  private static final VoxelShape SLEEPING_BAG_SHAPE = Block
      .createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);

  public SleepingBagBlock(DyeColor color) {
    super(BedType.SLEEPING_BAG, color);
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
                                    ShapeContext context) {
    return SLEEPING_BAG_SHAPE;
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new SleepingBagBlockEntity(pos, state, this.color);
  }
}
