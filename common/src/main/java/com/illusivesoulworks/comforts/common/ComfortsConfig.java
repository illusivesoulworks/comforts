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
import com.illusivesoulworks.comforts.platform.Services;
import com.illusivesoulworks.spectrelib.config.SpectreConfigSpec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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

      sleepingBagEffects = builder.comment(
              "List of debuffs to apply to players after using the sleeping bag\n"
                  + "Format: effect;duration(secs);power")
          .translation(CONFIG_PREFIX + "sleepingBagDebuffs")
          .define("sleepingBagDebuffs", new ArrayList<>());

      builder.pop();
    }
  }

  public static void reload() {
    ComfortsEvents.SLEEPING_BAG_EFFECTS.clear();
    SERVER.sleepingBagEffects.get().forEach(effect -> {
      String[] elements = effect.split(";");
      MobEffect mobEffect = Services.REGISTRY_UTIL.getMobEffect(new ResourceLocation(elements[0]));

      if (mobEffect == null) {
        return;
      }
      int duration = 0;
      int amp = 0;
      try {
        duration = Math.max(1, Math.min(Integer.parseInt(elements[1]), 1600));
        amp = Math.max(1, Math.min(Integer.parseInt(elements[2]), 4));
      } catch (Exception e) {
        ComfortsConstants.LOG.error("Problem parsing sleeping bag effects in config!", e);
      }
      ComfortsEvents.SLEEPING_BAG_EFFECTS.add(
          new MobEffectInstance(mobEffect, duration * 20, amp - 1));
    });
  }
}
