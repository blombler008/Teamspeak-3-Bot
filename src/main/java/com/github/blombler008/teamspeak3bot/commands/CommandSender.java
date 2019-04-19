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

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;

public abstract class CommandSender {

    public static final CommandSender CONSOLE   = new ConsoleCommandSender  (  "CONSOLE");
    public static final CommandSender CLIENT    = new ClientCommandSender   (  "CLIENT" );
    public static final CommandSender CHANNEL   = new ChannelCommandSender  (  "CHANNEL");
    public static final CommandSender SERVER    = new ServerCommandSender   (  "SERVER" );

    private String lowerString;

    public CommandSender(String s) {
        lowerString = s;
    }

    public static CommandSender getSender(TextMessageTargetMode targetMode) {
        switch (targetMode) {
            case SERVER:
                return SERVER;
            case CHANNEL:
                return CHANNEL;
            case CLIENT:
                return CLIENT;
        }
        return null;
    }
    public static CommandSender getSender(String targetMode) {
        switch (targetMode) {
            case "SERVER":
                return SERVER;
            case "CHANNEL":
                return CHANNEL;
            case "CLIENT":
                return CLIENT;
            case "CONSOLE":
                return CONSOLE;
        }
        return null;
    }

    public String toString() {
        return lowerString;
    }


    public abstract void sendMessage(int channel, int client, String message);


}
