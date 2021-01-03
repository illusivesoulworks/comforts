package top.theillusivec4.comforts;

import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.comforts.common.ComfortsEvents;
import top.theillusivec4.comforts.common.ComfortsRegistry;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.item.HammockItem;
import top.theillusivec4.comforts.common.item.RopeAndNailItem;
import top.theillusivec4.comforts.common.item.SleepingBagItem;
import top.theillusivec4.somnus.api.SleepEvents;

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
  }

  public static Identifier id(String path) {
    return new Identifier(MOD_ID, path);
  }
}
