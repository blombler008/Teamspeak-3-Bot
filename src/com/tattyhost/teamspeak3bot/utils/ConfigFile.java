package com.tattyhost.teamspeak3bot.utils;

import com.tattyhost.teamspeak3bot.Teamspeak3Bot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigFile {

    private File configFile;
    private Properties properties = new Properties();
    private String comment = "Standard comment!";

    private ConfigFile() {
    }

    protected ConfigFile(File dir, String name) {
        configFile = new File(dir, name);
        load();
    }

    public boolean load() {
        try {
            properties.load(new FileReader(configFile));
            return true;
        } catch (IOException e) {
            Teamspeak3Bot.getLogger().error(
                "Error occurred while reading properties from File: " + configFile.getName());
            e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        try {
            properties.save(new FileOutputStream(configFile), comment);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void reload() {
        save();
        load();
    }

    public String get(String path) {
        return properties.getProperty(path);
    }

    public void set(String path, String value) {
        properties.setProperty(path, value);
    }

    public File getConfigFile() {
        return configFile;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
