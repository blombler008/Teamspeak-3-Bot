package com.tattyhost.example;

import com.tattyhost.teamspeak3bot.CommandSource;
import com.tattyhost.teamspeak3bot.Teamspeak3Bot;
import com.tattyhost.teamspeak3bot.utils.Command;

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
    @Override public void run(CommandSource source, int id, String commandLabel, String[] args) {
        Teamspeak3Bot.getApi().sendPrivateMessage(id, commandLabel);
    }
}
