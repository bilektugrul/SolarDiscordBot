package io.github.bilektugrul.solardiscordbot.listener;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.linking.DiscordLinkManager;
import io.github.bilektugrul.solardiscordbot.users.User;
import io.github.bilektugrul.solardiscordbot.users.UserManager;
import io.github.bilektugrul.solardiscordbot.util.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.Locale;

public class BukkitListener implements Listener {

    private final SolarDiscordBot plugin;
    private final UserManager userManager;
    private final DiscordLinkManager discordLinkManager;

    public BukkitListener(SolarDiscordBot plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.discordLinkManager = plugin.getDiscordLinkManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        userManager.loadUser(player);

        if (!Utils.getString("presence").isEmpty()) {
            Activity.ActivityType type = Activity.ActivityType.valueOf(Utils.getString("presence-type").toUpperCase(Locale.ROOT));
            Activity activity = Activity.of(type, PlaceholderAPI.setPlaceholders(null, Utils.getString("presence")));
            plugin.getBot().getPresence().setPresence(activity, true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws IOException {
        Player player = e.getPlayer();
        User user = userManager.getUser(player);
        user.save();
        userManager.removeUser(user);

        if (discordLinkManager.isLinking(player)) {
            discordLinkManager.deleteCode(player);
        }

        if (!Utils.getString("presence").isEmpty()) {
            Activity.ActivityType type = Activity.ActivityType.valueOf(Utils.getString("presence-type").toUpperCase(Locale.ROOT));
            Activity activity = Activity.of(type, PlaceholderAPI.setPlaceholders(null, Utils.getString("presence")));
            plugin.getBot().getPresence().setPresence(activity, true);
        }
    }

}