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

package com.github.blombler008.teamspeak3bot.commands.listeners;

import com.github.blombler008.teamspeak3bot.commands.Command;
import com.github.blombler008.teamspeak3bot.commands.CommandExecutor;
import com.github.blombler008.teamspeak3bot.commands.CommandSender;
import com.github.blombler008.teamspeak3bot.commands.ConsoleCommandSender;
import com.github.blombler008.teamspeak3bot.utils.Language;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

public class CommandReload extends CommandExecutor {
    @Override
    public void run(CommandSender source, Command cmd, String commandLabel, String[] args) {
        int id = cmd.getInvokerId();
        ClientInfo sender = source.getInstance().getClient(id);

        if (source instanceof ConsoleCommandSender) {
            source.getInstance().getPluginManager().reloadPlugins();

        } else {
            if (sender.getUniqueIdentifier().equals(source.getInstance().getOwner().getUniqueIdentifier())) {
                source.getInstance().getPluginManager().reloadPlugins();
            } else {
                source.sendMessage(0, id, Language.get("nopermissions"));
            }
        }
    }

}
