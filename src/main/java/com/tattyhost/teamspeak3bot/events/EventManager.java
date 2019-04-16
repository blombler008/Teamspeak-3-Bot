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

package com.tattyhost.teamspeak3bot.events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.tattyhost.teamspeak3bot.Bot;
import com.tattyhost.teamspeak3bot.Teamspeak3Bot;
import com.tattyhost.teamspeak3bot.commands.CommandManager;
import com.tattyhost.teamspeak3bot.commands.CommandSender;
import com.tattyhost.teamspeak3bot.events.handlers.*;
import com.tattyhost.teamspeak3bot.utils.Language;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class EventManager {
    private static Map<EventType, EventPoint> events = new HashMap<>();
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

        for (Method mod : od) {
            boolean isAccessible = mod.isAccessible();
            mod.setAccessible(true);

            Teamspeak3Bot.debug(Language.EVENT, "Method: \"" + mod.getName() + "\"");
            Teamspeak3Bot.debug(Language.EVENT, "- Declared Annotations: " + Arrays
                .toString(mod.getAnnotations()));
            Teamspeak3Bot.debug(
                Language.EVENT, "- Declared Parameters: " + Arrays.toString(mod.getParameters()));

            if (mod.isAnnotationPresent(EventListener.class) && mod.getParameterCount() == 1) {
                Parameter parm = mod.getParameters()[0];

                Teamspeak3Bot
                    .debug(Language.EVENT, "- Parameter Class: " + parm.getType().getName());

                if (parm.getType().getGenericSuperclass().getTypeName()
                    .equals(Event.class.getTypeName())) {
                    Class<? extends Event> evType =
                        EventType.getFromEventType((Class<? extends Event>) parm.getType());
                    EventPoint p = new EventPoint();
                    p.method = mod;
                    p.methodClass = objClass;
                    p.eventType = EventType.getForEventType(evType);

                    events.put(p.eventType, p);
                    Teamspeak3Bot.debug(Language.EVENT, "{=} Added to the Event Listener!");
                    Teamspeak3Bot.debug(Language.EVENT, "{=} Method: " + p.method.getName());
                    Teamspeak3Bot
                        .debug(Language.EVENT, "{=} Class: " + p.methodClass.getSimpleName());
                    Teamspeak3Bot.debug(Language.EVENT, "{=} Event Type: " + p.eventType);
                }
            }
            mod.setAccessible(isAccessible);
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
                    if ((CommandManager
                        .checkCommand(cmdArray, CommandSender.getSender(e.getTargetMode()),
                            invokerId))) {

                        Map<String, String> map = new HashMap<>(e.getMap());
                        map.put("source", CommandSender.getSender(e.getTargetMode()).toString());
                        map.put("command", e.getMessage());
                        fireEvent(EventType.EVENT_COMMAND_PRE_PROCESS, map);
                        Teamspeak3Bot.debug(Language.EVENT, "CommandPreProcessEvent > " + map);

                    }
                } else {
                    Teamspeak3Bot
                        .debug(Language.EVENT, "TextMessageEvent > " + e.getMap().toString());
                    fireEvent(EventType.EVENT_TEXT_MESSAGE, e.getMap());
                }
            }

            @Override public void onClientJoin(ClientJoinEvent e) {
                Teamspeak3Bot.debug(Language.EVENT, "ClientJoinEvent > " + e.getMap().toString());
                Teamspeak3Bot.getClients().put(e.getClientId(), api.getClientInfo(e.getClientId()));
                fireEvent(EventType.EVENT_CLIENT_JOIN, e.getMap());
            }

            @Override public void onClientLeave(ClientLeaveEvent e) {
                Teamspeak3Bot.debug(Language.EVENT, "ClientLeaveEvent > " + e.getMap().toString());
                Teamspeak3Bot.getClients().remove(e.getClientId());
                fireEvent(EventType.EVENT_CLIENT_LEAVE, e.getMap());
            }

            @Override public void onServerEdit(ServerEditedEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "ServerEditedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_SERVER_EDIT, e.getMap());
            }

            @Override public void onChannelEdit(ChannelEditedEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "ChannelEditedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_EDIT, e.getMap());
            }

            @Override public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
                Teamspeak3Bot.debug(
                    Language.EVENT, "ChannelDescriptionEditedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_DESCRIPTION_CHANGED, e.getMap());
            }

            @Override public void onClientMoved(ClientMovedEvent e) {
                Teamspeak3Bot.debug(Language.EVENT, "ClientMovedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CLIENT_MOVED, e.getMap());
            }

            @Override public void onChannelCreate(ChannelCreateEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "ChannelCreateEvent > " + e.getMap().toString());
                Teamspeak3Bot.getChannels().put(e.getChannelId(), api.getChannelInfo(e.getChannelId()));
                fireEvent(EventType.EVENT_CHANNEL_CREATE, e.getMap());
            }

            @Override public void onChannelDeleted(ChannelDeletedEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "ChannelDeletedEvent > " + e.getMap().toString());
                Teamspeak3Bot.getChannels().remove(e.getChannelId());
                fireEvent(EventType.EVENT_CHANNEL_DELETED, e.getMap());
            }

            @Override public void onChannelMoved(ChannelMovedEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "ChannelMovedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_MOVED, e.getMap());
            }

            @Override public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
                Teamspeak3Bot.debug(
                    Language.EVENT, "ChannelPasswordChangedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_CHANNEL_PASSWORD_CHANGED, e.getMap());
            }

            @Override public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
                Teamspeak3Bot
                    .debug(Language.EVENT, "PrivilegeKeyUsedEvent > " + e.getMap().toString());
                fireEvent(EventType.EVENT_PRIVILEGE_KEY_USED, e.getMap());
            }
        });
    }

    public void fireEvent(EventType type, Map<String, String> event) {
        run(type, event);
    }

    private void run(EventType type, Map<String, String> e) {

        try {
            if (!events.containsKey(type))
                return;
            Method m = events.get(type).method;


            Class<Listener> ev = events.get(type).methodClass;
            Teamspeak3Bot.debug(Language.EVENT, "Method: " + m.getName());
            Teamspeak3Bot.debug(Language.EVENT, "Class: " + ev.getSimpleName());
            Teamspeak3Bot.debug(Language.EVENT, "Event Type: " + type);
            Teamspeak3Bot.debug(Language.EVENT, "Event Map: " + e);
            Teamspeak3Bot.debug(Language.EVENT, "executing...");
            switch (type) {
                case EVENT_CHANNEL_CREATE:
                    m.invoke(ev.newInstance(), new EventChannelCreate(e, api));
                case EVENT_CHANNEL_DELETED:
                    m.invoke(ev.newInstance(), new EventChannelDeleted(e, api));
                case EVENT_CHANNEL_DESCRIPTION_CHANGED:
                    m.invoke(ev.newInstance(), new EventChannelDescriptionChanged(e, api));
                case EVENT_CHANNEL_EDIT:
                    m.invoke(ev.newInstance(), new EventChannelEdit(e, api));
                case EVENT_CHANNEL_MOVED:
                    m.invoke(ev.newInstance(), new EventChannelMoved(e, api));
                case EVENT_CHANNEL_PASSWORD_CHANGED:
                    m.invoke(ev.newInstance(), new EventChannelPasswordChanged(e, api));
                case EVENT_CLIENT_JOIN:
                    m.invoke(ev.newInstance(), new EventClientJoin(e, api));
                case EVENT_CLIENT_LEAVE:
                    m.invoke(ev.newInstance(), new EventClientLeave(e, api));
                case EVENT_CLIENT_MOVED:
                    m.invoke(ev.newInstance(), new EventClientMoved(e, api));
                case EVENT_PRIVILEGE_KEY_USED:
                    m.invoke(ev.newInstance(), new EventPrivilegeKeyUsed(e, api));
                case EVENT_SERVER_EDIT:
                    m.invoke(ev.newInstance(), new EventServerEdit(e, api));
                case EVENT_TEXT_MESSAGE:
                    m.invoke(ev.newInstance(), new EventTextMessage(e, api));
                case EVENT_COMMAND_PRE_PROCESS:
                    m.invoke(ev.newInstance(), new EventCommandPreProcess(e, api));
            }

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e1) {
            e1.printStackTrace();
        }

    }


    private static class EventPoint {
        private Method method;
        private Class<Listener> methodClass;
        private EventType eventType;
    }
}
