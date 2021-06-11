package top.theillusivec4.comforts.common;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import top.theillusivec4.comforts.common.component.PlayerSleepTracker;
import top.theillusivec4.comforts.common.component.SleepTrackerComponent;

public class ComfortsComponents implements EntityComponentInitializer {

  public static final ComponentKey<SleepTrackerComponent> SLEEP_TRACKER =
      ComponentRegistry.getOrCreate(ComfortsMod.id("sleep_tracker"), SleepTrackerComponent.class);

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerForPlayers(SLEEP_TRACKER, PlayerSleepTracker::new,
        RespawnCopyStrategy.ALWAYS_COPY);
  }
}
