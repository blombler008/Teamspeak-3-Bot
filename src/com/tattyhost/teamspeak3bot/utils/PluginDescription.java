package com.tattyhost.teamspeak3bot.utils;

public class PluginDescription {

    private String version;
    private String description;
    private String name;

    public PluginDescription(String version, String description, String name) {
        this.description = description;
        this.name = name;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
