package com.nettakrim.lost_keys;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ListCommand {
    public static void register(RootCommandNode<FabricClientCommandSource> root) {
        LiteralCommandNode<FabricClientCommandSource> biteSoundNode = ClientCommandManager
                .literal("lost_keys:list")
                .executes(ListCommand::list)
                .build();

        root.addChild(biteSoundNode);
    }

    public static int list(CommandContext<FabricClientCommandSource> context) {
        if (LostKeysClient.keyOverrides.isEmpty() && LostKeysClient.commandBinds.isEmpty()) {
            LostKeysClient.say(Text.translatable(LostKeys.MOD_ID+".override.none"));
        }

        MutableText text = Text.empty();
        if (!LostKeysClient.keyOverrides.isEmpty()) {
            if (LostKeysClient.allMode != null) {
                text.append(Text.translatable(LostKeys.MOD_ID + ".override.all", LostKeysClient.allMode));
            }

            for (KeyOverride keyOverride : LostKeysClient.keyOverrides) {
                text.append(Text.translatable(LostKeys.MOD_ID + ".override", keyOverride.binding(), keyOverride.key()));
            }
            LostKeysClient.say(text);
        }
        if (!LostKeysClient.commandBinds.isEmpty()) {
            LostKeysClient.commandBinds.forEach((key, command) -> text.append(Text.translatable(LostKeys.MOD_ID + ".bind_command", key, command)));
            LostKeysClient.say(text);
        }
        return 1;
    }
}
