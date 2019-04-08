package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.tattyhost.teamspeak3bot.utils.Event;

import java.util.Map;

public class EventCommandPreProcess extends Event {

    public EventCommandPreProcess(Map<String, String> map, TS3Api api) {
        super(map, api);
    }

    @Override public EventCommandPreProcess getEvent() {
        return (EventCommandPreProcess) event;
    }

    @Override public TS3Api getApi() {
        return null;
    }
}
