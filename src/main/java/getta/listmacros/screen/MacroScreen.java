package getta.listmacros.screen;

import getta.listmacros.ListMacros;
import getta.listmacros.Macro;
import getta.listmacros.config.Config;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.awt.*;

public class MacroScreen extends Screen {

    private Screen screen;
    private Macro macro;
    private ButtonWidget backButton;
    private ButtonWidget deleteButton;
    private ButtonWidget addSubCommandButton;
    private TextFieldWidget addSubCommandInput;
    private TextFieldWidget editSelectedInput;
    private ButtonWidget applyEdit;
    private ButtonWidget deleteSubCommand;
    private String selected;
    private int macroIndex;

    public MacroScreen(Screen screen, Macro macro) {
        super(new TranslatableText( String.format("%s screen", macro.getDefaultCommand())));
        this.screen = screen;
        this.macro = macro;
        this.macroIndex = ListMacros.macros.indexOf(macro);
    }

    protected void init() {
        super.init();

        this.backButton = this.addDrawableChild(new ButtonWidget(5, this.height - 30, 70, 20, Text.of("Back"), (button) -> {
            this.client.setScreen(new ConfigScreen(screen));
        }));

        this.deleteButton = this.addDrawableChild(new ButtonWidget(this.width - 75, this.height - 30, 70, 20, Text.of("Delete"), (button) -> {
            try {
                ListMacros.macros.remove(this.macro);
                this.client.setScreen(new ConfigScreen(screen));
                Config.writeToFile(ListMacros.macros);
            }catch (NullPointerException ignored) {}
        }));

        this.addSubCommandButton = this.addDrawableChild(new ButtonWidget(5, 35, 90, 20, Text.of("Add subcommand"), (button) -> {
            if(!addSubCommandInput.getText().trim().equals("")) {
                if(!addSubCommandInput.getText().trim().startsWith("/")) {
                    addSubCommandInput.setText("/" + addSubCommandInput.getText());
                }
                ListMacros.macros.get(this.macroIndex).addSubCommand(addSubCommandInput.getText().trim());
                Config.writeToFile(ListMacros.macros);
                resetScreen();
            }
        }));

        this.applyEdit = this.addDrawableChild(new ButtonWidget(this.width - 105, 45, 90, 20, Text.of("Apply edit"), (buttonWidget) -> {

            if(!addSubCommandInput.getText().startsWith(" ") && this.selected != null) {

                try {
                    if(!this.editSelectedInput.getText().trim().startsWith("/")) {
                        this.editSelectedInput.setText("/" + this.editSelectedInput.getText());
                    }
                    ListMacros.macros.get(this.macroIndex).getSubCommands().set(ListMacros.macros.get(macroIndex).getSubCommands().indexOf(this.selected), this.editSelectedInput.getText());
                    Config.writeToFile(ListMacros.macros);
                    resetScreen();
                    macro.register(ClientCommandManager.DISPATCHER);
                } catch (IndexOutOfBoundsException e) {
                    this.editSelectedInput.setText("Error");
                }
            }
        }));

        this.deleteSubCommand = this.addDrawableChild(new ButtonWidget(this.width - 105, 70, 90, 20, Text.of("Delet selected"), (button) -> {
            if(this.selected != null && !this.selected.equals("defaultCommand")) {
                ListMacros.macros.get(this.macroIndex).getSubCommands().remove(this.selected);
                Config.writeToFile(ListMacros.macros);
                resetScreen();
                macro.register(ClientCommandManager.DISPATCHER);
            }
        }));

        this.addSubCommandInput = new TextFieldWidget(this.textRenderer, 5, 15, 100, textRenderer.fontHeight + 5, Text.of(""));
        this.editSelectedInput = new TextFieldWidget(this.textRenderer, this.width - 115, 25, 100, textRenderer.fontHeight + 5, Text.of(""));

        this.addSelectableChild(this.addSubCommandInput);
        this.addSelectableChild(this.editSelectedInput);

        if(!macro.getSubCommands().isEmpty()) {

            int xQuantity = 0;
            int yQuantity = 0;
            int totalQuantity = 0;

            for (String subCommand : this.macro.getSubCommands()) {
                int finalTotalQuantity = totalQuantity;
                this.addDrawableChild(new ButtonWidget(135 + xQuantity, 45 + yQuantity, 20, 20, Text.of(""), (buttonWidget) -> {
                    this.selected = this.macro.getSubCommands().get(finalTotalQuantity);

                }));
                totalQuantity++;

                yQuantity += 25;

                if(yQuantity + 30 >= this.height - 100) {
                    yQuantity = 0;
                    xQuantity += 93;
                }
            }
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);

        drawCenteredText(matrices, this.textRenderer, this.macro.getDefaultCommand() + " screen", this.width / 2, 10, new Color(255, 255, 255).getRGB());
        drawCenteredText(matrices, this.textRenderer, "Selected: " + this.selected, this.width - 80, 10, new Color(255, 255 ,255).getRGB());
        drawCenteredText(matrices, this.textRenderer, "Edit subcommand text", this.width - 180, 28, new Color(255, 255 ,255).getRGB());

        super.render(matrices, mouseX, mouseY, delta);

        this.addSubCommandInput.render(matrices, mouseX, mouseY, delta);
        this.editSelectedInput.render(matrices, mouseX, mouseY, delta);

        if(!macro.getSubCommands().isEmpty()) {

            int xQuantity = 0;
            int yQuantity = 0;

            for(String subCommand : this.macro.getSubCommands()) {
                drawStringWithShadow(matrices, this.textRenderer, subCommand, 160 + xQuantity, 50 + yQuantity, new Color(255, 255, 255).getRGB());
                yQuantity += 25;

                if(yQuantity + 30 >= this.height - 100) {
                    yQuantity = 0;
                    xQuantity += 93;
                }
            }
        }
    }

    private void resetScreen() {

        this.client.setScreen(new ConfigScreen(screen));
        this.client.setScreen(new MacroScreen(this.screen, this.macro));
    }

}
