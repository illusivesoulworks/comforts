/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common.items;

import c4.comforts.common.ConfigHandler;
import c4.comforts.common.blocks.ComfortsBlocks;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.common.capability.IWellRested;
import c4.comforts.common.capability.WellRested;
import c4.comforts.common.util.SleepHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemSleepingBag extends ItemBase {

    public ItemSleepingBag() {
        super("sleeping_bag");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (ConfigHandler.autoUse && !playerIn.isSneaking()) {
            if (worldIn.isRemote) {
                return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
            } else if (worldIn.provider.canRespawnHere() && worldIn.getBiome(playerIn.getPosition()) != Biomes.HELL) {
                EnumFacing enumfacing = playerIn.getHorizontalFacing();
                BlockPos pos = playerIn.getPosition().offset(enumfacing);
                IBlockState iblockstate = worldIn.getBlockState(pos);
                Block block = iblockstate.getBlock();
                boolean flag = block.isReplaceable(worldIn, pos);

                if (!flag) {
                    pos = pos.up();
                }

                BlockPos blockpos = pos.offset(enumfacing);
                ItemStack itemstack = playerIn.getHeldItem(handIn);

                if (playerIn.canPlayerEdit(pos, enumfacing, itemstack) && playerIn.canPlayerEdit(blockpos, enumfacing, itemstack)) {
                    IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
                    boolean flag1 = iblockstate1.getBlock().isReplaceable(worldIn, blockpos);
                    boolean flag2 = flag || worldIn.isAirBlock(pos);
                    boolean flag3 = flag1 || worldIn.isAirBlock(blockpos);

                    if (flag2 && flag3 && worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos, EnumFacing.UP) && worldIn.getBlockState(blockpos.down()).isSideSolid(worldIn, pos, EnumFacing.UP)) {

                        EntityPlayer.SleepResult entityplayer$sleepresult = SleepHelper.goToSleep(playerIn, blockpos, true);

                        if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK)
                        {
                            IBlockState iblockstate2 = ComfortsBlocks.SLEEPING_BAGS[itemstack.getMetadata()].getDefaultState().withProperty(BlockSleepingBag.OCCUPIED, false).withProperty(BlockSleepingBag.FACING, enumfacing).withProperty(BlockSleepingBag.PART, BlockSleepingBag.EnumPartType.FOOT);
                            IBlockState iblockstate3 = iblockstate2.withProperty(BlockSleepingBag.PART, BlockSleepingBag.EnumPartType.HEAD).withProperty(BlockSleepingBag.OCCUPIED, true);
                            worldIn.setBlockState(playerIn.getPosition().offset(enumfacing.getOpposite()), iblockstate2, 10);
                            worldIn.setBlockState(playerIn.getPosition(), iblockstate3, 10);
                            SoundType soundtype = iblockstate2.getBlock().getSoundType(iblockstate2, worldIn, pos, playerIn);
                            worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                            worldIn.notifyNeighborsRespectDebug(pos, block, false);
                            worldIn.notifyNeighborsRespectDebug(blockpos, iblockstate1.getBlock(), false);

                            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) playerIn, pos, itemstack);

                            itemstack.shrink(1);

                            return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
                        }
                        else
                        {
                            if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW)
                            {
                                playerIn.sendStatusMessage(new TextComponentTranslation("tile.sleeping_bag.noSleep"), true);
                            }
                            else if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_SAFE)
                            {
                                playerIn.sendStatusMessage(new TextComponentTranslation("tile.sleeping_bag.notSafe"), true);
                            }

                            return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
                        }

                    } else {

                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.sleeping_bag.noSpace"), true);

                        return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
                    }
                } else {

                    playerIn.sendStatusMessage(new TextComponentTranslation("tile.sleeping_bag.noSpace"), true);

                    return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
                }
            } else {
                BlockPos pos = playerIn.getPosition();
                worldIn.newExplosion(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 3.0F, true, true);
                return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
            }
        } else {
            return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!ConfigHandler.autoUse || player.isSneaking()) {
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

                    if (flag2 && flag3 && worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos, EnumFacing.UP) && worldIn.getBlockState(blockpos.down()).isSideSolid(worldIn, pos, EnumFacing.UP)) {
                        IBlockState iblockstate2 = ComfortsBlocks.SLEEPING_BAGS[itemstack.getMetadata()].getDefaultState().withProperty(BlockSleepingBag.OCCUPIED, false).withProperty(BlockSleepingBag.FACING, enumfacing).withProperty(BlockSleepingBag.PART, BlockSleepingBag.EnumPartType.FOOT);
                        worldIn.setBlockState(pos, iblockstate2, 10);
                        worldIn.setBlockState(blockpos, iblockstate2.withProperty(BlockSleepingBag.PART, BlockSleepingBag.EnumPartType.HEAD), 10);
                        SoundType soundtype = iblockstate2.getBlock().getSoundType(iblockstate2, worldIn, pos, player);
                        worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                        worldIn.notifyNeighborsRespectDebug(pos, block, false);
                        worldIn.notifyNeighborsRespectDebug(blockpos, iblockstate1.getBlock(), false);

                        if (player instanceof EntityPlayerMP) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, itemstack);
                        }

                        itemstack.shrink(1);
                        return EnumActionResult.SUCCESS;
                    } else {
                        return EnumActionResult.FAIL;
                    }
                } else {
                    return EnumActionResult.FAIL;
                }
            }
        } else {
            return EnumActionResult.FAIL;
        }
    }
}
