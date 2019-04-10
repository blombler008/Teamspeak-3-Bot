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

    private ConfigFile(File dir, String name) {
        configFile = new File(dir, name);
        load();
    }

    public static ConfigFile newConfigFile(File dir, String name) {
        ConfigFile ret = new ConfigFile(dir, name);
        return ret;
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
