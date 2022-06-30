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

import com.illusivesoulworks.comforts.common.block.entity.HammockBlockEntity;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class HammockBlockEntityRenderer extends BaseComfortsBlockEntityRenderer<HammockBlockEntity> {

  private static final String BOARD = "board";

  public HammockBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    super(ctx, "hammock", BaseComfortsBlockEntityRenderer.HAMMOCK_HEAD,
        BaseComfortsBlockEntityRenderer.HAMMOCK_FOOT);
  }

  public static LayerDefinition createHeadLayer() {
    MeshDefinition var0 = new MeshDefinition();
    PartDefinition var1 = var0.getRoot();
    var1.addOrReplaceChild("main", CubeListBuilder
        .create().texOffs(0, 0).addBox(1.0F, 1.0F, 2.0F, 14.0F, 15.0F, 1.0F), PartPose.ZERO);
    var1.addOrReplaceChild(BOARD, CubeListBuilder
        .create().texOffs(30, 0).addBox(0.0F, 0.0F, 2.0F, 16.0F, 1.0F, 1.0F), PartPose.ZERO);
    return LayerDefinition.create(var0, 64, 64);
  }

  public static LayerDefinition createFootLayer() {
    MeshDefinition var0 = new MeshDefinition();
    PartDefinition var1 = var0.getRoot();
    var1.addOrReplaceChild("main", CubeListBuilder
        .create().texOffs(0, 16).addBox(1.0F, 0.0F, 2.0F, 14.0F, 15.0F, 1.0F), PartPose.ZERO);
    var1.addOrReplaceChild(BOARD, CubeListBuilder
        .create().texOffs(30, 0).addBox(0.0F, 15.0F, 2.0F, 16.0F, 1.0F, 1.0F), PartPose.ZERO);
    return LayerDefinition.create(var0, 64, 64);
  }
}
