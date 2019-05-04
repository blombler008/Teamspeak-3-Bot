# Teamspeak 3 Bot
[![Build Status](https://img.shields.io/travis/blombler008/Teamspeak-3-Bot.svg?style=for-the-badge)](https://travis-ci.org/blombler008/Teamspeak-3-Bot) [![Top Language](https://img.shields.io/github/languages/top/blombler008/Teamspeak-3-Bot.svg?style=for-the-badge)](https://github.com/blombler008/Teamspeak-3-Bot/) [![GitHub repo size](https://img.shields.io/github/repo-size/blombler008/Teamspeak-3-Bot.svg?style=for-the-badge)](https://github.com/blombler008/Teamspeak-3-Bot/) [![GitHub](https://img.shields.io/github/license/blombler008/Teamspeak-3-Bot.svg?style=for-the-badge)](https://github.com/blombler008/Teamspeak-3-Bot/)

> This bot is 'Plugin Based', Which means Admins/Users are able to Create their own plugins for their server. Which uses the [TeamSpeak-3-Java-API](https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API) by [TheHolyWaffle](https://github.com/TheHolyWaffle) as engine.

### Features
> - Plugins can be loaded while running the query client.
> - MySQL driver, ready to connect.
> - Command and Event manager.

### Usage
> To use the bot you can simply create a folder (eg. ```mkdir /home/example/bot```) and download put the ```Teamspeak-sq-bot.jar``` in that folder. As soon as you execute the jar, a folder called ```Teamspeak3Bot``` is created in that same directory where the bot jar lays.
> You can specify the work dir by using the start parameter *```workDir```*. The Default work directory is ```./Teamspeak3Bot```.
>
> **Maven (Coming soon)**
> > 
>
> **Standalone(jar)**
> > If you are not able to use maven you can download the standalone jar with dependencies and implement that into your project.

### Parameters
> Parameters are used like the following (```--Key=Value```). A start parameter always starts with two dashes.

| Option | Value | Required | Example | Description |
| --- | --- | --- | --- | --- |
| debug |  | No | ```--debug``` | Enables the Debugger. **NOTE:** Complete information about the server query is shown (Password, Username, Server address, Port, etc)|
| workDir | bot's work directory | No | ```--workDir=/etc/bot/``` | Changes the work directory to the given path if valid. **DEFAULT:** ```./Teamspeak3Bot``` | 
| auth-key |  |  | ```--auth-key=abcdefgh``` | Only uses while developing |

### Config
> The config is a simple properties file called ```config.ini```, which is located and generated in the work directory. The first two lines of the config file is a comment, comment always starts with ```#```. **NOTE:** Everything has a key, a value and is required.

| Option  | Default | Example | Description |
| --- | --- | --- | --- |
| ```nickname``` | ```serverquerybot``` | ```nickname=Example Bot``` | Gives the bot a nickname when connecting to the server. Users who gets a message from the bot will see this name. |
| ```port``` | ```10011``` | ```port=12345``` | Server Query port to connect |
| ```host``` | ```127.0.0.1``` | ```host=ts.example.com``` | The hostname/Ip-address of the server |
| ```username``` | ```username``` | ```username=example``` | Username to login as server query |
| ```password``` | ```password``` | ```password=12345678``` | Password to login as server query |
| ```prefix``` | ```\!``` | ```prefix=\!``` | The command prefix for the com.github.theholywaffle.teamspeak3.commands entered by user in Teamspeak. **NOTE:** Special characters need a backslash like the `!` to be recognized! |
| ```lang``` | ```english``` | ```lang=english``` | Let you change the language for plugins |
| ```owner``` | ```1234567890abdef``` | ```owner=zbv5DDqRa3jy4LuM1cfUeyurud8\\=``` | Sets the owner of the bot, so the owner kan execute admin commands first before any of the other users. **NOTE:** Put the UID from your own client in this property |
| ```channel``` | ```0``` | ```channel=0``` | Sets the channel where the bot ist going to connect to. **NOTE:** invalid channel id causes errors |
 

### Commands
> A list of Commands 

| Command | Aliases | Parameters | Examples | Description |
| --- | --- | --- | --- | --- |
| ```uploadErrorLog``` |  |  | ```uploadErrorLog``` | Uploads the log file to pastebin. **NOTE:** Make sure that the debugging mode is used (with the ```debug``` parameter on start). |
| ```help``` | ```[help, ?]``` | [command] | ```help```, ```help example``` | Shows the help of the given parameter, and list of commands if no parameter is present. |
| ```reload``` | ```[reload, rl]``` | [plugin] | ```reload```, ```reload ExamplePlugin-v1``` | Reloads the given plugin, and reloads all plugins if no parameter is given |
| ```plugins``` | ```[plugins, pl]``` |  | ```plugins``` | Shows a list of plugins. |
| ```stop``` | ```[stop, quit, exit]``` | | ```stop``` | Logs out of the teamspeak server query and exit the program. |

### Examples 

**Adding an event listening**  
> To add an event listener you have to add the event class to the event manager. Like Shown in the Example class.
> ```java
> public class Example extends JavaPlugin {
>     public void onEnable() {
>         getInstance().getEventManager().addEventToProcessList(new ExampleEvent());
>     }
> }
> ```
> Now we have to register an event in the ```ExampleEvent``` class, it have to implement the ```Listener``` interface. The annotation ```@EventListener``` lets the manager know the following method is a event listener. The name of the method does not matter, what matter does is the parameter, make sure it is only one and extends the ```Event``` class.
> ```java
> public class ExampleEvent implements Listener {
>     @EventListener
>     public void onTextMessage(EventTextMessage e) {
>         // do stuff
>    }
> }
> ```
**List of Events**  
> Events always starts with ```Event``` in the beginning.
> - ```EventChannelCreate```
> - ```EventChannelDeleted```
> - ```EventChannelDescriptionChanged```
> - ```EventChannelEdit```
> - ```EventChannelMoved```
> - ```EventChannelPasswordChanged```
> - ```EventClientJoin```
> - ```EventClientLeave```
> - ```EventClientMoved```
> - ```EventCommandPreProcess```
> - ```EventPrivilegeKeyUsed```
> - ```EventServerEdit```
> - ```EventTextMessage```


**Adding an command**  
> To add a command you need to create a ```CommandTemplate```. To register the command we have to call the internal ```setExecutor(CommandExecutor cmdExec)``` method, we simply parse in a new instance of a class which extends ```CommandExecutor``` like shown. Method parameter order: 
> ```java
> public class CommandTemplate {
>     public CommandTemplate(Teamspeak3Bot instance, String[] aliases, String description, String commandString, String pluginName, String usage){}
> }
> ```
> ```java
> public class Example extends JavaPlugin {
>     public void onEnable() {
>         getInstance().getCommandManager().getCommand("example").setExecutor(new ExampleCommand());
>     }
> }
> ```
> In the ```ExampleCommand``` class we have let it extend ```CommandExecutor``` and implement the ```run``` method. The ```run``` method is executed when a user enters the command or alias.
> ```java
> public class ExampleCommand extends CommandExecutor {
> 
>     @Override
>     public void run(CommandSender source, Command cmd, String commandLabel, String[] args) {
>         // do stuff
>     }    
> }
> ```

**Examples** can be found [here](https://github.com/blombler008/Teamspeak-3-Bot/tree/master/Examples) are 2 files of the examples on top ...
