package dev.tildejustin.xrayfix.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tildejustin.xrayfix.mixin.accessor.InGameOverlayRendererAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.AFTER))
    private void alwaysRenderBlockOverlay(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        // InGameOverlayRenderer#renderOverlays
        RenderSystem.disableAlphaTest();
        PlayerEntity playerEntity = client.player;
        if (!playerEntity.noClip) {
            BlockState blockState = InGameOverlayRendererAccessor.callGetInWallBlockState(playerEntity);
            if (blockState != null) {
                InGameOverlayRendererAccessor.callRenderInWallOverlay(client, client.getBlockRenderManager().getModels().getSprite(blockState), matrices);
            }
        }
        RenderSystem.enableAlphaTest();
    }
}
