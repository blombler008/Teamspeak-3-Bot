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

package com.github.blombler008.teamspeak3bot.utils;

import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;

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
        if (!Files.isDirectory(Paths.get(path))) {
            Teamspeak3Bot.getLogger().error("Path is not a Directory: " + path);
            return false;
        }
        return true;
    }

    public static boolean clientExists(Teamspeak3Bot instance, int client) {
        return instance.getClients().containsKey(client);
    }

    public static boolean channelExists(Teamspeak3Bot instance, int channel) {
        return instance.getChannels().containsKey(channel);
    }
}
