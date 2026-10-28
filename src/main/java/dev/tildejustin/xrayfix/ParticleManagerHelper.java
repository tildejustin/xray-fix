package dev.tildejustin.xrayfix;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.*;

public class ParticleManagerHelper {

    public static boolean shouldCull(Particle particle) {
        Box box = particle.getBoundingBox();
        BlockPos.Mutable section = new BlockPos.Mutable();
        for (int x = MathHelper.floor(box.minX) >> 4; x <= MathHelper.floor(box.maxX) >> 4; x++) {
            for (int y = MathHelper.floor(box.minY) >> 4; y <= MathHelper.floor(box.maxY) >> 4; y++) {
                for (int z = MathHelper.floor(box.minZ) >> 4; z <= MathHelper.floor(box.maxZ) >> 4; z++) {
                    if (!ChunkRendererHelper.isRenderedChunk(section.set(x << 4, y << 4, z << 4))) return true;
                }
            }
        }
        return false;
    }
}
