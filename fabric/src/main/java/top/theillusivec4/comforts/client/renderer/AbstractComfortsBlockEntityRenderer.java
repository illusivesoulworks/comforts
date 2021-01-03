package top.theillusivec4.comforts.client.renderer;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.ComfortsMod;
import top.theillusivec4.comforts.common.block.entity.AbstractComfortsBlockEntity;

public class AbstractComfortsBlockEntityRenderer<T extends AbstractComfortsBlockEntity>
    extends BlockEntityRenderer<T> {

  private final String type;

  protected ModelPart headPiece;
  protected ModelPart footPiece;

  public AbstractComfortsBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher,
                                             final String type) {
    super(dispatcher);
    this.headPiece = new ModelPart(0, 0, 0, 0);
    this.footPiece = new ModelPart(0, 0, 0, 0);
    this.type = type;
  }

  @Override
  public void render(AbstractComfortsBlockEntity blockEntity, float partialTicks,
                     MatrixStack matrixStack, VertexConsumerProvider bufferIn, int combinedLightIn,
                     int combinedOverlayIn) {
    final SpriteIdentifier material = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
        new Identifier(ComfortsMod.MOD_ID,
            "entity/" + type + "/" + blockEntity.getColor().getName()));
    final World world = blockEntity.getWorld();

    if (world != null) {
      final BlockState blockstate = blockEntity.getCachedState();
      DoubleBlockProperties.PropertySource<? extends BedBlockEntity> propertySource =
          DoubleBlockProperties.toPropertySource(BlockEntityType.BED, BedBlock::getBedPart,
              BedBlock::getOppositePartDirection, ChestBlock.FACING, blockstate, world,
              blockEntity.getPos(), (worldAccess, blockPos) -> false);
      int i = propertySource.apply(new LightmapCoordinatesRetriever<>()).get(combinedLightIn);
      this.renderPiece(matrixStack, bufferIn, blockstate.get(BedBlock.PART) == BedPart.HEAD,
          blockstate.get(HorizontalFacingBlock.FACING), material, i, combinedOverlayIn, false);
    } else {
      this.renderPiece(matrixStack, bufferIn, true, Direction.SOUTH, material, combinedLightIn,
          combinedOverlayIn, false);
      this.renderPiece(matrixStack, bufferIn, false, Direction.SOUTH, material, combinedLightIn,
          combinedOverlayIn, true);
    }

  }

  protected void renderPiece(MatrixStack matrixStack, VertexConsumerProvider buffer, boolean isHead,
                             Direction direction, SpriteIdentifier material, int light, int overlay,
                             boolean p_228847_8_) {
    this.headPiece.visible = isHead;
    this.footPiece.visible = !isHead;
    matrixStack.push();
    matrixStack.translate(0.0D, 0.1875D, p_228847_8_ ? -1.0D : 0.0D);
    matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
    matrixStack.translate(0.5D, 0.5D, 0.5D);
    matrixStack
        .multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F + direction.asRotation()));
    matrixStack.translate(-0.5D, -0.5D, -0.5D);
    VertexConsumer ivertexbuilder = material.getVertexConsumer(buffer, RenderLayer::getEntitySolid);
    this.headPiece.render(matrixStack, ivertexbuilder, light, overlay);
    this.footPiece.render(matrixStack, ivertexbuilder, light, overlay);
    matrixStack.pop();
  }
}
