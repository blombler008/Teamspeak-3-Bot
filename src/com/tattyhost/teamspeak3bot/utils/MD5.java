package com.tattyhost.teamspeak3bot.utils;

public class MD5 {
    private static byte [] KEY = {'a', 'd', '4', '2', 'f', '6', '6', '9', '7', 'b', '0', '3', '5', 'b', '7', '5', '8', '0', 'e', '4', 'f', 'e', 'f', '9', '3', 'b', 'e', '2', '0', 'b', '4', 'd'};


    public static String getKey() {
        String ret = "";
        for(byte c: KEY){
            ret+= String.valueOf((char)c);
        }
        return ret;
    }

    public static String getRawKey() {
        return "debug";
    }
}
