/*
 * MIT License
 *
 * Copyright (c) 2019 blombler008
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.blombler008.teamspeak3bot.events;

import com.github.blombler008.teamspeak3bot.Bot;
import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.commands.CommandSender;
import com.github.blombler008.teamspeak3bot.events.handlers.*;
import com.github.blombler008.teamspeak3bot.utils.Language;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


public class EventManager {
    private static Map<EventType, List<EventPoint>> events = new HashMap<>();
    private Bot bot;
    private TS3Api api;

    public EventManager(Bot bot, TS3Api api) {
        this.bot = bot;
        this.api = api;
    }

    @SuppressWarnings(value = {"unchecked", "unused"})
    public static void addEventToProcessList(final Listener e) {
        Teamspeak3Bot.debug(Language.EVENT
            ,"-------------------------------addEventToProcessList--------------------------------");
        Teamspeak3Bot.debug(Language.EVENT, "Trying adding class: " + e.getClass().getName());

        Class<Listener> objClass = (Class<Listener>) e.getClass();

        Method[] od = objClass.getDeclaredMethods();

        for (Method method : od) {
            boolean isAccessible = method.isAccessible();
            method.setAccessible(true);

            Teamspeak3Bot.debug(Language.EVENT, "Method: \"" + method.getName() + "\"");
            Teamspeak3Bot.debug(Language.EVENT, "- Declared Annotations: " + Arrays
                .toString(method.getAnnotations()));
            Teamspeak3Bot.debug(
                Language.EVENT, "- Declared Parameters: " + Arrays.toString(method.getParameters()));

            if (method.isAnnotationPresent(EventListener.class) && method.getParameterCount() == 1) {
                EventListener evL = method.getAnnotation(EventListener.class);
                Parameter parm = method.getParameters()[0];

                Teamspeak3Bot
                    .debug(Language.EVENT, "- Parameter Class: " + parm.getType().getName());

                if (parm.getType().getGenericSuperclass().getTypeName()
                    .equals(Event.class.getTypeName())) {

                    Class<? extends Event> evType =
                        EventType.getFromEventType((Class<? extends Event>) parm.getType());

                    EventPoint eventPoint = new EventPoint();
                    eventPoint.method = method;
                    eventPoint.methodClass = objClass;
                    eventPoint.eventType = EventType.getForEventType(evType);
                    eventPoint.eventListener = evL;

                    List<EventPoint> eventPoints = events.getOrDefault(eventPoint.eventType, new ArrayList<>());
                    if(eventPoints.size()==0) {
                        eventPoints.add(eventPoint);
                    } else {
                        int count = eventPoints.size()-1;
                        for(int i=0; i<eventPoints.size(); i++) {
                            EventPoint p = eventPoints.get(i);
                            switch (p.getEventAnnotation().priority().compareTo(evL)) {
                                case -1:
                                    count++;
                                    break;
                                case 0:
                                    break;
                                case 1:
                                    count--;
                                    break;
                            }
                        }
                        if(count < 0) count = 0;
                        eventPoints.add(count, eventPoint);
                    }
                    events.put(eventPoint.eventType, eventPoints);
                    Teamspeak3Bot.debug(Language.EVENT, "{=} Added to the Event Listener!");
                    Teamspeak3Bot.debug(Language.EVENT, "{=} Method: " + eventPoint.method);
                    Teamspeak3Bot.debug(Language.EVENT, "{=} Class: " + eventPoint.methodClass.getSimpleName());
                    Teamspeak3Bot.debug(Language.EVENT, "{=} Event Type: " + eventPoint.eventType);
                }
            }
            method.setAccessible(isAccessible);
        }
        Teamspeak3Bot.debug(Language.EVENT,
            "------------------------------------------------------------------------------------");
    }

    public void registerEvents() {
        api.registerAllEvents();
        api.addTS3Listeners(new TS3Listener() {

            @Override public void onTextMessage(TextMessageEvent e) {
                if((e.getInvokerId() == 0)) return;
                if (e.getInvokerId() != bot.getClient().getId() ) {
                    String[] cmdArray = e.getMessage().split("\\s+");
                    int invokerId = e.getInvokerId();
                    Map<String, String> map = new HashMap<>(e.getMap());
                    map.put("source", String.valueOf(CommandSender.getSender(e.getTargetMode())));
                    map.put("command", e.getMessage());
                    Teamspeak3Bot.debug(Language.EVENT, "CommandPreProcessEvent > " + map);
                    fireEvent(EventType.EVENT_COMMAND_PRE_PROCESS, map, e);
                    return;
                }
                Teamspeak3Bot.debug(Language.EVENT, "TextMessageEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_TEXT_MESSAGE, e.getMap(), e);
            }

            @Override public void onClientJoin(ClientJoinEvent e) {
                Teamspeak3Bot.debug(Language.EVENT, "ClientJoinEvent > " + e.getMap().toString());
                Teamspeak3Bot.getClients().put(e.getClientId(), api.getClientInfo(e.getClientId()));
                fireEvent(EventType.EVENT_CLIENT_JOIN, e.getMap(), e);
            }

            @Override public void onClientLeave(ClientLeaveEvent e) {
                Teamspeak3Bot.debug(Language.EVENT, "ClientLeaveEvent > " + e.getMap().toString());
                Teamspeak3Bot.getClients().remove(e.getClientId());
                fireEvent(EventType.EVENT_CLIENT_LEAVE, e.getMap(), e);
            }

            @Override public void onServerEdit(ServerEditedEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "ServerEditedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_SERVER_EDIT, e.getMap(), e);
            }

            @Override public void onChannelEdit(ChannelEditedEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "ChannelEditedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_EDIT, e.getMap(), e);
            }

            @Override public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
                Teamspeak3Bot.debug(
                    Language.EVENT, "ChannelDescriptionEditedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_DESCRIPTION_CHANGED, e.getMap(), e);
            }

            @Override public void onClientMoved(ClientMovedEvent e) {
                Teamspeak3Bot.debug(Language.EVENT, "ClientMovedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CLIENT_MOVED, e.getMap(), e);
            }

            @Override public void onChannelCreate(ChannelCreateEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "ChannelCreateEvent > " + e.getMap().toString());
                Teamspeak3Bot.getChannels().put(e.getChannelId(), api.getChannelInfo(e.getChannelId()));
                fireEvent(EventType.EVENT_CHANNEL_CREATE, e.getMap(), e);
            }

            @Override public void onChannelDeleted(ChannelDeletedEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "ChannelDeletedEvent > " + e.getMap().toString());
                Teamspeak3Bot.getChannels().remove(e.getChannelId());
                fireEvent(EventType.EVENT_CHANNEL_DELETED, e.getMap(), e);
            }

            @Override public void onChannelMoved(ChannelMovedEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "ChannelMovedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_MOVED, e.getMap(), e);
            }

            @Override public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
                Teamspeak3Bot.debug(
                    Language.EVENT, "ChannelPasswordChangedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_PASSWORD_CHANGED, e.getMap(),e);
            }

            @Override public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "PrivilegeKeyUsedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_PRIVILEGE_KEY_USED, e.getMap(),e);
            }
        });
    }

    public void fireEvent(EventType type, Map<String, String> map, BaseEvent event) {
        run(type, map, event);
    }

    private void run(EventType type, Map<String, String> e, BaseEvent event) {
        Map<String, String> map = new HashMap<>(e);
        try {
            if (!events.containsKey(type))
                return;

            List<EventPoint> eventPoints = events.get(type);

            for(EventPoint eventPoint: eventPoints) {
                Method method = eventPoint.method;

                Class<Listener> ev = eventPoint.methodClass;
                Teamspeak3Bot.debug(Language.EVENT, "Methods: " + method);
                Teamspeak3Bot.debug(Language.EVENT, "Class: " + ev.getSimpleName());
                Teamspeak3Bot.debug(Language.EVENT, "Event Type: " + type);
                Teamspeak3Bot.debug(Language.EVENT, "Event Map: " + map);
                Teamspeak3Bot.debug(Language.EVENT, "Event Priority: " + method.getAnnotation(EventListener.class).priority());
                Teamspeak3Bot.debug(Language.EVENT, "executing...");

                switch (type) {
                    case EVENT_CHANNEL_CREATE:
                        method.invoke(ev.newInstance(), new EventChannelCreate(map, api, event));
                        break;
                    case EVENT_CHANNEL_DELETED:
                        method.invoke(ev.newInstance(), new EventChannelDeleted(map, api, event));
                        break;
                    case EVENT_CHANNEL_DESCRIPTION_CHANGED:
                        method.invoke(ev.newInstance(), new EventChannelDescriptionChanged(map, api, event));
                        break;
                    case EVENT_CHANNEL_EDIT:
                        method.invoke(ev.newInstance(), new EventChannelEdit(map, api, event));
                        break;
                    case EVENT_CHANNEL_MOVED:
                        method.invoke(ev.newInstance(), new EventChannelMoved(map, api, event));
                        break;
                    case EVENT_CHANNEL_PASSWORD_CHANGED:
                        method.invoke(ev.newInstance(), new EventChannelPasswordChanged(map, api, event));
                        break;
                    case EVENT_CLIENT_JOIN:
                        method.invoke(ev.newInstance(), new EventClientJoin(map, api, event));
                        break;
                    case EVENT_CLIENT_LEAVE:
                        method.invoke(ev.newInstance(), new EventClientLeave(map, api, event));
                        break;
                    case EVENT_CLIENT_MOVED:
                        method.invoke(ev.newInstance(), new EventClientMoved(map, api, event));
                        break;
                    case EVENT_PRIVILEGE_KEY_USED:
                        method.invoke(ev.newInstance(), new EventPrivilegeKeyUsed(map, api, event));
                        break;
                    case EVENT_SERVER_EDIT:
                        method.invoke(ev.newInstance(), new EventServerEdit(map, api, event));
                        break;
                    case EVENT_TEXT_MESSAGE:
                        method.invoke(ev.newInstance(), new EventTextMessage(map, api, event));
                        break;
                    case EVENT_COMMAND_PRE_PROCESS:
                        method.invoke(ev.newInstance(), new EventCommandPreProcess(map, api, event));
                        break;
                }
            }

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e1) {
            e1.printStackTrace();
        }

    }

    private static class EventPoint {
        private Method method;
        private Class<Listener> methodClass;
        private EventType eventType;

        private EventListener eventListener;

        public EventListener getEventAnnotation() {
            return eventListener;
        }
    }
}
