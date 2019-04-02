package com.tattyhost.example;

import com.tattyhost.teamspeak3bot.JavaPlugin;
import com.tattyhost.teamspeak3bot.Teamspeak3Bot;

public class Main extends JavaPlugin {
		
	@Override
	public void onDisable() {
		Teamspeak3Bot.getLogger().info("On disable Example");
	}
	
	@Override
	public void onEnable() {
		Teamspeak3Bot.getLogger().info("Plugin Enabled: " + getName() + ", " + getVersion());
		
	}
	
	@Override
	public void onLoad() {
		Teamspeak3Bot.getLogger().info("Plugin description: " + getPluginDescription().getDescription());
	}
}
