package top.theillusivec4.comforts.common.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.comforts.ComfortsMod;
import top.theillusivec4.comforts.common.tileentity.HammockTileEntity;
import top.theillusivec4.comforts.common.tileentity.SleepingBagTileEntity;

public class ComfortsTileEntities {
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ComfortsMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<SleepingBagTileEntity>> SLEEPING_BAG = TILE_ENTITIES.register("sleeping_bag", () -> BlockEntityType.Builder.of(SleepingBagTileEntity::new,
            ComfortsBlocks.SLEEPING_BAGS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));

    public static final RegistryObject<BlockEntityType<HammockTileEntity>> HAMMOCK = TILE_ENTITIES.register("hammock", () -> BlockEntityType.Builder.of(HammockTileEntity::new,
            ComfortsBlocks.HAMMOCKS.getEntries().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));

    public static void register(IEventBus bus) {
        TILE_ENTITIES.register(bus);
    }
}
