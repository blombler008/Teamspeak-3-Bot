package com.tattyhost.teamspeak3bot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import com.tattyhost.teamspeak3bot.utils.Language;
import com.tattyhost.teamspeak3bot.utils.Validator;

public class Bot {

    private String host;
    private int port;
    private String username;
    private String password;
    private String nickname;

    private TS3Api api;
    private TS3Config config;
    private TS3Query query;

    public Bot(String host, String port, String username, String password, String nickname) {

        if (!testForPort(port))
            return;

        this.nickname = nickname;
        this.port = Integer.parseInt(port);
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public synchronized boolean prepareConnection() {
        if (Validator.notNull(config) || Validator.notNull(query) || Validator.notNull(api)) {
            try {

                Teamspeak3Bot.debug(
                    Language.BOT + "Trying to connect to server: \'ts3serverquery://" + host + ":"
                        + port + "\'");

                config = new TS3Config();
                config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
                config.setHost(host);
                config.setQueryPort(port);
                config.setEnableCommunicationsLogging(Teamspeak3Bot.debuggerEnabled);

                query = new TS3Query(config);
                query.connect();

                api = query.getApi();

                Teamspeak3Bot.debug(
                    Language.BOT + "Connected to: \'ts3serverquery://" + host + ":" + port + "\'");

                return true;

            } catch (TS3ConnectionFailedException e) {

                Teamspeak3Bot.debug(
                    Language.BOT + "ERROR > Couldn't connect to server: \'ts3serverquery://" + host
                        + ":" + port + "\'");

                return false;
            }
        } else
            return false;
    }

    public synchronized boolean createConnection() {
        if (!Validator.notNull(api)) {
            try {
                Teamspeak3Bot.debug(
                    Language.BOT + "Trying login as: \'" + nickname + "\', with username: \'"
                        + username + "\', and password: \'" + password + "\'");

                api.login(username, password);
                api.selectVirtualServerById(1, nickname);
                Teamspeak3Bot.debug(Language.BOT + "Logged as: \'" + nickname + "\'");

                return true;
            } catch (Exception e) {
                api.logout();
                query.exit();

                Teamspeak3Bot.debug(
                    Language.BOT + "Couldn't login as: \'" + nickname + "\', with username: \'"
                        + username + "\', and password: \'" + password + "\'");

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
        return Teamspeak3Bot.getClient();
    }

    public String getExactClientName() {
        return getClient().getNickname();
    }
}
