package top.theillusivec4.comforts.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import top.theillusivec4.comforts.client.renderer.HammockBlockEntityRenderer;
import top.theillusivec4.comforts.client.renderer.SleepingBagBlockEntityRenderer;
import top.theillusivec4.comforts.common.ComfortsMod;
import top.theillusivec4.comforts.common.ComfortsRegistry;
import top.theillusivec4.comforts.common.network.ComfortsNetwork;
import top.theillusivec4.comforts.mixin.AccessorRenderLayers;

public class ComfortsClientMod implements ClientModInitializer {

  public static final EntityModelLayer SLEEPING_BAG_HEAD =
      new EntityModelLayer(new Identifier(ComfortsMod.MOD_ID, "sleeping_bag_head"), "main");
  public static final EntityModelLayer SLEEPING_BAG_FOOT =
      new EntityModelLayer(new Identifier(ComfortsMod.MOD_ID, "sleeping_bag_foot"), "main");
  public static final EntityModelLayer HAMMOCK_HEAD =
      new EntityModelLayer(new Identifier(ComfortsMod.MOD_ID, "hammock_head"), "main");
  public static final EntityModelLayer HAMMOCK_FOOT =
      new EntityModelLayer(new Identifier(ComfortsMod.MOD_ID, "hammock_foot"), "main");

  @Override
  public void onInitializeClient() {
    EntityModelLayerRegistry.registerModelLayer(SLEEPING_BAG_HEAD,
        SleepingBagBlockEntityRenderer::getHeadTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(
        SLEEPING_BAG_FOOT,
        SleepingBagBlockEntityRenderer::getFootTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(
        HAMMOCK_HEAD,
        HammockBlockEntityRenderer::getHeadTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(
        HAMMOCK_FOOT,
        HammockBlockEntityRenderer::getFootTexturedModelData);
    BlockEntityRendererRegistry
        .register(ComfortsRegistry.SLEEPING_BAG_BE, SleepingBagBlockEntityRenderer::new);
    BlockEntityRendererRegistry.register(ComfortsRegistry.HAMMOCK_BE,
        HammockBlockEntityRenderer::new);
    AccessorRenderLayers.getBlocks()
        .put(ComfortsRegistry.ROPE_AND_NAIL, RenderLayer.getTranslucent());
    ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
        .register((atlasTexture, registry) -> {
          for (final DyeColor color : DyeColor.values()) {
            registry.register(ComfortsMod.id("entity/hammock/" + color.getName()));
            registry.register(ComfortsMod.id("entity/sleeping_bag/" + color.getName()));
          }
        });
    ClientPlayNetworking
        .registerGlobalReceiver(ComfortsNetwork.SYNC_AUTOSLEEP, ComfortsNetwork::readAutoSleep);
  }
}
