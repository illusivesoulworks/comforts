package top.theillusivec4.comforts.common.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.comforts.ComfortsMod;
import top.theillusivec4.comforts.common.item.ComfortsBaseItem;
import top.theillusivec4.comforts.common.item.HammockItem;
import top.theillusivec4.comforts.common.item.SleepingBagItem;

public class ComfortsItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ComfortsMod.MOD_ID);

    public static final RegistryObject<Item> ROPE_AND_NAIL = ITEMS.register(ComfortsBlocks.ROPE_AND_NAIL.getKey().location().getPath(), () -> new ComfortsBaseItem(ComfortsBlocks.ROPE_AND_NAIL.get()));

    static {
        ComfortsBlocks.SLEEPING_BAGS.getEntries()
                .forEach(entry -> ITEMS.register(entry.getKey().location().getPath(), () -> new SleepingBagItem(entry.get())));
        ComfortsBlocks.HAMMOCKS.getEntries()
                .forEach(entry -> ITEMS.register(entry.getKey().location().getPath(), () -> new HammockItem(entry.get())));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

}
