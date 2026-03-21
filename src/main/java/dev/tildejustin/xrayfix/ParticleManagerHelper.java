package dev.tildejustin.xrayfix;

import com.google.common.collect.Lists;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class ParticleManagerHelper {

    public static boolean shouldCull(Particle particle) {
        Box box = particle.getBoundingBox();
        for (BlockPos pos : Lists.newArrayList(
                new BlockPos(box.minX, box.minY, box.minZ),
                new BlockPos(box.minX, box.maxY, box.minZ),
                new BlockPos(box.maxX, box.minY, box.minZ),
                new BlockPos(box.maxX, box.maxY, box.minZ),
                new BlockPos(box.minX, box.minY, box.maxZ),
                new BlockPos(box.minX, box.maxY, box.maxZ),
                new BlockPos(box.maxX, box.minY, box.maxZ),
                new BlockPos(box.maxX, box.maxY, box.maxZ)
        )) {
            if (!ChunkRendererHelper.isRenderedChunk(pos)) return true;
        }
        return false;
    }
}
