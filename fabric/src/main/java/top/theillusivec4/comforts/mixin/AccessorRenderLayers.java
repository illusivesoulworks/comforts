package top.theillusivec4.comforts.mixin;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderLayers.class)
public interface AccessorRenderLayers {

  @Accessor(value = "BLOCKS")
  static Map<Block, RenderLayer> getBlocks() {
    throw new AssertionError();
  }
}
