/*
 * Copyright (C) 2013 Spencer Alderman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogue.startupviewer;

import com.rogue.startupviewer.metrics.Metrics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @since 1.0
 * @author 1Rogue
 * @version 1.0
 *
 */
public class StartupViewer extends JavaPlugin {

    /**
     * No use yet
     *
     * @since 1.0
     * @version 1.0
     */
    @Override
    public void onLoad() {
    }

    /**
     * Enables stuff
     *
     * @since 1.0
     * @version 1.0
     */
    @Override
    public void onEnable() {
        try {
            Metrics metrics = new Metrics(this);
            getLogger().info("Enabling Metrics...");
            metrics.start();
        } catch (IOException ex) {
            Logger.getLogger(StartupViewer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * No use yet.
     *
     * @since 1.0
     * @version 1.0
     */
    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "{0} is disabled!", this.getName());
    }

    /**
     * Made to print a report on the server info
     *
     * @since 1.0
     * @version 1.0
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("startup")) {
            if (args.length == 1) {
                RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
                List<String> arguments = RuntimemxBean.getInputArguments();
                StringBuilder sb = new StringBuilder("Arguments: ");
                for (String arg : arguments) {
                    sb.append(arg).append(" ");
                }
                if (args[0].equalsIgnoreCase("print") && sender.hasPermission("startupviewer.print")) {
                    DateFormat dateFormat = new SimpleDateFormat("MM-dd-YY--HH-mm-ss");
                    Date date = new Date();
                    String filename = this.getDataFolder() + File.separator + "startup--" + dateFormat.format(date) + ".txt";

                    if (!this.getDataFolder().exists()) {
                        this.getDataFolder().mkdir();
                    }

                    File file = new File(filename);

                    try {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(StartupViewer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        BufferedWriter out = new BufferedWriter(new FileWriter(filename));
                        out.write(sb.toString());
                        out.close();
                    } catch (IOException ex) {
                        Logger.getLogger(StartupViewer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("view") && sender.hasPermission("startupviewer.view")) {
                    sender.sendMessage(sb.toString());
                    return true;
                }
            } else if (args.length == 0) {
                sender.sendMessage(_("[&9StartupViewer&f] v&a" + this.getDescription().getVersion() + "&f, made by &e1Rogue"));
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Gets the instance of the plugin in its entirety.
     *
     * @since 1.0
     * @version 1.0
     *
     * @return The plugin instance
     */
    public static StartupViewer getPlugin() {
        return (StartupViewer) Bukkit.getServer().getPluginManager().getPlugin("StartupViewer");
    }

    /**
     * Converts pre-made strings to have chat colors in them
     *
     * @param encoded String with unconverted color codes
     * @return string with correct chat colors included
     */
    public static String _(String encoded) {
        return ChatColor.translateAlternateColorCodes('&', encoded);
    }
}
