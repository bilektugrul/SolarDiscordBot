package io.github.bilektugrul.solardiscordbot.polls.commands;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.polls.Poll;
import io.github.bilektugrul.solardiscordbot.polls.PollTracker;
import io.github.bilektugrul.solardiscordbot.polls.types.NormalPoll;
import io.github.bilektugrul.solardiscordbot.polls.types.StrawPoll;
import io.github.bilektugrul.solardiscordbot.polls.types.ThreadPoll;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class PollEndCommand extends ListenerAdapter {

    private final PollTracker pollTracker;

    public PollEndCommand(SolarDiscordBot plugin) {
        this.pollTracker = plugin.getPollTracker();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String name = event.getName();

        if (name.equals("pollend")) {
            if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                event.reply("You can not use this command.").setEphemeral(true).queue();
                return;
            }

            long pollMessageID = event.getOption("message-id", OptionMapping::getAsLong);
            Poll poll = pollTracker.getPollByMessageID(pollMessageID);
            if (poll == null) {
                event.reply("There is no poll in that message.").setEphemeral(true).queue();
                return;
            }

            String type = poll instanceof NormalPoll ? "normal" : poll instanceof StrawPoll ? "straw" : "thread";
            if (type.equalsIgnoreCase("thread")) {
                ThreadPoll threadPoll = (ThreadPoll) poll;
                ThreadChannel channel = threadPoll.getThread();
                channel.getManager().setArchived(true).setLocked(true).queue();
            }

            pollTracker.removePoll(poll);
            poll.getChannel().retrieveMessageById(pollMessageID).queue(msg -> {
                MessageEditData data = MessageEditBuilder.fromMessage(msg)
                        .setContent("This poll has ended. (<t:" + System.currentTimeMillis() / 1000 + ":R>)")
                        .setEmbeds(msg.getEmbeds())
                        .setComponents()
                        .build();
                msg.editMessage(data).queue();
            });
            event.reply("Stopped given poll.").setEphemeral(true).queue();
        }
    }

}