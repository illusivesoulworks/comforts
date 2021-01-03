package top.theillusivec4.comforts.common.block;

import static top.theillusivec4.comforts.common.block.RopeAndNailBlock.SUPPORTING;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import top.theillusivec4.comforts.common.block.entity.HammockBlockEntity;

public class HammockBlock extends AbstractComfortsBlock {

  private static final VoxelShape HAMMOCK_SHAPE = Block
      .createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
  private static final VoxelShape NORTH_SHAPE = VoxelShapes
      .union(Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 16.0D),
          Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 1.0D));
  private static final VoxelShape SOUTH_SHAPE = VoxelShapes
      .union(Block.createCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 1.0D, 15.0D),
          Block.createCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 1.0D, 16.0D));
  private static final VoxelShape WEST_SHAPE = VoxelShapes
      .union(Block.createCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 1.0D, 15.0D),
          Block.createCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 16.0D));
  private static final VoxelShape EAST_SHAPE = VoxelShapes
      .union(Block.createCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 1.0D, 15.0D),
          Block.createCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D));

  public HammockBlock(DyeColor color) {
    super(BedType.HAMMOCK, color);
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

  @Override
  public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    super.onBreak(world, pos, state, player);
    final BedPart bedpart = state.get(PART);
    final boolean isHead = bedpart == BedPart.HEAD;
    final Direction direction = state.get(HorizontalFacingBlock.FACING);
    final BlockPos otherPos = pos.offset(getDirectionToOther(bedpart, direction));
    dropRopeSupport(pos, direction, isHead, world);
    dropRopeSupport(otherPos, direction, !isHead, world);
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
                                    ShapeContext context) {
    final Direction direction = getOppositePartDirection(state).getOpposite();
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
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    final Direction direction = ctx.getSide();
    final BlockPos blockpos = ctx.getBlockPos();
    final BlockPos blockpos1 = blockpos.offset(direction);
    final FluidState ifluidstate = ctx.getWorld().getFluidState(blockpos);
    return ctx.getWorld().getBlockState(blockpos1).canReplace(ctx) ?
        this.getDefaultState().with(HorizontalFacingBlock.FACING, direction)
            .with(AbstractComfortsBlock.WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER) : null;
  }

  @Override
  public BlockEntity createBlockEntity(BlockView world) {
    return new HammockBlockEntity(this.color);
  }
}
