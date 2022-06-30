/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.comforts.common.block;

import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RopeAndNailBlock extends Block implements SimpleWaterloggedBlock {

  public static final DirectionProperty HORIZONTAL_FACING = HorizontalDirectionalBlock.FACING;
  public static final BooleanProperty SUPPORTING = BooleanProperty.create("supporting");

  private static final Map<Direction, VoxelShape> SHAPES_R = new EnumMap<>(ImmutableMap
      .of(Direction.NORTH, Block.box(6.0D, 0.0D, 12.0D, 10.0D, 8.0D, 16.0D),
          Direction.SOUTH, Block.box(6.0D, 0.0D, 0.0D, 10.0D, 8.0D, 4.0D),
          Direction.WEST, Block.box(12.0D, 0.0D, 6.0D, 16.0D, 8.0D, 10.0D),
          Direction.EAST, Block.box(0.0D, 0.0D, 6.0D, 4.0D, 8.0D, 10.0D)));

  private static final Map<Direction, VoxelShape> SHAPES_S = new EnumMap<>(ImmutableMap
      .of(Direction.NORTH, Block.box(6.0D, 3.0D, 9.0D, 10.0D, 8.0D, 16.0D),
          Direction.SOUTH, Block.box(6.0D, 3.0D, 0.0D, 10.0D, 8.0D, 7.0D),
          Direction.WEST, Block.box(9.0D, 3.0D, 6.0D, 16.0D, 8.0D, 10.0D),
          Direction.EAST, Block.box(0.0D, 3.0D, 6.0D, 7.0D, 8.0D, 10.0D)));

  public RopeAndNailBlock() {
    super(
        Block.Properties.of(Material.WOOL).sound(SoundType.METAL).strength(0.2F));
    this.registerDefaultState(
        this.stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH)
            .setValue(SUPPORTING, false));
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public VoxelShape getShape(BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos,
                             @Nonnull CollisionContext context) {
    return state.getValue(SUPPORTING) ? SHAPES_S.get(state.getValue(HORIZONTAL_FACING))
        : SHAPES_R.get(state.getValue(HORIZONTAL_FACING));
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    final Direction direction = state.getValue(HORIZONTAL_FACING);
    final BlockPos blockpos = pos.relative(direction.getOpposite());
    final BlockState blockstate = level.getBlockState(blockpos);
    final boolean valid = blockstate.isFaceSturdy(level, blockpos, direction);

    if (!valid && level instanceof ServerLevel serverLevel) {
      dropHammock(serverLevel, pos, state);
    }
    return valid;
  }

  private static void dropHammock(Level level, BlockPos pos, BlockState state) {
    final BlockPos frontPos = pos.relative(state.getValue(HORIZONTAL_FACING));
    final BlockState frontState = level.getBlockState(frontPos);

    if (state.getValue(SUPPORTING) && frontState.getBlock() instanceof HammockBlock) {
      final BedPart bedpart = frontState.getValue(BedBlock.PART);
      final boolean isHead = bedpart == BedPart.HEAD;
      final Direction frontDirection = frontState.getValue(HORIZONTAL_FACING);
      final BlockPos otherPos = frontPos
          .relative(HammockBlock.getDirectionToOther(bedpart, frontDirection));

      if (isHead) {
        dropResources(frontState, level, frontPos);
      }

      if (frontState.getValue(BaseComfortsBlock.WATERLOGGED)) {
        level.setBlock(frontPos, Blocks.WATER.defaultBlockState(), 35);
      } else {
        level.setBlock(frontPos, Blocks.AIR.defaultBlockState(), 35);
      }
      HammockBlock.dropRopeSupport(otherPos, frontDirection, !isHead, level);
    }
  }

  @Override
  public void playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos,
                                @Nonnull BlockState state, @Nonnull Player player) {
    dropHammock(level, pos, state);
    super.playerWillDestroy(level, pos, state, player);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    final FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
    BlockState blockstate = this.defaultBlockState();
    final LevelReader worldreader = context.getLevel();
    final BlockPos blockpos = context.getClickedPos();
    final Direction[] directions = context.getNearestLookingDirections();

    for (Direction direction : directions) {

      if (direction.getAxis().isHorizontal()) {
        final Direction direction1 = direction.getOpposite();
        blockstate = blockstate.setValue(HORIZONTAL_FACING, direction1)
            .setValue(BaseComfortsBlock.WATERLOGGED, ifluidstate.getType() == Fluids.WATER);

        if (blockstate.canSurvive(worldreader, blockpos)) {
          return blockstate;
        }
      }
    }
    return null;
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public BlockState updateShape(@Nonnull BlockState stateIn, @Nonnull Direction facing,
                                @Nonnull BlockState facingState, @Nonnull LevelAccessor level,
                                @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {

    if (stateIn.getValue(BaseComfortsBlock.WATERLOGGED)) {
      level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }
    return facing.getOpposite() == stateIn.getValue(HORIZONTAL_FACING) && !stateIn
        .canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public BlockState rotate(@Nonnull BlockState state, Rotation rot) {
    return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public BlockState mirror(@Nonnull BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(SUPPORTING, HORIZONTAL_FACING, BaseComfortsBlock.WATERLOGGED);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(BaseComfortsBlock.WATERLOGGED) ? Fluids.WATER.getSource(false)
        : super.getFluidState(state);
  }
}
