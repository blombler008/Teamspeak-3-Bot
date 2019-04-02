package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

public class EventChannelDeleted extends Event {

    public EventChannelDeleted(BaseEvent event, TS3Api api) {
        super(event, api);
    }

    @Override public ChannelDeletedEvent getEvent() {
        return (ChannelDeletedEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
