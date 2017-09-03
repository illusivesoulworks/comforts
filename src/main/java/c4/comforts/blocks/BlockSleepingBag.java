package c4.comforts.blocks;

import c4.comforts.Comforts;
import c4.comforts.common.ComfortsHelper;
import c4.comforts.items.ComfortsItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockSleepingBag extends BlockHorizontal {

    public static final PropertyEnum<BlockSleepingBag.EnumPartType> PART = PropertyEnum.create("part", BlockSleepingBag.EnumPartType.class);
    public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");
    protected static final AxisAlignedBB SLEEPING_BAG_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);
    private int color;

    public BlockSleepingBag(EnumDyeColor color)
    {
        super(Material.CLOTH);
        this.setDefaultState(this.blockState.getBaseState().withProperty(PART, BlockSleepingBag.EnumPartType.FOOT).withProperty(OCCUPIED, false));
        this.setSoundType(SoundType.CLOTH);
        this.setHardness(0.2F);
        this.color = color.getMetadata();
        this.setRegistryName("sleeping_bag_" + color.getName());
        this.setUnlocalizedName(Comforts.MODID + ".sleeping_bag." + color.getName());
        this.disableStats();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return color;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            if (state.getValue(PART) != BlockSleepingBag.EnumPartType.HEAD)
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
                if (state.getValue(OCCUPIED))
                {
                    EntityPlayer entityplayer = this.getPlayerInSleepingBag(worldIn, pos);

                    if (entityplayer != null)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.sleepingbag.occupied"), true);
                        return true;
                    }

                    state = state.withProperty(OCCUPIED, false);
                    worldIn.setBlockState(pos, state, 4);
                }

                EntityPlayer.SleepResult entityplayer$sleepresult = playerIn.trySleep(pos);

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
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.sleepingbag.noSleep"), true);
                    }
                    else if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_SAFE)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.sleepingbag.notSafe"), true);
                    }
                    else if (entityplayer$sleepresult == EntityPlayer.SleepResult.TOO_FAR_AWAY)
                    {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.sleepingbag.tooFarAway"), true);
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

                worldIn.newExplosion(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 3.0F, true, true);
                return true;
            }
        }
    }

    @Nullable
    private EntityPlayer getPlayerInSleepingBag(World worldIn, BlockPos pos)
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

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        EnumFacing enumfacing = state.getValue(FACING);

        if (state.getValue(PART) == BlockSleepingBag.EnumPartType.FOOT)
        {
            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
            }
        }
        else if (worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this)
        {
            if (!worldIn.isRemote)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
            }

            worldIn.setBlockToAir(pos);
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(PART) == BlockSleepingBag.EnumPartType.FOOT ? Items.AIR : ComfortsItems.sleepingBag;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SLEEPING_BAG_AABB;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state)
    {
        return true;
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (state.getValue(PART) == BlockSleepingBag.EnumPartType.HEAD)
        {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
        }
    }

    public EnumPushReaction getMobilityFlag(IBlockState state)
    {
        return EnumPushReaction.DESTROY;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ComfortsItems.sleepingBag, 1, color);
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode && state.getValue(PART) == BlockSleepingBag.EnumPartType.FOOT)
        {
            BlockPos blockpos = pos.offset(state.getValue(FACING));

            if (worldIn.getBlockState(blockpos).getBlock() == this)
            {
                worldIn.setBlockToAir(blockpos);
            }
        }
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (state.getValue(PART) == BlockSleepingBag.EnumPartType.FOOT)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.offset(state.getValue(FACING)));

            if (iblockstate.getBlock() == this)
            {
                state = state.withProperty(OCCUPIED, iblockstate.getValue(OCCUPIED));
            }
        }

        return state;
    }

    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getHorizontal(meta);
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(PART, BlockSleepingBag.EnumPartType.HEAD).withProperty(FACING, enumfacing).withProperty(OCCUPIED, (meta & 4) > 0) : this.getDefaultState().withProperty(PART, BlockSleepingBag.EnumPartType.FOOT).withProperty(FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | (state.getValue(FACING)).getHorizontalIndex();

        if (state.getValue(PART) == BlockSleepingBag.EnumPartType.HEAD)
        {
            i |= 8;

            if (state.getValue(OCCUPIED))
            {
                i |= 4;
            }
        }

        return i;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, PART, OCCUPIED);
    }

    public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player)
    {
        return true;
    }

    public void setBedOccupied(IBlockAccess world, BlockPos pos, EntityPlayer player, boolean occupied)
    {
        if (world instanceof World)
        {
            IBlockState state = world.getBlockState(pos);
            state = state.getBlock().getActualState(state, world, pos);
            state = state.withProperty(BlockSleepingBag.OCCUPIED, occupied);
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
