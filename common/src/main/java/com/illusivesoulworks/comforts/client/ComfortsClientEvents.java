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

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.common.block.BaseComfortsBlock;
import com.illusivesoulworks.comforts.common.block.HammockBlock;
import com.illusivesoulworks.comforts.common.block.SleepingBagBlock;
import com.illusivesoulworks.comforts.mixin.AccessorPlayer;
import com.illusivesoulworks.comforts.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ComfortsClientEvents {

  public static void keyPressed(Player player, Screen screen, KeyEvent keyEvent) {

    if (screen instanceof InBedChatScreen) {
      player.getSleepingPos().ifPresent(bedPos -> {
        Block block = player.level().getBlockState(bedPos).getBlock();

        if (block instanceof BaseComfortsBlock comfortsBlock) {
          ComfortsConstants.TimeUse timeUse = comfortsBlock.getComfortsTimeUse();

          if (timeUse == ComfortsConstants.TimeUse.USABLE_DECORATIVE) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.options.keyTogglePerspective.matches(keyEvent)) {
              CameraType cameratype = mc.options.getCameraType();
              mc.options.setCameraType(mc.options.getCameraType().cycle());

              if (cameratype.isFirstPerson() != mc.options.getCameraType().isFirstPerson()) {
                mc.gameRenderer.checkEntityPostEffect(mc.options.getCameraType().isFirstPerson() ? mc.getCameraEntity() : null);
              }
              mc.levelRenderer.needsUpdate();
            }
          }
        }
      });
    }
  }

  public static void onPlayerRenderPre(LivingEntityRenderState renderState, PoseStack poseStack) {

    if (!(renderState instanceof AvatarRenderState playerState)) {
      return;
    }
    Minecraft mc = Minecraft.getInstance();
    ClientLevel level = mc.level;

    if (level != null && playerState.pose == Pose.SLEEPING) {
      Entity entity = level.getEntity(playerState.id);

      if (entity instanceof RemotePlayer player) {
        player.getSleepingPos().ifPresent(bedPos -> {
          final Block bed = player.level().getBlockState(bedPos).getBlock();

          if (bed instanceof SleepingBagBlock) {
            poseStack.translate(0.0f, -0.375F, 0.0f);
          } else if (bed instanceof HammockBlock) {
            poseStack.translate(0.0f, -0.5F, 0.0f);
          }
        });
      } else if (entity instanceof LocalPlayer player) {
        player.getSleepingPos().ifPresent(bedPos -> {
          final Block bed = player.level().getBlockState(bedPos).getBlock();

          if (bed instanceof SleepingBagBlock) {
            player.attackAnim = 0.0f;
            player.oAttackAnim = 0.0f;
          }
        });
      }
    }
  }

  public static void onPlayerRenderPost(LivingEntityRenderState renderState, PoseStack poseStack) {

    if (!(renderState instanceof AvatarRenderState playerState)) {
      return;
    }
    Minecraft mc = Minecraft.getInstance();
    ClientLevel level = mc.level;

    if (level != null && level.getEntity(playerState.id) instanceof RemotePlayer player
        && player.getPose() == Pose.SLEEPING) {
      player.getSleepingPos().ifPresent(bedPos -> {
        final Block bed = player.level().getBlockState(bedPos).getBlock();

        if (bed instanceof SleepingBagBlock) {
          poseStack.translate(0.0f, 0.375F, 0.0f);
        } else if (bed instanceof HammockBlock) {
          poseStack.translate(0.0f, 0.5F, 0.0f);
        }
      });
    }
  }

  public static void onTick(Player player) {

    if (!player.isSleeping()) {
      Services.SLEEP_EVENTS.getSleepData(player).ifPresent(data -> {
        BlockPos pos = data.getAutoSleepPos();

        if (pos != null) {
          final Level level = player.level();
          final BlockState state = level.getBlockState(pos);

          if (level.isLoaded(pos)) {
            boolean flag = state.getBlock() instanceof SleepingBagBlock;

            if (!flag) {
              pos = pos.below();
              flag = level.getBlockState(pos).getBlock() instanceof SleepingBagBlock;
            }

            if (flag) {
              BlockHitResult hit = new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()),
                                                      Direction.UP, pos, false);
              MultiPlayerGameMode playerController = Minecraft.getInstance().gameMode;

              if (playerController != null) {
                playerController.useItemOn((LocalPlayer) player, InteractionHand.MAIN_HAND, hit);
              }
            }
          }
          data.setAutoSleepPos(null);
        }
      });
    }
  }
}
