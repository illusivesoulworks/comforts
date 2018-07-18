/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.common.items;

import c4.comforts.common.ConfigHandler;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.common.blocks.ComfortsBlocks;
import c4.comforts.common.capability.CapabilitySleeping;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemSleepingBag extends ItemBase {

    public ItemSleepingBag() {
        super("sleeping_bag");
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        } else if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        } else {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            boolean flag = block.isReplaceable(worldIn, pos);

            if (!flag) {
                pos = pos.up();
            }

            int i = MathHelper.floor((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            EnumFacing enumfacing = EnumFacing.getHorizontal(i);
            BlockPos blockpos = pos.offset(enumfacing);
            ItemStack itemstack = player.getHeldItem(hand);

            if (player.canPlayerEdit(pos, facing, itemstack) && player.canPlayerEdit(blockpos, facing, itemstack)) {
                IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
                boolean flag1 = iblockstate1.getBlock().isReplaceable(worldIn, blockpos);
                boolean flag2 = flag || worldIn.isAirBlock(pos);
                boolean flag3 = flag1 || worldIn.isAirBlock(blockpos);

                if (flag2 && flag3 && worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos, EnumFacing.UP)
                        && worldIn.getBlockState(blockpos.down()).isSideSolid(worldIn, pos, EnumFacing.UP)) {
                    IBlockState iblockstate2 =
                            ComfortsBlocks.SLEEPING_BAGS[itemstack.getMetadata()].getDefaultState()
                            .withProperty(BlockSleepingBag.OCCUPIED, false)
                            .withProperty(BlockSleepingBag.FACING, enumfacing)
                            .withProperty(BlockSleepingBag.PART, BlockSleepingBag.EnumPartType.FOOT);

                    worldIn.setBlockState(pos, iblockstate2, 10);
                    worldIn.setBlockState(blockpos,
                            iblockstate2.withProperty(BlockSleepingBag.PART, BlockSleepingBag.EnumPartType.HEAD), 10);

                    SoundType soundtype = iblockstate2.getBlock().getSoundType(iblockstate2, worldIn, pos, player);
                    worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS,
                            (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                    worldIn.notifyNeighborsRespectDebug(pos, block, false);
                    worldIn.notifyNeighborsRespectDebug(blockpos, iblockstate1.getBlock(), false);

                    if (player instanceof EntityPlayerMP) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, itemstack);
                    }

                    itemstack.shrink(1);

                    if (ConfigHandler.autoUse && !player.isSneaking()) {
                        CapabilitySleeping.ISleeping sleeping = CapabilitySleeping.getSleeping(player);

                        if (sleeping != null) {
                            sleeping.setSleeping(true);
                            sleeping.setPos(blockpos);
                        }
                    }

                    return EnumActionResult.SUCCESS;
                } else {
                    return EnumActionResult.FAIL;
                }
            } else {
                return EnumActionResult.FAIL;
            }
        }
    }
}
