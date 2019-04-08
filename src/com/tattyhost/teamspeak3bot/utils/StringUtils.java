package com.tattyhost.teamspeak3bot.utils;

import java.util.Properties;

public class StringUtils {
    public static String getValue(String entry) {
        if(entry.startsWith("--")) {
            return entry.split("=")[1];
        }
        return null;
    }

    public static String getKey(String entry) {
        if(entry.startsWith("--")) {
            return entry.split("=")[0];
        }
        return null;
    }

    public static String getValueOf(String [] args, String key) {
        for(String entry: args) {
            if(entry.contains("--" + key)) {
                return getValue(entry);
            }
        }
        return null;
    }

    public static boolean hasKey(String entry, String key) {
        return entry.contains("--" + key);
    }

    public static boolean hasKey(String [] args, String key) {
        for(String entry: args) {
            if(hasKey(entry, key)) return true;
        }
        return false;
    }

    public static boolean hasValue(String [] args, String key, String value) {
        for (String entry: args) {
            if(hasKey(entry, key)) return hasValue(entry, key, value);
        }
        return false;
    }
    public static boolean hasValue(String entry, String key, String value) {


        if(hasKey(entry, key)) return getValue(entry).equals(value);
        return false;
    }

    public static Properties getProperties(String [] args) {
        Properties pro = new Properties();
        for(String arg: args) {
            pro.put(getKey(arg), getValue(arg));
        }
        return pro;
    }
}
