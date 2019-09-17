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

package top.theillusivec4.comforts.client.model;

import net.minecraft.client.renderer.entity.model.RendererModel;

public class HammockModel extends ComfortsBaseModel {

    private final RendererModel headPiece;
    private final RendererModel footPiece;
    private final RendererModel headBoard;
    private final RendererModel footBoard;

    public HammockModel() {
        super();
        this.headPiece = new RendererModel(this, 0, 0);
        this.headPiece.addBox(1.0F, 1.0F, 0.0F, 14, 15, 1, 0.0F);
        this.headBoard = new RendererModel(this, 30, 0);
        this.headBoard.addBox(0.0F, 0.0F, 0.0F, 16, 1, 1, 0.0F);
        this.footPiece = new RendererModel(this, 0, 16);
        this.footPiece.addBox(1.0F, 0.0F, 0.0F, 14, 15, 1, 0.0F);
        this.footBoard = new RendererModel(this, 30, 0);
        this.footBoard.addBox(0.0F, 15.0F, 0.0F, 16, 1, 1, 0.0F);
    }

    @Override
    public void render() {
        this.headPiece.render(0.0625F);
        this.headBoard.render(0.0625F);
        this.footPiece.render(0.0625F);
        this.footBoard.render(0.0625F);
    }

    @Override
    public void preparePiece(boolean isHead) {
        this.headPiece.showModel = isHead;
        this.headBoard.showModel = isHead;
        this.footPiece.showModel = !isHead;
        this.footBoard.showModel = !isHead;
    }
}
