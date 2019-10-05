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

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.comforts.Comforts;
import top.theillusivec4.comforts.client.model.ComfortsBaseModel;
import top.theillusivec4.comforts.common.tileentity.ComfortsBaseTileEntity;

public abstract class ComfortsBaseTileEntityRenderer<T extends ComfortsBaseTileEntity> extends
    TileEntityRenderer<T> {

  private final ResourceLocation[] textures;
  private final ComfortsBaseModel model;
  private final float height;

  public ComfortsBaseTileEntityRenderer(String textureName, ComfortsBaseModel model, float height) {
    this.textures = Arrays.stream(DyeColor.values())
        .sorted(Comparator.comparingInt(DyeColor::getId)).map(
            (color) -> new ResourceLocation(Comforts.MODID,
                "textures/entity/" + textureName + "/" + color.getTranslationKey() + ".png"))
        .toArray(ResourceLocation[]::new);
    this.model = model;
    this.height = height;
  }

  @Override
  public void render(T tileEntityIn, double x, double y, double z, float partialTicks,
      int destroyStage) {

    if (destroyStage >= 0) {
      this.bindTexture(DESTROY_STAGES[destroyStage]);
      GlStateManager.matrixMode(5890);
      GlStateManager.pushMatrix();
      GlStateManager.scalef(4.0F, 4.0F, 1.0F);
      GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
      GlStateManager.matrixMode(5888);
    } else {
      ResourceLocation resourcelocation = textures[tileEntityIn.getColor().getId()];

      if (resourcelocation != null) {
        this.bindTexture(resourcelocation);
      }
    }

    if (tileEntityIn.hasWorld()) {
      BlockState blockstate = tileEntityIn.getBlockState();
      this.renderPiece(blockstate.get(BedBlock.PART) == BedPart.HEAD, x, y, z,
          blockstate.get(BedBlock.HORIZONTAL_FACING));
    } else {
      this.renderPiece(true, x, y, z, Direction.SOUTH);
      this.renderPiece(false, x, y, z - 1.0D, Direction.SOUTH);
    }

    if (destroyStage >= 0) {
      GlStateManager.matrixMode(5890);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
    }

  }

  private void renderPiece(boolean isHead, double x, double y, double z, Direction direction) {
    this.model.preparePiece(isHead);
    GlStateManager.pushMatrix();
    GlStateManager.translatef((float) x, (float) y + height, (float) z);
    GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
    GlStateManager.translatef(0.5F, 0.5F, 0.5F);
    GlStateManager.rotatef(180.0F + direction.getHorizontalAngle(), 0.0F, 0.0F, 1.0F);
    GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
    GlStateManager.enableRescaleNormal();
    this.model.render();
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();
  }
}
