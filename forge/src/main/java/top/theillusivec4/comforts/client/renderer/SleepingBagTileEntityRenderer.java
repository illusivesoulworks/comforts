/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.client.renderer;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import top.theillusivec4.comforts.common.tileentity.SleepingBagTileEntity;

public class SleepingBagTileEntityRenderer extends
    ComfortsBaseTileEntityRenderer<SleepingBagTileEntity> {

  public SleepingBagTileEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    super(ctx, "sleeping_bag", ComfortsBaseTileEntityRenderer.SLEEPING_BAG_HEAD,
        ComfortsBaseTileEntityRenderer.SLEEPING_BAG_FOOT);
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
