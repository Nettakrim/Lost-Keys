package com.nettakrim.lost_keys;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record BindCommandPayload(String binding, String command) implements CustomPayload {
    public static final Id<BindCommandPayload> PACKET_ID = new Id<>(LostKeys.bindCommand);
    public static final PacketCodec<RegistryByteBuf, BindCommandPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            BindCommandPayload::binding,
            PacketCodecs.STRING,
            BindCommandPayload::command,
            BindCommandPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(PACKET_ID, PACKET_CODEC);
    }
}
