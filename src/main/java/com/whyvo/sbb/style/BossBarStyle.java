package com.whyvo.sbb.style;

import com.whyvo.sbb.bossbar.ClientStyledBossBar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

public interface BossBarStyle {

    /**
     * {@return vertical increment (vanilla boss bar is 19)}
     *
     * <p>Renders this style
     */
    @Environment(EnvType.CLIENT)
    int render(DrawContext context, int x, int y, ClientStyledBossBar bossBar);
}
