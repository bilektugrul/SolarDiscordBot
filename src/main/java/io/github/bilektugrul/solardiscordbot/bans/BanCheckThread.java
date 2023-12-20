package io.github.bilektugrul.solardiscordbot.bans;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class BanCheckThread extends BukkitRunnable {

    private final SolarDiscordBot plugin;
    private final BanManager banManager;
    private final Logger logger;

    public BanCheckThread(SolarDiscordBot plugin) {
        this.plugin = plugin;
        this.banManager = plugin.getBanManager();
        this.logger = plugin.getLogger();

        start();
    }

    public void start() {
        logger.info(ChatColor.GREEN + "Ban check saving thread is starting...");
        runTaskTimerAsynchronously(plugin, 1, 60L * 20);
    }

    @Override
    public void run() {
        Date now = new Date();

        List<BanData> usersToUnban = new ArrayList<>();
        for (BanData data : banManager.getBanDataSet()) {
            Date unbanDate = new Date(data.getUnbanMilliseconds());
            if (now.after(unbanDate)) {
                usersToUnban.add(data);
            }
        }

        usersToUnban.forEach(data -> {
            banManager.unban(data.getUserID());
            logger.info(data.getLastKnownUsername() + " is now unbanned.");
        });
    }

}