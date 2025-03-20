package com.whyvo.sbb.bossbar;

import com.whyvo.sbb.style.BossBarStyle;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;

import java.util.UUID;

public final class ClientStyledBossBar extends ClientBossBar {
    private BossBarStyle style;

    public ClientStyledBossBar(UUID uuid, Text name, float percent, BossBarStyle style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        super(uuid, name, percent, Color.PINK, Style.PROGRESS, darkenSky, dragonMusic, thickenFog);
        this.style = style;
    }

    /**
     * @deprecated Use {@link #setBossBarStyle(BossBarStyle)} instead.
     */
    @Deprecated(forRemoval = true)
    @Override
    public void setStyle(Style style) {
    }

    /**
     * @deprecated Use {@link #getBossBarStyle()} instead.
     */
    @Deprecated(forRemoval = true)
    public BossBar.Style getStyle() {
        return Style.PROGRESS;
    }

    @Deprecated(forRemoval = true)
    @Override
    public void setColor(Color color) {
    }

    @Deprecated(forRemoval = true)
    @Override
    public Color getColor() {
        return Color.PINK;
    }

    public void setBossBarStyle(BossBarStyle style) {
        this.style = style;
    }

    public BossBarStyle getBossBarStyle() {
        return style;
    }

    /**
     * {@return vertical increment (vanilla boss bar is 19)}
     *
     * <p>Renders this boss bar
     */
    public int render(DrawContext context, int x, int y) {
        return this.style.render(context, x, y, this);
    }
}
