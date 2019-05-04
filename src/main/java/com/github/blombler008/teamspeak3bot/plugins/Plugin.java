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

package com.github.blombler008.teamspeak3bot.plugins;

import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.config.ConfigManager;
import com.github.blombler008.teamspeak3bot.config.YamlConfiguration;

import java.io.File;

public class Plugin {

    Teamspeak3Bot instance;
    boolean enabled = false;
    File dataFolder;
    PluginDescription pluginDescription;
    YamlConfiguration configuration;
    ConfigManager configmanager;

    public Plugin() {
    }

    public Plugin(PluginDescription pluginDescription, YamlConfiguration configuration) {
        this.pluginDescription = pluginDescription;
        this.configuration = configuration;
    }

    public PluginDescription getPluginDescription() {
        return pluginDescription;
    }

    public Teamspeak3Bot getInstance() {
        return instance;
    }

    public YamlConfiguration getYamlConfiguration() {
        return configuration;
    }

    public String getVersion() {
        return getPluginDescription().getVersion();
    }

    public String getName() {
        return getPluginDescription().getName();
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public boolean setEnabled() {
        enabled = true;
        this.onEnable();
        return enabled;
    }

    public boolean setDisabled() {
        enabled = false;
        onDisable();
        return enabled;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onLoad() {
    }

    public ConfigManager getConfigManager() {
        return configmanager;
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "instance=" + instance +
                ", enabled=" + enabled +
                ", dataFolder=" + dataFolder +
                ", pluginDescription=" + pluginDescription +
                ", configuration=" + configuration +
                '}';
    }
}
