package com.whyvo.sbb;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StyledBossBarAPI implements ModInitializer {
	public static final String MOD_ID = "styled_boss_bar";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier PACKET_STYLED_BOSS_BAR = new Identifier(MOD_ID, "styled_boss_bar");



	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}

	public static void sendToPlayer(ServerPlayerEntity player, Identifier id, PacketByteBuf packet) {
		ServerPlayNetworking.send(player, id, packet);
	}
}