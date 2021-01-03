package top.theillusivec4.comforts.common.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;

public interface SleepTrackerComponent extends Component {

  long getSleepTime();

  void setSleepTime(long time);

  long getWakeTime();

  void setWakeTime(long wakeTime);

  long getTiredTime();

  void setTiredTime(long tiredTime);

  Optional<BlockPos> getAutoSleepPos();

  void setAutoSleepPos(BlockPos pos);
}
