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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import top.theillusivec4.comforts.ComfortsMod;
import top.theillusivec4.comforts.common.tileentity.HammockTileEntity;

public class HammockBlock extends ComfortsBaseBlock {

  private static final VoxelShape HAMMOCK_SHAPE = Block
      .box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
  private static final VoxelShape NORTH_SHAPE = Shapes
      .or(Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 16.0D),
          Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 1.0D));
  private static final VoxelShape SOUTH_SHAPE = Shapes
      .or(Block.box(1.0D, 0.0D, 0.0D, 15.0D, 1.0D, 15.0D),
          Block.box(0.0D, 0.0D, 15.0D, 16.0D, 1.0D, 16.0D));
  private static final VoxelShape WEST_SHAPE = Shapes
      .or(Block.box(1.0D, 0.0D, 1.0D, 16.0D, 1.0D, 15.0D),
          Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 16.0D));
  private static final VoxelShape EAST_SHAPE = Shapes
      .or(Block.box(1.0D, 0.0D, 1.0D, 16.0D, 1.0D, 15.0D),
          Block.box(15.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D));
  private final DyeColor color;

  public HammockBlock(DyeColor color) {
    super(BedType.HAMMOCK, color,
        Block.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.1F));
    this.color = color;
  }

  public static Direction getDirectionToOther(BedPart part, Direction facing) {
    return part == BedPart.FOOT ? facing : facing.getOpposite();
  }

  public static void dropRopeSupport(BlockPos pos, Direction direction, boolean isHead,
                                     Level worldIn) {
    BlockPos ropePos = isHead ? pos.relative(direction) : pos.relative(direction.getOpposite());
    BlockState ropeState = worldIn.getBlockState(ropePos);

    if (ropeState.getBlock() instanceof RopeAndNailBlock) {
      worldIn.setBlockAndUpdate(ropePos, ropeState.setValue(SUPPORTING, false));
    }
  }

  @Nonnull
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos,
                             CollisionContext context) {
    final Direction direction = getConnectedDirection(state).getOpposite();
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
  public void playerWillDestroy(Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state,
                                @Nonnull Player player) {
    super.playerWillDestroy(worldIn, pos, state, player);
    final BedPart bedpart = state.getValue(PART);
    final boolean isHead = bedpart == BedPart.HEAD;
    final Direction direction = state.getValue(FACING);
    final BlockPos otherPos = pos.relative(getDirectionToOther(bedpart, direction));
    dropRopeSupport(pos, direction, isHead, worldIn);
    dropRopeSupport(otherPos, direction, !isHead, worldIn);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    final Direction direction = context.getClickedFace();

    if (direction == Direction.UP || direction == Direction.DOWN) {
      return null;
    }
    final BlockPos blockpos = context.getClickedPos();
    final BlockPos blockpos1 = blockpos.relative(direction);
    final FluidState ifluidstate = context.getLevel().getFluidState(blockpos);
    return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this
        .defaultBlockState().setValue(FACING, direction)
        .setValue(ComfortsBaseBlock.WATERLOGGED, ifluidstate.getType() == Fluids.WATER) : null;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new HammockTileEntity(pos, state, this.color);
  }
}
