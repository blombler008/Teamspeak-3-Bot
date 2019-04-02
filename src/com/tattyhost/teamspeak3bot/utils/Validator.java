package com.tattyhost.teamspeak3bot.utils;

import com.tattyhost.teamspeak3bot.Teamspeak3Bot;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class Validator {

    public static boolean notNull(Object obj) {
         return (obj == null);
    }

    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            Teamspeak3Bot.getLogger().error("Invalid Path: " + path);
            return false;
        }
        return true;
    }
    public static boolean isDirectory(String path) {
        if(!Files.isDirectory(Paths.get(path))) {
            Teamspeak3Bot.getLogger().error("Path is not a Directory: " + path);
            return false;
        }
        return true;
    }
}
