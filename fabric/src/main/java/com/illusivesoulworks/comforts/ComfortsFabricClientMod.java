/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.comforts;

import com.illusivesoulworks.comforts.client.ComfortsClientEvents;
import com.illusivesoulworks.comforts.client.renderer.BaseComfortsBlockEntityRenderer;
import com.illusivesoulworks.comforts.client.renderer.HammockBlockEntityRenderer;
import com.illusivesoulworks.comforts.client.renderer.SleepingBagBlockEntityRenderer;
import com.illusivesoulworks.comforts.common.ComfortsFabricNetwork;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.block.entity.SleepingBagBlockEntity;
import com.illusivesoulworks.comforts.common.network.ComfortsPackets;
import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import java.util.EnumMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ComfortsFabricClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    EntityModelLayerRegistry.registerModelLayer(BaseComfortsBlockEntityRenderer.SLEEPING_BAG_HEAD,
        SleepingBagBlockEntityRenderer::createHeadLayer);
    EntityModelLayerRegistry.registerModelLayer(BaseComfortsBlockEntityRenderer.SLEEPING_BAG_FOOT,
        SleepingBagBlockEntityRenderer::createFootLayer);
    EntityModelLayerRegistry.registerModelLayer(BaseComfortsBlockEntityRenderer.HAMMOCK_HEAD,
        HammockBlockEntityRenderer::createHeadLayer);
    EntityModelLayerRegistry.registerModelLayer(BaseComfortsBlockEntityRenderer.HAMMOCK_FOOT,
        HammockBlockEntityRenderer::createFootLayer);
    BlockEntityRendererRegistry.register(ComfortsRegistry.SLEEPING_BAG_BLOCK_ENTITY.get(),
        SleepingBagBlockEntityRenderer::new);
    BlockEntityRendererRegistry.register(ComfortsRegistry.HAMMOCK_BLOCK_ENTITY.get(),
        HammockBlockEntityRenderer::new);
    ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS)
        .register((atlasTexture, registry) -> {
          for (final DyeColor color : DyeColor.values()) {
            registry.register(new ResourceLocation(ComfortsConstants.MOD_ID,
                "entity/hammock/" + color.getName()));
            registry.register(new ResourceLocation(ComfortsConstants.MOD_ID,
                "entity/sleeping_bag/" + color.getName()));
          }
        });
    BlockRenderLayerMap.INSTANCE.putBlock(ComfortsRegistry.ROPE_AND_NAIL_BLOCK.get(),
        RenderType.translucent());
    ClientTickEvents.END_CLIENT_TICK.register(client -> {

      if (client.player != null) {
        ComfortsClientEvents.onTick(client.player);
      }
    });
    ClientPlayNetworking.registerGlobalReceiver(ComfortsPackets.AUTO_SLEEP,
        (client, handler, buf, responseSender) -> {
          BlockPos pos = buf.readBlockPos();
          client.execute(() -> ComfortsPackets.handleAutoSleep(client.player, pos));
        });
  }
}
