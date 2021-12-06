/*
 * Copyright (c) 2017-2020 C4
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

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
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
  public void onPostPlayerTick(final TickEvent.PlayerTickEvent evt) {

    if (evt.phase == Phase.START && evt.side == LogicalSide.CLIENT) {
      final Player player = evt.player;

      if (!player.isSleeping()) {
        CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {
          final BlockPos pos = sleepdata.getAutoSleepPos();

          if (pos != null) {
            final Level world = player.level;
            final BlockState state = world.getBlockState(pos);

            if (world.isAreaLoaded(pos, 1) && state.getBlock() instanceof SleepingBagBlock) {
              BlockHitResult hit = new BlockHitResult(new Vec3(0, 0, 0),
                  player.getDirection(), pos, false);
              MultiPlayerGameMode playerController = Minecraft.getInstance().gameMode;

              if (playerController != null) {
                playerController
                    .useItemOn((LocalPlayer) player, (ClientLevel) player.level,
                        InteractionHand.MAIN_HAND, hit);
              }
            }

            sleepdata.setAutoSleepPos(null);
          }
        });
      }
    }
  }

  @SubscribeEvent
  public void onPlayerRenderPre(final RenderPlayerEvent.Pre evt) {
    final Player player = evt.getPlayer();

    if (player instanceof RemotePlayer && player.getPose() == Pose.SLEEPING) {
      player.getSleepingPos().ifPresent(bedPos -> {
        PoseStack matrixStack = evt.getPoseStack();
        final Block bed = player.level.getBlockState(bedPos).getBlock();
        if (bed instanceof SleepingBagBlock) {
          matrixStack.translate(0.0f, -0.375F, 0.0f);
        } else if (bed instanceof HammockBlock) {
          matrixStack.translate(0.0f, -0.5F, 0.0f);
        }
      });
    }
  }

  @SubscribeEvent
  public void onPlayerRenderPost(final RenderPlayerEvent.Post evt) {
    final Player player = evt.getPlayer();

    if (player instanceof RemotePlayer && player.getPose() == Pose.SLEEPING) {
      player.getSleepingPos().ifPresent(bedPos -> {
        PoseStack matrixStack = evt.getPoseStack();
        final Block bed = player.level.getBlockState(bedPos).getBlock();
        if (bed instanceof SleepingBagBlock) {
          matrixStack.translate(0.0f, 0.375F, 0.0f);
        } else if (bed instanceof HammockBlock) {
          matrixStack.translate(0.0f, 0.5F, 0.0f);
        }
      });
    }
  }
}
