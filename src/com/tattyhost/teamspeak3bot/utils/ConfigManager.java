package com.tattyhost.teamspeak3bot.utils;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static Map<File, Plugin> dataFolders = new HashMap<>();
    private Map<File, ConfigFile> configFileMap = new HashMap<>();

    private Plugin plugin;
    private File dataFolder;

    private ConfigManager(){}

    public ConfigManager(@NotNull File dataFolder) {
        if(dataFolders.containsKey(dataFolder)) {
            Plugin p = dataFolders.get(dataFolder);
            if(p.dataFolder == dataFolder) {
                this.plugin = p;
                this.dataFolder = dataFolder;
                return;
            }
        }
        throw new NullPointerException("DataFolder \"" + dataFolder.getName() + "\" is not available!");
    }

    public ConfigFile newConfig(File dir, String name) {
        ConfigFile config = new ConfigFile(dir, name);
        configFileMap.put(dir, config);
        return config;
    }

    protected static void add(File dataFolder, Plugin plugin) {
        dataFolders.put(dataFolder, plugin);
    }

    public static Map<File, Plugin> getDataFolders() {
        return dataFolders;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public Map<File, ConfigFile> getConfigFileMap() {
        return configFileMap;
    }
}
