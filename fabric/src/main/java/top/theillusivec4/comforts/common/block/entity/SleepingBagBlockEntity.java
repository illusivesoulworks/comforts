package top.theillusivec4.comforts.common.block.entity;

import net.minecraft.util.DyeColor;
import top.theillusivec4.comforts.common.ComfortsRegistry;

public class SleepingBagBlockEntity extends AbstractComfortsBlockEntity {

  public SleepingBagBlockEntity() {
    super(ComfortsRegistry.SLEEPING_BAG_BE);
  }

  public SleepingBagBlockEntity(DyeColor colorIn) {
    super(ComfortsRegistry.SLEEPING_BAG_BE, colorIn);
  }
}
