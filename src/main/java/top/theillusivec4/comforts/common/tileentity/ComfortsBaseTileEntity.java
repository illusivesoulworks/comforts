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

package top.theillusivec4.comforts.common.tileentity;

import net.minecraft.block.BedBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ComfortsBaseTileEntity extends TileEntity {

  private DyeColor color;

  public ComfortsBaseTileEntity(TileEntityType<?> tileEntityType) {
    super(tileEntityType);
  }

  public ComfortsBaseTileEntity(TileEntityType<?> tileEntityType, DyeColor colorIn) {
    this(tileEntityType);
    this.setColor(colorIn);
  }

  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(this.pos, 11, this.getUpdateTag());
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    this.read(pkt.getNbtCompound());
  }

  @OnlyIn(Dist.CLIENT)
  public DyeColor getColor() {

    if (this.color == null) {
      this.color = ((BedBlock) this.getBlockState().getBlock()).getColor();
    }
    return this.color;
  }

  public void setColor(DyeColor color) {
    this.color = color;
  }
}
