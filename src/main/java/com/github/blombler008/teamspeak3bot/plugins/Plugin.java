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

import java.io.File;
import java.util.Properties;

public class Plugin {

    Teamspeak3Bot instance;
    boolean enabled = false;
    File dataFolder;
    PluginDescription pluginDescription;
    Properties properties;

    public Plugin() {
    }

    public Plugin(PluginDescription pluginDescription, Properties properties) {
        this.pluginDescription = pluginDescription;
        this.properties = properties;
    }

    public PluginDescription getPluginDescription() {
        return pluginDescription;
    }

    public Teamspeak3Bot getInstance() {
        return instance;
    }

    public Properties getProperties() {
        return properties;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\'");
        sb.append(this.getClass().getTypeName());
        sb.append("\': {");
        int size = properties.size();
        for (int i = 0; i < size; i++) {
            String key = (String) properties.keySet().iterator().next();
            sb.append("\'");
            sb.append(key);
            sb.append("\': \'");
            sb.append(properties.getProperty(key));
            sb.append("\'");
            if (i + 1 != size)
                sb.append(", ");
            properties.keySet().remove(key);
        }
        sb.append("}}");
        return sb.toString();
    }
}
