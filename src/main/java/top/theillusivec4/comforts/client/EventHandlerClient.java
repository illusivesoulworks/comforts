/*
 * Copyright (C) 2017-2019  C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;

public class EventHandlerClient {

  @SubscribeEvent
  public void onPlayerRenderPre(RenderPlayerEvent.Pre evt) {
    final PlayerEntity player = evt.getPlayer();

    if (player instanceof RemoteClientPlayerEntity && player.getPose() == Pose.SLEEPING) {
      player.getBedPosition().ifPresent(bedPos -> {
        Block bed = player.world.getBlockState(bedPos).getBlock();
        if (bed instanceof SleepingBagBlock) {
          GlStateManager.translatef(0.0f, -0.375F, 0.0f);
        } else if (bed instanceof HammockBlock) {
          GlStateManager.translatef(0.0f, -0.5F, 0.0f);
        }
      });
    }
  }

  @SubscribeEvent
  public void onPlayerRenderPost(RenderPlayerEvent.Post evt) {
    final PlayerEntity player = evt.getPlayer();

    if (player instanceof RemoteClientPlayerEntity && player.getPose() == Pose.SLEEPING) {
      player.getBedPosition().ifPresent(bedPos -> {
        Block bed = player.world.getBlockState(bedPos).getBlock();
        if (bed instanceof SleepingBagBlock) {
          GlStateManager.translatef(0.0f, 0.375F, 0.0f);
        } else if (bed instanceof HammockBlock) {
          GlStateManager.translatef(0.0f, 0.5F, 0.0f);
        }
      });
    }
  }
}
