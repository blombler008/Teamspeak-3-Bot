package com.tattyhost.teamspeak3bot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import com.tattyhost.teamspeak3bot.listeners.Commnd_Help;
import com.tattyhost.teamspeak3bot.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.Set;

public class Teamspeak3Bot {

    private final File config;
    private final File workDir;
    private Bot bot;
    private static Teamspeak3Bot instance;
    private static Logger logger;
    private static Properties properties;
    private static PluginManager pluginManager;
    private static ServerQueryInfo botClient;

    public static boolean debuggerEnabled = false;
    protected static char customChar;

    private Teamspeak3Bot(String[] args) {
        if(instance == null) {
            workDir = new File(getWorkDirectory(args));
            workDir.mkdirs();

            config = new File(workDir, "config.ini");
            try {
                if(!config.exists()) {
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
                saveProperties();

            } catch (IOException e) {
                e.printStackTrace();
            }

            setInstance(this);
        } else {
            workDir = null;
            config = null;
        }

        Validator.notNull(workDir);
        Validator.notNull(config);
        Validator.notNull(properties);

    }





    public static void main(String [] args) {
        logger = LoggerFactory.getLogger(Teamspeak3Bot.class);
        enableDebugger(args, MD5.getKey());
        debug("{?} > {?} Main Bot, {+} Event, {@} Command, {#} Console, {~} Plugin");


        Teamspeak3Bot ts3bot = new Teamspeak3Bot(args);
        ts3bot.initializeProperties();
        if(ts3bot.prepare()) debug("{?} > Bot is prepared to login");
        else return;
        if(ts3bot.connect()) debug("{?} > Bot connected successful");
        else return;

        botClient = getApi().whoAmI();
        debug("{?} ServerQuery > " + botClient.getMap());

        new CommandManager(getApi(), customChar);
        new ConsoleManager();
        new EventManager(ts3bot.bot, getApi()).registerEvents();

        CommandManager.registerNewCommand("help", new Commnd_Help());
        // EventManager.addEventToProcessList(new TestEvent());
        pluginManager = new PluginManager(ts3bot.workDir);
        pluginManager.prepare(true);
        pluginManager.loadPlugins(true);
        pluginManager.enablePlugins(true);




    }

    public static void debug(String s) {
        if(Teamspeak3Bot.debuggerEnabled) getLogger().debug(s);
    }

    private boolean connect() {
        return bot.createConnection();
    }

    private boolean prepare() {
        return bot.prepareConnection();
    }

    public synchronized void initializeProperties() {
        String host = properties.getProperty("host");
        String port = properties.getProperty("port");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String nickname = properties.getProperty("nickname");

        customChar = properties.getProperty("prefix").charAt(0);

        bot = new Bot(host, port, username, password, nickname);
        debug("{?} > Properties initialized");
    }
    public void saveProperties() {

        Set<String> list = properties.stringPropertyNames();

        if(!properties.containsKey("host"))
            properties.setProperty("host", "127.0.0.1");

        if(!properties.containsKey("port"))
            properties.setProperty("port", "10011");

        if(!properties.containsKey("username"))
            properties.setProperty("username", "example_user");

        if(!properties.containsKey("password"))
            properties.setProperty("password", "example_password");

        if(!properties.containsKey("nickname"))
            properties.setProperty("nickname", "example_bot");

        if(!properties.containsKey("prefix")){
            properties.setProperty("prefix", "!");
        }

        try {
            //noinspection deprecation
            properties.save(new FileOutputStream(config), "Configuration File for the Teamspeak 3 bot");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void setInstance(Teamspeak3Bot instance) {
        Teamspeak3Bot.instance = instance;
    }

    private static void enableDebugger(String [] args, String key){
        //getLogger().info("" + StringUtils.hasValue(args, "auth-key", key));
        if(StringUtils.hasValue(args, "auth-key", key)) {
            Teamspeak3Bot.debuggerEnabled = StringUtils.hasKey(args, "debug");
            getLogger().info("Debugger has been enabled [key: " + MD5.getRawKey() + "]");
        }
    }

    private String getWorkDirectory(String[] args) {
        String ret = "Teamspeak3Bot/";
        if(StringUtils.hasKey(args,"workDir")) {
            ret = StringUtils.getValueOf(args, "workDir");

            if (!Validator.isValidPath(ret) || !Validator.isDirectory(ret))
                ret = "Teamspeak3Bot/";

            getLogger().info("Set Working Directory: \"" + ret + "\"");
        }
        return ret;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static Teamspeak3Bot getInstance() {
        return instance;
    }

    public static Bot getBot() {
        return getInstance().bot;
    }

    public static char getCustomChar() {
        return customChar;
    }

    public static TS3Api getApi() {
        return getBot().getApi();
    }

}
