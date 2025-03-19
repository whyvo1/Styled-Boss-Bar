package com.whyvo.sbb.injectface;


import com.whyvo.sbb.network.StyledBossBarS2CPacket;

public interface BossBarHudInjected {
    default void styledBossBarAPI$onStyledBossBar(StyledBossBarS2CPacket packet) {
    }
}
