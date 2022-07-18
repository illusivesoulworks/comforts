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

package com.illusivesoulworks.comforts.client;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ComfortsClientEventsListener {

  @SubscribeEvent
  public void onTick(final TickEvent.PlayerTickEvent evt) {

    if (evt.phase == TickEvent.Phase.START && evt.side == LogicalSide.CLIENT) {
      ComfortsClientEvents.onTick(evt.player);
    }
  }

  @SubscribeEvent
  public void onPlayerRenderPre(final RenderPlayerEvent.Pre evt) {
    ComfortsClientEvents.onPlayerRenderPre(evt.getEntity(), evt.getPoseStack());
  }

  @SubscribeEvent
  public void onPlayerRenderPost(final RenderPlayerEvent.Post evt) {
    ComfortsClientEvents.onPlayerRenderPost(evt.getEntity(), evt.getPoseStack());
  }
}
