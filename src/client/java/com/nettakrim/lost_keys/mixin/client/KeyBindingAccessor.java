package com.nettakrim.lost_keys.mixin.client;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor("KEYS_BY_ID")
    static Map<String, KeyBinding> getBinding() {
        throw new AssertionError("Replaced By Mixin");
    }
}
