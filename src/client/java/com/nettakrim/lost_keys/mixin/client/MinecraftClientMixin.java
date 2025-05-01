package com.nettakrim.lost_keys.mixin.client;

import com.nettakrim.lost_keys.KeyOverride;
import com.nettakrim.lost_keys.LostKeysClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
    private void saveSpace(CallbackInfo ci) {
        LostKeysClient.keyOverrides.clear();
        LostKeysClient.allMode = null;
    }

    @Inject(method = "setScreen", at = @At("TAIL"))
    private void screenClosed(Screen screen, CallbackInfo ci) {
        if (screen != null) {
            return;
        }

        for (KeyOverride keyOverride : LostKeysClient.keyOverrides) {
            if (keyOverride.key().equals("pressed")) {
                KeyBindingAccessor.getBinding().get(keyOverride.binding()).setPressed(true);
            }
        }
    }
}
