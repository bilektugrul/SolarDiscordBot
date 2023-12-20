package io.github.bilektugrul.solardiscordbot.commands;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final SolarDiscordBot plugin;

    public ReloadCommand(SolarDiscordBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("discordbot.reload")) {
            plugin.reloadConfig();
            plugin.getCmdManager().loadCmds();
            if (strings.length >= 1) {
                plugin.loadBot();
                commandSender.sendMessage("Config, commands and the bot reloaded!");
            } else {
                commandSender.sendMessage("Config and commands reloaded!");
            }
        }
        return true;
    }

}