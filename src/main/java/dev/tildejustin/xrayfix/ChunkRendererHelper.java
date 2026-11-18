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
    private static final boolean SODIUM = FabricLoader.getInstance().isModLoaded("sodium");

    public static boolean isRenderedChunk(BlockPos blockPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        WorldRenderer worldRenderer = client.worldRenderer;
        int chunkY = MathHelper.clamp(blockPos.getY() >> 4, 0, 15);
        boolean built;
        if (SODIUM) built = SodiumCompat.hasRenderData(blockPos.getX() >> 4, chunkY, blockPos.getZ() >> 4, worldRenderer);
        else {
            BlockPos origin = new BlockPos(blockPos.getX() & ~15, chunkY << 4, blockPos.getZ() & ~15);
            BuiltChunkStorage builtChunkStorage = ((WorldRendererAccessor) worldRenderer).getChunks();
            ChunkBuilder.BuiltChunk builtChunk = ((BuiltChunkStorageAccessor) builtChunkStorage).callGetRenderedChunk(origin);
            // the storage wraps around the camera, so positions outside render distance land on some other chunk
            built = builtChunk != null && builtChunk.getOrigin().equals(origin) && builtChunk.getData() != ChunkBuilder.ChunkData.EMPTY;
        }
        return built;
    }
}
