package top.theillusivec4.comforts.common.block;

import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class RopeAndNailBlock extends Block implements Waterloggable {

  public static final BooleanProperty SUPPORTING = BooleanProperty.of("supporting");

  private static final Map<Direction, VoxelShape> SHAPES_R = new EnumMap<>(ImmutableMap
      .of(Direction.NORTH, Block.createCuboidShape(6.0D, 0.0D, 12.0D, 10.0D, 8.0D, 16.0D),
          Direction.SOUTH, Block.createCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 8.0D, 4.0D),
          Direction.WEST, Block.createCuboidShape(12.0D, 0.0D, 6.0D, 16.0D, 8.0D, 10.0D),
          Direction.EAST, Block.createCuboidShape(0.0D, 0.0D, 6.0D, 4.0D, 8.0D, 10.0D)));

  private static final Map<Direction, VoxelShape> SHAPES_S = new EnumMap<>(ImmutableMap
      .of(Direction.NORTH, Block.createCuboidShape(6.0D, 3.0D, 9.0D, 10.0D, 8.0D, 16.0D),
          Direction.SOUTH, Block.createCuboidShape(6.0D, 3.0D, 0.0D, 10.0D, 8.0D, 7.0D),
          Direction.WEST, Block.createCuboidShape(9.0D, 3.0D, 6.0D, 16.0D, 8.0D, 10.0D),
          Direction.EAST, Block.createCuboidShape(0.0D, 3.0D, 6.0D, 7.0D, 8.0D, 10.0D)));

  public RopeAndNailBlock() {
    super(AbstractBlock.Settings.of(Material.WOOL).sounds(BlockSoundGroup.METAL).strength(0.2F));
    this.setDefaultState(
        this.getStateManager().getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(SUPPORTING, false));
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
                                    ShapeContext context) {
    Map<Direction, VoxelShape> shape = state.get(SUPPORTING) ? SHAPES_S : SHAPES_R;
    return shape.get(state.get(HorizontalFacingBlock.FACING));
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
    final Direction direction = state.get(HorizontalFacingBlock.FACING);
    final BlockPos blockpos = pos.offset(direction.getOpposite());
    final BlockState blockstate = world.getBlockState(blockpos);
    final boolean valid = blockstate.isSideSolidFullSquare(world, blockpos, direction);

    if (!valid && world instanceof ServerWorld serverWorld) {
      dropHammock(serverWorld, pos, state);
    }
    return valid;
  }

  private static void dropHammock(World world, BlockPos pos, BlockState state) {
    final BlockPos frontPos = pos.offset(state.get(HorizontalFacingBlock.FACING));
    final BlockState frontState = world.getBlockState(frontPos);

    if (state.get(SUPPORTING) && frontState.getBlock() instanceof HammockBlock) {
      final BedPart bedpart = frontState.get(BedBlock.PART);
      final boolean isHead = bedpart == BedPart.HEAD;
      final Direction frontDirection = frontState.get(HorizontalFacingBlock.FACING);
      final BlockPos otherPos = frontPos
          .offset(HammockBlock.getDirectionToOther(bedpart, frontDirection));

      if (isHead) {
        dropStacks(frontState, world, frontPos);
      }

      if (frontState.get(AbstractComfortsBlock.WATERLOGGED)) {
        world.setBlockState(frontPos, Blocks.WATER.getDefaultState(), 35);
      } else {
        world.setBlockState(frontPos, Blocks.AIR.getDefaultState(), 35);
      }
      HammockBlock.dropRopeSupport(otherPos, frontDirection, !isHead, world);
    }
  }

  @Override
  public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    dropHammock(world, pos, state);
    super.onBreak(world, pos, state, player);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    final FluidState ifluidstate = ctx.getWorld().getFluidState(ctx.getBlockPos());
    BlockState blockstate = this.getDefaultState();
    final World world = ctx.getWorld();
    final BlockPos blockpos = ctx.getBlockPos();
    final Direction[] directions = ctx.getPlacementDirections();

    for (Direction direction : directions) {

      if (direction.getAxis().isHorizontal()) {
        final Direction direction1 = direction.getOpposite();
        blockstate = blockstate.with(HorizontalFacingBlock.FACING, direction1)
            .with(AbstractComfortsBlock.WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);

        if (blockstate.canPlaceAt(world, blockpos)) {
          return blockstate;
        }
      }
    }
    return null;
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
                                              BlockState newState, WorldAccess world, BlockPos pos,
                                              BlockPos posFrom) {
    if (state.get(AbstractComfortsBlock.WATERLOGGED)) {
      world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
    }
    return direction.getOpposite() == state.get(HorizontalFacingBlock.FACING) &&
        !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState rotate(BlockState state, BlockRotation rot) {
    return state
        .with(HorizontalFacingBlock.FACING, rot.rotate(state.get(HorizontalFacingBlock.FACING)));
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
    return state.rotate(mirrorIn.getRotation(state.get(HorizontalFacingBlock.FACING)));
  }

  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(SUPPORTING, HorizontalFacingBlock.FACING, AbstractComfortsBlock.WATERLOGGED);
    super.appendProperties(builder);
  }

  @SuppressWarnings("deprecation")
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(AbstractComfortsBlock.WATERLOGGED) ? Fluids.WATER.getStill(false) :
        super.getFluidState(state);
  }
}
