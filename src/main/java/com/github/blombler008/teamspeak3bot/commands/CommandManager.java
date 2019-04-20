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

package com.github.blombler008.teamspeak3bot.commands;

import com.github.blombler008.teamspeak3bot.plugins.JavaPlugin;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.utils.Language;
import com.github.blombler008.teamspeak3bot.utils.Validator;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandManager {

    private static Map<String, Command> commands = new HashMap<>();
    private static Map<String, String[]> commandAliases = new HashMap<>();
    private static Map<String, String> commandPlugin = new HashMap<>();
    private static String customChar;
    private static TS3Api api;
    private static List<Thread> commandThreads = new ArrayList<>();
    private static Thread listenerThread;
    private static List<String> registeredCommands = new ArrayList<>();

    public CommandManager(TS3Api api, char customChar) {
        registeredCommands.add("quit");
        registeredCommands.add("exit");
        registeredCommands.add("uploadErrorLog");
        CommandManager.customChar = String.valueOf(customChar);
        CommandManager.api = api;
        AtomicBoolean breakOut = new AtomicBoolean(false);
        listenerThread = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    Thread.sleep(60000);
                    int x = commandThreads.size();
                    if(x==0) {
                        Teamspeak3Bot.debug(Language.COMMAND, "Nothing found to remove.");
                        run();
                    }
                    for(int i=x-1;i>=0;i--) {
                        Thread thr = commandThreads.get(i);
                        if(thr.isInterrupted() || !thr.isAlive()) {
                            commandThreads.remove(thr);
                            Teamspeak3Bot.debug(Language.COMMAND, "Thread " + thr.getName() + " removed from list.");
                        }
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                    breakOut.set(true);
                }
                if(!breakOut.get()) {
                    run();
                }
            }
        }, "CommandManager-Thread");
        listenerThread.start();
    }

    public static void registerNewCommand(JavaPlugin plugin, String str, String[] aliases, Command cmd) {
        registerNewCommand(plugin.getName(), str, aliases, cmd);
    }

    public static void registerNewCommand(String pluginName, String str, String[] aliases, Command cmd) {
        commands.put(str, cmd);
        commandPlugin.put(str, pluginName);
        String[] x = new String[]{};
        List<String> aliasList = Arrays.asList(aliases);
        if(!aliasList.contains(str)) {
            aliasList.add(str);
        }
        commandAliases.put(str, aliasList.toArray(x));

        for(String s: aliasList) {
            registeredCommands.add(pluginName + ":" + s);
            registeredCommands.add(s);
        }
    }

    public static boolean executeCommand(String cmd, String[] args, CommandSender source, int clientId, boolean run) {
        List<String> aList = Arrays.asList(args);
        if(!(aList.size() == 1))
            aList.remove(0);

        String cmdString = getCommandStringFromAlias(resolveCommand(cmd));

        if (!Validator.notNull(api)) {
            if (commands.containsKey(cmdString)) {
                if (run)
                    parseRun(cmdString, args, source, clientId);
                return true;
            } else {
                if (source == CommandSender.CONSOLE) {
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

    private static void parseRun(String cmdString, String[] args, CommandSender source, int id) {
        List<String> aList = Arrays.asList(args);

        Command cmd = commands.get(cmdString);

        String[] arguments = {};
        //To prevent a constant loop in the command
        Thread thread = new Thread(() -> cmd.run(source, id, cmdString, aList.toArray(arguments)),cmdString + "-" + commandThreads.size());
        commandThreads.add(thread);
        Teamspeak3Bot.debug(Language.COMMAND, thread.getName());
        thread.start();
    }

    public static boolean checkCommand(String cmdString, CommandSender source) {
        return checkCommand(new String[] {cmdString}, source);
    }

    public static boolean checkCommand(String[] cmd, CommandSender source) {
        List<String> aList = new ArrayList<>();
        Collections.addAll(aList, cmd);

        String[] arguments = {};
        String consoleMessage = "Command From %source%: " + aList.get(0);

        aList.set(0, aList.get(0).replaceFirst(customChar, ""));
        consoleMessage = consoleMessage.replaceAll("%source%", source.toString().toLowerCase());

        Teamspeak3Bot.debug(Language.COMMAND, consoleMessage);
        Teamspeak3Bot.debug(Language.COMMAND, "Custom Prefix Key: " + customChar);

        String str = getCommandStringFromAlias(resolveCommand(aList.get(0)));
        if(str == null) {
            return false;
        }
        String cPlugin = commandPlugin.get(str);
        String[] cAliases = commandAliases.get(str);
        Command cCommand = commands.get(str);
        Teamspeak3Bot.debug(Language.COMMAND, cPlugin + ":" + str + ", " + Arrays.toString(cAliases));
        if (source instanceof ConsoleCommandSender || cmd[0].startsWith(customChar)) {
            return commands.containsKey(str);
        //executeCommand(aList.get(0), aList.toArray(arguments), source, invokerId, false);
        }
        return false;
    }

    public static String getCommandStringFromAlias(String alias) {
        for(String key: commandAliases.keySet()) {
            if(alias.equalsIgnoreCase(key)) {
                return key;
            }
            String[] values = commandAliases.get(key);
            for(String vStr: values) {
                if(alias.equalsIgnoreCase(vStr)) {
                    return key;
                }
            }
        }
        return null;
    }

    public static String resolveCommand(String arg) {
        String[] str = arg.split(":+");
        if(str.length == 1) {
            return str[0];
        } else if(str.length > 1) {
            return str[1];
        }
        return "";
    }

    public static String getCommandFromArray(String[] cmdArray) {
        return cmdArray[0].replaceFirst(String.valueOf(customChar), "");
    }

    public static List<String> getCommandList() {
        return registeredCommands;
    }
}
