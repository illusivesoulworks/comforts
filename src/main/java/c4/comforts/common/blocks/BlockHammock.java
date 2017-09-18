package c4.comforts.common.blocks;

import c4.comforts.common.items.ComfortsItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import static c4.comforts.common.blocks.BlockRope.SUPPORTING;

public class BlockHammock extends BlockBase {

    protected static final AxisAlignedBB HAMMOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);

    public BlockHammock(EnumDyeColor color) {

        super("hammock", color);
        this.setExplosivePower(1.0F);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        EnumFacing enumfacing = state.getValue(FACING);

        if (state.getValue(PART) == EnumPartType.FOOT)
        {
            IBlockState blockstate = worldIn.getBlockState(pos.offset(enumfacing.getOpposite()));

            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this)
            {
                worldIn.setBlockToAir(pos);

                if (blockstate.getBlock() instanceof BlockRope) {
                    if (blockstate.getValue(SUPPORTING) && blockstate.getValue(FACING) == enumfacing) {
                        worldIn.setBlockState(pos.offset(enumfacing.getOpposite()), blockstate.withProperty(SUPPORTING, false));
                    }
                }

            } else if (!(blockstate.getBlock() instanceof BlockRope)) {

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

            IBlockState blockstate1 = worldIn.getBlockState(pos.offset(enumfacing));

            if (blockstate1.getBlock() instanceof BlockRope) {
                if (blockstate1.getValue(SUPPORTING) && blockstate1.getValue(FACING) == enumfacing.getOpposite()) {
                    worldIn.setBlockState(pos.offset(enumfacing), blockstate1.withProperty(SUPPORTING, false));
                }
            }
        } else if (!(worldIn.getBlockState(pos.offset(enumfacing)).getBlock() instanceof BlockRope)) {

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
        return new ItemStack(ComfortsItems.HAMMOCK, 1, color);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(PART) == EnumPartType.FOOT ? Items.AIR : ComfortsItems.HAMMOCK;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return HAMMOCK_AABB;
    }

}
