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

import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.plugins.JavaPlugin;
import com.github.blombler008.teamspeak3bot.utils.Language;
import com.github.blombler008.teamspeak3bot.utils.Validator;
import com.github.theholywaffle.teamspeak3.TS3Api;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandManager {

    private Map<String, CommandTemplate> commands = new HashMap<>();
    private Map<String, List<CommandExecutor>> commandExecutors = new HashMap<>();
    private String customChar;
    private TS3Api api;
    private List<Thread> commandThreads = new ArrayList<>();
    private Thread listenerThread;
    private List<String> registeredCommands = new ArrayList<>();
    private Teamspeak3Bot instance;

    public CommandManager(TS3Api api, char customChar, Teamspeak3Bot instance) {

        registeredCommands.add("quit");
        registeredCommands.add("exit");
        registeredCommands.add("stop");
        registeredCommands.add("uploadErrorLog");

        this.customChar = String.valueOf(customChar);
        this.api = api;
        this.instance = instance;

        AtomicBoolean breakOut = new AtomicBoolean(false);

        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                    int x = commandThreads.size();

                    if (x == 0) {
                        instance.debug(Language.COMMAND, "Nothing found to remove.");
                        run();
                    }

                    for (int i = x - 1; i >= 0; i--) {

                        Thread thr = commandThreads.get(i);
                        if (thr.isInterrupted() || !thr.isAlive()) {
                            commandThreads.remove(thr);
                            instance.debug(Language.COMMAND, "Thread " + thr.getName() + " removed from list.");
                        }
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                    breakOut.set(true);
                }
                if (!breakOut.get()) {
                    run();
                }
            }
        }, "CommandManager-Thread");
        listenerThread.start();
    }


    public CommandTemplate registerNewCommand(CommandTemplate cmdTemp) {
        commands.put(cmdTemp.getCommand(), cmdTemp);
        return cmdTemp;
    }

    public CommandTemplate getCommand(JavaPlugin plugin, String str, CommandExecutor cmdEx, CommandTemplate cmdTemp) {
        return getCommand(plugin.getName(), str, cmdEx, cmdTemp);
    }

    public CommandTemplate getCommand(String pluginName, String str, CommandExecutor cmdEx, CommandTemplate cmdTemp) {
        CommandTemplate cmd = commands.get(str);

        if(cmd == null) {
            cmd = registerNewCommand(cmdTemp);
        }

        List<CommandExecutor> executors = commandExecutors.getOrDefault(str, new ArrayList<>());
        executors.add(cmdEx);

        commandExecutors.put(str, executors);
        List<String> aliasList = cmd.getAliases();
        if (!aliasList.contains(str)) {
            aliasList.add(str);
        }

        // Command cmd = new Command(aliasList.toArray(new String[]{}), description, str, pluginName);
        commands.put(str, cmd);
        for (String s : cmd.getAliases()) {
            registeredCommands.add(pluginName + ":" + s);
            registeredCommands.add(s);
        }
        return cmd;
    }

    public boolean executeCommand(String cmd, String[] args, CommandSender source, int clientId, int channelId, boolean run) {

        String cmdString = getCommandStringFromAlias(resolveCommand(cmd));

        if (!Validator.notNull(api)) {

            if (commands.containsKey(cmdString)) {
                if (run) {
                    Map<String, String> map = new HashMap<>();
                    map.put("invokerid", clientId + "");
                    map.put("channelid", channelId + "");
                    parseRun(cmd, args, source, map);
                }
                return true;
            }
        } else
            Teamspeak3Bot.getLogger().error(Language.COMMAND + "API is null !!!!");
        return false;
    }

    private void parseRun(String cmd, String[] args, CommandSender source, Map<String, String> map) {

        List<CommandExecutor> executors = commandExecutors.getOrDefault(getCommandStringFromAlias(resolveCommand(cmd)), new ArrayList<>());
        CommandTemplate cmdTemp = commands.get(getCommandStringFromAlias(resolveCommand(cmd)));

        for (CommandExecutor executor : executors) {
            //To prevent a constant loop in the command
            Thread thread = new Thread(() -> executor.run(source, new Command(cmdTemp, map), cmd, args), cmdTemp.getCommand() + "-" + commandThreads.size());
            commandThreads.add(thread);
            instance.debug(Language.COMMAND, thread.getName());

            thread.start();
        }
    }

    public boolean checkCommand(String cmdString, int clientId, CommandSender source) {
        return checkCommand(new String[]{cmdString}, clientId, source);
    }

    public boolean checkCommand(String[] cmd, int clientId, CommandSender source) {
        List<String> aList = new ArrayList<>();
        Collections.addAll(aList, cmd);

        String consoleMessage = "Command From %source%: " + aList.get(0);

        aList.set(0, aList.get(0).replaceFirst(customChar, ""));
        consoleMessage = consoleMessage.replaceAll("%source%", source.toString().toLowerCase());

        instance.debug(Language.COMMAND, consoleMessage);
        instance.debug(Language.COMMAND, "Custom Prefix Key: " + customChar);

        String str = getCommandStringFromAlias(resolveCommand(aList.get(0)));

        if(!(source instanceof ConsoleCommandSender) && !(cmd[0].startsWith(customChar))) {
            return false;
        }

        if (str != null) {
            CommandTemplate cCommand = commands.get(str);
            String cPlugin = cCommand.getPlugin();
            List<String> cAliases = cCommand.getAliases();
            instance.debug(Language.COMMAND, cPlugin + ":" + str + ", " + Arrays.toString(cAliases.toArray()));
            return true;
        }

        source.sendMessage(0, clientId, "Unknown Command: " + cmd[0]);
        source.sendMessage(0, clientId, "Please use help or ? for help");
        return false;
    }

    public String getCommandStringFromAlias(String alias) {

        for (String key : commands.keySet()) {
            if (alias.equalsIgnoreCase(key)) {
                return key;
            }
            List<String> values = commands.get(key).getAliases();
            for (String value : values) {
                if (alias.equalsIgnoreCase(value)) {
                    return key;
                }
            }
        }

        return null;
    }

    public String resolveCommand(String arg) {
        String[] str = arg.split(":+");
        String command = "";

        if (str.length == 1) {
            command = str[0];
        } else if (str.length > 1) {
            if (commands.containsKey(getCommandStringFromAlias(str[1]))) {
                if (commands.get(getCommandStringFromAlias(str[1])).getPlugin().equalsIgnoreCase(str[0])) {
                    command = str[1];
                }
            }
        }
        return command;
    }

    public String getCommandFromArray(String[] cmdArray) {
        return cmdArray[0].replaceFirst(String.valueOf(customChar), "");
    }

    public List<String> getCommandList() {
        return registeredCommands;
    }

    public Map<String, CommandTemplate> getCommands() {
        return commands;
    }
}
