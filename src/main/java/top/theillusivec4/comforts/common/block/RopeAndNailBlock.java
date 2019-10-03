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

import static net.minecraft.block.BlockBed.PART;

import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI.D;
import top.theillusivec4.comforts.Comforts;

public class RopeAndNailBlock extends Block {

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

  @Nonnull
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return state.get(SUPPORTING) ? SHAPES_S.get(state.get(HORIZONTAL_FACING))
        : SHAPES_R.get(state.get(HORIZONTAL_FACING));
  }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    Direction direction = state.get(HORIZONTAL_FACING);
    BlockPos blockpos = pos.offset(direction.getOpposite());
    BlockState iblockstate = worldIn.getBlockState(blockpos);
    return iblockstate.getShape(worldIn, blockpos) == BlockFaceShape.SOLID
        && !isExceptBlockForAttachWithPiston(iblockstate.getBlock());
  }

  @Override
  public void onBlockHarvested(World worldIn, @Nonnull BlockPos pos, IBlockState state,
      @Nonnull EntityPlayer player) {
    BlockPos frontpos = pos.offset(state.get(HORIZONTAL_FACING));
    IBlockState frontstate = worldIn.getBlockState(frontpos);

    if (state.get(SUPPORTING) && frontstate.getBlock() instanceof HammockBlock) {
      BedPart bedpart = frontstate.get(PART);
      boolean flag = bedpart == BedPart.HEAD;
      EnumFacing facing = frontstate.get(HORIZONTAL_FACING);
      BlockPos blockpos = frontpos.offset(HammockBlock.getDirectionToOther(bedpart, facing));
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      worldIn.setBlockState(frontpos, Blocks.AIR.getDefaultState(), 35);
      worldIn.playEvent(player, 2001, frontpos, Block.getStateId(frontstate));

      if (iblockstate.getBlock() instanceof HammockBlock && iblockstate.get(PART) != bedpart) {
        worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
        worldIn.playEvent(player, 2001, blockpos, Block.getStateId(iblockstate));
        BlockPos posotherrope =
            flag ? frontpos.offset(facing.getOpposite(), 2) : frontpos.offset(facing, 2);
        IBlockState otherrope = worldIn.getBlockState(posotherrope);

        if (otherrope.getBlock() instanceof RopeAndNailBlock) {
          worldIn.setBlockState(posotherrope, otherrope.with(SUPPORTING, false));
        }

        if (!worldIn.isRemote && !player.isCreative()) {

          if (flag) {
            frontstate.dropBlockAsItem(worldIn, frontpos, 0);
          } else {
            iblockstate.dropBlockAsItem(worldIn, blockpos, 0);
          }
        }
        player.addStat(StatList.BLOCK_MINED.get(frontstate.getBlock()));
      }
    }
    super.onBlockHarvested(worldIn, pos, state, player);
  }

  @Nullable
  @Override
  public IBlockState getStateForPlacement(BlockItemUseContext context) {
    IBlockState iblockstate = this.getDefaultState();
    IWorldReaderBase iworldreaderbase = context.getWorld();
    BlockPos blockpos = context.getPos();
    EnumFacing[] aenumfacing = context.getNearestLookingDirections();

    for (EnumFacing enumfacing : aenumfacing) {

      if (enumfacing.getAxis().isHorizontal()) {
        EnumFacing enumfacing1 = enumfacing.getOpposite();
        iblockstate = iblockstate.with(HORIZONTAL_FACING, enumfacing1);

        if (iblockstate.isValidPosition(iworldreaderbase, blockpos)) {
          return iblockstate;
        }
      }
    }
    return null;
  }

  @Nonnull
  @Override
  public BlockState updatePostPlacement(@Nonnull BlockState stateIn, Direction facing,
      BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    return facing.getOpposite() == stateIn.get(HORIZONTAL_FACING) && !stateIn
        .isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
  }

  @Nonnull
  @Override
  public BlockState rotate(@Nonnull BlockState state, Rotation rot) {
    return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
  }

  @Nonnull
  @Override
  public BlockState mirror(@Nonnull BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(SUPPORTING);
    builder.add(HORIZONTAL_FACING);
  }

  @Nonnull
  @Override
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Nonnull
  @Override
  public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos,
      Direction face) {
    return BlockFaceShape.UNDEFINED;
  }
}
