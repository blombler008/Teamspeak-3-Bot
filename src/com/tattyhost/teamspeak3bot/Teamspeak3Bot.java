package com.tattyhost.teamspeak3bot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import com.tattyhost.teamspeak3bot.listeners.Commnd_Help;
import com.tattyhost.teamspeak3bot.utils.*;
import com.tattyhost.teamspeak3bot.utils.Language.Languages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Set;

public class Teamspeak3Bot {

    private static Bot bot;
    private static File workDir;
    private static File logsDir;
    private static File logFile;
    private static File config;
    private static Teamspeak3Bot instance;
    private static Logger logger;
    private static Properties properties;
    private static PluginManager pluginManager;
    private static ServerQueryInfo botClient;

    public static boolean debuggerEnabled = false;
    protected static char customChar;

    private Teamspeak3Bot(String[] args) {
        if(instance == null) {
            String strWorkDir = getWorkDirectory(args);
            workDir = new File(strWorkDir);
            getLogger().info("Set Working Directory: \"" + strWorkDir + "\"");
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
            config = null;
            properties = null;
        }
        
        if ((Validator.notNull(config)))
            throw new AssertionError("Config is null!");
        if ((Validator.notNull(properties)))
            throw new AssertionError("Bot Properties is null!");

    }





    public static void main(String [] args) {
        workDir = new File(getWorkDirectory(args));
        workDir.mkdirs();
        logsDir = new File(getWorkDirectory(args) + "\\logs");
        logsDir.mkdir();

        String name = "log-";
        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter fdt = DateTimeFormatter.ofPattern("yyyy_dd_MM-HH_mm_ss_SSS");
        name += dt.format(fdt) + ".txt.log";

        PrintStreamLogger prl = new PrintStreamLogger(System.out);
        logFile = new File(logsDir, name);
        try {
            logFile.createNewFile();
            prl.lg = new PrintStream(logFile);
            prl.out = System.out;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setOut(prl);
        System.out.println("Initialised Console!");


        logger = LoggerFactory.getLogger(Teamspeak3Bot.class);
        enableDebugger(args);

        Teamspeak3Bot ts3bot = new Teamspeak3Bot(args);
        ts3bot.initializeProperties();
        if(ts3bot.prepare()) debug(Language.MAIN + "Bot is prepared to login");
        else return;
        if(ts3bot.connect()) debug(Language.MAIN + "Bot connected successful");
        else return;

        botClient = getApi().whoAmI();
        debug(Language.MAIN + "ServerQuery > " + botClient.getMap());

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

    public static File getWorkDir() {
        return getInstance().workDir;
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

            String key = "0ec2eb25b6166c0c27a394ae118ad829"; // Found it some where ... so i guess free to use ^^
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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

            if(pageResponse.startsWith("http")) getLogger().info(Language.MAIN + "Pastebin link >> \"" + pageResponse + "\"");
            else getLogger().error(Language.MAIN + "Pastebin upload failed!");

        } catch (IOException ignore) {}
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
        Languages lang = Language.getNew(/*properties.getProperty("lang")*/ "english");
        getLogger().info(Language.LANGUAGE + lang.getProperties().toString());

        customChar = properties.getProperty("prefix").charAt(0);

        bot = new Bot(host, port, username, password, nickname);
        debug(Language.MAIN + "Properties initialized");
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

        if(!properties.containsKey("lang")){
            properties.setProperty("lang", "english");
        }

        try {
            //noinspection deprecation
            properties.save(new FileOutputStream(config), "Configuration File for the Teamspeak 3 bot");
        } catch (FileNotFoundException ignore) {}
    }

    private static void setInstance(Teamspeak3Bot instance) {
        Teamspeak3Bot.instance = instance;
    }

    private static void enableDebugger(String [] args){
        Teamspeak3Bot.debuggerEnabled = StringUtils.hasKey(args, "debug");
        if(Teamspeak3Bot.debuggerEnabled)
            getLogger().info(Language.MAIN + "Debugger has been enabled!");
    }

    private static String getWorkDirectory(String[] args) {
        String ret = "Teamspeak3Bot/";
        if(StringUtils.hasKey(args,"workDir")) {
            ret = StringUtils.getValueOf(args, "workDir");

            if (!Validator.isValidPath(ret) || !Validator.isDirectory(ret))
                ret = "Teamspeak3Bot/";
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
