# Teamspeak 3 Bot
[![Build Status](https://travis-ci.org/blombler008/Teamspeak-3-Bot.svg?branch=master)](https://travis-ci.org/blombler008/Teamspeak-3-Bot)  

This bot is 'Plugin Based'. Which means Admins/Users are able to Create their own plugins for their server. It uses the [TeamSpeak-3-Java-API](https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API) by [TheHolyWaffle](https://github.com/TheHolyWaffle) as engine.

## Usage
To use the bot you can simply create a folder (eg. ```mkdir /home/example/bot```) and download put the ```Teamspeak-sq-bot.jar``` in that folder. As soon as you execute the jar, a folder called ```Teamspeak3Bot``` is created in that same directory where the bot jar lays.
You can specify the work dir by using the start parameter *```workDir```*. The Default work directory is ```./Teamspeak3Bot```.

## Parameters
Parameters are used like the following (```--Key=Value```). A start parameter always starts with two dashes.

| Key | Value | Example | Description |
| --- | --- | --- | --- |
| debug | ***no value needed*** | ```--debug``` | Enables the Debugger. **NOTE:** Complete information about the server query is shown (Password, Username, Server address, Port, etc)|
| auth-key | | ```--auth-key=abcdefgh``` | |
| workDir | bot's working directory | ```--workDir=/etc/bot/``` | Changes the work directory to the given path if valid. **DEFAULT:** ```./Teamspeak3Bot``` | 

## Config
The config is a simple properties file called ```config.ini```, which is located and generated in the work directory. The first two lines of the config file is a comment, comment always starts with ```#```. **NOTE:** Everything has a key, a value and is required.

| Key  | Default | Example | Description |
| --- | --- | --- | --- |
| nickname | serverquerybot | nickname=Example Bot | Gives the bot a nickname when connecting to the server. Users who gets a message from the bot will see this name. |
| port | 10011 | port=12345 | Server Query port to connect |
| host | 127.0.0.1 | host=ts.example.com | The hostname/Ip-address of the server |
| username | username | username=example | Username to login as server query |
| password | password | password=12345678 | Password to login as server query |
| prefix | \\! | prefix=\\! | The command prefix for the com.github.theholywaffle.teamspeak3.commands entered by user in Teamspeak. **NOTE:** Special characters need a backslash like the `!` to be recognized! |
| lang | english | lang=english | Let you change the language for plugins |
 

## Commands
| Command | Aliases | Parameters | Examples | Description |
| --- | --- | --- | --- | --- |
| ```uploadErrorLog``` | ***none*** | ***none*** | ```uploadErrorLog``` | Uploads the log file to pastebin. **NOTE:** Make sure that the debugging mode is used (with the ```debug``` parameter on start). |
| ```help``` | ```[help, ?]``` | [command] | ```help```, ```help example``` | Shows the help of the given parameter, and list of com.github.theholywaffle.teamspeak3.commands if no parameter is present. |

## Examples(Comming soon)
