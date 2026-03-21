package dev.tildejustin.xrayfix.mixin;

import dev.tildejustin.xrayfix.*;
import dev.tildejustin.xrayfix.mixin.accessor.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(method = "shouldRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Frustum;isVisible(Lnet/minecraft/util/math/Box;)Z", shift = At.Shift.AFTER), cancellable = true)
    private <T extends Entity> void cullEntitiesInUnbuiltChunks(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            MinecraftClient client = MinecraftClient.getInstance();
            WorldRenderer worldRenderer = client.worldRenderer;
            if (ChunkRendererHelper.isRenderedChunk(entity.getBlockPos())) return;
            if (!EntityRendererHelper.otherwiseRendered(entity)) {
                // aw and ++ would be better performance
                ((WorldRendererAccessor) worldRenderer).setRegularEntityCount(((WorldRendererAccessor) worldRenderer).getRegularEntityCount() + 1);
            }
            cir.setReturnValue(false);
        }
    }
}
