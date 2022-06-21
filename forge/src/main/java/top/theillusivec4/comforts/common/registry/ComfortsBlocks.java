package top.theillusivec4.comforts.common.registry;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.comforts.ComfortsMod;
import top.theillusivec4.comforts.common.block.HammockBlock;
import top.theillusivec4.comforts.common.block.RopeAndNailBlock;
import top.theillusivec4.comforts.common.block.SleepingBagBlock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ComfortsBlocks {
    public static final DeferredRegister<Block> SLEEPING_BAGS = DeferredRegister.create(ForgeRegistries.BLOCKS, ComfortsMod.MOD_ID);
    public static final DeferredRegister<Block> HAMMOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ComfortsMod.MOD_ID);
    private static final DeferredRegister<Block> OTHER_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ComfortsMod.MOD_ID);
    public static final RegistryObject<Block> ROPE_AND_NAIL = OTHER_BLOCKS.register("rope_and_nail", RopeAndNailBlock::new);

    public static final Map<DyeColor, RegistryObject<Block>> SLEEPING_BAG_COLORS = new HashMap<>();

    static {
        Arrays.stream(DyeColor.values()).forEach(color -> {
            RegistryObject<Block> sleepingBag = SLEEPING_BAGS.register("sleeping_bag_" + color.getName(), () -> new SleepingBagBlock(color));
            SLEEPING_BAG_COLORS.put(color, sleepingBag);
            HAMMOCKS.register("hammock_" + color.getName(), () -> new HammockBlock(color));
        });
    }

    public static void register(IEventBus bus) {
        SLEEPING_BAGS.register(bus);
        HAMMOCKS.register(bus);
        OTHER_BLOCKS.register(bus);
    }
}
