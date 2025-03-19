package com.whyvo.sbb.style;

import net.minecraft.util.Identifier;

public record BossBarStyleEntry<T extends BossBarStyle>(Identifier id, T value) {
}
