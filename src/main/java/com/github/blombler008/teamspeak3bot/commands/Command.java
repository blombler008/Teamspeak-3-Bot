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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Command {

    private final String[] aliases;
    private final String description;
    private final String plugin;
    private final String command;
    private final Map<String, String> map;
    private final Teamspeak3Bot instance;

    public Command(CommandTemplate cmdTemp, Map<String, String> map) {
        this.aliases = cmdTemp.getAliases().toArray(new String[0]);
        this.description = cmdTemp.getDescription();
        this.command = cmdTemp.getCommand();
        this.plugin = cmdTemp.getPlugin();
        this.map = map;
        this.instance = cmdTemp.getInstance();
    }

    public int getInvokerId() {
        return Integer.parseInt(map.get("invokerid"));
    }

    public int getChannelId() {
        return Integer.parseInt(map.get("channelid"));
    }

    public String getName() {
        return command;
    }

    public JavaPlugin getPlugin() {
        List<JavaPlugin> javaPlugins = instance.getPluginManager().getPlugins();
        for(JavaPlugin javaPlugin: javaPlugins) {
            if(javaPlugin.getName().equals(plugin)) {
                return javaPlugin;
            }
        }
        return null;
    }

    public List<String> getAliases() {
        return Collections.unmodifiableList(Arrays.asList(aliases));
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(map);
    }

    public String getPluginName() {
        return plugin;
    }

}
