package top.theillusivec4.comforts.common;

import java.util.EnumMap;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.RopeAndNailBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;
import top.theillusivec4.comforts.common.block.entity.HammockBlockEntity;
import top.theillusivec4.comforts.common.block.entity.SleepingBagBlockEntity;
import top.theillusivec4.comforts.common.item.HammockItem;
import top.theillusivec4.comforts.common.item.RopeAndNailItem;
import top.theillusivec4.comforts.common.item.SleepingBagItem;

public class ComfortsRegistry {

  public static final EnumMap<DyeColor, Block> SLEEPING_BAGS = new EnumMap<>(DyeColor.class);
  public static final EnumMap<DyeColor, Block> HAMMOCKS = new EnumMap<>(DyeColor.class);
  public static final Block ROPE_AND_NAIL = new RopeAndNailBlock();
  public static final BlockEntityType<SleepingBagBlockEntity> SLEEPING_BAG_BE;
  public static final BlockEntityType<HammockBlockEntity> HAMMOCK_BE;

  static {
    for (DyeColor value : DyeColor.values()) {
      SLEEPING_BAGS.put(value, new SleepingBagBlock(value));
      HAMMOCKS.put(value, new HammockBlock(value));
    }
    SLEEPING_BAG_BE = BlockEntityType.Builder
        .create(SleepingBagBlockEntity::new, SLEEPING_BAGS.values().toArray(new Block[] {}))
        .build(null);
    HAMMOCK_BE = BlockEntityType.Builder
        .create(HammockBlockEntity::new, HAMMOCKS.values().toArray(new Block[] {})).build(null);
  }

  public static void setup() {
    for (DyeColor value : DyeColor.values()) {
      Block sleepingBag = ComfortsRegistry.SLEEPING_BAGS.get(value);
      Registry
          .register(Registry.BLOCK, ComfortsMod.id("sleeping_bag_" + value.getName()), sleepingBag);
      Registry.register(Registry.ITEM, ComfortsMod.id("sleeping_bag_" + value.getName()),
          new SleepingBagItem(sleepingBag));
      Block hammock = ComfortsRegistry.HAMMOCKS.get(value);
      Registry.register(Registry.BLOCK, ComfortsMod.id("hammock_" + value.getName()), hammock);
      Registry.register(Registry.ITEM, ComfortsMod.id("hammock_" + value.getName()),
          new HammockItem(hammock));
    }
    Registry
        .register(Registry.BLOCK, ComfortsMod.id("rope_and_nail"), ComfortsRegistry.ROPE_AND_NAIL);
    Registry.register(Registry.ITEM, ComfortsMod.id("rope_and_nail"), new RopeAndNailItem());
    Registry
        .register(Registry.BLOCK_ENTITY_TYPE, ComfortsMod.id("sleeping_bag"),
            ComfortsRegistry.SLEEPING_BAG_BE);
    Registry.register(Registry.BLOCK_ENTITY_TYPE, ComfortsMod.id("hammock"),
        ComfortsRegistry.HAMMOCK_BE);
  }
}
