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
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.tuple.Pair;

public class ComfortsConfig {

  public static final SpectreConfigSpec SERVER_SPEC;
  public static final Server SERVER;
  public static final SpectreConfigSpec COMMON_SPEC;
  public static final Common COMMON;
  private static final String CONFIG_PREFIX = "gui." + ComfortsConstants.MOD_ID + ".config.";

  static {
    Pair<Server, SpectreConfigSpec> specPair1 = new SpectreConfigSpec.Builder()
        .configure(Server::new);
    SERVER_SPEC = specPair1.getRight();
    SERVER = specPair1.getLeft();
    Pair<Common, SpectreConfigSpec> specPair2 = new SpectreConfigSpec.Builder()
        .configure(Common::new);
    COMMON_SPEC = specPair2.getRight();
    COMMON = specPair2.getLeft();
  }

  public static class Common {

    public final SpectreConfigSpec.BooleanValue enableHammockRecipes;
    public final SpectreConfigSpec.BooleanValue enableSleepingBagRecipes;

    public Common(SpectreConfigSpec.Builder builder) {

      enableHammockRecipes =
          builder.comment("If enabled, the default hammock recipes will be available.")
              .translation(CONFIG_PREFIX + "enableHammockRecipes")
              .define("enableHammockRecipes", true);

      enableSleepingBagRecipes =
          builder.comment("If enabled, the default sleeping bag recipes will be available.")
              .translation(CONFIG_PREFIX + "enableSleepingBagRecipes")
              .define("enableSleepingBagRecipes", true);
    }
  }

  public static class Server {

    public final SpectreConfigSpec.BooleanValue autoUse;
    public final SpectreConfigSpec.BooleanValue restrictSleeping;
    public final SpectreConfigSpec.DoubleValue restMultiplier;
    public final SpectreConfigSpec.EnumValue<ComfortsTimeUse> hammockUse;
    public final SpectreConfigSpec.EnumValue<ComfortsTimeUse> sleepingBagUse;
    public final SpectreConfigSpec.IntValue daySleepingPercentage;
    public final SpectreConfigSpec.IntValue dayWakeTimeOffset;
    public final SpectreConfigSpec.IntValue nightWakeTimeOffset;
    public final SpectreConfigSpec.BooleanValue sleepingBagsStopPhantoms;
    public final SpectreConfigSpec.BooleanValue hammocksStopPhantoms;
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

      daySleepingPercentage = builder.comment("""
              What percentage of players must sleep to skip the day.
              A percentage value of 0 will allow the day to be skipped by just 1 player, and a percentage value of 100 will require all players to sleep before skipping the day.
              A value of less than 0 will default to the playerSleepingPercentage game rule.
              """).translation(CONFIG_PREFIX + "daySleepingPercentage")
          .defineInRange("daySleepingPercentage", -1, -1, 100);

      dayWakeTimeOffset = builder.comment(
              "The amount of time, in ticks, to add or remove from the new time after sleeping through a night.")
          .translation(CONFIG_PREFIX + "dayWakeTimeOffset")
          .defineInRange("dayWakeTimeOffset", 0, -2000, 2000);

      nightWakeTimeOffset = builder.comment(
              "The amount of time, in ticks, to add or remove from the new time after sleeping through a day.")
          .translation(CONFIG_PREFIX + "nightWakeTimeOffset")
          .defineInRange("nightWakeTimeOffset", 0, -2000, 2000);

      hammocksStopPhantoms = builder.comment(
              "If enabled, attempting to sleep in hammocks stops phantoms from spawning.")
          .translation(CONFIG_PREFIX + "hammocksStopPhantoms").define("hammocksStopPhantoms", true);

      sleepingBagsStopPhantoms = builder.comment(
              "If enabled, attempting to sleep in sleeping bags stops phantoms from spawning.")
          .translation(CONFIG_PREFIX + "sleepingBagsStopPhantoms")
          .define("sleepingBagsStopPhantoms", true);

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
              s -> {
                if (s instanceof String str) {
                  String[] split = str.split(";");
                  return split.length == 3 && Identifier.tryParse(split[0]) != null &&
                      split[1].matches("\\d+") && split[2].matches("\\d+");
                }
                return false;
              });
    }
  }

  public static void reload() {
    ComfortsEvents.effectsInitialized = false;
  }

  public enum ComfortsTimeUse {
    NONE(Component.translatable("item.comforts.no_sleep")),
    DAY(Component.translatable("item.comforts.hammock.no_sleep")),
    NIGHT(Component.translatable("block.minecraft.bed.no_sleep")),
    DAY_OR_NIGHT(Component.translatable("item.comforts.hammock.no_sleep.2"));

    private final Component message;

    ComfortsTimeUse(Component message) {
      this.message = message;
    }

    public Component getMessage() {
      return this.message;
    }
  }
}
