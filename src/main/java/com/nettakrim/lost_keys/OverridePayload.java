package com.nettakrim.lost_keys;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record OverridePayload(String binding, String key) implements CustomPayload {
    public static final CustomPayload.Id<OverridePayload> PACKET_ID = new CustomPayload.Id<>(LostKeys.override);
    public static final PacketCodec<RegistryByteBuf, OverridePayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            OverridePayload::binding,
            PacketCodecs.STRING,
            OverridePayload::key,
            OverridePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(PACKET_ID, PACKET_CODEC);
    }
}
