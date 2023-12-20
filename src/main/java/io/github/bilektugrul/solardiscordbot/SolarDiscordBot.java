package io.github.bilektugrul.solardiscordbot;

import io.github.bilektugrul.solardiscordbot.bans.BanCheckThread;
import io.github.bilektugrul.solardiscordbot.bans.BanManager;
import io.github.bilektugrul.solardiscordbot.commands.DiscordCommands;
import io.github.bilektugrul.solardiscordbot.commands.ReloadCommand;
import io.github.bilektugrul.solardiscordbot.commands.linking.DiscordLinkCommand;
import io.github.bilektugrul.solardiscordbot.commands.moderation.BanCommand;
import io.github.bilektugrul.solardiscordbot.commands.moderation.KickCommand;
import io.github.bilektugrul.solardiscordbot.commands.moderation.MuteCommand;
import io.github.bilektugrul.solardiscordbot.customcmd.CmdManager;
import io.github.bilektugrul.solardiscordbot.linking.DiscordLinkManager;
import io.github.bilektugrul.solardiscordbot.listener.BukkitListener;
import io.github.bilektugrul.solardiscordbot.listener.DiscordMessageListener;
import io.github.bilektugrul.solardiscordbot.listener.LuckPermsListener;
import io.github.bilektugrul.solardiscordbot.polls.PollTracker;
import io.github.bilektugrul.solardiscordbot.polls.commands.NormalPollCommand;
import io.github.bilektugrul.solardiscordbot.polls.commands.PollEndCommand;
import io.github.bilektugrul.solardiscordbot.polls.commands.StrawPollCommand;
import io.github.bilektugrul.solardiscordbot.polls.commands.ThreadPollCommand;
import io.github.bilektugrul.solardiscordbot.users.UserManager;
import io.github.bilektugrul.solardiscordbot.util.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SolarDiscordBot extends JavaPlugin {

    private JDA bot;
    private CmdManager cmdManager;
    private BanManager banManager;
    private UserManager userManager;
    private DiscordLinkManager discordLinkManager;
    private PollTracker pollTracker;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.cmdManager = new CmdManager(this);
        this.banManager = new BanManager(this);
        this.userManager = new UserManager(this);
        for (Player looped : Bukkit.getOnlinePlayers()) {
            userManager.loadUser(looped);
        }

        this.discordLinkManager = new DiscordLinkManager(this);

        loadBot();
        new BanCheckThread(this);
        new LuckPermsListener(this, LuckPermsProvider.get());

        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
        getCommand("dcbot").setExecutor(new ReloadCommand(this));
        getCommand("discord").setExecutor(new DiscordLinkCommand(this));
    }

    @Override
    public void onDisable() {
        bot.shutdown();

        try {
            userManager.saveUsers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        pollTracker.savePolls();
        banManager.saveBans();
    }

    public void loadBot() {
        if (bot != null) {
            bot.shutdown();
        }

        this.bot = JDABuilder.createDefault(Utils.getString("token"))
                .enableCache(CacheFlag.ONLINE_STATUS)
                .enableCache(CacheFlag.ACTIVITY)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES)
                .build();

        List<CommandData> commandDatas = new ArrayList<>(Arrays.asList(Commands.slash("broadcast", "Broadcast a message in the server")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "message", "The message to broadcast in server", true),
                Commands.slash("dcbroadcast", "Broadcast a message in the discord server")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
                        .setGuildOnly(true)
                        .addOption(OptionType.CHANNEL, "channel", "The channel that you want to broadcast a message in", true)
                        .addOption(OptionType.ATTACHMENT, "message", "The message to broadcast", true),
                Commands.slash("stats", "Sends stats of given player")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "player", "The player whose stats you want to see", true),
                Commands.slash("poll", "Create a poll")
                        .setGuildOnly(true)
                        .addSubcommands(new SubcommandData("normal", "Create a poll with yes and no options")
                                .addOption(OptionType.STRING, "question", "Question for the poll", true)
                                .addOption(OptionType.ATTACHMENT, "image", "Set an image or video to be displayed with the poll"))
                        .addOption(OptionType.STRING, "question", "Question for the poll", true)
                        .addOption(OptionType.ATTACHMENT, "image", "Set an image or video to be displayed with the poll"),
                Commands.slash("normalpoll", "Create a poll with yes and no options")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "question", "Question for the poll", true)
                        .addOption(OptionType.ATTACHMENT, "image", "Set an image or video to be displayed with the poll"),
                Commands.slash("threadpoll", "Create a poll with discussion thread")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "question", "Question for the poll", true)
                        .addOption(OptionType.ATTACHMENT, "image", "Set an image or video to be displayed with the poll"),
                Commands.slash("pollend", "End a poll")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "message-id", "Poll message ID", true),
                Commands.slash("strawpoll", "Create a strawpoll")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "question", "Question for the poll", true)
                        .addOption(OptionType.STRING, "option-1", "An option", true)
                        .addOption(OptionType.STRING, "option-2", "An option", true)
                        .addOption(OptionType.STRING, "option-3", "An option")
                        .addOption(OptionType.STRING, "option-4", "An option")
                        .addOption(OptionType.STRING, "option-5", "An option")
                        .addOption(OptionType.STRING, "option-6", "An option")
                        .addOption(OptionType.STRING, "option-7", "An option")
                        .addOption(OptionType.STRING, "option-8", "An option")
                        .addOption(OptionType.STRING, "option-9", "An option")
                        .addOption(OptionType.STRING, "option-10", "An option")
                        .addOption(OptionType.ATTACHMENT, "image", "Set an image or video to be displayed with the poll"),
                Commands.slash("ban", "Ban command")
                        .addOption(OptionType.USER, "user", "User that will be banned", true)
                        .addOption(OptionType.STRING, "timespan", "Ban duration", false)
                        .addOption(OptionType.STRING, "reason", "The ban reason", false),
                Commands.slash("mute", "Mute command")
                        .addOption(OptionType.USER, "user", "User that will be timeouted", true)
                        .addOption(OptionType.STRING, "timespan", "Timeout duration", false)
                        .addOption(OptionType.STRING, "reason", "The timeout reason", false),
                Commands.slash("kick", "Mute command")
                        .addOption(OptionType.USER, "user", "User that will be kicked", true)
                        .addOption(OptionType.STRING, "reason", "The kick reason", false)));

        this.cmdManager.getCmds().forEach(cmd -> commandDatas.add(Commands.slash(cmd.getName(), cmd.getCommandDescription()).setGuildOnly(true)));
        this.bot.updateCommands().addCommands(commandDatas).queue();

        getServer().getScheduler().runTaskLater(this, () -> {
            this.pollTracker = new PollTracker(this);
            bot.addEventListener(new DiscordCommands(this),
                    new DiscordMessageListener(this),
                    new BanCommand(this),
                    new MuteCommand(this),
                    new KickCommand(this),
                    new NormalPollCommand(this),
                    new StrawPollCommand(this),
                    new ThreadPollCommand(this),
                    new PollEndCommand(this));
        }, 60);

        if (!Utils.getString("presence").isEmpty()) {
            getServer().getScheduler().runTaskLater(this, () ->
                            bot.getPresence().setActivity(Activity.playing(PlaceholderAPI.setPlaceholders(null, Utils.getString("presence")))),
                    100);
        }
    }

    public CmdManager getCmdManager() {
        return cmdManager;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public DiscordLinkManager getDiscordLinkManager() {
        return discordLinkManager;
    }

    public PollTracker getPollTracker() {
        return pollTracker;
    }

    public JDA getBot() {
        return bot;
    }

}