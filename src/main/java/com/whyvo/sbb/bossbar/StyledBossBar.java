package com.whyvo.sbb.bossbar;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.whyvo.sbb.StyledBossBarAPI;
import com.whyvo.sbb.network.StyledBossBarS2CPacket;
import com.whyvo.sbb.style.BossBarStyle;
import com.whyvo.sbb.style.BossBarStyleEntry;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class StyledBossBar<T extends BossBarStyle> extends BossBar {
    private BossBarStyleEntry<T> bossBarStyle;
    private final Set<ServerPlayerEntity> players = Sets.<ServerPlayerEntity>newHashSet();
    private final Set<ServerPlayerEntity> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
    private boolean visible = true;

    public StyledBossBar(Text displayName, BossBarStyleEntry<T> style) {
        super(MathHelper.randomUuid(), displayName, Color.PINK, Style.PROGRESS);
        this.bossBarStyle = style;
    }

    /**
     * @deprecated Use {@link #setBossBarStyle(BossBarStyleEntry)} instead.
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

    public BossBarStyleEntry<T> getBossBarStyle() {
        return bossBarStyle;
    }

    @Override
	public void setPercent(float percent) {
		if (percent != this.percent) {
			super.setPercent(percent);
            this.sendToPlayers(StyledBossBarS2CPacket.updateProgress(this));
		}
	}

    public void setBossBarStyle(BossBarStyleEntry<T> style) {
        if(this.bossBarStyle != style) {
            this.bossBarStyle = style;
            this.sendToPlayers(StyledBossBarS2CPacket.updateStyle(this));
        }
    }

    @Override
	public StyledBossBar<T> setDarkenSky(boolean darkenSky) {
		if (darkenSky != this.darkenSky) {
			super.setDarkenSky(darkenSky);
            this.sendToPlayers(StyledBossBarS2CPacket.updateProperties(this));
		}
		return this;
	}

	@Override
	public StyledBossBar<T> setDragonMusic(boolean dragonMusic) {
		if (dragonMusic != this.dragonMusic) {
			super.setDragonMusic(dragonMusic);
            this.sendToPlayers(StyledBossBarS2CPacket.updateProperties(this));
		}
		return this;
	}

	@Override
	public StyledBossBar<T> setThickenFog(boolean thickenFog) {
		if (thickenFog != this.thickenFog) {
			super.setThickenFog(thickenFog);
            this.sendToPlayers(StyledBossBarS2CPacket.updateProperties(this));
		}
		return this;
	}

    @Override
	public void setName(Text name) {
		if (!Objects.equal(name, this.name)) {
			super.setName(name);
            this.sendToPlayers(StyledBossBarS2CPacket.updateName(this));
		}
	}

    public void addPlayer(ServerPlayerEntity player) {
        if (this.players.add(player) && this.visible) {
            StyledBossBarAPI.sendToPlayer(player, StyledBossBarAPI.PACKET_STYLED_BOSS_BAR, StyledBossBarS2CPacket.add(this));
        }
    }

    public void removePlayer(ServerPlayerEntity player) {
		if (this.players.remove(player) && this.visible) {
            StyledBossBarAPI.sendToPlayer(player, StyledBossBarAPI.PACKET_STYLED_BOSS_BAR, StyledBossBarS2CPacket.remove(this.getUuid()));
		}
	}

    public void clearPlayers() {
		if (!this.players.isEmpty()) {
			for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(this.players)) {
				this.removePlayer(serverPlayerEntity);
			}
		}
	}

    public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		if (visible != this.visible) {
			this.visible = visible;
            this.sendToPlayers(visible ? StyledBossBarS2CPacket.add(this) : StyledBossBarS2CPacket.remove(this.getUuid()));
		}
	}

    private void sendToPlayers(PacketByteBuf buf) {
        for(ServerPlayerEntity player : this.players) {
            StyledBossBarAPI.sendToPlayer(player, StyledBossBarAPI.PACKET_STYLED_BOSS_BAR, buf);
        }
    }

    public Collection<ServerPlayerEntity> getPlayers() {
        return this.unmodifiablePlayers;
    }

}
