package top.theillusivec4.comforts.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTable.Builder;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.ValidationResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.comforts.Comforts;

public class ComfortsLootProvider extends LootTableProvider {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping()
      .create();

  private final DataGenerator dataGenerator;
  private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootParameterSet>> lootTables = ImmutableList
      .of(Pair.of(ComfortsBlockLootTables::new, LootParameterSets.BLOCK));

  public ComfortsLootProvider(DataGenerator generatorIn) {
    super(generatorIn);
    this.dataGenerator = generatorIn;
  }

  private static Path getPath(Path pathIn, ResourceLocation id) {
    return pathIn.resolve("data/" + id.getNamespace() + "/loot_tables/" + id.getPath() + ".json");
  }

  @Override
  public void act(@Nonnull DirectoryCache cache) {
    Path path = this.dataGenerator.getOutputFolder();
    Map<ResourceLocation, LootTable> map = Maps.newHashMap();
    this.lootTables
        .forEach((lootPair) -> lootPair.getFirst().get().accept((resourceLocation, lootTable) -> {
          if (map.put(resourceLocation, lootTable.setParameterSet(lootPair.getSecond()).build())
              != null) {
            throw new IllegalStateException("Duplicate loot table " + resourceLocation);
          }
        }));
    ValidationResults validationresults = new ValidationResults();

    map.forEach((resourceLocation, lootTable) -> {
      LootTableManager.func_215302_a(validationresults, resourceLocation, lootTable, map::get);
    });
    Multimap<String, String> multimap = validationresults.getProblems();

    if (!multimap.isEmpty()) {
      multimap.forEach((problemPath, problem) -> LOGGER
          .warn("Found validation problem in " + problemPath + ": " + problem));
      throw new IllegalStateException("Failed to validate loot tables, see logs");
    } else {
      map.forEach((resourceLocation, lootTable) -> {
        Path path1 = getPath(path, resourceLocation);

        try {
          IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), path1);
        } catch (IOException ioexception) {
          LOGGER.error("Couldn't save loot table {}", path1, ioexception);
        }
      });
    }
  }

  @Override
  @Nonnull
  public String getName() {
    return Comforts.MODID + "LootTables";
  }
}
