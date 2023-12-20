package io.github.bilektugrul.solardiscordbot.polls.types;

import io.github.bilektugrul.solardiscordbot.polls.Poll;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.util.logging.Logger;

public class ThreadPoll implements Poll {

    private final String question;

    private final Guild guild;
    private final TextChannel channel;
    private final Member author;

    private ThreadChannel thread;
    private Message message;

    public final boolean broken;

    public ThreadPoll(JDA jda, String question,
                     long guildId, long channelId, long pollMessageID,
                      long authorID, long threadId
    ) {
        this.question = question;

        this.guild = jda.getGuildById(guildId);
        this.channel = guild.getChannelById(TextChannel.class, channelId);
        this.author = guild.getMemberById(authorID);

        try {
            this.message = channel.retrieveMessageById(pollMessageID).complete();
        } catch (Exception e) {
            Logger.getLogger("SolarDiscordBot").warning("The poll message " + pollMessageID + " could not be found.");
            broken = true;
            return;
        }

        if (threadId != -1) {
            try {
                this.thread = message.getStartedThread();
            } catch (Exception e) {
                Logger.getLogger("SolarDiscordBot").warning("The poll thread " + threadId + " could not be found.");
                broken = true;
                return;
            }
        }

        this.broken = false;
    }

    public String getQuestion() {
        return question;
    }

    public void setMessage(long pollMessageID) {
        channel.retrieveMessageById(pollMessageID).queue(msg -> message = msg);
    }

    public void setThread(ThreadChannel thread) {
        this.thread = thread;
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

    public ThreadChannel getThread() {
        return thread;
    }

    public long getThreadId() {
        return thread.getIdLong();
    }

}