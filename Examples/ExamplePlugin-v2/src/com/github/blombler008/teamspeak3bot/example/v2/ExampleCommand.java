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

package com.github.blombler008.teamspeak3bot.example.v2;

import com.github.blombler008.teamspeak3bot.commands.ChannelCommandSender;
import com.github.blombler008.teamspeak3bot.commands.ClientCommandSender;
import com.github.blombler008.teamspeak3bot.commands.ConsoleCommandSender;
import com.github.blombler008.teamspeak3bot.commands.ServerCommandSender;
import com.github.blombler008.teamspeak3bot.commands.Command;
import com.github.blombler008.teamspeak3bot.commands.CommandSender;

public class ExampleCommand extends Command {

    /*!
     * NOTE: You cannot change the name of the method ...
     *
     * If the command (in this case "example") is send to the bot you this method runs
     * the parameters you get are:
     * - source: from where this command is executed
     * - id: the id of the client send the command to the bot(NOTE: Console have the id -1)
     * - commandLabel: the name of the name (in this case "example")
     * - args: the arguments send with the command
     *              (eg. me > bot: example hi there)
     *              - source = client
     *              - id = my client id from the teamspeak
     *              - commandLabel = "example"
     *              - args = {"hi", "there"}
     */
    @Override public void run(CommandSender source, int id, String commandLabel, String[] args) {
        if (source instanceof ConsoleCommandSender)
            source.sendMessage(0, id,
                "This happens when a Console messages the bot with the command!");
        if (source instanceof ClientCommandSender)
            source.sendMessage(0, id, "This happens when a client entered a command the bot!");
        if (source instanceof ServerCommandSender)
            source.sendMessage(0, id,
                "This happens when a client entered a command in the public server chat!");
        if (source instanceof ChannelCommandSender)
            source.sendMessage(0, id,
                "This happens when a client entered a command in the channel chat!");
    }
}
