package getta.listmacros.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import getta.listmacros.Macro;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(getConfigDir().getPath() + "\\ListMacros.json");

    public static void writeToFile(List<Macro> macros) {

        checkFile();
        try {
            if(macros != null) {

                BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE));

                GSON.toJson(macros, writer);
                writer.flush();
            }
        } catch (IOException ignored){}
    }

    public static List<Macro> loadFile() {

        checkFile();
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE));

            String line;
            while((line = reader.readLine()) != null) {

                sb.append(line);
            }
            List<Macro> list = GSON.fromJson(sb.toString(),new TypeToken<ArrayList<Macro>>(){}.getType());

            if(list != null ) {
                return list;
            }
        }catch (IOException ignored){}

        return new ArrayList<>();
    }

    public static void checkFile() {
        try {
            CONFIG_FILE.createNewFile();
        } catch (IOException ignored){}
    }

    private static File getConfigDir() {
        return new File(MinecraftClient.getInstance().runDirectory, "config");
    }
}
