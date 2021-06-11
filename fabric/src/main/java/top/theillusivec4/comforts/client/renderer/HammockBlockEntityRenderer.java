package top.theillusivec4.comforts.client.renderer;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import top.theillusivec4.comforts.client.ComfortsClientMod;
import top.theillusivec4.comforts.common.block.entity.HammockBlockEntity;

public class HammockBlockEntityRenderer
    extends AbstractComfortsBlockEntityRenderer<HammockBlockEntity> {

  private static final String BOARD = "board";

  public HammockBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    super(ctx, "hammock", ComfortsClientMod.HAMMOCK_HEAD, ComfortsClientMod.HAMMOCK_FOOT);
  }

  public static TexturedModelData getHeadTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();
    modelPartData.addChild("main",
        ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, 1.0F, 2.0F, 14.0F, 15.0F, 1.0F),
        ModelTransform.NONE);
    modelPartData.addChild(BOARD,
        ModelPartBuilder.create().uv(30, 0).cuboid(0.0F, 0.0F, 2.0F, 16.0F, 1.0F, 1.0F),
        ModelTransform.NONE);
    return TexturedModelData.of(modelData, 64, 64);
  }

  public static TexturedModelData getFootTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();
    modelPartData.addChild("main",
        ModelPartBuilder.create().uv(0, 16).cuboid(1.0F, 0.0F, 2.0F, 14.0F, 15.0F, 1.0F),
        ModelTransform.NONE);
    modelPartData.addChild(BOARD,
        ModelPartBuilder.create().uv(30, 0).cuboid(0.0F, 15.0F, 2.0F, 16.0F, 1.0F, 1.0F),
        ModelTransform.NONE);
    return TexturedModelData.of(modelData, 64, 64);
  }
}
