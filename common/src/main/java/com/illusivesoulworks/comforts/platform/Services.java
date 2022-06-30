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

import com.illusivesoulworks.comforts.ComfortsConstants;
import com.illusivesoulworks.comforts.platform.services.IPlatformHelper;
import com.illusivesoulworks.comforts.platform.services.IRegistryFactory;
import com.illusivesoulworks.comforts.platform.services.IRegistryUtil;
import com.illusivesoulworks.comforts.platform.services.ISleepEvents;
import java.util.ServiceLoader;

public class Services {

  public static final ISleepEvents SLEEP_EVENTS = load(ISleepEvents.class);
  public static final IRegistryUtil REGISTRY_UTIL = load(IRegistryUtil.class);
  public static final IRegistryFactory REGISTRY_FACTORY = load(IRegistryFactory.class);
  public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

  public static <T> T load(Class<T> clazz) {

    final T loadedService = ServiceLoader.load(clazz)
        .findFirst()
        .orElseThrow(
            () -> new NullPointerException("Failed to load service for " + clazz.getName()));
    ComfortsConstants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
    return loadedService;
  }
}
