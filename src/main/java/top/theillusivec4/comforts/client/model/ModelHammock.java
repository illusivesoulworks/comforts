package top.theillusivec4.comforts.client.model;

import net.minecraft.client.renderer.entity.model.ModelRenderer;

public class ModelHammock extends ModelComfortsBase {

    private final ModelRenderer headPiece;
    private final ModelRenderer footPiece;
    private final ModelRenderer headBoard;
    private final ModelRenderer footBoard;

    public ModelHammock() {
        super();
        this.headPiece = new ModelRenderer(this, 0, 0);
        this.headPiece.addBox(1.0F, 1.0F, 0.0F, 14, 15, 1, 0.0F);
        this.headBoard = new ModelRenderer(this, 30, 0);
        this.headBoard.addBox(0.0F, 0.0F, 0.0F, 16, 1, 1, 0.0F);
        this.footPiece = new ModelRenderer(this, 0, 16);
        this.footPiece.addBox(1.0F, 0.0F, 0.0F, 14, 15, 1, 0.0F);
        this.footBoard = new ModelRenderer(this, 30, 0);
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
