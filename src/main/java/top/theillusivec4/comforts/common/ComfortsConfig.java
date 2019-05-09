package top.theillusivec4.comforts.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.comforts.Comforts;

public class ComfortsConfig {

    private static final String CONFIG_PREFIX = "gui." + Comforts.MODID + ".config.";

    public static class Server {

        public final ForgeConfigSpec.BooleanValue autoUse;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("server");

            autoUse = builder
                    .comment("Set to true to automatically use sleeping bags when placed")
                    .translation(CONFIG_PREFIX + "autoUse")
                    .define("autoUse", true);
        }
    }

    public static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;
    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }
}
