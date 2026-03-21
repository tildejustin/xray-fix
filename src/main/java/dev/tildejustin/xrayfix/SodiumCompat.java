package dev.tildejustin.xrayfix;

import dev.tildejustin.xrayfix.mixin.accessor.SodiumWorldRendererAccessor;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.*;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import me.jellysquid.mods.sodium.client.world.WorldRendererExtended;
import net.minecraft.client.render.WorldRenderer;

public class SodiumCompat {
    public static boolean hasRenderData(int chunkX, int chunkY, int chunkZ, WorldRenderer worldRenderer) {
        SodiumWorldRenderer sodiumWorldRenderer = ((WorldRendererExtended) worldRenderer).getSodiumWorldRenderer();
        ChunkRenderManager<?> chunkRenderManager = ((SodiumWorldRendererAccessor) sodiumWorldRenderer).getChunkRenderManager();
        ChunkRenderContainer<?> chunkRenderData = chunkRenderManager.getRender(chunkX, chunkY, chunkZ);
        return chunkRenderData != null && chunkRenderData.getData() != ChunkRenderData.ABSENT;
    }
}
