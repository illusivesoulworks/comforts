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

import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.comforts.Comforts;

public class RopeAndNailBlock extends Block implements IWaterLoggable {

  public static final DirectionProperty HORIZONTAL_FACING = HorizontalBlock.HORIZONTAL_FACING;
  public static final BooleanProperty SUPPORTING = BooleanProperty.create("supporting");

  private static final Map<Direction, VoxelShape> SHAPES_R = new EnumMap<>(ImmutableMap
      .of(Direction.NORTH, Block.makeCuboidShape(6.0D, 0.0D, 12.0D, 10.0D, 8.0D, 16.0D),
          Direction.SOUTH, Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 8.0D, 4.0D),
          Direction.WEST, Block.makeCuboidShape(12.0D, 0.0D, 6.0D, 16.0D, 8.0D, 10.0D),
          Direction.EAST, Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 4.0D, 8.0D, 10.0D)));

  private static final Map<Direction, VoxelShape> SHAPES_S = new EnumMap<>(ImmutableMap
      .of(Direction.NORTH, Block.makeCuboidShape(6.0D, 3.0D, 9.0D, 10.0D, 8.0D, 16.0D),
          Direction.SOUTH, Block.makeCuboidShape(6.0D, 3.0D, 0.0D, 10.0D, 8.0D, 7.0D),
          Direction.WEST, Block.makeCuboidShape(9.0D, 3.0D, 6.0D, 16.0D, 8.0D, 10.0D),
          Direction.EAST, Block.makeCuboidShape(0.0D, 3.0D, 6.0D, 7.0D, 8.0D, 10.0D)));

  public RopeAndNailBlock() {
    super(
        Block.Properties.create(Material.WOOL).sound(SoundType.METAL).hardnessAndResistance(0.2F));
    this.setRegistryName(Comforts.MODID, "rope_and_nail");
    this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH)
        .with(SUPPORTING, false));
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
      ISelectionContext context) {
    return state.get(SUPPORTING) ? SHAPES_S.get(state.get(HORIZONTAL_FACING))
        : SHAPES_R.get(state.get(HORIZONTAL_FACING));
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    Direction direction = state.get(HORIZONTAL_FACING);
    BlockPos blockpos = pos.offset(direction.getOpposite());
    BlockState blockstate = worldIn.getBlockState(blockpos);
    boolean valid = blockstate.isSolidSide(worldIn, blockpos, direction);

    if (!valid && worldIn instanceof ServerWorld) {
      ServerWorld world = (ServerWorld) worldIn;
      BlockPos frontPos = pos.offset(state.get(HORIZONTAL_FACING));
      BlockState frontState = worldIn.getBlockState(frontPos);

      if (state.get(SUPPORTING) && frontState.getBlock() instanceof HammockBlock) {
        BedPart bedpart = frontState.get(BedBlock.PART);
        boolean isHead = bedpart == BedPart.HEAD;
        Direction frontDirection = frontState.get(HORIZONTAL_FACING);
        BlockPos otherPos = frontPos
            .offset(HammockBlock.getDirectionToOther(bedpart, frontDirection));
        BlockState otherState = world.getBlockState(otherPos);

        if (frontState.get(ComfortsBaseBlock.WATERLOGGED)) {
          world.setBlockState(frontPos, Blocks.WATER.getDefaultState(), 35);
        } else {
          world.setBlockState(frontPos, Blocks.AIR.getDefaultState(), 35);
        }

        if (otherState.getBlock() instanceof HammockBlock
            && otherState.get(BedBlock.PART) != bedpart) {
          HammockBlock
              .finishHammockDrops(frontState, frontPos, otherState, otherPos, frontDirection, world,
                  null);
          HammockBlock.dropRopeSupport(frontPos, frontDirection, isHead, world);
        }
      }
    }
    return valid;
  }

  @Override
  public void onBlockHarvested(World worldIn, @Nonnull BlockPos pos, BlockState state,
      @Nonnull PlayerEntity player) {
    BlockPos frontPos = pos.offset(state.get(HORIZONTAL_FACING));
    BlockState frontState = worldIn.getBlockState(frontPos);

    if (state.get(SUPPORTING) && frontState.getBlock() instanceof HammockBlock) {
      BedPart bedpart = frontState.get(BedBlock.PART);
      boolean isHead = bedpart == BedPart.HEAD;
      Direction direction = frontState.get(HORIZONTAL_FACING);
      BlockPos otherPos = frontPos.offset(HammockBlock.getDirectionToOther(bedpart, direction));
      BlockState otherState = worldIn.getBlockState(otherPos);

      if (frontState.get(ComfortsBaseBlock.WATERLOGGED)) {
        worldIn.setBlockState(frontPos, Blocks.WATER.getDefaultState(), 35);
      } else {
        worldIn.setBlockState(frontPos, Blocks.AIR.getDefaultState(), 35);
      }
      worldIn.playEvent(player, 2001, frontPos, Block.getStateId(frontState));

      if (otherState.getBlock() instanceof HammockBlock
          && otherState.get(BedBlock.PART) != bedpart) {
        HammockBlock
            .finishHammockDrops(frontState, frontPos, otherState, otherPos, direction, worldIn,
                player);
        HammockBlock.dropRopeSupport(frontPos, direction, isHead, worldIn);
        player.addStat(Stats.BLOCK_MINED.get(frontState.getBlock()));
      }
    }
    super.onBlockHarvested(worldIn, pos, state, player);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
    BlockState blockstate = this.getDefaultState();
    IWorldReader worldreader = context.getWorld();
    BlockPos blockpos = context.getPos();
    Direction[] directions = context.getNearestLookingDirections();

    for (Direction direction : directions) {

      if (direction.getAxis().isHorizontal()) {
        Direction direction1 = direction.getOpposite();
        blockstate = blockstate.with(HORIZONTAL_FACING, direction1)
            .with(ComfortsBaseBlock.WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);

        if (blockstate.isValidPosition(worldreader, blockpos)) {
          return blockstate;
        }
      }
    }
    return null;
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public BlockState updatePostPlacement(@Nonnull BlockState stateIn, Direction facing,
      BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {

    if (stateIn.get(ComfortsBaseBlock.WATERLOGGED)) {
      worldIn.getPendingFluidTicks()
          .scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
    }
    return facing.getOpposite() == stateIn.get(HORIZONTAL_FACING) && !stateIn
        .isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public BlockState rotate(@Nonnull BlockState state, Rotation rot) {
    return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public BlockState mirror(@Nonnull BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(SUPPORTING, HORIZONTAL_FACING, ComfortsBaseBlock.WATERLOGGED);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public IFluidState getFluidState(BlockState state) {
    return state.get(ComfortsBaseBlock.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false)
        : super.getFluidState(state);
  }
}
