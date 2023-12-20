package io.github.bilektugrul.solardiscordbot.polls.commands;

import com.google.common.collect.Lists;
import com.vdurmont.emoji.EmojiParser;
import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.polls.PollOption;
import io.github.bilektugrul.solardiscordbot.polls.PollTracker;
import io.github.bilektugrul.solardiscordbot.polls.types.StrawPoll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.List;

public class StrawPollCommand extends ListenerAdapter {

    private static final String[] optionEmotes = {":regional_indicator_a:", ":regional_indicator_b:", ":regional_indicator_c:",
            ":regional_indicator_d:", ":regional_indicator_e:", ":regional_indicator_f:",
            ":regional_indicator_g:", ":regional_indicator_h:", ":regional_indicator_i:",
            ":regional_indicator_j:"};

    private final JDA jda;
    private final PollTracker pollTracker;

    public StrawPollCommand(SolarDiscordBot plugin) {
        this.jda = plugin.getBot();
        this.pollTracker = plugin.getPollTracker();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String name = event.getName();

        if (name.equals("strawpoll")) {
            if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                event.reply("You can not use this command.").setEphemeral(true).queue();
                return;
            }

            String question = event.getOption("question", OptionMapping::getAsString);
            StrawPoll poll = new StrawPoll(jda, question,
                    event.getGuild().getIdLong(), event.getChannel().getIdLong(), -1, event.getMember().getIdLong(), Lists.newArrayList());

            StringSelectMenu.Builder builder = StringSelectMenu.create("choose-option");
            List<PollOption> optionList = Lists.newArrayList();
            for (int i = 0; i < 10; i++) {
                OptionMapping eventOption = event.getOption("option-" + (i + 1));
                if (eventOption != null) {
                    optionList.add(new PollOption(i, eventOption.getAsString(), Lists.newArrayList()));
                    builder.addOption(eventOption.getAsString(), String.valueOf(i), Emoji.fromUnicode(EmojiParser.parseToUnicode(optionEmotes[i])));
                }
            }

            event.deferReply().queue();
            poll.setOptions(optionList);
            EmbedBuilder embed = strawPollEmbed(poll);

            OptionMapping option = event.getOption("image");
            String image = option == null ? "" : option.getAsAttachment().getUrl();
            if (!image.isEmpty()) embed.setImage(image);

            event.getHook().sendMessageEmbeds(embed.build()).
                    addActionRow(builder.build())
                    .queue(msg -> {
                        poll.setMessage(msg.getIdLong());
                        pollTracker.addPoll(poll);
                    });
        }
    }

    public static EmbedBuilder strawPollEmbed(StrawPoll poll) {
        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor("Straw Poll Created", "https://discord.com/", "https://media.discordapp.net/attachments/772045047130095616/1144561010808725644/png-clipart-emoji-sticker-red-blue-and-green-bar-graph-thumbnail.png")
                .setTitle("**" + poll.getQuestion() + "**")
                .setFooter(poll.getAuthor().getEffectiveName() + " asked.", poll.getAuthor().getEffectiveAvatarUrl());

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < poll.getOptions().size(); i++) {
            PollOption option = poll.getPollOption(i);
            String optionStr = option.getOption();
            stringBuilder.append(optionEmotes[i]).append("  `` ").append(option.getVoters().size()).append(" votes `` - ").append(optionStr).append("\n");
        }

        builder.setDescription(stringBuilder.toString());
        return builder;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("choose-option")) {
            StrawPoll poll = (StrawPoll) pollTracker.getPollByMessageID(event.getMessageIdLong());
            poll.vote(event);
        }
    }

}