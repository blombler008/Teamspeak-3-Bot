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

import com.github.blombler008.teamspeak3bot.plugins.Plugin;
import com.github.blombler008.teamspeak3bot.utils.StringUtils;
import com.github.blombler008.teamspeak3bot.utils.Validator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static Map<File, Plugin> dataFolders = new HashMap<>();
    private Map<File, ConfigFile> configFileMap = new HashMap<>();

    private Plugin plugin;
    private File dataFolder;

    private ConfigManager() {
    }

    public ConfigManager(File dataFolder) {
        if (Validator.notNull(dataFolder))
            throw new NullPointerException("dataFolder is NULL!");
        File dataF = plugin.getDataFolder();
        if (dataFolders.containsKey(dataFolder)) {
            Plugin p = dataFolders.get(dataFolder);
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

    public static void add(File dataFolder, Plugin plugin) {
        if (dataFolders.containsValue(plugin) || dataFolders.containsKey(dataFolder))
            return;
        dataFolders.put(dataFolder, plugin);
    }

    public ConfigFile newConfig(File dir, String name) {
        ConfigFile config = ConfigFile.newConfigFile(dir, name); //TODO: upgrade to Yaml
        configFileMap.put(dir, config);
        return config;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public Map<File, ConfigFile> getConfigFileMap() {
        return new HashMap<>(configFileMap);
    }
}
