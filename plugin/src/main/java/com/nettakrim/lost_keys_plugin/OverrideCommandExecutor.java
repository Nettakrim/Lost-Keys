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
        if (command.getLabel().equalsIgnoreCase("lost_keys:override")) {
            if (args.length < 3) {
                commandSender.sendMessage("not enough arguments! /lost_keys:override <player> <binding> <key>");
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
            String key = args[2];

            for (Player player : players) {
                if (player != null) {
                    plugin.sendOverride(player, binding, key);
                }
            }
        }

        return true;
    }
}
