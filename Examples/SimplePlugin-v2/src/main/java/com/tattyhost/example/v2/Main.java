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

package com.tattyhost.example.v2;

import com.tattyhost.teamspeak3bot.commands.CommandManager;
import com.tattyhost.teamspeak3bot.events.EventManager;
import com.tattyhost.teamspeak3bot.plugins.JavaPlugin;
import com.tattyhost.teamspeak3bot.Teamspeak3Bot;
import com.tattyhost.teamspeak3bot.plugins.PluginDescription;

import java.util.Properties;

public class Main extends JavaPlugin {

    public Main(PluginDescription description, Properties pr) {
        super(description, pr);
    }

    @Override public void onDisable() {
        Teamspeak3Bot.info("On disable Example");
    }

    @Override public void onEnable() {
        Teamspeak3Bot.info("Plugin Enabled: " + getName() + ", " + getVersion());

        // Adding a event which executes when the event happens
        EventManager.addEventToProcessList(new ExampleEvent());

        // Adding a Command for the bot ...
        CommandManager.registerNewCommand("example", new ExampleCommand());
    }

    @Override public void onLoad() {
        Teamspeak3Bot.info("Plugin description: " + getPluginDescription().getDescription());
    }
}
