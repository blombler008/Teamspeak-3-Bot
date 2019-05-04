/*
 * MIT License
 *
 * Copyright (c) 2019 blombler008
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.blombler008.teamspeak3bot.config;

import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.plugins.JavaPlugin;
import com.github.blombler008.teamspeak3bot.plugins.Plugin;
import com.github.blombler008.teamspeak3bot.utils.StringUtils;
import com.github.blombler008.teamspeak3bot.utils.Validator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static Map<File, JavaPlugin> dataFolders = new HashMap<>();
    private static Teamspeak3Bot instance;
    private Map<FileConfiguration, ConfigFile> configFileMap = new HashMap<>();

    private JavaPlugin plugin;
    private File dataFolder;

    private ConfigManager() {
    }

    public static void setInstance(Teamspeak3Bot instance) {
        if(instance == null) ConfigManager.instance = instance;
    }

    public ConfigManager(File dataFolder, JavaPlugin plugin) {
        if (Validator.notNull(dataFolder))
            throw new NullPointerException("dataFolder is NULL!");
        File dataF = plugin.getDataFolder();
        if (dataFolders.containsKey(dataFolder)) {
            JavaPlugin p = dataFolders.get(dataFolder);
            if (dataF == dataFolder) {
                this.plugin = p;
                this.dataFolder = dataFolder;
                return;
            }
        }
        throw new NullPointerException(StringUtils.replaceStringWith("DataFolder \"%folder%\" is not available!","folder", dataFolder.getName()));
    }

    public static Map<File, Plugin> getDataFolders() {
        return new HashMap<>(dataFolders);
    }

    public static File add(File dataFolder, JavaPlugin plugin) {
        if (dataFolders.containsValue(plugin) || dataFolders.containsKey(dataFolder))
            return null;
        dataFolders.put(dataFolder, plugin);
        return dataFolder;
    }

    public static JavaPlugin get(File dataFolder) {
        if (dataFolders.containsKey(dataFolder))
            return dataFolders.get(dataFolder);
        return null;
    }

    public static JavaPlugin find(FileConfiguration configuration) {
        for(JavaPlugin plugin: dataFolders.values()) {
            for(FileConfiguration config: plugin.getConfigManager().configFileMap.keySet()) {
                if(config.getFile().getName().equals(configuration.getFile().getName())) {
                    return plugin;
                }
            }
        }
        return null;
    }

    public ConfigFile newConfig(FileConfiguration file) {
        FileConfiguration newOne = new FileConfiguration(new File(dataFolder, file.getFile().getName()));
        ConfigFile config = ConfigFile.newConfigFile(new FileConfiguration(newOne));
        configFileMap.put(file, config);
        return config;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public Map<FileConfiguration, ConfigFile> getConfigFileMap() {
        return new HashMap<>(configFileMap);
    }
}
