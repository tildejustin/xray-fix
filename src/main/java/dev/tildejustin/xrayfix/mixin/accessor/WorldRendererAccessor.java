package dev.tildejustin.xrayfix.mixin.accessor;

import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    @Accessor("chunks")
    BuiltChunkStorage getChunks();
}
