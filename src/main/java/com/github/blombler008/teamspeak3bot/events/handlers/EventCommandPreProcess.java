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

package com.github.blombler008.teamspeak3bot.events.handlers;

import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.commands.ChannelCommandSender;
import com.github.blombler008.teamspeak3bot.commands.CommandSender;
import com.github.blombler008.teamspeak3bot.events.Event;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;

import java.util.Map;

public class EventCommandPreProcess extends Event {

    protected CommandSender source;
    private Map<String, String> map;

    public EventCommandPreProcess(Teamspeak3Bot instance, Map<String, String> e, TS3Api api, BaseEvent event) {
        super(instance, e, api, event);
        this.map = e;
        this.source = CommandSender.getSender(map.get("source").toUpperCase());
        if(source instanceof ChannelCommandSender) {
            ((ChannelCommandSender) source).setChannelId(Integer.parseInt(map.get("channelid")));
        }
    }

    @Override public EventCommandPreProcess getEvent() {
        return this;
    }

    @Override public TS3Api getApi() {
        return null;
    }

    public CommandSender getCommandSource() {
        return source;
    }

    public int getChannelId() {
        return Integer.parseInt(map.getOrDefault("channelid", "0"));
    }
}
