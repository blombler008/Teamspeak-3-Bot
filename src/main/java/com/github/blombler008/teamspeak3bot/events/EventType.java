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

import com.github.blombler008.teamspeak3bot.events.handlers.*;

public enum EventType {
    EVENT_CHANNEL_CREATE(EventChannelCreate.class), EVENT_CHANNEL_DELETED(
            EventChannelDeleted.class), EVENT_CHANNEL_DESCRIPTION_CHANGED(
            EventChannelDescriptionChanged.class), EVENT_CHANNEL_EDIT(
            EventChannelEdit.class), EVENT_CHANNEL_MOVED(
            EventChannelMoved.class), EVENT_CHANNEL_PASSWORD_CHANGED(
            EventChannelPasswordChanged.class), EVENT_CLIENT_JOIN(
            EventClientJoin.class), EVENT_CLIENT_LEAVE(EventClientLeave.class), EVENT_CLIENT_MOVED(
            EventClientMoved.class), EVENT_PRIVILEGE_KEY_USED(
            EventPrivilegeKeyUsed.class), EVENT_SERVER_EDIT(
            EventServerEdit.class), EVENT_COMMAND_PRE_PROCESS(
            EventCommandPreProcess.class), EVENT_TEXT_MESSAGE(EventTextMessage.class);


    private Class<? extends Event> evClass;

    EventType(Class<? extends Event> svClass) {
        evClass = svClass;
    }

    public static Class<? extends Event> getFromEventType(Class<? extends Event> svClass) {
        for (EventType ev : values()) {
            if (ev.evClass == svClass) {
                return ev.evClass;
            }
        }
        return null;
    }

    public static EventType getForEventType(Class<? extends Event> svClass) {
        for (EventType ev : values()) {
            if (ev.evClass == svClass) {
                return ev;
            }
        }
        return null;
    }

}

