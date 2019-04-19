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

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.utils.Language;
import com.github.blombler008.teamspeak3bot.utils.Validator;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandManager {

    private static Map<String, Command> commands = new HashMap<>();
    private static String customChar;
    private static TS3Api api;
    private static List<Thread> commandThreads = new ArrayList<>();
    private static Thread listenerThread;

    public CommandManager(TS3Api api, char customChar) {
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

    public static void registerNewCommand(String str, Command cmd) {
        commands.put(str, cmd);
    }

    public static boolean executeCommand(String[] args, CommandSender source, int clientId, boolean run) {
        List<String> aList = Arrays.asList(args);
        String cmdString = aList.get(0);

        if (!Validator.notNull(api)) {
            if (commands.containsKey(cmdString)) {
                if (run)
                    parseRun(args, source, clientId);
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

    private static void parseRun(String[] args, CommandSender source, int id) {
        List<String> aList = Arrays.asList(args);

        String label = args[0];
        Command cmd = commands.get(label);

        String[] arguments = {};
        //To prevent a constant loop in the command
        Thread thread = new Thread(() -> cmd.run(source, id, label, aList.toArray(arguments)),label + "-" + commandThreads.size());
        commandThreads.add(thread);
        Teamspeak3Bot.debug(Language.COMMAND, thread.getName());
        thread.start();
    }

    public static boolean checkCommand(String cmdString, CommandSender source, int invokerId) {
        return checkCommand(new String[] {cmdString}, source, invokerId);
    }

    public static boolean checkCommand(String[] cmd, CommandSender source, int invokerId) {
        List<String> aList = new ArrayList<>();
        Collections.addAll(aList, cmd);

        String[] arguments = {};
        String consoleMessage = "Command From %source%: " + aList.get(0);

        aList.set(0, aList.get(0).replaceFirst(customChar, ""));
        consoleMessage = consoleMessage.replaceAll("%source%", source.toString().toLowerCase());

        Teamspeak3Bot.debug(Language.COMMAND, consoleMessage);
        Teamspeak3Bot.debug(Language.COMMAND, "Custom Prefix Key: " + customChar);
        if(source instanceof ConsoleCommandSender || cmd[0].startsWith(customChar))
            return executeCommand(aList.toArray(arguments), source, invokerId, false);
        else return false;
    }


    public static String getCommandFromArray(String[] cmdArray) {
        return cmdArray[0].replaceFirst(String.valueOf(customChar), "");
    }
}