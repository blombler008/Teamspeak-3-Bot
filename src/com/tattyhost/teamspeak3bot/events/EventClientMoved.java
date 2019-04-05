package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

import java.util.Map;

public class EventClientMoved extends Event {

    public EventClientMoved(Map<String, String> event, TS3Api api) {
        super(event, api);
    }

    @Override public ClientMovedEvent getEvent() {
        return (ClientMovedEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
