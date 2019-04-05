package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

import java.util.Map;

public class EventServerEdit extends Event {

    public EventServerEdit(Map<String, String> event, TS3Api api) {
        super(event, api);
    }

    @Override public ServerEditedEvent getEvent() {
        return (ServerEditedEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
