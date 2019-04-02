package com.tattyhost.teamspeak3bot.utils;

import com.tattyhost.teamspeak3bot.CommandSource;

public abstract class Command {

    public abstract void run(final CommandSource source, final int id, final String commandLabel, final String[] args);
}
