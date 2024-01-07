package io.github.bilektugrul.solardiscordbot.util;

import io.github.bilektugrul.solardiscordbot.customcmd.Command;
import io.github.bilektugrul.solardiscordbot.linking.DiscordLinkManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.time.LocalDateTime;

public class DiscordUtils {

    public static MessageEmbed buildStatsEmbed(String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        String path = "stats-command.";

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.decode(Utils.getString(path + "color")));
        builder.setTimestamp(LocalDateTime.now());

        if (!Utils.getString(path + "title").isEmpty()) {
            builder.setTitle(PlaceholderAPI.setPlaceholders(player, Utils.getString(path + "title")));
        }

        if (!Utils.getString(path + "thumbnail").isEmpty()) {
            builder.setThumbnail(PlaceholderAPI.setPlaceholders(player, Utils.getString(path + "thumbnail")));
        }

        if (!Utils.getString(path + "description").isEmpty()) {
            builder.setDescription(PlaceholderAPI.setPlaceholders(player, Utils.getString(path + "description")));
        }

        if (!Utils.getString(path + "footer").isEmpty()) {
            builder.setFooter(PlaceholderAPI.setPlaceholders(player, Utils.getString(path + "footer")), PlaceholderAPI.setPlaceholders(player, Utils.getString(path + "footer-icon")));
        }

        for (String i : Utils.getConfig().getConfigurationSection(path + "fields").getKeys(false)) {
            path = "stats-command.fields." + i  + ".";

            String name = Utils.getString(path + "name");
            String value = Utils.getString(path + "value");

            builder.addField(PlaceholderAPI.setPlaceholders(player, name), PlaceholderAPI.setPlaceholders(player, value), false);
        }

        return builder.build();
    }

    public static MessageEmbed buildCustomEmbed(Command command) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(command.getColor());
        builder.setTimestamp(LocalDateTime.now());

        if (!command.getTitle().isEmpty()) {
            builder.setTitle(PlaceholderAPI.setPlaceholders(null, command.getTitle()));
        }

        if (!command.getThumbnail().isEmpty()) {
            builder.setThumbnail(PlaceholderAPI.setPlaceholders(null, command.getThumbnail()));
        }

        if (!command.getDescription().isEmpty()) {
            builder.setDescription(PlaceholderAPI.setPlaceholders(null, command.getDescription()));
        }

        if (!command.getFooter().isEmpty()) {
            builder.setFooter(PlaceholderAPI.setPlaceholders(null, command.getFooter()), PlaceholderAPI.setPlaceholders(null, command.getFooterIcon()));
        }

        for (MessageEmbed.Field field : command.getFields()) {
            builder.addField(PlaceholderAPI.setPlaceholders(null, field.getName()), PlaceholderAPI.setPlaceholders(null, field.getValue()), false);
        }

        return builder.build();
    }

    public static MessageEmbed buildInfoEmbed(DiscordLinkManager.LinkInfo info) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(info.getMcName());
        String path = "info-command.";

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.decode(Utils.getString(path + "color")));
        builder.setTimestamp(LocalDateTime.now());

        if (!Utils.getString(path + "title").isEmpty()) {
            builder.setTitle(PlaceholderAPI.setPlaceholders(player, Utils.getString(path + "title")));
        }

        if (!Utils.getString(path + "thumbnail").isEmpty()) {
            builder.setThumbnail(PlaceholderAPI.setPlaceholders(player, Utils.getString(path + "thumbnail")));
        }

        if (!Utils.getString(path + "description").isEmpty()) {
            builder.setDescription(PlaceholderAPI.setPlaceholders(player, Utils.getString(path + "description")));
        }

        if (!Utils.getString(path + "footer").isEmpty()) {
            builder.setFooter(PlaceholderAPI.setPlaceholders(player, Utils.getString(path + "footer")), PlaceholderAPI.setPlaceholders(player, Utils.getString(path + "footer-icon")));
        }

        for (String i : Utils.getConfig().getConfigurationSection(path + "fields").getKeys(false)) {
            path = "info-command.fields." + i  + ".";

            String name = Utils.getString(path + "name");
            String value = Utils.getString(path + "value")
                    .replace("%discordName%", info.getDiscordName())
                    .replace("%mcName%", info.getMcName())
                    .replace("%discordID%", String.valueOf(info.getDiscordID()));

            builder.addField(PlaceholderAPI.setPlaceholders(player, name), PlaceholderAPI.setPlaceholders(player, value), false);
        }

        return builder.build();
    }

}