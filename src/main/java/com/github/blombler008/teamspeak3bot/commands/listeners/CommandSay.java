package com.github.blombler008.teamspeak3bot.commands.listeners;/*
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

import com.github.blombler008.teamspeak3bot.commands.Command;
import com.github.blombler008.teamspeak3bot.commands.CommandExecutor;
import com.github.blombler008.teamspeak3bot.commands.CommandSender;
import com.github.theholywaffle.teamspeak3.TS3QueryX;
import com.github.theholywaffle.teamspeak3.commands.CommandBuilderX;
import com.github.theholywaffle.teamspeak3.commands.parameter.KeyValueParam;

public class CommandSay extends CommandExecutor {

    @Override
    public void run(CommandSender source, Command cmd, String commandLabel, String[] args) {
        int chId = cmd.getChannelId();
        int clId = cmd.getInvokerId();

        if(!(args.length > 0)) {
            source.sendMessage(chId, clId, "Usage: say " + cmd.getUsage());
        } else {
            StringBuilder str = new StringBuilder();
            for(int i=0;i<args.length; i++)  {
                str.append(args[i]);
                if(i != args.length-1) {
                    str.append(" ");
                }
            }


            CommandBuilderX x = new CommandBuilderX("gm", 1);
            x.add(new KeyValueParam("msg", str.toString()));
            com.github.theholywaffle.teamspeak3.commands.Command cmdX = x.build();
            TS3QueryX.doCommandAsync(source.getInstance().getBot().getQuery(), cmdX);
        }

    }
}
