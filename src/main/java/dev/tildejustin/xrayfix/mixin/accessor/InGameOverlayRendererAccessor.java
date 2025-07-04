package dev.tildejustin.xrayfix.mixin.accessor;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InGameOverlayRenderer.class)
public interface InGameOverlayRendererAccessor {
    @Invoker
    static BlockState callGetInWallBlockState(PlayerEntity playerEntity) {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static void callRenderInWallOverlay(MinecraftClient minecraftClient, Sprite sprite, MatrixStack matrixStack) {
        throw new UnsupportedOperationException();
    }
}
