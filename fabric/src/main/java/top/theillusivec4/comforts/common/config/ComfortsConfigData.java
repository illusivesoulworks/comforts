package top.theillusivec4.comforts.common.config;

import java.util.ArrayList;
import java.util.List;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import top.theillusivec4.comforts.common.ComfortsMod;

@Config(name = ComfortsMod.MOD_ID)
public class ComfortsConfigData implements ConfigData {

  @ConfigEntry.Gui.Tooltip
  @Comment("Set to true to automatically use sleeping bags when placed")
  public boolean autoUse = true;

  @ConfigEntry.Gui.Tooltip
  @Comment("Set to true to prevent sleeping depending on how long you previously slept")
  public boolean wellRested = false;

  @ConfigEntry.Gui.Tooltip(count = 2)
  @Comment("Determines how long you need to wait before sleeping if well-rested is true (larger numbers = sleep sooner)")
  public double sleepyFactor = 2.0D;

  @ConfigEntry.Gui.Tooltip
  @Comment("Set to true to enable sleeping in hammocks at night")
  public boolean nightHammocks = false;

  @ConfigEntry.Gui.Tooltip
  @Comment("The chance that a sleeping bag will break upon usage")
  public double sleepingBagBreakage = 0.0D;

  @ConfigEntry.Gui.Tooltip(count = 2)
  @Comment("List of debuffs to apply to players after using the sleeping bag\nFormat: [effect];[duration(secs)];[power]")
  public List<String> sleepingBagDebuffs = new ArrayList<>();
}
