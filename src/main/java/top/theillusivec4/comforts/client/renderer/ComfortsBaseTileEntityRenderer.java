/*
 * Copyright (C) 2017-2019  C4
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
import javax.annotation.Nonnull;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityMerger.ICallbackWrapper;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.tileentity.ComfortsBaseTileEntity;

public abstract class ComfortsBaseTileEntityRenderer<T extends ComfortsBaseTileEntity> extends
    TileEntityRenderer<T> {

  protected ModelRenderer headPiece;
  protected ModelRenderer footPiece;

  public ComfortsBaseTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
    super(dispatcher);
    this.headPiece = new ModelRenderer(0, 0, 0, 0);
    this.footPiece = new ModelRenderer(0, 0, 0, 0);
  }

  @Override
  public void render(ComfortsBaseTileEntity tileEntityIn, float partialTicks,
      @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn,
      int combinedOverlayIn) {
    Material material = Atlases.BED_TEXTURES[tileEntityIn.getColor().getId()];
    World world = tileEntityIn.getWorld();

    if (world != null) {
      BlockState blockstate = tileEntityIn.getBlockState();
      ICallbackWrapper<? extends BedTileEntity> icallbackwrapper = TileEntityMerger
          .func_226924_a_(TileEntityType.BED, BedBlock::func_226863_i_, BedBlock::func_226862_h_,
              ChestBlock.FACING, blockstate, world, tileEntityIn.getPos(),
              (p_228846_0_, p_228846_1_) -> false);
      int i = icallbackwrapper.apply(new DualBrightnessCallback<>()).get(combinedLightIn);
      this.renderPiece(matrixStackIn, bufferIn, blockstate.get(BedBlock.PART) == BedPart.HEAD,
          blockstate.get(BedBlock.HORIZONTAL_FACING), material, i, combinedOverlayIn, false);
    } else {
      this.renderPiece(matrixStackIn, bufferIn, true, Direction.SOUTH, material, combinedLightIn,
          combinedOverlayIn, false);
      this.renderPiece(matrixStackIn, bufferIn, false, Direction.SOUTH, material, combinedLightIn,
          combinedOverlayIn, true);
    }

  }

  protected void renderPiece(MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean isHead,
      Direction direction, Material material, int light, int overlay, boolean p_228847_8_) {
    this.headPiece.showModel = isHead;
    this.footPiece.showModel = !isHead;
    matrixStack.push();
    matrixStack.translate(0.0D, 0.5625D, p_228847_8_ ? -1.0D : 0.0D);
    matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
    matrixStack.translate(0.5D, 0.5D, 0.5D);
    matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F + direction.getHorizontalAngle()));
    matrixStack.translate(-0.5D, -0.5D, -0.5D);
    IVertexBuilder ivertexbuilder = material.getBuffer(buffer, RenderType::entitySolid);
    this.headPiece.render(matrixStack, ivertexbuilder, light, overlay);
    this.footPiece.render(matrixStack, ivertexbuilder, light, overlay);
    matrixStack.pop();
  }
}
