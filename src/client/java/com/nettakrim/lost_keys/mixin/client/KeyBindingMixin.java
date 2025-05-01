package com.nettakrim.lost_keys.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.nettakrim.lost_keys.KeyBindingInterface;
import com.nettakrim.lost_keys.KeyOverride;
import com.nettakrim.lost_keys.LostKeys;
import com.nettakrim.lost_keys.LostKeysClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements KeyBindingInterface {
    @Shadow @Final private static Map<String, KeyBinding> KEYS_BY_ID;

    @Shadow public abstract String getTranslationKey();

    @Unique private boolean value0;
    @Unique private boolean value1;
    @Unique private boolean value2;

    @Unique private boolean exhausted;

    @Unique private static final Set<InputUtil.Key> pressedCommands = new HashSet<>();

    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), method = "setKeyPressed")
    private static <V> V applyKeyOverrides(V originalV, InputUtil.Key pressedKey, boolean pressed) {
        KeyBinding original = (KeyBinding)originalV;

        String command = LostKeysClient.commandBinds.get(pressedKey.getTranslationKey());
        if (command != null && pressed != pressedCommands.contains(pressedKey)) {
            if (pressed) {
                LostKeysClient.runCommand(command);
                pressedCommands.add(pressedKey);
            } else {
                pressedCommands.remove(pressedKey);
            }
        }

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

    @ModifyReturnValue(at = @At(value = "RETURN"), method = "wasPressed")
    private boolean applyKeyPressedOverrides(boolean original) {
        if (exhausted) {
            return original;
        }

        for (KeyOverride keyOverride : LostKeysClient.keyOverrides) {
            if (keyOverride.binding().equals(getTranslationKey())) {
                // getting the keybinding from a keyboard key is awkward
                KeyBinding redirect = KEYS_BY_ID.get(keyOverride.key());

                if (redirect == null) {
                    return original;
                }

                KeyBindingMixin mixin = (KeyBindingMixin)(Object)redirect;

                exhausted = true;
                return mixin.value1 && !mixin.value2;
            }
        }

        return original;
    }

    @Override
    public void lostKeys$update() {
        if (value0 && !value1) {
            LostKeysClient.runCommand(LostKeysClient.commandBinds.get(getTranslationKey()));
        }

        value2 = value1;
        value1 = value0;
        exhausted = false;
    }

    @Inject(at = @At("TAIL"), method = "setPressed")
    private void onSetPressed(boolean pressed, CallbackInfo ci) {
        value0 = pressed;
    }
}
