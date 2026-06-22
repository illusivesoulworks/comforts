package com.illusivesoulworks.comforts.common.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NullMarked;

public class RopeAndNailItem extends BaseComfortsItem {

  public RopeAndNailItem(Block block) {
    super(block);
  }

  @Override
  public @NullMarked void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                          TooltipDisplay tooltipDisplay,
                                          Consumer<Component> consumer, TooltipFlag flag) {
    consumer.accept(Component.translatable("item.comforts.rope_and_nail.placement.tooltip")
        .withStyle(ChatFormatting.GRAY));
  }
}
