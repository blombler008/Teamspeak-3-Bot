package com.tattyhost.teamspeak3bot.utils;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.tattyhost.teamspeak3bot.CommandSource;
import com.tattyhost.teamspeak3bot.Teamspeak3Bot;

import java.util.*;

public class CommandManager {

    private static Map<String, Command> commands = new HashMap<>();
    private static char customChar;
    private static TS3Api api;

    public CommandManager(TS3Api api, char customChar) {
        this.customChar = customChar;
        this.api = api;
    }

    public static void registerNewCommand(String str, Command cmd) {
        commands.put(str, cmd);
    }

    public static void executeCommand(String[] args, CommandSource source, int clientId) {
        List<String> aList = Arrays.asList(args);
        String cmdString = aList.get(0);

        if(!Validator.notNull(api)) {
            if (commands.containsKey(cmdString)) {
                parseRun(args, source, clientId);
            } else {
                if (source == CommandSource.CONSOLE) {
                    Teamspeak3Bot.getLogger().info("Unknown Command: " + args[0]);
                    Teamspeak3Bot.getLogger().info("Please use help or ? for help");
                } else {
                    api.sendPrivateMessage(clientId, "Unknown Command: " + customChar + args[0]);
                    api.sendPrivateMessage(clientId, "Please use "
                        + customChar + "help or "
                        + customChar + "? for help");
                }
            }
        } else Teamspeak3Bot.getLogger().error("API is null !!!!");

    }

    private static void parseRun(String [] args, CommandSource source, int id) {
        List<String> aList = new ArrayList<>();

        for (int i = 1; i < args.length-1; i++) {
            aList.add(args[i]);
        }

        String label = args[0];
        Command cmd = commands.get(label);

        String[] arguments = {};
        cmd.run(source, id, label, aList.toArray(arguments));
    }

    public static boolean checkCommand(BaseEvent event) {

        if(event instanceof TextMessageEvent) {
            TextMessageEvent e = (TextMessageEvent) event;
            List<String> aList = Arrays.asList((e.getMessage().split("\\s+")));

            String [] arguments = {};
            String cmdString = aList.get(0);
            String consoleMessage = "{@} Command From %source%: " + aList.get(0);

            CommandSource source = null;

            aList.set(0, aList.get(0).replaceFirst(String.valueOf(customChar), ""));
            Teamspeak3Bot.debug("Custom Pre-Key: " + customChar);

            if(cmdString.charAt(0) == customChar) {

                switch (e.getTargetMode()) {
                    case CLIENT:
                        source = CommandSource.CLIENT;
                        break;
                    case SERVER:
                        source = CommandSource.SERVER;
                        break;
                    case CHANNEL:
                        source = CommandSource.CHANNEL;
                        break;
                }

                consoleMessage = consoleMessage.replaceAll("%source%", source.toString().toLowerCase());
                executeCommand(aList.toArray(arguments), source, e.getInvokerId());
                Teamspeak3Bot.debug(consoleMessage);
                return true;
            }
        }
        return false;
    }
}
