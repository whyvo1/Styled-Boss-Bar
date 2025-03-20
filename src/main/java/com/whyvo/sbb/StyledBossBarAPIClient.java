package com.whyvo.sbb;

import com.whyvo.sbb.network.StyledBossBarS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.PacketCodec;

public class StyledBossBarAPIClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.registerReceiver();
    }

    private void registerReceiver() {
        PayloadTypeRegistry.playS2C().register(StyledBossBarS2CPayload.ID, PacketCodec.of(
                StyledBossBarS2CPayload::writePacket,
                StyledBossBarS2CPayload::new
        ));
        ClientPlayNetworking.registerGlobalReceiver(StyledBossBarS2CPayload.ID, StyledBossBarS2CPayload::handle);
    }
}
