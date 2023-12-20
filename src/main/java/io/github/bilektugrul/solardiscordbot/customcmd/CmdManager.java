package io.github.bilektugrul.solardiscordbot.customcmd;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import me.despical.commons.configuration.ConfigUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmdManager {

    private final SolarDiscordBot plugin;
    private final Set<Command> cmds = new HashSet<>();

    public CmdManager(SolarDiscordBot plugin) {
        this.plugin = plugin;
        loadCmds();
    }

    public void loadCmds() {
        cmds.clear();

        String s = File.separator;
        File cmdFiles = new File("plugins" + s + plugin.getDescription().getName() + s + "customcmds" + s);
        if (cmdFiles.listFiles() == null) {
            ConfigUtils.getConfig(plugin, "customcmds/server");
        }

        cmdFiles = new File("plugins" + s + plugin.getDescription().getName() + s + "customcmds" + s);
        for (File cmdFile : cmdFiles.listFiles()) {
            loadCmd(YamlConfiguration.loadConfiguration(cmdFile));
        }
    }

    public void loadCmd(FileConfiguration file) {
        String path = "data.";

        String name = file.getString(path + "name");
        Color color = Color.decode(file.getString(path + "color"));
        String title = file.getString(path + "title");
        String thumbnail = file.getString(path + "thumbnail");
        String commandDescription = file.getString(path + "command-description");
        String description = file.getString(path + "description");
        String footer = file.getString(path + "footer");
        String footerIcon = file.getString(path + "footer-icon");
        String sentMessage = file.getString(path + "sent-message");

        List<MessageEmbed.Field> fields = new ArrayList<>();

        for (String id : file.getConfigurationSection(path + "fields").getKeys(false)) {
            String fieldPath = path + "fields." + id + ".";
            String fieldName = file.getString(fieldPath + "name");
            String fieldValue = file.getString(fieldPath + "value");

            fields.add(new MessageEmbed.Field(fieldName, fieldValue, false));
        }

        Command command = new Command().setName(name)
                .setColor(color)
                .setTitle(title)
                .setThumbnail(thumbnail)
                .setCommandDescription(commandDescription)
                .setDescription(description)
                .setFooter(footer)
                .setFooterIcon(footerIcon)
                .setFields(fields)
                .setSentMessage(sentMessage);
        cmds.add(command);
    }

    public Command getCmd(String name) {
        for (Command cmd : cmds) {
            if (cmd.getName().equalsIgnoreCase(name)) {
                return cmd;
            }
        }

        return null;
    }

    public Set<Command> getCmds() {
        return cmds;
    }

}