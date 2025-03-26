package com.whyvo.sbb.mixin.client;

import com.google.common.collect.Maps;
import com.whyvo.sbb.bossbar.ClientStyledBossBar;
import com.whyvo.sbb.injectface.BossBarHudInjected;
import com.whyvo.sbb.network.StyledBossBarS2CPayload;
import com.whyvo.sbb.style.BossBarStyleApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin implements BossBarHudInjected {

    @Shadow
    @Final
    Map<UUID, ClientBossBar> bossBars;

    @Unique
    private Map<UUID, ClientStyledBossBar> styledBossBars;

    @Unique
    private int cachedHeight;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectInit(MinecraftClient client, CallbackInfo ci) {
        this.styledBossBars = Maps.newHashMap();
    }

    @Override
    public void styledBossBarAPI$onStyledBossBar(StyledBossBarS2CPayload packet) {
        packet.accept(new StyledBossBarS2CPayload.Consumer() {
            @Override
            public void add(UUID uuid, Text name, float percent, Identifier styleId, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
                BossBarHudMixin.this.styledBossBars.put(uuid, new ClientStyledBossBar(uuid, name, percent, BossBarStyleApi.getEntry(styleId).value(), darkenSky, dragonMusic, thickenFog));
            }

            @Override
            public void remove(UUID uuid) {
                BossBarHudMixin.this.styledBossBars.remove(uuid);
            }

            @Override
            public void updateProgress(UUID uuid, float percent) {
                BossBarHudMixin.this.styledBossBars.get(uuid).setPercent(percent);
            }

            @Override
            public void updateName(UUID uuid, Text name) {
                BossBarHudMixin.this.styledBossBars.get(uuid).setName(name);
            }

            @Override
            public void updateStyle(UUID uuid, Identifier styleId) {
                ClientStyledBossBar clientBossBar = BossBarHudMixin.this.styledBossBars.get(uuid);
                clientBossBar.setBossBarStyle(BossBarStyleApi.getEntry(styleId).value());
            }

            @Override
            public void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
                ClientStyledBossBar clientBossBar = BossBarHudMixin.this.styledBossBars.get(uuid);
                clientBossBar.setDarkenSky(darkenSky);
                clientBossBar.setDragonMusic(dragonMusic);
                clientBossBar.setThickenFog(thickenFog);
            }
        });
    }

    @Override
    public Map<UUID, ClientStyledBossBar> styledBossBarAPI$getStyledBossBars() {
        return this.styledBossBars;
    }

    @Override
    public int styledBossBarAPI$getCachedHeight() {
        return this.cachedHeight;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void injectRender(DrawContext context, CallbackInfo ci) {
        if (this.bossBars.isEmpty() && !this.styledBossBars.isEmpty()) {
            this.cachedHeight = this.renderAllStyled(context, 12);
        }
    }

    @ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 0), name = "j")
    private int modifyVariableRender(int j, DrawContext context) {
        if(this.styledBossBars.isEmpty()) {
            this.cachedHeight = Math.min(context.getScaledWindowHeight() / 3, bossBars.size() * 19);
            return j;
        }
        int j0 = this.renderAllStyled(context, j);
        this.cachedHeight = Math.min(context.getScaledWindowHeight() / 3, j0 + bossBars.size() * 19);
        return j0;
    }

    @Unique
    private int renderAllStyled(DrawContext context, int j) {
        int limit = context.getScaledWindowHeight() / 3;
        int i = context.getScaledWindowWidth() / 2 - 91;
        for(ClientStyledBossBar bossBar : this.styledBossBars.values()) {
            j += bossBar.render(context, i, j);
            if(j >= limit) {
                break;
            }
        }
        return j;
    }


    @Inject(method = "shouldPlayDragonMusic", at = @At("RETURN"), cancellable = true)
    private void injectShouldPlayDragonMusic(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || forAllStyled(ClientStyledBossBar::hasDragonMusic));
    }

    @Inject(method = "shouldDarkenSky", at = @At("RETURN"), cancellable = true)
    private void injectShouldDarkenSky(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || forAllStyled(ClientStyledBossBar::shouldDarkenSky));
    }

    @Inject(method = "shouldThickenFog", at = @At("RETURN"), cancellable = true)
    private void injectShouldThickenFog(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || forAllStyled(ClientStyledBossBar::shouldThickenFog));
    }

    @Unique
    private boolean forAllStyled(Predicate<ClientStyledBossBar> predicate) {
        if(!this.styledBossBars.isEmpty()) {
            for (ClientStyledBossBar bossBar : this.styledBossBars.values()) {
                if (predicate.test(bossBar)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Inject(method = "clear", at = @At("RETURN"))
    private void injectClear(CallbackInfo ci) {
        this.styledBossBars.clear();
    }
}
