package com.whyvo.sbb.style;

import com.mojang.blaze3d.systems.RenderSystem;
import com.whyvo.sbb.bossbar.ClientStyledBossBar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record SimpleBossBarStyle(Identifier baseTexture, @Nullable Identifier overlayTexture, int baseHeight,
                                 int baseTextureHeight, int baseOffsetX, int baseOffsetY, int overlayOffsetX,
                                 int overlayOffsetY, int overlayWidth, int overlayHeight, int verticalIncrement,
                                 int progress) implements BossBarStyle {

    public SimpleBossBarStyle(Identifier baseTexture, int baseHeight,
                              int baseTextureHeight, int baseOffsetX, int baseOffsetY, int verticalIncrement,
                              int progress) {
        this(baseTexture, null, baseHeight, baseTextureHeight, baseOffsetX, baseOffsetY, 0, 0, 0, 0, verticalIncrement, progress);
    }


    @Environment(EnvType.CLIENT)
    @Override
    public int render(DrawContext context, int x, int y, ClientStyledBossBar bossBar) {
        MinecraftClient client = MinecraftClient.getInstance();
        int i = client.getWindow().getScaledWidth();
        int j = y - 9;

        int cX = x + this.baseOffsetX;
        int cY = y + this.baseOffsetY;

        client.getProfiler().push("BossBarStyleBase");
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.baseTexture);
        context.drawTexture(this.baseTexture, cX, cY, 0, 0, this.progress, this.baseHeight, 256, this.baseTextureHeight);
        int p = (int) (bossBar.getPercent() * (this.progress + 1));
        if (p > 0) {
            context.drawTexture(this.baseTexture, cX, cY, 0, this.baseHeight, p, this.baseHeight, 256, this.baseTextureHeight);
        }
        client.getProfiler().pop();

        Text name = bossBar.getName();
        int l = client.textRenderer.getWidth(name);
        int i1 = i / 2 - l / 2;
        context.drawTextWithShadow(client.textRenderer, name, i1, j, 16777215);

        if (this.overlayTexture != null) {
            client.getProfiler().push("BossBarStyleOverlay");
            RenderSystem.setShaderTexture(0, this.overlayTexture);
            context.drawTexture(this.overlayTexture, cX + this.overlayOffsetX, cY + this.overlayOffsetY, 0, 0, this.overlayWidth, this.overlayHeight, this.overlayWidth, this.overlayHeight);
            client.getProfiler().pop();
        }

        return this.verticalIncrement;
    }
}
