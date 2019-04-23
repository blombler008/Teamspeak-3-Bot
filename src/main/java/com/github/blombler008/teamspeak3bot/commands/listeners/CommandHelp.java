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

import com.github.blombler008.teamspeak3bot.commands.*;

public class CommandHelp extends CommandExecutor {
    @Override
    public void run(CommandSender source, Command cmd, String commandLabel, String[] args) {
        int clId = cmd.getInvokerId();
        int chId = cmd.getChannelId();
        source.sendMessage(chId, clId,
            "-------------------------------- Help --------------------------------");
        for (String command : source.getInstance().getCommandManager().getCommands().keySet()) {
            resolveCommand(chId, clId, source, command);
        }
        source.sendMessage(chId, clId,
            "----------------------------------------------------------------------");
    }

    private void resolveCommand(int chId, int clId, CommandSender source, String command) {
        StringBuilder stringBuilder = new StringBuilder(" - ");
        if (!(source instanceof ConsoleCommandSender)) {
            stringBuilder.append(source.getInstance().getCustomChar());
        }
        CommandTemplate cmdTemp =
            source.getInstance().getCommandManager().getCommands().get(command);
        stringBuilder.append(cmdTemp.getCommand());
        source.sendMessage(chId, clId, stringBuilder.toString());

        stringBuilder = new StringBuilder("    * ");
        stringBuilder.append("Description: ");
        StringBuilder desc = new StringBuilder(cmdTemp.getDescription());
        if (desc.length() > 45) {
            desc.substring(0, 45);
            desc.delete(45, desc.length());
            desc.append("...");
        }
        stringBuilder.append(desc);
        source.sendMessage(chId, clId, stringBuilder.toString());
    }
}
