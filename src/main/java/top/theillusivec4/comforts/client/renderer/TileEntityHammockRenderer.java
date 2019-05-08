package top.theillusivec4.comforts.client.renderer;

import top.theillusivec4.comforts.client.model.ModelHammock;
import top.theillusivec4.comforts.common.tileentity.TileEntityHammock;

public class TileEntityHammockRenderer extends TileEntityComfortsRendererBase<TileEntityHammock> {

    public TileEntityHammockRenderer() {
        super("hammock", new ModelHammock(), 0.0625F);
    }
}
