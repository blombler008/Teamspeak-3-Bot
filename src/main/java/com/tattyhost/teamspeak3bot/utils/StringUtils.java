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

package com.tattyhost.teamspeak3bot.utils;

import java.util.Properties;

public class StringUtils {
    public static String getValue(String entry) {
        if (entry.startsWith("--")) {
            return entry.split("=")[1];
        }
        return null;
    }

    public static String getKey(String entry) {
        if (entry.startsWith("--")) {
            return entry.split("=")[0];
        }
        return null;
    }

    public static String getValueOf(String[] args, String key) {
        for (String entry : args) {
            if (entry.contains("--" + key)) {
                return getValue(entry);
            }
        }
        return null;
    }

    public static boolean hasKey(String entry, String key) {
        return getKey(entry).equals("--" + key);
    }

    public static boolean hasKey(String[] args, String key) {
        for (String entry : args) {
            if (hasKey(entry, key))
                return true;
        }
        return false;
    }

    public static boolean hasValue(String[] args, String key, String value) {
        for (String entry : args) {
            if (hasKey(entry, key))
                return hasValue(entry, key, value);
        }
        return false;
    }

    public static boolean hasValue(String entry, String key, String value) {


        if (hasKey(entry, key))
            return getValue(entry).equals(value);
        return false;
    }

    public static Properties getProperties(String[] args) {
        Properties pro = new Properties();
        for (String arg : args) {
            pro.put(getKey(arg), getValue(arg));
        }
        return pro;
    }
}
