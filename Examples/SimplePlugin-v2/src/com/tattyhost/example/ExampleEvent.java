package com.tattyhost.example;

import com.tattyhost.teamspeak3bot.EventListener;
import com.tattyhost.teamspeak3bot.Listener;
import com.tattyhost.teamspeak3bot.Teamspeak3Bot;
import com.tattyhost.teamspeak3bot.events.EventTextMessage;

public class ExampleEvent implements Listener {


    @EventListener  // If a client Messages the bot this Event is executed ...
    // NOTE: the name of the event doesn't matter at all
    public void onTextMessage(EventTextMessage e) {
        Teamspeak3Bot.getLogger().info("Example Event Stun > " + e.getEvent().getMessage());
    }

}
