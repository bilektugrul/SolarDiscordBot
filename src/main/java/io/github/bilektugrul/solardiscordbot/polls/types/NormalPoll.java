package io.github.bilektugrul.solardiscordbot.polls.types;

import io.github.bilektugrul.solardiscordbot.polls.Poll;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.logging.Logger;

public class NormalPoll implements Poll {

    private final String question;

    private final Guild guild;
    private final TextChannel channel;
    private final Member author;

    private Message message;
    private List<Long> yesVotes;
    private List<Long> noVotes;

    public final boolean broken;

    public NormalPoll(JDA jda, String question,
                      long guildId, long channelId, long pollMessageID, long authorID,
                      List<Long> yesVotes, List<Long> noVotes
    ) {
        this.question = question;

        this.guild = jda.getGuildById(guildId);
        this.channel = guild.getChannelById(TextChannel.class, channelId);
        this.author = guild.getMemberById(authorID);
        this.yesVotes = yesVotes;
        this.noVotes = noVotes;

        try {
            this.message = channel.retrieveMessageById(pollMessageID).complete();
        } catch (Exception e) {
            Logger.getLogger("SolarDiscordBot").warning("The poll message " + pollMessageID + " could not be found.");
            broken = true;
            return;
        }

        this.broken = false;
    }

    public String getQuestion() {
        return question;
    }

    public boolean yesVote(Member member) {
        long id = member.getIdLong();

        if (isVotedYes(member)) return false;

        if (isVotedNo(member)) {
            noVotes.remove(id);
        }

        yesVotes.add(member.getIdLong());
        return true;
    }

    public boolean noVote(Member member) {
        long id = member.getIdLong();

        if (isVotedNo(member)) return false;

        if (isVotedYes(member)) {
            yesVotes.remove(id);
        }

        noVotes.add(member.getIdLong());
        return true;
    }

    public boolean isVotedYes(Member member) {
        long id = member.getIdLong();

        return yesVotes.contains(id);
    }

    public boolean isVotedNo(Member member) {
        long id = member.getIdLong();

        return noVotes.contains(id);
    }

    public void setMessage(long pollMessageID) {
        channel.retrieveMessageById(pollMessageID).queue(msg -> message = msg);
    }

    public List<Long> getYesVotes() {
        return yesVotes;
    }

    public List<Long> getNoVotes() {
        return noVotes;
    }



    @Override
    public long getMessageId() {
        return message.getIdLong();
    }

    @Override
    public long getGuildId() {
        return guild.getIdLong();
    }

    @Override
    public long getChannelId() {
        return channel.getIdLong();
    }

    public long getAuthorId() {
        return author.getIdLong();
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