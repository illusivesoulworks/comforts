/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.common.blocks;

import c4.comforts.Comforts;
import c4.comforts.common.tileentities.TileEntityHammock;
import c4.comforts.common.util.ComfortsUtil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockBase extends BlockHorizontal {

    public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");
    protected float explosivePower = 5.0F;
    protected String textOccupied;
    protected String textNoSleep;
    protected String textNotSafe;
    protected String textTooFar;
    protected int color;

    public BlockBase(String name, EnumDyeColor color) {
        super(Material.CLOTH);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT).withProperty(OCCUPIED, false));
        this.setSoundType(SoundType.CLOTH);
        this.setHardness(0.2F);
        this.color = color.getMetadata();
        this.setRegistryName(name + "_" + color.getName());
        this.setTranslationKey(Comforts.MODID + "." + name + "." + color.getName());
        this.setTextComponents(name);
        this.disableStats();
    }

    private void setTextComponents(String name) {
        textOccupied = "tile." + name + ".occupied";
        textNoSleep = "tile." + name + ".noSleep";
        textNotSafe = "tile." + name + ".notSafe";
        textTooFar = "tile." + name + ".tooFarAway";
    }

    protected void setExplosivePower(float power) {
        explosivePower = power;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            this.doSleep(worldIn, pos, state, playerIn);
        }
        return true;
    }

    public EntityPlayer.SleepResult doSleep(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn) {
        BlockPos originalPos = pos;

        if (state.getValue(BlockBed.PART) != BlockBed.EnumPartType.HEAD) {
            pos = pos.offset(state.getValue(FACING));
            state = worldIn.getBlockState(pos);

            if (state.getBlock() != this) {
                return EntityPlayer.SleepResult.OTHER_PROBLEM;
            }
        }

        net.minecraft.world.WorldProvider.WorldSleepResult sleepResult = worldIn.provider.canSleepAt(playerIn, pos);
        if (sleepResult != net.minecraft.world.WorldProvider.WorldSleepResult.BED_EXPLODES) {

            if (sleepResult == net.minecraft.world.WorldProvider.WorldSleepResult.DENY) {
                return EntityPlayer.SleepResult.OTHER_PROBLEM;
            }

            if (state.getBlock() instanceof BlockHammock) {
                TileEntity tileentity = worldIn.getTileEntity(originalPos);
                TileEntity tileentity2 = worldIn.getTileEntity(pos);

                if (tileentity instanceof TileEntityHammock && tileentity2 instanceof TileEntityHammock) {

                    if (((TileEntityHammock) tileentity).isOccupied() || ((TileEntityHammock) tileentity2).isOccupied()) {
                        playerIn.sendStatusMessage(new TextComponentTranslation(textOccupied), true);
                        return EntityPlayer.SleepResult.OTHER_PROBLEM;
                    }
                }
            }

            if (state.getValue(OCCUPIED))
            {
                EntityPlayer entityplayer = this.getPlayerInComfort(worldIn, pos);

                if (entityplayer != null)
                {
                    playerIn.sendStatusMessage(new TextComponentTranslation(textOccupied), true);
                    return EntityPlayer.SleepResult.OTHER_PROBLEM;
                }

                state = state.withProperty(OCCUPIED, false);
                worldIn.setBlockState(pos, state, 4);
            }

            EntityPlayer.SleepResult entityplayer$sleepresult = playerIn.trySleep(pos);

            if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK) {
                state = state.withProperty(OCCUPIED, true);
                worldIn.setBlockState(pos, state, 4);
            } else {
                if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                    playerIn.sendStatusMessage(new TextComponentTranslation(textNoSleep), true);
                } else if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_SAFE) {
                    playerIn.sendStatusMessage(new TextComponentTranslation(textNotSafe), true);
                } else if (entityplayer$sleepresult == EntityPlayer.SleepResult.TOO_FAR_AWAY) {
                    playerIn.sendStatusMessage(new TextComponentTranslation(textTooFar), true);
                }
            }
            return entityplayer$sleepresult;
        } else {
            worldIn.setBlockToAir(pos);
            BlockPos blockpos = pos.offset((state.getValue(FACING)).getOpposite());

            if (worldIn.getBlockState(blockpos).getBlock() == this) {
                worldIn.setBlockToAir(blockpos);
            }
            worldIn.newExplosion(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, explosivePower, true, true);
            return EntityPlayer.SleepResult.OTHER_PROBLEM;
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return color;
    }

    @Nullable
    protected EntityPlayer getPlayerInComfort(World worldIn, BlockPos pos) {

        for (EntityPlayer entityplayer : worldIn.playerEntities) {

            if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(pos)) {
                return entityplayer;
            }
        }
        return null;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    public void dropBlockAsItemWithChance(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, float chance, int fortune) {

        if (state.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
        }
    }

    @Nonnull
    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {

        if (player.capabilities.isCreativeMode && state.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            BlockPos blockpos = pos.offset(state.getValue(FACING));

            if (worldIn.getBlockState(blockpos).getBlock() == this) {
                worldIn.setBlockToAir(blockpos);
            }
        }
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (state.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            IBlockState iblockstate = worldIn.getBlockState(pos.offset(state.getValue(FACING)));

            if (iblockstate.getBlock() == this) {
                state = state.withProperty(OCCUPIED, iblockstate.getValue(OCCUPIED));
            }
        }
        return state;
    }

    @Nonnull
    @Override
    public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Nonnull
    @Override
    public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta);
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD).withProperty(FACING, enumfacing).withProperty(OCCUPIED, (meta & 4) > 0) : this.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT).withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | (state.getValue(FACING)).getHorizontalIndex();

        if (state.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) {
            i |= 8;

            if (state.getValue(OCCUPIED)) {
                i |= 4;
            }
        }
        return i;
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, BlockBed.PART, OCCUPIED);
    }

    @Override
    public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player) {
        return true;
    }

    @Override
    public void setBedOccupied(IBlockAccess world, @Nonnull BlockPos pos, EntityPlayer player, boolean occupied) {

        if (world instanceof World) {
            IBlockState state = world.getBlockState(pos);
            state = state.getBlock().getActualState(state, world, pos);
            state = state.withProperty(BlockBase.OCCUPIED, occupied);
            ((World)world).setBlockState(pos, state, 4);
        }
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @SideOnly(Side.CLIENT)
    public IBlockColor colorMultiplier() {
        return (state, worldIn, pos, tintIndex) -> ComfortsUtil.getColor(color);
    }
}
