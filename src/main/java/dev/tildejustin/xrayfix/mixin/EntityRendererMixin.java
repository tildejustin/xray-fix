package dev.tildejustin.xrayfix.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.tildejustin.xrayfix.mixin.accessor.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @ModifyExpressionValue(method = "shouldRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Frustum;isVisible(Lnet/minecraft/util/math/Box;)Z"))
    private boolean checkIfChunkIsBuilt(boolean original, Entity entity) {
        // TODO: fix sodium
        return original && !((BuiltChunkStorageAccessor) ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).getChunks()).callGetRenderedChunk(entity.getBlockPos()).getData().isEmpty();
    }
}
