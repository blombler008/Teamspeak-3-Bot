package com.tattyhost.example;

import com.tattyhost.teamspeak3bot.ChannelCommandSender;
import com.tattyhost.teamspeak3bot.ClientCommandSender;
import com.tattyhost.teamspeak3bot.ConsoleCommandSender;
import com.tattyhost.teamspeak3bot.ServerCommandSender;
import com.tattyhost.teamspeak3bot.utils.Command;
import com.tattyhost.teamspeak3bot.utils.CommandSender;

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
