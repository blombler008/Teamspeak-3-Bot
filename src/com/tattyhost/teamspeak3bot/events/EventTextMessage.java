package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

import java.util.Map;

public class EventTextMessage extends Event {

    public EventTextMessage(Map<String, String> event, TS3Api api) {
        super(event, api);
    }

    @Override public TextMessageEvent getEvent() {
        return (TextMessageEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
