package com.tattyhost.teamspeak3bot.utils;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;

public abstract class Event {

    protected BaseEvent event;
    protected TS3Api api;

    public Event(BaseEvent event, TS3Api api) {
        this.api = api;
        this.event = event;
    }
    public abstract BaseEvent getEvent();
    public abstract TS3Api getApi();
}
