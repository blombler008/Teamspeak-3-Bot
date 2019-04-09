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

    private String languageProperty;
    private static Languages currentLanguage;

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

                this.properties.load(new InputStreamReader(ClassLoader.getSystemResource("lang/" + fileName).openStream()));
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
