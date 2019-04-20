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

package com.github.blombler008.teamspeak3bot.console;

import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.commands.CommandCompleter;
import com.github.blombler008.teamspeak3bot.commands.CommandSender;
import com.github.blombler008.teamspeak3bot.events.EventType;
import com.github.blombler008.teamspeak3bot.utils.Language;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConsoleManager {

    private static ConsoleManager instance;
    private Thread thread;
    private boolean breakOut;
    private String line;
    private ConsoleReader reader;

    public ConsoleManager() {
        if(System.getenv().containsKey("intellij")) jline.TerminalFactory.registerFlavor( jline.TerminalFactory.Flavor.WINDOWS, jline.UnsupportedTerminal.class);

        breakOut = false;
        line = "";
        if (instance == null)
            instance = this;

        thread = new Thread(() -> { // A new thread is used to run the listener aside the main code

            try {
                reader = new ConsoleReader();

                while (!breakOut) {
                    line = reader.readLine();
                    if(line != null) {
                        Teamspeak3Bot.writeToFile(line);
                        Teamspeak3Bot.getLogger().info(Language.CONSOLE + "ADMIN INPUT > " + line);

                        if (line.split(" ")[0].equalsIgnoreCase("exit")) {
                            Teamspeak3Bot.shutdown();
                            return;
                        }
                        if (line.split(" ")[0].equalsIgnoreCase("quit")) {
                            Teamspeak3Bot.shutdown();
                            return;
                        }

                        if (line.split(" ")[0].equalsIgnoreCase("uploadErrorLog")) {
                            Teamspeak3Bot.uploadErrorLog();
                            continue;
                        }
                        Map<String, String> map = new HashMap<>();
                        map.put("source", CommandSender.CONSOLE.toString());
                        map.put("command", line);
                        map.put("invokerid", "-1");
                        map.put("invokername", "");
                        map.put("invokeruid", "superadmin");
                        map.put("reasonid", "0");
                        map.put("reasonmsg", "");
                        Teamspeak3Bot.getEventManager()
                            .fireEvent(EventType.EVENT_COMMAND_PRE_PROCESS, map, null);
                    }
                }
            } catch (NullPointerException | IOException e) {
                Teamspeak3Bot.getLogger().error("Error Occurred in Console Listener");
                breakOut = true;
            }
        }, "Console-Listener");
        thread.start();
    }

    public static ConsoleManager getInstance() {
        return instance;
    }

    public ConsoleReader getReader() {
        return reader;
    }

    public Thread getThread() {
        return thread;
    }

    public void setCompleters() {
        reader.addCompleter(CommandCompleter.getCompleter());
        //Readline.load(ReadlineLibrary.GnuReadline);
        //Readline.setCompleter(new CommandCompleter());
    }

}
