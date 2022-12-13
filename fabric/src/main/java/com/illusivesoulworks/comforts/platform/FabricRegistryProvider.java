/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.comforts.platform;

import com.illusivesoulworks.comforts.common.registry.RegistryObject;
import com.illusivesoulworks.comforts.common.registry.RegistryProvider;
import com.illusivesoulworks.comforts.platform.services.IRegistryFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class FabricRegistryProvider implements IRegistryFactory {

  @Override
  public <T> RegistryProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey,
                                        String modId) {
    return new Provider<>(modId, resourceKey);
  }

  @Override
  public <T> RegistryProvider<T> create(Registry<T> registry, String modId) {
    return new Provider<>(modId, registry);
  }

  private static class Provider<T> implements RegistryProvider<T> {
    private final String modId;
    private final Registry<T> registry;

    private final Set<RegistryObject<T>> entries = new HashSet<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries);

    @SuppressWarnings({"unchecked"})
    private Provider(String modId, ResourceKey<? extends Registry<T>> key) {
      this.modId = modId;

      final var reg = BuiltInRegistries.REGISTRY.get(key.location());
      if (reg == null) {
        throw new RuntimeException("Registry with name " + key.location() + " was not found!");
      }
      registry = (Registry<T>) reg;
    }

    private Provider(String modId, Registry<T> registry) {
      this.modId = modId;
      this.registry = registry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
      final var rl = new ResourceLocation(modId, name);
      final var obj = Registry.register(registry, rl, supplier.get());
      final var ro = new RegistryObject<I>() {
        final ResourceKey<I> key =
            ResourceKey.create((ResourceKey<? extends Registry<I>>) registry.key(), rl);

        @Override
        public ResourceKey<I> getResourceKey() {
          return key;
        }

        @Override
        public ResourceLocation getId() {
          return rl;
        }

        @Override
        public I get() {
          return obj;
        }

        @Override
        public Holder<I> asHolder() {
          return (Holder<I>) registry.getHolderOrThrow((ResourceKey<T>) this.key);
        }
      };
      entries.add((RegistryObject<T>) ro);
      return ro;
    }

    @Override
    public Collection<RegistryObject<T>> getEntries() {
      return entriesView;
    }

    @Override
    public String getModId() {
      return modId;
    }
  }
}
