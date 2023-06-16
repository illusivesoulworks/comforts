package com.illusivesoulworks.comforts.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class RopeAndNailItem extends BaseComfortsItem {

  public RopeAndNailItem(Block block) {
    super(block);
  }

  @Override
  public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level,
                              @Nonnull List<Component> components,
                              @Nonnull TooltipFlag flag) {
    components.add(Component.translatable("item.comforts.rope_and_nail.placement.tooltip")
        .withStyle(ChatFormatting.GRAY));
  }
}
