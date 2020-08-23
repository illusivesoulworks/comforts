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
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepResult;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import top.theillusivec4.comforts.Comforts;

public class ComfortsBaseBlock extends BedBlock implements IWaterLoggable {

  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  private static final Field SLEEP_TIMER = ObfuscationReflectionHelper
      .findField(PlayerEntity.class, "field_71076_b");

  private final BedType type;

  public ComfortsBaseBlock(BedType type, DyeColor colorIn, Block.Properties properties) {
    super(colorIn, properties);
    this.type = type;
    this.setDefaultState(
        this.stateContainer.getBaseState().with(PART, BedPart.FOOT).with(OCCUPIED, false)
            .with(WATERLOGGED, false));
  }

  private static Direction getDirectionToOther(BedPart part, Direction direction) {
    return part == BedPart.FOOT ? direction : direction.getOpposite();
  }

  @Nonnull
  @Override
  public ActionResultType onBlockActivated(@Nonnull BlockState state, World worldIn,
      @Nonnull BlockPos pos, @Nonnull PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

    if (worldIn.isRemote) {
      return ActionResultType.CONSUME;
    } else {

      if (state.get(PART) != BedPart.HEAD) {
        pos = pos.offset(state.get(HORIZONTAL_FACING));
        state = worldIn.getBlockState(pos);

        if (!state.isIn(this)) {
          return ActionResultType.CONSUME;
        }
      }

      if (!func_235330_a_(worldIn)) {
        worldIn.removeBlock(pos, false);
        BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());

        if (worldIn.getBlockState(blockpos).isIn(this)) {
          worldIn.removeBlock(blockpos, false);
        }
        worldIn
            .createExplosion(null, DamageSource.func_233546_a_(), null, (double) pos.getX() + 0.5D,
                (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true,
                Explosion.Mode.DESTROY);
        return ActionResultType.SUCCESS;
      } else if (state.get(OCCUPIED)) {

        if (!this.func_226861_a_(worldIn, pos)) {
          player.sendStatusMessage(
              new TranslationTextComponent("block.comforts." + this.type.name + ".occupied"), true);
        }
        return ActionResultType.SUCCESS;
      } else if (player instanceof ServerPlayerEntity) {
        trySleep((ServerPlayerEntity) player, pos).ifLeft((result) -> {

          if (result != null) {
            ITextComponent text;
            switch (result) {
              case NOT_POSSIBLE_NOW:
                text = type == BedType.HAMMOCK ? new TranslationTextComponent(
                    "block.comforts." + type.name + ".no_sleep")
                    : new TranslationTextComponent("block.minecraft.bed.no_sleep");
                break;
              case TOO_FAR_AWAY:
                text = new TranslationTextComponent(
                    "block.comforts." + type.name + ".too_far_away");
                break;
              default:
                text = result.getMessage();
            }
            player.sendStatusMessage(text, true);
          }
        });
      }
    }
    return ActionResultType.SUCCESS;
  }

  public static Either<SleepResult, Unit> trySleep(ServerPlayerEntity playerEntity, BlockPos at) {
    java.util.Optional<BlockPos> optAt = java.util.Optional.of(at);
    PlayerEntity.SleepResult ret = net.minecraftforge.event.ForgeEventFactory
        .onPlayerSleepInBed(playerEntity, optAt);

    if (ret != null) {
      return Either.left(ret);
    }
    Direction direction = playerEntity.world.getBlockState(at)
        .get(HorizontalBlock.HORIZONTAL_FACING);

    if (!playerEntity.isSleeping() && playerEntity.isAlive()) {

      if (!playerEntity.world.func_230315_m_().func_236043_f_()) {
        return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
      } else if (!func_241147_a_(playerEntity, at, direction)) {
        return Either.left(PlayerEntity.SleepResult.TOO_FAR_AWAY);
      } else if (func_241156_b_(playerEntity, at, direction)) {
        return Either.left(PlayerEntity.SleepResult.OBSTRUCTED);
      } else {

        if (!ForgeEventFactory.fireSleepingTimeCheck(playerEntity, optAt)) {
          return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
        } else {

          if (!playerEntity.isCreative()) {
            double d0 = 8.0D;
            double d1 = 5.0D;
            Vector3d vector3d = Vector3d.copyCenteredHorizontally(at);
            List<MonsterEntity> list = playerEntity.world.getEntitiesWithinAABB(MonsterEntity.class,
                new AxisAlignedBB(vector3d.getX() - 8.0D, vector3d.getY() - 5.0D,
                    vector3d.getZ() - 8.0D, vector3d.getX() + 8.0D, vector3d.getY() + 5.0D,
                    vector3d.getZ() + 8.0D),
                (p_241146_1_) -> p_241146_1_.func_230292_f_(playerEntity));

            if (!list.isEmpty()) {
              return Either.left(PlayerEntity.SleepResult.NOT_SAFE);
            }
          }
          playerEntity.startSleeping(at);

          try {
            SLEEP_TIMER.setInt(playerEntity, 0);
          } catch (IllegalAccessException e) {
            Comforts.LOGGER.error("Error setting sleep timer!");
          }
          playerEntity.addStat(Stats.SLEEP_IN_BED);
          CriteriaTriggers.SLEPT_IN_BED.trigger(playerEntity);
          ((ServerWorld) playerEntity.world).updateAllPlayersSleepingFlag();
          return Either.right(Unit.INSTANCE);
        }
      }
    } else {
      return Either.left(PlayerEntity.SleepResult.OTHER_PROBLEM);
    }
  }

  private static boolean func_241147_a_(ServerPlayerEntity playerEntity, BlockPos p_241147_1_,
      Direction p_241147_2_) {
    if (p_241147_2_ == null) {
      return false;
    }
    return func_241158_g_(playerEntity, p_241147_1_) || func_241158_g_(playerEntity,
        p_241147_1_.offset(p_241147_2_.getOpposite()));
  }

  private static boolean func_241158_g_(ServerPlayerEntity playerEntity, BlockPos p_241158_1_) {
    Vector3d vector3d = Vector3d.copyCenteredHorizontally(p_241158_1_);
    return Math.abs(playerEntity.getPosX() - vector3d.getX()) <= 3.0D
        && Math.abs(playerEntity.getPosY() - vector3d.getY()) <= 2.0D
        && Math.abs(playerEntity.getPosZ() - vector3d.getZ()) <= 3.0D;
  }

  private static boolean func_241156_b_(ServerPlayerEntity playerEntity, BlockPos p_241156_1_,
      Direction p_241156_2_) {
    BlockPos blockpos = p_241156_1_.up();
    return isAbnormalCube(playerEntity.world, blockpos) || isAbnormalCube(playerEntity.world,
        blockpos.offset(p_241156_2_.getOpposite()));
  }

  private static boolean isAbnormalCube(World world, BlockPos pos) {
    return world.getBlockState(pos).isSuffocating(world, pos);
  }

  private boolean func_226861_a_(World p_226861_1_, BlockPos p_226861_2_) {
    List<VillagerEntity> list = p_226861_1_
        .getEntitiesWithinAABB(VillagerEntity.class, new AxisAlignedBB(p_226861_2_),
            LivingEntity::isSleeping);

    if (list.isEmpty()) {
      return false;
    } else {
      list.get(0).wakeUp();
      return true;
    }
  }

  @Override
  public void onBlockHarvested(World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state,
      @Nonnull PlayerEntity player) {

    if (!worldIn.isRemote && player.isCreative()) {
      BedPart bedpart = state.get(PART);

      if (bedpart == BedPart.FOOT) {
        BlockPos blockpos = pos.offset(getDirectionToOther(bedpart, state.get(HORIZONTAL_FACING)));
        BlockState blockstate = worldIn.getBlockState(blockpos);

        if (blockstate.getBlock() == this && blockstate.get(PART) == BedPart.HEAD) {

          if (blockstate.get(WATERLOGGED)) {
            worldIn.setBlockState(blockpos, Blocks.WATER.getDefaultState(), 35);
          } else {
            worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
          }
          worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
        }
      }
    }
    worldIn.playEvent(player, 2001, pos, getStateId(state));

    if (this.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
      PiglinTasks.func_234478_a_(player, false);
    }
  }

  @Nonnull
  @Override
  public BlockState updatePostPlacement(BlockState stateIn, @Nonnull Direction facing,
      @Nonnull BlockState facingState, @Nonnull IWorld worldIn, @Nonnull BlockPos currentPos,
      @Nonnull BlockPos facingPos) {

    if (stateIn.get(WATERLOGGED)) {
      worldIn.getPendingFluidTicks()
          .scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
    }

    return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    FluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
    BlockState state = super.getStateForPlacement(context);
    return state == null ? null : state.with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
  }

  @Override
  protected void fillStateContainer(Builder<Block, BlockState> builder) {
    builder.add(WATERLOGGED);
    super.fillStateContainer(builder);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false)
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
