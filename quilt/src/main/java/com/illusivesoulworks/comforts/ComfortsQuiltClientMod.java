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
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.network.ComfortsPackets;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class ComfortsQuiltClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient(ModContainer modContainer) {
    EntityModelLayerRegistry.registerModelLayer(BaseComfortsBlockEntityRenderer.SLEEPING_BAG_HEAD,
        SleepingBagBlockEntityRenderer::createHeadLayer);
    EntityModelLayerRegistry.registerModelLayer(BaseComfortsBlockEntityRenderer.SLEEPING_BAG_FOOT,
        SleepingBagBlockEntityRenderer::createFootLayer);
    EntityModelLayerRegistry.registerModelLayer(BaseComfortsBlockEntityRenderer.HAMMOCK_HEAD,
        HammockBlockEntityRenderer::createHeadLayer);
    EntityModelLayerRegistry.registerModelLayer(BaseComfortsBlockEntityRenderer.HAMMOCK_FOOT,
        HammockBlockEntityRenderer::createFootLayer);
    BlockEntityRenderers.register(ComfortsRegistry.SLEEPING_BAG_BLOCK_ENTITY.get(),
        SleepingBagBlockEntityRenderer::new);
    BlockEntityRenderers.register(ComfortsRegistry.HAMMOCK_BLOCK_ENTITY.get(),
        HammockBlockEntityRenderer::new);
    BlockRenderLayerMap.put(RenderType.translucent(), ComfortsRegistry.ROPE_AND_NAIL_BLOCK.get());
    ClientTickEvents.END.register(client -> {

      if (client.player != null) {
        ComfortsClientEvents.onTick(client.player);
      }
    });
    ClientPlayNetworking.registerGlobalReceiver(ComfortsPackets.AUTO_SLEEP,
        (client, handler, buf, responseSender) -> {
          BlockPos pos = buf.readBlockPos();
          client.execute(() -> ComfortsPackets.handleAutoSleep(client.player, pos));
        });
    ClientPlayNetworking.registerGlobalReceiver(ComfortsPackets.PLACE_BAG,
        (client, handler, buf, responseSender) -> {
          InteractionHand hand = buf.readEnum(InteractionHand.class);
          Vec3 location = new Vec3(buf.readVector3f());
          Direction direction = buf.readEnum(Direction.class);
          BlockPos pos = buf.readBlockPos();
          boolean inside = buf.readBoolean();
          client.execute(() -> ComfortsPackets.handlePlaceBag(client.player, hand,
              new BlockHitResult(location, direction, pos, inside)));
        });
  }
}
