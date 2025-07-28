package Nivkida.confcommand;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Confcommand.MODID)
public class Confcommand {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "confcommand";
    // Directly reference a slf4j logger
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Config COMMON;
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Config(builder);
        COMMON_SPEC = builder.build();
    }

    public Confcommand() {
        // Регистрируем конфиг
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);

        // Подписываемся на событие setup, чтобы потом зарегистрировать слушатель команд
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Регистрируем класс-обработчик команд на FORGE-шине
        MinecraftForge.EVENT_BUS.register(CommandHandler.class);
    }

    // Вложенный класс для хранения конфигурации
    public static class Config {
        // Список разрешённых имён команд (без слеша и без аргументов)
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> allowedCommands;

        public Config(ForgeConfigSpec.Builder builder) {
            builder.comment("Allowed commands without OP").push("commands");
            allowedCommands = builder
                    .comment("List of command names (no slash, no args)")
                    .defineList(
                            "allowed_commands",
                            // Здесь по умолчанию две команды: give и gamemode
                            Arrays.asList("give", "gamemode"),
                            o -> o instanceof String
                    );
            builder.pop();
        }
    }
}