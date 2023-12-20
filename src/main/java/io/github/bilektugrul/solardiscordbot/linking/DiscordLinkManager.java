package io.github.bilektugrul.solardiscordbot.linking;

import com.mifmif.common.regex.Generex;
import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.users.User;
import io.github.bilektugrul.solardiscordbot.users.UserManager;
import io.github.bilektugrul.solardiscordbot.util.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DiscordLinkManager {

    private final SolarDiscordBot plugin;

    private final UserManager userManager;
    private final Generex generex = new Generex("[A-Za-z0-9]{8}");
    private final Map<Player, String> codes = new HashMap<>();
    private final LuckPerms luckPerms = LuckPermsProvider.get();

    public DiscordLinkManager(SolarDiscordBot plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    public void startLinkProcess(Player player) {
        User mcUser = userManager.getUser(player);
        if (mcUser.getDiscordID() != -1) {
            player.sendMessage(Utils.getMessage("messages.already-linked", player));
            return;
        }

        if (codes.containsKey(player)) {
            String code = codes.get(player);
            String message = Utils.getMessage("messages.code-already-created", player).replace("%code%", code);
            BaseComponent component = new TextComponent(message);
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, code));
            player.sendMessage(component);
            return;
        }

        String code = generex.random();
        codes.put(player, code);

        String message = Utils.getMessage("messages.code-created", player).replace("%code%", code);
        BaseComponent component = new TextComponent(message);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, code));
        player.sendMessage(component);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (mcUser.getDiscordID() == -1) {
                codes.entrySet().removeIf(p -> p.getValue().equalsIgnoreCase(code));
            }
        }, (60 * 5) * 20);
    }

    public void link(TextChannel channel, Member member, Player player) {
        User mcUser = userManager.getUser(player);
        if (mcUser.getDiscordID() != -1) {
            player.sendMessage(Utils.getMessage("messages.already-linked", player));
            return;
        }

        codes.remove(player);

        mcUser.setDiscordID(member.getIdLong());
        giveRole(player);

        player.sendMessage(Utils.getMessage("messages.linked", player).replace("%account%", member.getUser().getName()));
        channel.sendMessage(Utils.getMessage("messages.linked-discord", player)
                .replace("%name%", player.getName())
                .replace("%member%", member.getAsMention())).queue();
    }

    public void unlink(Player player) {
        User mcUser = userManager.getUser(player);
        long discordID = mcUser.getDiscordID();

        if (discordID == -1) {
            player.sendMessage(Utils.getMessage("messages.not-linked", player));
            return;
        }

        removeRole(player);
        mcUser.setDiscordID(-1);
        player.sendMessage(Utils.getMessage("messages.unlinked", player));
    }

    public void sendInfo(Player player) {
        User mcUser = userManager.getUser(player);
        if (mcUser.getDiscordID() == -1) {
            player.sendMessage(Utils.getMessage("messages.not-linked", player));
            return;
        }

        String message = Utils.getMessage("messages.info", player);
        player.sendMessage(message.replace("%account%", plugin.getBot().getUserById(mcUser.getDiscordID()).getName()));
    }

    public void giveRole(Player player) {
        net.luckperms.api.model.user.User lpUser = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        User mcUser = userManager.getUser(player);

        String group = lpUser.getPrimaryGroup();

        if (Utils.getConfig().isConfigurationSection("rank-sync." + group)) {
            Guild guild = plugin.getBot().getGuildById(Utils.getLong("discord-guild-id"));
            Member member = guild.getMemberById(mcUser.getDiscordID());

            if (mcUser.getGivenRole() != -1) {
                guild.removeRoleFromMember(member, guild.getRoleById(mcUser.getGivenRole())).queue(s -> giveRole2(member, guild, mcUser, group));
            } else {
                giveRole2(member, guild, mcUser, group);
            }
        }
    }

    private void giveRole2(Member member, Guild guild, User mcUser, String group) {
        long roleID = Utils.getLong("rank-sync." + group + ".discord-role-id");
        guild.addRoleToMember(member, guild.getRoleById(roleID)).queue();
        mcUser.setGivenRole(roleID);
    }

    public void removeRole(Player player) {
        User mcUser = userManager.getUser(player);
        long discordID = mcUser.getDiscordID();

        Guild guild = plugin.getBot().getGuildById(Utils.getLong("discord-guild-id"));
        Member member = guild.getMemberById(discordID);

        if (mcUser.getGivenRole() != -1) {
            guild.removeRoleFromMember(member, guild.getRoleById(mcUser.getGivenRole())).queue();
            mcUser.setGivenRole(-1);
        }
    }

    public void deleteCode(Player player) {
        codes.remove(player);
    }

    public boolean doesCodeExist(String code) {
        return codes.containsValue(code);
    }

    public Player getCodeOwner(String code) {
        for (Map.Entry<Player, String> entry : codes.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(code)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public boolean isLinking(Player player) {
        return codes.containsKey(player);
    }

}