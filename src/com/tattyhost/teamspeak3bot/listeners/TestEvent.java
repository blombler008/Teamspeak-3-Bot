package com.tattyhost.teamspeak3bot.listeners;

import com.tattyhost.teamspeak3bot.Teamspeak3Bot;
import com.tattyhost.teamspeak3bot.events.EventTextMessage;
import com.tattyhost.teamspeak3bot.EventListener;
import com.tattyhost.teamspeak3bot.Listener;

public class TestEvent implements Listener {


    @EventListener
    public void onTextMessage(EventTextMessage e) {
        Teamspeak3Bot.getLogger().info("Event Stun " + e.getEvent().getMessage());
    }

}
