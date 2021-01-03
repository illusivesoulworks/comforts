package top.theillusivec4.comforts.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import top.theillusivec4.comforts.common.ComfortsMod;

public abstract class AbstractComfortsItem extends BlockItem {

  public AbstractComfortsItem(Block block) {
    super(block, new Item.Settings().group(ComfortsMod.GROUP));
  }

  @Override
  protected boolean place(ItemPlacementContext context, BlockState state) {
    return context.getWorld().setBlockState(context.getBlockPos(), state, 26);
  }
}
