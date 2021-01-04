package top.theillusivec4.comforts.common.config;

import java.util.ArrayList;
import java.util.List;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.comforts.common.ComfortsMod;

public class ComfortsConfig {

  public static boolean autoUse;
  public static boolean wellRested;
  public static double sleepyFactor;
  public static boolean nightHammocks;
  public static double sleepingBagBreakage;
  public static List<StatusEffectInstance> sleepingBagDebuffs = new ArrayList<>();

  public static void setup() {
    ComfortsConfigData data =
        AutoConfig.register(ComfortsConfigData.class, JanksonConfigSerializer::new).getConfig();
    ServerLifecycleEvents.SERVER_STARTED.register(server -> bake(data));
    ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, b) -> bake(data));
  }

  public static void bake(ComfortsConfigData data) {
    autoUse = data.autoUse;
    wellRested = data.wellRested;
    sleepyFactor = data.sleepyFactor;
    nightHammocks = data.nightHammocks;
    sleepingBagBreakage = data.sleepingBagBreakage;
    sleepingBagDebuffs.clear();
    List<String> newDebuffs = data.sleepingBagDebuffs;

    for (String debuff : newDebuffs) {
      String[] elements = debuff.split(";");
      StatusEffect effect = Registry.STATUS_EFFECT.get(Identifier.tryParse(elements[0]));

      if (effect == null) {
        return;
      }
      int duration;
      int amp;
      try {
        duration = Math.max(1, Math.min(Integer.parseInt(elements[1]), 1600));
        amp = Math.max(1, Math.min(Integer.parseInt(elements[2]), 4));
      } catch (NumberFormatException e) {
        ComfortsMod.LOGGER.error("Malformed config in sleeping bag debuffs: " + debuff);
        continue;
      }
      sleepingBagDebuffs.add(new StatusEffectInstance(effect, duration * 20, amp - 1));
    }
  }
}
