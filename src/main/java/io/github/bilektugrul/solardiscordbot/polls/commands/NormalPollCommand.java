package io.github.bilektugrul.solardiscordbot.polls.commands;

import com.google.common.collect.Lists;
import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.polls.PollTracker;
import io.github.bilektugrul.solardiscordbot.polls.types.NormalPoll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class NormalPollCommand extends ListenerAdapter {

    private final JDA jda;
    private final PollTracker pollTracker;

    public NormalPollCommand(SolarDiscordBot plugin) {
        this.jda = plugin.getBot();
        this.pollTracker = plugin.getPollTracker();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String name = event.getName();

        if (name.equals("normalpoll")) {
            if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                event.reply("You can not use this command.").setEphemeral(true).queue();
                return;
            }

            String question = event.getOption("question", OptionMapping::getAsString);
            NormalPoll poll = new NormalPoll(jda, question,
                    event.getGuild().getIdLong(), event.getChannel().getIdLong(), -1, event.getMember().getIdLong(),
                    Lists.newArrayList(), Lists.newArrayList());
            EmbedBuilder embed = normalPollEmbed(poll);
            event.deferReply().queue();

            OptionMapping option = event.getOption("image");
            String image = option == null ? "" : option.getAsAttachment().getUrl();
            if (!image.isEmpty()) embed.setImage(image);

            event.getHook().sendMessageEmbeds(embed.build())
                    .addActionRow(Button.success("normal-yes", Emoji.fromUnicode("✅")), Button.success("normal-no", Emoji.fromUnicode("U+274C")))
                    .queue(msg -> {
                        poll.setMessage(msg.getIdLong());
                        pollTracker.addPoll(poll);
                    });
        }
    }

    private EmbedBuilder normalPollEmbed(NormalPoll poll) {
        return new EmbedBuilder()
                .setAuthor("Poll Created", "https://discord.com/", "https://media.discordapp.net/attachments/772045047130095616/1144561010808725644/png-clipart-emoji-sticker-red-blue-and-green-bar-graph-thumbnail.png")
                .setTitle("**" + poll.getQuestion() + "**")
                .setDescription("✅ `` " + poll.getYesVotes().size() + " votes `` ❌ `` " + poll.getNoVotes().size() + " votes ``")
                .setFooter(poll.getAuthor().getEffectiveName() + " asked.", poll.getAuthor().getEffectiveAvatarUrl());
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("normal-yes")) {
            NormalPoll poll = (NormalPoll) pollTracker.getPollByMessageID(event.getMessageIdLong());
            poll.yesVote(event.getMember());
            event.deferEdit().queue();
            event.getHook().editOriginalEmbeds(normalPollEmbed(poll).build()).queue();
        } else if (event.getComponentId().equals("normal-no")) {
            NormalPoll poll = (NormalPoll) pollTracker.getPollByMessageID(event.getMessageIdLong());
            poll.noVote(event.getMember());
            event.deferEdit().queue();
            event.getHook().editOriginalEmbeds(normalPollEmbed(poll).build()).queue();
        }
    }

}