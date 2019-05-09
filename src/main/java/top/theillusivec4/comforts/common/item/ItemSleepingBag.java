package top.theillusivec4.comforts.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.ComfortsConfig;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;

import javax.annotation.Nonnull;

public class ItemSleepingBag extends ItemComfortsBase {

    public ItemSleepingBag(Block block) {
        super(block);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(ItemUseContext context) {
        EnumActionResult result = super.onItemUse(context);
        EntityPlayer player = context.getPlayer();

        if (player != null && result == EnumActionResult.SUCCESS && ComfortsConfig.SERVER.autoUse.get() && !player.isSneaking()) {
            BlockPos pos = context.getPos().up();
            World world = context.getWorld();
            IBlockState state = world.getBlockState(pos);

            if (state.get(BlockBed.PART) != BedPart.HEAD) {
                pos = pos.offset(state.get(BlockHorizontal.HORIZONTAL_FACING));
            }
            final BlockPos blockpos = pos;
            CapabilitySleepData.getCapability(player).ifPresent(sleepdata -> {
                sleepdata.setSleeping(true);
                sleepdata.setSleepingPos(blockpos);
            });
            return EnumActionResult.SUCCESS;
        }
        return result;
    }
}
