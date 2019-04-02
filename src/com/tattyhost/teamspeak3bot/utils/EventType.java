package com.tattyhost.teamspeak3bot.utils;

import com.tattyhost.teamspeak3bot.events.*;

public enum EventType {
    EVENT_CHANNEL_CREATE(EventChannelCreate.class),
    EVENT_CHANNEL_DELETED(EventChannelDeleted.class),
    EVENT_CHANNEL_DESCRIPTION_CHANGED(EventChannelDescriptionChanged.class),
    EVENT_CHANNEL_EDIT(EventChannelEdit.class),
    EVENT_CHANNEL_MOVED(EventChannelMoved.class),
    EVENT_CHANNEL_PASSWORD_CHANGED(EventChannelPasswordChanged.class),
    EVENT_CLIENT_JOIN(EventClientJoin.class),
    EVENT_CLIENT_LEAVE(EventClientLeave.class),
    EVENT_CLIENT_MOVED(EventClientMoved.class),
    EVENT_PRIVILEGE_KEY_USED(EventPrivilegeKeyUsed.class),
    EVENT_SERVER_EDIT(EventServerEdit.class),
    EVENT_TEXT_MESSAGE(EventTextMessage.class);

    private Class< ?extends Event> evClass;

    EventType(Class<? extends Event> svClass) {
        evClass = svClass;
    }

    public static Class<? extends Event> getFromEventType(Class<? extends Event> svClass) {
        for(EventType ev: values()) {
            if (ev.evClass == svClass) {
                return ev.evClass;
            }
        }
        return null;
    }

    public static EventType getForEventType(Class<? extends Event> svClass) {
        for(EventType ev: values()) {
            if (ev.evClass == svClass) {
                return ev;
            }
        }
        return null;
    }

}

