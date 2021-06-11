package top.theillusivec4.comforts.client.renderer;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import top.theillusivec4.comforts.client.ComfortsClientMod;
import top.theillusivec4.comforts.common.block.entity.SleepingBagBlockEntity;

public class SleepingBagBlockEntityRenderer
    extends AbstractComfortsBlockEntityRenderer<SleepingBagBlockEntity> {

  public SleepingBagBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    super(ctx, "sleeping_bag", ComfortsClientMod.SLEEPING_BAG_HEAD,
        ComfortsClientMod.SLEEPING_BAG_FOOT);
  }

  public static TexturedModelData getHeadTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();
    modelPartData.addChild("main", ModelPartBuilder
        .create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 3.0F), ModelTransform.NONE);
    return TexturedModelData.of(modelData, 64, 64);
  }

  public static TexturedModelData getFootTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();
    modelPartData.addChild("main",
        ModelPartBuilder.create().uv(0, 19).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 3.0F),
        ModelTransform.NONE);
    return TexturedModelData.of(modelData, 64, 64);
  }
}
