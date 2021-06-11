package top.theillusivec4.comforts.common.config;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.comforts.common.ComfortsMod;

public class ComfortsConfig {

  public static boolean autoUse = true;
  public static boolean wellRested = false;
  public static double sleepyFactor = 2.0d;
  public static boolean nightHammocks = false;
  public static double sleepingBagBreakage = 0.0d;
  public static List<StatusEffectInstance> sleepingBagDebuffs = new ArrayList<>();
}
