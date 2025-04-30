package com.nettakrim.lost_keys;

import com.mojang.brigadier.tree.RootCommandNode;
import com.nettakrim.lost_keys.mixin.client.KeyBindingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LostKeysClient implements ClientModInitializer {
	public static String allMode = null;
	public static List<KeyOverride> keyOverrides = new ArrayList<>();
	public static HashMap<String, String> commandOverrides = new HashMap<>();

	public static boolean logNext = false;

	public static final Style nameStyle = Style.EMPTY.withColor(0xE8AD48);
	public static final Style textStyle = Style.EMPTY.withColor(0xAAAAAA);

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(OverridePayload.PACKET_ID, ((payload, context) -> addOverride(payload.binding(), payload.key())));
		ClientPlayNetworking.registerGlobalReceiver(CommandPayload.PACKET_ID, ((payload, context) -> addCommand(payload.binding(), payload.command())));

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			RootCommandNode<FabricClientCommandSource> root = dispatcher.getRoot();

			ListCommand.register(root);
			LogNextCommand.register(root);
		});

		ClientTickEvents.START_CLIENT_TICK.register((client) -> KeyBindingAccessor.getBinding().values().forEach(keyBinding -> ((KeyBindingInterface)keyBinding).lostKeys$update()));
	}

	public static void addOverride(String binding, String key) {
		keyOverrides.removeIf((override) -> override.binding().equals(binding));

		Map<String, KeyBinding> keyBindings = KeyBindingAccessor.getBinding();

		if (binding.equals("all")) {
			for (KeyBinding targetBinding : keyBindings.values()) {
				targetBinding.setPressed(false);
			}

			if (key.equals("default")) {
				keyOverrides.clear();
				allMode = null;
			} else {
				allMode = key;
			}
			return;
		}

		KeyBinding targetBinding = keyBindings.get(binding);
		if (targetBinding != null) {
			targetBinding.setPressed(key.equals("pressed"));
		}

		if (key.equals("default")) {
			return;
		}

		keyOverrides.add(new KeyOverride(binding, key));
	}

	public static void addCommand(String binding, String command) {
		if (command.equals("none") || command.equals("default")) {
			commandOverrides.remove(binding);
		} else {
			commandOverrides.put(binding, command);
		}
	}

	public static void say(MutableText text) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null) return;
		client.player.sendMessage(Text.translatable(LostKeys.MOD_ID+".say").setStyle(nameStyle).append(text.setStyle(textStyle)), false);
	}
}