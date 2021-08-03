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

import com.mojang.datafixers.util.Either;
import java.lang.reflect.Field;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import top.theillusivec4.comforts.ComfortsMod;

public class ComfortsBaseBlock extends BedBlock implements SimpleWaterloggedBlock {

  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  private static final Field SLEEP_TIMER = ObfuscationReflectionHelper
      .findField(Player.class, "f_36110_");

  private final BedType type;

  public ComfortsBaseBlock(final BedType type, DyeColor colorIn, Block.Properties properties) {
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
  public InteractionResult use(@Nonnull BlockState state, Level worldIn,
                               @Nonnull BlockPos pos, @Nonnull Player player,
                               InteractionHand handIn, BlockHitResult hit) {

    if (worldIn.isClientSide) {
      return InteractionResult.CONSUME;
    } else {

      if (state.getValue(PART) != BedPart.HEAD) {
        pos = pos.relative(state.getValue(FACING));
        state = worldIn.getBlockState(pos);

        if (!state.is(this)) {
          return InteractionResult.CONSUME;
        }
      }

      if (!canSetSpawn(worldIn)) {
        worldIn.removeBlock(pos, false);
        final BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());

        if (worldIn.getBlockState(blockpos).is(this)) {
          worldIn.removeBlock(blockpos, false);
        }
        worldIn
            .explode(null, DamageSource.badRespawnPointExplosion(), null,
                (double) pos.getX() + 0.5D,
                (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true,
                Explosion.BlockInteraction.DESTROY);
        return InteractionResult.SUCCESS;
      } else if (state.getValue(OCCUPIED)) {

        if (!this.kickVillagerOutOfBed(worldIn, pos)) {
          player.displayClientMessage(
              new TranslatableComponent("block.comforts." + this.type.name + ".occupied"), true);
        }
        return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer) {
        trySleep((ServerPlayer) player, pos).ifLeft((result) -> {

          if (result != null) {
            final Component text;
            switch (result) {
              case NOT_POSSIBLE_NOW:
                text = type == BedType.HAMMOCK ? new TranslatableComponent(
                    "block.comforts." + type.name + ".no_sleep")
                    : new TranslatableComponent("block.minecraft.bed.no_sleep");
                break;
              case TOO_FAR_AWAY:
                text = new TranslatableComponent(
                    "block.comforts." + type.name + ".too_far_away");
                break;
              default:
                text = result.getMessage();
            }
            player.displayClientMessage(text, true);
          }
        });
      }
    }
    return InteractionResult.SUCCESS;
  }

  public static Either<BedSleepingProblem, Unit> trySleep(ServerPlayer playerEntity, BlockPos at) {
    final java.util.Optional<BlockPos> optAt = java.util.Optional.of(at);
    final Player.BedSleepingProblem ret = net.minecraftforge.event.ForgeEventFactory
        .onPlayerSleepInBed(playerEntity, optAt);

    if (ret != null) {
      return Either.left(ret);
    }
    final Direction direction = playerEntity.level.getBlockState(at)
        .getValue(HorizontalDirectionalBlock.FACING);

    if (!playerEntity.isSleeping() && playerEntity.isAlive()) {

      if (!playerEntity.level.dimensionType().natural()) {
        return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
      } else if (!bedInRange(playerEntity, at, direction)) {
        return Either.left(Player.BedSleepingProblem.TOO_FAR_AWAY);
      } else if (bedBlocked(playerEntity, at, direction)) {
        return Either.left(Player.BedSleepingProblem.OBSTRUCTED);
      } else {

        if (!ForgeEventFactory.fireSleepingTimeCheck(playerEntity, optAt)) {
          return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
        } else {

          if (!playerEntity.isCreative()) {
            final double d0 = 8.0D;
            final double d1 = 5.0D;
            final Vec3 vector3d = Vec3.atBottomCenterOf(at);
            List<Monster> list = playerEntity.level.getEntitiesOfClass(Monster.class,
                new AABB(vector3d.x() - d0, vector3d.y() - d1,
                    vector3d.z() - d0, vector3d.x() + d0, vector3d.y() + d1,
                    vector3d.z() + d0),
                (p_241146_1_) -> p_241146_1_.isPreventingPlayerRest(playerEntity));

            if (!list.isEmpty()) {
              return Either.left(Player.BedSleepingProblem.NOT_SAFE);
            }
          }
          playerEntity.startSleeping(at);

          try {
            SLEEP_TIMER.setInt(playerEntity, 0);
          } catch (IllegalAccessException e) {
            ComfortsMod.LOGGER.error("Error setting sleep timer!");
          }
          playerEntity.awardStat(Stats.SLEEP_IN_BED);
          CriteriaTriggers.SLEPT_IN_BED.trigger(playerEntity);
          ((ServerLevel) playerEntity.level).updateSleepingPlayerList();
          return Either.right(Unit.INSTANCE);
        }
      }
    } else {
      return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
    }
  }

  private static boolean bedInRange(ServerPlayer playerEntity, BlockPos p_241147_1_,
                                    Direction p_241147_2_) {
    if (p_241147_2_ == null) {
      return false;
    }
    return isReachableBedBlock(playerEntity, p_241147_1_) || isReachableBedBlock(playerEntity,
        p_241147_1_.relative(p_241147_2_.getOpposite()));
  }

  private static boolean isReachableBedBlock(ServerPlayer playerEntity, BlockPos p_241158_1_) {
    final Vec3 vector3d = Vec3.atBottomCenterOf(p_241158_1_);
    return Math.abs(playerEntity.getX() - vector3d.x()) <= 3.0D
        && Math.abs(playerEntity.getY() - vector3d.y()) <= 2.0D
        && Math.abs(playerEntity.getZ() - vector3d.z()) <= 3.0D;
  }

  private static boolean bedBlocked(ServerPlayer playerEntity, BlockPos p_241156_1_,
                                    Direction p_241156_2_) {
    final BlockPos blockpos = p_241156_1_.above();
    return isAbnormalCube(playerEntity.level, blockpos) || isAbnormalCube(playerEntity.level,
        blockpos.relative(p_241156_2_.getOpposite()));
  }

  private static boolean isAbnormalCube(Level world, BlockPos pos) {
    return world.getBlockState(pos).isSuffocating(world, pos);
  }

  private boolean kickVillagerOutOfBed(Level p_226861_1_, BlockPos p_226861_2_) {
    List<Villager> list = p_226861_1_
        .getEntitiesOfClass(Villager.class, new AABB(p_226861_2_),
            LivingEntity::isSleeping);

    if (list.isEmpty()) {
      return false;
    } else {
      list.get(0).stopSleeping();
      return true;
    }
  }

  @Override
  public void playerWillDestroy(Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state,
                                @Nonnull Player player) {

    if (!worldIn.isClientSide && player.isCreative()) {
      final BedPart bedpart = state.getValue(PART);

      if (bedpart == BedPart.FOOT) {
        final BlockPos blockpos =
            pos.relative(getDirectionToOther(bedpart, state.getValue(FACING)));
        final BlockState blockstate = worldIn.getBlockState(blockpos);

        if (blockstate.getBlock() == this && blockstate.getValue(PART) == BedPart.HEAD) {

          if (blockstate.getValue(WATERLOGGED)) {
            worldIn.setBlock(blockpos, Blocks.WATER.defaultBlockState(), 35);
          } else {
            worldIn.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
          }
          worldIn.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
        }
      }
    }
    worldIn.levelEvent(player, 2001, pos, getId(state));

    if (BlockTags.GUARDED_BY_PIGLINS.contains(this)) {
      PiglinAi.angerNearbyPiglins(player, false);
    }
  }

  @Nonnull
  @Override
  public BlockState updateShape(BlockState stateIn, @Nonnull Direction facing,
                                @Nonnull BlockState facingState, @Nonnull LevelAccessor worldIn,
                                @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {

    if (stateIn.getValue(WATERLOGGED)) {
      worldIn.getLiquidTicks()
          .scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
    }

    return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
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
  protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
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
