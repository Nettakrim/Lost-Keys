package com.nettakrim.lost_keys;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Iterator;

public class OverrideCommand{
    public static void registerNode(RootCommandNode<ServerCommandSource> root) {
        LiteralCommandNode<ServerCommandSource> clientExecutionNode = CommandManager
            .literal("lostkeys:override")
            .requires((source) -> source.hasPermissionLevel(2))
            .then(
                CommandManager.argument("targets", EntityArgumentType.players()).then(
                    CommandManager.argument("binding", StringArgumentType.string()).then(
                            CommandManager.argument("key", StringArgumentType.string()).executes(OverrideCommand::run)
                    )
                )
            )
            .build();

        root.addChild(clientExecutionNode);
    }

    public static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String binding = StringArgumentType.getString(context, "binding");
        String key = StringArgumentType.getString(context, "key");

        int i = 0;

        for(Iterator<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets").iterator(); targets.hasNext(); ++i) {
            ServerPlayerEntity player = targets.next();
            LostKeys.overridePlayer(player, binding, key);
        }

        return i;
    }
}
