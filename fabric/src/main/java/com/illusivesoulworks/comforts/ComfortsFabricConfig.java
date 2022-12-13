package com.illusivesoulworks.comforts;

import com.illusivesoulworks.spectrelib.config.SpectreConfigInitializer;

public class ComfortsFabricConfig implements SpectreConfigInitializer {

  @Override
  public void onInitialize() {
    ComfortsCommonMod.initConfig();
  }
}
