package dev.tildejustin.xrayfix.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.tildejustin.xrayfix.CameraExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
    @WrapWithCondition(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameOverlayRenderer;renderInWallOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private static boolean cancelOverlayRenderInConditionalPosition(MinecraftClient minecraftClient, Sprite sprite, MatrixStack matrixStack) {
        return false;
    }

    @ModifyExpressionValue(method = "getInWallBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getEyeY()D"))
    private static double useCameraYForBlockOverlay(double original) {
        MinecraftClient client = MinecraftClient.getInstance();
        return ((CameraExtension) client.gameRenderer.getCamera()).xray_fix$getFirstPersonY();
    }

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
