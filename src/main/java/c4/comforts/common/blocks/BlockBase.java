/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common.blocks;

import c4.comforts.Comforts;
import c4.comforts.common.tileentities.TileEntityHammock;
import c4.comforts.common.util.ComfortsHelper;
import c4.comforts.common.util.SleepHelper;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockBase extends BlockHorizontal {

    public static final PropertyEnum<BlockBase.EnumPartType> PART = PropertyEnum.create("part", BlockBase.EnumPartType.class);
    public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");
    protected float explosivePower = 5.0F;
    protected String textOccupied;
    protected String textNoSleep;
    protected String textNotSafe;
    protected String textTooFar;
    protected int color;

    public BlockBase(String name, EnumDyeColor color)
    {
        super(Material.CLOTH);
        this.setDefaultState(this.blockState.getBaseState().withProperty(PART, BlockBase.EnumPartType.FOOT).withProperty(OCCUPIED, false));
        this.setSoundType(SoundType.CLOTH);
        this.setHardness(0.2F);
        this.color = color.getMetadata();
        this.setRegistryName(name + "_" + color.getName());
        this.setUnlocalizedName(Comforts.MODID + "." + name + "." + color.getName());
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            BlockPos originalPos = pos;

            if (state.getValue(PART) != EnumPartType.HEAD)
            {
                pos = pos.offset(state.getValue(FACING));
                state = worldIn.getBlockState(pos);

                if (state.getBlock() != this)
                {
                    return true;
                }
            }

            if (worldIn.provider.canRespawnHere() && worldIn.getBiome(pos) != Biomes.HELL)
            {

                if (state.getBlock() instanceof BlockHammock) {
                    TileEntity tileentity = worldIn.getTileEntity(originalPos);
                    TileEntity tileentity2 = worldIn.getTileEntity(pos);
                    if (tileentity instanceof TileEntityHammock && tileentity2 instanceof TileEntityHammock) {
                        if (((TileEntityHammock) tileentity).isOccupied() || ((TileEntityHammock) tileentity2).isOccupied()) {
                            playerIn.sendStatusMessage(new TextComponentTranslation(textOccupied), true);
                            return true;
                        }
                    }
                }

                if (state.getValue(OCCUPIED))
                {
                    EntityPlayer entityplayer = this.getPlayerInComfort(worldIn, pos);

                    if (entityplayer != null)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation(textOccupied), true);
                        return true;
                    }

                    state = state.withProperty(OCCUPIED, false);
                    worldIn.setBlockState(pos, state, 4);
                }

                EntityPlayer.SleepResult entityplayer$sleepresult = SleepHelper.goToSleep(playerIn, pos, false);

                if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK)
                {
                    state = state.withProperty(OCCUPIED, true);
                    worldIn.setBlockState(pos, state, 4);
                    return true;
                }
                else
                {
                    if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation(textNoSleep), true);
                    }
                    else if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_SAFE)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation(textNotSafe), true);
                    }
                    else if (entityplayer$sleepresult == EntityPlayer.SleepResult.TOO_FAR_AWAY)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation(textTooFar), true);
                    }

                    return true;
                }
            }
            else
            {
                worldIn.setBlockToAir(pos);
                BlockPos blockpos = pos.offset((state.getValue(FACING)).getOpposite());

                if (worldIn.getBlockState(blockpos).getBlock() == this)
                {
                    worldIn.setBlockToAir(blockpos);
                }

                worldIn.newExplosion(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, explosivePower, true, true);
                return true;
            }
        }
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return color;
    }

    @Nullable
    protected EntityPlayer getPlayerInComfort(World worldIn, BlockPos pos)
    {
        for (EntityPlayer entityplayer : worldIn.playerEntities)
        {
            if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(pos))
            {
                return entityplayer;
            }
        }

        return null;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state)
    {
        return true;
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (state.getValue(PART) == BlockBase.EnumPartType.HEAD)
        {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
        }
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state)
    {
        return EnumPushReaction.DESTROY;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode && state.getValue(PART) == BlockBase.EnumPartType.FOOT)
        {
            BlockPos blockpos = pos.offset(state.getValue(FACING));

            if (worldIn.getBlockState(blockpos).getBlock() == this)
            {
                worldIn.setBlockToAir(blockpos);
            }
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (state.getValue(PART) == BlockBase.EnumPartType.FOOT)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.offset(state.getValue(FACING)));

            if (iblockstate.getBlock() == this)
            {
                state = state.withProperty(OCCUPIED, iblockstate.getValue(OCCUPIED));
            }
        }

        return state;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getHorizontal(meta);
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(PART, BlockBase.EnumPartType.HEAD).withProperty(FACING, enumfacing).withProperty(OCCUPIED, (meta & 4) > 0) : this.getDefaultState().withProperty(PART, BlockBase.EnumPartType.FOOT).withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | (state.getValue(FACING)).getHorizontalIndex();

        if (state.getValue(PART) == BlockBase.EnumPartType.HEAD)
        {
            i |= 8;

            if (state.getValue(OCCUPIED))
            {
                i |= 4;
            }
        }

        return i;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, PART, OCCUPIED);
    }

    @Override
    public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player)
    {
        return true;
    }

    @Override
    public void setBedOccupied(IBlockAccess world, BlockPos pos, EntityPlayer player, boolean occupied)
    {
        if (world instanceof World)
        {
            IBlockState state = world.getBlockState(pos);
            state = state.getBlock().getActualState(state, world, pos);
            state = state.withProperty(BlockBase.OCCUPIED, occupied);
            ((World)world).setBlockState(pos, state, 4);
        }
    }

    @SideOnly(Side.CLIENT)
    public IBlockColor colorMultiplier() {
        return (state, worldIn, pos, tintIndex) -> ComfortsHelper.getColor(color);
    }

    public enum EnumPartType implements IStringSerializable
    {
        HEAD("head"),
        FOOT("foot");

        private final String name;

        EnumPartType(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}
