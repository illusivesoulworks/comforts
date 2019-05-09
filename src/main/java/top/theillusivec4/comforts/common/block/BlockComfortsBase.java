package top.theillusivec4.comforts.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockComfortsBase extends BlockBed {

    private final BedType type;

    public BlockComfortsBase(BedType type, EnumDyeColor colorIn, Block.Properties properties) {
        super(colorIn, properties);
        this.type = type;
    }

    @Override
    public boolean onBlockActivated(@Nonnull IBlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

        if (worldIn.isRemote) {
            return true;
        } else {

            if (state.get(PART) != BedPart.HEAD) {
                pos = pos.offset(state.get(HORIZONTAL_FACING));
                state = worldIn.getBlockState(pos);
                if (state.getBlock() != this) {
                    return true;
                }
            }
            net.minecraftforge.common.extensions.IForgeDimension.SleepResult sleepResult = worldIn.dimension.canSleepAt(player, pos);

            if (sleepResult != net.minecraftforge.common.extensions.IForgeDimension.SleepResult.BED_EXPLODES) {

                if (sleepResult == net.minecraftforge.common.extensions.IForgeDimension.SleepResult.DENY) {
                    return true;
                }

                if (state.get(OCCUPIED)) {
                    EntityPlayer entityplayer = this.getPlayerInBed(worldIn, pos);

                    if (entityplayer != null) {
                        player.sendStatusMessage(new TextComponentTranslation("block.comforts." + type.name + "occupied"), true);
                        return true;
                    }
                    state = state.with(OCCUPIED, false);
                    worldIn.setBlockState(pos, state, 4);
                }
                EntityPlayer.SleepResult entityplayer$sleepresult = player.trySleep(pos);

                if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK) {
                    state = state.with(OCCUPIED, true);
                    worldIn.setBlockState(pos, state, 4);
                    return true;
                } else {

                    if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                        TextComponentTranslation text = type == BedType.HAMMOCK ? new TextComponentTranslation("block.comforts." + type.name + ".no_sleep") : new TextComponentTranslation("block.minecraft.bed.no_sleep");
                        player.sendStatusMessage(text, true);
                    } else if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_SAFE) {
                        player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.not_safe"), true);
                    } else if (entityplayer$sleepresult == EntityPlayer.SleepResult.TOO_FAR_AWAY) {
                        player.sendStatusMessage(new TextComponentTranslation("block.comforts." + type.name + "too_far_away"), true);
                    }
                    return true;
                }
            } else {
                worldIn.removeBlock(pos);
                BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());
                if (worldIn.getBlockState(blockpos).getBlock() == this) {
                    worldIn.removeBlock(blockpos);
                }
                worldIn.createExplosion(null, DamageSource.netherBedExplosion(), (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, true);
                return true;
            }
        }
    }

    @Nullable
    private EntityPlayer getPlayerInBed(World worldIn, BlockPos pos) {

        for(EntityPlayer entityplayer : worldIn.playerEntities) {

            if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(pos)) {
                return entityplayer;
            }
        }
        return null;
    }

    enum BedType {
        HAMMOCK("hammock"),
        SLEEPING_BAG("sleeping_bag");

        private final String name;

        BedType(String name) {
            this.name = name;
        }
    }
}
