package com.illusivesoulworks.comforts;

import com.illusivesoulworks.spectrelib.config.SpectreLibInitializer;
import org.quiltmc.loader.api.ModContainer;

public class ComfortsConfigInitializer implements SpectreLibInitializer {

  @Override
  public void onInitializeConfig(ModContainer modContainer) {
    ComfortsCommonMod.initConfig();
  }
}
