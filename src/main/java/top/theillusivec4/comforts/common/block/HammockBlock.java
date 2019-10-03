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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.stats.StatList;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import top.theillusivec4.comforts.Comforts;
import top.theillusivec4.comforts.common.tileentity.TileEntityHammock;

import javax.annotation.Nonnull;

import static top.theillusivec4.comforts.common.block.RopeAndNailBlock.SUPPORTING;

public class HammockBlock extends ComfortsBaseBlock {

    private static final VoxelShape HAMMOCK_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private final DyeColor color;

    public HammockBlock(DyeColor color) {
        super(BedType.HAMMOCK, color, Block.Properties.create(Material.WOOL).sound(SoundType.CLOTH).hardnessAndResistance(0.1F));
        this.color = color;
        this.setRegistryName(Comforts.MODID, "hammock_" + color.getTranslationKey());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return HAMMOCK_SHAPE;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, @Nonnull PlayerEntity player) {
        BedPart bedpart = state.get(PART);
        boolean flag = bedpart == BedPart.HEAD;
        Direction facing = state.get(HORIZONTAL_FACING);
        BlockPos blockpos = pos.offset(getDirectionToOther(bedpart, facing));
        BlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() == this && iblockstate.get(PART) != bedpart) {
            worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
            worldIn.playEvent(player, 2001, blockpos, Block.getStateId(iblockstate));
            BlockPos posrope1 = flag ? pos.offset(facing) : pos.offset(facing.getOpposite());
            BlockPos posrope2 = flag ? pos.offset(facing.getOpposite(), 2) : pos.offset(facing, 2);
            BlockState rope1 = worldIn.getBlockState(posrope1);
            BlockState rope2 = worldIn.getBlockState(posrope2);

            if (rope1.getBlock() instanceof RopeAndNailBlock) {
                worldIn.setBlockState(posrope1, rope1.with(SUPPORTING, false));
            }

            if (rope2.getBlock() instanceof RopeAndNailBlock) {
                worldIn.setBlockState(posrope2, rope2.with(SUPPORTING, false));
            }

            if (!worldIn.isRemote && !player.isCreative()) {

                if (flag) {
                    spawnDrops(worldIn, pos, 0);
                } else {
                    iblockstate.dropBlockAsItem(worldIn, blockpos, 0);
                }
            }
            player.addStat(Stats.BLOCK_MINED.get(this));
        }
        worldIn.playEvent(player, 2001, pos, Block.getStateId(state));
    }

    public static Direction getDirectionToOther(BedPart part, Direction facing) {
        return part == BedPart.FOOT ? facing : facing.getOpposite();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getFace();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(direction);
        return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this.getDefaultState().with(HORIZONTAL_FACING, direction) : null;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityHammock(this.color);
    }
}
