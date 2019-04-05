package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

import java.util.Map;

public class EventClientLeave extends Event {

    public EventClientLeave(Map<String, String> event, TS3Api api) {
        super(event, api);
    }

    @Override public ClientLeaveEvent getEvent() {
        return (ClientLeaveEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
