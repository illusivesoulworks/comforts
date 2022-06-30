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

package com.illusivesoulworks.comforts;

import com.illusivesoulworks.comforts.common.ComfortsConfig;
import com.illusivesoulworks.comforts.common.ComfortsRegistry;
import com.illusivesoulworks.spectrelib.config.SpectreConfig;
import com.illusivesoulworks.spectrelib.config.SpectreConfigLoader;

public class ComfortsCommonMod {

  public static void init() {
    SpectreConfig config =
        SpectreConfigLoader.add(SpectreConfig.Type.SERVER, ComfortsConfig.SERVER_SPEC,
            ComfortsConstants.MOD_ID);
    config.addLoadListener(configFile -> ComfortsConfig.reload());
    config.addReloadListener(configFile -> ComfortsConfig.reload());
    ComfortsRegistry.init();
  }
}