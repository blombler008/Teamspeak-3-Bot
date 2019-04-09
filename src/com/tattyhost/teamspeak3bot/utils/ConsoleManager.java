package com.tattyhost.teamspeak3bot.utils;

import com.tattyhost.teamspeak3bot.CommandSource;
import com.tattyhost.teamspeak3bot.Teamspeak3Bot;

import java.util.Scanner;

public class ConsoleManager {

    private static ConsoleManager instance;
    private boolean breaking;
    private Scanner input;
    private String line;

    public ConsoleManager() {
        breaking = true;
        line = "";
        input = new Scanner(System.in);

        if (instance == null)
            instance = this;
        new Thread(new Runnable() {

            @Override public void run() {
                try {
                    if (input.hasNextLine()) {
                        line = input.nextLine();
                        Teamspeak3Bot.getLogger().info(Language.CONSOLE + "ADMIN INPUT > " + line);

                        if (line.split(" ")[0].equalsIgnoreCase("exit")) {
                            return;
                        }
                        if (line.split(" ")[0].equalsIgnoreCase("quit")) {
                            Teamspeak3Bot.shutdown();
                            return;
                        }

                        if (line.split(" ")[0].equalsIgnoreCase("uploadErrorLog")) {
                            Teamspeak3Bot.uploadErrorLog();
                            run();
                        }

                        CommandManager
                            .executeCommand(line.split(" "), CommandSource.CONSOLE, -1, true);

                        System.out.print("> ");
                    }
                } catch (NullPointerException e) {
                    Teamspeak3Bot.getLogger().error("Error Occurred in Console Listener");
                    breaking = false;
                }
                if (breaking)
                    run();
            }
        }, "Console-Listener").start();
    }

    public static ConsoleManager getInstance() {
        return instance;
    }

}
