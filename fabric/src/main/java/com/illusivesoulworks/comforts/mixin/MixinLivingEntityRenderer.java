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
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

  @Inject(at = @At("HEAD"), method = "render*")
  private void comforts$sleepingTranslate(S livingEntityRenderState, PoseStack poseStack,
                                          MultiBufferSource multiBufferSource, int packedLight,
                                          CallbackInfo ci) {
    ComfortsClientEvents.onPlayerRenderPre(livingEntityRenderState, poseStack);
  }

  @Inject(at = @At("TAIL"), method = "render*")
  private void comforts$resetSleepingTranslate(S livingEntityRenderState, PoseStack poseStack,
                                               MultiBufferSource multiBufferSource, int packedLight,
                                               CallbackInfo ci) {
    ComfortsClientEvents.onPlayerRenderPost(livingEntityRenderState, poseStack);
  }
}
