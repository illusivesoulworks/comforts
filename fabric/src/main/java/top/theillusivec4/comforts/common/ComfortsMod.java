package top.theillusivec4.comforts.common;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.comforts.common.config.ComfortsConfig;
import top.theillusivec4.comforts.common.config.ComfortsConfigData;

public class ComfortsMod implements ModInitializer {

  public static final String MOD_ID = "comforts";
  public static final Logger LOGGER = LogManager.getLogger();
  public static final ItemGroup GROUP = FabricItemGroupBuilder
      .build(id("general"),
          () -> new ItemStack(ComfortsRegistry.SLEEPING_BAGS.get(DyeColor.RED)));

  @Override
  public void onInitialize() {
    ComfortsRegistry.setup();
    ComfortsEvents.setup();
    ComfortsConfig.setup();
  }

  public static Identifier id(String path) {
    return new Identifier(MOD_ID, path);
  }
}
