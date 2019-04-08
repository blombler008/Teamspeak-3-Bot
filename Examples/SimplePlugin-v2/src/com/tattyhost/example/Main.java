package com.tattyhost.example;

import com.tattyhost.teamspeak3bot.JavaPlugin;
import com.tattyhost.teamspeak3bot.Teamspeak3Bot;
import com.tattyhost.teamspeak3bot.utils.CommandManager;
import com.tattyhost.teamspeak3bot.utils.EventManager;

public class Main extends JavaPlugin {

    @Override public void onDisable() {
        Teamspeak3Bot.getLogger().info("On disable Example");
    }

    @Override public void onEnable() {
        Teamspeak3Bot.getLogger().info("Plugin Enabled: " + getName() + ", " + getVersion());

        // Adding a event which executes when the event happens
        EventManager.addEventToProcessList(new ExampleEvent());

        // Adding a Command for the bot ...
        CommandManager.registerNewCommand("example", new ExampleCommand());
    }

    @Override public void onLoad() {
        Teamspeak3Bot.getLogger()
            .info("Plugin description: " + getPluginDescription().getDescription());
    }
}
