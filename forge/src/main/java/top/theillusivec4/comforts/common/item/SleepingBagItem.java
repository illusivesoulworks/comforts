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

package top.theillusivec4.comforts.common.item;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import top.theillusivec4.comforts.common.ComfortsConfig;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;
import top.theillusivec4.comforts.common.network.ComfortsNetwork;
import top.theillusivec4.comforts.common.network.SPacketAutoSleep;

public class SleepingBagItem extends ComfortsBaseItem {

  public SleepingBagItem(Block block) {
    super(block);
  }

  @Nonnull
  @Override
  public InteractionResult useOn(UseOnContext context) {
    final InteractionResult result = super.useOn(context);
    final Player player = context.getPlayer();

    if (player instanceof ServerPlayer && result.consumesAction()
        && ComfortsConfig.SERVER.autoUse.get() && !player.isCrouching()) {
      final BlockPos pos = context.getClickedPos().above();
      CapabilitySleepData.getCapability(player)
          .ifPresent(sleepdata -> sleepdata.setAutoSleepPos(pos));
      ComfortsNetwork.INSTANCE
          .send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
              new SPacketAutoSleep(player.getId(), pos));
    }
    return result;
  }
}
