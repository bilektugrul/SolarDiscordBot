package io.github.bilektugrul.solardiscordbot.commands.discord;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.customcmd.CmdManager;
import io.github.bilektugrul.solardiscordbot.customcmd.Command;
import io.github.bilektugrul.solardiscordbot.linking.DiscordLinkManager;
import io.github.bilektugrul.solardiscordbot.util.DiscordUtils;
import io.github.bilektugrul.solardiscordbot.util.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.io.File;
import java.io.IOException;

public class DiscordCommands extends ListenerAdapter {

    private final SolarDiscordBot plugin;
    private final CmdManager cmdManager;
    private final DiscordLinkManager discordLinkManager;

    public DiscordCommands(SolarDiscordBot plugin) {
        this.plugin = plugin;
        this.cmdManager = plugin.getCmdManager();
        this.discordLinkManager = plugin.getDiscordLinkManager();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String name = event.getName();

        if (name.equals("broadcast")) {
            if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                event.reply("You can not use this command.").setEphemeral(true).queue();
                return;
            }

            String message = event.getOption("message", OptionMapping::getAsString);

            // Before broadcasting our message, tell the user we received the command
            // This sends a "Bot is thinking..." message which is later edited once we finished

            event.deferReply().queue();
            plugin.getServer().broadcastMessage(Utils.colored(Utils.getString("broadcast-prefix") + Utils.colored(PlaceholderAPI.setPlaceholders(null, message))));
            event.getHook().editOriginal("Message sent!").queue();

        } else if (name.equals("dcbroadcast")) {
            if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                event.reply("You can not use this command.").setEphemeral(true).queue();
                return;
            }

            GuildChannelUnion channel = event.getOption("channel", OptionMapping::getAsChannel);
            if (channel != null && channel.getType().equals(ChannelType.TEXT)) {
                Message.Attachment attachment = event.getOption("message", OptionMapping::getAsAttachment);

                event.deferReply().queue();

                attachment.getProxy().downloadToFile(new File(plugin.getServer().getWorldContainer() + "botdownload-" + System.currentTimeMillis())).thenAccept(f -> {
                    try {
                        channel.asTextChannel().sendMessage(Utils.fileToString(f)).queue();
                        event.getHook().editOriginal("Message sent!").queue();
                    } catch (IOException e) {
                        event.getHook().editOriginal("Something went wrong! Check server console.").queue();
                        throw new RuntimeException(e);
                    }
                });

            } else {
                event.getHook().editOriginal("Please type a text channel.").queue();
            }

        } else if (name.equals("stats")) {
            String playerName = event.getOption("player", OptionMapping::getAsString);

            MessageEmbed embed = DiscordUtils.buildStatsEmbed(playerName);
            event.deferReply().queue();

            event.getHook().editOriginal(MessageEditData.fromEmbeds(embed)).queue();
        } else if (name.equals("linkinfo")) {
            if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
                event.reply("You can not use this command.").setEphemeral(true).queue();
                return;
            }

            String player = event.getOption("player", OptionMapping::getAsString);

            event.deferReply().queue();
            DiscordLinkManager.LinkInfo info = discordLinkManager.getInfo(player);
            event.getHook().editOriginal(MessageEditData.fromEmbeds(DiscordUtils.buildInfoEmbed(info))).queue();
        } else {
            Command cmd = cmdManager.getCmd(name);
            if (cmd == null) return;

            MessageEmbed embed = cmd.getEmbed();
            event.deferReply().queue();

            event.getHook().editOriginal(MessageEditData.fromEmbeds(embed)).queue();
        }

    }

}