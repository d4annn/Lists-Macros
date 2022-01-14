package getta.listmacros;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.List;

public class Macro {

    private String defaultCommand;
    private List<String> subCommands;

    public Macro(String defaultCommand, List<String> subCommands) {
        this.defaultCommand = defaultCommand;
        this.subCommands = subCommands;
    }

    public String getDefaultCommand() {
        return defaultCommand;
    }

    public List<String> getSubCommands() {
        return subCommands;
    }

    public void addSubCommand(String commandToAdd) {
        this.subCommands.add(commandToAdd);
    }

    public int execute() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        for(String command : this.subCommands) {
            player.sendChatMessage(command);
        }

        return 1;
    }

    public void setDefaultCommand(String defaultCommand) {
        this.defaultCommand = defaultCommand;
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal(defaultCommand)
                .executes(context -> execute()));
    }
}

