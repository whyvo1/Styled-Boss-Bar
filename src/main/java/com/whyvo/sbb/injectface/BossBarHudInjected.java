package com.whyvo.sbb.injectface;


import com.whyvo.sbb.network.StyledBossBarS2CPayload;

public interface BossBarHudInjected {
    default void styledBossBarAPI$onStyledBossBar(StyledBossBarS2CPayload packet) {
    }
}
