package top.theillusivec4.comforts.client.renderer;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.ComfortsMod;
import top.theillusivec4.comforts.common.block.entity.AbstractComfortsBlockEntity;

public class AbstractComfortsBlockEntityRenderer<T extends AbstractComfortsBlockEntity>
    implements BlockEntityRenderer<T> {

  private final String type;

  protected ModelPart headPiece;
  protected ModelPart footPiece;

  public AbstractComfortsBlockEntityRenderer(BlockEntityRendererFactory.Context ctx, String type,
                                             EntityModelLayer headModel,
                                             EntityModelLayer footModel) {
    this.headPiece = ctx.getLayerModelPart(headModel);
    this.footPiece = ctx.getLayerModelPart(footModel);
    this.type = type;
  }

  @Override
  public void render(AbstractComfortsBlockEntity blockEntity, float partialTicks,
                     MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                     int combinedLightIn, int combinedOverlayIn) {
    SpriteIdentifier material = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
        new Identifier(ComfortsMod.MOD_ID,
            "entity/" + type + "/" + blockEntity.getColor().getName()));
    World world = blockEntity.getWorld();

    if (world != null) {
      BlockState blockState = blockEntity.getCachedState();
      DoubleBlockProperties.PropertySource<? extends BedBlockEntity> propertySource =
          DoubleBlockProperties.toPropertySource(BlockEntityType.BED, BedBlock::getBedPart,
              BedBlock::getOppositePartDirection, ChestBlock.FACING, blockState, world,
              blockEntity.getPos(), (worldAccess, blockPos) -> false);
      int k = (propertySource.apply(new LightmapCoordinatesRetriever<>())).get(combinedLightIn);
      this.renderPart(matrixStack, vertexConsumerProvider,
          blockState.get(BedBlock.PART) == BedPart.HEAD ? this.headPiece : this.footPiece,
          blockState.get(BedBlock.FACING), material, k, combinedOverlayIn, false);
    } else {
      this.renderPart(matrixStack, vertexConsumerProvider, this.headPiece, Direction.SOUTH,
          material, combinedLightIn, combinedOverlayIn, false);
      this.renderPart(matrixStack, vertexConsumerProvider, this.footPiece, Direction.SOUTH,
          material, combinedLightIn, combinedOverlayIn, true);
    }
  }

  private void renderPart(MatrixStack matrix, VertexConsumerProvider vertexConsumers,
                          ModelPart modelPart, Direction direction, SpriteIdentifier sprite,
                          int light, int overlay, boolean isFoot) {
    matrix.push();
    matrix.translate(0.0D, 0.1875D, isFoot ? -1.0D : 0.0D);
    matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
    matrix.translate(0.5D, 0.5D, 0.5D);
    matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F + direction.asRotation()));
    matrix.translate(-0.5D, -0.5D, -0.5D);
    VertexConsumer vertexConsumer =
        sprite.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
    modelPart.render(matrix, vertexConsumer, light, overlay);
    matrix.pop();
  }
}
