package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

import java.util.Map;

public class EventChannelEdit extends Event {

    public EventChannelEdit(Map<String, String> event, TS3Api api) {
        super(event, api);
    }

    @Override public ChannelEditedEvent getEvent() {
        return (ChannelEditedEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }
}
