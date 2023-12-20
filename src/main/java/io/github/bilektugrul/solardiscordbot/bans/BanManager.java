package io.github.bilektugrul.solardiscordbot.bans;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import me.despical.commons.configuration.ConfigUtils;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class BanManager {

    private final SolarDiscordBot plugin;
    private final Set<BanData> banDataSet = new HashSet<>();

    public BanManager(SolarDiscordBot plugin) {
        this.plugin = plugin;
        this.loadBans();
    }

    public void loadBans() {
        banDataSet.clear();

        FileConfiguration bansFile = ConfigUtils.getConfig(plugin, "bans");;
        if (!bansFile.isConfigurationSection("bans")) return;

        for (String section : bansFile.getConfigurationSection("bans").getKeys(false)) {
            long userID = Long.parseLong(section);
            String path = "bans." + section + ".";

            String lastKnownUsername = bansFile.getString(path + "username");
            long banMs = bansFile.getLong(path + "ban-ms");
            long unbanMs = bansFile.getLong(path + "unban-ms");

            BanData banData = new BanData(userID, lastKnownUsername, banMs, unbanMs);
            banDataSet.add(banData);
        }
    }

    public void saveBans() {
        FileConfiguration bansFile = ConfigUtils.getConfig(plugin, "bans");;
        bansFile.set("bans", null);

        for (BanData banData : banDataSet) {
            long userID = banData.getUserID();
            String path = "bans." + userID + ".";

            String lastKnownUsername = banData.getLastKnownUsername();
            long banMs = banData.getBanMilliseconds();
            long unbanMs = banData.getUnbanMilliseconds();

            bansFile.set(path + "username", lastKnownUsername);
            bansFile.set(path + "ban-ms", banMs);
            bansFile.set(path + "unban-ms", unbanMs);
        }
        ConfigUtils.saveConfig(plugin, bansFile, "bans");
    }

    public void ban(User user, long banUntil) {
        long userID = user.getIdLong();
        long ms = System.currentTimeMillis();
        String username = user.getName();

        banDataSet.add(new BanData(userID, username, ms, banUntil));
        saveBans();
    }

    public void unban(long userID) {
        banDataSet.removeIf(data -> data.getUserID() == userID);
        saveBans();
    }

    public Set<BanData> getBanDataSet() {
        return banDataSet;
    }

}