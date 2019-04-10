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

    private Language() {
    }

    public static Languages getNew(String lang) {
        if (Validator.notNull(currentLanguage)) {

            Language language = new Language();
            language.languageProperty = lang;
            Languages l = Languages.fromName(lang);
            currentLanguage = l != null ? l : Languages.ENGLISH;

        }
        return currentLanguage;
    }

    public static String get(String property) {
        return currentLanguage.getProperties().getProperty(property);
    }

    public String getLanguageProperty() {
        return languageProperty;
    }


    public enum Languages {
        ENGLISH("english", "english.ini");

        private Properties properties;
        private String propertyName;

        Languages(String propertyName, String fileName) {
            this.propertyName = propertyName;
            this.properties = new Properties();
            try {

                this.properties.load(new InputStreamReader(
                    ClassLoader.getSystemResource("lang/" + fileName).openStream()));
                URL uri = ClassLoader.getSystemResource("lang/" + fileName);
                Teamspeak3Bot.debug(LANGUAGE + "URL of language file: " + uri);

                Teamspeak3Bot.getLogger().info(LANGUAGE + "loaded properties: " + propertyName);

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

        public String getPropertyName() {
            return propertyName;
        }

        public Properties getProperties() {
            return properties;
        }
    }

}
