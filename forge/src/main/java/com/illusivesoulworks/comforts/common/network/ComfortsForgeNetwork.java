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

package com.illusivesoulworks.comforts.common.network;

import com.illusivesoulworks.comforts.ComfortsConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ComfortsForgeNetwork {

  private static final String PROTOCOL_VERSION = "1";

  public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
      new ResourceLocation(ComfortsConstants.MOD_ID, "main"), () -> PROTOCOL_VERSION,
      PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
  );

  public static void setup() {
    INSTANCE.registerMessage(0, SPacketAutoSleep.class, SPacketAutoSleep::encode,
        SPacketAutoSleep::decode, SPacketAutoSleep::handle);
    INSTANCE.registerMessage(1, SPacketPlaceBag.class, SPacketPlaceBag::encode,
        SPacketPlaceBag::decode, SPacketPlaceBag::handle);
  }
}
