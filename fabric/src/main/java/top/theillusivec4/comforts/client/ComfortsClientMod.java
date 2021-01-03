package top.theillusivec4.comforts.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.DyeColor;
import top.theillusivec4.comforts.ComfortsMod;
import top.theillusivec4.comforts.client.renderer.HammockBlockEntityRenderer;
import top.theillusivec4.comforts.client.renderer.SleepingBagBlockEntityRenderer;
import top.theillusivec4.comforts.common.ComfortsRegistry;
import top.theillusivec4.comforts.mixin.AccessorRenderLayers;

public class ComfortsClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    BlockEntityRendererRegistry.INSTANCE
        .register(ComfortsRegistry.SLEEPING_BAG_BE, SleepingBagBlockEntityRenderer::new);
    BlockEntityRendererRegistry.INSTANCE
        .register(ComfortsRegistry.HAMMOCK_BE, HammockBlockEntityRenderer::new);
    AccessorRenderLayers.getBlocks().put(ComfortsRegistry.ROPE_AND_NAIL,
        RenderLayer.getTranslucent());
    ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
        .register((atlasTexture, registry) -> {
          for (final DyeColor color : DyeColor.values()) {
            registry.register(ComfortsMod.id("entity/hammock/" + color.getName()));
            registry.register(ComfortsMod.id("entity/sleeping_bag/" + color.getName()));
          }
        });
  }
}
