package top.theillusivec4.comforts.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.registries.ObjectHolder;
import top.theillusivec4.comforts.Comforts;

import java.util.EnumMap;

public class ComfortsBlocks {

    public static final EnumMap<EnumDyeColor, Block> SLEEPING_BAGS = new EnumMap<>(EnumDyeColor.class);

    public static final EnumMap<EnumDyeColor, Block> HAMMOCKS = new EnumMap<>(EnumDyeColor.class);

    @ObjectHolder("comforts:rope_and_nail")
    public static final Block ROPE_AND_NAIL = null;
}
