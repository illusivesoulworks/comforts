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

package com.illusivesoulworks.comforts.mixin;

import com.illusivesoulworks.comforts.client.ComfortsClientEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {

  @Inject(at = @At("HEAD"), method = "render")
  private void comforts$sleepingTranslate(AbstractClientPlayer entity, float entityYaw,
                                          float partialTicks, PoseStack matrixStack,
                                          MultiBufferSource buffer, int packedLight,
                                          CallbackInfo ci) {
    ComfortsClientEvents.onPlayerRenderPre(entity, matrixStack);
  }

  @Inject(at = @At("TAIL"), method = "render")
  private void comforts$resetSleepingTranslate(AbstractClientPlayer entity, float entityYaw,
                                               float partialTicks, PoseStack matrixStack,
                                               MultiBufferSource buffer, int packedLight,
                                               CallbackInfo ci) {
    ComfortsClientEvents.onPlayerRenderPost(entity, matrixStack);
  }
}
