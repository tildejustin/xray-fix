package dev.tildejustin.xrayfix.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.tildejustin.xrayfix.CameraExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
    @ModifyExpressionValue(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z"))
    private static boolean doPerspectiveCheckNow(boolean original) {
        // !isSpectator && perspective == 0 -> isSpectator || perspective != 0
        MinecraftClient client = MinecraftClient.getInstance();
        return original || client.options.perspective != 0;
    }

    @ModifyExpressionValue(method = "getInWallBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getEyeY()D"))
    private static double useCameraYForBlockOverlay(double original) {
        MinecraftClient client = MinecraftClient.getInstance();
        return ((CameraExtension) client.gameRenderer.getCamera()).xray_fix$getFirstPersonY();
    }
}
