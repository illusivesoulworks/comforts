package top.theillusivec4.comforts.common.integration;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import top.theillusivec4.comforts.common.config.ComfortsConfigData;

public class ComfortsModMenu implements ModMenuApi {

  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return screen -> AutoConfig.getConfigScreen(ComfortsConfigData.class, screen).get();
  }
}
