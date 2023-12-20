package io.github.bilektugrul.solardiscordbot.users;


import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class User {

    private static final SolarDiscordBot plugin = JavaPlugin.getPlugin(SolarDiscordBot.class);

    private final YamlConfiguration data;
    private final String name;
    private final UUID uuid;

    private long discordID = -1;
    private long givenRole = -1;

    public User(YamlConfiguration data, UUID uuid, String name) {
        this.data = data;
        this.uuid = uuid;
        this.name = name;

        if (data.isLong("discordID")) {
            this.discordID = data.getLong("discordID");
        }

        if (data.isLong("givenRoleID")) {
            this.givenRole = data.getLong("givenRoleID");
        }

        data.set("lastKnownName", name);
    }

    public long getDiscordID() {
        return discordID;
    }

    public void setDiscordID(long discordID) {
        this.discordID = discordID;
    }

    public long getGivenRole() {
        return givenRole;
    }

    public void setGivenRole(long givenRole) {
        this.givenRole = givenRole;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void save() throws IOException {
        data.set("discordID", discordID);
        data.set("givenRoleID", givenRole);

        data.save(new File(plugin.getDataFolder() + "/players/" + uuid + ".yml"));
    }

}