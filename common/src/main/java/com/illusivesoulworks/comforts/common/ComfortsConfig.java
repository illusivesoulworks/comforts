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
import net.minecraft.resources.Identifier;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.List;

public class ComfortsConfig {

  public static final ForgeConfigSpec SERVER_SPEC;
  public static final Server SERVER;
  public static final ForgeConfigSpec COMMON_SPEC;
  public static final Common COMMON;
  private static final String CONFIG_PREFIX = "gui." + ComfortsConstants.MOD_ID + ".config.";

  static {
    Pair<Server, ForgeConfigSpec> specPair1 = new ForgeConfigSpec.Builder()
        .configure(Server::new);
    SERVER_SPEC = specPair1.getRight();
    SERVER = specPair1.getLeft();
    Pair<Common, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder()
        .configure(Common::new);
    COMMON_SPEC = specPair2.getRight();
    COMMON = specPair2.getLeft();
  }

  public static class Common {

    public final ForgeConfigSpec.BooleanValue enableHammockRecipes;
    public final ForgeConfigSpec.BooleanValue enableSleepingBagRecipes;

    public Common(ForgeConfigSpec.Builder builder) {

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

    public final ForgeConfigSpec.BooleanValue autoUse;
    public final ForgeConfigSpec.BooleanValue restrictSleeping;
    public final ForgeConfigSpec.DoubleValue restMultiplier;
    public final ForgeConfigSpec.EnumValue<ComfortsConstants.TimeUse> hammockUse;
    public final ForgeConfigSpec.EnumValue<ComfortsConstants.TimeUse> sleepingBagUse;
    public final ForgeConfigSpec.IntValue daySleepingPercentage;
    public final ForgeConfigSpec.IntValue dayWakeTimeOffset;
    public final ForgeConfigSpec.IntValue nightWakeTimeOffset;
    public final ForgeConfigSpec.BooleanValue sleepingBagsStopPhantoms;
    public final ForgeConfigSpec.BooleanValue hammocksStopPhantoms;
    public final ForgeConfigSpec.IntValue sleepingBagBreakChance;
    public final ForgeConfigSpec.DoubleValue sleepingBagBreakChanceLuckMultiplier;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> sleepingBagEffects;

    public Server(ForgeConfigSpec.Builder builder) {

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
          .translation(CONFIG_PREFIX + "hammockUse").defineEnum("hammockUse", ComfortsConstants.TimeUse.DAY);

      sleepingBagUse = builder.comment("The time of day that sleeping bags can be used.")
          .translation(CONFIG_PREFIX + "sleepingBagUse")
          .defineEnum("sleepingBagUse", ComfortsConstants.TimeUse.NIGHT);

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


}
