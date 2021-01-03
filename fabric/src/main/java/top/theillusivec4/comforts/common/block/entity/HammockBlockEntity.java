package top.theillusivec4.comforts.common.block.entity;

import net.minecraft.util.DyeColor;
import top.theillusivec4.comforts.common.ComfortsRegistry;

public class HammockBlockEntity extends AbstractComfortsBlockEntity {

  public HammockBlockEntity() {
    super(ComfortsRegistry.HAMMOCK_BE);
  }

  public HammockBlockEntity(DyeColor colorIn) {
    super(ComfortsRegistry.HAMMOCK_BE, colorIn);
  }
}
