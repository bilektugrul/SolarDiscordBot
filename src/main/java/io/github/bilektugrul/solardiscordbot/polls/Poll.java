package io.github.bilektugrul.solardiscordbot.polls;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public interface Poll {

    long getMessageId();

    long getGuildId();

    long getChannelId();

    long getAuthorId();

    Message getMessage();

    Guild getGuild();

    TextChannel getChannel();

    Member getAuthor();

    String getQuestion();

}