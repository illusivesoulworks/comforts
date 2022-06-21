/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

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

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.comforts.ComfortsMod;

public class ComfortsLootProvider extends LootTableProvider {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping()
      .create();

  private final DataGenerator dataGenerator;
  private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> lootTables = ImmutableList
      .of(Pair.of(ComfortsBlockLootTables::new, LootContextParamSets.BLOCK));

  public ComfortsLootProvider(DataGenerator generatorIn) {
    super(generatorIn);
    this.dataGenerator = generatorIn;
  }

  private static Path getPath(Path pathIn, ResourceLocation id) {
    return pathIn.resolve("data/" + id.getNamespace() + "/loot_tables/" + id.getPath() + ".json");
  }

  @Override
  public void run(@Nonnull CachedOutput cache) {
    final Path path = this.dataGenerator.getOutputFolder();
    Map<ResourceLocation, LootTable> map = Maps.newHashMap();
    this.lootTables
        .forEach((lootPair) -> lootPair.getFirst().get().accept((resourceLocation, lootTable) -> {
          if (map.put(resourceLocation, lootTable.setParamSet(lootPair.getSecond()).build())
              != null) {
            throw new IllegalStateException("Duplicate loot table " + resourceLocation);
          }
        }));
    ValidationContext validationtracker = new ValidationContext(LootContextParamSets.ALL_PARAMS,
        (resourceLocation) -> null, map::get);
    validate(map, validationtracker);
    Multimap<String, String> multimap = validationtracker.getProblems();

    if (!multimap.isEmpty()) {
      multimap.forEach((problemPath, problem) -> LOGGER
          .warn("Found validation problem in " + problemPath + ": " + problem));
      throw new IllegalStateException("Failed to validate loot tables, see logs");
    } else {
      map.forEach((resourceLocation, lootTable) -> {
        final Path path1 = getPath(path, resourceLocation);

        try {
          DataProvider.saveStable(cache, LootTables.serialize(lootTable), path1);
        } catch (IOException ioexception) {
          LOGGER.error("Couldn't save loot table {}", path1, ioexception);
        }
      });
    }
  }

  @Override
  @Nonnull
  public String getName() {
    return ComfortsMod.MOD_ID + "LootTables";
  }
}
