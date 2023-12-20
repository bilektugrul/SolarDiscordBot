package io.github.bilektugrul.solardiscordbot.listener;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.linking.DiscordLinkManager;
import io.github.bilektugrul.solardiscordbot.util.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class DiscordMessageListener extends ListenerAdapter {

    private final SolarDiscordBot plugin;
    private final DiscordLinkManager discordLinkManager;

    public DiscordMessageListener(SolarDiscordBot plugin) {
        this.plugin = plugin;
        this.discordLinkManager = plugin.getDiscordLinkManager();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        long channelID = event.getChannel().getIdLong();
        if (channelID == Utils.getLong("discord-link-channel-id")) {
            if (event.getAuthor().getIdLong() == plugin.getBot().getSelfUser().getIdLong()) return;

            Message message = event.getMessage();
            message.delete().queue();
            String content = message.getContentRaw();

            if (!discordLinkManager.doesCodeExist(content)) return;

            Player owner = discordLinkManager.getCodeOwner(content);
            discordLinkManager.link(event.getChannel().asTextChannel(), event.getMember(), owner);
        }
    }

}