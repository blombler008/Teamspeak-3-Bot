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

package com.github.blombler008.teamspeak3bot.utils;

import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.config.FileConfiguration;
import com.github.blombler008.teamspeak3bot.config.YamlConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

public class Language {

    public static final String LANGUAGE = "[ == Language] > ";
    public static final String MAIN = "[ == Main] > ";
    public static final String PLUGIN = "[ == Plugin] > ";
    public static final String CONSOLE = "[ == Console] > ";
    public static final String EVENT = "[ == Event] > ";
    public static final String COMMAND = "[ == Command] > ";
    public static final String BOT = "[ == Bot] > ";
    private static Languages currentLanguage;
    private String languageProperty;
    private Teamspeak3Bot instance;

    private Language() {
    }

    private Language(Teamspeak3Bot instance) {
        this.instance = instance;
    }

    public static Languages getNew(String lang, Teamspeak3Bot instance) {
        if (Validator.notNull(currentLanguage)) {

            Language language = new Language(instance);
            language.languageProperty = lang;
            Languages l = Languages.fromName(lang);
            currentLanguage = l != null ? l : Languages.ENGLISH;

        }
        return currentLanguage;
    }

    public static String get(String property) {
        return currentLanguage.getConfiguration().getString(property);
    }

    public String getLanguageProperty() {
        return languageProperty;
    }


    public enum Languages {
        ENGLISH("english", "english.ini");

        private YamlConfiguration configuration;
        private String propertyName;

        Languages(String propertyName, String fileName) {
            this.propertyName = propertyName;
            try {

                this.configuration = new YamlConfiguration(new FileConfiguration(ClassLoader.getSystemResource("lang/" + fileName).openStream()));
                URL url = ClassLoader.getSystemResource("lang/" + fileName);
                Teamspeak3Bot.getInstance().debug(LANGUAGE, StringUtils.replaceStringWith("URL of language file: %url%", "url", url.toString()));

                Teamspeak3Bot.getInstance().info(StringUtils.replaceStringWith("loaded properties: %property%", "property", propertyName));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static Languages fromName(String lang) {
            for (Languages d : Languages.values()) {
                if (lang.equalsIgnoreCase(d.propertyName)) {
                    return d;
                }
            }
            return null;
        }

        public String getConfigurationName() {
            return propertyName;
        }

        public YamlConfiguration getConfiguration() {
            return configuration;
        }
    }

}
