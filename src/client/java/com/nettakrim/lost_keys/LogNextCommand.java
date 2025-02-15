package com.nettakrim.lost_keys;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class LogNextCommand {
    public static void register(RootCommandNode<FabricClientCommandSource> root) {
        LiteralCommandNode<FabricClientCommandSource> biteSoundNode = ClientCommandManager
                .literal("lost_keys:lognext")
                .executes(LogNextCommand::logNext)
                .build();

        root.addChild(biteSoundNode);
    }

    public static int logNext(CommandContext<FabricClientCommandSource> context) {
        LostKeysClient.logNext = true;
        return 1;
    }
}
