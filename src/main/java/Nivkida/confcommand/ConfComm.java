package Nivkida.confcommand;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ConfComm {
    public static final ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ALLOWED_COMMANDS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Configcommands Mod Configuration");

        ALLOWED_COMMANDS = builder
                .comment("List of commands that can be executed without OP status")
                .defineList("allowed_commands", Arrays.asList("give", "gamemode"), o -> o instanceof String);

        SPEC = builder.build();
    }
}
