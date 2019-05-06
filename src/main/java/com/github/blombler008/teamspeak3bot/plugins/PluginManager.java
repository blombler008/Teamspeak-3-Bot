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

package com.github.blombler008.teamspeak3bot.plugins;

import com.github.blombler008.teamspeak3bot.Teamspeak3Bot;
import com.github.blombler008.teamspeak3bot.commands.CommandTemplate;
import com.github.blombler008.teamspeak3bot.config.ConfigManager;
import com.github.blombler008.teamspeak3bot.config.FileConfiguration;
import com.github.blombler008.teamspeak3bot.config.YamlConfiguration;
import com.github.blombler008.teamspeak3bot.utils.Language;
import com.github.blombler008.teamspeak3bot.utils.Validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PluginManager {

    private File workDir;
    private File pluginsDir;
    private File[] pluginFiles;
    private List<JavaPlugin> plugins = new ArrayList<>();
    private boolean finished = false;
    private Map<JavaPlugin, Boolean> pluginStates = new HashMap<>();
    private Map<JavaPlugin, File> pluginFileMap = new HashMap<>();
    private Teamspeak3Bot instance;

    public PluginManager(File workDir, Teamspeak3Bot instance) {
        this.workDir = workDir;
        this.instance = instance;
        this.pluginsDir = new File(workDir, "plugins");
        //noinspection ResultOfMethodCallIgnored
        pluginsDir.mkdirs();
    }

    @SuppressWarnings("unchecked")
    public boolean prepare(File file, boolean debug) {
        try {
            JarFile jFile = new JarFile(file);
            ZipEntry zEntry = jFile.getEntry("plugin.ini");

            if (Validator.notNull(zEntry)) {
                Teamspeak3Bot.getLogger().error(Language.PLUGIN + "File: plugin.ini not found!");
                return false;
            }

            YamlConfiguration configuration = new YamlConfiguration(new FileConfiguration(jFile.getInputStream(zEntry)));
            String name = configuration.getString("name");
            String version = configuration.getString("version");
            String description = configuration.getString("description");
            String clazz = configuration.getString("main");

            for (String command : configuration.getCommandList()) {
                List<String> aliases = configuration.getList("aliases");
                if (!aliases.contains(command)) {
                    aliases.add(command);
                }
                CommandTemplate cmdTemp = new CommandTemplate(instance, aliases.toArray(new String[]{}), configuration.getString("description"), command, name, configuration.getString("usage"));
                instance.getCommandManager().registerNewCommand(cmdTemp);
            }

            instance.debug(Language.PLUGIN, "Properties of plugin \'" + name + "\' > " + configuration.get("*"));

            if (!Validator.notNull(name) && !Validator.notNull(version) && !Validator.notNull(description) && !Validator.notNull(clazz)) {

                PluginDescription pluginDescription = new PluginDescription(version, description, name);

                URL url = new URL("jar:file:" + file.getAbsolutePath() + "!/");
                URLClassLoader cl = URLClassLoader.newInstance(new URL[]{url});
                Class<JavaPlugin> pluginClass = (Class<JavaPlugin>) cl.loadClass(clazz);

                instance.debug(Language.PLUGIN, url.getFile());

                JavaPlugin plugin = pluginClass.newInstance();
                plugin.pluginDescription = pluginDescription;
                plugin.configuration = configuration;
                plugin.instance = instance;
                plugins.add(plugin);

                if (debug) instance.debug(Language.PLUGIN, "Plugin \'" + name + "\' added > " + plugin.toString());
                pluginFileMap.put(plugin, file);
            }

        } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
            Teamspeak3Bot.getLogger().error(Language.PLUGIN + "plugin.ini in " + file.getName() + " is not valid!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean prepare(boolean debug) {
        pluginFileMap.clear();
        plugins.clear();
        pluginStates.clear();
        pluginFiles = pluginsDir.listFiles((dir, name) -> name.endsWith(".jar"));
        finished = false;
        if (pluginFiles.length != 0) {
            for (File file : pluginFiles) {
                prepare(file, debug);
            }
            finished = true;
        }

        return true;
    }

    public void reloadPlugins() {
        reloadPlugins(false);
    }

    public void loadPlugins() {
        loadPlugins(false);
    }

    public void enablePlugins() {
        enablePlugins(false);
    }

    public void disablePlugins() {
        disablePlugins(false);
    }

    public void reloadPlugins(boolean debug) {
        List<JavaPlugin> cpi = new ArrayList<>(plugins);
        for (JavaPlugin p : cpi) {
            reloadPlugin(p, debug);
        }
    }

    public void loadPlugins(boolean debug) {
        for (JavaPlugin p : plugins) {
            loadPlugin(p, debug);
            pluginStates.remove(p);
        }
    }

    public void enablePlugins(boolean debug) {
        for (JavaPlugin p : plugins) {
            enablePlugin(p, debug);
            pluginStates.put(p, true);
        }
    }

    public void disablePlugins(boolean debug) {
        for (JavaPlugin p : plugins) {
            disablePlugin(p, debug);
            pluginStates.put(p, false);
        }
    }

    public boolean reloadPlugin(JavaPlugin p) {
        return reloadPlugin(p, false);
    }

    public boolean loadPlugin(JavaPlugin p) {
        return loadPlugin(p, false);
    }

    public boolean enablePlugin(JavaPlugin p) {
        return enablePlugin(p, false);
    }

    public boolean disablePlugin(JavaPlugin p) {
        return disablePlugin(p, false);
    }

    public boolean reloadPlugin(JavaPlugin p, boolean debug) {
        Teamspeak3Bot.getLogger().info("----------------------------------------------------------------------");
        if (debug)
            instance.debug(Language.PLUGIN, "Reloading plugin  > " + p.getName());
        Teamspeak3Bot.getLogger().info("Reloading plugin > [v" + p.getVersion() + ", " + p.getName() + "]");
        try {
            disablePlugin(p);
            plugins.remove(p);
            pluginStates.remove(p);
            prepare(pluginFileMap.get(p), debug);
            loadPlugin(p);
            enablePlugin(p);
            if (debug)
                instance.debug(Language.PLUGIN, "Plugin reloaded  > " + p.getName());
            return true;
        } catch (Exception e) {
            Teamspeak3Bot.getLogger().error("Error occurred while reloading > " + p.getClass().getSimpleName());
            e.printStackTrace();
        }
        return false;
    }

    public boolean loadPlugin(JavaPlugin p, boolean debug) {
        if (debug)
            instance.debug(Language.PLUGIN, "Loading plugin > " + p.getName());
        Teamspeak3Bot.getLogger().info("Loading plugin > [v" + p.getVersion() + ", " + p.getName() + "]");
        try {
            p.onLoad();
            File dataF = (p.dataFolder = new File(pluginsDir, p.getName()));
            dataF.mkdir();
            p.configmanager = new ConfigManager(ConfigManager.add(dataF, p),p);
            if (debug)
                instance.debug(Language.PLUGIN, "Plugin loaded > " + p.getName());
            return true;
        } catch (Exception e) {
            Teamspeak3Bot.getLogger().error("Error occurred while loading > " + p.getClass().getSimpleName());
            e.printStackTrace();
        }
        return false;
    }

    public boolean enablePlugin(JavaPlugin p, boolean debug) {
        if (debug)
            instance.debug(Language.PLUGIN, "Enabling plugin > " + p.getName());
        Teamspeak3Bot.getLogger()
                .info("Enabling plugin > [v" + p.getVersion() + ", " + p.getName() + "]");
        try {
            p.setEnabled();
            if (debug)
                instance.debug(Language.PLUGIN, "Plugin enabled > " + p.getName());
            return true;
        } catch (Exception e) {
            Teamspeak3Bot.getLogger()
                    .error("Error occurred while enabling > " + p.getClass().getSimpleName());
            e.printStackTrace();
        }
        return false;
    }

    public boolean disablePlugin(JavaPlugin p, boolean debug) {
        if (debug)
            instance.debug(Language.PLUGIN, "Disabling plugin > " + p.getName());
        Teamspeak3Bot.getLogger()
                .info("Disabling plugin > [v" + p.getVersion() + ", " + p.getName() + "]");
        try {
            p.setDisabled();
            if (debug)
                instance.debug(Language.PLUGIN, "Plugin disabled > " + p.getName());
            return true;
        } catch (Exception e) {
            Teamspeak3Bot.getLogger()
                    .error("Error occurred while disabling > " + p.getClass().getSimpleName());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void finalize() throws Throwable {

        disablePlugins();

        super.finalize();
    }

    public List<JavaPlugin> getPlugins() {
        return plugins;
    }


    public void isFinished(Callback c) {
        new Thread(() -> {
            while (true) {
                if (plugins.size() == pluginStates.size()
                        && instance.getConsoleManager().getReader() != null && finished) {
                    instance.info("Done! For help use \"help\" or \"?\".");
                    c.call();
                    break;
                }
            }
        }, "Waiter").start();
    }

    public JavaPlugin find(String arg) {
        for (JavaPlugin pl : plugins) {
            if (pl.getName().equals(arg)) {
                return pl;
            }
        }
        return null;
    }

    public interface Callback {
        void call();
    }
}
