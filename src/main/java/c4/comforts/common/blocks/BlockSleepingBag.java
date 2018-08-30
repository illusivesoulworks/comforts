/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.common.blocks;

import c4.comforts.common.items.ComfortsItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockSleepingBag extends BlockBase {

    private static final AxisAlignedBB SLEEPING_BAG_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);

    public BlockSleepingBag(EnumDyeColor color)
    {
        super("sleeping_bag", color);
        this.setExplosivePower(3.0F);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        EnumFacing enumfacing = state.getValue(FACING);

        if (state.getValue(PART) == EnumPartType.FOOT)
        {
            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
            }

        }
        else if (worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this)
        {
            if (!worldIn.isRemote)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
            }

            worldIn.setBlockToAir(pos);
        }
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(ComfortsItems.SLEEPING_BAG, 1, color);
    }

    @Nonnull
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(PART) == EnumPartType.FOOT ? Items.AIR : ComfortsItems.SLEEPING_BAG;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SLEEPING_BAG_AABB;
    }
}
