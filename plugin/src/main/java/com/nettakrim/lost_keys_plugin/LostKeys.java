package com.nettakrim.lost_keys_plugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class LostKeys extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("override").setExecutor(new OverrideCommandExecutor(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "lost_keys:override");
    }

    public void sendMessage(Player player, String binding, String key, String type) {
        byte[] bindingBuf = getStringBuf(binding);
        byte[] keyBuf = getStringBuf(key);
        byte[] packetBuf = new byte[bindingBuf.length+keyBuf.length];
        System.arraycopy(bindingBuf, 0, packetBuf, 0, bindingBuf.length);
        System.arraycopy(keyBuf, 0, packetBuf, bindingBuf.length, keyBuf.length);

        player.sendPluginMessage(this, "lost_keys:"+type, packetBuf);
    }

    private byte[] getStringBuf(String s) {
        byte[] utf = s.getBytes(StandardCharsets.UTF_8);
        byte[] varInt = getVarInt(utf.length);
        byte[] buf = new byte[varInt.length+utf.length];
        System.arraycopy(varInt, 0, buf, 0, varInt.length);
        System.arraycopy(utf, 0, buf, varInt.length, utf.length);
        return buf;
    }

    private byte[] getVarInt(int i) {
        List<Byte> buf = new ArrayList<>();

        while((i & -128) != 0) {
            buf.add((byte)(i & 127 | 128));
            i >>>= 7;
        }

        buf.add((byte)i);

        byte[] bytes = new byte[buf.size()];
        for (int j = 0; j < bytes.length; j++) {
            bytes[j] = buf.get(j);
        }
        return bytes;
    }
}
