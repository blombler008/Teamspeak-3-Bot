package com.github.blombler008.teamspeak3bot.commands;/*
 *
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

import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;

import java.util.Arrays;
import java.util.List;

public class CommandTemplate {
    private final String[] aliases;
    private final String description;
    private final String command;
    private final String plugin;
    private final String usage;
    private Teamspeak3Bot instance;

    public CommandTemplate(Teamspeak3Bot instance, String[] aliases, String description, String command, String plugin, String usage) {
        this.aliases = (aliases != null) ? aliases : new String[]{};

        this.command = command;
        this.description = description;
        this.plugin = plugin;
        this.instance = instance;
        this.usage = usage;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String getPlugin() {
        return plugin;
    }

    public String getUsage() {
        return usage;
    }

    public List<String> getAliases() {
        return Arrays.asList(aliases);
    }

    public CommandTemplate setExecutor(CommandExecutor executor) {
        return instance.getCommandManager().getCommand(plugin, command, executor, this);
    }

    public Teamspeak3Bot getInstance() {
        return instance;
    }
}

