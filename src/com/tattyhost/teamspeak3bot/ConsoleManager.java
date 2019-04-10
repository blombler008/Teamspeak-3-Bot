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

package com.tattyhost.teamspeak3bot;

import com.tattyhost.teamspeak3bot.utils.CommandSender;
import com.tattyhost.teamspeak3bot.utils.Language;

import java.util.Scanner;

public class ConsoleManager {

    private static ConsoleManager instance;
    private boolean breaking;
    private Scanner input;
    private String line;

    public ConsoleManager() {
        breaking = true;
        line = "";
        input = new Scanner(System.in);

        if (instance == null)
            instance = this;
        new Thread(new Runnable() {

            @Override public void run() {
                try {
                    if (input.hasNextLine()) {
                        line = input.nextLine();
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
                            run();
                        }

                        CommandManager
                            .executeCommand(line.split(" "), CommandSender.CONSOLE, -1, true);

                    }
                } catch (NullPointerException e) {
                    Teamspeak3Bot.getLogger().error("Error Occurred in Console Listener");
                    breaking = false;
                }
                if (breaking)
                    run();
            }
        }, "Console-Listener").start();
    }

    public static ConsoleManager getInstance() {
        return instance;
    }

}
