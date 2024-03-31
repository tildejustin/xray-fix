package dev.tildejustin.xrayfix.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.tildejustin.xrayfix.SodiumCompat;
import dev.tildejustin.xrayfix.mixin.accessor.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Unique
    private static final boolean sodium = FabricLoader.getInstance().isModLoaded("sodium");

    @ModifyExpressionValue(method = "shouldRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Frustum;isVisible(Lnet/minecraft/util/math/Box;)Z"))
    private boolean checkIfChunkIsBuilt(boolean original, Entity entity) {
        return original && (!sodium ? !((BuiltChunkStorageAccessor) ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).getChunks()).callGetRenderedChunk(entity.getBlockPos()).getData().isEmpty() : SodiumCompat.isEntityChunkBuilt(entity));
    }
}
