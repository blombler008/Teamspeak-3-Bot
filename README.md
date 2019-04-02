# Teamspeak 3 Bot
This bot is 'Plugin Based'. Which means Admins/Users are able to Create their own plugins for their server. It uses the [TeamSpeak-3-Java-API](https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API) by [TheHolyWaffle](https://github.com/TheHolyWaffle) as engine.

## Usage
To use the bot you can simply create a folder (eg. ```mkdir /home/example/bot```) and download put the ```Teamspeak-sq-bot.jar``` in that folder. As soon as you execute the jar, a folder called ```Teamspeak3Bot``` is created in that same directory where the bot jar lays.
You can specify the work dir by using the start parameter *```workdir```*. The Default work directory is ```./Teamspeak3Bot```.

## Parameters
Parameters are used like the following (```--Key=Value```).

| Parameter Key | Parameter Value | Example | Description |
| --- | --- | --- | --- |
|```--debug```| ***no value needed*** | ```--debug``` | Enables the Debugger. **NOTE:** complete information about the server query is shown (Password, Username, Server address, Port, etc)|
|```--auth-key```| | ```--auth-key=abcdefgh```| |
|```--workdir```| bot's working directory | ```--workdir=/etc/bot/```| Changes the work directory to the given path if valid. **DEFAULT:** ```./Teamspeak3Bot``` | 

## Config
The config is a simple properties file called ```config.ini```, which is located and generated in the work directory.

## Commands(Comming soon)
## Examples(Comming soon)
