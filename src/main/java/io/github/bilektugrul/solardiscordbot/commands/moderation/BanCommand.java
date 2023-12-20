package io.github.bilektugrul.solardiscordbot.commands.moderation;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.bans.BanManager;
import io.github.bilektugrul.solardiscordbot.util.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class BanCommand extends ListenerAdapter {

    private final BanManager banManager;

    public BanCommand(SolarDiscordBot plugin) {
        this.banManager = plugin.getBanManager();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String name = event.getName();

        if (name.equals("ban")) {
            if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
                event.reply("You can not use this command.").setEphemeral(true).queue();
                return;
            }

            Member member = event.getOption("user", OptionMapping::getAsMember);
            OptionMapping duration = event.getOption("timespan");
            OptionMapping reason = event.getOption("reason");
            String reasonStr = reason == null ? "" : reason.getAsString();

            event.deferReply().queue();

            if (duration != null) {
                String durString = duration.getAsString();

                long dur;
                String[] split = durString.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                int durOriginal = Integer.parseInt(split[0]);
                dur = durOriginal;
                String durName;

                if (durString.contains("s")) {
                    durName = "seconds";
                } else if (durString.contains("d")) {
                    dur = dur * 86400;
                    durName = "days";
                } else if (durString.contains("m")) {
                    dur = dur * 2629743;
                    durName = "months";
                } else if (durString.contains("w")) {
                    dur = dur * 604800;
                    durName = "weeks";
                } else if (durString.contains("y")) {
                    dur = dur * 31556926;
                    durName = "years";
                } else {
                    event.getHook().editOriginal("You have used the command incorrectly.").queue();
                    return;
                }

                //member.ban(0, TimeUnit.SECONDS).reason(reasonStr).queue();
                long durMS = dur * 1000;
                long unbanAt = System.currentTimeMillis() + durMS;
                banManager.ban(member.getUser(), unbanAt);
                event.getHook().editOriginal(member.getAsMention() + " has been banned for " + durOriginal + " " + durName + ".").queue();
                event.getChannel().sendMessage("They'll be unbanned at " + Utils.millisToString(unbanAt)).queue();
            } else {
                //member.ban(0, TimeUnit.DAYS).reason(reasonStr).queue();
                event.getHook().editOriginal(member.getAsMention() + " has been banned until eternity.").queue();
            }

        }
    }

}