package com.tattyhost.teamspeak3bot.utils;

import java.io.File;
import java.util.Properties;

public abstract class Plugin {

    protected File dataFolder;
    protected PluginDescription pluginDescription;
    protected Properties properties;
    protected boolean enabled = false;

    public Plugin(){}

    public Plugin(PluginDescription pluginDescription, Properties properties) {
        this.pluginDescription = pluginDescription;
        this.properties = properties;
    }

    public PluginDescription getPluginDescription() {
        return pluginDescription;
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

    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void onLoad();

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\'" + this.getClass().getTypeName() + "\': {");
        int size = properties.size();
        for(int i=0; i < size; i++) {
            String key = (String) properties.keySet().iterator().next();
            sb.append("\'" + key + "\': \'" + properties.getProperty(key) + "\'");
            if (i + 1 != size)
                sb.append(", ");
            properties.keySet().remove(key);
        }
        sb.append("}}");
        return sb.toString();
    }
}
