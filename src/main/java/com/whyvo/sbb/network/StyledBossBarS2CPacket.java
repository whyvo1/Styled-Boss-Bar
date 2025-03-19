package com.whyvo.sbb.network;

import com.whyvo.sbb.bossbar.StyledBossBar;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.function.Function;

public class StyledBossBarS2CPacket {

    private final UUID uuid;
    private final Action action;
	static final Action REMOVE_ACTION = new Action() {
		@Override
		public Type getType() {
			return Type.REMOVE;
		}

		@Override
		public void accept(UUID uuid, Consumer consumer) {
			consumer.remove(uuid);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
		}
	};

    private StyledBossBarS2CPacket(UUID uuid, Action action) {
		this.uuid = uuid;
		this.action = action;
	}

    public StyledBossBarS2CPacket(PacketByteBuf buf) {
		this.uuid = buf.readUuid();
		Type type = buf.readEnumConstant(Type.class);
		this.action = type.parser.apply(buf);
	}

	public static PacketByteBuf add(StyledBossBar<?> bar) {
		return new StyledBossBarS2CPacket(bar.getUuid(), new AddAction(bar)).toPacket();
	}

	public static PacketByteBuf remove(UUID uuid) {
		return new StyledBossBarS2CPacket(uuid, REMOVE_ACTION).toPacket();
	}

	public static PacketByteBuf updateProgress(StyledBossBar<?> bar) {
		return new StyledBossBarS2CPacket(bar.getUuid(), new UpdateProgressAction(bar.getPercent())).toPacket();
	}

	public static PacketByteBuf updateName(StyledBossBar<?> bar) {
		return new StyledBossBarS2CPacket(bar.getUuid(), new UpdateNameAction(bar.getName())).toPacket();
	}

	public static PacketByteBuf updateStyle(StyledBossBar<?> bar) {
		return new StyledBossBarS2CPacket(bar.getUuid(), new UpdateStyleAction(bar.getBossBarStyle().id())).toPacket();
	}

	public static PacketByteBuf updateProperties(StyledBossBar<?> bar) {
		return new StyledBossBarS2CPacket(bar.getUuid(), new UpdatePropertiesAction(bar.shouldDarkenSky(), bar.hasDragonMusic(), bar.shouldThickenFog())).toPacket();
	}

    public PacketByteBuf toPacket() {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeUuid(this.uuid);
		buf.writeEnumConstant(this.action.getType());
		this.action.toPacket(buf);
		return buf;
	}

	public static void handle(MinecraftClient client, PacketByteBuf buffer) {
		StyledBossBarS2CPacket packet = new StyledBossBarS2CPacket(buffer);
		client.execute(() -> client.inGameHud.getBossBarHud().styledBossBarAPI$onStyledBossBar(packet));
	}

	public void accept(Consumer consumer) {
		this.action.accept(this.uuid, consumer);
	}

    interface Action {
        StyledBossBarS2CPacket.Type getType();

        void accept(UUID uuid, StyledBossBarS2CPacket.Consumer consumer);

        void toPacket(PacketByteBuf buf);
    }

    static class AddAction implements Action {
		private final Text name;
		private final float percent;
		private final Identifier styleId;
		private final boolean darkenSky;
		private final boolean dragonMusic;
		private final boolean thickenFog;

		AddAction(StyledBossBar<?> bar) {
			this.name = bar.getName();
			this.percent = bar.getPercent();
			this.styleId = bar.getBossBarStyle().id();
			this.darkenSky = bar.shouldDarkenSky();
			this.dragonMusic = bar.hasDragonMusic();
			this.thickenFog = bar.shouldThickenFog();
		}

		private AddAction(PacketByteBuf buf) {
			this.name = buf.readText();
			this.percent = buf.readFloat();
			this.styleId = buf.readIdentifier();
			short i = buf.readUnsignedByte();
			this.darkenSky = (i & 1) > 0;
			this.dragonMusic = (i & 2) > 0;
			this.thickenFog = (i & 4) > 0;
		}

		@Override
		public StyledBossBarS2CPacket.Type getType() {
			return StyledBossBarS2CPacket.Type.ADD;
		}

		@Override
		public void accept(UUID uuid, Consumer consumer) {
			consumer.add(uuid, this.name, this.percent, this.styleId, this.darkenSky, this.dragonMusic, this.thickenFog);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
			buf.writeText(this.name);
			buf.writeFloat(this.percent);
			buf.writeIdentifier(this.styleId);
			buf.writeByte(makeByte(this.darkenSky, this.dragonMusic, this.thickenFog));
		}
	}

	static class UpdateProgressAction implements Action {
		private final float percent;

		UpdateProgressAction(float percent) {
			this.percent = percent;
		}

		private UpdateProgressAction(PacketByteBuf buf) {
			this.percent = buf.readFloat();
		}

		@Override
		public Type getType() {
			return Type.UPDATE_PROGRESS;
		}

		@Override
		public void accept(UUID uuid, Consumer consumer) {
			consumer.updateProgress(uuid, this.percent);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
			buf.writeFloat(this.percent);
		}
	}

	static class UpdateNameAction implements Action {
		private final Text name;

		UpdateNameAction(Text name) {
			this.name = name;
		}

		private UpdateNameAction(PacketByteBuf buf) {
			this.name = buf.readText();
		}

		@Override
		public Type getType() {
			return Type.UPDATE_NAME;
		}

		@Override
		public void accept(UUID uuid, Consumer consumer) {
			consumer.updateName(uuid, this.name);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
			buf.writeText(this.name);
		}
	}

	static class UpdateStyleAction implements Action {
		private final Identifier styleId;

		UpdateStyleAction(Identifier styleId) {
			this.styleId = styleId;
		}

		private UpdateStyleAction(PacketByteBuf buf) {
			this.styleId = buf.readIdentifier();
		}

		@Override
		public Type getType() {
			return Type.UPDATE_STYLE;
		}

		@Override
		public void accept(UUID uuid, Consumer consumer) {
			consumer.updateStyle(uuid, this.styleId);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
			buf.writeIdentifier(this.styleId);
		}
	}

	static class UpdatePropertiesAction implements Action {
		private final boolean darkenSky;
		private final boolean dragonMusic;
		private final boolean thickenFog;

		UpdatePropertiesAction(boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
			this.darkenSky = darkenSky;
			this.dragonMusic = dragonMusic;
			this.thickenFog = thickenFog;
		}

		private UpdatePropertiesAction(PacketByteBuf buf) {
			int i = buf.readUnsignedByte();
			this.darkenSky = (i & 1) > 0;
			this.dragonMusic = (i & 2) > 0;
			this.thickenFog = (i & 4) > 0;
		}

		@Override
		public Type getType() {
			return Type.UPDATE_PROPERTIES;
		}

		@Override
		public void accept(UUID uuid, Consumer consumer) {
			consumer.updateProperties(uuid, this.darkenSky, this.dragonMusic, this.thickenFog);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
			buf.writeByte(makeByte(this.darkenSky, this.dragonMusic, this.thickenFog));
		}
	}

    public interface Consumer {
		default void add(UUID uuid, Text name, float percent, Identifier styleId, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
		}

		default void remove(UUID uuid) {
		}

		default void updateProgress(UUID uuid, float percent) {
		}

		default void updateName(UUID uuid, Text name) {
		}

		default void updateStyle(UUID uuid, Identifier styleId) {
		}

		default void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
		}
	}

    enum Type {
        ADD(AddAction::new),
		UPDATE_STYLE(UpdateStyleAction::new),
		REMOVE(buf -> REMOVE_ACTION),
		UPDATE_PROGRESS(UpdateProgressAction::new),
		UPDATE_NAME(UpdateNameAction::new),
		UPDATE_PROPERTIES(UpdatePropertiesAction::new);

        final Function<PacketByteBuf, Action> parser;

		Type(Function<PacketByteBuf, Action> parser) {
			this.parser = parser;
		}
    }

    static int makeByte(boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        int i = 0;
        if (darkenSky) {
            i |= 1;
        }
        if (dragonMusic) {
            i |= 2;
        }
        if (thickenFog) {
            i |= 4;
        }
        return i;
    }
}
