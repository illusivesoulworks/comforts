package com.illusivesoulworks.comforts;

import com.illusivesoulworks.spectrelib.config.SpectreLibInitializer;
import org.quiltmc.loader.api.ModContainer;

public class ComfortsConfigInitializer implements SpectreLibInitializer {

  public void onInitializeConfig() {
    this.onInitializeConfig(null);
  }

  @Override
  public void onInitializeConfig(ModContainer modContainer) {
    ComfortsCommonMod.initConfig();
  }
}
