package com.whyvo.sbb.injectface;


import com.whyvo.sbb.bossbar.ClientStyledBossBar;
import com.whyvo.sbb.network.StyledBossBarS2CPayload;

import java.util.Map;
import java.util.UUID;

public interface BossBarHudInjected {
    default void styledBossBarAPI$onStyledBossBar(StyledBossBarS2CPayload packet) {
    }

    default Map<UUID, ClientStyledBossBar> styledBossBarAPI$getStyledBossBars() {
        return null;
    }

    default int styledBossBarAPI$getCachedHeight() {
        return 0;
    }
}
