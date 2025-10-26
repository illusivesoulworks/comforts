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
import com.mojang.math.Axis;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.state.BedRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;

public abstract class BaseComfortsBlockEntityRenderer<T extends BaseComfortsBlockEntity> implements
    BlockEntityRenderer<T, BedRenderState> {

  public static final ModelLayerLocation SLEEPING_BAG_HEAD = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "sleeping_bag_head"), "main");
  public static final ModelLayerLocation SLEEPING_BAG_FOOT = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "sleeping_bag_foot"), "main");
  public static final ModelLayerLocation HAMMOCK_HEAD = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "hammock_head"), "main");
  public static final ModelLayerLocation HAMMOCK_FOOT = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "hammock_foot"), "main");

  private final String type;

  protected final MaterialSet materials;
  protected final Model.Simple headModel;
  protected final Model.Simple footModel;

  public BaseComfortsBlockEntityRenderer(BlockEntityRendererProvider.Context context,
                                         String type,
                                         ModelLayerLocation headModel,
                                         ModelLayerLocation footModel) {
    this(type, context.materials(), context.entityModelSet(), headModel, footModel);
  }

  public BaseComfortsBlockEntityRenderer(String type, MaterialSet materials,
                                         EntityModelSet modelSet,
                                         ModelLayerLocation headModel,
                                         ModelLayerLocation footModel) {
    this.headModel = new Model.Simple(modelSet.bakeLayer(headModel), RenderType::entitySolid);
    this.footModel = new Model.Simple(modelSet.bakeLayer(footModel), RenderType::entitySolid);
    this.materials = materials;
    this.type = type;
  }

  @Nonnull
  @Override
  public BedRenderState createRenderState() {
    return new BedRenderState();
  }

  public void extractRenderState(
      @Nonnull T comfortsBlockEntity, @Nonnull BedRenderState bedRenderState,
      float partialTicks, @Nonnull Vec3 cameraPosition,
      @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress
  ) {
    BlockEntityRenderer.super.extractRenderState(comfortsBlockEntity, bedRenderState,
                                                 partialTicks, cameraPosition, breakProgress);
    bedRenderState.color = comfortsBlockEntity.getColor();
    bedRenderState.facing = comfortsBlockEntity.getBlockState().getValue(BedBlock.FACING);
    bedRenderState.isHead =
        comfortsBlockEntity.getBlockState().getValue(BedBlock.PART) == BedPart.HEAD;

    if (comfortsBlockEntity.getLevel() != null) {
      DoubleBlockCombiner.NeighborCombineResult<? extends BedBlockEntity> neighborcombineresult =
          DoubleBlockCombiner.combineWithNeigbour(
              BlockEntityType.BED,
              BedBlock::getBlockType,
              BedBlock::getConnectedDirection,
              ChestBlock.FACING,
              comfortsBlockEntity.getBlockState(),
              comfortsBlockEntity.getLevel(),
              comfortsBlockEntity.getBlockPos(),
              (p_112202_, p_112203_) -> false
          );
      bedRenderState.lightCoords =
          neighborcombineresult.apply(new BrightnessCombiner<>()).get(bedRenderState.lightCoords);
    }
  }

  @Override
  public void submit(@Nonnull BedRenderState bedRenderState, @Nonnull PoseStack poseStack,
                     @Nonnull SubmitNodeCollector nodeCollector,
                     @Nonnull CameraRenderState cameraRenderState) {
    final Material material =
        new Material(ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png"),
                     ResourceLocation.fromNamespaceAndPath(ComfortsConstants.MOD_ID,
                                                           "entity/" + this.type + "/"
                                                               + bedRenderState.color.getName()));
    poseStack.pushPose();
    poseStack.translate(0.0D, 0.1875D, 0.0D);
    poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
    poseStack.translate(0.5D, 0.5D, 0.5D);
    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F + bedRenderState.facing.toYRot()));
    poseStack.translate(-0.5D, -0.5D, -0.5D);
    nodeCollector.submitModel(
        bedRenderState.isHead ? this.headModel : this.footModel,
        Unit.INSTANCE,
        poseStack,
        material.renderType(RenderType::entitySolid),
        bedRenderState.lightCoords,
        OverlayTexture.NO_OVERLAY,
        -1,
        this.materials.get(material),
        0,
        bedRenderState.breakProgress
    );
    poseStack.popPose();
  }
}
