/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common.blocks;

import c4.comforts.common.items.ComfortsItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSleepingBag extends BlockBase {

    protected static final AxisAlignedBB SLEEPING_BAG_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);

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

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ComfortsItems.SLEEPING_BAG, 1, color);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(PART) == EnumPartType.FOOT ? Items.AIR : ComfortsItems.SLEEPING_BAG;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SLEEPING_BAG_AABB;
    }
}
