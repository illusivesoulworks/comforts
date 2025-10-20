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

import com.illusivesoulworks.comforts.client.ComfortsClientEventsListener;
import com.illusivesoulworks.comforts.client.renderer.BaseComfortsBlockEntityRenderer;
import com.illusivesoulworks.comforts.client.renderer.HammockBlockEntityRenderer;
import com.illusivesoulworks.comforts.client.renderer.SleepingBagBlockEntityRenderer;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ComfortsConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ComfortsForgeClientMod {

  @SubscribeEvent
  public static void clientSetup(final FMLClientSetupEvent evt) {
    MinecraftForge.EVENT_BUS.register(new ComfortsClientEventsListener());
  }

  @SubscribeEvent
  public static void entityRenderers(final EntityRenderersEvent.RegisterRenderers evt) {
    evt.registerBlockEntityRenderer(ComfortsRegistry.SLEEPING_BAG_BLOCK_ENTITY.get(),
                                    SleepingBagBlockEntityRenderer::new);
    evt.registerBlockEntityRenderer(ComfortsRegistry.HAMMOCK_BLOCK_ENTITY.get(),
                                    HammockBlockEntityRenderer::new);
  }

  @SubscribeEvent
  public static void layerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions evt) {
    evt.registerLayerDefinition(BaseComfortsBlockEntityRenderer.SLEEPING_BAG_HEAD,
                                SleepingBagBlockEntityRenderer::createHeadLayer);
    evt.registerLayerDefinition(BaseComfortsBlockEntityRenderer.SLEEPING_BAG_FOOT,
                                SleepingBagBlockEntityRenderer::createFootLayer);
    evt.registerLayerDefinition(BaseComfortsBlockEntityRenderer.HAMMOCK_HEAD,
                                HammockBlockEntityRenderer::createHeadLayer);
    evt.registerLayerDefinition(BaseComfortsBlockEntityRenderer.HAMMOCK_FOOT,
                                HammockBlockEntityRenderer::createFootLayer);
  }
}
