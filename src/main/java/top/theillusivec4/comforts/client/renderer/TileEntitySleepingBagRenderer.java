package top.theillusivec4.comforts.client.renderer;

import top.theillusivec4.comforts.client.model.ModelSleepingBag;
import top.theillusivec4.comforts.common.tileentity.TileEntitySleepingBag;

public class TileEntitySleepingBagRenderer extends TileEntityComfortsRendererBase<TileEntitySleepingBag> {

    public TileEntitySleepingBagRenderer() {
        super("sleeping_bag",new ModelSleepingBag(), 0.1875F);
    }
}
