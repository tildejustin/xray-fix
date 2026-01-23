package dev.tildejustin.xrayfix.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InGameOverlayRenderer.class)
public interface InGameOverlayRendererAccessor {
    @Invoker
    static void callRenderInWallOverlay(MinecraftClient minecraftClient, Sprite sprite, MatrixStack matrixStack) {
        throw new UnsupportedOperationException();
    }
}
