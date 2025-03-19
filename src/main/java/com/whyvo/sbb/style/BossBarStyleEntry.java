package com.whyvo.sbb.style;

import net.minecraft.util.Identifier;

public class BossBarStyleEntry<T extends BossBarStyle> {
    private final Identifier id;
    private final T value;

    BossBarStyleEntry(Identifier id, T value) {
        this.id = id;
        this.value = value;
    }

    public Identifier id() {
        return this.id;
    }

    public T value() {
        return this.value;
    }
}
