package com.nettakrim.lost_keys;

import com.mojang.brigadier.tree.RootCommandNode;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LostKeys implements ModInitializer {
	public static final String MOD_ID = "lost_keys";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier override = Identifier.of(MOD_ID, "override");

	public static void overridePlayer(ServerPlayerEntity player, String binding, String key) {
		ServerPlayNetworking.send(player, new OverridePayload(binding, key));
	}

	@Override
	public void onInitialize() {
		OverridePayload.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			RootCommandNode<ServerCommandSource> root = dispatcher.getRoot();

			OverrideCommand.registerNode(root);
		});
	}
}