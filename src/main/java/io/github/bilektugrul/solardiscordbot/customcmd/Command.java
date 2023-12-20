package io.github.bilektugrul.solardiscordbot.customcmd;

import io.github.bilektugrul.solardiscordbot.util.DiscordUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Command {

    private String name, title, thumbnail, commandDescription, description, footer, footerIcon, sentMessage;
    private Color color;

    private List<MessageEmbed.Field> fields = new ArrayList<>();

    public String getName() {
        return name;
    }

    public Command setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Command setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Command setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    public Command setCommandDescription(String commandDescription) {
        this.commandDescription = commandDescription;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Command setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getFooter() {
        return footer;
    }

    public Command setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public String getFooterIcon() {
        return footerIcon;
    }

    public Command setFooterIcon(String footerIcon) {
        this.footerIcon = footerIcon;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public Command setColor(Color color) {
        this.color = color;
        return this;
    }

    public List<MessageEmbed.Field> getFields() {
        return fields;
    }

    public Command setFields(List<MessageEmbed.Field> fields) {
        this.fields = fields;
        return this;
    }

    public String getSentMessage() {
        return sentMessage;
    }

    public Command setSentMessage(String sentMessage) {
        this.sentMessage = sentMessage;
        return this;
    }

    public MessageEmbed getEmbed() {
        return DiscordUtils.buildCustomEmbed(this);
    }

}