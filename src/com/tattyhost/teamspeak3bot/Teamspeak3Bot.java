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

package com.tattyhost.teamspeak3bot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3QueryX;
import com.github.theholywaffle.teamspeak3.api.wrapper.*;
import com.github.theholywaffle.teamspeak3.commands.Command;
import com.github.theholywaffle.teamspeak3.commands.CommandBuilderX;
import com.github.theholywaffle.teamspeak3.commands.parameter.KeyValueParam;
import com.tattyhost.teamspeak3bot.listeners.Command_Help;
import com.tattyhost.teamspeak3bot.listeners.Command_Plugins;
import com.tattyhost.teamspeak3bot.listeners.Command_Reload;
import com.tattyhost.teamspeak3bot.listeners.Event_CommandFired;
import com.tattyhost.teamspeak3bot.utils.Language;
import com.tattyhost.teamspeak3bot.utils.Language.Languages;
import com.tattyhost.teamspeak3bot.utils.PrintStreamLogger;
import com.tattyhost.teamspeak3bot.utils.StringUtils;
import com.tattyhost.teamspeak3bot.utils.Validator;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Teamspeak3Bot {

    public static boolean debuggerEnabled = false;
    protected static char customChar;
    private static Bot bot;
    private static File workDir;
    private static File logsDir;
    private static File logFile;
    private static File eventLogFile;
    private static File config;
    private static Teamspeak3Bot instance;
    private static Logger logger;
    private static Properties properties;
    private static PluginManager pluginManager;
    private static ServerQueryInfo botClient;
    private static PrintStreamLogger out;
    private static EventManager eventManager;
    private static ClientInfo owner;

    static Map<Integer, ChannelInfo> channels = new HashMap<>();
    static Map<Integer, ClientInfo> clients = new HashMap<>();

    private Teamspeak3Bot(String[] args) {

        if (instance == null) {
            String strWorkDir = getWorkDirectory(args);
            workDir = new File(strWorkDir);
            getLogger().info("Set Working Directory: \"" + strWorkDir + "\"");
            workDir.mkdirs();

            config = new File(workDir, "config.ini");
            try {
                if (!config.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    config.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            getLogger().info("Config : \"" + config.getAbsolutePath() + "\"");

            try {
                properties = new Properties();
                properties.load(new FileReader(config));
                saveProperties(properties);

            } catch (IOException e) {
                e.printStackTrace();
            }

            setInstance(this);
        } else {
            config = null;
            properties = null;
        }

        if ((Validator.notNull(config)))
            throw new AssertionError("Config is null!");
        if ((Validator.notNull(properties)))
            throw new AssertionError("Bot Properties is null!");

    }

    public static void main(String[] args) {
        workDir = new File(getWorkDirectory(args));
        workDir.mkdirs();

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_dd_MM---HH_mm_ss_SSS");

        logsDir = new File(getWorkDirectory(), "logs" + File.separator + "log-" + date.format(formatter));
        logsDir.mkdir();

        String nameLogFile = "log.txt.log";
        String nameEventLogFile = "event.txt.log";

        out = new PrintStreamLogger(System.out);
        logFile = new File(logsDir, nameLogFile);
        eventLogFile = new File(logsDir, nameLogFile);
        try {
            logFile.createNewFile();
            out.lg = new PrintStream(logFile);
            out.out = System.out;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setOut(out);
        System.out.println("Initialised Console!");
        new ConsoleManager();

        logger = LoggerFactory.getLogger(Teamspeak3Bot.class);
        enableDebugger(args);

        debug(Language.MAIN, "File.separator == " + File.separator + "; In code: " + JSONObject.escape(File.separator));
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            debug(Language.MAIN, "MySQL Driver \"com.mysql.cj.jdbc.Driver\" has been loaded in!");
        } catch (Exception ignore) {
            debug(Language.MAIN, "Failed to load MySQL Driver!");
        }

        Teamspeak3Bot ts3bot = new Teamspeak3Bot(args);
        ts3bot.initializeProperties();
        if (ts3bot.prepare())
            debug(Language.MAIN, "Bot is prepared to login");
        else
            return;
        if (ts3bot.connect())
            debug(Language.MAIN, "Bot connected successful");
        else
            return;

        owner = getApi().getClientByUId(properties.getProperty("owner"));
        botClient = getApi().whoAmI();
        int id;

        for (Client c : getApi().getClients()) {
            id = c.getId();
            clients.put(id, getApi().getClientInfo(id));
        }

        for (Channel c : getApi().getChannels()) {
            id = c.getId();
            channels.put(id, getApi().getChannelInfo(id));
        }

        debug(Language.MAIN, "Owner > " + owner.getMap());
        debug(Language.MAIN, "ServerQuery > " + botClient.getMap());
        debug(Language.MAIN, "Channels > " + channels.size());
        debug(Language.MAIN, "Online Clients > " + clients.size());

        new CommandManager(getApi(), customChar);

        eventManager = new EventManager(bot, getApi());
        eventManager.registerEvents();

        CommandBuilderX x = new CommandBuilderX("gm", 1);
        x.add(new KeyValueParam("msg", Language.MAIN + "I just joined the server but got not fully implemented!!"));
        Command cmd = x.build();

        CommandManager.registerNewCommand("help", new Command_Help());
        CommandManager.registerNewCommand("reload", new Command_Reload());
        CommandManager.registerNewCommand("plugins", new Command_Plugins());
        EventManager.addEventToProcessList(new Event_CommandFired());
        pluginManager = new PluginManager(workDir);
        pluginManager.prepare(true);
        pluginManager.loadPlugins(true);
        pluginManager.enablePlugins(true);


        TS3QueryX.doCommandAsync(bot.getQuery(), cmd);
    }

    public static void debug(String l, String s) {
        if (Teamspeak3Bot.debuggerEnabled)
            getLogger().debug(l + s);
    }

    public static File getWorkDir() {
        return workDir;
    }

    public static File getLogsDir() {
        return logsDir;
    }



    public static void uploadErrorLog() {
        try {
            StringBuilder content = new StringBuilder();
            BufferedReader logReader = new BufferedReader(new FileReader(logFile));
            String logLine;
            while ((logLine = logReader.readLine()) != null) {
                if (content.length() > 0) {
                    content.append('\n');
                }
                content.append(logLine);
            }
            logReader.close();

            StringBuilder str = new StringBuilder();
            // Found it some where ... so i guess free to use ^^
            String key = "0ec2eb25b6166c0c27a394ae118ad829";
            //String length = "10M"; // Just for testings
            str.append("api_option=paste&");
            str.append("api_dev_key=" + key + "&");
            //str.append("&api_paste_expire_date=" + length + "&"); // Just for testings
            str.append("api_paste_code=" + URLEncoder.encode(new String(content), "UTF-8") + "&");
            str.append("api_paste_name=" + logFile.getName() + "&");

            URL url = new URL("http://pastebin.com/api/api_post.php");

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

            wr.write(new String(str));
            wr.flush();
            wr.close();

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(line);
            }

            reader.close();
            String pageResponse = new String(builder);

            if (pageResponse.startsWith("http"))
                getLogger().info(Language.MAIN + "Pastebin link >> \"" + pageResponse + "\"");
            else
                getLogger().error(Language.MAIN + "Pastebin upload failed!");

        } catch (IOException ignore) {
        }
    }

    public static ServerQueryInfo getClient() {
        return botClient;
    }

    public static ClientInfo getClient(int client) {
        return getClients().getOrDefault(client, null);
    }

    public static void shutdown() {
        bot.getApi().logout();
        System.exit(1);
    }

    public static Map<Integer, ClientInfo> getClients() {
        return new HashMap<>(clients);
    }

    public static Map<Integer, ChannelInfo> getChannels() {
        return new HashMap<>(channels);
    }

    public static ClientInfo getOwner() {
        return owner;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    private static void enableDebugger(String[] args) {
        Teamspeak3Bot.debuggerEnabled = StringUtils.hasKey(args, "debug");
        if (Teamspeak3Bot.debuggerEnabled)
            getLogger().info(Language.MAIN + "Debugger has been enabled!");
    }

    private static File getWorkDirectory() {
        return workDir;
    }

    private static String getWorkDirectory(String[] args) {
        String ret = "Teamspeak3Bot";
        if (StringUtils.hasKey(args, "workDir")) {
            ret = StringUtils.getValueOf(args, "workDir");

            if (!Validator.isValidPath(ret) || !Validator.isDirectory(ret))
                ret = "Teamspeak3Bot";
        }
        return ret + File.separator;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static Teamspeak3Bot getInstance() {
        return instance;
    }

    private static void setInstance(Teamspeak3Bot instance) {
        Teamspeak3Bot.instance = instance;
    }

    public static Bot getBot() {
        return bot;
    }

    public static char getCustomChar() {
        return customChar;
    }

    public static TS3Api getApi() {
        return getBot().getApi();
    }

    public static File getLogFile() {
        return logFile;
    }

    public static void writeToFile(String line) {
        out.writeSeparate(line + System.lineSeparator(), false);
    }

    public static EventManager getEventManager() {
        return eventManager;
    }

    private boolean connect() {
        return bot.createConnection();
    }

    private boolean prepare() {
        return bot.prepareConnection();
    }

    public synchronized void initializeProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(config));
            String host = properties.getProperty("host");
            String port = properties.getProperty("port");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            String nickname = properties.getProperty("nickname");
            String channel = properties.getProperty("channel");
            // Languages lang = Language.getNew(properties.getProperty("lang"));
            // languages getting added later //
            Languages lang = Language.getNew("english");
            debug(Language.LANGUAGE, lang.getProperties().toString());

            customChar = properties.getProperty("prefix").charAt(0);

            bot = new Bot(host, port, username, password, nickname, channel);
            debug(Language.MAIN, "Properties initialized");
        } catch (IOException ignore) {
        }
    }

    public void saveProperties(Properties properties) {

        Set<String> list = properties.stringPropertyNames();

        if (!properties.containsKey("host"))
            properties.setProperty("host", "127.0.0.1");

        if (!properties.containsKey("port"))
            properties.setProperty("port", "10011");

        if (!properties.containsKey("username"))
            properties.setProperty("username", "username");

        if (!properties.containsKey("password"))
            properties.setProperty("password", "password");

        if (!properties.containsKey("nickname"))
            properties.setProperty("nickname", "serverquerybot");

        if (!properties.containsKey("prefix"))
            properties.setProperty("prefix", "!");

        if (!properties.containsKey("lang"))
            properties.setProperty("lang", "english");

        if (!properties.containsKey("owner"))
            properties.setProperty("owner", "1234567890abdef");

        if (!properties.containsKey("channel"))
            properties.setProperty("channel", "0");


        try {
            String comment = "Configuration File for the Teamspeak 3 bot";
            //noinspection deprecation
            properties.save(new FileOutputStream(config), comment);
        } catch (FileNotFoundException ignore) {
        }
    }

}
