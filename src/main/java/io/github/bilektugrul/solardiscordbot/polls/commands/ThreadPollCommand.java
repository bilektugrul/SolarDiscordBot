package io.github.bilektugrul.solardiscordbot.polls.commands;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.polls.PollTracker;
import io.github.bilektugrul.solardiscordbot.polls.types.ThreadPoll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class ThreadPollCommand extends ListenerAdapter {

    private final JDA jda;
    private final PollTracker pollTracker;

    public ThreadPollCommand(SolarDiscordBot plugin) {
        this.jda = plugin.getBot();
        this.pollTracker = plugin.getPollTracker();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String name = event.getName();

        if (name.equals("threadpoll")) {
            if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                event.reply("You can not use this command.").setEphemeral(true).queue();
                return;
            }

            String question = event.getOption("question", OptionMapping::getAsString);
            ThreadPoll poll = new ThreadPoll(jda, question,
                    event.getGuild().getIdLong(), event.getChannel().getIdLong(), -1, event.getMember().getIdLong(), -1);

            event.deferReply().queue();
            EmbedBuilder embed = threadPollEmbed(poll);

            OptionMapping option = event.getOption("image");
            String image = option == null ? "" : option.getAsAttachment().getUrl();
            if (!image.isEmpty()) embed.setImage(image);

            event.getHook().sendMessageEmbeds(embed.build())
                    .queue(msg -> {
                        poll.setMessage(msg.getIdLong());
                        msg.createThreadChannel("What do you think?").queue(poll::setThread);
                        pollTracker.addPoll(poll);
                    });
        }
    }

    public static EmbedBuilder threadPollEmbed(ThreadPoll poll) {
        return new EmbedBuilder()
                .setAuthor("Thread Poll Created", "https://discord.com/", "https://media.discordapp.net/attachments/772045047130095616/1144561010808725644/png-clipart-emoji-sticker-red-blue-and-green-bar-graph-thumbnail.png")
                .setTitle("**" + poll.getQuestion() + "**")
                .setFooter(poll.getAuthor().getEffectiveName() + " asked.", poll.getAuthor().getEffectiveAvatarUrl());
    }

}