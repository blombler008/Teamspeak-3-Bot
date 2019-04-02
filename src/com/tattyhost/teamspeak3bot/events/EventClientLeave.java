package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

public class EventClientLeave extends Event {

    public EventClientLeave(BaseEvent event, TS3Api api) {
        super(event, api);
    }

    @Override public ClientLeaveEvent getEvent() {
        return (ClientLeaveEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
