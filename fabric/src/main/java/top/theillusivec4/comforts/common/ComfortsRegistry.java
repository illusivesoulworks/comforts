package top.theillusivec4.comforts.common;

import java.util.EnumMap;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.DyeColor;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.RopeAndNailBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;
import top.theillusivec4.comforts.common.block.entity.HammockBlockEntity;
import top.theillusivec4.comforts.common.block.entity.SleepingBagBlockEntity;

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
}
