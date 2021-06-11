package top.theillusivec4.comforts.common.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import top.theillusivec4.comforts.common.ComfortsMod;
import top.theillusivec4.comforts.common.config.AutoConfigPlugin;
import top.theillusivec4.comforts.common.config.ComfortsConfigData;

public class ComfortsModMenu implements ModMenuApi {

  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {

    if (ComfortsMod.isConfigLoaded) {
      return AutoConfigPlugin::getConfigScreen;
    } else {
      return screen -> null;
    }
  }
}
