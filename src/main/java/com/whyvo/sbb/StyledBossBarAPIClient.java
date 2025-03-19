package com.whyvo.sbb;

import com.whyvo.sbb.network.StyledBossBarS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class StyledBossBarAPIClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(StyledBossBarAPI.PACKET_STYLED_BOSS_BAR,
                (client, handler, buf, responseSender) -> StyledBossBarS2CPacket.handle(client, buf));
    }
}
