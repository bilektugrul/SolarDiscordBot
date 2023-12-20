package io.github.bilektugrul.solardiscordbot.listener;

import io.github.bilektugrul.solardiscordbot.SolarDiscordBot;
import io.github.bilektugrul.solardiscordbot.linking.DiscordLinkManager;
import io.github.bilektugrul.solardiscordbot.users.UserManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

public class LuckPermsListener {

    private final SolarDiscordBot plugin;
    private final UserManager userManager;
    private final DiscordLinkManager discordLinkManager;

    public LuckPermsListener(SolarDiscordBot plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.discordLinkManager = plugin.getDiscordLinkManager();

        EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(this.plugin, NodeAddEvent.class, this::nodeAddEvent);
    }

    private void nodeAddEvent(NodeAddEvent event) {
        User target = (User) event.getTarget();
        Node node = event.getNode();

        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            Player player = this.plugin.getServer().getPlayer(target.getUniqueId());
            if (player == null) {
                return;
            }

            if (!(node instanceof InheritanceNode)) {
                return;
            }

            io.github.bilektugrul.solardiscordbot.users.User mcUser = userManager.getUser(player);
            if (mcUser.getDiscordID() == -1) return;

            if (mcUser.getGivenRole() != -1) {
                discordLinkManager.removeRole(player);
            }

            discordLinkManager.giveRole(player);
        });

    }
}