package com.illusivesoulworks.comforts;

import com.illusivesoulworks.spectrelib.config.SpectreLibInitializer;

public class ComfortsConfigInitializer implements SpectreLibInitializer {

  @Override
  public void onInitializeConfig() {
    ComfortsCommonMod.initConfig();
  }
}
