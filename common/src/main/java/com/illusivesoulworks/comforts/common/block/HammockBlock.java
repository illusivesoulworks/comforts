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

import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.comforts.common.block.entity.BaseComfortsBlockEntity;
import com.illusivesoulworks.comforts.common.block.entity.HammockBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HammockBlock extends BaseComfortsBlock {

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
      .or(Block.box(0.0D, 0.0D, 1.0D, 16.0D, 1.0D, 15.0D),
          Block.box(15.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D));
  private final DyeColor color;

  public HammockBlock(DyeColor color) {
    super(BaseComfortsBlock.BedType.HAMMOCK, color,
        Block.Properties.of().ignitedByLava().mapColor(MapColor.WOOL).sound(SoundType.WOOL)
            .strength(0.1F));
    this.color = color;
  }

  public static Direction getDirectionToOther(BedPart part, Direction facing) {
    return part == BedPart.FOOT ? facing : facing.getOpposite();
  }

  public static void dropRopeSupport(BlockPos pos, Direction direction, boolean isHead,
                                     Level level) {
    BlockPos ropePos = isHead ? pos.relative(direction) : pos.relative(direction.getOpposite());
    BlockState ropeState = level.getBlockState(ropePos);

    if (ropeState.getBlock() instanceof RopeAndNailBlock) {
      level.setBlockAndUpdate(ropePos, ropeState.setValue(RopeAndNailBlock.SUPPORTING, false));
    }
  }

  @Nonnull
  @Override
  public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level,
                             @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
    final Direction direction = getConnectedDirection(state).getOpposite();
    return switch (direction) {
      case NORTH -> NORTH_SHAPE;
      case SOUTH -> SOUTH_SHAPE;
      case WEST -> WEST_SHAPE;
      case EAST -> EAST_SHAPE;
      default -> HAMMOCK_SHAPE;
    };
  }

  @Override
  public void playerWillDestroy(Level level, @Nonnull BlockPos pos, @Nonnull BlockState state,
                                @Nonnull Player player) {
    super.playerWillDestroy(level, pos, state, player);
    final BedPart bedpart = state.getValue(PART);
    final boolean isHead = bedpart == BedPart.HEAD;
    final Direction direction = state.getValue(FACING);
    final BlockPos otherPos = pos.relative(getDirectionToOther(bedpart, direction));
    dropRopeSupport(pos, direction, isHead, level);
    dropRopeSupport(otherPos, direction, !isHead, level);
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
        .setValue(BaseComfortsBlock.WATERLOGGED, ifluidstate.getType() == Fluids.WATER) : null;
  }

  @Override
  public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
    return new HammockBlockEntity(pos, state, this.color);
  }

  @Override
  public BlockEntityType<? extends BaseComfortsBlockEntity> getBlockEntityType() {
    return ComfortsRegistry.HAMMOCK_BLOCK_ENTITY.get();
  }
}
