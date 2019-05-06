package top.theillusivec4.comforts;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import top.theillusivec4.comforts.client.TileEntitySleepingBagRenderer;
import top.theillusivec4.comforts.common.EventHandlerCommon;
import top.theillusivec4.comforts.common.block.BlockSleepingBag;
import top.theillusivec4.comforts.common.capability.CapabilitySleepData;
import top.theillusivec4.comforts.common.init.ComfortsBlocks;
import top.theillusivec4.comforts.common.init.ComfortsTileEntities;
import top.theillusivec4.comforts.common.item.ItemSleepingBag;
import top.theillusivec4.comforts.common.tileentity.TileEntitySleepingBag;

import java.util.Arrays;

@Mod(Comforts.MODID)
public class Comforts {

    public static final String MODID = "comforts";

    public static final ItemGroup CREATIVE_TAB = new ItemGroup(-1, "comforts") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(ComfortsBlocks.SLEEPING_BAGS.get(EnumDyeColor.RED));
        }
    };

    public Comforts() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(this::setupClient);
    }

    private void setup(FMLCommonSetupEvent evt) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
        CapabilitySleepData.register();
    }

    private void setupClient(FMLClientSetupEvent evt) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySleepingBag.class, new TileEntitySleepingBagRenderer());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> evt) {
            IForgeRegistry<Block> registry = evt.getRegistry();
            Arrays.stream(EnumDyeColor.values()).forEach(color -> {
                BlockSleepingBag sleepingBag = new BlockSleepingBag(color);
                ComfortsBlocks.SLEEPING_BAGS.put(color, sleepingBag);
                registry.register(sleepingBag);
            });
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> evt) {
            IForgeRegistry<Item> registry = evt.getRegistry();
            ComfortsBlocks.SLEEPING_BAGS.values().forEach(block -> registry.register(new ItemSleepingBag(block)));
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> evt) {
            evt.getRegistry().registerAll(ComfortsTileEntities.SLEEPING_BAG_TE);
        }
    }
}
