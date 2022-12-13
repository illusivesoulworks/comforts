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

import com.illusivesoulworks.comforts.common.ComfortsConfig;
import com.illusivesoulworks.comforts.mixin.AccessorPlayer;
import com.illusivesoulworks.comforts.platform.Services;
import com.mojang.datafixers.util.Either;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class BaseComfortsBlock extends BedBlock implements SimpleWaterloggedBlock {

  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  private final BedType type;

  public BaseComfortsBlock(final BedType type, DyeColor colorIn, Block.Properties properties) {
    super(colorIn, properties);
    this.type = type;
    this.registerDefaultState(
        this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, false)
            .setValue(WATERLOGGED, false));
  }

  private static Direction getDirectionToOther(BedPart part, Direction direction) {
    return part == BedPart.FOOT ? direction : direction.getOpposite();
  }

  @Nonnull
  @Override
  public InteractionResult use(@Nonnull BlockState state, Level level,
                               @Nonnull BlockPos pos, @Nonnull Player player,
                               @Nonnull InteractionHand handIn, @Nonnull BlockHitResult hit) {

    if (level.isClientSide) {
      return InteractionResult.CONSUME;
    } else {

      if (state.getValue(PART) != BedPart.HEAD) {
        pos = pos.relative(state.getValue(FACING));
        state = level.getBlockState(pos);

        if (!state.is(this)) {
          return InteractionResult.CONSUME;
        }
      }

      if (!canSetSpawn(level)) {
        level.removeBlock(pos, false);
        final BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());

        if (level.getBlockState(blockpos).is(this)) {
          level.removeBlock(blockpos, false);
        }
        level.explode(null, DamageSource.badRespawnPointExplosion(pos.getCenter()), null,
            pos.getCenter(), 5.0F, true, Level.ExplosionInteraction.BLOCK);
        return InteractionResult.SUCCESS;
      } else if (state.getValue(OCCUPIED)) {

        if (!this.kickVillagerOutOfBed(level, pos)) {
          player.displayClientMessage(
              Component.translatable("block.comforts." + this.type.name + ".occupied"), true);
        }
        return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer serverPlayer) {
        trySleep(serverPlayer, pos).ifLeft((result) -> {

          if (result != null) {
            final Component text = switch (result) {
              case NOT_POSSIBLE_NOW -> {
                if (type == BedType.HAMMOCK) {
                  String key = "block.comforts." + type.name + ".no_sleep";

                  if (ComfortsConfig.SERVER.nightHammocks.get()) {
                    key += ".2";
                  }
                  yield Component.translatable(key);
                }
                yield Component.translatable("block.minecraft.bed.no_sleep");
              }
              case TOO_FAR_AWAY -> Component.translatable(
                  "block.comforts." + type.name + ".too_far_away");
              default -> result.getMessage();
            };

            if (text != null) {
              player.displayClientMessage(text, true);
            }
          }
        });
      }
    }
    return InteractionResult.SUCCESS;
  }

  public static Either<Player.BedSleepingProblem, Unit> trySleep(ServerPlayer player, BlockPos at) {
    final Player.BedSleepingProblem ret = Services.SLEEP_EVENTS.getSleepResult(player, at);

    if (ret != null) {
      return Either.left(ret);
    }
    final Direction direction = player.level.getBlockState(at)
        .getValue(HorizontalDirectionalBlock.FACING);

    if (!player.isSleeping() && player.isAlive()) {

      if (!player.level.dimensionType().natural()) {
        return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
      } else if (!bedInRange(player, at, direction)) {
        return Either.left(Player.BedSleepingProblem.TOO_FAR_AWAY);
      } else if (bedBlocked(player, at, direction)) {
        return Either.left(Player.BedSleepingProblem.OBSTRUCTED);
      } else {

        if (Services.SLEEP_EVENTS.isAwakeTime(player, at)) {
          return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
        } else {

          if (!player.isCreative()) {
            final double d0 = 8.0D;
            final double d1 = 5.0D;
            final Vec3 vector3d = Vec3.atBottomCenterOf(at);
            List<Monster> list = player.level.getEntitiesOfClass(Monster.class,
                new AABB(vector3d.x() - d0, vector3d.y() - d1, vector3d.z() - d0, vector3d.x() + d0,
                    vector3d.y() + d1, vector3d.z() + d0),
                (monster) -> monster.isPreventingPlayerRest(player));

            if (!list.isEmpty()) {
              return Either.left(Player.BedSleepingProblem.NOT_SAFE);
            }
          }
          player.startSleeping(at);
          ((AccessorPlayer) player).setSleepCounter(0);
          player.awardStat(Stats.SLEEP_IN_BED);
          CriteriaTriggers.SLEPT_IN_BED.trigger(player);
          ((ServerLevel) player.level).updateSleepingPlayerList();
          return Either.right(Unit.INSTANCE);
        }
      }
    } else {
      return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
    }
  }

  private static boolean bedInRange(ServerPlayer playerEntity, BlockPos blockPos,
                                    Direction direction) {
    if (direction == null) {
      return false;
    }
    return isReachableBedBlock(playerEntity, blockPos) || isReachableBedBlock(playerEntity,
        blockPos.relative(direction.getOpposite()));
  }

  private static boolean isReachableBedBlock(ServerPlayer playerEntity, BlockPos blockPos) {
    final Vec3 vector3d = Vec3.atBottomCenterOf(blockPos);
    return Math.abs(playerEntity.getX() - vector3d.x()) <= 3.0D
        && Math.abs(playerEntity.getY() - vector3d.y()) <= 2.0D
        && Math.abs(playerEntity.getZ() - vector3d.z()) <= 3.0D;
  }

  private static boolean bedBlocked(ServerPlayer playerEntity, BlockPos blockPos,
                                    Direction direction) {
    final BlockPos blockpos = blockPos.above();
    return isAbnormalCube(playerEntity.level, blockpos) || isAbnormalCube(playerEntity.level,
        blockpos.relative(direction.getOpposite()));
  }

  private static boolean isAbnormalCube(Level world, BlockPos pos) {
    return world.getBlockState(pos).isSuffocating(world, pos);
  }

  private boolean kickVillagerOutOfBed(Level level, BlockPos blockPos) {
    List<Villager> list =
        level.getEntitiesOfClass(Villager.class, new AABB(blockPos), LivingEntity::isSleeping);

    if (list.isEmpty()) {
      return false;
    } else {
      list.get(0).stopSleeping();
      return true;
    }
  }

  @Override
  public void playerWillDestroy(Level level, @Nonnull BlockPos pos, @Nonnull BlockState state,
                                @Nonnull Player player) {

    if (!level.isClientSide && player.isCreative()) {
      final BedPart bedpart = state.getValue(PART);

      if (bedpart == BedPart.FOOT) {
        final BlockPos blockpos =
            pos.relative(getDirectionToOther(bedpart, state.getValue(FACING)));
        final BlockState blockstate = level.getBlockState(blockpos);

        if (blockstate.getBlock() == this && blockstate.getValue(PART) == BedPart.HEAD) {

          if (blockstate.getValue(WATERLOGGED)) {
            level.setBlock(blockpos, Blocks.WATER.defaultBlockState(), 35);
          } else {
            level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
          }
          level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
        }
      }
    }
    level.levelEvent(player, 2001, pos, getId(state));

    if (state.is(BlockTags.GUARDED_BY_PIGLINS)) {
      PiglinAi.angerNearbyPiglins(player, false);
    }
  }

  @Nonnull
  @Override
  public BlockState updateShape(BlockState stateIn, @Nonnull Direction facing,
                                @Nonnull BlockState facingState, @Nonnull LevelAccessor level,
                                @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {

    if (stateIn.getValue(WATERLOGGED)) {
      level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }

    return super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    final FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
    final BlockState state = super.getStateForPlacement(context);
    return state == null ? null :
        state.setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(WATERLOGGED);
    super.createBlockStateDefinition(builder);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false)
        : super.getFluidState(state);
  }

  enum BedType {
    HAMMOCK("hammock"), SLEEPING_BAG("sleeping_bag");

    private final String name;

    BedType(String name) {
      this.name = name;
    }
  }
}
