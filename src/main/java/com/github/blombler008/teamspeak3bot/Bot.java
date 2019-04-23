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
import com.github.blombler008.teamspeak3bot.utils.Validator;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;

public class Bot {

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

    public Bot(Teamspeak3Bot instance, String host, String port, String username, String password,
        String nickname, String channel) {

        if (!testForPort(port))
            return;
        this.instance = instance;
        this.nickname = nickname;
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = Integer.parseInt(port);
        this.channel = Integer.parseInt(channel);
    }

    public synchronized boolean prepareConnection() {
        if (Validator.notNull(config) || Validator.notNull(query) || Validator.notNull(api)) {
            try {

                instance.debug(Language.BOT,
                    "Trying to connect to server: \'ts3serverquery://" + host + ":" + port + "\'");

                config = new TS3Config();
                config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
                config.setHost(host);
                config.setQueryPort(port);
                config.setEnableCommunicationsLogging(Teamspeak3Bot.getDebugged());

                query = new TS3Query(config);
                query.connect();

                api = query.getApi();

                instance.debug(Language.BOT,
                    "Connected to: \'ts3serverquery://" + host + ":" + port + "\'");

                return true;

            } catch (TS3ConnectionFailedException e) {

                instance.debug(Language.BOT,
                    "ERROR > Couldn't connect to server: \'ts3serverquery://" + host + ":" + port
                        + "\'");

                return false;
            }
        } else
            return false;
    }

    public synchronized boolean createConnection() {
        if (!Validator.notNull(api)) {
            try {
                instance.debug(Language.BOT,
                    "Trying login as: \'" + nickname + "\', with username: \'" + username
                        + "\', and password: \'" + password + "\'");

                api.login(username, password);
                api.selectVirtualServerById(1, nickname);
                api.moveClient(api.whoAmI().getId(), channel);
                instance.debug(Language.BOT, "Logged as: \'" + nickname + "\'");

                return true;
            } catch (Exception e) {
                api.logout();
                query.exit();

                instance.debug(Language.BOT,
                    "Couldn't login as: \'" + nickname + "\', with username: \'" + username
                        + "\', and password: \'" + password + "\'");
                instance.shutdown();

                return false;
            }

        } else
            return false;
    }

    private boolean testForPort(String port) {
        try {
            Integer.parseInt(port);
            return true;
        } catch (NumberFormatException e) {
            Teamspeak3Bot.getLogger().error("Port not valid!");
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
