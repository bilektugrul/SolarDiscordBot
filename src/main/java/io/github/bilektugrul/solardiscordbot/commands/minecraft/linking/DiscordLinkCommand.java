package io.github.bilektugrul.solardiscordbot.commands.minecraft.linking;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.linking.DiscordLinkManager;
import io.github.bilektugrul.solardiscordbot.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordLinkCommand implements CommandExecutor {

    private final SolarDiscordBot plugin;
    private final DiscordLinkManager discordLinkManager;

    public DiscordLinkCommand(SolarDiscordBot plugin) {
        this.plugin = plugin;
        this.discordLinkManager = plugin.getDiscordLinkManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(Utils.getMessage("messages.command-message", player));
            return true;
        }

        if (args[0].equalsIgnoreCase("link")) {
            discordLinkManager.startLinkProcess(player);
        } else if (args[0].equalsIgnoreCase("unlink")) {
            discordLinkManager.unlink(player);
        } else if (args[0].equalsIgnoreCase("info")) {
            if (args.length == 1) {
                DiscordLinkManager.LinkInfo info = discordLinkManager.getInfo(player.getName());
                Utils.sendInfo(player.getName(), player, null, info);
                return true;
            }

            if (!player.hasPermission("solarbot.infoothers")) {
                return true;
            }

            String otherName = args[1];
            DiscordLinkManager.LinkInfo info = discordLinkManager.getInfo(otherName);
            Utils.sendInfo(args[1], player, null, info);
        }

        return true;
    }

}