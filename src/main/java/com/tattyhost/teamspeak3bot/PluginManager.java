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

package com.tattyhost.teamspeak3bot;

import com.tattyhost.teamspeak3bot.utils.Language;
import com.tattyhost.teamspeak3bot.utils.PluginDescription;
import com.tattyhost.teamspeak3bot.utils.Validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PluginManager {

    private File workDir;
    private File pluginsDir;
    private File[] pluginFiles;
    private List<JavaPlugin> plugins = new ArrayList<>();

    public PluginManager(File workDir) {
        this.workDir = workDir;
        this.pluginsDir = new File(workDir, "plugins");
        //noinspection ResultOfMethodCallIgnored
        pluginsDir.mkdirs();
        this.pluginFiles = pluginsDir.listFiles((dir, name) -> name.endsWith(".jar"));


    }

    public boolean prepare(boolean debug) {
        if (pluginFiles.length != 0) {
            for (File file : pluginFiles) {
                try {
                    JarFile jFile = new JarFile(file);
                    ZipEntry zEntry = jFile.getEntry("plugin.ini");
                    if (Validator.notNull(zEntry)) {
                        Teamspeak3Bot.getLogger()
                            .error(Language.PLUGIN + "File: plugin.ini not found!");
                        return false;
                    }
                    Properties properties = new Properties();
                    properties.load(new InputStreamReader(jFile.getInputStream(zEntry)));

                    String name =
                        (properties.containsKey("name") ? properties.getProperty("name") : null);
                    String version = (properties.containsKey("version") ?
                        properties.getProperty("version") :
                        null);
                    String description = (properties.containsKey("description") ?
                        properties.getProperty("description") :
                        null);
                    String mainClass =
                        (properties.containsKey("main") ? properties.getProperty("main") : null);
                    properties.remove("main");

                    if (debug)
                        Teamspeak3Bot.debug(
                            Language.PLUGIN, "Properties of plugin \'" + name + "\' > "
                                + properties.toString());


                    if (!Validator.notNull(name) && !Validator.notNull(version) && !Validator
                        .notNull(description) && !Validator.notNull(mainClass)) {

                        PluginDescription pluginDescription =
                            new PluginDescription(version, description, name);

                        URL[] urls = {new URL("jar:file:" + file.getAbsolutePath() + "!/")};

                        Teamspeak3Bot.debug(Language.PLUGIN, urls[0].getFile());

                        URLClassLoader cl = URLClassLoader.newInstance(urls);
                        //noinspection SingleStatementInBlock, unchecked
                        Class<JavaPlugin> pluginClass = (Class<JavaPlugin>) cl.loadClass(mainClass);

                        Constructor<JavaPlugin> constructor =
                            (Constructor<JavaPlugin>) pluginClass.getSuperclass()
                                .getConstructor(PluginDescription.class, Properties.class);
                        // getConstructor(Properties.class, PluginDescription.class);
                        JavaPlugin plugin = constructor.newInstance(pluginDescription, properties);

                        plugins.add(plugin);
                        if (debug)
                            Teamspeak3Bot.debug(
                                Language.PLUGIN, "Plugin \'" + name + "\' added > " + plugin
                                    .toString());
                    }

                } catch (ClassNotFoundException | NoSuchMethodException | IOException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    Teamspeak3Bot.getLogger().error(
                        Language.PLUGIN + "plugin.ini in " + file.getName() + " is not valid!");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    public boolean loadPlugins(boolean debug) {
        for (JavaPlugin p : plugins) {
            if (debug)
                Teamspeak3Bot.debug(Language.PLUGIN, "Loading plugin > " + p.getName());
            Teamspeak3Bot.getLogger()
                .info("Loading plugin > [v" + p.getVersion() + ", " + p.getName() + "]");
            try {
                p.onLoad();
                File dataF = p.setDataFolder(new File(pluginsDir, p.getName()));
                dataF.mkdir();
                ConfigManager.add(dataF, p);
                if (debug)
                    Teamspeak3Bot.debug(Language.PLUGIN, "Plugin loaded > " + p.getName());
            } catch (Exception e) {
                Teamspeak3Bot.getLogger()
                    .error("Error occurred while loading > " + p.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean enablePlugins(boolean debug) {
        for (JavaPlugin p : plugins) {
            if (debug)
                Teamspeak3Bot.debug(Language.PLUGIN, "Enabling plugin > " + p.getName());
            Teamspeak3Bot.getLogger()
                .info("Enabling plugin > [v" + p.getVersion() + ", " + p.getName() + "]");
            try {
                p.setEnabled();
                if (debug)
                    Teamspeak3Bot.debug(Language.PLUGIN, "Plugin enabled > " + p.getName());
            } catch (Exception e) {
                Teamspeak3Bot.getLogger()
                    .error("Error occurred while enabling > " + p.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean disablePlugins(boolean debug) {
        for (JavaPlugin p : plugins) {
            if (debug)
                Teamspeak3Bot.debug(Language.PLUGIN, "Disabling plugin > " + p.getName());
            Teamspeak3Bot.getLogger()
                .info("Disabling plugin > [v" + p.getVersion() + ", " + p.getName() + "]");
            try {
                p.setDisabled();
                if (debug)
                    Teamspeak3Bot.debug(Language.PLUGIN, "Plugin disabled > " + p.getName());
            } catch (Exception e) {
                Teamspeak3Bot.getLogger()
                    .error("Error occurred while disabling > " + p.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean reloadPlugins(boolean debug) {
        for (JavaPlugin p : plugins) {
            if (debug)
                Teamspeak3Bot.debug(Language.PLUGIN, "Reloading plugin  > " + p.getName());
            Teamspeak3Bot.getLogger()
                .info("Reloading plugin > [v" + p.getVersion() + ", " + p.getName() + "]");
            try {
                disablePlugins();
                loadPlugins();
                enablePlugins();
                if (debug)
                    Teamspeak3Bot.debug(Language.PLUGIN, "Plugin reloaded  > " + p.getName());
            } catch (Exception e) {
                Teamspeak3Bot.getLogger()
                    .error("Error occurred while reloading > " + p.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        return true;
    }



    public boolean loadPlugins() {
        return loadPlugins(false);
    }


    public boolean disablePlugins() {
        return disablePlugins(false);
    }


    public boolean enablePlugins() {
        return enablePlugins(false);
    }

    public boolean reloadPlugins() {
        return reloadPlugins(false);
    }



    @Override protected void finalize() throws Throwable {

        disablePlugins();

        super.finalize();
    }

    public List<JavaPlugin> getPlugins() {
        return plugins;
    }
}
