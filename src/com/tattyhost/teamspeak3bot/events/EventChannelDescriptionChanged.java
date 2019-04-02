package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDescriptionEditedEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

public class EventChannelDescriptionChanged extends Event {

    public EventChannelDescriptionChanged(BaseEvent event, TS3Api api) {
        super(event, api);
    }

    @Override public ChannelDescriptionEditedEvent getEvent() {
        return (ChannelDescriptionEditedEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
