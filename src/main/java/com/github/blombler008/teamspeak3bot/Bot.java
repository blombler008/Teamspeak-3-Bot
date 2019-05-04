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

package com.github.blombler008.teamspeak3bot;

import com.github.blombler008.teamspeak3bot.utils.Language;
import com.github.blombler008.teamspeak3bot.utils.StringUtils;
import com.github.blombler008.teamspeak3bot.utils.Validator;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;

import java.util.HashMap;
import java.util.Map;

public class Bot {

    private Map<String, String> map = new HashMap<>();

    private Teamspeak3Bot instance;
    private String host;
    private String username;
    private String password;
    private String nickname;
    private int channel;
    private int port;

    private TS3Api api;
    private TS3Config config;
    private TS3Query query;

    public Bot(Teamspeak3Bot instance, String host, String port, String username, String password, String nickname, String channel) {

        if (!testForIntiger(port) || !testForIntiger(channel)) {
            return;
        }

        this.instance = instance;

        this.nickname = nickname;
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = Integer.parseInt(port);
        this.channel = Integer.parseInt(channel);

        map.put("host", host);
        map.put("port", port);
        map.put("nickname", nickname);
        map.put("username", username);
        map.put("password", password);
        map.put("channel", channel);
    }

    public synchronized boolean prepareConnection() {
        if (Validator.notNull(config) || Validator.notNull(query) || Validator.notNull(api)) {
            String text;
            try {
                text = "Trying to connect to server: \'ts3serverquery://%host%:%port%\'";
                instance.debug(Language.BOT, StringUtils.replaceStringWith(text, map));

                config = new TS3Config();
                config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
                config.setHost(host);
                config.setQueryPort(port);
                config.setEnableCommunicationsLogging(Teamspeak3Bot.getDebugged());

                query = new TS3Query(config);
                query.connect();

                api = query.getApi();

                text = "Connected to: \'ts3serverquery://%host%:%port%\'";
                instance.debug(Language.BOT, StringUtils.replaceStringWith(text, map));

                return true;

            } catch (TS3ConnectionFailedException e) {
                text = "ERROR > Couldn't connect to server: \'ts3serverquery://%host%:%port%\'";
                instance.debug(Language.BOT, StringUtils.replaceStringWith(text, map));

                return false;
            }
        } else
            return false;
    }

    public synchronized boolean createConnection() {

        if (!Validator.notNull(api)) {
            String text;
            try {
                text = "Trying login as: \'%nickname%\', with username: \'%username%\', and password: \'%password%\'";
                instance.debug(Language.BOT, StringUtils.replaceStringWith(text, map));

                api.login(username, password);
                api.selectVirtualServerById(1, nickname);
                api.moveClient(api.whoAmI().getId(), channel);
                instance.debug(Language.BOT, "Logged as: \'" + nickname + "\'");

                return true;
            } catch (Exception e) {
                api.logout();
                query.exit();
                text = "Couldn't login as: \'%nickname%\', with username: \'%username%\', and password: \'%password%\'";
                instance.debug(Language.BOT, StringUtils.replaceStringWith(text, map));
                instance.shutdown();

                return false;
            }

        } else
            return false;
    }

    private boolean testForIntiger(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            Teamspeak3Bot.getLogger().error(StringUtils.replaceStringWith("Invalid number %number%!", "number", number));
            return false;
        }
    }


    public TS3Api getApi() {
        return api;
    }

    public ServerQueryInfo getClient() {
        return instance.getClient();
    }

    public String getExactClientName() {
        return getClient().getNickname();
    }

    public TS3Query getQuery() {
        return query;
    }
}
