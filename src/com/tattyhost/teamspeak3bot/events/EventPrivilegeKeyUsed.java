package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.PrivilegeKeyUsedEvent;
import com.tattyhost.teamspeak3bot.utils.Event;

import java.util.Map;

public class EventPrivilegeKeyUsed extends Event {

    public EventPrivilegeKeyUsed(Map<String, String> event, TS3Api api) {
        super(event, api);
    }

    @Override public PrivilegeKeyUsedEvent getEvent() {
        return (PrivilegeKeyUsedEvent) event;
    }

    @Override public TS3Api getApi() {
        return api;
    }

}
