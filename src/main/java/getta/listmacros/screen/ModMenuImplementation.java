package getta.listmacros.screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuImplementation implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {

        return screen -> new Screen(Text.of("")) {
            @Override
            protected void init() {
                ConfigScreen configScreen = new ConfigScreen(screen);
                this.client.setScreen(configScreen);
            }
        };
    }
}
