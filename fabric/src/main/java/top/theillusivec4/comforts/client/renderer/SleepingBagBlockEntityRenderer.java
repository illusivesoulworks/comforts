package top.theillusivec4.comforts.client.renderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import top.theillusivec4.comforts.common.block.entity.SleepingBagBlockEntity;

public class SleepingBagBlockEntityRenderer
    extends AbstractComfortsBlockEntityRenderer<SleepingBagBlockEntity> {

  public SleepingBagBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
    super(dispatcher, "sleeping_bag");
    this.headPiece = new ModelPart(64, 64, 0, 0);
    this.headPiece.addCuboid(0.0F, 0.0F, 0.0F, 16, 16, 3, 0.0F);
    this.footPiece = new ModelPart(64, 64, 0, 19);
    this.footPiece.addCuboid(0.0F, 0.0F, 0.0F, 16, 16, 3, 0.0F);
  }
}
