package io.github.bilektugrul.solardiscordbot.users;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private final SolarDiscordBot plugin;
    private final Set<User> userList = new HashSet<>();

    public UserManager(SolarDiscordBot plugin) {
        this.plugin = plugin;
    }

    public User loadUser(Player p) {
        return loadUser(p.getName(), true);
    }

    public User loadUser(String name, boolean keep) {
        YamlConfiguration dataFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/players/" + name + ".yml"));
        User user = new User(dataFile, name);
        if (keep) userList.add(user);
        return user;
    }

    public User getUser(Player p) {
        String name = p.getName();
        return getUser(name);
    }

    public User getUser(String name) {
        for (User user : userList) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }

        return null;
    }

    public boolean isLoaded(String name) {
        return getUser(name) != null;
    }

    public void removeUser(User user) {
        userList.remove(user);
    }

    public Set<User> getUserList() {
        return new HashSet<>(userList);
    }

    public void saveUsers() throws IOException {
        for (User user : userList) {
            user.save();
        }
    }

}