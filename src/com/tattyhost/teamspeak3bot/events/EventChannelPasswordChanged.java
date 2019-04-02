package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelPasswordChangedEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

public class EventChannelPasswordChanged extends Event {

    public EventChannelPasswordChanged(BaseEvent event, TS3Api api) {
        super(event, api);
    }

    @Override public ChannelPasswordChangedEvent getEvent() {
        return (ChannelPasswordChangedEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }

}
