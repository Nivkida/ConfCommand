package Nivkida.confcommand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mod.EventBusSubscriber(
        modid = Confcommand.MODID,
        bus   = Mod.EventBusSubscriber.Bus.FORGE  // обязательно FORGE-шина!
)

public class CommandHandler {
    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        // Полная введённая строка ("/give @p stone" или "give @p stone")
        String full = event.getParseResults().getReader().getString();
        if (full.startsWith("/")) full = full.substring(1);

        // Имя до первого пробела
        String name = full.contains(" ") ? full.substring(0, full.indexOf(' ')) : full;

        LOGGER.info("[ConfigCommand] Detected command → '{}'", name);

        List<? extends String> allowed = Confcommand.COMMON.allowedCommands.get();
        if (allowed == null) {
            LOGGER.warn("[ConfigCommand] allowed_commands == null (конфиг не прочитан?)");
            return;
        }

        if (allowed.contains(name)) {
            LOGGER.info("[ConfigCommand] '{}' is allowed, re-executing with OP", name);
            // Останавливаем родную обработку
            event.setCanceled(true);

            // Повышаем права и выполняем
            CommandDispatcher<CommandSourceStack> disp =
                    event.getParseResults().getContext().getDispatcher();
            CommandSourceStack src =
                    event.getParseResults().getContext().getSource()
                            .withPermission(4); // OP‑4

            try {
                disp.execute(full, src);
            } catch (CommandSyntaxException ex) {
                LOGGER.error("[ConfigCommand] Ошибка исполнения '{}':", full, ex);
            }
        } else {
            LOGGER.info("[ConfigCommand] '{}' not in allowed list → skipping", name);
        }
    }
}