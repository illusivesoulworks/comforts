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

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BedPart;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
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

  public static boolean skipToNight(ServerWorld world) {
    final boolean[] skipToNight = {false};
    List<ServerPlayerEntity> players = world.getPlayers();

    for (PlayerEntity player : players) {
      player.getBedPosition().ifPresent(bedPos -> {
        if (player.isPlayerFullyAsleep() && world.getBlockState(bedPos)
            .getBlock() instanceof HammockBlock) {

          if (world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            long i = world.getDayTime() + 24000L;
            long worldTime = world.getDayTime() % 24000L;

            if (worldTime > 500L && worldTime < 11500L) {
              world.setDayTime((i - i % 24000L) - 12001L);
            }
          }

          skipToNight[0] = true;
        }
      });

      if (skipToNight[0]) {
        break;
      }
    }

    if (skipToNight[0]) {
      ObfuscationReflectionHelper.setPrivateValue(ServerWorld.class, world, false, "field_73068_P");
      players.stream().filter(LivingEntity::isSleeping)
          .forEach((player) -> player.func_225652_a_(false, false));

      if (world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
        world.dimension.resetRainAndThunder();
      }
      return true;
    } else {
      return false;
    }
  }

  public static void dropRopeSupport(BlockPos pos, Direction direction, boolean isHead,
      World worldIn) {
    BlockPos ropePos = isHead ? pos.offset(direction) : pos.offset(direction.getOpposite());
    BlockState ropeState = worldIn.getBlockState(ropePos);

    if (ropeState.getBlock() instanceof RopeAndNailBlock) {
      worldIn.setBlockState(ropePos, ropeState.with(SUPPORTING, false));
    }
  }

  @SuppressWarnings("ConstantConditions")
  public static void finishHammockDrops(BlockState state, BlockPos pos, BlockState otherState,
      BlockPos otherPos, Direction direction, World worldIn, @Nullable PlayerEntity player) {
    BedPart bedpart = otherState.get(BedBlock.PART);
    boolean isHead = bedpart == BedPart.HEAD;

    if (otherState.get(ComfortsBaseBlock.WATERLOGGED)) {
      worldIn.setBlockState(otherPos, Blocks.WATER.getDefaultState(), 35);
    } else {
      worldIn.setBlockState(otherPos, Blocks.AIR.getDefaultState(), 35);
    }

    worldIn.playEvent(player, 2001, otherPos, Block.getStateId(otherState));
    dropRopeSupport(otherPos, direction, isHead, worldIn);

    if (!worldIn.isRemote) {
      ItemStack itemstack = ItemStack.EMPTY;

      if (player != null) {

        if (player.isCreative()) {
          return;
        }
        itemstack = player.getHeldItemMainhand();
      }
      spawnDrops(state, worldIn, pos, null, player, itemstack);
      spawnDrops(otherState, worldIn, otherPos, null, player, itemstack);
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
  public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state,
      @Nonnull PlayerEntity player) {
    BedPart bedpart = state.get(PART);
    boolean isHead = bedpart == BedPart.HEAD;
    Direction direction = state.get(HORIZONTAL_FACING);
    BlockPos otherPos = pos.offset(getDirectionToOther(bedpart, direction));
    BlockState otherState = worldIn.getBlockState(otherPos);

    if (otherState.getBlock() == this && otherState.get(PART) != bedpart) {
      finishHammockDrops(state, pos, otherState, otherPos, direction, worldIn, player);
      dropRopeSupport(pos, direction, isHead, worldIn);
      player.addStat(Stats.BLOCK_MINED.get(this));
    }
    worldIn.playEvent(player, 2001, pos, Block.getStateId(state));
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction direction = context.getFace();
    BlockPos blockpos = context.getPos();
    BlockPos blockpos1 = blockpos.offset(direction);
    IFluidState ifluidstate = context.getWorld().getFluidState(blockpos);
    return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this
        .getDefaultState().with(HORIZONTAL_FACING, direction)
        .with(ComfortsBaseBlock.WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER) : null;
  }

  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new HammockTileEntity(this.color);
  }
}
