package com.tattyhost.teamspeak3bot.listeners;

import com.tattyhost.teamspeak3bot.Teamspeak3Bot;
import com.tattyhost.teamspeak3bot.utils.Command;
import com.tattyhost.teamspeak3bot.CommandSource;

public class TestCommand extends Command {

    @Override public void run(CommandSource source, int id, String commandLabel, String[] args) {
        Teamspeak3Bot.getApi().sendPrivateMessage(id, commandLabel);
    }
}
