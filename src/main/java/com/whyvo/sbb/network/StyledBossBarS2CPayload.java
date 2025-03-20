package com.whyvo.sbb.network;

import com.whyvo.sbb.StyledBossBarAPI;
import com.whyvo.sbb.bossbar.StyledBossBar;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.function.Function;

public class StyledBossBarS2CPayload implements CustomPayload {
	public static final Id<StyledBossBarS2CPayload> ID = new CustomPayload.Id<>(StyledBossBarAPI.PACKET_STYLED_BOSS_BAR);

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
		public void toPacket(RegistryByteBuf buf) {
		}
	};

    private StyledBossBarS2CPayload(UUID uuid, Action action) {
		this.uuid = uuid;
		this.action = action;
	}

    public StyledBossBarS2CPayload(RegistryByteBuf buf) {
		this.uuid = buf.readUuid();
		Type type = buf.readEnumConstant(Type.class);
		this.action = type.parser.apply(buf);
	}

	public static StyledBossBarS2CPayload add(StyledBossBar<?> bar) {
		return new StyledBossBarS2CPayload(bar.getUuid(), new AddAction(bar));
	}

	public static StyledBossBarS2CPayload remove(UUID uuid) {
		return new StyledBossBarS2CPayload(uuid, REMOVE_ACTION);
	}

	public static StyledBossBarS2CPayload updateProgress(StyledBossBar<?> bar) {
		return new StyledBossBarS2CPayload(bar.getUuid(), new UpdateProgressAction(bar.getPercent()));
	}

	public static StyledBossBarS2CPayload updateName(StyledBossBar<?> bar) {
		return new StyledBossBarS2CPayload(bar.getUuid(), new UpdateNameAction(bar.getName()));
	}

	public static StyledBossBarS2CPayload updateStyle(StyledBossBar<?> bar) {
		return new StyledBossBarS2CPayload(bar.getUuid(), new UpdateStyleAction(bar.getBossBarStyle().id()));
	}

	public static StyledBossBarS2CPayload updateProperties(StyledBossBar<?> bar) {
		return new StyledBossBarS2CPayload(bar.getUuid(), new UpdatePropertiesAction(bar.shouldDarkenSky(), bar.hasDragonMusic(), bar.shouldThickenFog()));
	}

    public void writePacket(RegistryByteBuf buf) {
		buf.writeUuid(this.uuid);
		buf.writeEnumConstant(this.action.getType());
		this.action.toPacket(buf);
	}

	public static void handle(StyledBossBarS2CPayload payload, ClientPlayNetworking.Context context) {
		context.client().inGameHud.getBossBarHud().styledBossBarAPI$onStyledBossBar(payload);
	}

	public void accept(Consumer consumer) {
		this.action.accept(this.uuid, consumer);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	interface Action {
        StyledBossBarS2CPayload.Type getType();

        void accept(UUID uuid, StyledBossBarS2CPayload.Consumer consumer);

        void toPacket(RegistryByteBuf buf);
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

		private AddAction(RegistryByteBuf buf) {
			this.name = TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
			this.percent = buf.readFloat();
			this.styleId = buf.readIdentifier();
			short i = buf.readUnsignedByte();
			this.darkenSky = (i & 1) > 0;
			this.dragonMusic = (i & 2) > 0;
			this.thickenFog = (i & 4) > 0;
		}

		@Override
		public StyledBossBarS2CPayload.Type getType() {
			return StyledBossBarS2CPayload.Type.ADD;
		}

		@Override
		public void accept(UUID uuid, Consumer consumer) {
			consumer.add(uuid, this.name, this.percent, this.styleId, this.darkenSky, this.dragonMusic, this.thickenFog);
		}

		@Override
		public void toPacket(RegistryByteBuf buf) {
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.name);
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

		private UpdateProgressAction(RegistryByteBuf buf) {
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
		public void toPacket(RegistryByteBuf buf) {
			buf.writeFloat(this.percent);
		}
	}

	static class UpdateNameAction implements Action {
		private final Text name;

		UpdateNameAction(Text name) {
			this.name = name;
		}

		private UpdateNameAction(RegistryByteBuf buf) {
			this.name = TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
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
		public void toPacket(RegistryByteBuf buf) {
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.name);
		}
	}

	static class UpdateStyleAction implements Action {
		private final Identifier styleId;

		UpdateStyleAction(Identifier styleId) {
			this.styleId = styleId;
		}

		private UpdateStyleAction(RegistryByteBuf buf) {
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
		public void toPacket(RegistryByteBuf buf) {
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

		private UpdatePropertiesAction(RegistryByteBuf buf) {
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
		public void toPacket(RegistryByteBuf buf) {
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

        final Function<RegistryByteBuf, Action> parser;

		Type(Function<RegistryByteBuf, Action> parser) {
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
