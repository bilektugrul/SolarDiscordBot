package io.github.bilektugrul.solardiscordbot.polls;

import com.google.common.collect.Lists;
import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.polls.types.NormalPoll;
import io.github.bilektugrul.solardiscordbot.polls.types.StrawPoll;
import io.github.bilektugrul.solardiscordbot.polls.types.ThreadPoll;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PollTracker {

    private final SolarDiscordBot plugin;
    private final Set<Poll> polls = new HashSet<>();

    private FileConfiguration pollFile;

    public PollTracker(SolarDiscordBot plugin) {
        this.plugin = plugin;

        loadPolls();
    }

    public void loadPolls() {
        this.polls.clear();
        this.pollFile = ConfigUtils.getConfig(plugin, "polls");

        if (!pollFile.isSet("polls")) return;

        for (String key : pollFile.getConfigurationSection("polls").getKeys(false)) {
            loadPoll(pollFile.getConfigurationSection("polls." + key));
        }
    }

    public void loadPoll(ConfigurationSection section) {
        String type = section.getString("type");
        String question = section.getString("question");

        long messageID = Long.parseLong(section.getName());
        long guild = section.getLong("guild");
        long channel = section.getLong("channel");
        long author = section.getLong("author");

        if (type.equalsIgnoreCase("straw")) {
            List<PollOption> optionList = Lists.newArrayList();
            for (String key : section.getConfigurationSection("options").getKeys(false)) {
                PollOption option = new PollOption(Integer.parseInt(key),
                        section.getString("options." + key + ".option"),
                        section.getLongList("options." + key + ".voters"));
                optionList.add(option);
            }

            StrawPoll strawPoll = new StrawPoll(plugin.getBot(), question, guild, channel, messageID, author, Lists.newArrayList(optionList));
            if (strawPoll.broken) {
                return;
            }

            polls.add(strawPoll);
        } else if (type.equalsIgnoreCase("normal")) {
            List<Long> yesVotes = section.getLongList("yes-votes");
            List<Long> noVotes = section.getLongList("no-votes");

            NormalPoll normalPoll = new NormalPoll(plugin.getBot(), question, guild, channel, messageID, author, yesVotes, noVotes);
            if (normalPoll.broken) {
                return;
            }

            polls.add(normalPoll);
        } else if (type.equalsIgnoreCase("thread")) {
            ThreadPoll threadPoll = new ThreadPoll(plugin.getBot(), question, guild, channel, messageID, author, section.getLong("thread-id"));
            if (threadPoll.broken) {
                return;
            }

            polls.add(threadPoll);
        }
    }

    public void addPoll(Poll poll) {
        polls.add(poll);
    }

    public void removePoll(Poll poll) {
        polls.remove(poll);
    }

    public Poll getPollByMessageID(long id) {
        for (Poll poll : polls) {
            if (poll.getMessageId() == id) {
                return poll;
            }
        }

        return null;
    }

    public void savePolls() {
        pollFile.set("polls", null);
        for (Poll poll : polls) {
            String type = poll instanceof NormalPoll ? "normal" : poll instanceof StrawPoll ? "straw" : "thread";

            String path = "polls." + poll.getMessageId() + ".";
            pollFile.set(path + "type", type);
            pollFile.set(path + "question", poll.getQuestion());
            pollFile.set(path + "guild", poll.getGuildId());
            pollFile.set(path + "channel", poll.getChannelId());
            pollFile.set(path + "author", poll.getAuthorId());

            if (type.equalsIgnoreCase("straw")) {
                StrawPoll strawPoll = (StrawPoll) poll;
                for (PollOption option : strawPoll.getOptions()) {
                    int id = option.getId();
                    pollFile.set(path + "options." + id + ".option", option.getOption());
                    pollFile.set(path + "options." + id + ".voters", option.getVoters());
                }
            } else if (type.equalsIgnoreCase("normal")) {
                NormalPoll normalPoll = (NormalPoll) poll;
                pollFile.set(path + "yes-votes", normalPoll.getYesVotes());
                pollFile.set(path + "no-votes", normalPoll.getNoVotes());
            } else if (type.equalsIgnoreCase("thread")) {
                ThreadPoll threadPoll = (ThreadPoll) poll;

                pollFile.set(path + "thread-id", threadPoll.getThreadId());
            }
        }

        ConfigUtils.saveConfig(plugin, pollFile, "polls");
    }

}