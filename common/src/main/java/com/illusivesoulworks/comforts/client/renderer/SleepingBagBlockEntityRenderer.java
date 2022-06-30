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

import com.illusivesoulworks.comforts.common.block.entity.SleepingBagBlockEntity;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class SleepingBagBlockEntityRenderer extends BaseComfortsBlockEntityRenderer<SleepingBagBlockEntity> {

  public SleepingBagBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    super(ctx, "sleeping_bag", BaseComfortsBlockEntityRenderer.SLEEPING_BAG_HEAD,
        BaseComfortsBlockEntityRenderer.SLEEPING_BAG_FOOT);
  }

  public static LayerDefinition createHeadLayer() {
    MeshDefinition var0 = new MeshDefinition();
    PartDefinition var1 = var0.getRoot();
    var1.addOrReplaceChild("main", CubeListBuilder
        .create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 3.0F), PartPose.ZERO);
    return LayerDefinition.create(var0, 64, 64);
  }

  public static LayerDefinition createFootLayer() {
    MeshDefinition var0 = new MeshDefinition();
    PartDefinition var1 = var0.getRoot();
    var1.addOrReplaceChild("main", CubeListBuilder
        .create().texOffs(0, 19).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 3.0F), PartPose.ZERO);
    return LayerDefinition.create(var0, 64, 64);
  }
}
