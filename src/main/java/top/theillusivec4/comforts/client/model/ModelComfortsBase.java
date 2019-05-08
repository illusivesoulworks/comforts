package top.theillusivec4.comforts.client.model;

import net.minecraft.client.renderer.entity.model.ModelBase;

public abstract class ModelComfortsBase extends ModelBase {

    public ModelComfortsBase() {
        this.textureWidth = 64;
        this.textureHeight = 64;
    }

    public abstract void render();

    public abstract void preparePiece(boolean isHead);
}
