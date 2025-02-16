package com.nettakrim.lost_keys;

import com.mojang.brigadier.tree.RootCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class LostKeysClient implements ClientModInitializer {
	public static String allMode = null;
	public static List<KeyOverride> keyOverrides = new ArrayList<>();

	public static boolean logNext = false;

	public static final Style nameStyle = Style.EMPTY.withColor(0xE8AD48);
	public static final Style textStyle = Style.EMPTY.withColor(0xAAAAAA);

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(OverridePayload.PACKET_ID, ((payload, context) -> addOverride(payload.binding(), payload.key())));

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			RootCommandNode<FabricClientCommandSource> root = dispatcher.getRoot();

			ListCommand.register(root);
			LogNextCommand.register(root);
		});
	}

	public static void addOverride(String binding, String key) {
		keyOverrides.removeIf((override) -> override.binding().equals(binding));

		if (binding.equals("all")) {
			if (key.equals("default")) {
				keyOverrides.clear();
				allMode = null;
			} else {
				allMode = key;
			}
			return;
		}

		if (key.equals("default")) {
			return;
		}

		keyOverrides.add(new KeyOverride(binding, key));
	}

	public static void say(MutableText text) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null) return;
		client.player.sendMessage(Text.translatable(LostKeys.MOD_ID+".say").setStyle(nameStyle).append(text.setStyle(textStyle)), false);
	}
}