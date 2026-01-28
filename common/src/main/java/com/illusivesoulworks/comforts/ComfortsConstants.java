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

package com.illusivesoulworks.comforts;

import java.util.logging.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComfortsConstants {

  public static final String MOD_ID = "comforts";
  public static final String MOD_NAME = "Comforts";
  public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

  public static final Player.BedSleepingProblem NOT_NOW = new Player.BedSleepingProblem(null);

  public enum Result {
    ALLOW,
    DEFAULT,
    DENY
  }

  public enum TimeUse {
    NONE(Component.translatable("item.comforts.no_sleep")),
    DAY(Component.translatable("item.comforts.hammock.no_sleep")),
    NIGHT(Component.translatable("block.minecraft.bed.no_sleep")),
    DAY_OR_NIGHT(Component.translatable("item.comforts.hammock.no_sleep.2"));

    private final Component message;

    TimeUse(Component message) {
      this.message = message;
    }

    public Component getMessage() {
      return this.message;
    }
  }
}