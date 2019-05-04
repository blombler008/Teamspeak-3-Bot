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

package com.github.blombler008.teamspeak3bot.config;

import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.plugins.JavaPlugin;
import com.github.blombler008.teamspeak3bot.utils.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

public class FileConfiguration {

    private InputStream inputStream;
    private File file;
    private boolean locked = false;
    private boolean finishedCopy = true;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public FileConfiguration(File file) {
        this.file = file;
        try {
            if (!file.isDirectory()) {
                if (!file.exists()) {
                    file.mkdirs();
                    if (file.isDirectory()) {
                        file.delete();
                    }
                    file.createNewFile();
                }
                return;
            }
            throw new FileNotFoundException(StringUtils.replaceStringWith("Failed to read file: %file%", "file", file.getAbsolutePath()));
        } catch (Throwable ignore) {
        }
    }

    public FileConfiguration(FileConfiguration config) {
        this.file = config.file;
        this.inputStream = config.inputStream;
        this.locked = config.locked;
        this.finishedCopy = config.finishedCopy;
    }

    public FileConfiguration(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public String toString() {
        return "FileConfiguration{" +
                "file=" + file +
                ", locked=" + locked +
                '}';
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public Reader getReader() throws FileNotFoundException {
        if (file == null) {
            return new InputStreamReader(inputStream);
        }
        return new FileReader(file);
    }

    public Writer getWriter() throws IOException {
        if (!locked) {
            if (file == null) {
                return null;
            }
            return new FileWriter(file);
        }
        return null;
    }

    public boolean copy() {
        if (!locked) {
            finishedCopy = false;
            try {
                BufferedWriter bf = new BufferedWriter(new FileWriter(file));
                JavaPlugin plugin = ConfigManager.find(this);
                InputStream stream;
                if(plugin != null) {
                    ClassLoader x = plugin.getClass().getClassLoader();
                    stream = x.getResourceAsStream(file.getName());
                } else {
                    stream = ClassLoader.getSystemResourceAsStream(file.getName());
                }

                Scanner scanner = new Scanner(stream);

                while (scanner.hasNextLine()) {
                    bf.write(scanner.nextLine());
                    bf.newLine();
                    bf.flush();
                }

                finishedCopy = true;
                return true;
            } catch (IOException e) {
                Teamspeak3Bot.getLogger().error(StringUtils.replaceStringWith("File not found: %file%", "file", file.getName()));
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public File getFile() {
        return file;
    }

    public void lock() {
        locked = true;
    }

    public boolean finishedCopy() {
        return finishedCopy;
    }

    public boolean isLocked() {
        return locked;
    }
}
