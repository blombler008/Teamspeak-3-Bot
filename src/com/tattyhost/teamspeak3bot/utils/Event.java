package com.tattyhost.teamspeak3bot.utils;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;

import java.util.Map;

public abstract class Event extends BaseEvent {

    protected BaseEvent event;
    protected TS3Api api;

    public Event(Map<String, String> map, TS3Api api) {
        super(map);
        this.event = event;
        this.api = api;
    }

    @Override public void fire(TS3Listener listener) {

    }

    public abstract BaseEvent getEvent();
    public abstract TS3Api getApi();
}
