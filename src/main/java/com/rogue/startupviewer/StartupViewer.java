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
 * Main class for StartupViewer
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class StartupViewer extends JavaPlugin {

    /**
     * Enables metrics
     *
     * @since 1.0.0
     * @version 1.0.0
     */
    @Override
    public void onEnable() {
        try {
            Metrics metrics = new Metrics(this);
            getLogger().info("Enabling Metrics...");
            metrics.start();
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Error starting metrics!", ex);
        }

    }

    /**
     * Made to print a report on the server info
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param sender The command executor
     * @param cmd The command being executed
     * @param label The actual command used
     * @param args Command arguments
     * @return true when handled correctly
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("startup")) {
            if (args.length == 1) {
                RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
                List<String> arguments = RuntimemxBean.getInputArguments();
                StringBuilder sb = new StringBuilder("Arguments: ");
                for (String arg : arguments) {
                    sb.append("&9").append(arg).append("&f, ");
                }
                String out = __(sb.substring(0, sb.length() - 2));
                if (args[0].equalsIgnoreCase("print") && sender.hasPermission("startupviewer.print")) {
                    return this.printReport(sender, this.stripColor(out));
                } else if (args[0].equalsIgnoreCase("view") && sender.hasPermission("startupviewer.view")) {
                    sender.sendMessage(out);
                    return true;
                } else if (args[0].equalsIgnoreCase("view") || args[0].equalsIgnoreCase("print")) {
                    this.communicate(sender, "&cYou do not have permission for that!");
                }
            }
            this.communicate(sender, "v&a" + this.getDescription().getVersion() + "&f, made by &91Rogue");
            sender.sendMessage(__("Commands: &9view&f, &9print"));
            return true;
        }
        return false;
    }

    /**
     * Converts pre-made strings to have chat colors in them
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param encoded String with unconverted color codes
     * @return string with correct chat colors included
     */
    public String __(String encoded) {
        return ChatColor.translateAlternateColorCodes('&', encoded);
    }

    /**
     * Strips color from an encoded string
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param encoded
     * @return
     */
    private String stripColor(String encoded) {
        System.out.println("Stripcolor before = " + encoded);
        String back = ChatColor.stripColor(encoded);
        System.out.println("Stripcolor after = " + back);
        return back;
    }

    /**
     * Sends a message in the context of the plugin
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param target The target to send to
     * @param send The message to send
     */
    private void communicate(CommandSender target, String send) {
        target.sendMessage(__("[&9StartupViewer&f] " + send));
    }

    /**
     * Prints the command-line arguments to a file
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param sender The command sender (used for messages)
     * @param write The report to write
     * @return True if written to file, false otherwise
     */
    public boolean printReport(CommandSender sender, String write) {
        boolean success = true;

        System.out.println("write = " + write);
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-YY--HH-mm-ss");
        Date date = new Date();
        String filename = "startup--" + dateFormat.format(date) + ".txt";

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        File file = new File(this.getDataFolder(), filename);

        BufferedWriter out = null;
        FileWriter fw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            out = new BufferedWriter(fw);
            out.write(write);
        } catch (IOException ex) {
            this.communicate(sender, "&cError writing arguments to file!");
            this.getLogger().log(Level.SEVERE, "Error writing arguments to file!", ex);
            success = false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                this.communicate(sender, "&cError closing file streams!");
                this.getLogger().log(Level.SEVERE, "Error closing file streams!", ex);
            }
        }

        return success;
    }
}
