package dev.tildejustin.xrayfix;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;

import java.util.Objects;

public class ParticleManagerHelper {

    public static boolean shouldCull(ClientWorld world, Particle particle, Camera camera) {
        Vec3d cameraPos = camera.getPos();
        Vec3d boxCenterPos = particle.getBoundingBox().getCenter();
        BlockPos blockPos = new BlockPos(boxCenterPos);

        if (cameraPos.squaredDistanceTo(boxCenterPos) < Math.pow(4, 2)) return false;

        BlockHitResult rayTrace = world.rayTrace(new RayTraceContext(
                camera.getPos(), boxCenterPos,
                RayTraceContext.ShapeType.VISUAL,
                RayTraceContext.FluidHandling.NONE,
                camera.getFocusedEntity()
        ));

        if (rayTrace.getType() == HitResult.Type.MISS) return false;
        return !Objects.equals(rayTrace.getBlockPos(), blockPos);
    }
}
