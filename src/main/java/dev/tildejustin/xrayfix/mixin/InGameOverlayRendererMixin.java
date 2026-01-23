package dev.tildejustin.xrayfix.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
    // mc-225337 fix but it's insane code
    @ModifyArg(method = "renderInWallOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;vertex(Lnet/minecraft/util/math/Matrix4f;FFF)Lnet/minecraft/client/render/VertexConsumer;"), index = 1)
    private static float modify(float original) {
        float hwRatio = (float) MinecraftClient.getInstance().getWindow().getFramebufferWidth() / MinecraftClient.getInstance().getWindow().getFramebufferHeight();
        return original * Math.max(hwRatio, 1) * .35f;
    }

    @ModifyArg(method = "renderInWallOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;vertex(Lnet/minecraft/util/math/Matrix4f;FFF)Lnet/minecraft/client/render/VertexConsumer;"), index = 2)
    private static float modify2(float original) {
        float hwRatio = (float) MinecraftClient.getInstance().getWindow().getFramebufferWidth() / MinecraftClient.getInstance().getWindow().getFramebufferHeight();
        return original * Math.max(hwRatio, 1) * .35f;
    }
}
