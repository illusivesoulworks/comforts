package top.theillusivec4.comforts.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.comforts.common.config.AutoConfigPlugin;
import top.theillusivec4.comforts.common.config.ComfortsConfig;

public class ComfortsMod implements ModInitializer {

  public static final String MOD_ID = "comforts";
  public static final Logger LOGGER = LogManager.getLogger();
  public static final ItemGroup GROUP = FabricItemGroupBuilder
      .build(id("general"),
          () -> new ItemStack(ComfortsRegistry.SLEEPING_BAGS.get(DyeColor.RED)));

  public static boolean isConfigLoaded = false;

  @Override
  public void onInitialize() {
    ComfortsRegistry.setup();
    ComfortsEvents.setup();
    isConfigLoaded = FabricLoader.getInstance().isModLoaded("cloth-config2");

    if (isConfigLoaded) {
      AutoConfigPlugin.setup();
    }
  }

  public static Identifier id(String path) {
    return new Identifier(MOD_ID, path);
  }
}
