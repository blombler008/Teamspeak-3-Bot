package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

public class EventChannelCreate extends Event {

    public EventChannelCreate(BaseEvent e, TS3Api api) {
        super(e, api);
    }

    @Override public ChannelCreateEvent getEvent() {
        return (ChannelCreateEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
