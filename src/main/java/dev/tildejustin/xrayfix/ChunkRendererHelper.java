package dev.tildejustin.xrayfix;

import dev.tildejustin.xrayfix.mixin.accessor.BuiltChunkStorageAccessor;
import dev.tildejustin.xrayfix.mixin.accessor.WorldRendererAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ChunkRendererHelper {

    public static boolean isRenderedChunk(BlockPos blockPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        WorldRenderer worldRenderer = client.worldRenderer;
        boolean built;
        if (FabricLoader.getInstance().isModLoaded("sodium")) built = SodiumCompat.hasRenderData(blockPos.getX() >> 4, MathHelper.clamp(blockPos.getY() >> 4, 0, 15), blockPos.getZ() >> 4, worldRenderer);
        else {
            BuiltChunkStorage builtChunkStorage = ((WorldRendererAccessor) worldRenderer).getChunks();
            ChunkBuilder.BuiltChunk builtChunk = ((BuiltChunkStorageAccessor) builtChunkStorage).callGetRenderedChunk(blockPos);
            built = builtChunk != null && builtChunk.getData() != ChunkBuilder.ChunkData.EMPTY;
        }
        return built;
    }
}
