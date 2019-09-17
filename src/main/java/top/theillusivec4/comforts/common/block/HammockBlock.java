/*
 * Copyright (C) 2017-2019  C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import top.theillusivec4.comforts.Comforts;
import top.theillusivec4.comforts.common.tileentity.TileEntityHammock;

import javax.annotation.Nonnull;

import static top.theillusivec4.comforts.common.block.BlockRopeAndNail.SUPPORTING;

public class HammockBlock extends ComfortsBaseBlock {

    private static final VoxelShape HAMMOCK_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private final EnumDyeColor color;

    public HammockBlock(EnumDyeColor color) {
        super(BedType.HAMMOCK, color, Block.Properties.create(Material.CLOTH).sound(SoundType.CLOTH).hardnessAndResistance(0.1F));
        this.color = color;
        this.setRegistryName(Comforts.MODID, "hammock_" + color.getTranslationKey());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return HAMMOCK_SHAPE;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, @Nonnull EntityPlayer player) {
        BedPart bedpart = state.get(PART);
        boolean flag = bedpart == BedPart.HEAD;
        EnumFacing facing = state.get(HORIZONTAL_FACING);
        BlockPos blockpos = pos.offset(getDirectionToOther(bedpart, facing));
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() == this && iblockstate.get(PART) != bedpart) {
            worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
            worldIn.playEvent(player, 2001, blockpos, Block.getStateId(iblockstate));
            BlockPos posrope1 = flag ? pos.offset(facing) : pos.offset(facing.getOpposite());
            BlockPos posrope2 = flag ? pos.offset(facing.getOpposite(), 2) : pos.offset(facing, 2);
            IBlockState rope1 = worldIn.getBlockState(posrope1);
            IBlockState rope2 = worldIn.getBlockState(posrope2);

            if (rope1.getBlock() instanceof BlockRopeAndNail) {
                worldIn.setBlockState(posrope1, rope1.with(SUPPORTING, false));
            }

            if (rope2.getBlock() instanceof BlockRopeAndNail) {
                worldIn.setBlockState(posrope2, rope2.with(SUPPORTING, false));
            }

            if (!worldIn.isRemote && !player.isCreative()) {

                if (flag) {
                    state.dropBlockAsItem(worldIn, pos, 0);
                } else {
                    iblockstate.dropBlockAsItem(worldIn, blockpos, 0);
                }
            }
            player.addStat(StatList.BLOCK_MINED.get(this));
        }
        worldIn.playEvent(player, 2001, pos, Block.getStateId(state));
    }

    public static EnumFacing getDirectionToOther(BedPart part, EnumFacing facing) {
        return part == BedPart.FOOT ? facing : facing.getOpposite();
    }

    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        EnumFacing enumfacing = context.getFace();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(enumfacing);
        return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this.getDefaultState().with(HORIZONTAL_FACING, enumfacing) : null;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityHammock(this.color);
    }
}
