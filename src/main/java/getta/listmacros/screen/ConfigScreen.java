package getta.listmacros.screen;

import getta.listmacros.ListMacros;
import getta.listmacros.Macro;
import getta.listmacros.config.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {

    private Screen screen;
    private ButtonWidget addButton;
    private ButtonWidget backButton;
    private ButtonWidget addSubCommand;
    private TextFieldWidget defaultCommandInput;
    private TextFieldWidget subCommandsInput;
    private Macro creatingMacro;

    public ConfigScreen(Screen screen) {
        super(new TranslatableText("Macro lists screen"));
        this.screen = screen;
    }

    protected void init() {
        super.init();

        this.backButton = this.addDrawableChild(new ButtonWidget(5, this.height - 30, 70, 20, Text.of("Back"), (button) -> {
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }));

        this.addSubCommand = this.addDrawableChild(new ButtonWidget(5, 55, 90, 20, Text.of("Add subcommand"), (button) -> {
            if(!this.subCommandsInput.getText().trim().startsWith(" ") && !this.subCommandsInput.getText().equals("")) {

                if(!this.subCommandsInput.getText().trim().startsWith("/")) {
                    this.subCommandsInput.setText("/" + this.subCommandsInput.getText());
                }

                if (creatingMacro == null) {
                    ArrayList<String> macroSubCommandList = new ArrayList<>();
                    creatingMacro = new Macro("", macroSubCommandList);
                }
                creatingMacro.addSubCommand(this.subCommandsInput.getText().trim());
                this.subCommandsInput.setText("");
            }
            }));

        this.addButton = this.addDrawableChild(new ButtonWidget(this.width - 75, this.height - 30, 70, 20, Text.of("Create"), (button) -> {
            if(!this.defaultCommandInput.getText().trim().startsWith(" ")) {

                boolean reapeted = false;
                for (Macro macro : ListMacros.macros) {
                    if (macro.getDefaultCommand().equalsIgnoreCase(this.defaultCommandInput.getText().trim())) {
                        reapeted = true;
                    }
                }

                if (!reapeted) {

                    if(this.defaultCommandInput.getText().trim().startsWith("/")) {
                        this.defaultCommandInput.setText(defaultCommandInput.getText().replace("/", ""));
                    }

                    Macro macro;
                    try {
                        macro = new Macro(this.defaultCommandInput.getText().trim(), creatingMacro.getSubCommands());
                    } catch (NullPointerException e) {
                        macro = new Macro(this.defaultCommandInput.getText().trim(), new ArrayList<>());
                    }
                    creatingMacro = null;
                    defaultCommandInput.setText("");
                    ListMacros.macros.add(macro);
                    Config.writeToFile(ListMacros.macros);
                    macro.register(ClientCommandManager.DISPATCHER);
                    resetScreen();
                }
            }
        }));

        this.defaultCommandInput = new TextFieldWidget(this.textRenderer, 5, 15, 85, textRenderer.fontHeight + 5, Text.of(""));
        this.subCommandsInput = new TextFieldWidget(this.textRenderer, 5, 35, 100, textRenderer.fontHeight + 5, Text.of(""));

        this.addSelectableChild(this.defaultCommandInput);
        this.addSelectableChild(this.subCommandsInput);

        int xQuantity = 0;
        int yQuantity = 0;

        for(Macro macro : ListMacros.macros) {

            this.addDrawableChild(new ButtonWidget(130 + xQuantity, 50 + yQuantity, 75, 20, Text.of(macro.getDefaultCommand()), (button) -> {
                this.client.setScreen(new MacroScreen(screen, macro));
            }));
            yQuantity += 25;

            if(yQuantity + 30 >= this.height - 30) {
                yQuantity = 0;
                xQuantity += 93;
            }
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, new Color(255, 255, 255).getRGB());
        super.render(matrices, mouseX, mouseY, delta);
        this.defaultCommandInput.render(matrices, mouseX, mouseY, delta);
        this.subCommandsInput.render(matrices, mouseX, mouseY, delta);

        if(this.creatingMacro != null && !this.creatingMacro.getSubCommands().isEmpty()) {
            int xQuantity = 0;
            int yQuantity = 0;

            for(String macro : this.creatingMacro.getSubCommands()) {
                drawStringWithShadow(matrices, textRenderer, macro, 5 + xQuantity, 100 + yQuantity, Color.WHITE.getRGB());
                yQuantity += 15;

                if(yQuantity + 30 >= this.height - 120) {
                    yQuantity = 0;
                    xQuantity += 120;
                }
            }
        }

    }

    private void resetScreen() {

        this.client.setScreen(new ConfigScreen(this.screen));
    }


}
