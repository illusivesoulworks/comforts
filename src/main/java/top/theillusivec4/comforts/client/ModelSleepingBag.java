package top.theillusivec4.comforts.client;

import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;

public class ModelSleepingBag extends ModelBase {

    private final ModelRenderer headPiece;
    private final ModelRenderer footPiece;

    public ModelSleepingBag() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.headPiece = new ModelRenderer(this, 0, 0);
        this.headPiece.addBox(0.0F, 0.0F, 0.0F, 16, 16, 3, 0.0F);
        this.footPiece = new ModelRenderer(this, 0, 19);
        this.footPiece.addBox(0.0F, 0.0F, 0.0F, 16, 16, 3, 0.0F);
    }

    public void render() {
        this.headPiece.render(0.0625F);
        this.footPiece.render(0.0625F);
    }

    public void preparePiece(boolean isHead) {
        this.headPiece.showModel = isHead;
        this.footPiece.showModel = !isHead;
    }
}
