package com.tattyhost.teamspeak3bot.utils;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.tattyhost.teamspeak3bot.CommandSource;
import com.tattyhost.teamspeak3bot.Teamspeak3Bot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {

    private static Map<String, Command> commands = new HashMap<>();
    private static char customChar;
    private static TS3Api api;

    public CommandManager(TS3Api api, char customChar) {
        CommandManager.customChar = customChar;
        CommandManager.api = api;
    }

    public static void registerNewCommand(String str, Command cmd) {
        commands.put(str, cmd);
    }

    static boolean executeCommand(String[] args, CommandSource source, int clientId, boolean run) {
        List<String> aList = Arrays.asList(args);
        String cmdString = aList.get(0);

        if (!Validator.notNull(api)) {
            if (commands.containsKey(cmdString)) {
                if (run)
                    parseRun(args, source, clientId);
                return true;
            } else {
                if (source == CommandSource.CONSOLE) {
                    Teamspeak3Bot.getLogger()
                        .info(Language.COMMAND + "Unknown Command: " + cmdString);
                    Teamspeak3Bot.getLogger()
                        .info(Language.COMMAND + "Please use help or ? for help");
                } else {
                    api.sendPrivateMessage(clientId, "Unknown Command: " + customChar + cmdString);
                    api.sendPrivateMessage(clientId,
                        "Please use " + customChar + "help or " + customChar + "? for help");
                }
            }
        } else
            Teamspeak3Bot.getLogger().error(Language.COMMAND + "API is null !!!!");
        return false;
    }

    private static void parseRun(String[] args, CommandSource source, int id) {
        List<String> aList = Arrays.asList(args);

        String label = args[0];
        Command cmd = commands.get(label);

        String[] arguments = {};
        cmd.run(source, id, label, aList.toArray(arguments));
    }

    public static boolean checkCommand(String cmd, CommandSource source, int invokerId) {
        return checkCommand(new String[] {cmd}, source, invokerId);
    }

    public static boolean checkCommand(String[] cmdStringArray, CommandSource source,
        int invokerId) {
        List<String> aList = Arrays.asList(cmdStringArray);

        String[] arguments = {};
        String consoleMessage = Language.COMMAND + "Command From %source%: " + aList.get(0);

        aList.set(0, aList.get(0).replaceFirst(String.valueOf(customChar), ""));
        consoleMessage = consoleMessage.replaceAll("%source%", source.toString().toLowerCase());

        Teamspeak3Bot.debug(consoleMessage);
        Teamspeak3Bot.debug(Language.COMMAND + "Custom Prefix Key: " + customChar);

        return executeCommand(aList.toArray(arguments), source, invokerId, false);
    }


    public static String getCommandFromArray(String[] cmdArray) {
        return cmdArray[0].replaceFirst(String.valueOf(customChar), "");
    }
}
