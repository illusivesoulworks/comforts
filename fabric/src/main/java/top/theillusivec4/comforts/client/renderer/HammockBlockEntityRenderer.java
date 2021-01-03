package top.theillusivec4.comforts.client.renderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import top.theillusivec4.comforts.common.block.entity.HammockBlockEntity;

public class HammockBlockEntityRenderer
    extends AbstractComfortsBlockEntityRenderer<HammockBlockEntity> {

  protected ModelPart headBoard;
  protected ModelPart footBoard;

  public HammockBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
    super(dispatcher, "hammock");
    this.headPiece = new ModelPart(64, 64, 0, 0);
    this.headPiece.addCuboid(1.0F, 1.0F, 0.0F, 14, 15, 1, 0.0F);
    this.headBoard = new ModelPart(64, 64, 30, 0);
    this.headBoard.addCuboid(0.0F, 0.0F, 0.0F, 16, 1, 1, 0.0F);
    this.footPiece = new ModelPart(64, 64, 0, 16);
    this.footPiece.addCuboid(1.0F, 0.0F, 0.0F, 14, 15, 1, 0.0F);
    this.footBoard = new ModelPart(64, 64, 30, 0);
    this.footBoard.addCuboid(0.0F, 15.0F, 0.0F, 16, 1, 1, 0.0F);
  }

  @Override
  protected void renderPiece(MatrixStack matrixStack, VertexConsumerProvider buffer, boolean isHead,
                             Direction direction, SpriteIdentifier material, int light, int overlay,
                             boolean p_228847_8_) {
    this.headPiece.visible = isHead;
    this.headBoard.visible = isHead;
    this.footPiece.visible = !isHead;
    this.footBoard.visible = !isHead;
    matrixStack.push();
    matrixStack.translate(0.0D, 0.0625D, p_228847_8_ ? -1.0D : 0.0D);
    matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
    matrixStack.translate(0.5D, 0.5D, 0.5D);
    matrixStack
        .multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F + direction.getHorizontal()));
    matrixStack.translate(-0.5D, -0.5D, -0.5D);
    final VertexConsumer ivertexbuilder =
        material.getVertexConsumer(buffer, RenderLayer::getEntitySolid);
    this.headPiece.render(matrixStack, ivertexbuilder, light, overlay);
    this.headBoard.render(matrixStack, ivertexbuilder, light, overlay);
    this.footPiece.render(matrixStack, ivertexbuilder, light, overlay);
    this.footBoard.render(matrixStack, ivertexbuilder, light, overlay);
    matrixStack.pop();
  }
}
