package top.theillusivec4.comforts.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import top.theillusivec4.comforts.common.ComfortsComponents;
import top.theillusivec4.comforts.common.config.ComfortsConfig;
import top.theillusivec4.comforts.common.network.ComfortsNetwork;

public class SleepingBagItem extends AbstractComfortsItem {

  public SleepingBagItem(Block block) {
    super(block);
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    final ActionResult result = super.useOnBlock(context);
    final PlayerEntity player = context.getPlayer();

    if (player instanceof ServerPlayerEntity && result.isAccepted() && ComfortsConfig.autoUse &&
        !player.isSneaking()) {
      final BlockPos pos = context.getBlockPos().up();
      ComfortsComponents.SLEEP_TRACKER.maybeGet(player)
          .ifPresent(tracker -> tracker.setAutoSleepPos(pos));
      ComfortsNetwork.sendAutoSleep(player, pos);
    }
    return result;
  }
}
