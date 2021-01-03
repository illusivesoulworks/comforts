package top.theillusivec4.comforts.common.block;

import static net.minecraft.entity.player.PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW;
import static net.minecraft.entity.player.PlayerEntity.SleepFailureReason.TOO_FAR_AWAY;


import com.mojang.datafixers.util.Either;
import java.util.List;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import top.theillusivec4.comforts.mixin.AccessorPlayerEntity;
import top.theillusivec4.somnus.api.SleepEvents;

public abstract class AbstractComfortsBlock extends BedBlock implements Waterloggable {

  public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

  protected final DyeColor color;

  private final BedType type;

  public AbstractComfortsBlock(final BedType type, final DyeColor color) {
    super(color, AbstractBlock.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOL).strength(0.1F));
    this.type = type;
    this.color = color;
    this.setDefaultState(
        this.getStateManager().getDefaultState().with(PART, BedPart.FOOT).with(OCCUPIED, false)
            .with(WATERLOGGED, false));
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                            Hand hand, BlockHitResult hit) {

    if (world.isClient) {
      return ActionResult.CONSUME;
    } else {

      if (state.get(PART) != BedPart.HEAD) {
        pos = pos.offset(state.get(FACING));
        state = world.getBlockState(pos);

        if (!state.isOf(this)) {
          return ActionResult.CONSUME;
        }
      }

      if (!isOverworld(world)) {
        world.removeBlock(pos, false);
        BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());

        if (world.getBlockState(blockPos).isOf(this)) {
          world.removeBlock(blockPos, false);
        }
        world
            .createExplosion(null, DamageSource.badRespawnPoint(), null, (double) pos.getX() + 0.5D,
                (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true,
                Explosion.DestructionType.DESTROY);
        return ActionResult.SUCCESS;
      } else if (state.get(OCCUPIED)) {

        if (!this.isFree(world, pos)) {
          player.sendMessage(new TranslatableText("block.comforts." + this.type.name + ".occupied"),
              true);
        }
        return ActionResult.SUCCESS;
      } else if (player instanceof ServerPlayerEntity) {
        trySleep((ServerPlayerEntity) player, pos).ifLeft((sleepFailureReason) -> {
          if (sleepFailureReason != null) {
            final Text text;

            switch (sleepFailureReason) {
              case NOT_POSSIBLE_NOW:
                text = type == BedType.HAMMOCK ? new TranslatableText(
                    "block.comforts." + type.name + ".no_sleep")
                    : new TranslatableText("block.minecraft.bed.no_sleep");
                break;
              case TOO_FAR_AWAY:
                text = new TranslatableText(
                    "block.comforts." + type.name + ".too_far_away");
                break;
              default:
                text = sleepFailureReason.toText();
            }
            player.sendMessage(text, true);
          }
        });
      }
      return ActionResult.SUCCESS;
    }
  }

  private static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
    return part == BedPart.FOOT ? direction : direction.getOpposite();
  }

  @Override
  public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {

    if (!world.isClient && player.isCreative()) {
      BedPart bedPart = state.get(PART);

      if (bedPart == BedPart.FOOT) {
        BlockPos blockPos = pos.offset(getDirectionTowardsOtherPart(bedPart, state.get(FACING)));
        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.getBlock() == this && blockState.get(PART) == BedPart.HEAD) {

          if (blockState.get(WATERLOGGED)) {
            world.setBlockState(blockPos, Blocks.WATER.getDefaultState(), 35);
          } else {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
          }
          world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
        }
      }
    }

    world.syncWorldEvent(player, 2001, pos, getRawIdFromState(state));

    if (this.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
      PiglinBrain.onGuardedBlockInteracted(player, false);
    }
  }

  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
                                              BlockState newState, WorldAccess world, BlockPos pos,
                                              BlockPos posFrom) {

    if (state.get(WATERLOGGED)) {
      world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
    }
    return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    final FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
    final BlockState state = super.getPlacementState(ctx);
    return state == null ? null : state.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(WATERLOGGED);
    super.appendProperties(builder);
  }

  @SuppressWarnings("deprecation")
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
  }

  public static Either<PlayerEntity.SleepFailureReason, Unit> trySleep(ServerPlayerEntity player,
                                                                       BlockPos pos) {
    PlayerEntity.SleepFailureReason result = SleepEvents.TRY_SLEEP.invoker().trySleep(player, pos);

    if (result != null) {
      return Either.left(result);
    }
    Direction direction = player.world.getBlockState(pos).get(HorizontalFacingBlock.FACING);

    if (!player.isSleeping() && player.isAlive()) {

      if (!player.world.getDimension().isNatural()) {
        return Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_HERE);
      } else if (!isBedTooFarAway(player, pos, direction)) {
        return Either.left(TOO_FAR_AWAY);
      } else if (isBedObstructed(player, pos, direction)) {
        return Either.left(PlayerEntity.SleepFailureReason.OBSTRUCTED);
      } else {
        player.setSpawnPoint(player.world.getRegistryKey(), pos, player.yaw, false, true);

        if (player.world.isDay()) {
          return Either.left(NOT_POSSIBLE_NOW);
        } else {

          if (!player.isCreative()) {
            double d = 8.0D;
            double e = 5.0D;
            Vec3d vec3d = Vec3d.ofBottomCenter(pos);
            List<HostileEntity> list = player.world.getEntitiesByClass(HostileEntity.class,
                new Box(vec3d.getX() - d, vec3d.getY() - e, vec3d.getZ() - d, vec3d.getX() + d,
                    vec3d.getY() + e, vec3d.getZ() + d),
                (hostileEntity) -> hostileEntity.isAngryAt(player));
            if (!list.isEmpty()) {
              return Either.left(PlayerEntity.SleepFailureReason.NOT_SAFE);
            }
          }
          player.sleep(pos);
          ((AccessorPlayerEntity) player).setSleepTimer(0);
          player.incrementStat(Stats.SLEEP_IN_BED);
          Criteria.SLEPT_IN_BED.trigger(player);
          ((ServerWorld) player.world).updateSleepingPlayers();
          return Either.right(Unit.INSTANCE);
        }
      }
    } else {
      return Either.left(PlayerEntity.SleepFailureReason.OTHER_PROBLEM);
    }
  }

  private boolean isFree(World world, BlockPos pos) {
    List<VillagerEntity> list =
        world.getEntitiesByClass(VillagerEntity.class, new Box(pos), LivingEntity::isSleeping);

    if (list.isEmpty()) {
      return false;
    } else {
      list.get(0).wakeUp();
      return true;
    }
  }

  private static boolean isBedTooFarAway(ServerPlayerEntity player, BlockPos pos,
                                         Direction direction) {
    return isBedTooFarAway(player, pos) ||
        isBedTooFarAway(player, pos.offset(direction.getOpposite()));
  }

  private static boolean isBedTooFarAway(ServerPlayerEntity player, BlockPos pos) {
    Vec3d vec3d = Vec3d.ofBottomCenter(pos);
    return Math.abs(player.getX() - vec3d.getX()) <= 3.0D &&
        Math.abs(player.getY() - vec3d.getY()) <= 2.0D &&
        Math.abs(player.getZ() - vec3d.getZ()) <= 3.0D;
  }

  private static boolean isBedObstructed(ServerPlayerEntity player, BlockPos pos,
                                         Direction direction) {
    BlockPos blockPos = pos.up();
    return suffocates(player, blockPos) ||
        suffocates(player, blockPos.offset(direction.getOpposite()));
  }

  protected static boolean suffocates(ServerPlayerEntity player, BlockPos pos) {
    return player.world.getBlockState(pos).shouldSuffocate(player.world, pos);
  }

  public enum BedType {
    HAMMOCK("hammock"), SLEEPING_BAG("sleeping_bag");

    private final String name;

    BedType(String name) {
      this.name = name;
    }
  }
}
