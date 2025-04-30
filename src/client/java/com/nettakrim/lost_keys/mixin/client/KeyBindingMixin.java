package com.nettakrim.lost_keys.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nettakrim.lost_keys.KeyOverride;
import com.nettakrim.lost_keys.LostKeys;
import com.nettakrim.lost_keys.LostKeysClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {
    @Shadow @Final private static Map<String, KeyBinding> KEYS_BY_ID;

    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), method = "setKeyPressed")
    private static <V> V applyKeyOverrides(V originalV, InputUtil.Key pressedKey, boolean pressed) {
        KeyBinding original = (KeyBinding)originalV;

        if (pressed && LostKeysClient.logNext) {
            LostKeysClient.logNext = false;
            if (original == null) {
                LostKeysClient.say(Text.translatable(LostKeys.MOD_ID+".log.key", pressedKey.getTranslationKey()));
            } else {
                LostKeysClient.say(Text.translatable(LostKeys.MOD_ID+".log.binding", original.getTranslationKey(), pressedKey.getTranslationKey()));
            }
        }

        if (LostKeysClient.allMode != null) {
            original = null;
            String targetKey = LostKeysClient.allMode;
            KeyBinding redirect = KEYS_BY_ID.get(targetKey);
            if (redirect != null) {
                targetKey = redirect.getBoundKeyTranslationKey();
            }
            if (pressedKey.getTranslationKey().equals(targetKey)) {
                KEYS_BY_ID.values().forEach((binding -> binding.setPressed(pressed)));
            }
        }

        for (KeyOverride keyOverride : LostKeysClient.keyOverrides) {
            // stop key being activated by vanilla functionality if its overridden
            if (original != null && original.getTranslationKey().equals(keyOverride.binding())) {
                if (keyOverride.key().equals("pressed")) {
                    original.setPressed(true);
                    return null;
                }
                original = null;
            }

            // activate all bindings of the given key
            String targetKey = keyOverride.key();
            KeyBinding redirect = KEYS_BY_ID.get(targetKey);
            if (redirect != null) {
                targetKey = redirect.getBoundKeyTranslationKey();
            }

            if (pressedKey.getTranslationKey().equals(targetKey)) {
                KeyBinding targetBinding = KEYS_BY_ID.get(keyOverride.binding());
                if (targetBinding != null) {
                    targetBinding.setPressed(pressed);
                }
            }
        }

        // noinspection unchecked
        return (V)original;
    }
}
