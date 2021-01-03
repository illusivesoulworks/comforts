package top.theillusivec4.comforts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.comforts.common.ComfortsRegistry;
import top.theillusivec4.comforts.common.block.RopeAndNailBlock;
import top.theillusivec4.comforts.common.item.HammockItem;
import top.theillusivec4.comforts.common.item.RopeAndNailItem;
import top.theillusivec4.comforts.common.item.SleepingBagItem;

public class ComfortsMod implements ModInitializer {

  public static final String MOD_ID = "assets/comforts";
  public static final Logger LOGGER = LogManager.getLogger();
  public static final ItemGroup GROUP = FabricItemGroupBuilder
      .build(id("assets/comforts"),
          () -> new ItemStack(ComfortsRegistry.SLEEPING_BAGS.get(DyeColor.RED)));

  @Override
  public void onInitialize() {
    for (DyeColor value : DyeColor.values()) {
      Block sleepingBag = ComfortsRegistry.SLEEPING_BAGS.get(value);
      Registry.register(Registry.BLOCK, id("sleeping_bag"), sleepingBag);
      Registry.register(Registry.ITEM, id("sleeping_bag"), new SleepingBagItem(sleepingBag));
      Block hammock = ComfortsRegistry.HAMMOCKS.get(value);
      Registry.register(Registry.BLOCK, id("hammock"), hammock);
      Registry.register(Registry.ITEM, id("hammock"), new HammockItem(hammock));
    }
    Registry.register(Registry.BLOCK, id("rope_and_nail"), new RopeAndNailBlock());
    Registry.register(Registry.ITEM, id("rope_and_nail"), new RopeAndNailItem());
    Registry
        .register(Registry.BLOCK_ENTITY_TYPE, id("sleeping_bag"), ComfortsRegistry.SLEEPING_BAG_BE);
    Registry.register(Registry.BLOCK_ENTITY_TYPE, id("hammock"), ComfortsRegistry.HAMMOCK_BE);
  }

  public static Identifier id(String path) {
    return new Identifier(MOD_ID, path);
  }
}
