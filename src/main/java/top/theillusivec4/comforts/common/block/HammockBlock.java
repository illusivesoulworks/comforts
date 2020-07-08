/*
 * Copyright (c) 2017-2020 C4
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

import static top.theillusivec4.comforts.common.block.RopeAndNailBlock.SUPPORTING;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import top.theillusivec4.comforts.Comforts;
import top.theillusivec4.comforts.common.tileentity.HammockTileEntity;

public class HammockBlock extends ComfortsBaseBlock {

  private static final VoxelShape HAMMOCK_SHAPE = Block
      .makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
  private static final VoxelShape NORTH_SHAPE = VoxelShapes
      .or(Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 16.0D),
          Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 1.0D));
  private static final VoxelShape SOUTH_SHAPE = VoxelShapes
      .or(Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 1.0D, 15.0D),
          Block.makeCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 1.0D, 16.0D));
  private static final VoxelShape WEST_SHAPE = VoxelShapes
      .or(Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 1.0D, 15.0D),
          Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 16.0D));
  private static final VoxelShape EAST_SHAPE = VoxelShapes
      .or(Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 1.0D, 15.0D),
          Block.makeCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D));
  private final DyeColor color;

  public HammockBlock(DyeColor color) {
    super(BedType.HAMMOCK, color,
        Block.Properties.create(Material.WOOL).sound(SoundType.CLOTH).hardnessAndResistance(0.1F));
    this.color = color;
    this.setRegistryName(Comforts.MODID, "hammock_" + color.getTranslationKey());
  }

  public static Direction getDirectionToOther(BedPart part, Direction facing) {
    return part == BedPart.FOOT ? facing : facing.getOpposite();
  }

  public static void dropRopeSupport(BlockPos pos, Direction direction, boolean isHead,
      World worldIn) {
    BlockPos ropePos = isHead ? pos.offset(direction) : pos.offset(direction.getOpposite());
    BlockState ropeState = worldIn.getBlockState(ropePos);

    if (ropeState.getBlock() instanceof RopeAndNailBlock) {
      worldIn.setBlockState(ropePos, ropeState.with(SUPPORTING, false));
    }
  }

  @Nonnull
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
      ISelectionContext context) {
    Direction direction = func_226862_h_(state).getOpposite();
    switch (direction) {
      case NORTH:
        return NORTH_SHAPE;
      case SOUTH:
        return SOUTH_SHAPE;
      case WEST:
        return WEST_SHAPE;
      case EAST:
        return EAST_SHAPE;
      default:
        return HAMMOCK_SHAPE;
    }
  }

  @Override
  public void onBlockHarvested(World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state,
      @Nonnull PlayerEntity player) {
    super.onBlockHarvested(worldIn, pos, state, player);
    BedPart bedpart = state.get(PART);
    boolean isHead = bedpart == BedPart.HEAD;
    Direction direction = state.get(HORIZONTAL_FACING);
    BlockPos otherPos = pos.offset(getDirectionToOther(bedpart, direction));
    dropRopeSupport(pos, direction, isHead, worldIn);
    dropRopeSupport(otherPos, direction, !isHead, worldIn);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction direction = context.getFace();
    BlockPos blockpos = context.getPos();
    BlockPos blockpos1 = blockpos.offset(direction);
    FluidState ifluidstate = context.getWorld().getFluidState(blockpos);
    return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this
        .getDefaultState().with(HORIZONTAL_FACING, direction)
        .with(ComfortsBaseBlock.WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER) : null;
  }

  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new HammockTileEntity(this.color);
  }
}
