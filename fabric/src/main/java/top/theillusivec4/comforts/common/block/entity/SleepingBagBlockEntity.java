package top.theillusivec4.comforts.common.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import top.theillusivec4.comforts.common.ComfortsRegistry;

public class SleepingBagBlockEntity extends AbstractComfortsBlockEntity {

  public SleepingBagBlockEntity(BlockPos pos, BlockState state) {
    super(ComfortsRegistry.SLEEPING_BAG_BE, pos, state);
  }

  public SleepingBagBlockEntity(BlockPos pos, BlockState state, DyeColor colorIn) {
    super(ComfortsRegistry.SLEEPING_BAG_BE, pos, state, colorIn);
  }
}
