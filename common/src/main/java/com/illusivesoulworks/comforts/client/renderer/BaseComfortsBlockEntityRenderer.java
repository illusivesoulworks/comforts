/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.comforts.client.renderer;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.block.entity.BaseComfortsBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import javax.annotation.Nonnull;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public abstract class BaseComfortsBlockEntityRenderer<T extends BaseComfortsBlockEntity> implements
    BlockEntityRenderer<T> {

  public static final ModelLayerLocation SLEEPING_BAG_HEAD =
      new ModelLayerLocation(new ResourceLocation(ComfortsConstants.MOD_ID, "sleeping_bag_head"),
          "main");
  public static final ModelLayerLocation SLEEPING_BAG_FOOT =
      new ModelLayerLocation(new ResourceLocation(ComfortsConstants.MOD_ID, "sleeping_bag_foot"),
          "main");
  public static final ModelLayerLocation HAMMOCK_HEAD =
      new ModelLayerLocation(new ResourceLocation(ComfortsConstants.MOD_ID, "hammock_head"),
          "main");
  public static final ModelLayerLocation HAMMOCK_FOOT =
      new ModelLayerLocation(new ResourceLocation(ComfortsConstants.MOD_ID, "hammock_foot"),
          "main");

  private final String type;

  protected ModelPart headPiece;
  protected ModelPart footPiece;

  public BaseComfortsBlockEntityRenderer(BlockEntityRendererProvider.Context ctx, String type,
                                         ModelLayerLocation headModel,
                                         ModelLayerLocation footModel) {
    this.headPiece = ctx.bakeLayer(headModel);
    this.footPiece = ctx.bakeLayer(footModel);
    this.type = type;
  }

  @Override
  public void render(BaseComfortsBlockEntity blockEntity, float partialTicks,
                     @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer,
                     int combinedLightIn, int combinedOverlayIn) {
    final Material material = new Material(InventoryMenu.BLOCK_ATLAS,
        new ResourceLocation(ComfortsConstants.MOD_ID,
            "entity/" + type + "/" + blockEntity.getColor().getName()));
    final Level level = blockEntity.getLevel();

    if (level != null) {
      final BlockState blockstate = blockEntity.getBlockState();
      DoubleBlockCombiner.NeighborCombineResult<? extends BedBlockEntity> icallbackwrapper =
          DoubleBlockCombiner
              .combineWithNeigbour(BlockEntityType.BED, BedBlock::getBlockType,
                  BedBlock::getConnectedDirection,
                  ChestBlock.FACING, blockstate, level, blockEntity.getBlockPos(),
                  (p_228846_0_, p_228846_1_) -> false);
      final int i = icallbackwrapper.apply(new BrightnessCombiner<>()).get(combinedLightIn);
      this.renderPiece(matrixStack, buffer, blockstate.getValue(BedBlock.PART) == BedPart.HEAD,
          blockstate.getValue(BedBlock.FACING), material, i, combinedOverlayIn, false);
    } else {
      this.renderPiece(matrixStack, buffer, true, Direction.SOUTH, material, combinedLightIn,
          combinedOverlayIn, false);
      this.renderPiece(matrixStack, buffer, false, Direction.SOUTH, material, combinedLightIn,
          combinedOverlayIn, true);
    }

  }

  protected void renderPiece(PoseStack matrixStack, MultiBufferSource buffer, boolean isHead,
                             Direction direction, Material material, int light, int overlay,
                             boolean p_228847_8_) {
    this.headPiece.visible = isHead;
    this.footPiece.visible = !isHead;
    matrixStack.pushPose();
    matrixStack.translate(0.0D, 0.1875D, p_228847_8_ ? -1.0D : 0.0D);
    matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
    matrixStack.translate(0.5D, 0.5D, 0.5D);
    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F + direction.toYRot()));
    matrixStack.translate(-0.5D, -0.5D, -0.5D);
    VertexConsumer ivertexbuilder = material.buffer(buffer, RenderType::entitySolid);
    this.headPiece.render(matrixStack, ivertexbuilder, light, overlay);
    this.footPiece.render(matrixStack, ivertexbuilder, light, overlay);
    matrixStack.popPose();
  }
}
