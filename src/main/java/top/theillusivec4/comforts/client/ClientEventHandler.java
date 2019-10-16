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
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;

public class ClientEventHandler {

  @SubscribeEvent
  public void onPostPlayerTick(TickEvent.PlayerTickEvent evt) {

    if (evt.phase == Phase.START && evt.side == LogicalSide.CLIENT) {
      PlayerEntity player = evt.player;

      if (!player.isSleeping()) {
        CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {
          BlockPos pos = sleepdata.getAutoSleepPos();

          if (pos != null) {
            World world = player.world;
            BlockState state = world.getBlockState(pos);

            if (world.isAreaLoaded(pos, 1) && state.getBlock() instanceof SleepingBagBlock) {
              BlockRayTraceResult hit = new BlockRayTraceResult(new Vec3d(0, 0, 0),
                  player.getHorizontalFacing(), pos, false);
              Minecraft.getInstance().playerController
                  .func_217292_a((ClientPlayerEntity) player, (ClientWorld) player.world,
                      Hand.MAIN_HAND, hit);
            }
            sleepdata.setAutoSleepPos(null);
          }
        });
      }
    }
  }

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
