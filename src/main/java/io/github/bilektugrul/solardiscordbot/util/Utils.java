package io.github.bilektugrul.solardiscordbot.util;


import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.linking.DiscordLinkManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

    private static final SolarDiscordBot plugin = JavaPlugin.getPlugin(SolarDiscordBot.class);
    public static final DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

    public static FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public static int getInt(String path) {
        return plugin.getConfig().getInt(path);
    }

    public static long getLong(String path) {
        return plugin.getConfig().getLong(path);
    }

    public static String getString(String string) {
        return plugin.getConfig().getString(string);
    }

    public static Boolean getBoolean(String string) {
        return plugin.getConfig().getBoolean(string);
    }

    public static List<String> getStringList(String string) {
        return plugin.getConfig().getStringList(string);
    }

    public static String getMessage(String msg, Player player) {
        String message = listToString(colored(getStringList(msg)));
        if (player != null) {
            message = message.replace("%player%", player.getName());
        }

        return message;
    }

    public static String colored(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> colored(List<String> strings) {
        List<String> list = new ArrayList<>();
        for (String str : strings) {
            list.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        return list;
    }

    public static String arrayToString(String[] array) {
        return String.join(" ", array);
    }

    public static String listToString(List<String> list) {
        return String.join("\n", list);
    }

    public static String fileToString(File file) throws IOException {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<String> content = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null)
            content.add(line);

        return listToString(content);
    }

    public static String millisToString(long millis) {
        Date date = new Date(millis);
        return dateFormat.format(date);
    }

    public static void sendInfo(String arg, Player player, TextChannel channel, DiscordLinkManager.LinkInfo info) {
        if (info == null) {
            if (channel == null) {
                player.sendMessage(getMessage("messages.not-present", player).replace("%request%", arg));
            } else {
                channel.sendMessage(getMessage("messages.not-present", player).replace("%request%", arg)).queue();
            }

            return;
        }

        String message = channel == null ? getMessage("messages.info", player) : getMessage("messages.info-discord", player);
        message = message.replace("%request%", arg)
                .replace("%discordName%", info.getDiscordName())
                .replace("%mcName%", info.getMcName())
                .replace("%discordID%", String.valueOf(info.getDiscordID()));
        if (channel == null) {
            player.sendMessage(message);
        } else {
            channel.sendMessageEmbeds(DiscordUtils.buildInfoEmbed(info)).queue();
        }
    }
}