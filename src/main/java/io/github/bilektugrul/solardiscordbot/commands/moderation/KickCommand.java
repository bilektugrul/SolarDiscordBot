package io.github.bilektugrul.solardiscordbot.commands.moderation;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.bans.BanManager;
import io.github.bilektugrul.solardiscordbot.util.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class KickCommand extends ListenerAdapter {

    private final BanManager banManager;

    public KickCommand(SolarDiscordBot plugin) {
        this.banManager = plugin.getBanManager();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String name = event.getName();

        if (name.equals("kick")) {
            if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
                event.reply("You can not use this command.").setEphemeral(true).queue();
                return;
            }

            Member member = event.getOption("user", OptionMapping::getAsMember);
            OptionMapping reason = event.getOption("reason");
            String reasonStr = reason == null ? "" : reason.getAsString();

            event.deferReply().queue();
            member.kick().reason(reasonStr).queue();
            event.getHook().editOriginal(member.getAsMention() + " has been kicked because of \"" + reason + "\".").queue();

        }
    }

}