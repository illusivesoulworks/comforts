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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import top.theillusivec4.comforts.common.tileentity.HammockTileEntity;

public class HammockTileEntityRenderer extends ComfortsBaseTileEntityRenderer<HammockTileEntity> {

  protected ModelRenderer headBoard;
  protected ModelRenderer footBoard;

  public HammockTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
    super(dispatcher, "hammock");
    this.headPiece = new ModelRenderer(64, 64, 0, 0);
    this.headPiece.addBox(1.0F, 1.0F, 0.0F, 14, 15, 1, 0.0F);
    this.headBoard = new ModelRenderer(64, 64, 30, 0);
    this.headBoard.addBox(0.0F, 0.0F, 0.0F, 16, 1, 1, 0.0F);
    this.footPiece = new ModelRenderer(64, 64, 0, 16);
    this.footPiece.addBox(1.0F, 0.0F, 0.0F, 14, 15, 1, 0.0F);
    this.footBoard = new ModelRenderer(64, 64, 30, 0);
    this.footBoard.addBox(0.0F, 15.0F, 0.0F, 16, 1, 1, 0.0F);
  }

  @Override
  protected void renderPiece(MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean isHead,
      Direction direction, RenderMaterial material, int light, int overlay, boolean p_228847_8_) {
    this.headPiece.showModel = isHead;
    this.headBoard.showModel = isHead;
    this.footPiece.showModel = !isHead;
    this.footBoard.showModel = !isHead;
    matrixStack.push();
    matrixStack.translate(0.0D, 0.0625D, p_228847_8_ ? -1.0D : 0.0D);
    matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
    matrixStack.translate(0.5D, 0.5D, 0.5D);
    matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F + direction.getHorizontalAngle()));
    matrixStack.translate(-0.5D, -0.5D, -0.5D);
    final IVertexBuilder ivertexbuilder = material.getBuffer(buffer, RenderType::getEntitySolid);
    this.headPiece.render(matrixStack, ivertexbuilder, light, overlay);
    this.headBoard.render(matrixStack, ivertexbuilder, light, overlay);
    this.footPiece.render(matrixStack, ivertexbuilder, light, overlay);
    this.footBoard.render(matrixStack, ivertexbuilder, light, overlay);
    matrixStack.pop();
  }
}
