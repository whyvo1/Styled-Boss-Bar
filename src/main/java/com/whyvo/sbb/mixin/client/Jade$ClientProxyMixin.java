package com.whyvo.sbb.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snownee.jade.util.ClientProxy;

@Mixin(ClientProxy.class)
public class Jade$ClientProxyMixin {

    @Inject(method = "getBossBarRect", at = @At("RETURN"), cancellable = true)
    private static void injectGetBossBarRect(CallbackInfoReturnable<Rect2i> cir) {
        Rect2i rect = cir.getReturnValue();
        MinecraftClient mc = MinecraftClient.getInstance();
        BossBarHud hud = mc.inGameHud.getBossBarHud();
        int size = hud.styledBossBarAPI$getStyledBossBars().size();
        if (size == 0) {
            return;
        }
        if (rect == null) {
            cir.setReturnValue(new Rect2i(mc.getWindow().getScaledWidth() / 2 - 91, 12, 182, hud.styledBossBarAPI$getCachedHeight()));
        }
        else {
            rect.setHeight(Math.min(rect.getHeight() + hud.styledBossBarAPI$getCachedHeight(), mc.getWindow().getScaledHeight() / 3));
        }
    }
}
