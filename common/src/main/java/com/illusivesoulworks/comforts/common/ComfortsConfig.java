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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    public final SpectreConfigSpec.BooleanValue restrictSleeping;
    public final SpectreConfigSpec.DoubleValue restMultiplier;
    public final SpectreConfigSpec.EnumValue<ComfortsTimeUse> hammockUse;
    public final SpectreConfigSpec.EnumValue<ComfortsTimeUse> sleepingBagUse;
    public final SpectreConfigSpec.IntValue sleepingBagBreakChance;
    public final SpectreConfigSpec.DoubleValue sleepingBagBreakChanceLuckMultiplier;
    public final SpectreConfigSpec.ConfigValue<List<? extends String>> sleepingBagEffects;

    public Server(SpectreConfigSpec.Builder builder) {

      autoUse = builder.comment(
              "If enabled, players automatically attempt to use sleeping bags when placed.")
          .translation(CONFIG_PREFIX + "autoUse").define("autoUse", true);

      restrictSleeping = builder
          .comment("If enabled, players cannot sleep again for a period of time after sleeping.")
          .translation(CONFIG_PREFIX + "restrictSleeping").define("restrictSleeping", false);

      restMultiplier = builder.comment(
              "If restrictSleeping is true, this value will determine the length of wait time (larger numbers sleep sooner).")
          .translation(CONFIG_PREFIX + "restMultiplier")
          .defineInRange("restMultiplier", 2.0D, 1.0D, 20.0D);

      hammockUse = builder.comment("The time of day that hammocks can be used.")
          .translation(CONFIG_PREFIX + "hammockUse").defineEnum("hammockUse", ComfortsTimeUse.DAY);

      sleepingBagUse = builder.comment("The time of day that sleeping bags can be used.")
          .translation(CONFIG_PREFIX + "sleepingBagUse")
          .defineEnum("sleepingBagUse", ComfortsTimeUse.NIGHT);

      sleepingBagBreakChance =
          builder.comment("The percentage chance that a sleeping bag will break upon use.")
              .translation(CONFIG_PREFIX + "sleepingBagBreakChance")
              .defineInRange("sleepingBagBreakChance", 0, 0, 100);

      sleepingBagBreakChanceLuckMultiplier = builder.comment(
              "The value that will be multiplied by a player's luck then added to sleepingBagBreakChance.")
          .translation(CONFIG_PREFIX + "sleepingBagBreakChanceLuckMultiplier")
          .defineInRange("sleepingBagBreakChanceLuckMultiplier", 0.0D, -1.0D, 1.0D);

      sleepingBagEffects = builder.comment(
              "The status effects to apply to players after using the sleeping bag.\n"
                  + "Format: effect;duration(secs);power")
          .translation(CONFIG_PREFIX + "sleepingBagEffects")
          .defineListAllowEmpty(List.of("sleepingBagEffects"), ArrayList::new,
              s -> s instanceof String str && ResourceLocation.isValidResourceLocation(str) &&
                  str.split(";").length == 3);
    }
  }

  public static void reload() {
    ComfortsEvents.effectsInitialized = false;
  }

  public enum ComfortsTimeUse {
    NONE(Component.translatable("block.comforts.no_sleep")),
    DAY(Component.translatable("block.comforts.hammock.no_sleep")),
    NIGHT(Component.translatable("block.minecraft.bed.no_sleep")),
    DAY_OR_NIGHT(Component.translatable("block.comforts.hammock.no_sleep.2"));

    private final Component message;

    ComfortsTimeUse(Component message) {
      this.message = message;
    }

    public Component getMessage() {
      return this.message;
    }
  }
}
