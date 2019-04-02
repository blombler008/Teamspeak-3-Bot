package com.tattyhost.teamspeak3bot.utils;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.tattyhost.teamspeak3bot.Bot;
import com.tattyhost.teamspeak3bot.EventListener;
import com.tattyhost.teamspeak3bot.Listener;
import com.tattyhost.teamspeak3bot.Teamspeak3Bot;
import com.tattyhost.teamspeak3bot.events.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class EventManager {
    private Bot bot;
    private TS3Api api;

    private static Map<EventType, EventPoint> events = new HashMap<>();

    public EventManager(Bot bot, TS3Api api) {
        this.bot = bot;
        this.api = api;
    }

    public void registerEvents () {
        api.registerAllEvents();
        api.addTS3Listeners(new TS3Listener() {
            @Override public void onTextMessage(TextMessageEvent e) {
                Teamspeak3Bot.debug("{+} TextMessageEvent > " + e.getMap().toString());
                if(e.getInvokerId() != bot.client().getId())
                    if(!(CommandManager.checkCommand(e)))
                        fireEvent(EventType.EVENT_TEXT_MESSAGE, e);
            }

            @Override public void onClientJoin(ClientJoinEvent e) {
                Teamspeak3Bot.debug("{+} ClientJoinEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CLIENT_JOIN, e);
            }

            @Override public void onClientLeave(ClientLeaveEvent e) {
                Teamspeak3Bot.debug("{+} ClientLeaveEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CLIENT_LEAVE, e);
            }

            @Override public void onServerEdit(ServerEditedEvent e) {
                Teamspeak3Bot.debug("{+} ServerEditedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_SERVER_EDIT, e);
            }

            @Override public void onChannelEdit(ChannelEditedEvent e) {
                Teamspeak3Bot.debug("{+} ChannelEditedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_EDIT, e);
            }

            @Override public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
                Teamspeak3Bot.debug("{+} ChannelDescriptionEditedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_DESCRIPTION_CHANGED, e);
            }

            @Override public void onClientMoved(ClientMovedEvent e) {
                Teamspeak3Bot.debug("{+} ClientMovedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CLIENT_MOVED, e);
            }

            @Override public void onChannelCreate(ChannelCreateEvent e) {
                Teamspeak3Bot.debug("{+} ChannelCreateEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_CREATE, e);
            }

            @Override public void onChannelDeleted(ChannelDeletedEvent e) {
                Teamspeak3Bot.debug("{+} ChannelDeletedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_DELETED, e);
            }

            @Override public void onChannelMoved(ChannelMovedEvent e) {
                Teamspeak3Bot.debug("{+} ChannelMovedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_MOVED, e);
            }

            @Override public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
                Teamspeak3Bot.debug("{+} ChannelPasswordChangedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_PASSWORD_CHANGED, e);
            }

            @Override public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
                Teamspeak3Bot.debug("{+} PrivilegeKeyUsedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_PRIVILEGE_KEY_USED, e);
            }
        });
    }

    public void fireEvent(EventType type, BaseEvent event) {
        run(type, event);
    }

    private void run(EventType type, BaseEvent e) {

        try {
            if(!events.containsKey(type)) return;
            Method m = events.get(type).method;


            Class<Listener> ev = events.get(type).methodClass;
            Teamspeak3Bot.debug("{+} Method: " + m.getName());
            Teamspeak3Bot.debug("{+} Class: " + ev.getSimpleName());
            Teamspeak3Bot.debug("{+} Event Type: " + type);
            Teamspeak3Bot.debug("{+} executing...");
            switch (type) {
                case EVENT_CHANNEL_CREATE:
                    m.invoke(ev.newInstance(), new EventChannelCreate(e,api));
                case EVENT_CHANNEL_DELETED:
                    m.invoke(ev.newInstance(), new EventChannelDeleted(e,api));
                case EVENT_CHANNEL_DESCRIPTION_CHANGED:
                    m.invoke(ev.newInstance(), new EventChannelDescriptionChanged(e,api));
                case EVENT_CHANNEL_EDIT:
                    m.invoke(ev.newInstance(), new EventChannelEdit(e,api));
                case EVENT_CHANNEL_MOVED:
                    m.invoke(ev.newInstance(), new EventChannelMoved(e,api));
                case EVENT_CHANNEL_PASSWORD_CHANGED:
                    m.invoke(ev.newInstance(), new EventChannelPasswordChanged(e,api));
                case EVENT_CLIENT_JOIN:
                    m.invoke(ev.newInstance(), new EventClientJoin(e,api));
                case EVENT_CLIENT_LEAVE:
                    m.invoke(ev.newInstance(), new EventClientLeave(e,api));
                case EVENT_CLIENT_MOVED:
                    m.invoke(ev.newInstance(), new EventClientMoved(e,api));
                case EVENT_PRIVILEGE_KEY_USED:
                    m.invoke(ev.newInstance(), new EventPrivilegeKeyUsed(e,api));
                case EVENT_SERVER_EDIT:
                    m.invoke(ev.newInstance(), new EventServerEdit(e,api));
                case EVENT_TEXT_MESSAGE:
                    m.invoke(ev.newInstance(), new EventTextMessage(e,api));
            }

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e1) {
            e1.printStackTrace();
        }

    }


    @SuppressWarnings(value = "unchecked")
    public static void addEventToProcessList(final Listener e) {
        Teamspeak3Bot.debug("-------------------------------addEventToProcessList--------------------------------");
        Teamspeak3Bot.debug("Trying adding class: " + e.getClass().getName());

        Class<Listener> objClass = (Class<Listener>) e.getClass();

        Method [] od = objClass.getDeclaredMethods();

        for(Method mod: od) {
            boolean isAccessible = mod.isAccessible();
            mod.setAccessible(true);

            Teamspeak3Bot.debug("Method: \"" + mod.getName() + "\"");
            Teamspeak3Bot.debug("- Declared Annotations: " + Arrays.toString(mod.getAnnotations()));
            Teamspeak3Bot.debug("- Declared Parameters: " + Arrays.toString(mod.getParameters()));

            if(mod.isAnnotationPresent(EventListener.class) && mod.getParameterCount() == 1) {
                Parameter parm = mod.getParameters()[0];

                Teamspeak3Bot.debug("- Parameter Class: " + parm.getType().getName());

                if(parm.getType().getGenericSuperclass().getTypeName().equals(Event.class.getTypeName())) {
                    Class<? extends Event> evType = EventType.getFromEventType((Class<? extends Event>) parm.getType());
                    EventPoint p = new EventPoint();
                    p.method = mod;
                    p.methodClass = objClass;
                    p.eventType = EventType.getForEventType(evType);

                    events.put(p.eventType, p);
                    Teamspeak3Bot.debug("{=} Added to the Event Listener!");
                    Teamspeak3Bot.debug("{=} Method: " + p.method.getName());
                    Teamspeak3Bot.debug("{=} Class: " + p.methodClass.getSimpleName());
                    Teamspeak3Bot.debug("{=} Event Type: " + p.eventType);
                }
            }
            mod.setAccessible(isAccessible);
        }
        Teamspeak3Bot.debug("------------------------------------------------------------------------------------");
    }


    private static class EventPoint {
        private Method method;
        private Class<Listener> methodClass;
        private EventType eventType;
    }
}
