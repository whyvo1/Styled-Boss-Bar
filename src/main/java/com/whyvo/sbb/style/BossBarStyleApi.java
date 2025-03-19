package com.whyvo.sbb.style;

import com.whyvo.sbb.StyledBossBarAPI;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BossBarStyleApi {
    private static final Map<Identifier, BossBarStyleEntry<?>> BOSS_BAR_STYLES = new HashMap<>();

    public static final BossBarStyleEntry<SimpleBossBarStyle> VANILLA_PROGRESS_STYLE = BossBarStyleApi.register(
            new Identifier(StyledBossBarAPI.MOD_ID, "vanilla_progress"),
            new SimpleBossBarStyle(
                    new Identifier("textures/gui/bars.png"),
                    5, 16, 0,
                    0, 19, 193));

    public static <T extends BossBarStyle> BossBarStyleEntry<T> register(Identifier id, T style) {
        BossBarStyleEntry<T> entry = new BossBarStyleEntry<>(id, style);
        BOSS_BAR_STYLES.put(id, entry);
        return entry;
    }

    @NotNull
    public static BossBarStyleEntry<?> getEntry(Identifier id) {
        return BOSS_BAR_STYLES.getOrDefault(id, VANILLA_PROGRESS_STYLE);
    }


}
