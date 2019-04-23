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

import com.github.blombler008.teamspeak3bot.commands.CommandManager;
import com.github.blombler008.teamspeak3bot.commands.CommandSender;
import com.github.blombler008.teamspeak3bot.commands.CommandTemplate;
import com.github.blombler008.teamspeak3bot.commands.listeners.CommandHelp;
import com.github.blombler008.teamspeak3bot.commands.listeners.CommandPlugins;
import com.github.blombler008.teamspeak3bot.commands.listeners.CommandReload;
import com.github.blombler008.teamspeak3bot.console.ConsoleManager;
import com.github.blombler008.teamspeak3bot.console.PrintStreamLogger;
import com.github.blombler008.teamspeak3bot.events.EventManager;
import com.github.blombler008.teamspeak3bot.events.listeners.EventCommandFired;
import com.github.blombler008.teamspeak3bot.plugins.PluginManager;
import com.github.blombler008.teamspeak3bot.utils.Language;
import com.github.blombler008.teamspeak3bot.utils.Language.Languages;
import com.github.blombler008.teamspeak3bot.utils.StringUtils;
import com.github.blombler008.teamspeak3bot.utils.Validator;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.*;
import com.github.theholywaffle.teamspeak3.commands.Command;
import com.github.theholywaffle.teamspeak3.commands.CommandBuilderX;
import com.github.theholywaffle.teamspeak3.commands.parameter.KeyValueParam;
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

public class Teamspeak3Bot {

    private static File workDir;
    private static File logsDir;
    private static File logFile;
    private static File config;
    private static Logger logger;
    private static PrintStreamLogger out;
    private static Teamspeak3Bot instance;

    private boolean debuggerEnabled = false;
    private char customChar;

    private Bot bot;
    private ClientInfo owner;
    private ConsoleManager consoleManager;
    private EventManager eventManager;
    private Properties properties;
    private PluginManager pluginManager;
    private ServerQueryInfo botClient;
    private Map<Integer, ChannelInfo> channels = new HashMap<>();
    private Map<Integer, ClientInfo> clients = new HashMap<>();
    private CommandManager commandManager;

    private Teamspeak3Bot(String[] args) {

        if (instance == null) {

            enableDebugger(args);
            consoleManager = new ConsoleManager(this);

            debug(Language.MAIN, "File.separator == " + File.separator + "; In code: " + JSONObject.escape(File.separator));
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                debug(Language.MAIN, "MySQL Driver \"com.mysql.cj.jdbc.Driver\" has been loaded in!");
            } catch (Exception ignore) {
                debug(Language.MAIN, "Failed to load MySQL Driver!");
            }
            ;
            info("Set Working Directory: \"" + workDir.getAbsolutePath() + "\"");



            config = new File(workDir, "config.ini");
            try {
                if (!config.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    config.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            info("Config : \"" + config.getAbsolutePath() + "\"");

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



        initializeProperties();
        if (prepare()) {
            debug(Language.MAIN, "Bot is prepared to login");
        } else {
            return;
        }
        if (connect()) {
            debug(Language.MAIN, "Bot connected successful");
        } else {
            return;
        }

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

        commandManager = new CommandManager(getApi(), customChar, this);

        CommandSender.setInstance(this);

        eventManager = new EventManager(bot, getApi(), this);
        eventManager.registerEvents();

        CommandBuilderX x = new CommandBuilderX("gm", 1);
        x.add(new KeyValueParam("msg", Language.MAIN + "I just joined the server but got not fully implemented!!"));
        Command cmd = x.build();

        CommandTemplate helpTemplate = new CommandTemplate(this ,new String[]{"help", "?"}, "Shows the help list", "help", "Teamspeak");
        CommandTemplate reloadTemplate = new CommandTemplate(this ,new String[]{"reload", "rl"}, "Reload all plugins", "reload", "Teamspeak");
        CommandTemplate pluginsTemplate = new CommandTemplate(this ,new String[]{"plugins", "pl"}, "Shows a list of enabled plugins", "plugins", "TeamspeakT");

        commandManager.registerNewCommand(helpTemplate).setExecutor(new CommandHelp());
        commandManager.registerNewCommand(reloadTemplate).setExecutor(new CommandReload());
        commandManager.registerNewCommand(pluginsTemplate).setExecutor(new CommandPlugins());

        eventManager.addEventToProcessList(new EventCommandFired());
        pluginManager = new PluginManager(workDir, this);
        pluginManager.prepare(true);
        pluginManager.loadPlugins(true);
        pluginManager.enablePlugins(true);
        pluginManager.isFinished(consoleManager::setCompleters);

    }

    public static void main(String[] args) {
        workDir = new File(getWorkDirectory(args));
        if (workDir.mkdirs()) {
            System.out.println("The new work directory has been created!");
        }

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_dd_MM---HH_mm_ss_SSS");

        logsDir = new File(getWorkDirectory(), "logs" + File.separator + "log-" + date.format(formatter));
        logsDir.mkdirs();

        String nameLogFile = "log.txt.log";

        out = new PrintStreamLogger(System.out);
        logFile = new File(logsDir, nameLogFile);
        try {
            logFile.createNewFile();
            out.lg = new PrintStream(logFile);
            out.out = System.out;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setOut(out);
        logger = LoggerFactory.getLogger(Teamspeak3Bot.class);
        System.out.println("Initialised Console!");

        Teamspeak3Bot ts3bot = new Teamspeak3Bot(args);

    }

    public void debug(String l, String s) {
        if (debuggerEnabled)
            getLogger().debug(l + s);
    }

    public static File getWorkDir() {
        return workDir;
    }

    public static File getLogsDir() {
        return logsDir;
    }



    public void uploadErrorLog() {
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
                info(Language.MAIN + "Pastebin link >> \"" + pageResponse + "\"");
            else
                getLogger().error(Language.MAIN + "Pastebin upload failed!");

        } catch (IOException ignore) {
        }
    }

    public ServerQueryInfo getClient() {
        return botClient;
    }

    public ClientInfo getClient(int client) {
        return getClients().getOrDefault(client, null);
    }

    public void shutdown() {
        consoleManager.getThread().interrupt();
        bot.getApi().logout();
        System.exit(1);
    }

    public Map<Integer, ClientInfo> getClients() {
        return new HashMap<>(clients);
    }

    public Map<Integer, ChannelInfo> getChannels() {
        return new HashMap<>(channels);
    }

    public ClientInfo getOwner() {
        return owner;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    private void enableDebugger(String[] args) {
        debuggerEnabled = StringUtils.hasKey(args, "debug");
        if (debuggerEnabled)
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

    public Bot getBot() {
        return bot;
    }

    public char getCustomChar() {
        return customChar;
    }

    public TS3Api getApi() {
        return getBot().getApi();
    }

    public static File getLogFile() {
        return logFile;
    }

    public void writeToFile(String line) {
        out.writeSeparate(line + System.lineSeparator(), false);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void info(String str) {
        getLogger().info(str);
    }

    public static boolean getDebugged() {
        return instance.debuggerEnabled;
    }

    public ConsoleManager getConsoleManager() {
        return consoleManager;
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
            Languages lang = Language.getNew("english", this);
            debug(Language.LANGUAGE, lang.getProperties().toString());

            customChar = properties.getProperty("prefix").charAt(0);

            bot = new Bot(this, host, port, username, password, nickname, channel);
            debug(Language.MAIN, "Properties initialized");
        } catch (IOException ignore) {
        }
    }

    public void saveProperties(Properties properties) {

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
            properties.store(new FileOutputStream(config), comment);
        } catch (IOException ignore) {
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
