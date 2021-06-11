package top.theillusivec4.comforts.common.config;

import java.util.List;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.comforts.common.ComfortsMod;

public class AutoConfigPlugin {

  public static void setup() {
    ComfortsConfigData data =
        AutoConfig.register(ComfortsConfigData.class, JanksonConfigSerializer::new).getConfig();
    ServerLifecycleEvents.SERVER_STARTED.register(server -> bake(data));
    ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, b) -> bake(data));
  }

  public static Screen getConfigScreen(Screen screen) {
    return AutoConfig.getConfigScreen(ComfortsConfigData.class, screen).get();
  }

  private static void bake(ComfortsConfigData data) {
    ComfortsConfig.autoUse = data.autoUse;
    ComfortsConfig.wellRested = data.wellRested;
    ComfortsConfig.sleepyFactor = data.sleepyFactor;
    ComfortsConfig.nightHammocks = data.nightHammocks;
    ComfortsConfig.sleepingBagBreakage = data.sleepingBagBreakage;
    ComfortsConfig.sleepingBagDebuffs.clear();
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
      ComfortsConfig.sleepingBagDebuffs.add(new StatusEffectInstance(effect, duration * 20, amp - 1));
    }
  }
}
