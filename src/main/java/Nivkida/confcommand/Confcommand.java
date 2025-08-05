package Nivkida.confcommand;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

@Mod(Confcommand.MODID)
public class Confcommand {
    public static final String MODID = "confcommand";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Config COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Config(builder);
        COMMON_SPEC = builder.build();
    }

    public Confcommand() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(CommandHandler.class);
    }

    public static class Config {
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> allowedCommands;

        public Config(ForgeConfigSpec.Builder builder) {
            builder.comment("Allowed commands without OP").push("commands");
            allowedCommands = builder
                    .comment("List of command names (no slash, no args)")
                    .defineList(
                            "allowed_commands",
                            Arrays.asList("give", "gamemode"),
                            o -> o instanceof String
                    );
            builder.pop();
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommandHandler {
        @SubscribeEvent
        public static void onCommand(CommandEvent event) {
            String input = event.getParseResults().getReader().getString();
            if (input.startsWith("/")) {
                input = input.substring(1);
            }

            int spaceIndex = input.indexOf(' ');
            String commandName = spaceIndex == -1 ? input : input.substring(0, spaceIndex);

            List<? extends String> allowed = COMMON.allowedCommands.get();
            if (allowed == null) {
                LOGGER.warn("Allowed commands list is null");
                return;
            }

            // Проверяем наличие прав у источника
            boolean hasPermission = event.getParseResults().getContext().getSource().hasPermission(4);

            if (allowed.contains(commandName) && !hasPermission) {
                event.setCanceled(true);
                try {
                    event.getParseResults().getContext().getDispatcher().execute(
                            input,
                            event.getParseResults().getContext().getSource().withPermission(4)
                    );
                } catch (Exception e) {
                    LOGGER.error("Error executing command: {}", input, e);
                }
            }
        }
    }
}