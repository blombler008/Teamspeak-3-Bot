package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

public class EventChannelEdit extends Event {

    public EventChannelEdit(BaseEvent event, TS3Api api) {
        super(event, api);
    }

    @Override public ChannelEditedEvent getEvent() {
        return (ChannelEditedEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
