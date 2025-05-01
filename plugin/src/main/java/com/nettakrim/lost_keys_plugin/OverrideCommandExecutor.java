package com.nettakrim.lost_keys_plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OverrideCommandExecutor implements CommandExecutor {
    public LostKeys plugin;

    public OverrideCommandExecutor(LostKeys plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean isOverride = command.getLabel().equalsIgnoreCase("override");
        boolean isCommand = command.getLabel().equalsIgnoreCase("bind_command");
        if (isOverride || isCommand) {
            if (args.length < 3) {
                commandSender.sendMessage("not enough arguments! /lost_keys:"+(isOverride ? "override":"bind_command")+" <player> <binding> <" + (isOverride ? "key>" : "command>"));
                return false;
            }

            String playerName = args[0];

            Player[] players;
            if (playerName.startsWith("@")) {
                players = commandSender.getServer().getOnlinePlayers().toArray(new Player[0]);
            } else {
                players = new Player[] {commandSender.getServer().getPlayer(playerName)};
            }

            String binding = args[1];
            StringBuilder key = new StringBuilder(args[2]);
            if (args.length > 3) {
                for (int i = 3; i < args.length; i++) {
                    key.append(" ").append(args[i]);
                }
            }

            for (Player player : players) {
                if (player != null) {
                    plugin.sendMessage(player, binding, key.toString(), isOverride ? "override" : "bind_command");
                }
            }
        }
        return true;
    }
}
