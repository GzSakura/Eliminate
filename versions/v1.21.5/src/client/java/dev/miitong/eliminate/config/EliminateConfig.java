package dev.miitong.eliminate.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EliminateConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "eliminate.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enabled = true;
    public int cullingDistance = 32;
    public boolean debugMode = false;
    public int updateSpeed = 20;
    public boolean syncWithSodium = false;
    public boolean fovCullingEnabled = true;
    public boolean aggressiveMountainCulling = true;
    public boolean transparencyAwareness = true;
    public boolean horizontalCulling = true;
    public boolean biomeAwareCulling = true;
    public boolean dynamicCullingDistance = false;
    public int targetFps = 60;
    public int minDynamicDistance = 16;
    public int maxDynamicDistance = 64;

    private static EliminateConfig instance;

    public static EliminateConfig getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                instance = GSON.fromJson(reader, EliminateConfig.class);
            } catch (IOException e) {
                instance = new EliminateConfig();
            }
        } else {
            instance = new EliminateConfig();
            save();
        }
    }

    public static void save() {
        if (instance == null) instance = new EliminateConfig();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
