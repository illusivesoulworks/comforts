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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public class ComfortsForgeNetwork {

  private static final int PTC_VERSION = 1;

  public static SimpleChannel INSTANCE;

  public static void setup() {
    INSTANCE = ChannelBuilder.named(
            Identifier.fromNamespaceAndPath(ComfortsConstants.MOD_ID, "main"))
        .networkProtocolVersion(PTC_VERSION)
        .clientAcceptedVersions(Channel.VersionTest.exact(PTC_VERSION))
        .serverAcceptedVersions(Channel.VersionTest.exact(PTC_VERSION)).simpleChannel();

    registerS2C(SPacketAutoSleep.class, SPacketAutoSleep.STREAM_CODEC::encode,
        SPacketAutoSleep.STREAM_CODEC::decode, ClientPacketHandler::handle);
    registerS2C(SPacketPlaceBag.class, SPacketPlaceBag.STREAM_CODEC::encode,
        SPacketPlaceBag.STREAM_CODEC::decode, ClientPacketHandler::handle);
  }

  public static <M> void registerS2C(Class<M> messageType, BiConsumer<FriendlyByteBuf, M> encoder,
                                     Function<FriendlyByteBuf, M> decoder,
                                     Consumer<M> messageConsumer) {
    INSTANCE.messageBuilder(messageType)
        .decoder(decoder)
        .encoder(((m, friendlyByteBuf) -> encoder.accept(friendlyByteBuf, m)))
        .consumerNetworkThread((m, context) -> {
          context.enqueueWork(() -> messageConsumer.accept(m));
          context.setPacketHandled(true);
        }).add();
  }
}
