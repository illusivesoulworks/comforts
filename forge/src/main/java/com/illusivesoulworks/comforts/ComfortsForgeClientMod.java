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
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ComfortsForgeClientMod {

  public static void init() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(ComfortsForgeClientMod::clientSetup);
    eventBus.addListener(ComfortsForgeClientMod::entityRenderers);
    eventBus.addListener(ComfortsForgeClientMod::layerDefinitions);
    eventBus.addListener(ComfortsForgeClientMod::textureStitch);
  }

  private static void clientSetup(final FMLClientSetupEvent evt) {
    MinecraftForge.EVENT_BUS.register(new ComfortsClientEventsListener());
    ItemBlockRenderTypes.setRenderLayer(ComfortsRegistry.ROPE_AND_NAIL_BLOCK.get(),
        RenderType.translucent());
  }

  private static void entityRenderers(final EntityRenderersEvent.RegisterRenderers evt) {
    evt.registerBlockEntityRenderer(ComfortsRegistry.SLEEPING_BAG_BLOCK_ENTITY.get(),
        SleepingBagBlockEntityRenderer::new);
    evt.registerBlockEntityRenderer(ComfortsRegistry.HAMMOCK_BLOCK_ENTITY.get(),
        HammockBlockEntityRenderer::new);
  }

  private static void layerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions evt) {
    evt.registerLayerDefinition(BaseComfortsBlockEntityRenderer.SLEEPING_BAG_HEAD,
        SleepingBagBlockEntityRenderer::createHeadLayer);
    evt.registerLayerDefinition(BaseComfortsBlockEntityRenderer.SLEEPING_BAG_FOOT,
        SleepingBagBlockEntityRenderer::createFootLayer);
    evt.registerLayerDefinition(BaseComfortsBlockEntityRenderer.HAMMOCK_HEAD,
        HammockBlockEntityRenderer::createHeadLayer);
    evt.registerLayerDefinition(BaseComfortsBlockEntityRenderer.HAMMOCK_FOOT,
        HammockBlockEntityRenderer::createFootLayer);
  }

  private static void textureStitch(TextureStitchEvent.Pre evt) {

    if (evt.getAtlas().location() == InventoryMenu.BLOCK_ATLAS) {

      for (final DyeColor color : DyeColor.values()) {
        evt.addSprite(
            new ResourceLocation(ComfortsConstants.MOD_ID, "entity/hammock/" + color.getName()));
        evt.addSprite(new ResourceLocation(ComfortsConstants.MOD_ID,
            "entity/sleeping_bag/" + color.getName()));
      }
    }
  }
}
