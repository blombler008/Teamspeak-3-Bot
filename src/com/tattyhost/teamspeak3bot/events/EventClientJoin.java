package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

public class EventClientJoin extends Event {

    public EventClientJoin(BaseEvent event, TS3Api api) {
        super(event, api);
    }

    @Override public ClientJoinEvent getEvent() {
        return (ClientJoinEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
