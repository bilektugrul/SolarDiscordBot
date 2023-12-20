package io.github.bilektugrul.solardiscordbot.polls.types;

import io.github.bilektugrul.solardiscordbot.polls.Poll;
import io.github.bilektugrul.solardiscordbot.polls.PollOption;
import io.github.bilektugrul.solardiscordbot.polls.commands.StrawPollCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.List;
import java.util.logging.Logger;

public class StrawPoll implements Poll {

    private final String question;

    private final Guild guild;
    private final TextChannel channel;
    private final Member author;

    private Message message;
    private List<PollOption> options;

    public final boolean broken;

    public StrawPoll(JDA jda, String question,
                      long guildId, long channelId, long pollMessageID, long authorID,
                     List<PollOption> options
    ) {
        this.question = question;

        this.guild = jda.getGuildById(guildId);
        this.channel = guild.getChannelById(TextChannel.class, channelId);
        this.author = guild.getMemberById(authorID);
        this.options = options;

        if (pollMessageID != -1) {
            try {
                this.message = channel.retrieveMessageById(pollMessageID).complete();
            } catch (Exception e) {
                Logger.getLogger("SolarDiscordBot").warning("The poll message " + pollMessageID + " could not be found.");
                broken = true;
                return;
            }
        }

        this.broken = false;
    }

    public String getQuestion() {
        return question;
    }

    public void vote(StringSelectInteractionEvent event) {
        Member member = event.getMember();
        long id = member.getIdLong();
        PollOption option = options.get(Integer.parseInt(event.getValues().get(0)));
        boolean changed = false;

        if (isVoted(member)) {
            PollOption votedOption = getVotedOption(member);
            votedOption.removeVoter(id);
            changed = true;
        }

        option.addVoter(id);

        MessageEditData data = MessageEditData.fromEmbeds(StrawPollCommand.strawPollEmbed(this).build());
        if (changed) {
            event.reply("Your vote was changed.").setEphemeral(true).queue();
        } else {
            event.reply("Your vote was recorded.").setEphemeral(true).queue();
        }
        long msgID = event.getMessageIdLong();
        event.getChannel().retrieveMessageById(msgID).queue(msg -> msg.editMessage(data).queue());
    }

    public boolean isVoted(Member member) {
        return getVotedOption(member) != null;
    }

    public void setMessage(long pollMessageID) {
        channel.retrieveMessageById(pollMessageID).queue(msg -> message = msg);
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }

    public PollOption getPollOption(int pollOptionID) {
        return options.get(pollOptionID);
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public PollOption getVotedOption(Member member) {
        long id = member.getIdLong();

        for (PollOption option : options) {
            if (option.getVoters().contains(id)) {
                return option;
            }
        }

        return null;
    }

    @Override
    public long getMessageId() {
        return message.getIdLong();
    }

    public long getAuthorId() {
        return author.getIdLong();
    }

    public long getGuildId() {
        return guild.getIdLong();
    }

    public long getChannelId() {
        return channel.getIdLong();
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public Guild getGuild() {
        return guild;
    }

    @Override
    public TextChannel getChannel() {
        return channel;
    }

    @Override
    public Member getAuthor() {
        return author;
    }

}