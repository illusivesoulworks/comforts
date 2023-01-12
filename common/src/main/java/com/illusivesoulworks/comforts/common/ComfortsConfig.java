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

package com.illusivesoulworks.comforts.common;

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.spectrelib.config.SpectreConfigSpec;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class ComfortsConfig {

  public static final SpectreConfigSpec SERVER_SPEC;
  public static final Server SERVER;
  private static final String CONFIG_PREFIX = "gui." + ComfortsConstants.MOD_ID + ".config.";

  static {
    final Pair<Server, SpectreConfigSpec> specPair = new SpectreConfigSpec.Builder()
        .configure(Server::new);
    SERVER_SPEC = specPair.getRight();
    SERVER = specPair.getLeft();
  }

  public static class Server {

    public final SpectreConfigSpec.BooleanValue autoUse;
    public final SpectreConfigSpec.BooleanValue wellRested;
    public final SpectreConfigSpec.DoubleValue sleepyFactor;
    public final SpectreConfigSpec.BooleanValue nightHammocks;
    public final SpectreConfigSpec.DoubleValue sleepingBagBreakage;
    public final SpectreConfigSpec.DoubleValue sleepingBagBreakageLuckMultiplier;
    public final SpectreConfigSpec.ConfigValue<List<String>> sleepingBagEffects;

    public Server(SpectreConfigSpec.Builder builder) {
      builder.push("server");

      autoUse = builder.comment("Set to true to automatically use sleeping bags when placed")
          .translation(CONFIG_PREFIX + "autoUse").define("autoUse", true);

      wellRested = builder
          .comment("Set to true to prevent sleeping depending on how long you previously slept")
          .translation(CONFIG_PREFIX + "wellRested").define("wellRested", false);

      sleepyFactor = builder.comment(
              "If well rested is true, this value is used to determine how long you need before being able to sleep again (larger numbers = can sleep sooner)")
          .translation(CONFIG_PREFIX + "sleepyFactor")
          .defineInRange("sleepyFactor", 2.0D, 1.0D, 20.0D);

      nightHammocks = builder.comment("Set to true to enable sleeping in hammocks at night")
          .translation(CONFIG_PREFIX + "nightHammocks").define("nightHammocks", false);

      sleepingBagBreakage = builder.comment("The chance that a sleeping bag will break upon usage")
          .translation(CONFIG_PREFIX + "sleepingBagBreakage")
          .defineInRange("sleepingBagBreakage", 0.0D, 0.0D, 1.0D);

      sleepingBagBreakageLuckMultiplier = builder.comment(
              "The value that will be multiplied by a player's luck then added/subtracted from the sleepingBagBreakage value")
          .translation(CONFIG_PREFIX + "sleepingBagBreakageLuckMultiplier")
          .defineInRange("sleepingBagBreakageLuckMultiplier", 0.0D, -1.0D, 1.0D);

      sleepingBagEffects = builder.comment(
              "List of debuffs to apply to players after using the sleeping bag\n"
                  + "Format: effect;duration(secs);power")
          .translation(CONFIG_PREFIX + "sleepingBagDebuffs")
          .define("sleepingBagDebuffs", new ArrayList<>());

      builder.pop();
    }
  }

  public static void reload() {
    ComfortsEvents.effectsInitialized = false;
  }
}
