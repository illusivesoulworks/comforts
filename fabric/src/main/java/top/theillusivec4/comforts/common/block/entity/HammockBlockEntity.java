package top.theillusivec4.comforts.common.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import top.theillusivec4.comforts.common.ComfortsRegistry;

public class HammockBlockEntity extends AbstractComfortsBlockEntity {

  public HammockBlockEntity(BlockPos pos, BlockState state) {
    super(ComfortsRegistry.HAMMOCK_BE, pos, state);
  }

  public HammockBlockEntity(BlockPos pos, BlockState state, DyeColor colorIn) {
    super(ComfortsRegistry.HAMMOCK_BE, pos, state, colorIn);
  }
}
