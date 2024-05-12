package dev.tildejustin.xrayfix;

import dev.tildejustin.xrayfix.mixin.accessor.SodiumWorldRendererAccessor;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderContainer;
import me.jellysquid.mods.sodium.client.world.WorldRendererExtended;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class SodiumCompat {
    public static boolean isEntityChunkBuilt(Entity entity) {
        ChunkRenderContainer<?> chunkRenderContainer = ((SodiumWorldRendererAccessor) ((WorldRendererExtended) MinecraftClient.getInstance().worldRenderer).getSodiumWorldRenderer()).getChunkRenderManager().getRender(entity.chunkX, entity.chunkY, entity.chunkZ);
        return chunkRenderContainer != null && !chunkRenderContainer.isEmpty();
    }
}
