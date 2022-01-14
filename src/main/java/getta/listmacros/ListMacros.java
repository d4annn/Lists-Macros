package getta.listmacros;

import com.mojang.brigadier.CommandDispatcher;
import getta.listmacros.config.Config;
import getta.listmacros.screen.ConfigScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class ListMacros implements ModInitializer {

    public static List<Macro> macros = new ArrayList<>();

    @Override
    public void onInitialize() {

        Config.checkFile();
        macros = Config.loadFile();

        for(Macro macro : macros) {
            macro.register(ClientCommandManager.DISPATCHER);
        }

        registerMainCommand(ClientCommandManager.DISPATCHER);
    }

    private void registerMainCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("listmacros")
                .executes(context -> {
                    MinecraftClient.getInstance().setScreen(new ConfigScreen(MinecraftClient.getInstance().currentScreen));
                    return 1;
                }));
    }
}
