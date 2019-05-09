package top.theillusivec4.comforts.common.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import top.theillusivec4.comforts.Comforts;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.block.BlockBed.PART;

public class BlockRopeAndNail extends Block {

    public static final DirectionProperty HORIZONTAL_FACING = BlockHorizontal.HORIZONTAL_FACING;
    public static final BooleanProperty SUPPORTING = BooleanProperty.create("supporting");

    private static final Map<EnumFacing, VoxelShape> SHAPES_R = new EnumMap<>(ImmutableMap.of(
            EnumFacing.NORTH, Block.makeCuboidShape(6.0D, 0.0D, 12.0D, 10.0D, 8.0D, 16.0D),
            EnumFacing.SOUTH, Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 8.0D, 4.0D),
            EnumFacing.WEST, Block.makeCuboidShape(12.0D, 0.0D, 6.0D, 16.0D, 8.0D, 10.0D),
            EnumFacing.EAST, Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 4.0D, 8.0D, 10.0D)));

    private static final Map<EnumFacing, VoxelShape> SHAPES_S = new EnumMap<>(ImmutableMap.of(
            EnumFacing.NORTH, Block.makeCuboidShape(6.0D, 3.0D, 9.0D, 10.0D, 8.0D, 16.0D),
            EnumFacing.SOUTH, Block.makeCuboidShape(6.0D, 3.0D, 0.0D, 10.0D, 8.0D, 7.0D),
            EnumFacing.WEST, Block.makeCuboidShape(9.0D, 3.0D, 6.0D, 16.0D, 8.0D, 10.0D),
            EnumFacing.EAST, Block.makeCuboidShape(0.0D, 3.0D, 6.0D, 7.0D, 8.0D, 10.0D)));

    public BlockRopeAndNail() {
        super(Block.Properties.create(Material.CLOTH).sound(SoundType.METAL).hardnessAndResistance(0.2F));
        this.setRegistryName(Comforts.MODID, "rope_and_nail");
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(SUPPORTING, false));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.get(SUPPORTING) ? SHAPES_S.get(state.get(HORIZONTAL_FACING)) : SHAPES_R.get(state.get(HORIZONTAL_FACING));
    }

    @Override
    public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
        EnumFacing enumfacing = state.get(HORIZONTAL_FACING);
        BlockPos blockpos = pos.offset(enumfacing.getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape(worldIn, blockpos, enumfacing) == BlockFaceShape.SOLID && !isExceptBlockForAttachWithPiston(iblockstate.getBlock());
    }

    @Override
    public void onBlockHarvested(World worldIn, @Nonnull BlockPos pos, IBlockState state, @Nonnull EntityPlayer player) {
        BlockPos frontpos = pos.offset(state.get(HORIZONTAL_FACING));
        IBlockState frontstate = worldIn.getBlockState(frontpos);

        if (state.get(SUPPORTING) && frontstate.getBlock() instanceof BlockHammock) {
            BedPart bedpart = frontstate.get(PART);
            boolean flag = bedpart == BedPart.HEAD;
            EnumFacing facing = frontstate.get(HORIZONTAL_FACING);
            BlockPos blockpos = frontpos.offset(BlockHammock.getDirectionToOther(bedpart, facing));
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            worldIn.setBlockState(frontpos, Blocks.AIR.getDefaultState(), 35);
            worldIn.playEvent(player, 2001, frontpos, Block.getStateId(frontstate));

            if (iblockstate.getBlock() instanceof BlockHammock && iblockstate.get(PART) != bedpart) {
                worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                worldIn.playEvent(player, 2001, blockpos, Block.getStateId(iblockstate));
                BlockPos posotherrope = flag ? frontpos.offset(facing.getOpposite(), 2) : frontpos.offset(facing, 2);
                IBlockState otherrope = worldIn.getBlockState(posotherrope);

                if (otherrope.getBlock() instanceof BlockRopeAndNail) {
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

        for(EnumFacing enumfacing : aenumfacing) {

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
    public IBlockState updatePostPlacement(@Nonnull IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing.getOpposite() == stateIn.get(HORIZONTAL_FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
    }

    @Nonnull
    @Override
    public IBlockState rotate(@Nonnull IBlockState state, Rotation rot) {
        return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
    }

    @Nonnull
    @Override
    public IBlockState mirror(@Nonnull IBlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
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
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
