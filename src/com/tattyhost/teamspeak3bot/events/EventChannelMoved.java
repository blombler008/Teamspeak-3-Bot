package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelMovedEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

public class EventChannelMoved extends Event {

    public EventChannelMoved(BaseEvent event, TS3Api api) {
        super(event, api);
    }

    @Override public ChannelMovedEvent getEvent() {
        return (ChannelMovedEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
